package de.tum.sep.siglerbischoff.notenverwaltung.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLInvalidAuthorizationSpecException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Jahre;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Klasse;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Kurs;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Schueler;

class MysqlDAO implements DAO {

	private Connection dbverbindung;
	
	@Override
	public Benutzer passwortPruefen(String benutzerName, String passwort, Properties config) throws DatenbankFehler {
		
		String sql = "SELECT benutzerId, loginName, name, istAdmin FROM benutzer "
				+ "WHERE loginName = ?";
		try {
			dbverbindung = DriverManager.getConnection(
					"jdbc:mariadb://" + config.getProperty("dbhost") + "/" 
							+ config.getProperty("dbname"), 
					benutzerName, 
					passwort);
			try (PreparedStatement s = dbverbindung.prepareStatement(sql)) {
					erstelleTabellen();
					s.setString(1, benutzerName);
				try (ResultSet rs = s.executeQuery()) {
					if (rs.next()) {
						return new Benutzer(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getBoolean(4));
					} else {
						return null;
					}
				}
			}
		} catch (SQLInvalidAuthorizationSpecException e) {
			e.printStackTrace();
			return null;
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		} 
	}

	@Override
	public Jahre gebeJahre() throws DatenbankFehler {
		String sql = "SELECT schuljahr FROM klasse "
				+ "UNION SELECT schuljahr FROM kurs "
				+ "ORDER BY schuljahr DESC";
		List<Integer> list = new Vector<>();
		try(Statement s = dbverbindung.createStatement()) {
			try(ResultSet rs = s.executeQuery(sql)) {
				while(rs.next()) {
					list.add(rs.getInt(1));
				}
				return new Jahre(list);
			}
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}
	
	@Override
	public ListModel<Benutzer> gebeBenutzer() throws DatenbankFehler {
		String sql = "SELECT benutzerID, loginName, name, istAdmin FROM benutzer";
		try(Statement s = dbverbindung.createStatement()) {
			DefaultListModel<Benutzer> list = new DefaultListModel<>();
			try(ResultSet rs = s.executeQuery(sql)) {
				while(rs.next()) {
					list.addElement(new Benutzer(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getBoolean(4))); 
				}
			}
			return list;
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		} 
	}
	
	@Override
	public ListModel<Schueler> gebeSchueler() throws DatenbankFehler {
		String sql = "SELECT schuelerID, name, gebDat "
				+ "FROM schueler "
				+ "ORDER BY name";
		try (Statement s = dbverbindung.createStatement()) {
			DefaultListModel<Schueler> list = new DefaultListModel<>();
			try (ResultSet rs = s.executeQuery(sql)) {
				while (rs.next()) {
					list.addElement(new Schueler(rs.getInt(1), rs.getString(2), new Date(rs.getDate(3).getTime())));
				}
			}
			return list;
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}	

	@Override
	public ListModel<Kurs> gebeKurse(Schueler schueler, int jahr) throws DatenbankFehler {
		String sql = "SELECT kurs.kursID, kurs.name, kurs.fach, kurs.lehrerID "
				+ "FROM kurs, nimmtteil "
				+ "WHERE kurs.kursID = nimmtteil.kursID AND "
					+ "nimmtteil.schuelerID = ? AND "
					+ "kurs.schuljahr = ?"
				+ "ORDER BY kurs.name";
		try (PreparedStatement s = dbverbindung.prepareStatement(sql)) {
			s.setInt(1, schueler.getId());
			s.setInt(2, jahr);
			DefaultListModel<Kurs> list = new DefaultListModel<>();
			try (ResultSet rs = s.executeQuery()) {
				while (rs.next()) {
					list.addElement(new Kurs(rs.getInt(1), rs.getString(2), 
							rs.getString(3), jahr, rs.getInt(4)));
				}
				return list;
			}
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}

	@Override
	public ListModel<Kurs> gebeKurse(Benutzer benutzer, int jahr) throws DatenbankFehler {
		String sql = "SELECT kursID, name, fach "
				+ "FROM kurs "
				+ "WHERE lehrerID = ? AND "
					+ "schuljahr = ? "
				+ "ORDER BY name";
		try (PreparedStatement s = dbverbindung.prepareStatement(sql)) {
			s.setInt(1, benutzer.getId());
			s.setInt(2, jahr);
			DefaultListModel<Kurs> list = new DefaultListModel<>();
			try (ResultSet rs = s.executeQuery()) {
				while (rs.next()) {
					list.addElement(new Kurs(rs.getInt(1), rs.getString(2), 
							rs.getString(3), jahr, benutzer.getId()));
				}
				return list;
			}
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}

	@Override
	public ListModel<Klasse> gebeGeleiteteKlassen(Benutzer benutzer, int jahr) throws DatenbankFehler {
		String sql = "SELECT klasseID, name "
				+ "FROM klasse "
				+ "WHERE klassenlehrerID = ? AND "
					+ "schuljahr = ? "
				+ "ORDER BY name";
		try (PreparedStatement s = dbverbindung.prepareStatement(sql)) {
			s.setInt(1, benutzer.getId());
			s.setInt(2, jahr);
			DefaultListModel<Klasse> list = new DefaultListModel<>();
			try (ResultSet rs = s.executeQuery()) {
				while (rs.next()) {
					list.addElement(new Klasse(rs.getInt(1), rs.getString(2), jahr, benutzer.getId()));
				}
				return list;
			}
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}
	
	@Override
	public Benutzer benutzerAnlegen(String loginName, String name, String passwort, boolean istAdmin) throws DatenbankFehler {
		String sql = "INSERT INTO benutzer "
				+ "(loginName, name, istAdmin) VALUES "
				+ "('" + loginName + "', "
				+ "'" + name + "', "
				+ "" + istAdmin + ")";
		try (Statement s1 = dbverbindung.createStatement()) {
			s1.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			
			try (ResultSet rs = s1.getGeneratedKeys()) {
				rs.next();
				int id = rs.getInt(1);
				
				try (Statement s2 = dbverbindung.createStatement()) {
					String sqlCreate = "CREATE USER '" + loginName + "' "
							+ "IDENTIFIED BY '" + passwort + "'";
					s2.addBatch(sqlCreate);
					String sqlGrant = "GRANT INSERT, DELETE, SELECT, UPDATE ON Notenmanager.* "
							+ "TO '" + loginName + "'";
					s2.addBatch(sqlGrant);
					if(istAdmin) {
						String sqlGrantAdmin = "GRANT CREATE USER, GRANT OPTION ON *.* "
								+ "TO '" + loginName + "'";
						s2.addBatch(sqlGrantAdmin);
					}
					s2.executeBatch();
					
					return new Benutzer(id, loginName, name, istAdmin );
				}
			}
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}

	@Override
	public Klasse klasseEinrichten(String name, int jahr, Benutzer klassenlehrer) throws DatenbankFehler{
		String sql = "INSERT INTO klasse (name, schuljahr, klassenlehrerID) VALUES "
				+ "('" + name + "', "
				+ jahr + ", "
				+ klassenlehrer.getId() + ")";
		try (Statement s = dbverbindung.createStatement()) {
			s.execute(sql);
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}

	@Override
	public Kurs kursEinrichten(String name, String fach, int jahr, Benutzer kursleiter) throws DatenbankFehler {
		String sql = "INSERT INTO kurs (name, fach, schuljahr, lehrerID) VALUES "
				+ "('" + name + "', "
				+ "'" + fach + "', "
				+ jahr + ", "
				+ kursleiter.getId() + ")";
		try (Statement s = dbverbindung.createStatement()) {
			s.execute(sql);
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}
	
	// Benutzer ueber den loginName loeschen. Es wird der User aus der benutzer-Tabelle sowie als
	// auch der dazugehoerige Datenbank-User selbst geloescht. 
	@Override
	public void benutzerLoeschen(Benutzer benutzer) throws DatenbankFehler{
		String sql = "DELETE FROM benutzer WHERE benutzerID = " + benutzer.getId();
		try (Statement s = dbverbindung.createStatement()) {
			s.execute(sql);
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
		try (Statement s = dbverbindung.createStatement()) {
			sql = "DROP USER '" + benutzer.getLoginName() + "'@'%'";
			s.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public Schueler schuelerHinzufuegen(String name, Date gebDat) throws DatenbankFehler {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String sql = "INSERT INTO schueler (name, gebDat) VALUES "
				+ "('" + name + "', "
				+ "'" + df.format(gebDat) + "')";
		try (Statement s = dbverbindung.createStatement()) {
			s.execute(sql);
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}

	@Override
	public void schuelerAendern(Schueler schueler, String neuerName, Date neuesGebDat) throws DatenbankFehler {
		schueler.setName(neuerName);
		schueler.setGebDat(neuesGebDat);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String sql = "UPDATE schueler SET name = '" + neuerName + "', "
				+ "gebDat = '" + df.format(neuesGebDat) + "' "
				+ "WHERE schuelerID = " + schueler.getId();
		try (Statement s = dbverbindung.createStatement()) {
			s.executeUpdate(sql);
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}

	
	@Override
	public void schuelerLoeschen(Schueler schueler) throws DatenbankFehler{
		String sql = "DELETE FROM schueler WHERE schuelerID = " + schueler.getId();
		try (Statement s = dbverbindung.createStatement()) {
			s.executeUpdate(sql);
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}	
	}
	
	
	private void erstelleTabellen() throws SQLException {
		String schuelerTblle = 
				  "CREATE TABLE IF NOT EXISTS schueler (" 
				+ "schuelerID INT PRIMARY KEY AUTO_INCREMENT, "
				+ "name VARCHAR(50), " 
				+ "gebDat DATE)";

		String benutzerTblle = 
				  "CREATE TABLE IF NOT EXISTS benutzer (" 
				+ "benutzerID INT PRIMARY KEY AUTO_INCREMENT, " 
				+ "loginName VARCHAR(50) UNIQUE, "
				+ "name VARCHAR(50), "
				+ "istAdmin BOOLEAN DEFAULT FALSE)";

		String kursTblle = 
				  "CREATE TABLE IF NOT EXISTS kurs (" 
				+ "kursID INT PRIMARY KEY AUTO_INCREMENT, "
				+ "name VARCHAR(50), " 
				+ "fach VARCHAR(30), "
				+ "schuljahr YEAR, "
				+ "lehrerID INT, "
				+ "FOREIGN KEY (lehrerID) REFERENCES benutzer (benutzerID) ON DELETE CASCADE)";
		
		String klasseTblle = 
				  "CREATE TABLE IF NOT EXISTS klasse ("
				+ "klasseID INT PRIMARY KEY AUTO_INCREMENT, "
				+ "name VARCHAR(50), "
				+ "schuljahr YEAR, "
				+ "klassenlehrerID INT, "
				+ "FOREIGN KEY (klassenlehrerID) REFERENCES benutzer (benutzerID) ON DELETE CASCADE)";

		String nimmtTeilTblle = 
				  "CREATE TABLE IF NOT EXISTS nimmtTeil (" 
				+ "kursID INT, " 
				+ "schuelerID INT, "
				+ "PRIMARY KEY (kursID, schuelerID),"
				+ "FOREIGN KEY (kursID) REFERENCES kurs (kursID) ON DELETE CASCADE,"
				+ "FOREIGN KEY (schuelerID) REFERENCES schueler (schuelerID) ON DELETE CASCADE)";
		
		String istInKlasseTblle = 
				  "CREATE TABLE IF NOT EXISTS istInKlasse ("
				+ "klasseID INT, "
				+ "schuelerID INT, "
				+ "PRIMARY KEY (klasseID, schuelerID), "
				+ "FOREIGN KEY (klasseID) REFERENCES klasse (klasseID) ON DELETE CASCADE,"
				+ "FOREIGN KEY (schuelerID) REFERENCES schueler (schuelerID) ON DELETE CASCADE)";

		String noteTblle = 
				  "CREATE TABLE IF NOT EXISTS note (" 
				+ "noteID INT PRIMARY KEY AUTO_INCREMENT, "
				+ "wert DECIMAL(3,2) UNSIGNED ZEROFILL NOT NULL, "
				+ "datum DATE, "
				+ "art VARCHAR(50), "
				+ "gewichtung DECIMAL(4,2) UNSIGNED ZEROFILL DEFAULT 1, "
				+ "tendenz ENUM('+','-'), "
				+ "schuelerID INT, "
				+ "kursID INT,"
				+ "FOREIGN KEY (schuelerID) REFERENCES schueler (schuelerID) ON DELETE CASCADE,"
				+ "FOREIGN KEY (kursID) REFERENCES kurs (kursID) ON DELETE CASCADE)";

		try (Statement stmt = dbverbindung.createStatement()) {
			stmt.addBatch(schuelerTblle);
			stmt.addBatch(benutzerTblle);
			stmt.addBatch(kursTblle);
			stmt.addBatch(klasseTblle);
			stmt.addBatch(nimmtTeilTblle);
			stmt.addBatch(istInKlasseTblle);
			stmt.addBatch(noteTblle);
			stmt.executeBatch();
		}
	}
}
