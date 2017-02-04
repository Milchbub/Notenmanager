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

	private Connection db;
	private Properties config;
	
	@Override
	Benutzer passwortPruefen(String loginName, char[] pass, Properties config) throws DatenbankFehler {
		this.config = config;
		
		String sql = "SELECT loginName, benutzer, istAdmin FROM Benutzer "
				+ "WHERE loginName = ?";
		
		try {
			db = DriverManager.getConnection(
					"jdbc:mariadb://" + config.getProperty("dbhost") + "/" 
							+ config.getProperty("dbname"), 
					loginName, 
					new String(pass));
			try (PreparedStatement s = db.prepareStatement(sql)) {
				s.setString(1, loginName);
				try (ResultSet rs = s.executeQuery()) {
					if (rs.next()) {
						return new Benutzer(rs.getString(1), rs.getString(2), rs.getBoolean(3));
					} else {
						return null;
					}
				}
			}
		} catch (SQLInvalidAuthorizationSpecException e) {
			//TODO
			e.printStackTrace();
			return null;
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}
	
	@Override
	Jahre gebeJahre() throws DatenbankFehler {
		String sql = "SELECT klasse_jahr AS jahr FROM Klasse "
				+ "UNION SELECT kurs_jahr AS jahr FROM Kurs "
				+ "ORDER BY jahr DESC";
		
		Listenpacker<Integer> w = new Listenpacker<Integer>() {
			@Override
			protected Integer gib(ResultSet rs) throws SQLException {
				return rs.getInt(1);
			}
		};
		return new Jahre(w.gebeListe(sql));
	}
	
	@Override
	List<Benutzer> gebeAlleBenutzer() throws DatenbankFehler {
		String sql = "SELECT loginName, benutzer, istAdmin FROM Benutzer";

		return new Listenpacker<Benutzer>() {
			@Override
			protected Benutzer gib(ResultSet rs) throws SQLException {
				return new Benutzer(rs.getString(1), rs.getString(2), rs.getBoolean(3));
			}
		}.gebeListe(sql);
	}
	
	@Override
	List<Schueler> gebeAlleSchueler() throws DatenbankFehler {
		String sql = "SELECT schuelerID, schueler, gebDat FROM Schueler";
		
		return new Listenpacker<Schueler>() {
			@Override
			protected Schueler gib(ResultSet rs) throws SQLException {
				return new Schueler(rs.getInt(1), rs.getString(2), rs.getDate(3));
			}
		}.gebeListe(sql);
	}
	
	@Override
	List<Klasse> gebeKlassen(final int jahr) throws DatenbankFehler {
		String sql = "SELECT klasse, loginName, benutzer, istAdmin "
				+ "FROM Klasse JOIN Benutzer ON (klassenleiter = loginName) "
				+ "WHERE klasse_jahr = ? "
				+ "ORDER BY klasse";
		
		return new Listenpacker<Klasse>() {
			@Override
			protected void variablenSetzen(PreparedStatement s) throws SQLException {
				s.setInt(1, jahr);
			}
			@Override
			protected Klasse gib(ResultSet rs) throws SQLException {
				Benutzer b = new Benutzer(rs.getString(2), rs.getString(3), rs.getBoolean(4));
				return new Klasse(rs.getString(1), jahr, b);
			}
		}.gebeListe(sql);
	}
	
	@Override
	List<Schueler> gebeSchueler(final Klasse klasse) throws DatenbankFehler {
		String sql = "SELECT schuelerID, schueler, gebDat "
				+ "FROM Schueler "
					+ "JOIN Besucht USING (schuelerID) "
					+ "JOIN Klasse USING (klasse, klasse_jahr) "
				+ "WHERE klasse = ? AND "
					+ "klasse_jahr = ? "
				+ "ORDER BY schueler";
		
		return new Listenpacker<Schueler>() {
			@Override
			protected void variablenSetzen(PreparedStatement s) throws SQLException {
				s.setString(1, klasse.gebeName());
				s.setInt(2, klasse.gebeJahr());
			}
			
			@Override
			protected Schueler gib(ResultSet rs) throws SQLException {
				return new Schueler(rs.getInt(1), rs.getString(2), rs.getDate(3));
			}
		}.gebeListe(sql);
	}
	
	@Override
	List<Kurs> gebeKurse(final int jahr) throws DatenbankFehler {
		String sql = "SELECT kurs, fach, loginName, benutzer, istAdmin "
				+ "FROM Kurs "
					+ "JOIN Benutzer ON (kursleiter = loginName) "
				+ "WHERE kurs_jahr = ? "
				+ "ORDER BY kurs";
		
		return new Listenpacker<Kurs>() {
			@Override
			protected void variablenSetzen(PreparedStatement s) throws SQLException {
				s.setInt(1, jahr);
			}
			@Override
			protected Kurs gib(ResultSet rs) throws SQLException {
				Benutzer b = new Benutzer(rs.getString(3), rs.getString(4), rs.getBoolean(5));
				return new Kurs(rs.getString(1), jahr, rs.getString(2), b);
			}
		}.gebeListe(sql);
	}
	
	@Override
	List<Schueler> gebeSchueler(final Kurs kurs) throws DatenbankFehler {
		String sql = "SELECT schuelerID, schueler, gebDat "
				+ "FROM Schueler "
					+ "JOIN Belegt USING (schuelerID) "
					+ "JOIN Kurs USING (kurs, kurs_jahr) "
				+ "WHERE kurs = ? AND kurs_jahr = ? "
				+ "ORDER BY schueler";
		
		return new Listenpacker<Schueler>() {
			@Override
			protected void variablenSetzen(PreparedStatement s) throws SQLException {
				s.setString(1, kurs.gebeName());
				s.setInt(2, kurs.gebeJahr());
			}
			@Override
			protected Schueler gib(ResultSet rs) throws SQLException {
				return new Schueler(rs.getInt(1), rs.getString(2), rs.getDate(3));
			}
		}.gebeListe(sql);
	}
	
	@Override
	List<Note> gebeNoten(final Kurs kurs, final Schueler schueler) throws DatenbankFehler {
		String sql = "SELECT noteID, wert, datum, gewichtung, art, kommentar "
				+ "FROM Note "
					+ "JOIN Kurs USING (kurs, kurs_jahr) "
					+ "JOIN Schueler USING (schuelerID) "
				+ "WHERE schuelerID = ? AND kurs = ? AND kurs_jahr = ? "
				+ "ORDER BY datum DESC";
		return new Listenpacker<Note>() {
			@Override
			protected void variablenSetzen(PreparedStatement s) throws SQLException {
				s.setInt(1, schueler.gebeId());
				s.setString(2, kurs.gebeName());
				s.setInt(3, kurs.gebeJahr());
			}
			@Override
			protected Note gib(ResultSet rs) throws SQLException {
				return new Note(rs.getInt(1), rs.getInt(2), rs.getDate(3), 
						rs.getDouble(4), rs.getString(4), rs.getString(5), kurs, schueler);
			}
		}.gebeListe(sql);
	}
	
	@Override
	List<Kurs> gebeKurse(Schueler schueler, int jahr) throws DatenbankFehler {
		String sql = "SELECT kurs, fach, loginName, benutzer, istAdmin "
				+ "FROM Kurs "
					+ "JOIN Benutzer ON (kursleiter = loginName) "
					+ "JOIN Belegt USING (kurs, kurs_jahr) "
				+ "WHERE schuelerID = ? AND kurs_jahr = ? "
				+ "ORDER BY kurs";
		return new Listenpacker<Kurs>() {
			@Override
			protected void variablenSetzen(PreparedStatement s) throws SQLException {
				s.setInt(1, schueler.gebeId());
				s.setInt(2, jahr);
			}
			@Override
			protected Kurs gib(ResultSet rs) throws SQLException {
				Benutzer b = new Benutzer(rs.getString(3), rs.getString(4), rs.getBoolean(5));
				return new Kurs(rs.getString(1), jahr, rs.getString(2), b);
			}
		}.gebeListe(sql);
	}
	
	@Override
	List<Kurs> gebeKurse(Benutzer benutzer, int jahr) throws DatenbankFehler {
		String sql = "SELECT kurs, fach "
				+ "FROM Kurs "
				+ "WHERE kursleiter = ? AND kurs_jahr = ? "
				+ "ORDER BY kurs";
		return new Listenpacker<Kurs>() {
			@Override
			protected void variablenSetzen(PreparedStatement s) throws SQLException {
				s.setString(1, benutzer.gebeLoginName());
				s.setInt(2, jahr);
			}
			@Override
			protected Kurs gib(ResultSet rs) throws SQLException {
				return new Kurs(rs.getString(1), jahr, rs.getString(2), benutzer);
			}
		}.gebeListe(sql);
	}
	
	@Override
	List<Klasse> gebeGeleiteteKlassen(Benutzer benutzer, int jahr) throws DatenbankFehler {
		String sql = "SELECT klasse "
				+ "FROM Klasse "
				+ "WHERE klassenleiter = ? AND klasse_jahr = ? "
				+ "ORDER BY klasse";
		return new Listenpacker<Klasse>() {
			@Override
			protected void variablenSetzen(PreparedStatement s) throws SQLException {
				s.setString(1, benutzer.gebeLoginName());
				s.setInt(2, jahr);
			}
			@Override
			protected Klasse gib(ResultSet rs) throws SQLException {
				return new Klasse(rs.getString(1), jahr, benutzer);
			}
		}.gebeListe(sql);
	}
	
	@Override
	Benutzer benutzerAnlegen(String loginName, String name, char[] passwort, boolean istAdmin) throws DatenbankFehler {
		String sql1 = "INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE "
				+ "(?, ?, ?)";
		String sql2 = "CREATE USER ?";
		String sqlAdmin = "GRANT admin TO ?@'%'";
		
		String[] views = new String[] {
				"CREATE VIEW klassen_" + loginName + " AS "
						+ "SELECT * FROM Klasse WHERE klassenleiter = ?",
				"CREATE VIEW kurse_" + loginName + " AS "
						+ "SELECT * FROM Kurs WHERE kursleiter = ?",
				"CREATE VIEW noten_" + loginName + " AS "
						+ "SELECT * FROM Note WHERE benutzer = ?"
		};
		String[] grants = new String[] {
				"GRANT SELECT ON " + config.getProperty("dbname") + ".klassen_" + loginName + " TO ?@'%'",
				"GRANT SELECT ON " + config.getProperty("dbname") + ".kurse_" + loginName + " TO ?@'%'",
				"GRANT INSERT, DELETE, SELECT, UPDATE ON " + config.getProperty("dbname") + ".noten_" + loginName + " "
						+ "TO ?@'%' WITH CHECK OPTION"
		};
		
		try (PreparedStatement s1 = db.prepareStatement(sql1)) {
			s1.setString(1, loginName);
			s1.setString(2, name);
			s1.setBoolean(3, istAdmin);
			s1.executeUpdate();
			
			try (PreparedStatement s2 = db.prepareStatement(sql2)) {
				s2.setString(1, loginName);
				s2.execute();
				if (istAdmin) {
					try (PreparedStatement s3 = db.prepareStatement(sqlAdmin)) {
						s3.setString(1, loginName);
						s3.execute();
					}
				}
				for(int i = 0; i < 3; i++) {
					try (PreparedStatement s = db.prepareStatement(views[i])) {
						s.setString(1, loginName);
						s.execute();
					}
					try (PreparedStatement s = db.prepareStatement(grants[i])) {
						s.setString(1, loginName);
						s.execute();
					}
				}
				return new Benutzer(loginName, name, istAdmin);
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
	void benutzerAendern(Benutzer benutzer, String neuerName, boolean neuIstAdmin) throws DatenbankFehler {
		String sql1 = "UPDATE Benutzer SET benutzer = ?, istAdmin = ? "
				+ "WHERE loginName = ?";
		String sql2;
		if(neuIstAdmin) {
			sql2 = "GRANT CREATE USER, GRANT OPTION ON *.* TO ?@'%'";
		} else {
			sql2 = "REVOKE CREATE USER, GRANT OPTION ON *.* FROM ?@'%'";
		}
		try (PreparedStatement s1 = db.prepareStatement(sql1)) {
			s1.setString(1, neuerName);
			s1.setBoolean(2, neuIstAdmin);
			s1.setString(3, benutzer.gebeLoginName());
			s1.executeUpdate();
			
			try (PreparedStatement s2 = db.prepareStatement(sql2)) {
				s2.setString(1, benutzer.gebeLoginName());
				s2.execute();
			}
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}
	
	@Override
	void benutzerLoeschen(Benutzer benutzer) throws DatenbankFehler {
		String sql = "DELETE FROM Benutzer WHERE loginName = ?";
		String sql2 = "DROP USER ?";
		try (PreparedStatement s = db.prepareStatement(sql)) {
			s.setString(1, benutzer.gebeLoginName());
			s.execute();
			try (PreparedStatement s2 = db.prepareStatement(sql2)) {
				s2.setString(1, benutzer.gebeLoginName());
				s2.execute();
			}
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}
	
	@Override
	Schueler schuelerHinzufuegen(String name, Date gebDat) throws DatenbankFehler {
		String sql = "INSERT INTO Schueler (schueler, gebDat) VALUE "
				+ "(?, ?)";
		try (PreparedStatement s = db.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			s.setString(1, name);
			s.setString(2, df.format(gebDat));
			s.executeUpdate();
			try (ResultSet rs = s.getGeneratedKeys()) {
				rs.next();
				return new Schueler(rs.getInt(1), name, gebDat);
			}
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}
	
	@Override
	void schuelerAendern(Schueler schueler, String neuerName, Date neuesGebDat) throws DatenbankFehler {
		String sql = "UPDATE Schueler SET schueler = ?, gebDat = ? "
				+ "WHERE schuelerID = ?";
		try (PreparedStatement s = db.prepareStatement(sql)) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			s.setString(1, neuerName);
			s.setString(2, df.format(neuesGebDat));
			s.setInt(3, schueler.gebeId());
			s.executeUpdate();
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}
	
	@Override
	void schuelerLoeschen(Schueler schueler) throws DatenbankFehler {
		String sql = "DELETE FROM Schueler WHERE schuelerID = ?";
		try (PreparedStatement s = db.prepareStatement(sql)) {
			s.setInt(1, schueler.gebeId());
			s.executeUpdate();
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}
	
	@Override
	Klasse klasseEinrichten(String name, int jahr, Benutzer klassenlehrer) throws DatenbankFehler {
		String sql = "INSERT INTO Klasse (klasse, klasse_jahr, klassenleiter) VALUE "
				+ "(?, ?, ?)";
		try (PreparedStatement s = db.prepareStatement(sql)) {
			s.setString(1, name);
			s.setInt(2, jahr);
			s.setString(3, klassenlehrer.gebeLoginName());
			s.executeUpdate();
			return new Klasse(name, jahr, klassenlehrer);
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}
	
	@Override
	void klasseAendern(Klasse klasse, Benutzer neuerKlassenlehrer) throws DatenbankFehler {
		String sql = "UPDATE Klasse SET klassenleiter = ? "
				+ "WHERE klasse = ? AND klasse_jahr = ?";
		try (PreparedStatement s = db.prepareStatement(sql)) {
			s.setString(1, neuerKlassenlehrer.gebeLoginName());
			s.setString(2, klasse.gebeName());
			s.setInt(3, klasse.gebeJahr());
			s.executeUpdate();
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		} 
	}
	
	@Override
	void klasseLoeschen(Klasse klasse) throws DatenbankFehler {
		String sql = "DELETE FROM Klasse WHERE klasse = ? AND klasse_jahr = ?";
		try (PreparedStatement s = db.prepareStatement(sql)) {
			s.setString(1, klasse.gebeName());
			s.setInt(2, klasse.gebeJahr());
			s.executeUpdate();
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}
	
	@Override
	void zuKlasseHinzufuegen(Klasse klasse, Schueler schueler) throws DatenbankFehler {
		String sql = "INSERT INTO Besucht (schuelerID, klasse, klasse_jahr) VALUE "
				+ "(?, ?, ?)";
		try (PreparedStatement s = db.prepareStatement(sql)) {
			s.setInt(1, schueler.gebeId());
			s.setString(2, klasse.gebeName());
			s.setInt(3, klasse.gebeJahr());
			s.executeUpdate();
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}
	
	@Override
	void ausKlasseLoeschen(Klasse klasse, Schueler schueler) throws DatenbankFehler {
		String sql = "DELETE FROM Besucht "
				+ "WHERE schuelerID = ? AND klasse = ? AND klasse_jahr = ?";
		try (PreparedStatement s = db.prepareStatement(sql)) {
			s.setInt(1, schueler.gebeId());
			s.setString(2, klasse.gebeName());
			s.setInt(3, klasse.gebeJahr());
			s.executeUpdate();
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}
	
	@Override
	Kurs kursEinrichten(String name, int jahr, String fach, Benutzer kursleiter) throws DatenbankFehler {
		String sql = "INSERT INTO Kurs (kurs, kurs_jahr, fach, kursleiter) VALUE "
				+ "(?, ?, ?, ?)";
		try (PreparedStatement s = db.prepareStatement(sql)) {
			s.setString(1, name);
			s.setInt(2, jahr);
			s.setString(3, fach);
			s.setString(4, kursleiter.gebeLoginName());
			s.executeUpdate();
			return new Kurs(name, jahr, fach, kursleiter);
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}
	
	@Override
	void kursAendern(Kurs kurs, String neuesFach, Benutzer neuerKursleiter) throws DatenbankFehler {
		String sql = "UPDATE Kurs SET fach = ?, kursleiter = ? "
				+ "WHERE kurs = ? AND kurs_jahr = ?";
		try (PreparedStatement s = db.prepareStatement(sql)) {
			s.setString(1, neuesFach);
			s.setString(2, neuerKursleiter.gebeLoginName());
			s.setString(3, kurs.gebeName());
			s.setInt(4, kurs.gebeJahr());
			s.executeUpdate();
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		} 
	}
	
	@Override
	void kursLoeschen(Kurs kurs) throws DatenbankFehler {
		String sql = "DELETE FROM Kurs WHERE kurs = ? AND kurs_jahr = ?";
		try (PreparedStatement s = db.prepareStatement(sql)) {
			s.setString(1, kurs.gebeName());
			s.setInt(2, kurs.gebeJahr());
			s.executeUpdate();
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}
	
	@Override
	void zuKursHinzufuegen(Kurs kurs, Schueler schueler) throws DatenbankFehler {
		String sql = "INSERT INTO Belegt (schuelerID, kurs, kurs_jahr) VALUE "
				+ "(?, ?, ?)";
		try (PreparedStatement s = db.prepareStatement(sql)) {
			s.setInt(1, schueler.gebeId());
			s.setString(2, kurs.gebeName());
			s.setInt(3, kurs.gebeJahr());
			s.executeUpdate();
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}
	
	@Override
	void ausKursLoeschen(Kurs kurs, Schueler schueler) throws DatenbankFehler {
		String sql = "DELETE FROM Besucht "
				+ "WHERE schuelerID = ? AND kurs = ? AND kurs_jahr = ?";
		try (PreparedStatement s = db.prepareStatement(sql)) {
			s.setInt(1, schueler.gebeId());
			s.setString(2, kurs.gebeName());
			s.setInt(3, kurs.gebeJahr());
			s.executeUpdate();
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}
	
	@Override
	Note noteHinzufuegen(int wert, Date datum, double gewichtung, String art, String kommentar, Kurs kurs,
			Schueler schueler, Benutzer benutzer) throws DatenbankFehler {
		String sql = "INSERT INTO Note (wert, datum, gewichtung, art, "
					+ "kommentar, kurs, kurs_jahr, schuelerID, benutzer) VALUE "
				+ "(?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement s = db.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			s.setInt(1, wert);
			s.setString(2, df.format(datum));
			s.setDouble(3, gewichtung);
			s.setString(4, art);
			s.setString(5, kommentar);
			s.setString(6, kurs.gebeName());
			s.setInt(7, kurs.gebeJahr());
			s.setInt(8, schueler.gebeId());
			s.setString(9, benutzer.gebeLoginName());
			s.executeUpdate();
			try (ResultSet rs = s.getGeneratedKeys()) {
				rs.next();
				int id = rs.getInt(1);
				return new Note(id, wert, datum, gewichtung, art, kommentar, kurs, schueler);
			}
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}
	
	@Override
	void noteAendern(Note note, int neuerWert, Date neuesDatum, double neueGewichtung, String neueArt,
			String neuerKommentar) throws DatenbankFehler {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	void noteLoeschen(Note note) throws DatenbankFehler {
		String sql = "DELETE FROM Note WHERE noteID = ?";
		try (PreparedStatement s = db.prepareStatement(sql)) {
			s.setInt(1, note.gebeId());
		} catch (SQLException e) {
			throw new DatenbankFehler(e);
		}
	}
	
	private abstract class Listenpacker<T> {
		
		protected final List<T> gebeListe(String sql) throws DatenbankFehler {
			try (PreparedStatement s = db.prepareStatement(sql)) {
				variablenSetzen(s);
				try (ResultSet rs = s.executeQuery()) {
					List<T> list = new Vector<T>();
					while(rs.next()) {
						list.add(gib(rs));
					}
					return list;
				}
			} catch (SQLException e) {
				throw new DatenbankFehler(e);
			}
		}
		
		protected void variablenSetzen(PreparedStatement s) throws SQLException {}
		
		protected abstract T gib(ResultSet rs) throws SQLException;
	}
}
