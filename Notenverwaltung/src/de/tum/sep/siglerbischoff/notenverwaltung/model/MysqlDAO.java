package de.tum.sep.siglerbischoff.notenverwaltung.model;

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

class MysqlDAO extends DAO {

	private Connection dbverbindung;
	private Properties config;
	
	@Override
	Benutzer passwortPruefen(String benutzerName, String passwort, Properties config) throws DatenbankFehler {
		this.config = config;
		
		String sql = "SELECT benutzerId, loginName, name, istAdmin FROM benutzer "
				+ "WHERE loginName = ?";
		try {
			dbverbindung = DriverManager.getConnection(
					"jdbc:mariadb://" + config.getProperty("dbhost") + "/" 
							+ config.getProperty("dbname"), 
					benutzerName, 
					passwort);
			try (PreparedStatement s = dbverbindung.prepareStatement(sql)) {
					//erstelleTabellen();
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
	Jahre gebeJahre() throws DatenbankFehler {
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
	List<Benutzer> gebeBenutzer() throws DatenbankFehler {
		String sql = "SELECT benutzerID, loginName, name, istAdmin FROM benutzer";
		try(Statement s = dbverbindung.createStatement()) {
			Vector<Benutzer> list = new Vector<>();
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
	List<Schueler> gebeSchueler() throws DatenbankFehler {
		String sql = "SELECT schuelerID, name, gebDat "
				+ "FROM schueler "
				+ "ORDER BY name";
		try (Statement s = dbverbindung.createStatement()) {
			Vector<Schueler> list = new Vector<>();
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
	List<Klasse> gebeKlassen(int jahr) throws DatenbankFehler {
		String sql = "SELECT klasseID, klasse.name, benutzerID, loginName, benutzer.name, istAdmin "
				+ "FROM klasse, benutzer "
				+ "WHERE schuljahr = " + jahr + " AND "
					+ "klassenlehrerID = benutzerID "
				+ "ORDER BY klasse.name";
		try (Statement s = dbverbindung.createStatement()) {
			Vector<Klasse> list = new Vector<>();
			try (ResultSet rs = s.executeQuery(sql)) {
				while (rs.next()) {
					Benutzer lehrer = new Benutzer(rs.getInt(3), 
							rs.getString(4), rs.getString(5), rs.getBoolean(6));
					list.addElement(new Klasse(rs.getInt(1), rs.getString(2), jahr, lehrer));
				}
			}
			return list;
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}
	
	@Override
	List<Schueler> gebeSchueler(Klasse klasse) throws DatenbankFehler {
		String sql = "SELECT schueler.schuelerID, schueler.name, gebDat "
				+ "FROM klasse, schueler, istInKlasse "
				+ "WHERE klasse.klasseID = " + klasse.gebeId() + " AND "
					+ "klasse.klasseID = istInKlasse.klasseID AND "
					+ "schueler.schuelerID = istInKlasse.schuelerID " 
				+ "ORDER BY schueler.name";
		try (Statement s = dbverbindung.createStatement()) {
			Vector<Schueler> list = new Vector<>();
			try (ResultSet rs = s.executeQuery(sql)) {
				while (rs.next()) {
					list.addElement(new Schueler(rs.getInt(1), 
							rs.getString(2), new Date(rs.getDate(3).getTime())));
				}
			}
			return list;
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}
	
	@Override
	List<Kurs> gebeKurse(int jahr) throws DatenbankFehler {
		String sql = "SELECT kursID, kurs.name, fach, benutzerID, loginName, benutzer.name, istAdmin "
				+ "FROM kurs, benutzer "
				+ "WHERE schuljahr = " + jahr + " AND "
					+ "lehrerID = benutzerID "
				+ "ORDER BY kurs.name";
		try (Statement s = dbverbindung.createStatement()) {
			Vector<Kurs> list = new Vector<>();
			try (ResultSet rs = s.executeQuery(sql)) {
				while (rs.next()) {
					Benutzer lehrer = new Benutzer(rs.getInt(4), 
							rs.getString(5), rs.getString(6), rs.getBoolean(7));
					list.addElement(new Kurs(rs.getInt(1), rs.getString(2), rs.getString(3), jahr, lehrer));
				}
			}
			return list;
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}
	
	@Override
	List<Schueler> gebeSchueler(Kurs kurs) throws DatenbankFehler {
		String sql = "SELECT schueler.schuelerID, schueler.name, gebDat "
				+ "FROM kurs, schueler, nimmtTeil "
				+ "WHERE kurs.kursID = " + kurs.gebeId() + " AND "
					+ "kurs.kursID = nimmtTeil.kursID AND "
					+ "schueler.schuelerID = nimmtTeil.schuelerID " 
				+ "ORDER BY schueler.name";
		try (Statement s = dbverbindung.createStatement()) {
			Vector<Schueler> list = new Vector<>();
			try (ResultSet rs = s.executeQuery(sql)) {
				while (rs.next()) {
					list.addElement(new Schueler(rs.getInt(1), 
							rs.getString(2), new Date(rs.getDate(3).getTime())));
				}
			}
			return list;
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}
	
	@Override
	List<Kurs> gebeKurse(Schueler schueler, int jahr) throws DatenbankFehler {
		String sql = "SELECT kurs.kursID, kurs.name, kurs.fach, "
						+ "benutzer.benutzerID, benutzer.loginName, benutzer.name, benutzer.istAdmin "
				+ "FROM kurs, nimmTteil "
				+ "WHERE kurs.kursID = nimmtTeil.kursID AND "
					+ "nimmtTeil.schuelerID = ? AND "
					+ "kurs.schuljahr = ? AND "
					+ "kurs.lehrerID = benutzer.BenutzerID"
				+ "ORDER BY kurs.name";
		try (PreparedStatement s = dbverbindung.prepareStatement(sql)) {
			s.setInt(1, schueler.gebeId());
			s.setInt(2, jahr);
			Vector<Kurs> list = new Vector<>();
			try (ResultSet rs = s.executeQuery()) {
				while (rs.next()) {
					Benutzer lehrer = new Benutzer(rs.getInt(4), rs.getString(5), rs.getString(6), rs.getBoolean(7));
					list.addElement(new Kurs(rs.getInt(1), rs.getString(2), 
							rs.getString(3), jahr, lehrer));
				}
				return list;
			}
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}

	@Override
	List<Kurs> gebeKurse(Benutzer benutzer, int jahr) throws DatenbankFehler {
		String sql = "SELECT kursID, name, fach "
				+ "FROM kurs "
				+ "WHERE lehrerID = ? AND "
					+ "schuljahr = ? "
				+ "ORDER BY name";
		try (PreparedStatement s = dbverbindung.prepareStatement(sql)) {
			s.setInt(1, benutzer.gebeId());
			s.setInt(2, jahr);
			Vector<Kurs> list = new Vector<>();
			try (ResultSet rs = s.executeQuery()) {
				while (rs.next()) {
					list.addElement(new Kurs(rs.getInt(1), rs.getString(2), 
							rs.getString(3), jahr, benutzer));
				}
				return list;
			}
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}

	@Override
	List<Klasse> gebeGeleiteteKlassen(Benutzer benutzer, int jahr) throws DatenbankFehler {
		String sql = "SELECT klasseID, name "
				+ "FROM klasse "
				+ "WHERE klassenlehrerID = ? AND "
					+ "schuljahr = ? "
				+ "ORDER BY name";
		try (PreparedStatement s = dbverbindung.prepareStatement(sql)) {
			s.setInt(1, benutzer.gebeId());
			s.setInt(2, jahr);
			Vector<Klasse> list = new Vector<>();
			try (ResultSet rs = s.executeQuery()) {
				while (rs.next()) {
					list.addElement(new Klasse(rs.getInt(1), rs.getString(2), jahr, benutzer));
				}
				return list;
			}
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}
	
	@Override
	Benutzer benutzerAnlegen(String loginName, String name, char[] passwort, boolean istAdmin) throws DatenbankFehler {
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
					String grantSql = "GRANT INSERT, DELETE, SELECT, UPDATE "
							+ "ON " + config.getProperty("dbname") + ".* "
							+ "TO '" + loginName + "' "
							+ "IDENTIFIED BY '" + new String(passwort) + "'";
					s2.addBatch(grantSql);
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
		} finally {
			for(int i = 0; i < passwort.length; i++) {
				passwort[i] = 0;
			}
		}
	}
	
	@Override
	void benutzerLoginAendern(Benutzer benutzer, String neuerLoginName) throws DatenbankFehler {
		String sql = "UPDATE benutzer SET loginName = '" + neuerLoginName + "' "
				+ "WHERE benutzerID = " + benutzer.gebeId();
		try (Statement s1 = dbverbindung.createStatement()) {
			s1.executeUpdate(sql);
			
			try (Statement s2 = dbverbindung.createStatement()) {
				String renameSql = "RENAME USER '" + benutzer.gebeLoginName() + "' "
						+ "TO '" + neuerLoginName + "'";
				s2.execute(renameSql);
			}
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}

	@Override
	void benutzerNameAendern(int id, String neuerName) throws DatenbankFehler {
		String sql = "UPDATE benutzer SET name = '" + neuerName + "' "
				+ "WHERE benutzerID = " + id;
		try (Statement s = dbverbindung.createStatement()) {
			s.executeUpdate(sql);
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}
	
	@Override
	void benutzerIstAdminAendern(Benutzer benutzer) throws DatenbankFehler {
		String sql = "UPDATE benutzer SET istAdmin = " + !benutzer.istAdmin() + " "
				+ "WHERE benutzerID = " + benutzer.gebeId();
		
		String grantSql;
		if(benutzer.istAdmin()) {
			grantSql = "REVOKE CREATE USER, GRANT OPTION ON *.* "
					+ "FROM '" + benutzer.gebeLoginName() + "'";
		} else {
			grantSql = "GRANT CREATE USER, GRANT OPTION ON *.* "
					+ "TO '" + benutzer.gebeLoginName() + "'";
		}
		try (Statement s = dbverbindung.createStatement()) {
			s.addBatch(sql);
			s.addBatch(grantSql);
			s.executeBatch();
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}

	// Es wird der User aus der benutzer-Tabelle sowie als
	// auch der dazugehoerige Datenbank-User selbst geloescht. 
	@Override
	void benutzerLoeschen(Benutzer benutzer) throws DatenbankFehler{
		String sql = "DELETE FROM benutzer WHERE benutzerID = " + benutzer.gebeId();
		try (Statement s1 = dbverbindung.createStatement()) {
			s1.execute(sql);
			
			try (Statement s2 = dbverbindung.createStatement()) {
				sql = "DROP USER '" + benutzer.gebeLoginName() + "'";
				s2.execute(sql);
			}
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}		
	}
	
	
	//Schuelerverwaltung (Schueler hinzufuegen, aendern, loeschen)
	
	@Override
	Schueler schuelerHinzufuegen(String name, Date gebDat) throws DatenbankFehler  {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String sql = "INSERT INTO schueler (name, gebDat) VALUES "
				+ "('" + name + "', "
				+ "'" + df.format(gebDat) + "')";
		try (Statement s1 = dbverbindung.createStatement()) {
			s1.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			
			try (ResultSet rs = s1.getGeneratedKeys()) {
				rs.next();
				int id = rs.getInt(1);
				
				return new Schueler(id, name, gebDat);
			}
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}

	@Override
	void schuelerAendern(int id, String neuerName, Date neuesGebDat) throws DatenbankFehler {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String sql = "UPDATE schueler SET name = '" + neuerName + "', "
				+ "gebDat = '" + df.format(neuesGebDat) + "' "
				+ "WHERE schuelerID = " + id;
		try (Statement s = dbverbindung.createStatement()) {
			s.executeUpdate(sql);
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}

	
	@Override
	void schuelerLoeschen(int id) throws DatenbankFehler{
		String sql = "DELETE FROM schueler WHERE schuelerID = " + id;
		try (Statement s = dbverbindung.createStatement()) {
			s.executeUpdate(sql);
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}	
	}
	
	//Klassenverwaltung (Klasse hinzufuegen, aendern, loeschen)
	
	@Override
	Klasse klasseEinrichten(String name, int jahr, Benutzer klassenlehrer) throws DatenbankFehler{
		String sql = "INSERT INTO klasse (name, schuljahr, klassenlehrerID) VALUES "
				+ "('" + name + "', "
				+ jahr + ", "
				+ klassenlehrer.gebeId() + ")";
		try (Statement s = dbverbindung.createStatement()) {
			s.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			
			try (ResultSet rs = s.getGeneratedKeys()) {
				rs.next();
				int id = rs.getInt(1);
				
				return new Klasse(id, name, jahr, klassenlehrer);
			}
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}	
	}
	
	@Override
	void klasseAendern(int id, String neuerName, Benutzer neuerKlassenlehrer) throws DatenbankFehler {
		String sql = "UPDATE klasse SET name = '" + neuerName + "', "
				+ "klassenlehrerID = '" + neuerKlassenlehrer.gebeId() + "' "
				+ "WHERE klasseID = " + id;
		try (Statement s = dbverbindung.createStatement()) {
			s.executeUpdate(sql);
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}
	
	//TODO Hier wird der Klassenlehrer gleich mitgelöscht, oder??
	@Override
	void klasseLoeschen(int id) throws DatenbankFehler{
		String sql = "DELETE FROM klasse WHERE klasseID = " + id;
		try (Statement s = dbverbindung.createStatement()) {
			s.executeUpdate(sql);
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}	
	}
	
	//Kursverwaltung (Kurs hinzufuegen, aendern, loeschen)
	
	@Override
	Kurs kursEinrichten(String name, String fach, int jahr, Benutzer kursleiter) throws DatenbankFehler {
		String sql = "INSERT INTO kurs (name, fach, schuljahr, lehrerID) VALUES "
				+ "('" + name + "', "
				+ "'" + fach + "', "
				+ jahr + ", "
				+ kursleiter.gebeId() + ")";
		try (Statement s = dbverbindung.createStatement()) {
			s.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			
			try (ResultSet rs = s.getGeneratedKeys()) {
				rs.next();
				int id = rs.getInt(1);
				
				return new Kurs(id, name, fach, jahr, kursleiter);
			}
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}	
	}
	
	@Override
	void kursAendern(int id, String neuerName, String neuesFach, Benutzer neuerKursleiter) throws DatenbankFehler {
		String sql = "UPDATE klasse SET name = '" + neuerName + "', "
				+ "fach = '" + neuesFach + "', "
				+ "lehrerID = '" + neuerKursleiter.gebeId() + "' "
				+ "WHERE kursID = " + id;
		try (Statement s = dbverbindung.createStatement()) {
			s.executeUpdate(sql);
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}

	//TODO Hier wird der Kursleider gleich mitgelöscht, oder??
	@Override
	void kursLoeschen(int id) throws DatenbankFehler {
		String sql = "DELETE FROM kurs WHERE kursID = " + id;
		try (Statement s = dbverbindung.createStatement()) {
			s.executeUpdate(sql);
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}	
	}
	
	void fireSQL(String sql) throws DatenbankFehler{
		try (Statement s = dbverbindung.createStatement()) {
			s.executeUpdate(sql);
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}
	
	ResultSet fireSQLResult(String sql) throws DatenbankFehler{
		try (Statement s = dbverbindung.createStatement()) {
			s.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			return s.getGeneratedKeys();
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}
	
	
	//TODO	
	@SuppressWarnings("unused")
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
