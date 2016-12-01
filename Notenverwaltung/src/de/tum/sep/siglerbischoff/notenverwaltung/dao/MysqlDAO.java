package de.tum.sep.siglerbischoff.notenverwaltung.dao;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import javax.swing.table.TableModel;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Benutzer;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Jahre;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Klasse;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Kurs;
import de.tum.sep.siglerbischoff.notenverwaltung.model.Schueler;

class MysqlDAO implements DAO {

//	private static final String dbuser = "jdbc";
	private static final String dbuser = "root";
//	private static final String dbpass = "8xpPWLYzXSZAVRjt";
	private static final String dbpass = "maulwurf.";
	private static final String dbaddress = "localhost";
//	private static final String dbaddress = "127.0.0.1:3306";
	private static final String dbname = "notenmanager";

	private Connection dbverbindung;
	
	@Override
	public Benutzer passwortPruefen(String benutzerName, String passwort) throws DatenbankFehler {
		String sql = "SELECT passwort, benutzerId, name, istAdmin FROM benutzer "
				+ "WHERE loginName = ?";
		try (PreparedStatement s = dbverbindung.prepareStatement(sql)) {
			s.setString(1, benutzerName);
			try (ResultSet rs = s.executeQuery()) {
				if (rs.next() && rs.getString(1).equals(DAO.hashPasswort(passwort))) {
					return new Benutzer(rs.getInt(2), rs.getString(3), rs.getBoolean(4));
				} else {
					return null;
				}
			}
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
		String sql = "SELECT benutzerID, name, istAdmin FROM benutzer";
		try(Statement s = dbverbindung.createStatement()) {
			List<Benutzer> list = new Vector<>();
			try(ResultSet rs = s.executeQuery(sql)) {
				while(rs.next()) {
					list.add(new Benutzer(rs.getInt(1), rs.getString(2), rs.getBoolean(3))); 
				}
			}
			return new ListModelAdaptor<>(list);
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		} 
	}
	
	@Override
	public ListModel<Schueler> gebeSchueler() throws DatenbankFehler {
		String sql = "SELECT schuelerID, name FROM schueler";
		try(Statement s = dbverbindung.createStatement()) {
			List<Schueler> list = new Vector<>();
			try(ResultSet rs = s.executeQuery(sql)) {
				while(rs.next()) {
					list.add(new Schueler(rs.getInt(1), rs.getString(2))); 
				}
			}
			return new ListModelAdaptor<>(list);
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
			List<Kurs> list = new Vector<>();
			try (ResultSet rs = s.executeQuery()) {
				while (rs.next()) {
					list.add(new Kurs(rs.getInt(1), rs.getString(2), 
							rs.getString(3), jahr, rs.getInt(4)));
				}
				return new ListModelAdaptor<>(list);
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
			List<Kurs> list = new Vector<>();
			try (ResultSet rs = s.executeQuery()) {
				while (rs.next()) {
					list.add(new Kurs(rs.getInt(1), rs.getString(2), 
							rs.getString(3), jahr, benutzer.getId()));
				}
				return new ListModelAdaptor<>(list);
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
			List<Klasse> list = new Vector<>();
			try (ResultSet rs = s.executeQuery()) {
				while (rs.next()) {
					list.add(new Klasse(rs.getInt(1), rs.getString(2), jahr, benutzer.getId()));
				}
				return new ListModelAdaptor<>(list);
			}
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}
	
	@Override
	public TableModel gebeSchuelerdaten() {
		return null;
	}
	
	@Override
	public void benutzerAnlegen(String name, String loginName, String passwort, boolean istAdmin) throws DatenbankFehler {
		passwort = DAO.hashPasswort(passwort);
		
		String sql = "INSERT INTO benutzer "
				+ "(loginName, name, passwort, istAdmin) VALUES "
				+ "('" + loginName + "', "
				+ "'" + name + "', "
				+ "'" + passwort + "', "
				+ "" + istAdmin + ")";
		try (Statement s = dbverbindung.createStatement()) {
			s.execute(sql);
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
		
		try (Statement s = dbverbindung.createStatement()) {
			sql = "CREATE USER " + loginName + " "
					+ "IDENTIFIED BY '" + passwort + "'";
			s.addBatch(sql);
			sql = "GRANT INSERT, DELETE, SELECT, UPDATE ON Notenmanager.* "
					+ "TO " + loginName;
			s.addBatch(sql);
			if(istAdmin) {
			}
			s.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void schülerHinzufügen(String name, String gebDat, String adresse) throws DatenbankFehler {
		String sql = "INSERT INTO schueler (name, gebDat, adresse) VALUES "
				+ "('" + name + "', "
				+ "'" + gebDat + "', "
				+ "'" + adresse + "')";
		try (Statement s = dbverbindung.createStatement()) {
			s.execute(sql);
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}

	@Override
	public void klasseEinrichten(String name, int jahr, Benutzer klassenlehrer) throws DatenbankFehler{
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
	public void kursEinrichten(String name, String fach, int jahr, Benutzer kursleiter) throws DatenbankFehler {
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
	public void benutzerLoeschen(String loginName) throws DatenbankFehler{
		String sql = "DELETE FROM benutzer WHERE loginName = " + "'" + loginName + "'";
		try (Statement s = dbverbindung.createStatement()) {
			s.execute(sql);
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
		try (Statement s = dbverbindung.createStatement()) {
			sql = "DROP USER '" + loginName + "'@'%'";
			s.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void schuelerLoeschen(String schuelerName) throws DatenbankFehler{
		int schuelerID;
		
		String sql = "SELECT schuelerID "
				+ "FROM schueler "
				+ "WHERE name = "+"'" + schuelerName + "'";
		try (Statement s = dbverbindung.createStatement()) {
			ResultSet rs = s.executeQuery(sql);
			rs.next();
			schuelerID=rs.getInt(1);
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}		
		String sqlDelete = "DELETE FROM schueler WHERE schuelerID =" +schuelerID;
		try (Statement s = dbverbindung.createStatement()) {
			s.execute(sqlDelete);
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}	
	}
	
	private void datenbankInitialisieren() throws SQLException {
		dbverbindung = DriverManager.getConnection(
				"jdbc:mariadb://" + dbaddress + "/" + dbname, 
				dbuser, 
				dbpass);

		erstelleTabellen();
	}

	private void erstelleTabellen() throws SQLException {
		String schuelerTblle = 
				  "CREATE TABLE IF NOT EXISTS schueler (" 
				+ "schuelerID INT PRIMARY KEY AUTO_INCREMENT, "
				+ "name VARCHAR(50), " 
				+ "gebDat DATE, "
				+ "adresse VARCHAR(50))";

		String benutzerTblle = 
				  "CREATE TABLE IF NOT EXISTS benutzer (" 
				+ "benutzerID INT PRIMARY KEY AUTO_INCREMENT, " 
				+ "loginName VARCHAR(50) UNIQUE, "
				+ "name VARCHAR(50), "
				+ "passwort VARCHAR(32), "
				+ "istAdmin BOOLEAN DEFAULT FALSE)";

		String kursTblle = 
				  "CREATE TABLE IF NOT EXISTS kurs (" 
				+ "kursID INT PRIMARY KEY AUTO_INCREMENT, "
				+ "name VARCHAR(50), " 
				+ "fach VARCHAR(30), "
				+ "schuljahr YEAR, "
				+ "lehrerID INT,"
				+ "FOREIGN KEY (lehrerID) REFERENCES benutzer (benutzerID) ON DELETE CASCADE)";
		
		String klasseTblle = 
				  "CREATE TABLE IF NOT EXISTS klasse ("
				+ "klasseID INT PRIMARY KEY AUTO_INCREMENT, "
				+ "name VARCHAR(50), "
				+ "schuljahr YEAR, "
				+ "klassenlehrerID INT,"
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
			stmt.addBatch(klasseTblle);
			stmt.addBatch(benutzerTblle);
			stmt.addBatch(kursTblle);
			stmt.addBatch(nimmtTeilTblle);
			stmt.addBatch(istInKlasseTblle);
			stmt.addBatch(noteTblle);
			stmt.executeBatch();
		}
	}
	
	public MysqlDAO() throws DatenbankFehler {
		try {
			datenbankInitialisieren();
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}
	
	private class ListModelAdaptor<T> implements ListModel<T> {

		private List<T> list;
		
		ListModelAdaptor(List<T> list) {
			this.list = list;
		}
		
		@Override
		public int getSize() {
			return list.size();
		}

		@Override
		public T getElementAt(int index) {
			return list.get(index);
		}

		@Override
		public void addListDataListener(ListDataListener l) {
			// TODO Auto-generated method stub
		}

		@Override
		public void removeListDataListener(ListDataListener l) {
			// TODO Auto-generated method stub
		}
		
	}
}
