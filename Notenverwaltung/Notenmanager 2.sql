CREATE DATABASE Notenmanager;
USE Notenmanager;
FLUSH PRIVILEGES;

CREATE TABLE IF NOT EXISTS Benutzer (
	loginName VARCHAR(32) NOT NULL KEY, 
	benutzer VARCHAR(64) NOT NULL, 
	istAdmin BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS Schueler (
	schuelerID INT NOT NULL AUTO_INCREMENT KEY, 
	schueler VARCHAR(64) NOT NULL,
	gebDat DATE NOT NULL
); 

CREATE TABLE IF NOT EXISTS Klasse (
	klasse VARCHAR(16) NOT NULL,
	klasse_jahr INT UNSIGNED NOT NULL,
	klassenleiter VARCHAR(32) NOT NULL,
	PRIMARY KEY (klasse, klasse_jahr),
	FOREIGN KEY (klassenleiter)
		REFERENCES Benutzer (loginName)
		ON DELETE RESTRICT
		ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS Kurs (
	kurs VARCHAR(16) NOT NULL,
	kurs_jahr INT UNSIGNED NOT NULL,
	fach VARCHAR(32) NOT NULL,
	kursleiter VARCHAR(32) NOT NULL,
	PRIMARY KEY (kurs, kurs_jahr),
	FOREIGN KEY (kursleiter) 
		REFERENCES Benutzer (loginName)
		ON DELETE RESTRICT
		ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS Note (
	noteID INT NOT NULL AUTO_INCREMENT KEY,
	wert INT UNSIGNED NOT NULL, 
	datum DATE NOT NULL, 
	gewichtung DECIMAL(4,2) NOT NULL,
	art VARCHAR(64) NOT NULL,
	kommentar VARCHAR(255) NOT NULL DEFAULT '',
	kurs VARCHAR(16) NOT NULL,
	kurs_jahr INT UNSIGNED NOT NULL,
	schuelerID INT NOT NULL,
	benutzer VARCHAR(32) NOT NULL,
	FOREIGN KEY (kurs, kurs_jahr)
		REFERENCES Kurs (kurs, kurs_jahr)
		ON DELETE RESTRICT
		ON UPDATE CASCADE,
	FOREIGN KEY (schuelerID)
		REFERENCES Schueler (schuelerID)
		ON DELETE CASCADE
		ON UPDATE CASCADE,
	FOREIGN KEY (benutzer)
		REFERENCES Benutzer (loginName)
		ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS Besucht (
	schuelerID INT NOT NULL,
	klasse VARCHAR(16) NOT NULL,
	klasse_jahr INT UNSIGNED NOT NULL,
	FOREIGN KEY (klasse, klasse_jahr)
		REFERENCES Klasse (klasse, klasse_jahr)
		ON DELETE RESTRICT
		ON UPDATE CASCADE,
	FOREIGN KEY (schuelerID)
		REFERENCES Schueler (schuelerID)
		ON DELETE CASCADE
		ON UPDATE CASCADE,
	PRIMARY KEY (schuelerID, klasse, klasse_jahr), 
	UNIQUE (schuelerID, klasse_jahr)
);

CREATE TABLE IF NOT EXISTS Belegt (
	schuelerID INT NOT NULL,
	kurs VARCHAR(16) NOT NULL,
	kurs_jahr INT UNSIGNED NOT NULL,
	FOREIGN KEY (kurs, kurs_jahr)
		REFERENCES Kurs (kurs, kurs_jahr)
		ON DELETE RESTRICT
		ON UPDATE CASCADE,
	FOREIGN KEY (schuelerID)
		REFERENCES Schueler (schuelerID)
		ON DELETE CASCADE
		ON UPDATE CASCADE,
	PRIMARY KEY (schuelerID, kurs, kurs_jahr)
);

DELIMITER $$
CREATE TRIGGER trigger_klasse BEFORE INSERT ON Klasse 
FOR EACH ROW BEGIN
	IF (NEW.klasse_jahr < 2010 OR NEW.klasse_jahr > YEAR(NOW()) + 5) THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'Das angegebene Jahr ist ungueltig';
	END IF;
END$$

CREATE TRIGGER trigger_kurs BEFORE INSERT ON Kurs 
FOR EACH ROW BEGIN
	IF (NEW.kurs_jahr < 2010 OR NEW.kurs_jahr > YEAR(NOW()) + 5) THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'Das angegebene Jahr ist ungueltig';
	END IF;
END$$

CREATE TRIGGER trigger_note BEFORE INSERT ON Note 
FOR EACH ROW BEGIN
	IF (NEW.wert < 1 OR NEW.wert > 6) THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'Der Notenwert muss zwischen 1 und 6 liegen';
	ELSEIF (NEW.datum < '2010-01-01' OR NEW.datum > NOW()) THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'Das angegebene Datum ist ungueltig';
	ELSEIF ((SELECT COUNT(*) FROM Belegt WHERE schuelerID = NEW.schuelerID 
				AND kurs = NEW.kurs AND kurs_jahr = NEW.kurs_jahr) < 1) THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'Ein Schueler muss einen Kurs belegen, um eine Note darin bekommen zu koennen';
	END IF;
END$$

CREATE TRIGGER trigger_schueler BEFORE INSERT ON Schueler 
FOR EACH ROW BEGIN
	IF (NEW.gebDat < '1900-01-01' OR NEW.gebDat > NOW()) THEN
		SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'Das angegebene Geburtsdatum ist ungueltig';
	END IF;
END$$

DELIMITER ;

CREATE ROLE admin;
GRANT SELECT, INSERT, UPDATE, DELETE ON Notenmanager.* TO admin;
GRANT CREATE VIEW, GRANT OPTION ON * TO admin;
GRANT CREATE USER ON *.* TO admin WITH GRANT OPTION;

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("root", "Root", true);
CREATE VIEW noten_loeschen_root AS 
	SELECT * FROM Note WHERE benutzer = 'root' WITH CHECK OPTION; 
CREATE VIEW noten_root AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "root"; 
CREATE VIEW noten_klasse_root AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "root"; 
GRANT SELECT ON Benutzer TO 'root'@'%';
GRANT SELECT ON Klasse TO 'root'@'%'; 
GRANT SELECT ON Kurs TO 'root'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_root TO 'root'@'%'; 
GRANT SELECT ON noten_root TO 'root'@'%'; 
GRANT SELECT ON noten_klasse_root TO 'root'@'%'; 
GRANT SELECT ON Schueler TO 'root'@'%'; 
GRANT SELECT ON Besucht TO 'root'@'%'; 
GRANT SELECT ON Belegt TO 'root'@'%';
GRANT admin TO 'root'@'%' WITH ADMIN OPTION; 

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("sigl", "Magdalena Sigler", true);
CREATE USER 'sigl'@'%' IDENTIFIED BY "tu2017";
CREATE VIEW noten_loeschen_sigl AS 
	SELECT * FROM Note WHERE benutzer = 'sigl' WITH CHECK OPTION; 
CREATE VIEW noten_sigl AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "sigl"; 
CREATE VIEW noten_klasse_sigl AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "sigl"; 
GRANT SELECT ON Benutzer TO 'sigl'@'%';
GRANT SELECT ON Klasse TO 'sigl'@'%'; 
GRANT SELECT ON Kurs TO 'sigl'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_sigl TO 'sigl'@'%'; 
GRANT SELECT ON noten_sigl TO 'sigl'@'%'; 
GRANT SELECT ON noten_klasse_sigl TO 'sigl'@'%'; 
GRANT SELECT ON Schueler TO 'sigl'@'%'; 
GRANT SELECT ON Besucht TO 'sigl'@'%'; 
GRANT SELECT ON Belegt TO 'sigl'@'%';
GRANT admin TO 'sigl'@'%' WITH ADMIN OPTION; 

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("bisc", "Michael Bischoff", true);
CREATE USER 'bisc'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_bisc AS 
	SELECT * FROM Note WHERE benutzer = 'bisc' WITH CHECK OPTION; 
CREATE VIEW noten_bisc AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "bisc"; 
CREATE VIEW noten_klasse_bisc AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "bisc"; 
GRANT SELECT ON Benutzer TO 'bisc'@'%';
GRANT SELECT ON Klasse TO 'bisc'@'%'; 
GRANT SELECT ON Kurs TO 'bisc'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_bisc TO 'bisc'@'%'; 
GRANT SELECT ON noten_bisc TO 'bisc'@'%'; 
GRANT SELECT ON noten_klasse_bisc TO 'bisc'@'%'; 
GRANT SELECT ON Schueler TO 'bisc'@'%'; 
GRANT SELECT ON Besucht TO 'bisc'@'%'; 
GRANT SELECT ON Belegt TO 'bisc'@'%';
GRANT admin TO 'bisc'@'%' WITH ADMIN OPTION; 

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("ilbu", "Ilona Burkert", false);
CREATE USER 'ilbu'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_ilbu AS 
	SELECT * FROM Note WHERE benutzer = 'ilbu' WITH CHECK OPTION; 
CREATE VIEW noten_ilbu AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "ilbu"; 
CREATE VIEW noten_klasse_ilbu AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "ilbu"; 
GRANT SELECT ON Benutzer TO 'ilbu'@'%';
GRANT SELECT ON Klasse TO 'ilbu'@'%'; 
GRANT SELECT ON Kurs TO 'ilbu'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_ilbu TO 'ilbu'@'%'; 
GRANT SELECT ON noten_ilbu TO 'ilbu'@'%'; 
GRANT SELECT ON noten_klasse_ilbu TO 'ilbu'@'%'; 
GRANT SELECT ON Schueler TO 'ilbu'@'%'; 
GRANT SELECT ON Besucht TO 'ilbu'@'%'; 
GRANT SELECT ON Belegt TO 'ilbu'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("lekr", "Leah Kreis", false);
CREATE USER 'lekr'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_lekr AS 
	SELECT * FROM Note WHERE benutzer = 'lekr' WITH CHECK OPTION; 
CREATE VIEW noten_lekr AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "lekr"; 
CREATE VIEW noten_klasse_lekr AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "lekr"; 
GRANT SELECT ON Benutzer TO 'lekr'@'%';
GRANT SELECT ON Klasse TO 'lekr'@'%'; 
GRANT SELECT ON Kurs TO 'lekr'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_lekr TO 'lekr'@'%'; 
GRANT SELECT ON noten_lekr TO 'lekr'@'%'; 
GRANT SELECT ON noten_klasse_lekr TO 'lekr'@'%'; 
GRANT SELECT ON Schueler TO 'lekr'@'%'; 
GRANT SELECT ON Besucht TO 'lekr'@'%'; 
GRANT SELECT ON Belegt TO 'lekr'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("wosp", "Wolfgang Speicher", false);
CREATE USER 'wosp'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_wosp AS 
	SELECT * FROM Note WHERE benutzer = 'wosp' WITH CHECK OPTION; 
CREATE VIEW noten_wosp AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "wosp"; 
CREATE VIEW noten_klasse_wosp AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "wosp"; 
GRANT SELECT ON Benutzer TO 'wosp'@'%';
GRANT SELECT ON Klasse TO 'wosp'@'%'; 
GRANT SELECT ON Kurs TO 'wosp'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_wosp TO 'wosp'@'%'; 
GRANT SELECT ON noten_wosp TO 'wosp'@'%'; 
GRANT SELECT ON noten_klasse_wosp TO 'wosp'@'%'; 
GRANT SELECT ON Schueler TO 'wosp'@'%'; 
GRANT SELECT ON Besucht TO 'wosp'@'%'; 
GRANT SELECT ON Belegt TO 'wosp'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("joba", "Johannes Baltes", false);
CREATE USER 'joba'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_joba AS 
	SELECT * FROM Note WHERE benutzer = 'joba' WITH CHECK OPTION; 
CREATE VIEW noten_joba AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "joba"; 
CREATE VIEW noten_klasse_joba AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "joba"; 
GRANT SELECT ON Benutzer TO 'joba'@'%';
GRANT SELECT ON Klasse TO 'joba'@'%'; 
GRANT SELECT ON Kurs TO 'joba'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_joba TO 'joba'@'%'; 
GRANT SELECT ON noten_joba TO 'joba'@'%'; 
GRANT SELECT ON noten_klasse_joba TO 'joba'@'%'; 
GRANT SELECT ON Schueler TO 'joba'@'%'; 
GRANT SELECT ON Besucht TO 'joba'@'%'; 
GRANT SELECT ON Belegt TO 'joba'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("codi", "Cordula Dietzel", false);
CREATE USER 'codi'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_codi AS 
	SELECT * FROM Note WHERE benutzer = 'codi' WITH CHECK OPTION; 
CREATE VIEW noten_codi AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "codi"; 
CREATE VIEW noten_klasse_codi AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "codi"; 
GRANT SELECT ON Benutzer TO 'codi'@'%';
GRANT SELECT ON Klasse TO 'codi'@'%'; 
GRANT SELECT ON Kurs TO 'codi'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_codi TO 'codi'@'%'; 
GRANT SELECT ON noten_codi TO 'codi'@'%'; 
GRANT SELECT ON noten_klasse_codi TO 'codi'@'%'; 
GRANT SELECT ON Schueler TO 'codi'@'%'; 
GRANT SELECT ON Besucht TO 'codi'@'%'; 
GRANT SELECT ON Belegt TO 'codi'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("jakr", "Jacob Kruse", false);
CREATE USER 'jakr'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_jakr AS 
	SELECT * FROM Note WHERE benutzer = 'jakr' WITH CHECK OPTION; 
CREATE VIEW noten_jakr AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "jakr"; 
CREATE VIEW noten_klasse_jakr AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "jakr"; 
GRANT SELECT ON Benutzer TO 'jakr'@'%';
GRANT SELECT ON Klasse TO 'jakr'@'%'; 
GRANT SELECT ON Kurs TO 'jakr'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_jakr TO 'jakr'@'%'; 
GRANT SELECT ON noten_jakr TO 'jakr'@'%'; 
GRANT SELECT ON noten_klasse_jakr TO 'jakr'@'%'; 
GRANT SELECT ON Schueler TO 'jakr'@'%'; 
GRANT SELECT ON Besucht TO 'jakr'@'%'; 
GRANT SELECT ON Belegt TO 'jakr'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("bear", "Bernd Arnold", false);
CREATE USER 'bear'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_bear AS 
	SELECT * FROM Note WHERE benutzer = 'bear' WITH CHECK OPTION; 
CREATE VIEW noten_bear AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "bear"; 
CREATE VIEW noten_klasse_bear AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "bear"; 
GRANT SELECT ON Benutzer TO 'bear'@'%';
GRANT SELECT ON Klasse TO 'bear'@'%'; 
GRANT SELECT ON Kurs TO 'bear'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_bear TO 'bear'@'%'; 
GRANT SELECT ON noten_bear TO 'bear'@'%'; 
GRANT SELECT ON noten_klasse_bear TO 'bear'@'%'; 
GRANT SELECT ON Schueler TO 'bear'@'%'; 
GRANT SELECT ON Besucht TO 'bear'@'%'; 
GRANT SELECT ON Belegt TO 'bear'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("veha", "Veronica Hartwig", false);
CREATE USER 'veha'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_veha AS 
	SELECT * FROM Note WHERE benutzer = 'veha' WITH CHECK OPTION; 
CREATE VIEW noten_veha AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "veha"; 
CREATE VIEW noten_klasse_veha AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "veha"; 
GRANT SELECT ON Benutzer TO 'veha'@'%';
GRANT SELECT ON Klasse TO 'veha'@'%'; 
GRANT SELECT ON Kurs TO 'veha'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_veha TO 'veha'@'%'; 
GRANT SELECT ON noten_veha TO 'veha'@'%'; 
GRANT SELECT ON noten_klasse_veha TO 'veha'@'%'; 
GRANT SELECT ON Schueler TO 'veha'@'%'; 
GRANT SELECT ON Besucht TO 'veha'@'%'; 
GRANT SELECT ON Belegt TO 'veha'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("casc", "Carmen Schmidt", false);
CREATE USER 'casc'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_casc AS 
	SELECT * FROM Note WHERE benutzer = 'casc' WITH CHECK OPTION; 
CREATE VIEW noten_casc AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "casc"; 
CREATE VIEW noten_klasse_casc AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "casc"; 
GRANT SELECT ON Benutzer TO 'casc'@'%';
GRANT SELECT ON Klasse TO 'casc'@'%'; 
GRANT SELECT ON Kurs TO 'casc'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_casc TO 'casc'@'%'; 
GRANT SELECT ON noten_casc TO 'casc'@'%'; 
GRANT SELECT ON noten_klasse_casc TO 'casc'@'%'; 
GRANT SELECT ON Schueler TO 'casc'@'%'; 
GRANT SELECT ON Besucht TO 'casc'@'%'; 
GRANT SELECT ON Belegt TO 'casc'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("hahi", "Harry Hipp", false);
CREATE USER 'hahi'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_hahi AS 
	SELECT * FROM Note WHERE benutzer = 'hahi' WITH CHECK OPTION; 
CREATE VIEW noten_hahi AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "hahi"; 
CREATE VIEW noten_klasse_hahi AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "hahi"; 
GRANT SELECT ON Benutzer TO 'hahi'@'%';
GRANT SELECT ON Klasse TO 'hahi'@'%'; 
GRANT SELECT ON Kurs TO 'hahi'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_hahi TO 'hahi'@'%'; 
GRANT SELECT ON noten_hahi TO 'hahi'@'%'; 
GRANT SELECT ON noten_klasse_hahi TO 'hahi'@'%'; 
GRANT SELECT ON Schueler TO 'hahi'@'%'; 
GRANT SELECT ON Besucht TO 'hahi'@'%'; 
GRANT SELECT ON Belegt TO 'hahi'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("hagr", "Hans Greif", false);
CREATE USER 'hagr'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_hagr AS 
	SELECT * FROM Note WHERE benutzer = 'hagr' WITH CHECK OPTION; 
CREATE VIEW noten_hagr AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "hagr"; 
CREATE VIEW noten_klasse_hagr AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "hagr"; 
GRANT SELECT ON Benutzer TO 'hagr'@'%';
GRANT SELECT ON Klasse TO 'hagr'@'%'; 
GRANT SELECT ON Kurs TO 'hagr'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_hagr TO 'hagr'@'%'; 
GRANT SELECT ON noten_hagr TO 'hagr'@'%'; 
GRANT SELECT ON noten_klasse_hagr TO 'hagr'@'%'; 
GRANT SELECT ON Schueler TO 'hagr'@'%'; 
GRANT SELECT ON Besucht TO 'hagr'@'%'; 
GRANT SELECT ON Belegt TO 'hagr'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("kari", "Karlheinz Ring", false);
CREATE USER 'kari'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_kari AS 
	SELECT * FROM Note WHERE benutzer = 'kari' WITH CHECK OPTION; 
CREATE VIEW noten_kari AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "kari"; 
CREATE VIEW noten_klasse_kari AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "kari"; 
GRANT SELECT ON Benutzer TO 'kari'@'%';
GRANT SELECT ON Klasse TO 'kari'@'%'; 
GRANT SELECT ON Kurs TO 'kari'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_kari TO 'kari'@'%'; 
GRANT SELECT ON noten_kari TO 'kari'@'%'; 
GRANT SELECT ON noten_klasse_kari TO 'kari'@'%'; 
GRANT SELECT ON Schueler TO 'kari'@'%'; 
GRANT SELECT ON Besucht TO 'kari'@'%'; 
GRANT SELECT ON Belegt TO 'kari'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("heba", "Helmut Baron", false);
CREATE USER 'heba'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_heba AS 
	SELECT * FROM Note WHERE benutzer = 'heba' WITH CHECK OPTION; 
CREATE VIEW noten_heba AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "heba"; 
CREATE VIEW noten_klasse_heba AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "heba"; 
GRANT SELECT ON Benutzer TO 'heba'@'%';
GRANT SELECT ON Klasse TO 'heba'@'%'; 
GRANT SELECT ON Kurs TO 'heba'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_heba TO 'heba'@'%'; 
GRANT SELECT ON noten_heba TO 'heba'@'%'; 
GRANT SELECT ON noten_klasse_heba TO 'heba'@'%'; 
GRANT SELECT ON Schueler TO 'heba'@'%'; 
GRANT SELECT ON Besucht TO 'heba'@'%'; 
GRANT SELECT ON Belegt TO 'heba'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("dapr", "Daniel Preuss", false);
CREATE USER 'dapr'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_dapr AS 
	SELECT * FROM Note WHERE benutzer = 'dapr' WITH CHECK OPTION; 
CREATE VIEW noten_dapr AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "dapr"; 
CREATE VIEW noten_klasse_dapr AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "dapr"; 
GRANT SELECT ON Benutzer TO 'dapr'@'%';
GRANT SELECT ON Klasse TO 'dapr'@'%'; 
GRANT SELECT ON Kurs TO 'dapr'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_dapr TO 'dapr'@'%'; 
GRANT SELECT ON noten_dapr TO 'dapr'@'%'; 
GRANT SELECT ON noten_klasse_dapr TO 'dapr'@'%'; 
GRANT SELECT ON Schueler TO 'dapr'@'%'; 
GRANT SELECT ON Besucht TO 'dapr'@'%'; 
GRANT SELECT ON Belegt TO 'dapr'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("elke", "Elsa Keim", false);
CREATE USER 'elke'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_elke AS 
	SELECT * FROM Note WHERE benutzer = 'elke' WITH CHECK OPTION; 
CREATE VIEW noten_elke AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "elke"; 
CREATE VIEW noten_klasse_elke AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "elke"; 
GRANT SELECT ON Benutzer TO 'elke'@'%';
GRANT SELECT ON Klasse TO 'elke'@'%'; 
GRANT SELECT ON Kurs TO 'elke'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_elke TO 'elke'@'%'; 
GRANT SELECT ON noten_elke TO 'elke'@'%'; 
GRANT SELECT ON noten_klasse_elke TO 'elke'@'%'; 
GRANT SELECT ON Schueler TO 'elke'@'%'; 
GRANT SELECT ON Besucht TO 'elke'@'%'; 
GRANT SELECT ON Belegt TO 'elke'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("repo", "René Pohl", false);
CREATE USER 'repo'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_repo AS 
	SELECT * FROM Note WHERE benutzer = 'repo' WITH CHECK OPTION; 
CREATE VIEW noten_repo AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "repo"; 
CREATE VIEW noten_klasse_repo AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "repo"; 
GRANT SELECT ON Benutzer TO 'repo'@'%';
GRANT SELECT ON Klasse TO 'repo'@'%'; 
GRANT SELECT ON Kurs TO 'repo'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_repo TO 'repo'@'%'; 
GRANT SELECT ON noten_repo TO 'repo'@'%'; 
GRANT SELECT ON noten_klasse_repo TO 'repo'@'%'; 
GRANT SELECT ON Schueler TO 'repo'@'%'; 
GRANT SELECT ON Besucht TO 'repo'@'%'; 
GRANT SELECT ON Belegt TO 'repo'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("embl", "Emilie Blau", false);
CREATE USER 'embl'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_embl AS 
	SELECT * FROM Note WHERE benutzer = 'embl' WITH CHECK OPTION; 
CREATE VIEW noten_embl AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "embl"; 
CREATE VIEW noten_klasse_embl AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "embl"; 
GRANT SELECT ON Benutzer TO 'embl'@'%';
GRANT SELECT ON Klasse TO 'embl'@'%'; 
GRANT SELECT ON Kurs TO 'embl'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_embl TO 'embl'@'%'; 
GRANT SELECT ON noten_embl TO 'embl'@'%'; 
GRANT SELECT ON noten_klasse_embl TO 'embl'@'%'; 
GRANT SELECT ON Schueler TO 'embl'@'%'; 
GRANT SELECT ON Besucht TO 'embl'@'%'; 
GRANT SELECT ON Belegt TO 'embl'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("brso", "Brigitte Sorg", false);
CREATE USER 'brso'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_brso AS 
	SELECT * FROM Note WHERE benutzer = 'brso' WITH CHECK OPTION; 
CREATE VIEW noten_brso AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "brso"; 
CREATE VIEW noten_klasse_brso AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "brso"; 
GRANT SELECT ON Benutzer TO 'brso'@'%';
GRANT SELECT ON Klasse TO 'brso'@'%'; 
GRANT SELECT ON Kurs TO 'brso'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_brso TO 'brso'@'%'; 
GRANT SELECT ON noten_brso TO 'brso'@'%'; 
GRANT SELECT ON noten_klasse_brso TO 'brso'@'%'; 
GRANT SELECT ON Schueler TO 'brso'@'%'; 
GRANT SELECT ON Besucht TO 'brso'@'%'; 
GRANT SELECT ON Belegt TO 'brso'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("alme", "Alfred Meissner", false);
CREATE USER 'alme'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_alme AS 
	SELECT * FROM Note WHERE benutzer = 'alme' WITH CHECK OPTION; 
CREATE VIEW noten_alme AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "alme"; 
CREATE VIEW noten_klasse_alme AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "alme"; 
GRANT SELECT ON Benutzer TO 'alme'@'%';
GRANT SELECT ON Klasse TO 'alme'@'%'; 
GRANT SELECT ON Kurs TO 'alme'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_alme TO 'alme'@'%'; 
GRANT SELECT ON noten_alme TO 'alme'@'%'; 
GRANT SELECT ON noten_klasse_alme TO 'alme'@'%'; 
GRANT SELECT ON Schueler TO 'alme'@'%'; 
GRANT SELECT ON Besucht TO 'alme'@'%'; 
GRANT SELECT ON Belegt TO 'alme'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("clwe", "Claus Wedel", false);
CREATE USER 'clwe'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_clwe AS 
	SELECT * FROM Note WHERE benutzer = 'clwe' WITH CHECK OPTION; 
CREATE VIEW noten_clwe AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "clwe"; 
CREATE VIEW noten_klasse_clwe AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "clwe"; 
GRANT SELECT ON Benutzer TO 'clwe'@'%';
GRANT SELECT ON Klasse TO 'clwe'@'%'; 
GRANT SELECT ON Kurs TO 'clwe'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_clwe TO 'clwe'@'%'; 
GRANT SELECT ON noten_clwe TO 'clwe'@'%'; 
GRANT SELECT ON noten_klasse_clwe TO 'clwe'@'%'; 
GRANT SELECT ON Schueler TO 'clwe'@'%'; 
GRANT SELECT ON Besucht TO 'clwe'@'%'; 
GRANT SELECT ON Belegt TO 'clwe'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("leku", "Lennart Küpper", false);
CREATE USER 'leku'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_leku AS 
	SELECT * FROM Note WHERE benutzer = 'leku' WITH CHECK OPTION; 
CREATE VIEW noten_leku AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "leku"; 
CREATE VIEW noten_klasse_leku AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "leku"; 
GRANT SELECT ON Benutzer TO 'leku'@'%';
GRANT SELECT ON Klasse TO 'leku'@'%'; 
GRANT SELECT ON Kurs TO 'leku'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_leku TO 'leku'@'%'; 
GRANT SELECT ON noten_leku TO 'leku'@'%'; 
GRANT SELECT ON noten_klasse_leku TO 'leku'@'%'; 
GRANT SELECT ON Schueler TO 'leku'@'%'; 
GRANT SELECT ON Besucht TO 'leku'@'%'; 
GRANT SELECT ON Belegt TO 'leku'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("jala", "Jasmin Langer", false);
CREATE USER 'jala'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_jala AS 
	SELECT * FROM Note WHERE benutzer = 'jala' WITH CHECK OPTION; 
CREATE VIEW noten_jala AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "jala"; 
CREATE VIEW noten_klasse_jala AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "jala"; 
GRANT SELECT ON Benutzer TO 'jala'@'%';
GRANT SELECT ON Klasse TO 'jala'@'%'; 
GRANT SELECT ON Kurs TO 'jala'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_jala TO 'jala'@'%'; 
GRANT SELECT ON noten_jala TO 'jala'@'%'; 
GRANT SELECT ON noten_klasse_jala TO 'jala'@'%'; 
GRANT SELECT ON Schueler TO 'jala'@'%'; 
GRANT SELECT ON Besucht TO 'jala'@'%'; 
GRANT SELECT ON Belegt TO 'jala'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("debr", "Dennis Bruder", false);
CREATE USER 'debr'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_debr AS 
	SELECT * FROM Note WHERE benutzer = 'debr' WITH CHECK OPTION; 
CREATE VIEW noten_debr AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "debr"; 
CREATE VIEW noten_klasse_debr AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "debr"; 
GRANT SELECT ON Benutzer TO 'debr'@'%';
GRANT SELECT ON Klasse TO 'debr'@'%'; 
GRANT SELECT ON Kurs TO 'debr'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_debr TO 'debr'@'%'; 
GRANT SELECT ON noten_debr TO 'debr'@'%'; 
GRANT SELECT ON noten_klasse_debr TO 'debr'@'%'; 
GRANT SELECT ON Schueler TO 'debr'@'%'; 
GRANT SELECT ON Besucht TO 'debr'@'%'; 
GRANT SELECT ON Belegt TO 'debr'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("fiec", "Finn Ecker", false);
CREATE USER 'fiec'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_fiec AS 
	SELECT * FROM Note WHERE benutzer = 'fiec' WITH CHECK OPTION; 
CREATE VIEW noten_fiec AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "fiec"; 
CREATE VIEW noten_klasse_fiec AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "fiec"; 
GRANT SELECT ON Benutzer TO 'fiec'@'%';
GRANT SELECT ON Klasse TO 'fiec'@'%'; 
GRANT SELECT ON Kurs TO 'fiec'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_fiec TO 'fiec'@'%'; 
GRANT SELECT ON noten_fiec TO 'fiec'@'%'; 
GRANT SELECT ON noten_klasse_fiec TO 'fiec'@'%'; 
GRANT SELECT ON Schueler TO 'fiec'@'%'; 
GRANT SELECT ON Besucht TO 'fiec'@'%'; 
GRANT SELECT ON Belegt TO 'fiec'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("gest", "Gerd Stegmann", false);
CREATE USER 'gest'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_gest AS 
	SELECT * FROM Note WHERE benutzer = 'gest' WITH CHECK OPTION; 
CREATE VIEW noten_gest AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "gest"; 
CREATE VIEW noten_klasse_gest AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "gest"; 
GRANT SELECT ON Benutzer TO 'gest'@'%';
GRANT SELECT ON Klasse TO 'gest'@'%'; 
GRANT SELECT ON Kurs TO 'gest'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_gest TO 'gest'@'%'; 
GRANT SELECT ON noten_gest TO 'gest'@'%'; 
GRANT SELECT ON noten_klasse_gest TO 'gest'@'%'; 
GRANT SELECT ON Schueler TO 'gest'@'%'; 
GRANT SELECT ON Besucht TO 'gest'@'%'; 
GRANT SELECT ON Belegt TO 'gest'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("geki", "Gerd Kirschbaum", false);
CREATE USER 'geki'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_geki AS 
	SELECT * FROM Note WHERE benutzer = 'geki' WITH CHECK OPTION; 
CREATE VIEW noten_geki AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "geki"; 
CREATE VIEW noten_klasse_geki AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "geki"; 
GRANT SELECT ON Benutzer TO 'geki'@'%';
GRANT SELECT ON Klasse TO 'geki'@'%'; 
GRANT SELECT ON Kurs TO 'geki'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_geki TO 'geki'@'%'; 
GRANT SELECT ON noten_geki TO 'geki'@'%'; 
GRANT SELECT ON noten_klasse_geki TO 'geki'@'%'; 
GRANT SELECT ON Schueler TO 'geki'@'%'; 
GRANT SELECT ON Besucht TO 'geki'@'%'; 
GRANT SELECT ON Belegt TO 'geki'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("lisc", "Linda Scheuermann", false);
CREATE USER 'lisc'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_lisc AS 
	SELECT * FROM Note WHERE benutzer = 'lisc' WITH CHECK OPTION; 
CREATE VIEW noten_lisc AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "lisc"; 
CREATE VIEW noten_klasse_lisc AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "lisc"; 
GRANT SELECT ON Benutzer TO 'lisc'@'%';
GRANT SELECT ON Klasse TO 'lisc'@'%'; 
GRANT SELECT ON Kurs TO 'lisc'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_lisc TO 'lisc'@'%'; 
GRANT SELECT ON noten_lisc TO 'lisc'@'%'; 
GRANT SELECT ON noten_klasse_lisc TO 'lisc'@'%'; 
GRANT SELECT ON Schueler TO 'lisc'@'%'; 
GRANT SELECT ON Besucht TO 'lisc'@'%'; 
GRANT SELECT ON Belegt TO 'lisc'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("imsc", "Imke Schreiber", false);
CREATE USER 'imsc'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_imsc AS 
	SELECT * FROM Note WHERE benutzer = 'imsc' WITH CHECK OPTION; 
CREATE VIEW noten_imsc AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "imsc"; 
CREATE VIEW noten_klasse_imsc AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "imsc"; 
GRANT SELECT ON Benutzer TO 'imsc'@'%';
GRANT SELECT ON Klasse TO 'imsc'@'%'; 
GRANT SELECT ON Kurs TO 'imsc'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_imsc TO 'imsc'@'%'; 
GRANT SELECT ON noten_imsc TO 'imsc'@'%'; 
GRANT SELECT ON noten_klasse_imsc TO 'imsc'@'%'; 
GRANT SELECT ON Schueler TO 'imsc'@'%'; 
GRANT SELECT ON Besucht TO 'imsc'@'%'; 
GRANT SELECT ON Belegt TO 'imsc'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("pare", "Patrick Reich", false);
CREATE USER 'pare'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_pare AS 
	SELECT * FROM Note WHERE benutzer = 'pare' WITH CHECK OPTION; 
CREATE VIEW noten_pare AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "pare"; 
CREATE VIEW noten_klasse_pare AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "pare"; 
GRANT SELECT ON Benutzer TO 'pare'@'%';
GRANT SELECT ON Klasse TO 'pare'@'%'; 
GRANT SELECT ON Kurs TO 'pare'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_pare TO 'pare'@'%'; 
GRANT SELECT ON noten_pare TO 'pare'@'%'; 
GRANT SELECT ON noten_klasse_pare TO 'pare'@'%'; 
GRANT SELECT ON Schueler TO 'pare'@'%'; 
GRANT SELECT ON Besucht TO 'pare'@'%'; 
GRANT SELECT ON Belegt TO 'pare'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("miki", "Michael Kießling", false);
CREATE USER 'miki'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_miki AS 
	SELECT * FROM Note WHERE benutzer = 'miki' WITH CHECK OPTION; 
CREATE VIEW noten_miki AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "miki"; 
CREATE VIEW noten_klasse_miki AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "miki"; 
GRANT SELECT ON Benutzer TO 'miki'@'%';
GRANT SELECT ON Klasse TO 'miki'@'%'; 
GRANT SELECT ON Kurs TO 'miki'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_miki TO 'miki'@'%'; 
GRANT SELECT ON noten_miki TO 'miki'@'%'; 
GRANT SELECT ON noten_klasse_miki TO 'miki'@'%'; 
GRANT SELECT ON Schueler TO 'miki'@'%'; 
GRANT SELECT ON Besucht TO 'miki'@'%'; 
GRANT SELECT ON Belegt TO 'miki'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("faeb", "Fatma Eberlein", false);
CREATE USER 'faeb'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_faeb AS 
	SELECT * FROM Note WHERE benutzer = 'faeb' WITH CHECK OPTION; 
CREATE VIEW noten_faeb AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "faeb"; 
CREATE VIEW noten_klasse_faeb AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "faeb"; 
GRANT SELECT ON Benutzer TO 'faeb'@'%';
GRANT SELECT ON Klasse TO 'faeb'@'%'; 
GRANT SELECT ON Kurs TO 'faeb'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_faeb TO 'faeb'@'%'; 
GRANT SELECT ON noten_faeb TO 'faeb'@'%'; 
GRANT SELECT ON noten_klasse_faeb TO 'faeb'@'%'; 
GRANT SELECT ON Schueler TO 'faeb'@'%'; 
GRANT SELECT ON Besucht TO 'faeb'@'%'; 
GRANT SELECT ON Belegt TO 'faeb'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("olho", "Olaf Hoffmeister", false);
CREATE USER 'olho'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_olho AS 
	SELECT * FROM Note WHERE benutzer = 'olho' WITH CHECK OPTION; 
CREATE VIEW noten_olho AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "olho"; 
CREATE VIEW noten_klasse_olho AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "olho"; 
GRANT SELECT ON Benutzer TO 'olho'@'%';
GRANT SELECT ON Klasse TO 'olho'@'%'; 
GRANT SELECT ON Kurs TO 'olho'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_olho TO 'olho'@'%'; 
GRANT SELECT ON noten_olho TO 'olho'@'%'; 
GRANT SELECT ON noten_klasse_olho TO 'olho'@'%'; 
GRANT SELECT ON Schueler TO 'olho'@'%'; 
GRANT SELECT ON Besucht TO 'olho'@'%'; 
GRANT SELECT ON Belegt TO 'olho'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("dobe", "Dörthe Bensch", false);
CREATE USER 'dobe'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_dobe AS 
	SELECT * FROM Note WHERE benutzer = 'dobe' WITH CHECK OPTION; 
CREATE VIEW noten_dobe AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "dobe"; 
CREATE VIEW noten_klasse_dobe AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "dobe"; 
GRANT SELECT ON Benutzer TO 'dobe'@'%';
GRANT SELECT ON Klasse TO 'dobe'@'%'; 
GRANT SELECT ON Kurs TO 'dobe'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_dobe TO 'dobe'@'%'; 
GRANT SELECT ON noten_dobe TO 'dobe'@'%'; 
GRANT SELECT ON noten_klasse_dobe TO 'dobe'@'%'; 
GRANT SELECT ON Schueler TO 'dobe'@'%'; 
GRANT SELECT ON Besucht TO 'dobe'@'%'; 
GRANT SELECT ON Belegt TO 'dobe'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("rako", "Ralf Koller", false);
CREATE USER 'rako'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_rako AS 
	SELECT * FROM Note WHERE benutzer = 'rako' WITH CHECK OPTION; 
CREATE VIEW noten_rako AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "rako"; 
CREATE VIEW noten_klasse_rako AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "rako"; 
GRANT SELECT ON Benutzer TO 'rako'@'%';
GRANT SELECT ON Klasse TO 'rako'@'%'; 
GRANT SELECT ON Kurs TO 'rako'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_rako TO 'rako'@'%'; 
GRANT SELECT ON noten_rako TO 'rako'@'%'; 
GRANT SELECT ON noten_klasse_rako TO 'rako'@'%'; 
GRANT SELECT ON Schueler TO 'rako'@'%'; 
GRANT SELECT ON Besucht TO 'rako'@'%'; 
GRANT SELECT ON Belegt TO 'rako'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("dare", "David Rentsch", false);
CREATE USER 'dare'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_dare AS 
	SELECT * FROM Note WHERE benutzer = 'dare' WITH CHECK OPTION; 
CREATE VIEW noten_dare AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "dare"; 
CREATE VIEW noten_klasse_dare AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "dare"; 
GRANT SELECT ON Benutzer TO 'dare'@'%';
GRANT SELECT ON Klasse TO 'dare'@'%'; 
GRANT SELECT ON Kurs TO 'dare'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_dare TO 'dare'@'%'; 
GRANT SELECT ON noten_dare TO 'dare'@'%'; 
GRANT SELECT ON noten_klasse_dare TO 'dare'@'%'; 
GRANT SELECT ON Schueler TO 'dare'@'%'; 
GRANT SELECT ON Besucht TO 'dare'@'%'; 
GRANT SELECT ON Belegt TO 'dare'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("sigr", "Siegfried Grebe", false);
CREATE USER 'sigr'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_sigr AS 
	SELECT * FROM Note WHERE benutzer = 'sigr' WITH CHECK OPTION; 
CREATE VIEW noten_sigr AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "sigr"; 
CREATE VIEW noten_klasse_sigr AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "sigr"; 
GRANT SELECT ON Benutzer TO 'sigr'@'%';
GRANT SELECT ON Klasse TO 'sigr'@'%'; 
GRANT SELECT ON Kurs TO 'sigr'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_sigr TO 'sigr'@'%'; 
GRANT SELECT ON noten_sigr TO 'sigr'@'%'; 
GRANT SELECT ON noten_klasse_sigr TO 'sigr'@'%'; 
GRANT SELECT ON Schueler TO 'sigr'@'%'; 
GRANT SELECT ON Besucht TO 'sigr'@'%'; 
GRANT SELECT ON Belegt TO 'sigr'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("mami", "Marco Mischke", false);
CREATE USER 'mami'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_mami AS 
	SELECT * FROM Note WHERE benutzer = 'mami' WITH CHECK OPTION; 
CREATE VIEW noten_mami AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "mami"; 
CREATE VIEW noten_klasse_mami AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "mami"; 
GRANT SELECT ON Benutzer TO 'mami'@'%';
GRANT SELECT ON Klasse TO 'mami'@'%'; 
GRANT SELECT ON Kurs TO 'mami'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_mami TO 'mami'@'%'; 
GRANT SELECT ON noten_mami TO 'mami'@'%'; 
GRANT SELECT ON noten_klasse_mami TO 'mami'@'%'; 
GRANT SELECT ON Schueler TO 'mami'@'%'; 
GRANT SELECT ON Besucht TO 'mami'@'%'; 
GRANT SELECT ON Belegt TO 'mami'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("brse", "Britta Severin", false);
CREATE USER 'brse'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_brse AS 
	SELECT * FROM Note WHERE benutzer = 'brse' WITH CHECK OPTION; 
CREATE VIEW noten_brse AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "brse"; 
CREATE VIEW noten_klasse_brse AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "brse"; 
GRANT SELECT ON Benutzer TO 'brse'@'%';
GRANT SELECT ON Klasse TO 'brse'@'%'; 
GRANT SELECT ON Kurs TO 'brse'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_brse TO 'brse'@'%'; 
GRANT SELECT ON noten_brse TO 'brse'@'%'; 
GRANT SELECT ON noten_klasse_brse TO 'brse'@'%'; 
GRANT SELECT ON Schueler TO 'brse'@'%'; 
GRANT SELECT ON Besucht TO 'brse'@'%'; 
GRANT SELECT ON Belegt TO 'brse'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("tobe", "Tom Bergmann", false);
CREATE USER 'tobe'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_tobe AS 
	SELECT * FROM Note WHERE benutzer = 'tobe' WITH CHECK OPTION; 
CREATE VIEW noten_tobe AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "tobe"; 
CREATE VIEW noten_klasse_tobe AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "tobe"; 
GRANT SELECT ON Benutzer TO 'tobe'@'%';
GRANT SELECT ON Klasse TO 'tobe'@'%'; 
GRANT SELECT ON Kurs TO 'tobe'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_tobe TO 'tobe'@'%'; 
GRANT SELECT ON noten_tobe TO 'tobe'@'%'; 
GRANT SELECT ON noten_klasse_tobe TO 'tobe'@'%'; 
GRANT SELECT ON Schueler TO 'tobe'@'%'; 
GRANT SELECT ON Besucht TO 'tobe'@'%'; 
GRANT SELECT ON Belegt TO 'tobe'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("erja", "Erwin Jacobsen", false);
CREATE USER 'erja'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_erja AS 
	SELECT * FROM Note WHERE benutzer = 'erja' WITH CHECK OPTION; 
CREATE VIEW noten_erja AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "erja"; 
CREATE VIEW noten_klasse_erja AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "erja"; 
GRANT SELECT ON Benutzer TO 'erja'@'%';
GRANT SELECT ON Klasse TO 'erja'@'%'; 
GRANT SELECT ON Kurs TO 'erja'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_erja TO 'erja'@'%'; 
GRANT SELECT ON noten_erja TO 'erja'@'%'; 
GRANT SELECT ON noten_klasse_erja TO 'erja'@'%'; 
GRANT SELECT ON Schueler TO 'erja'@'%'; 
GRANT SELECT ON Besucht TO 'erja'@'%'; 
GRANT SELECT ON Belegt TO 'erja'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("wizi", "Wiebke Ziemer", false);
CREATE USER 'wizi'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_wizi AS 
	SELECT * FROM Note WHERE benutzer = 'wizi' WITH CHECK OPTION; 
CREATE VIEW noten_wizi AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "wizi"; 
CREATE VIEW noten_klasse_wizi AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "wizi"; 
GRANT SELECT ON Benutzer TO 'wizi'@'%';
GRANT SELECT ON Klasse TO 'wizi'@'%'; 
GRANT SELECT ON Kurs TO 'wizi'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_wizi TO 'wizi'@'%'; 
GRANT SELECT ON noten_wizi TO 'wizi'@'%'; 
GRANT SELECT ON noten_klasse_wizi TO 'wizi'@'%'; 
GRANT SELECT ON Schueler TO 'wizi'@'%'; 
GRANT SELECT ON Besucht TO 'wizi'@'%'; 
GRANT SELECT ON Belegt TO 'wizi'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("besc", "Berta Schreier", false);
CREATE USER 'besc'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_besc AS 
	SELECT * FROM Note WHERE benutzer = 'besc' WITH CHECK OPTION; 
CREATE VIEW noten_besc AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "besc"; 
CREATE VIEW noten_klasse_besc AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "besc"; 
GRANT SELECT ON Benutzer TO 'besc'@'%';
GRANT SELECT ON Klasse TO 'besc'@'%'; 
GRANT SELECT ON Kurs TO 'besc'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_besc TO 'besc'@'%'; 
GRANT SELECT ON noten_besc TO 'besc'@'%'; 
GRANT SELECT ON noten_klasse_besc TO 'besc'@'%'; 
GRANT SELECT ON Schueler TO 'besc'@'%'; 
GRANT SELECT ON Besucht TO 'besc'@'%'; 
GRANT SELECT ON Belegt TO 'besc'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("joot", "Joachim Otten", false);
CREATE USER 'joot'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_joot AS 
	SELECT * FROM Note WHERE benutzer = 'joot' WITH CHECK OPTION; 
CREATE VIEW noten_joot AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "joot"; 
CREATE VIEW noten_klasse_joot AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "joot"; 
GRANT SELECT ON Benutzer TO 'joot'@'%';
GRANT SELECT ON Klasse TO 'joot'@'%'; 
GRANT SELECT ON Kurs TO 'joot'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_joot TO 'joot'@'%'; 
GRANT SELECT ON noten_joot TO 'joot'@'%'; 
GRANT SELECT ON noten_klasse_joot TO 'joot'@'%'; 
GRANT SELECT ON Schueler TO 'joot'@'%'; 
GRANT SELECT ON Besucht TO 'joot'@'%'; 
GRANT SELECT ON Belegt TO 'joot'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("syhe", "Sybille Helmer", false);
CREATE USER 'syhe'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_syhe AS 
	SELECT * FROM Note WHERE benutzer = 'syhe' WITH CHECK OPTION; 
CREATE VIEW noten_syhe AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "syhe"; 
CREATE VIEW noten_klasse_syhe AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "syhe"; 
GRANT SELECT ON Benutzer TO 'syhe'@'%';
GRANT SELECT ON Klasse TO 'syhe'@'%'; 
GRANT SELECT ON Kurs TO 'syhe'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_syhe TO 'syhe'@'%'; 
GRANT SELECT ON noten_syhe TO 'syhe'@'%'; 
GRANT SELECT ON noten_klasse_syhe TO 'syhe'@'%'; 
GRANT SELECT ON Schueler TO 'syhe'@'%'; 
GRANT SELECT ON Besucht TO 'syhe'@'%'; 
GRANT SELECT ON Belegt TO 'syhe'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("koka", "Kordula Kamm", false);
CREATE USER 'koka'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_koka AS 
	SELECT * FROM Note WHERE benutzer = 'koka' WITH CHECK OPTION; 
CREATE VIEW noten_koka AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "koka"; 
CREATE VIEW noten_klasse_koka AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "koka"; 
GRANT SELECT ON Benutzer TO 'koka'@'%';
GRANT SELECT ON Klasse TO 'koka'@'%'; 
GRANT SELECT ON Kurs TO 'koka'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_koka TO 'koka'@'%'; 
GRANT SELECT ON noten_koka TO 'koka'@'%'; 
GRANT SELECT ON noten_klasse_koka TO 'koka'@'%'; 
GRANT SELECT ON Schueler TO 'koka'@'%'; 
GRANT SELECT ON Besucht TO 'koka'@'%'; 
GRANT SELECT ON Belegt TO 'koka'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("chba", "Christel Baldauf", false);
CREATE USER 'chba'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_chba AS 
	SELECT * FROM Note WHERE benutzer = 'chba' WITH CHECK OPTION; 
CREATE VIEW noten_chba AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "chba"; 
CREATE VIEW noten_klasse_chba AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "chba"; 
GRANT SELECT ON Benutzer TO 'chba'@'%';
GRANT SELECT ON Klasse TO 'chba'@'%'; 
GRANT SELECT ON Kurs TO 'chba'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_chba TO 'chba'@'%'; 
GRANT SELECT ON noten_chba TO 'chba'@'%'; 
GRANT SELECT ON noten_klasse_chba TO 'chba'@'%'; 
GRANT SELECT ON Schueler TO 'chba'@'%'; 
GRANT SELECT ON Besucht TO 'chba'@'%'; 
GRANT SELECT ON Belegt TO 'chba'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("halo", "Harri Loos", false);
CREATE USER 'halo'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_halo AS 
	SELECT * FROM Note WHERE benutzer = 'halo' WITH CHECK OPTION; 
CREATE VIEW noten_halo AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "halo"; 
CREATE VIEW noten_klasse_halo AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "halo"; 
GRANT SELECT ON Benutzer TO 'halo'@'%';
GRANT SELECT ON Klasse TO 'halo'@'%'; 
GRANT SELECT ON Kurs TO 'halo'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_halo TO 'halo'@'%'; 
GRANT SELECT ON noten_halo TO 'halo'@'%'; 
GRANT SELECT ON noten_klasse_halo TO 'halo'@'%'; 
GRANT SELECT ON Schueler TO 'halo'@'%'; 
GRANT SELECT ON Besucht TO 'halo'@'%'; 
GRANT SELECT ON Belegt TO 'halo'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("heho", "Herta Holzwarth", false);
CREATE USER 'heho'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_heho AS 
	SELECT * FROM Note WHERE benutzer = 'heho' WITH CHECK OPTION; 
CREATE VIEW noten_heho AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "heho"; 
CREATE VIEW noten_klasse_heho AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "heho"; 
GRANT SELECT ON Benutzer TO 'heho'@'%';
GRANT SELECT ON Klasse TO 'heho'@'%'; 
GRANT SELECT ON Kurs TO 'heho'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_heho TO 'heho'@'%'; 
GRANT SELECT ON noten_heho TO 'heho'@'%'; 
GRANT SELECT ON noten_klasse_heho TO 'heho'@'%'; 
GRANT SELECT ON Schueler TO 'heho'@'%'; 
GRANT SELECT ON Besucht TO 'heho'@'%'; 
GRANT SELECT ON Belegt TO 'heho'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("hosp", "Holger Sperlich", false);
CREATE USER 'hosp'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_hosp AS 
	SELECT * FROM Note WHERE benutzer = 'hosp' WITH CHECK OPTION; 
CREATE VIEW noten_hosp AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "hosp"; 
CREATE VIEW noten_klasse_hosp AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "hosp"; 
GRANT SELECT ON Benutzer TO 'hosp'@'%';
GRANT SELECT ON Klasse TO 'hosp'@'%'; 
GRANT SELECT ON Kurs TO 'hosp'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_hosp TO 'hosp'@'%'; 
GRANT SELECT ON noten_hosp TO 'hosp'@'%'; 
GRANT SELECT ON noten_klasse_hosp TO 'hosp'@'%'; 
GRANT SELECT ON Schueler TO 'hosp'@'%'; 
GRANT SELECT ON Besucht TO 'hosp'@'%'; 
GRANT SELECT ON Belegt TO 'hosp'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("jath", "Janett Thome", false);
CREATE USER 'jath'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_jath AS 
	SELECT * FROM Note WHERE benutzer = 'jath' WITH CHECK OPTION; 
CREATE VIEW noten_jath AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "jath"; 
CREATE VIEW noten_klasse_jath AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "jath"; 
GRANT SELECT ON Benutzer TO 'jath'@'%';
GRANT SELECT ON Klasse TO 'jath'@'%'; 
GRANT SELECT ON Kurs TO 'jath'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_jath TO 'jath'@'%'; 
GRANT SELECT ON noten_jath TO 'jath'@'%'; 
GRANT SELECT ON noten_klasse_jath TO 'jath'@'%'; 
GRANT SELECT ON Schueler TO 'jath'@'%'; 
GRANT SELECT ON Besucht TO 'jath'@'%'; 
GRANT SELECT ON Belegt TO 'jath'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("emad", "Emily Adrian", false);
CREATE USER 'emad'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_emad AS 
	SELECT * FROM Note WHERE benutzer = 'emad' WITH CHECK OPTION; 
CREATE VIEW noten_emad AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "emad"; 
CREATE VIEW noten_klasse_emad AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "emad"; 
GRANT SELECT ON Benutzer TO 'emad'@'%';
GRANT SELECT ON Klasse TO 'emad'@'%'; 
GRANT SELECT ON Kurs TO 'emad'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_emad TO 'emad'@'%'; 
GRANT SELECT ON noten_emad TO 'emad'@'%'; 
GRANT SELECT ON noten_klasse_emad TO 'emad'@'%'; 
GRANT SELECT ON Schueler TO 'emad'@'%'; 
GRANT SELECT ON Besucht TO 'emad'@'%'; 
GRANT SELECT ON Belegt TO 'emad'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("joni", "Jonathan Nitz", false);
CREATE USER 'joni'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_joni AS 
	SELECT * FROM Note WHERE benutzer = 'joni' WITH CHECK OPTION; 
CREATE VIEW noten_joni AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "joni"; 
CREATE VIEW noten_klasse_joni AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "joni"; 
GRANT SELECT ON Benutzer TO 'joni'@'%';
GRANT SELECT ON Klasse TO 'joni'@'%'; 
GRANT SELECT ON Kurs TO 'joni'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_joni TO 'joni'@'%'; 
GRANT SELECT ON noten_joni TO 'joni'@'%'; 
GRANT SELECT ON noten_klasse_joni TO 'joni'@'%'; 
GRANT SELECT ON Schueler TO 'joni'@'%'; 
GRANT SELECT ON Besucht TO 'joni'@'%'; 
GRANT SELECT ON Belegt TO 'joni'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("dowe", "Dörthe Westermann", false);
CREATE USER 'dowe'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_dowe AS 
	SELECT * FROM Note WHERE benutzer = 'dowe' WITH CHECK OPTION; 
CREATE VIEW noten_dowe AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "dowe"; 
CREATE VIEW noten_klasse_dowe AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "dowe"; 
GRANT SELECT ON Benutzer TO 'dowe'@'%';
GRANT SELECT ON Klasse TO 'dowe'@'%'; 
GRANT SELECT ON Kurs TO 'dowe'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_dowe TO 'dowe'@'%'; 
GRANT SELECT ON noten_dowe TO 'dowe'@'%'; 
GRANT SELECT ON noten_klasse_dowe TO 'dowe'@'%'; 
GRANT SELECT ON Schueler TO 'dowe'@'%'; 
GRANT SELECT ON Besucht TO 'dowe'@'%'; 
GRANT SELECT ON Belegt TO 'dowe'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("noka", "Noah Kaminski", false);
CREATE USER 'noka'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_noka AS 
	SELECT * FROM Note WHERE benutzer = 'noka' WITH CHECK OPTION; 
CREATE VIEW noten_noka AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "noka"; 
CREATE VIEW noten_klasse_noka AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "noka"; 
GRANT SELECT ON Benutzer TO 'noka'@'%';
GRANT SELECT ON Klasse TO 'noka'@'%'; 
GRANT SELECT ON Kurs TO 'noka'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_noka TO 'noka'@'%'; 
GRANT SELECT ON noten_noka TO 'noka'@'%'; 
GRANT SELECT ON noten_klasse_noka TO 'noka'@'%'; 
GRANT SELECT ON Schueler TO 'noka'@'%'; 
GRANT SELECT ON Besucht TO 'noka'@'%'; 
GRANT SELECT ON Belegt TO 'noka'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("bihe", "Birgit Heckmann", false);
CREATE USER 'bihe'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_bihe AS 
	SELECT * FROM Note WHERE benutzer = 'bihe' WITH CHECK OPTION; 
CREATE VIEW noten_bihe AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "bihe"; 
CREATE VIEW noten_klasse_bihe AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "bihe"; 
GRANT SELECT ON Benutzer TO 'bihe'@'%';
GRANT SELECT ON Klasse TO 'bihe'@'%'; 
GRANT SELECT ON Kurs TO 'bihe'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_bihe TO 'bihe'@'%'; 
GRANT SELECT ON noten_bihe TO 'bihe'@'%'; 
GRANT SELECT ON noten_klasse_bihe TO 'bihe'@'%'; 
GRANT SELECT ON Schueler TO 'bihe'@'%'; 
GRANT SELECT ON Besucht TO 'bihe'@'%'; 
GRANT SELECT ON Belegt TO 'bihe'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("kaei", "Katarzyna Eismann", false);
CREATE USER 'kaei'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_kaei AS 
	SELECT * FROM Note WHERE benutzer = 'kaei' WITH CHECK OPTION; 
CREATE VIEW noten_kaei AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "kaei"; 
CREATE VIEW noten_klasse_kaei AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "kaei"; 
GRANT SELECT ON Benutzer TO 'kaei'@'%';
GRANT SELECT ON Klasse TO 'kaei'@'%'; 
GRANT SELECT ON Kurs TO 'kaei'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_kaei TO 'kaei'@'%'; 
GRANT SELECT ON noten_kaei TO 'kaei'@'%'; 
GRANT SELECT ON noten_klasse_kaei TO 'kaei'@'%'; 
GRANT SELECT ON Schueler TO 'kaei'@'%'; 
GRANT SELECT ON Besucht TO 'kaei'@'%'; 
GRANT SELECT ON Belegt TO 'kaei'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("daem", "Daniela Emmert", false);
CREATE USER 'daem'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_daem AS 
	SELECT * FROM Note WHERE benutzer = 'daem' WITH CHECK OPTION; 
CREATE VIEW noten_daem AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "daem"; 
CREATE VIEW noten_klasse_daem AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "daem"; 
GRANT SELECT ON Benutzer TO 'daem'@'%';
GRANT SELECT ON Klasse TO 'daem'@'%'; 
GRANT SELECT ON Kurs TO 'daem'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_daem TO 'daem'@'%'; 
GRANT SELECT ON noten_daem TO 'daem'@'%'; 
GRANT SELECT ON noten_klasse_daem TO 'daem'@'%'; 
GRANT SELECT ON Schueler TO 'daem'@'%'; 
GRANT SELECT ON Besucht TO 'daem'@'%'; 
GRANT SELECT ON Belegt TO 'daem'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("embo", "Emilie Borowski", false);
CREATE USER 'embo'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_embo AS 
	SELECT * FROM Note WHERE benutzer = 'embo' WITH CHECK OPTION; 
CREATE VIEW noten_embo AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "embo"; 
CREATE VIEW noten_klasse_embo AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "embo"; 
GRANT SELECT ON Benutzer TO 'embo'@'%';
GRANT SELECT ON Klasse TO 'embo'@'%'; 
GRANT SELECT ON Kurs TO 'embo'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_embo TO 'embo'@'%'; 
GRANT SELECT ON noten_embo TO 'embo'@'%'; 
GRANT SELECT ON noten_klasse_embo TO 'embo'@'%'; 
GRANT SELECT ON Schueler TO 'embo'@'%'; 
GRANT SELECT ON Besucht TO 'embo'@'%'; 
GRANT SELECT ON Belegt TO 'embo'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("swga", "Swen Gaida", false);
CREATE USER 'swga'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_swga AS 
	SELECT * FROM Note WHERE benutzer = 'swga' WITH CHECK OPTION; 
CREATE VIEW noten_swga AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "swga"; 
CREATE VIEW noten_klasse_swga AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "swga"; 
GRANT SELECT ON Benutzer TO 'swga'@'%';
GRANT SELECT ON Klasse TO 'swga'@'%'; 
GRANT SELECT ON Kurs TO 'swga'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_swga TO 'swga'@'%'; 
GRANT SELECT ON noten_swga TO 'swga'@'%'; 
GRANT SELECT ON noten_klasse_swga TO 'swga'@'%'; 
GRANT SELECT ON Schueler TO 'swga'@'%'; 
GRANT SELECT ON Besucht TO 'swga'@'%'; 
GRANT SELECT ON Belegt TO 'swga'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("fika", "Filiz Kamm", false);
CREATE USER 'fika'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_fika AS 
	SELECT * FROM Note WHERE benutzer = 'fika' WITH CHECK OPTION; 
CREATE VIEW noten_fika AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "fika"; 
CREATE VIEW noten_klasse_fika AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "fika"; 
GRANT SELECT ON Benutzer TO 'fika'@'%';
GRANT SELECT ON Klasse TO 'fika'@'%'; 
GRANT SELECT ON Kurs TO 'fika'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_fika TO 'fika'@'%'; 
GRANT SELECT ON noten_fika TO 'fika'@'%'; 
GRANT SELECT ON noten_klasse_fika TO 'fika'@'%'; 
GRANT SELECT ON Schueler TO 'fika'@'%'; 
GRANT SELECT ON Besucht TO 'fika'@'%'; 
GRANT SELECT ON Belegt TO 'fika'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("siwi", "Sinah Winter", false);
CREATE USER 'siwi'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_siwi AS 
	SELECT * FROM Note WHERE benutzer = 'siwi' WITH CHECK OPTION; 
CREATE VIEW noten_siwi AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "siwi"; 
CREATE VIEW noten_klasse_siwi AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "siwi"; 
GRANT SELECT ON Benutzer TO 'siwi'@'%';
GRANT SELECT ON Klasse TO 'siwi'@'%'; 
GRANT SELECT ON Kurs TO 'siwi'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_siwi TO 'siwi'@'%'; 
GRANT SELECT ON noten_siwi TO 'siwi'@'%'; 
GRANT SELECT ON noten_klasse_siwi TO 'siwi'@'%'; 
GRANT SELECT ON Schueler TO 'siwi'@'%'; 
GRANT SELECT ON Besucht TO 'siwi'@'%'; 
GRANT SELECT ON Belegt TO 'siwi'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("sabr", "Sarah Brand", false);
CREATE USER 'sabr'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_sabr AS 
	SELECT * FROM Note WHERE benutzer = 'sabr' WITH CHECK OPTION; 
CREATE VIEW noten_sabr AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "sabr"; 
CREATE VIEW noten_klasse_sabr AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "sabr"; 
GRANT SELECT ON Benutzer TO 'sabr'@'%';
GRANT SELECT ON Klasse TO 'sabr'@'%'; 
GRANT SELECT ON Kurs TO 'sabr'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_sabr TO 'sabr'@'%'; 
GRANT SELECT ON noten_sabr TO 'sabr'@'%'; 
GRANT SELECT ON noten_klasse_sabr TO 'sabr'@'%'; 
GRANT SELECT ON Schueler TO 'sabr'@'%'; 
GRANT SELECT ON Besucht TO 'sabr'@'%'; 
GRANT SELECT ON Belegt TO 'sabr'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("hail", "Hatice Ilg", false);
CREATE USER 'hail'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_hail AS 
	SELECT * FROM Note WHERE benutzer = 'hail' WITH CHECK OPTION; 
CREATE VIEW noten_hail AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "hail"; 
CREATE VIEW noten_klasse_hail AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "hail"; 
GRANT SELECT ON Benutzer TO 'hail'@'%';
GRANT SELECT ON Klasse TO 'hail'@'%'; 
GRANT SELECT ON Kurs TO 'hail'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_hail TO 'hail'@'%'; 
GRANT SELECT ON noten_hail TO 'hail'@'%'; 
GRANT SELECT ON noten_klasse_hail TO 'hail'@'%'; 
GRANT SELECT ON Schueler TO 'hail'@'%'; 
GRANT SELECT ON Besucht TO 'hail'@'%'; 
GRANT SELECT ON Belegt TO 'hail'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("jasc", "Janet Schult", false);
CREATE USER 'jasc'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_jasc AS 
	SELECT * FROM Note WHERE benutzer = 'jasc' WITH CHECK OPTION; 
CREATE VIEW noten_jasc AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "jasc"; 
CREATE VIEW noten_klasse_jasc AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "jasc"; 
GRANT SELECT ON Benutzer TO 'jasc'@'%';
GRANT SELECT ON Klasse TO 'jasc'@'%'; 
GRANT SELECT ON Kurs TO 'jasc'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_jasc TO 'jasc'@'%'; 
GRANT SELECT ON noten_jasc TO 'jasc'@'%'; 
GRANT SELECT ON noten_klasse_jasc TO 'jasc'@'%'; 
GRANT SELECT ON Schueler TO 'jasc'@'%'; 
GRANT SELECT ON Besucht TO 'jasc'@'%'; 
GRANT SELECT ON Belegt TO 'jasc'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("jabl", "Jakob Blank", false);
CREATE USER 'jabl'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_jabl AS 
	SELECT * FROM Note WHERE benutzer = 'jabl' WITH CHECK OPTION; 
CREATE VIEW noten_jabl AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "jabl"; 
CREATE VIEW noten_klasse_jabl AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "jabl"; 
GRANT SELECT ON Benutzer TO 'jabl'@'%';
GRANT SELECT ON Klasse TO 'jabl'@'%'; 
GRANT SELECT ON Kurs TO 'jabl'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_jabl TO 'jabl'@'%'; 
GRANT SELECT ON noten_jabl TO 'jabl'@'%'; 
GRANT SELECT ON noten_klasse_jabl TO 'jabl'@'%'; 
GRANT SELECT ON Schueler TO 'jabl'@'%'; 
GRANT SELECT ON Besucht TO 'jabl'@'%'; 
GRANT SELECT ON Belegt TO 'jabl'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("frhi", "Franz Hirt", false);
CREATE USER 'frhi'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_frhi AS 
	SELECT * FROM Note WHERE benutzer = 'frhi' WITH CHECK OPTION; 
CREATE VIEW noten_frhi AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "frhi"; 
CREATE VIEW noten_klasse_frhi AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "frhi"; 
GRANT SELECT ON Benutzer TO 'frhi'@'%';
GRANT SELECT ON Klasse TO 'frhi'@'%'; 
GRANT SELECT ON Kurs TO 'frhi'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_frhi TO 'frhi'@'%'; 
GRANT SELECT ON noten_frhi TO 'frhi'@'%'; 
GRANT SELECT ON noten_klasse_frhi TO 'frhi'@'%'; 
GRANT SELECT ON Schueler TO 'frhi'@'%'; 
GRANT SELECT ON Besucht TO 'frhi'@'%'; 
GRANT SELECT ON Belegt TO 'frhi'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("klwe", "Klaus Wegener", false);
CREATE USER 'klwe'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_klwe AS 
	SELECT * FROM Note WHERE benutzer = 'klwe' WITH CHECK OPTION; 
CREATE VIEW noten_klwe AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "klwe"; 
CREATE VIEW noten_klasse_klwe AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "klwe"; 
GRANT SELECT ON Benutzer TO 'klwe'@'%';
GRANT SELECT ON Klasse TO 'klwe'@'%'; 
GRANT SELECT ON Kurs TO 'klwe'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_klwe TO 'klwe'@'%'; 
GRANT SELECT ON noten_klwe TO 'klwe'@'%'; 
GRANT SELECT ON noten_klasse_klwe TO 'klwe'@'%'; 
GRANT SELECT ON Schueler TO 'klwe'@'%'; 
GRANT SELECT ON Besucht TO 'klwe'@'%'; 
GRANT SELECT ON Belegt TO 'klwe'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("erge", "Erika Gerlach", false);
CREATE USER 'erge'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_erge AS 
	SELECT * FROM Note WHERE benutzer = 'erge' WITH CHECK OPTION; 
CREATE VIEW noten_erge AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "erge"; 
CREATE VIEW noten_klasse_erge AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "erge"; 
GRANT SELECT ON Benutzer TO 'erge'@'%';
GRANT SELECT ON Klasse TO 'erge'@'%'; 
GRANT SELECT ON Kurs TO 'erge'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_erge TO 'erge'@'%'; 
GRANT SELECT ON noten_erge TO 'erge'@'%'; 
GRANT SELECT ON noten_klasse_erge TO 'erge'@'%'; 
GRANT SELECT ON Schueler TO 'erge'@'%'; 
GRANT SELECT ON Besucht TO 'erge'@'%'; 
GRANT SELECT ON Belegt TO 'erge'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("nioz", "Nico Öztürk", false);
CREATE USER 'nioz'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_nioz AS 
	SELECT * FROM Note WHERE benutzer = 'nioz' WITH CHECK OPTION; 
CREATE VIEW noten_nioz AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "nioz"; 
CREATE VIEW noten_klasse_nioz AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "nioz"; 
GRANT SELECT ON Benutzer TO 'nioz'@'%';
GRANT SELECT ON Klasse TO 'nioz'@'%'; 
GRANT SELECT ON Kurs TO 'nioz'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_nioz TO 'nioz'@'%'; 
GRANT SELECT ON noten_nioz TO 'nioz'@'%'; 
GRANT SELECT ON noten_klasse_nioz TO 'nioz'@'%'; 
GRANT SELECT ON Schueler TO 'nioz'@'%'; 
GRANT SELECT ON Besucht TO 'nioz'@'%'; 
GRANT SELECT ON Belegt TO 'nioz'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("anhe", "Angela Herold", false);
CREATE USER 'anhe'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_anhe AS 
	SELECT * FROM Note WHERE benutzer = 'anhe' WITH CHECK OPTION; 
CREATE VIEW noten_anhe AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "anhe"; 
CREATE VIEW noten_klasse_anhe AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "anhe"; 
GRANT SELECT ON Benutzer TO 'anhe'@'%';
GRANT SELECT ON Klasse TO 'anhe'@'%'; 
GRANT SELECT ON Kurs TO 'anhe'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_anhe TO 'anhe'@'%'; 
GRANT SELECT ON noten_anhe TO 'anhe'@'%'; 
GRANT SELECT ON noten_klasse_anhe TO 'anhe'@'%'; 
GRANT SELECT ON Schueler TO 'anhe'@'%'; 
GRANT SELECT ON Besucht TO 'anhe'@'%'; 
GRANT SELECT ON Belegt TO 'anhe'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("kosc", "Korinna Schuch", false);
CREATE USER 'kosc'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_kosc AS 
	SELECT * FROM Note WHERE benutzer = 'kosc' WITH CHECK OPTION; 
CREATE VIEW noten_kosc AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "kosc"; 
CREATE VIEW noten_klasse_kosc AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "kosc"; 
GRANT SELECT ON Benutzer TO 'kosc'@'%';
GRANT SELECT ON Klasse TO 'kosc'@'%'; 
GRANT SELECT ON Kurs TO 'kosc'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_kosc TO 'kosc'@'%'; 
GRANT SELECT ON noten_kosc TO 'kosc'@'%'; 
GRANT SELECT ON noten_klasse_kosc TO 'kosc'@'%'; 
GRANT SELECT ON Schueler TO 'kosc'@'%'; 
GRANT SELECT ON Besucht TO 'kosc'@'%'; 
GRANT SELECT ON Belegt TO 'kosc'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("masc", "Mark Scheurer", false);
CREATE USER 'masc'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_masc AS 
	SELECT * FROM Note WHERE benutzer = 'masc' WITH CHECK OPTION; 
CREATE VIEW noten_masc AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "masc"; 
CREATE VIEW noten_klasse_masc AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "masc"; 
GRANT SELECT ON Benutzer TO 'masc'@'%';
GRANT SELECT ON Klasse TO 'masc'@'%'; 
GRANT SELECT ON Kurs TO 'masc'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_masc TO 'masc'@'%'; 
GRANT SELECT ON noten_masc TO 'masc'@'%'; 
GRANT SELECT ON noten_klasse_masc TO 'masc'@'%'; 
GRANT SELECT ON Schueler TO 'masc'@'%'; 
GRANT SELECT ON Besucht TO 'masc'@'%'; 
GRANT SELECT ON Belegt TO 'masc'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("pasc", "Paul Schwabe", false);
CREATE USER 'pasc'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_pasc AS 
	SELECT * FROM Note WHERE benutzer = 'pasc' WITH CHECK OPTION; 
CREATE VIEW noten_pasc AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "pasc"; 
CREATE VIEW noten_klasse_pasc AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "pasc"; 
GRANT SELECT ON Benutzer TO 'pasc'@'%';
GRANT SELECT ON Klasse TO 'pasc'@'%'; 
GRANT SELECT ON Kurs TO 'pasc'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_pasc TO 'pasc'@'%'; 
GRANT SELECT ON noten_pasc TO 'pasc'@'%'; 
GRANT SELECT ON noten_klasse_pasc TO 'pasc'@'%'; 
GRANT SELECT ON Schueler TO 'pasc'@'%'; 
GRANT SELECT ON Besucht TO 'pasc'@'%'; 
GRANT SELECT ON Belegt TO 'pasc'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("kato", "Katie Topp", false);
CREATE USER 'kato'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_kato AS 
	SELECT * FROM Note WHERE benutzer = 'kato' WITH CHECK OPTION; 
CREATE VIEW noten_kato AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "kato"; 
CREATE VIEW noten_klasse_kato AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "kato"; 
GRANT SELECT ON Benutzer TO 'kato'@'%';
GRANT SELECT ON Klasse TO 'kato'@'%'; 
GRANT SELECT ON Kurs TO 'kato'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_kato TO 'kato'@'%'; 
GRANT SELECT ON noten_kato TO 'kato'@'%'; 
GRANT SELECT ON noten_klasse_kato TO 'kato'@'%'; 
GRANT SELECT ON Schueler TO 'kato'@'%'; 
GRANT SELECT ON Besucht TO 'kato'@'%'; 
GRANT SELECT ON Belegt TO 'kato'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("anbo", "Annica Böck", false);
CREATE USER 'anbo'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_anbo AS 
	SELECT * FROM Note WHERE benutzer = 'anbo' WITH CHECK OPTION; 
CREATE VIEW noten_anbo AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "anbo"; 
CREATE VIEW noten_klasse_anbo AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "anbo"; 
GRANT SELECT ON Benutzer TO 'anbo'@'%';
GRANT SELECT ON Klasse TO 'anbo'@'%'; 
GRANT SELECT ON Kurs TO 'anbo'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_anbo TO 'anbo'@'%'; 
GRANT SELECT ON noten_anbo TO 'anbo'@'%'; 
GRANT SELECT ON noten_klasse_anbo TO 'anbo'@'%'; 
GRANT SELECT ON Schueler TO 'anbo'@'%'; 
GRANT SELECT ON Besucht TO 'anbo'@'%'; 
GRANT SELECT ON Belegt TO 'anbo'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("jeda", "Jennifer Dahms", false);
CREATE USER 'jeda'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_jeda AS 
	SELECT * FROM Note WHERE benutzer = 'jeda' WITH CHECK OPTION; 
CREATE VIEW noten_jeda AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "jeda"; 
CREATE VIEW noten_klasse_jeda AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "jeda"; 
GRANT SELECT ON Benutzer TO 'jeda'@'%';
GRANT SELECT ON Klasse TO 'jeda'@'%'; 
GRANT SELECT ON Kurs TO 'jeda'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_jeda TO 'jeda'@'%'; 
GRANT SELECT ON noten_jeda TO 'jeda'@'%'; 
GRANT SELECT ON noten_klasse_jeda TO 'jeda'@'%'; 
GRANT SELECT ON Schueler TO 'jeda'@'%'; 
GRANT SELECT ON Besucht TO 'jeda'@'%'; 
GRANT SELECT ON Belegt TO 'jeda'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("auma", "Auguste Martin", false);
CREATE USER 'auma'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_auma AS 
	SELECT * FROM Note WHERE benutzer = 'auma' WITH CHECK OPTION; 
CREATE VIEW noten_auma AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "auma"; 
CREATE VIEW noten_klasse_auma AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "auma"; 
GRANT SELECT ON Benutzer TO 'auma'@'%';
GRANT SELECT ON Klasse TO 'auma'@'%'; 
GRANT SELECT ON Kurs TO 'auma'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_auma TO 'auma'@'%'; 
GRANT SELECT ON noten_auma TO 'auma'@'%'; 
GRANT SELECT ON noten_klasse_auma TO 'auma'@'%'; 
GRANT SELECT ON Schueler TO 'auma'@'%'; 
GRANT SELECT ON Besucht TO 'auma'@'%'; 
GRANT SELECT ON Belegt TO 'auma'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("osma", "Oscar Marks", false);
CREATE USER 'osma'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_osma AS 
	SELECT * FROM Note WHERE benutzer = 'osma' WITH CHECK OPTION; 
CREATE VIEW noten_osma AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "osma"; 
CREATE VIEW noten_klasse_osma AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "osma"; 
GRANT SELECT ON Benutzer TO 'osma'@'%';
GRANT SELECT ON Klasse TO 'osma'@'%'; 
GRANT SELECT ON Kurs TO 'osma'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_osma TO 'osma'@'%'; 
GRANT SELECT ON noten_osma TO 'osma'@'%'; 
GRANT SELECT ON noten_klasse_osma TO 'osma'@'%'; 
GRANT SELECT ON Schueler TO 'osma'@'%'; 
GRANT SELECT ON Besucht TO 'osma'@'%'; 
GRANT SELECT ON Belegt TO 'osma'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("kana", "Karola Nagler", false);
CREATE USER 'kana'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_kana AS 
	SELECT * FROM Note WHERE benutzer = 'kana' WITH CHECK OPTION; 
CREATE VIEW noten_kana AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "kana"; 
CREATE VIEW noten_klasse_kana AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "kana"; 
GRANT SELECT ON Benutzer TO 'kana'@'%';
GRANT SELECT ON Klasse TO 'kana'@'%'; 
GRANT SELECT ON Kurs TO 'kana'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_kana TO 'kana'@'%'; 
GRANT SELECT ON noten_kana TO 'kana'@'%'; 
GRANT SELECT ON noten_klasse_kana TO 'kana'@'%'; 
GRANT SELECT ON Schueler TO 'kana'@'%'; 
GRANT SELECT ON Besucht TO 'kana'@'%'; 
GRANT SELECT ON Belegt TO 'kana'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("lini", "Lina Nix", false);
CREATE USER 'lini'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_lini AS 
	SELECT * FROM Note WHERE benutzer = 'lini' WITH CHECK OPTION; 
CREATE VIEW noten_lini AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "lini"; 
CREATE VIEW noten_klasse_lini AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "lini"; 
GRANT SELECT ON Benutzer TO 'lini'@'%';
GRANT SELECT ON Klasse TO 'lini'@'%'; 
GRANT SELECT ON Kurs TO 'lini'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_lini TO 'lini'@'%'; 
GRANT SELECT ON noten_lini TO 'lini'@'%'; 
GRANT SELECT ON noten_klasse_lini TO 'lini'@'%'; 
GRANT SELECT ON Schueler TO 'lini'@'%'; 
GRANT SELECT ON Besucht TO 'lini'@'%'; 
GRANT SELECT ON Belegt TO 'lini'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("hobo", "Holger Bohm", false);
CREATE USER 'hobo'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_hobo AS 
	SELECT * FROM Note WHERE benutzer = 'hobo' WITH CHECK OPTION; 
CREATE VIEW noten_hobo AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "hobo"; 
CREATE VIEW noten_klasse_hobo AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "hobo"; 
GRANT SELECT ON Benutzer TO 'hobo'@'%';
GRANT SELECT ON Klasse TO 'hobo'@'%'; 
GRANT SELECT ON Kurs TO 'hobo'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_hobo TO 'hobo'@'%'; 
GRANT SELECT ON noten_hobo TO 'hobo'@'%'; 
GRANT SELECT ON noten_klasse_hobo TO 'hobo'@'%'; 
GRANT SELECT ON Schueler TO 'hobo'@'%'; 
GRANT SELECT ON Besucht TO 'hobo'@'%'; 
GRANT SELECT ON Belegt TO 'hobo'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("sisc", "Sibylle Schenke", false);
CREATE USER 'sisc'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_sisc AS 
	SELECT * FROM Note WHERE benutzer = 'sisc' WITH CHECK OPTION; 
CREATE VIEW noten_sisc AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "sisc"; 
CREATE VIEW noten_klasse_sisc AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "sisc"; 
GRANT SELECT ON Benutzer TO 'sisc'@'%';
GRANT SELECT ON Klasse TO 'sisc'@'%'; 
GRANT SELECT ON Kurs TO 'sisc'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_sisc TO 'sisc'@'%'; 
GRANT SELECT ON noten_sisc TO 'sisc'@'%'; 
GRANT SELECT ON noten_klasse_sisc TO 'sisc'@'%'; 
GRANT SELECT ON Schueler TO 'sisc'@'%'; 
GRANT SELECT ON Besucht TO 'sisc'@'%'; 
GRANT SELECT ON Belegt TO 'sisc'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("axme", "Axel Meiners", false);
CREATE USER 'axme'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_axme AS 
	SELECT * FROM Note WHERE benutzer = 'axme' WITH CHECK OPTION; 
CREATE VIEW noten_axme AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "axme"; 
CREATE VIEW noten_klasse_axme AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "axme"; 
GRANT SELECT ON Benutzer TO 'axme'@'%';
GRANT SELECT ON Klasse TO 'axme'@'%'; 
GRANT SELECT ON Kurs TO 'axme'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_axme TO 'axme'@'%'; 
GRANT SELECT ON noten_axme TO 'axme'@'%'; 
GRANT SELECT ON noten_klasse_axme TO 'axme'@'%'; 
GRANT SELECT ON Schueler TO 'axme'@'%'; 
GRANT SELECT ON Besucht TO 'axme'@'%'; 
GRANT SELECT ON Belegt TO 'axme'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("kaga", "Käthe Gaida", false);
CREATE USER 'kaga'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_kaga AS 
	SELECT * FROM Note WHERE benutzer = 'kaga' WITH CHECK OPTION; 
CREATE VIEW noten_kaga AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "kaga"; 
CREATE VIEW noten_klasse_kaga AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "kaga"; 
GRANT SELECT ON Benutzer TO 'kaga'@'%';
GRANT SELECT ON Klasse TO 'kaga'@'%'; 
GRANT SELECT ON Kurs TO 'kaga'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_kaga TO 'kaga'@'%'; 
GRANT SELECT ON noten_kaga TO 'kaga'@'%'; 
GRANT SELECT ON noten_klasse_kaga TO 'kaga'@'%'; 
GRANT SELECT ON Schueler TO 'kaga'@'%'; 
GRANT SELECT ON Besucht TO 'kaga'@'%'; 
GRANT SELECT ON Belegt TO 'kaga'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("kawi", "Karolin Willms", false);
CREATE USER 'kawi'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_kawi AS 
	SELECT * FROM Note WHERE benutzer = 'kawi' WITH CHECK OPTION; 
CREATE VIEW noten_kawi AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "kawi"; 
CREATE VIEW noten_klasse_kawi AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "kawi"; 
GRANT SELECT ON Benutzer TO 'kawi'@'%';
GRANT SELECT ON Klasse TO 'kawi'@'%'; 
GRANT SELECT ON Kurs TO 'kawi'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_kawi TO 'kawi'@'%'; 
GRANT SELECT ON noten_kawi TO 'kawi'@'%'; 
GRANT SELECT ON noten_klasse_kawi TO 'kawi'@'%'; 
GRANT SELECT ON Schueler TO 'kawi'@'%'; 
GRANT SELECT ON Besucht TO 'kawi'@'%'; 
GRANT SELECT ON Belegt TO 'kawi'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("haja", "Harri Jauch", false);
CREATE USER 'haja'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_haja AS 
	SELECT * FROM Note WHERE benutzer = 'haja' WITH CHECK OPTION; 
CREATE VIEW noten_haja AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "haja"; 
CREATE VIEW noten_klasse_haja AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "haja"; 
GRANT SELECT ON Benutzer TO 'haja'@'%';
GRANT SELECT ON Klasse TO 'haja'@'%'; 
GRANT SELECT ON Kurs TO 'haja'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_haja TO 'haja'@'%'; 
GRANT SELECT ON noten_haja TO 'haja'@'%'; 
GRANT SELECT ON noten_klasse_haja TO 'haja'@'%'; 
GRANT SELECT ON Schueler TO 'haja'@'%'; 
GRANT SELECT ON Besucht TO 'haja'@'%'; 
GRANT SELECT ON Belegt TO 'haja'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("maer", "Maria Erdmann", false);
CREATE USER 'maer'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_maer AS 
	SELECT * FROM Note WHERE benutzer = 'maer' WITH CHECK OPTION; 
CREATE VIEW noten_maer AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "maer"; 
CREATE VIEW noten_klasse_maer AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "maer"; 
GRANT SELECT ON Benutzer TO 'maer'@'%';
GRANT SELECT ON Klasse TO 'maer'@'%'; 
GRANT SELECT ON Kurs TO 'maer'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_maer TO 'maer'@'%'; 
GRANT SELECT ON noten_maer TO 'maer'@'%'; 
GRANT SELECT ON noten_klasse_maer TO 'maer'@'%'; 
GRANT SELECT ON Schueler TO 'maer'@'%'; 
GRANT SELECT ON Besucht TO 'maer'@'%'; 
GRANT SELECT ON Belegt TO 'maer'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("krbi", "Kristiane Birk", false);
CREATE USER 'krbi'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_krbi AS 
	SELECT * FROM Note WHERE benutzer = 'krbi' WITH CHECK OPTION; 
CREATE VIEW noten_krbi AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "krbi"; 
CREATE VIEW noten_klasse_krbi AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "krbi"; 
GRANT SELECT ON Benutzer TO 'krbi'@'%';
GRANT SELECT ON Klasse TO 'krbi'@'%'; 
GRANT SELECT ON Kurs TO 'krbi'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_krbi TO 'krbi'@'%'; 
GRANT SELECT ON noten_krbi TO 'krbi'@'%'; 
GRANT SELECT ON noten_klasse_krbi TO 'krbi'@'%'; 
GRANT SELECT ON Schueler TO 'krbi'@'%'; 
GRANT SELECT ON Besucht TO 'krbi'@'%'; 
GRANT SELECT ON Belegt TO 'krbi'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("rasc", "Ralf Schuller", false);
CREATE USER 'rasc'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_rasc AS 
	SELECT * FROM Note WHERE benutzer = 'rasc' WITH CHECK OPTION; 
CREATE VIEW noten_rasc AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "rasc"; 
CREATE VIEW noten_klasse_rasc AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "rasc"; 
GRANT SELECT ON Benutzer TO 'rasc'@'%';
GRANT SELECT ON Klasse TO 'rasc'@'%'; 
GRANT SELECT ON Kurs TO 'rasc'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_rasc TO 'rasc'@'%'; 
GRANT SELECT ON noten_rasc TO 'rasc'@'%'; 
GRANT SELECT ON noten_klasse_rasc TO 'rasc'@'%'; 
GRANT SELECT ON Schueler TO 'rasc'@'%'; 
GRANT SELECT ON Besucht TO 'rasc'@'%'; 
GRANT SELECT ON Belegt TO 'rasc'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("clda", "Claus Danner", false);
CREATE USER 'clda'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_clda AS 
	SELECT * FROM Note WHERE benutzer = 'clda' WITH CHECK OPTION; 
CREATE VIEW noten_clda AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "clda"; 
CREATE VIEW noten_klasse_clda AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "clda"; 
GRANT SELECT ON Benutzer TO 'clda'@'%';
GRANT SELECT ON Klasse TO 'clda'@'%'; 
GRANT SELECT ON Kurs TO 'clda'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_clda TO 'clda'@'%'; 
GRANT SELECT ON noten_clda TO 'clda'@'%'; 
GRANT SELECT ON noten_klasse_clda TO 'clda'@'%'; 
GRANT SELECT ON Schueler TO 'clda'@'%'; 
GRANT SELECT ON Besucht TO 'clda'@'%'; 
GRANT SELECT ON Belegt TO 'clda'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("maga", "Mark Gärtner", false);
CREATE USER 'maga'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_maga AS 
	SELECT * FROM Note WHERE benutzer = 'maga' WITH CHECK OPTION; 
CREATE VIEW noten_maga AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "maga"; 
CREATE VIEW noten_klasse_maga AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "maga"; 
GRANT SELECT ON Benutzer TO 'maga'@'%';
GRANT SELECT ON Klasse TO 'maga'@'%'; 
GRANT SELECT ON Kurs TO 'maga'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_maga TO 'maga'@'%'; 
GRANT SELECT ON noten_maga TO 'maga'@'%'; 
GRANT SELECT ON noten_klasse_maga TO 'maga'@'%'; 
GRANT SELECT ON Schueler TO 'maga'@'%'; 
GRANT SELECT ON Besucht TO 'maga'@'%'; 
GRANT SELECT ON Belegt TO 'maga'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("juoe", "Julia Oestreich", false);
CREATE USER 'juoe'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_juoe AS 
	SELECT * FROM Note WHERE benutzer = 'juoe' WITH CHECK OPTION; 
CREATE VIEW noten_juoe AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "juoe"; 
CREATE VIEW noten_klasse_juoe AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "juoe"; 
GRANT SELECT ON Benutzer TO 'juoe'@'%';
GRANT SELECT ON Klasse TO 'juoe'@'%'; 
GRANT SELECT ON Kurs TO 'juoe'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_juoe TO 'juoe'@'%'; 
GRANT SELECT ON noten_juoe TO 'juoe'@'%'; 
GRANT SELECT ON noten_klasse_juoe TO 'juoe'@'%'; 
GRANT SELECT ON Schueler TO 'juoe'@'%'; 
GRANT SELECT ON Besucht TO 'juoe'@'%'; 
GRANT SELECT ON Belegt TO 'juoe'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("midi", "Michael Dinkel", false);
CREATE USER 'midi'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_midi AS 
	SELECT * FROM Note WHERE benutzer = 'midi' WITH CHECK OPTION; 
CREATE VIEW noten_midi AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "midi"; 
CREATE VIEW noten_klasse_midi AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "midi"; 
GRANT SELECT ON Benutzer TO 'midi'@'%';
GRANT SELECT ON Klasse TO 'midi'@'%'; 
GRANT SELECT ON Kurs TO 'midi'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_midi TO 'midi'@'%'; 
GRANT SELECT ON noten_midi TO 'midi'@'%'; 
GRANT SELECT ON noten_klasse_midi TO 'midi'@'%'; 
GRANT SELECT ON Schueler TO 'midi'@'%'; 
GRANT SELECT ON Besucht TO 'midi'@'%'; 
GRANT SELECT ON Belegt TO 'midi'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("bema", "Berta Mauer", false);
CREATE USER 'bema'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_bema AS 
	SELECT * FROM Note WHERE benutzer = 'bema' WITH CHECK OPTION; 
CREATE VIEW noten_bema AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "bema"; 
CREATE VIEW noten_klasse_bema AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "bema"; 
GRANT SELECT ON Benutzer TO 'bema'@'%';
GRANT SELECT ON Klasse TO 'bema'@'%'; 
GRANT SELECT ON Kurs TO 'bema'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_bema TO 'bema'@'%'; 
GRANT SELECT ON noten_bema TO 'bema'@'%'; 
GRANT SELECT ON noten_klasse_bema TO 'bema'@'%'; 
GRANT SELECT ON Schueler TO 'bema'@'%'; 
GRANT SELECT ON Besucht TO 'bema'@'%'; 
GRANT SELECT ON Belegt TO 'bema'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("hera", "Hermann Rasche", false);
CREATE USER 'hera'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_hera AS 
	SELECT * FROM Note WHERE benutzer = 'hera' WITH CHECK OPTION; 
CREATE VIEW noten_hera AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "hera"; 
CREATE VIEW noten_klasse_hera AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "hera"; 
GRANT SELECT ON Benutzer TO 'hera'@'%';
GRANT SELECT ON Klasse TO 'hera'@'%'; 
GRANT SELECT ON Kurs TO 'hera'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_hera TO 'hera'@'%'; 
GRANT SELECT ON noten_hera TO 'hera'@'%'; 
GRANT SELECT ON noten_klasse_hera TO 'hera'@'%'; 
GRANT SELECT ON Schueler TO 'hera'@'%'; 
GRANT SELECT ON Besucht TO 'hera'@'%'; 
GRANT SELECT ON Belegt TO 'hera'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("howe", "Holger Wege", false);
CREATE USER 'howe'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_howe AS 
	SELECT * FROM Note WHERE benutzer = 'howe' WITH CHECK OPTION; 
CREATE VIEW noten_howe AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "howe"; 
CREATE VIEW noten_klasse_howe AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "howe"; 
GRANT SELECT ON Benutzer TO 'howe'@'%';
GRANT SELECT ON Klasse TO 'howe'@'%'; 
GRANT SELECT ON Kurs TO 'howe'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_howe TO 'howe'@'%'; 
GRANT SELECT ON noten_howe TO 'howe'@'%'; 
GRANT SELECT ON noten_klasse_howe TO 'howe'@'%'; 
GRANT SELECT ON Schueler TO 'howe'@'%'; 
GRANT SELECT ON Besucht TO 'howe'@'%'; 
GRANT SELECT ON Belegt TO 'howe'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("wire", "Wibke Reisinger", false);
CREATE USER 'wire'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_wire AS 
	SELECT * FROM Note WHERE benutzer = 'wire' WITH CHECK OPTION; 
CREATE VIEW noten_wire AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "wire"; 
CREATE VIEW noten_klasse_wire AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "wire"; 
GRANT SELECT ON Benutzer TO 'wire'@'%';
GRANT SELECT ON Klasse TO 'wire'@'%'; 
GRANT SELECT ON Kurs TO 'wire'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_wire TO 'wire'@'%'; 
GRANT SELECT ON noten_wire TO 'wire'@'%'; 
GRANT SELECT ON noten_klasse_wire TO 'wire'@'%'; 
GRANT SELECT ON Schueler TO 'wire'@'%'; 
GRANT SELECT ON Besucht TO 'wire'@'%'; 
GRANT SELECT ON Belegt TO 'wire'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("dohe", "Dominic Heinrichs", false);
CREATE USER 'dohe'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_dohe AS 
	SELECT * FROM Note WHERE benutzer = 'dohe' WITH CHECK OPTION; 
CREATE VIEW noten_dohe AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "dohe"; 
CREATE VIEW noten_klasse_dohe AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "dohe"; 
GRANT SELECT ON Benutzer TO 'dohe'@'%';
GRANT SELECT ON Klasse TO 'dohe'@'%'; 
GRANT SELECT ON Kurs TO 'dohe'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_dohe TO 'dohe'@'%'; 
GRANT SELECT ON noten_dohe TO 'dohe'@'%'; 
GRANT SELECT ON noten_klasse_dohe TO 'dohe'@'%'; 
GRANT SELECT ON Schueler TO 'dohe'@'%'; 
GRANT SELECT ON Besucht TO 'dohe'@'%'; 
GRANT SELECT ON Belegt TO 'dohe'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("wibe", "Willy Bellmann", false);
CREATE USER 'wibe'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_wibe AS 
	SELECT * FROM Note WHERE benutzer = 'wibe' WITH CHECK OPTION; 
CREATE VIEW noten_wibe AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "wibe"; 
CREATE VIEW noten_klasse_wibe AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "wibe"; 
GRANT SELECT ON Benutzer TO 'wibe'@'%';
GRANT SELECT ON Klasse TO 'wibe'@'%'; 
GRANT SELECT ON Kurs TO 'wibe'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_wibe TO 'wibe'@'%'; 
GRANT SELECT ON noten_wibe TO 'wibe'@'%'; 
GRANT SELECT ON noten_klasse_wibe TO 'wibe'@'%'; 
GRANT SELECT ON Schueler TO 'wibe'@'%'; 
GRANT SELECT ON Besucht TO 'wibe'@'%'; 
GRANT SELECT ON Belegt TO 'wibe'@'%';

INSERT INTO Benutzer (loginName, benutzer, istAdmin) VALUE ("thre", "Thorsten Reinhardt", false);
CREATE USER 'thre'@'%' IDENTIFIED BY "1234";
CREATE VIEW noten_loeschen_thre AS 
	SELECT * FROM Note WHERE benutzer = 'thre' WITH CHECK OPTION; 
CREATE VIEW noten_thre AS 
	SELECT kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note JOIN Kurs USING (kurs, kurs_jahr) WHERE kursleiter = "thre"; 
CREATE VIEW noten_klasse_thre AS 
	SELECT klasse, klasse_jahr, kurs, kurs_jahr, schuelerID, noteID, wert, datum, gewichtung, art, kommentar 
	FROM Note 
		JOIN Besucht USING (schuelerID) 
		JOIN Klasse USING (klasse, klasse_jahr) 
	WHERE klassenleiter = "thre"; 
GRANT SELECT ON Benutzer TO 'thre'@'%';
GRANT SELECT ON Klasse TO 'thre'@'%'; 
GRANT SELECT ON Kurs TO 'thre'@'%'; 
GRANT INSERT, DELETE ON noten_loeschen_thre TO 'thre'@'%'; 
GRANT SELECT ON noten_thre TO 'thre'@'%'; 
GRANT SELECT ON noten_klasse_thre TO 'thre'@'%'; 
GRANT SELECT ON Schueler TO 'thre'@'%'; 
GRANT SELECT ON Besucht TO 'thre'@'%'; 
GRANT SELECT ON Belegt TO 'thre'@'%';

INSERT INTO Schueler (schuelerID, schueler, gebDat) VALUES 
	(101, "Adolf Sievers", "2005-8-22"),
	(102, "Gertrud Lehnen", "2004-6-18"),
	(103, "Marco Bernard", "2004-3-28"),
	(104, "Heinz Kuhnert", "2004-8-9"),
	(105, "Andre Niehaus", "2005-12-16"),
	(106, "Maren Moosmann", "2005-7-1"),
	(107, "Celina Reitz", "2005-7-3"),
	(108, "Jakob Behm", "2004-9-11"),
	(109, "Gert Traut", "2005-3-11"),
	(110, "Simone Dahmen", "2006-1-17"),
	(111, "Brigitte Maaßen", "2003-1-2"),
	(112, "Phillipp Kölsch", "2004-4-13"),
	(113, "Annett Kerscher", "2004-8-21"),
	(114, "Gustav Schneck", "2004-1-31"),
	(115, "Sven Nickel", "2005-10-3"),
	(116, "Clara Knauf", "2005-5-29"),
	(117, "Thomas Tölle", "2006-12-12"),
	(118, "Karsten Holstein", "2004-4-1"),
	(119, "Kathrin Probst", "2005-2-22"),
	(120, "Brigitte Dinter", "2005-3-17"),
	(121, "Anja Kreutzer", "2005-6-11"),
	(122, "Timm Leber", "2006-5-24"),
	(123, "Gabriele Hase", "2004-8-13"),
	(124, "Emilie Tausch", "2005-8-24"),
	(125, "Catrin Tiede", "2004-10-13"),
	(126, "Jonathan Uhl", "2005-2-1"),
	(127, "Björn Schleicher", "2006-12-12"),
	(128, "Lea Sieben", "2004-6-4"),
	(129, "Louis Haußmann", "2003-6-18"),
	(130, "Marie Feldmann", "2004-8-6"),
	(131, "Lili Mittelstädt", "2005-7-31"),
	(132, "Yannik Yilmaz", "2005-8-17"),
	(133, "Bettina Börger", "2005-8-20"),
	(134, "Luis Engl", "2004-10-21"),
	(135, "Karl-Heinz Wittich", "2005-7-25"),
	(136, "Luisa Bloch", "2004-7-21"),
	(137, "Henni Sinn", "2004-10-8"),
	(138, "Claudia Orth", "2004-8-5"),
	(139, "Annika Staudinger", "2006-7-25"),
	(140, "Wolfgang Röder", "2005-4-11"),
	(141, "Uwe Stenzel", "2004-8-25"),
	(142, "Heinrich Sieber", "2006-3-9"),
	(143, "Meik Härtel", "2005-6-15"),
	(144, "Gabi Hauschild", "2005-5-22"),
	(145, "Rebecca Buck", "2005-3-10"),
	(146, "Auguste Teske", "2005-12-6"),
	(147, "Katharina Schreier", "2004-12-30"),
	(148, "David Teichmann", "2004-6-19"),
	(149, "Marta Hanisch", "2004-7-30"),
	(150, "Claus Gerling", "2004-1-30"),
	(151, "Simone Mezger", "2004-2-4"),
	(152, "Tanja Pahl", "2005-10-1"),
	(153, "Oskar Wünsche", "2006-4-18"),
	(154, "Christiane Reindl", "2005-12-22"),
	(155, "Beatrice Rauh", "2005-2-21"),
	(156, "Stephan Kiefer", "2004-7-2"),
	(157, "Heinrich Rank", "2004-5-25"),
	(158, "Werner Aßmann", "2004-3-21"),
	(159, "Patrick Schober", "2005-12-6"),
	(160, "Walter Buchmann", "2006-8-10"),
	(161, "Eveline Schülke", "2005-9-11"),
	(162, "Leoni Daum", "2005-4-28"),
	(163, "Swantje Nießen", "2004-9-1"),
	(164, "Otto Morgenroth", "2004-11-6"),
	(165, "Lukas Huber", "2004-3-17"),
	(166, "Elfriede Schweizer", "2005-4-1"),
	(167, "Yannic Gutmann", "2005-7-24"),
	(168, "Dieter Benz", "2005-6-2"),
	(169, "Louisa Reinsch", "2006-7-8"),
	(170, "Herbert Wolfrum", "2004-6-24"),
	(171, "Harry Weißenborn", "2005-6-24"),
	(172, "Dominic Zipfel", "2004-2-11"),
	(173, "Mirjam Siegl", "2005-5-17"),
	(174, "Stephan Wittek", "2005-1-4"),
	(175, "Tim Ferber", "2004-3-16"),
	(176, "Yannik Gaul", "2006-10-1"),
	(177, "Antje Schaaf", "2006-3-14"),
	(178, "Otto Schorn", "2005-10-12"),
	(179, "Emil Holzinger", "2005-10-12"),
	(180, "Hellmut Preiß", "2004-4-12"),
	(181, "Elisabeth Haberland", "2004-11-23"),
	(182, "Konstanze Duda", "2004-2-8"),
	(183, "Mark Behrend", "2005-5-25"),
	(184, "Jan Schünemann", "2004-2-13"),
	(185, "Emilie Kremser", "2004-6-24"),
	(186, "Ramona Rabe", "2005-7-16"),
	(187, "Marco Weiler", "2005-6-1"),
	(188, "Niels Kirchhoff", "2006-3-8"),
	(189, "Karl-Heinz Kissel", "2005-1-25"),
	(190, "Lennart Dreher", "2005-9-8"),
	(191, "Carolin Schaal", "2005-12-6"),
	(192, "Justin Jaeger", "2003-2-11"),
	(193, "Timm Klages", "2005-9-4"),
	(194, "Dennis Petry", "2005-6-30"),
	(195, "Meike Eibl", "2006-6-9"),
	(196, "Johann Heimann", "2005-3-30"),
	(197, "Alexandra Langguth", "2005-5-10"),
	(198, "Berit Nußbaum", "2004-6-27"),
	(199, "Artur Linz", "2004-1-26"),
	(200, "Noah Seubert", "2004-9-27"),
	(201, "Iris Deppe", "2005-3-7"),
	(202, "Karlheinz Liebmann", "2006-6-18"),
	(203, "Sophia Gehring", "2004-12-5"),
	(204, "Anita Czech", "2006-6-17"),
	(205, "Celina Franzen", "2005-7-14"),
	(206, "Caroline Stahl", "2005-2-18"),
	(207, "Alina Kowalski", "2003-12-27"),
	(208, "Mario Bock", "2003-1-11"),
	(209, "Bianca Hildebrand", "2004-3-30"),
	(210, "Karsten Braune", "2005-4-2"),
	(211, "Volker Beutler", "2006-1-30"),
	(212, "Margarete Teubner", "2005-8-14"),
	(213, "Marc Sebastian", "2005-1-26"),
	(214, "Dominic Köhler", "2006-9-1"),
	(215, "Viktoria Gürtler", "2005-6-23"),
	(216, "Verena Hanisch", "2007-11-27"),
	(217, "Jonas Zahn", "2004-12-23"),
	(218, "Lidia Boy", "2005-11-7"),
	(219, "Ursula Falkenberg", "2004-7-10"),
	(220, "Dana Langbein", "2003-10-17"),
	(221, "Birte Ferstl", "2005-10-12"),
	(222, "Steffen Nelles", "2005-5-14"),
	(223, "Frida Pietschmann", "2003-11-22"),
	(224, "Anke Lanz", "2007-4-12"),
	(225, "Kevin Oswald", "2006-8-9"),
	(226, "Kay Wichmann", "2005-10-29"),
	(227, "Yannick Hirt", "2005-11-22"),
	(228, "Peggy Werle", "2005-1-26"),
	(229, "Celina Schmitz", "2006-9-7"),
	(230, "Günter Kalb", "2005-10-6"),
	(231, "Jannik Mast", "2005-5-11"),
	(232, "Max Schmid", "2005-5-26"),
	(233, "Waltraud Haß", "2003-12-28"),
	(234, "Antonia Eckert", "2005-1-2"),
	(235, "Holger Tonn", "2006-8-27"),
	(236, "Noah Strunz", "2005-7-13"),
	(237, "Vera Kaminski", "2005-3-8"),
	(238, "Joachim Mack", "2005-12-16"),
	(239, "Finn Renz", "2004-3-18"),
	(240, "Joanna Storck", "2005-6-26"),
	(241, "Caroline Engelke", "2004-5-28"),
	(242, "Oskar Bayer", "2004-5-22"),
	(243, "Rainer Leis", "2005-3-11"),
	(244, "Heinz Hillmann", "2006-6-23"),
	(245, "Frauke Wiegel", "2005-5-31"),
	(246, "Anna Strube", "2004-2-23"),
	(247, "Lars Schindler", "2006-9-14"),
	(248, "Swantje Wittmann", "2005-7-19"),
	(249, "Elias Kirschbaum", "2006-7-9"),
	(250, "Wibke Hommel", "2004-4-29"),
	(251, "Heinz Neumeier", "2006-2-12"),
	(252, "Horst Otto", "2005-12-2"),
	(253, "Kristina Grebe", "2003-11-25"),
	(254, "Ann Mehl", "2005-6-6"),
	(255, "Uwe Giesecke", "2006-12-14"),
	(256, "Ole Zimmermann", "2005-7-13"),
	(257, "Lutz Wolff", "2005-9-8"),
	(258, "Marina Strobel", "2005-12-5"),
	(259, "Sven Junge", "2004-11-8"),
	(260, "Judith Bade", "2004-4-23"),
	(261, "Robert Lichtenberg", "2005-6-11"),
	(262, "Günther Gehring", "2005-11-5"),
	(263, "Lili Rehm", "2003-2-23"),
	(264, "Irina Oswald", "2006-8-22"),
	(265, "Dorothea Stockmann", "2005-6-9"),
	(266, "Olaf Fix", "2005-9-26"),
	(267, "Mareike Guth", "2004-6-19"),
	(268, "Carola Naß", "2004-7-9"),
	(269, "Erich Großkopf", "2006-4-5"),
	(270, "Marcus Burg", "2004-9-1"),
	(271, "Simon Seifert", "2004-12-26"),
	(272, "Karola Mies", "2005-12-18"),
	(273, "Hermann Sacher", "2005-4-6"),
	(274, "Janett Wagner", "2004-8-21"),
	(275, "Jacob Alt", "2005-9-19"),
	(276, "Joachim Senger", "2004-1-26"),
	(277, "Mathias Baumert", "2004-5-9"),
	(278, "Janin Kempf", "2005-12-14"),
	(279, "Daniel Riemann", "2004-5-22"),
	(280, "Elsa Leder", "2005-4-30"),
	(281, "Matthias Brennecke", "2005-8-8"),
	(282, "Steffen Gebauer", "2004-1-21"),
	(283, "Olga List", "2005-6-1"),
	(284, "Horst Casper", "2004-8-12"),
	(285, "Walter Schleich", "2005-3-7"),
	(286, "Doreen Koppe", "2005-4-30"),
	(287, "Harry Knopf", "2005-6-10"),
	(288, "Juliane Heide", "2006-5-26"),
	(289, "Carola Bangert", "2003-2-18"),
	(290, "Melissa Lohse", "2005-5-3"),
	(291, "Reiner Eichmann", "2005-12-21"),
	(292, "Martin Busche", "2005-9-2"),
	(293, "Nele Reichenbach", "2006-7-4"),
	(294, "Elias Gürtler", "2004-1-14"),
	(295, "Fynn Olschewski", "2005-8-4"),
	(296, "Sascha Wohlfarth", "2005-1-16"),
	(297, "Rolf Ertl", "2006-4-7"),
	(298, "Anke Strauß", "2004-6-10"),
	(299, "Johann Kögel", "2004-3-29"),
	(300, "Christina Groth", "2006-8-7"),
	(301, "Christine Schönemann", "2005-7-28"),
	(302, "Josef Schöning", "2003-3-28"),
	(303, "Jacqueline Topp", "2004-2-11"),
	(304, "Ariane Hense", "2005-12-4"),
	(305, "Martina Völker", "2004-11-25"),
	(306, "Natascha Schuh", "2005-11-11"),
	(307, "Barbara Zühlke", "2003-8-3"),
	(308, "Hellmut Seifert", "2004-11-5"),
	(309, "Gerd Neuber", "2003-6-18"),
	(310, "Katharina Falke", "2003-7-17"),
	(311, "Christoph Döll", "2003-10-16"),
	(312, "Petra Nolte", "2003-5-20"),
	(313, "Dörte Kurth", "2003-6-20"),
	(314, "Rudolph Schmied", "2003-10-11"),
	(315, "Kathie Buchner", "2004-3-15"),
	(316, "Susanne Rasche", "2003-5-30"),
	(317, "Carl Mertes", "2005-9-30"),
	(318, "Siegfried Obermeier", "2006-10-21"),
	(319, "Heinz Unger", "2005-11-16"),
	(320, "Helena Schmiedel", "2003-7-7"),
	(321, "Lilly Bröcker", "2004-2-28"),
	(322, "Gustav Rasche", "2005-6-28"),
	(323, "Johannes Osterloh", "2003-9-14"),
	(324, "Agnieszka Henseler", "2003-7-30"),
	(325, "Tania Sasse", "2003-5-18"),
	(326, "Jaqueline Reinicke", "2003-3-10"),
	(327, "Lukas Röder", "2005-4-18"),
	(328, "Vera Reichart", "2004-7-16"),
	(329, "Ann Krenz", "2004-6-19"),
	(330, "Oscar Kanzler", "2004-1-29"),
	(331, "Joanna Schenkel", "2004-12-6"),
	(332, "Kordula Kelm", "2004-11-28"),
	(333, "Mike Erbe", "2002-10-26"),
	(334, "Erna Hildenbrand", "2005-6-5"),
	(335, "Luka Hagemann", "2003-1-31"),
	(336, "Annica Wildner", "2003-6-3"),
	(337, "Heinrich Endres", "2004-12-1"),
	(338, "Doris Witte", "2003-4-30"),
	(339, "Carsten Matern", "2003-1-16"),
	(340, "Katrin Oertel", "2005-6-21"),
	(341, "Karl-Heinz Habel", "2004-6-5"),
	(342, "Helene Kindermann", "2002-5-24"),
	(343, "Berta Albert", "2003-2-7"),
	(344, "Daniel Ritter", "2004-9-11"),
	(345, "Jürgen Moos", "2004-2-12"),
	(346, "Andre Lembke", "2004-11-24"),
	(347, "Max Priebe", "2004-11-2"),
	(348, "Carl Ludewig", "2003-4-22"),
	(349, "Maria Dietzel", "2005-2-10"),
	(350, "Louis Göhler", "2002-11-20"),
	(351, "Jeannette Hallmann", "2005-12-22"),
	(352, "Helena Schurr", "2004-8-7"),
	(353, "Sebastian Ley", "2003-12-27"),
	(354, "Tim Wild", "2003-9-26"),
	(355, "Maik Wünsche", "2003-4-28"),
	(356, "Antonia Bergner", "2002-3-8"),
	(357, "Christine Gold", "2004-3-11"),
	(358, "Dirk Stevens", "2003-9-3"),
	(359, "Olga Kunzmann", "2003-4-19"),
	(360, "Ariane Kropf", "2004-6-2"),
	(361, "Siegfried Dettmann", "2005-4-18"),
	(362, "Matthias Hammann", "2004-9-30"),
	(363, "Josef Michael", "2005-7-16"),
	(364, "Waltraud Scherer", "2003-7-24"),
	(365, "Korinna Gräber", "2004-7-27"),
	(366, "Emma Schwab", "2003-10-11"),
	(367, "Sofia Strauß", "2003-1-31"),
	(368, "Susann Mai", "2003-2-6"),
	(369, "Alexander Arlt", "2003-5-29"),
	(370, "Niclas Wuttke", "2005-9-20"),
	(371, "Elfriede Schwerdtfeger", "2005-8-25"),
	(372, "Rainer Meyer", "2004-10-29"),
	(373, "Benjamin Teske", "2003-1-29"),
	(374, "Anita Wendland", "2004-1-14"),
	(375, "Dirk Menke", "2005-6-20"),
	(376, "Lennart Preis", "2004-2-4"),
	(377, "Rainer Pesch", "2004-4-20"),
	(378, "Dominic Stahl", "2004-6-26"),
	(379, "Felix Hey", "2004-3-28"),
	(380, "Helene Hülsmann", "2003-10-13"),
	(381, "Emine Bahr", "2004-2-28"),
	(382, "Carsten Wehner", "2004-11-20"),
	(383, "Walter Schaffer", "2002-8-24"),
	(384, "Lutz Helfrich", "2004-6-24"),
	(385, "Annette Strack", "2003-11-3"),
	(386, "Andrea Prill", "2003-5-6"),
	(387, "Jonathan Linz", "2004-12-5"),
	(388, "Werner Sondermann", "2004-5-14"),
	(389, "Katarina Salzmann", "2003-9-19"),
	(390, "Kristina Kreuzer", "2004-5-29"),
	(391, "André Lechner", "2002-2-2"),
	(392, "Ramona Aßmann", "2004-4-22"),
	(393, "Timm Brüning", "2004-4-6"),
	(394, "Marko Kromer", "2003-9-23"),
	(395, "Catrin Wieczorek", "2003-2-25"),
	(396, "Swenja Küppers", "2005-8-24"),
	(397, "Cordula Bunk", "2005-4-12"),
	(398, "Daniela Gutmann", "2003-8-15"),
	(399, "Friedrich Bohn", "2003-12-29"),
	(400, "Lothar Schipper", "2003-3-19"),
	(401, "Niels Frühauf", "2003-12-22"),
	(402, "Filiz Kehr", "2004-2-18"),
	(403, "Christel Glück", "2003-4-11"),
	(404, "Ralph Limmer", "2003-1-13"),
	(405, "Jacqueline Sachs", "2003-5-16"),
	(406, "Kathi Junge", "2004-1-10"),
	(407, "Kordula Weingärtner", "2003-12-18"),
	(408, "Christian Duda", "2002-4-7"),
	(409, "Hugo Gleich", "2003-7-16"),
	(410, "Lennart Domke", "2003-4-7"),
	(411, "Esther Löwen", "2004-11-24"),
	(412, "Lidia Steffen", "2003-10-16"),
	(413, "Dennis Rick", "2004-2-28"),
	(414, "Korinna Geisler", "2004-5-27"),
	(415, "Karsten Retzlaff", "2003-12-24"),
	(416, "Anica Schmidbauer", "2003-1-30"),
	(417, "Ute Philippi", "2003-12-11"),
	(418, "Sabine Heßler", "2003-6-25"),
	(419, "Frida Borrmann", "2004-6-5"),
	(420, "Jan Fütterer", "2005-9-24"),
	(421, "Wilhelmine Stach", "2004-5-6"),
	(422, "Niels Himmel", "2004-11-6"),
	(423, "Catarina Mittelstädt", "2006-8-6"),
	(424, "André Blumenthal", "2003-3-10"),
	(425, "Katarina Schall", "2003-2-17"),
	(426, "Marvin Hagen", "2002-6-14"),
	(427, "Christa Groth", "2002-11-30"),
	(428, "Ruth Wick", "2004-3-23"),
	(429, "Christoph Pohlmann", "2003-4-13"),
	(430, "Martin Stefan", "2002-1-29"),
	(431, "Kathrin Rosenbaum", "2004-5-18"),
	(432, "Ilona Olbrich", "2005-8-9"),
	(433, "Daniel Lucht", "2003-9-22"),
	(434, "Matthias Russ", "2004-1-25"),
	(435, "Edith Sahin", "2003-2-16"),
	(436, "Luca Stenger", "2004-6-6"),
	(437, "Luis Senft", "2003-5-22"),
	(438, "Stefan Hauser", "2002-12-17"),
	(439, "Emil Holzmann", "2003-8-14"),
	(440, "Otto Wachter", "2004-9-24"),
	(441, "Antje Brückner", "2004-2-1"),
	(442, "Yannik Krone", "2005-8-17"),
	(443, "Yannik Ehrlich", "2003-11-13"),
	(444, "Christian Lüders", "2005-10-1"),
	(445, "Horst Assmann", "2003-10-7"),
	(446, "Günther Jeschke", "2003-4-28"),
	(447, "Ida Thum", "2004-4-11"),
	(448, "Simon Rhein", "2003-6-5"),
	(449, "Mathias Johannes", "2004-7-17"),
	(450, "Isabel Wagenknecht", "2003-3-29"),
	(451, "Steffen Dorsch", "2004-7-21"),
	(452, "Michelle Hampe", "2004-8-4"),
	(453, "Liselotte Faller", "2001-2-6"),
	(454, "Peter Schorn", "2003-12-24"),
	(455, "Albert Wittmann", "2003-3-10"),
	(456, "Rudolf Leuschner", "2004-5-20"),
	(457, "Norbert Tetzlaff", "2004-11-29"),
	(458, "Otto Jessen", "2004-7-5"),
	(459, "Hildegard Weidlich", "2003-1-16"),
	(460, "Annie Drescher", "2004-5-3"),
	(461, "Catarina Görlich", "2002-10-16"),
	(462, "Marta Berens", "2003-1-18"),
	(463, "Steffen Dressler", "2003-7-3"),
	(464, "Lennart Semmler", "2004-9-22"),
	(465, "Yasmin Nix", "2004-4-9"),
	(466, "Lothar Tauber", "2002-6-11"),
	(467, "Kathi Sanders", "2004-9-17"),
	(468, "Antje Huppertz", "2004-7-27"),
	(469, "Sabine Beckert", "2002-5-5"),
	(470, "Volker Demir", "2004-6-20"),
	(471, "Yannick Steinhoff", "2003-5-25"),
	(472, "Birgit Dannenberg", "2004-11-15"),
	(473, "Cindy Donner", "2003-12-20"),
	(474, "Antje Ober", "2004-8-13"),
	(475, "Helga Klaus", "2004-10-8"),
	(476, "Kai Wurm", "2003-7-24"),
	(477, "Rolf Schall", "2004-12-31"),
	(478, "Kathi Scheibel", "2003-10-24"),
	(479, "Marina Nitsche", "2004-1-6"),
	(480, "Ernst Döhler", "2003-8-19"),
	(481, "Luisa Liebe", "2004-11-13"),
	(482, "Marianne Spiekermann", "2004-8-11"),
	(483, "Tobias Niebuhr", "2005-11-7"),
	(484, "Anny Eichholz", "2004-1-2"),
	(485, "Martin Sacher", "2003-8-21"),
	(486, "Leoni Pauly", "2004-4-29"),
	(487, "Diana Kienle", "2005-9-5"),
	(488, "Magdalena Krapf", "2003-7-29"),
	(489, "Dominik Behringer", "2004-11-18"),
	(490, "Christoph Kramer", "2003-8-29"),
	(491, "Günther Nehring", "2003-10-26"),
	(492, "Sara Bartelt", "2003-12-27"),
	(493, "Lucas Höller", "2004-11-24"),
	(494, "Irene Arens", "2004-3-22"),
	(495, "Jürgen Gräfe", "2004-4-27"),
	(496, "Heike Gellert", "2003-8-15"),
	(497, "Ursula Abel", "2005-9-25"),
	(498, "Carola Deckert", "2004-7-31"),
	(499, "Svantje Röll", "2005-11-10"),
	(500, "Maike Jakobi", "2004-3-28"),
	(501, "Ingo Renz", "2002-7-31"),
	(502, "Mike Neitzel", "2002-3-21"),
	(503, "Katarzyna Kämmerer", "2001-3-17"),
	(504, "Leonie Rasche", "2001-11-30"),
	(505, "Ariane Hennemann", "2004-12-16"),
	(506, "Ralph Pfau", "2003-12-13"),
	(507, "Klaus Schimmelpfennig", "2003-10-20"),
	(508, "Olaf Kress", "2004-8-5"),
	(509, "Hans Neher", "2003-4-9"),
	(510, "David Kleber", "2002-5-3"),
	(511, "Yasmin Gentner", "2001-11-15"),
	(512, "Birthe Pickel", "2004-12-3"),
	(513, "Willy Knaus", "2004-3-1"),
	(514, "Hedwig Irmer", "2000-7-2"),
	(515, "Katharina Schaffner", "2003-9-7"),
	(516, "Annette Lauber", "2002-9-25"),
	(517, "Birgit Sell", "2002-3-29"),
	(518, "Kevin Backes", "2003-12-13"),
	(519, "Hugo Stang", "2002-4-27"),
	(520, "Christa Sowa", "2003-10-13"),
	(521, "Erwin Steininger", "2003-7-27"),
	(522, "Timm Späth", "2002-10-25"),
	(523, "Mike Ganter", "2003-8-4"),
	(524, "Erna Göring", "2003-3-1"),
	(525, "Metha Lübke", "2002-7-30"),
	(526, "Ilse Kern", "2002-1-28"),
	(527, "Evelin Scheck", "2003-6-25"),
	(528, "Yannik Eckardt", "2003-12-6"),
	(529, "Karolin Münz", "2002-9-24"),
	(530, "Arthur Janzen", "2003-3-24"),
	(531, "Timm Holz", "2003-9-20"),
	(532, "Mandy Greger", "2003-6-13"),
	(533, "Niclas Riedel", "2002-7-29"),
	(534, "Swen Becher", "2002-11-29"),
	(535, "Martin Faller", "2001-11-27"),
	(536, "Joseph Gräber", "2003-12-7"),
	(537, "Frauke Fels", "2001-8-1"),
	(538, "René Kober", "2002-12-30"),
	(539, "Jürgen Schlömer", "2002-9-19"),
	(540, "Herta Nehring", "2004-9-11"),
	(541, "Finn Knoop", "2003-5-14"),
	(542, "Stephan Reichl", "2001-8-19"),
	(543, "Karlheinz Mäder", "2003-9-19"),
	(544, "Silvia Kilic", "2004-8-11"),
	(545, "Yannic Vieth", "2004-9-8"),
	(546, "Yannic Hauptmann", "2001-9-25"),
	(547, "Annemarie Imhof", "2003-3-27"),
	(548, "André Grau", "2003-4-11"),
	(549, "Heidi Zauner", "2004-10-28"),
	(550, "Anni Brümmer", "2002-6-12"),
	(551, "Patrick Reiff", "2004-10-25"),
	(552, "Helmuth Belz", "2003-5-22"),
	(553, "Sigrid Göpfert", "2004-10-2"),
	(554, "Karsten Waibel", "2002-1-22"),
	(555, "Annette Schwalbe", "2003-10-11"),
	(556, "Ingeborg Mund", "2002-10-26"),
	(557, "Karin Hammerschmidt", "2003-9-6"),
	(558, "Yasmin Sauer", "2002-11-14"),
	(559, "Anneliese Schmieder", "2004-4-27"),
	(560, "Sylwia Huber", "2003-11-25"),
	(561, "Franz Winkel", "2003-5-22"),
	(562, "Arthur Pollmann", "2002-7-11"),
	(563, "Sofie Ring", "2003-10-4"),
	(564, "Lilly Degenhardt", "2002-6-6"),
	(565, "Wilhelm Scheffel", "2001-12-5"),
	(566, "Ingrid Treiber", "2002-4-15"),
	(567, "Janina Kuhnt", "2003-4-13"),
	(568, "Leon Rust", "2003-11-7"),
	(569, "Liselotte Mittmann", "2005-2-17"),
	(570, "Franziska Schümann", "2002-10-16"),
	(571, "Ann Hüttner", "2002-11-3"),
	(572, "Gerd Naumann", "2003-3-8"),
	(573, "Swantje Eder", "2003-6-16"),
	(574, "Sofie Tietze", "2003-9-25"),
	(575, "Sylwia Feiler", "2003-8-11"),
	(576, "Lutz Merkel", "2004-2-12"),
	(577, "Karen Spengler", "2002-9-8"),
	(578, "Wilhelm Reith", "2003-1-13"),
	(579, "Anica Kaufhold", "2004-7-2"),
	(580, "Tatjana Feist", "2005-11-7"),
	(581, "Max Schaffer", "2004-12-6"),
	(582, "Karoline Beckers", "2002-12-12"),
	(583, "Marion Friedl", "2003-4-21"),
	(584, "Friederike Liebert", "2003-2-9"),
	(585, "Simone Mertes", "2003-6-21"),
	(586, "Emil Siemers", "2003-8-16"),
	(587, "Gabriela Meister", "2003-11-10"),
	(588, "Dominic Lehner", "2002-11-18"),
	(589, "Willi Munz", "2001-12-10"),
	(590, "Elli Reger", "2002-8-27"),
	(591, "Dieter Maucher", "2002-11-16"),
	(592, "Günter Sachs", "2002-5-15"),
	(593, "Justin Lucht", "2004-5-4"),
	(594, "Gesa Kirschbaum", "2003-1-13"),
	(595, "Sibylle Kaps", "2001-10-3"),
	(596, "Corinna Edel", "2001-8-19"),
	(597, "Stephan Hartung", "2003-5-19"),
	(598, "André Leibold", "2002-2-15"),
	(599, "Mark Reinhold", "2002-1-26"),
	(600, "Larissa Lamm", "2001-6-10"),
	(601, "Wolfgang Roder", "2004-8-21"),
	(602, "Daniel Seiffert", "2003-1-10"),
	(603, "Luis Daniel", "2001-1-26"),
	(604, "Kristiane Helms", "2003-11-6"),
	(605, "Lennard Höft", "2003-2-1"),
	(606, "Ina Karsten", "2004-12-15"),
	(607, "Rolf Fichtner", "2004-10-20"),
	(608, "Irmgard Brucker", "2002-12-16"),
	(609, "Ralph Acker", "2003-6-4"),
	(610, "Karla Kapp", "2003-1-17"),
	(611, "Gerhard Ladwig", "2002-4-20"),
	(612, "Irene Schmiedel", "2000-5-26"),
	(613, "Evelyne Steinhauer", "2003-9-26"),
	(614, "Judith Augustin", "2002-2-13"),
	(615, "Frida Hering", "2002-5-12"),
	(616, "Michael Reichart", "2001-1-7"),
	(617, "Marcus George", "2002-1-5"),
	(618, "Janina Friedel", "2003-11-30"),
	(619, "Sina Pape", "2002-5-24"),
	(620, "Hans Lambert", "2001-4-5"),
	(621, "Jeannette Spangenberg", "2002-12-21"),
	(622, "Willy Lind", "2001-4-5"),
	(623, "Cathleen Schimmel", "2002-11-12"),
	(624, "Leon Weiland", "2003-10-12"),
	(625, "Lennart Bendig", "2003-9-22"),
	(626, "Janine Huber", "2004-9-24"),
	(627, "Liselotte Hofmann", "2002-10-24"),
	(628, "Dörthe Böker", "2002-9-27"),
	(629, "Malgorzata Kohnen", "2003-10-15"),
	(630, "Josef Schmelzer", "2005-3-26"),
	(631, "Wolfgang Tietjen", "2002-12-14"),
	(632, "Ernst Kupfer", "2002-4-27"),
	(633, "Irina Reinhard", "2003-11-25"),
	(634, "Larissa Hoffmann", "2003-1-21"),
	(635, "Stefanie Rommel", "2003-11-11"),
	(636, "Albert Eichholz", "2003-12-15"),
	(637, "Susan Seibel", "2003-3-10"),
	(638, "Veronica Birke", "2003-11-24"),
	(639, "Florian Beutel", "2003-9-28"),
	(640, "Margot Hacker", "2002-9-29"),
	(641, "Karola Steck", "2002-2-28"),
	(642, "Meike Sand", "2002-4-26"),
	(643, "Nele Gall", "2004-4-7"),
	(644, "Oliver Siegmund", "2004-8-2"),
	(645, "Olaf Reichmann", "2004-1-17"),
	(646, "Anne Neu", "2003-3-4"),
	(647, "Konstanze Grahl", "2002-1-8"),
	(648, "Beata Borchert", "2003-7-15"),
	(649, "Birgit Pfeil", "2002-12-5"),
	(650, "Sylke Keitel", "2001-6-27"),
	(651, "Kati Hass", "2003-12-31"),
	(652, "Katharina Kropf", "2003-10-31"),
	(653, "Melina Beutel", "2001-4-7"),
	(654, "Lara Eckhardt", "2001-4-23"),
	(655, "Simon Kley", "2002-9-16"),
	(656, "Stefan Junk", "2003-3-3"),
	(657, "Marion Lanz", "2003-3-7"),
	(658, "Elsa Kleinhans", "2003-12-29"),
	(659, "Peter Bürger", "2004-5-6"),
	(660, "Lena Gassner", "2002-8-26"),
	(661, "Dieter Fuchs", "2003-2-24"),
	(662, "Ewa Buchholz", "2002-6-17"),
	(663, "Stephan Matthes", "2002-7-20"),
	(664, "Louis Öztürk", "2003-12-18"),
	(665, "Georg Pfeuffer", "2002-6-20"),
	(666, "Metha Schorn", "2001-11-25"),
	(667, "Anneliese Kreß", "2003-3-31"),
	(668, "Kathie Muth", "2003-4-1"),
	(669, "Nina Reiner", "2001-1-23"),
	(670, "Günther Behr", "2003-6-14"),
	(671, "Melissa Leicht", "2002-1-23"),
	(672, "Kornelia Wessel", "2003-3-11"),
	(673, "Neele Roeder", "2003-3-3"),
	(674, "Karl-Heinz Anton", "2002-4-25"),
	(675, "Sophia Faust", "2002-11-6"),
	(676, "Ralf Grosser", "2002-10-20"),
	(677, "Hatice Frenzel", "2002-4-16"),
	(678, "Norbert Hunger", "2002-11-3"),
	(679, "Frida Heisig", "2001-9-29"),
	(680, "Michael Teufel", "2001-6-6"),
	(681, "Catrin Schade", "2002-1-7"),
	(682, "Eric Böck", "2003-1-24"),
	(683, "Oskar Deppe", "2003-5-11"),
	(684, "Käte Rosenkranz", "2002-6-11"),
	(685, "Marie Lohse", "2002-3-11"),
	(686, "Helmuth Gottschling", "2003-3-17"),
	(687, "Annika Neumeier", "2003-7-7"),
	(688, "Tobias Lorenz", "2002-3-4"),
	(689, "Cornelia Lippold", "2002-11-9"),
	(690, "Sylke Christian", "2002-8-13"),
	(691, "Käthe Schöttler", "2002-2-2"),
	(692, "Lennart Pawlik", "2004-6-29"),
	(693, "Mark Kirchhof", "2003-8-3"),
	(694, "Hellmut Enke", "2003-9-9"),
	(695, "Ulrike Fenske", "2003-6-4"),
	(696, "Manja Frieß", "2003-9-7"),
	(697, "Tom Heuer", "2004-8-22"),
	(698, "Benjamin Menzel", "2003-3-26"),
	(699, "Arthur Michael", "2003-10-25"),
	(700, "Leonie Kuschel", "2002-12-8"),
	(701, "Martina Klinger", "2002-5-27"),
	(702, "Christiane Abraham", "2003-10-15"),
	(703, "Malgorzata Taube", "2002-8-7"),
	(704, "Heike Dahm", "2002-2-8"),
	(705, "Beatrice Ahlers", "2001-9-20"),
	(706, "Karl-Heinz Lenk", "2004-5-30"),
	(707, "Benjamin Hinrichsen", "2001-4-25"),
	(708, "Dominic Hesse", "2001-2-27"),
	(709, "Niklas Seyfarth", "2002-7-14"),
	(710, "Käte Tietjen", "2003-5-29"),
	(711, "Elly Eichinger", "2002-6-11"),
	(712, "Max Bruder", "2001-10-25"),
	(713, "Catarina Hammel", "2001-5-4"),
	(714, "Tobias Pfeifer", "2002-7-15"),
	(715, "Hellmut Retzlaff", "2001-3-24"),
	(716, "Niels Gottschling", "2001-12-4"),
	(717, "Niels Schwaiger", "2000-1-2"),
	(718, "Arthur Just", "2001-2-23"),
	(719, "Patrick Rother", "2003-1-31"),
	(720, "Patrizia Bräuer", "2001-1-10"),
	(721, "Dieter Weinberg", "2002-5-26"),
	(722, "Walther Holzner", "2001-10-21"),
	(723, "Stephan Mucha", "2003-2-5"),
	(724, "Cathleen Ewers", "2002-8-14"),
	(725, "Yannick Kretzer", "2003-5-11"),
	(726, "Arthur Sauter", "2001-11-4"),
	(727, "Finn Husemann", "2000-1-30"),
	(728, "Isabell Wiedenmann", "2003-8-26"),
	(729, "Anne Paschke", "2002-6-25"),
	(730, "Ole Wacker", "2001-5-25"),
	(731, "Rebekka Schick", "2001-9-25"),
	(732, "Annett Rose", "2002-9-15"),
	(733, "Waltraud Krell", "2001-4-9"),
	(734, "Sofie Brüggemann", "2003-5-16"),
	(735, "Sybille Weinberger", "2001-5-16"),
	(736, "Gabriela Steffens", "2001-5-28"),
	(737, "Justin Baur", "2000-2-4"),
	(738, "Emine Rieger", "2002-11-22"),
	(739, "Christian Haller", "2001-10-2"),
	(740, "Yannik Bamberger", "2002-6-28"),
	(741, "Harri Heinz", "2001-6-20"),
	(742, "Silke Schuchardt", "2001-9-7"),
	(743, "Yannic Happel", "2001-6-20"),
	(744, "Martin Bräutigam", "2003-2-28"),
	(745, "Christel Böttger", "2002-3-14"),
	(746, "Victoria Mertens", "2001-12-9"),
	(747, "Mario Götte", "2002-6-4"),
	(748, "Jenny Stingl", "2002-1-27"),
	(749, "Eva Salomon", "2002-11-2"),
	(750, "Gertrud Stefan", "2001-2-8"),
	(751, "Yannick Reimann", "2001-11-29"),
	(752, "Dörte Timmermann", "2002-7-26"),
	(753, "Louis Giebel", "2001-12-20"),
	(754, "Dörte Hümmer", "2001-3-4"),
	(755, "Marko Wiesner", "2002-9-27"),
	(756, "Veronica Strecker", "2003-8-6"),
	(757, "Metha Hense", "2003-11-2"),
	(758, "Artur Lind", "2002-11-16"),
	(759, "Anja Keller", "2002-12-21"),
	(760, "Jens Hubert", "2000-11-16"),
	(761, "Karl Eggert", "2001-10-20"),
	(762, "Isabella Tillmann", "2001-5-22"),
	(763, "Jacob Zell", "2001-9-25"),
	(764, "Bernd Rosin", "2003-9-20"),
	(765, "Niclas Thome", "2001-2-13"),
	(766, "Michael Schock", "2003-12-11"),
	(767, "Wolfgang Liebmann", "2003-8-6"),
	(768, "Natalie Kießling", "2002-2-2"),
	(769, "Christel Heide", "2002-7-28"),
	(770, "Helmut Rösler", "2002-12-14"),
	(771, "Veronika Grundmann", "2002-1-24"),
	(772, "Erik Sasse", "2002-5-12"),
	(773, "Olaf Jansen", "2001-6-2"),
	(774, "Margot Schaper", "2000-1-7"),
	(775, "Sybille Riedel", "2002-8-13"),
	(776, "Isabell Piontek", "2002-4-13"),
	(777, "Adolf Kerscher", "2001-3-3"),
	(778, "Cornelia Krings", "2003-6-24"),
	(779, "Ivonne Dohrmann", "2001-11-4"),
	(780, "Eric Leicht", "2003-8-29"),
	(781, "Jens Speer", "2001-9-14"),
	(782, "Lucas Dahmen", "2000-11-6"),
	(783, "Werner Köhne", "2002-3-8"),
	(784, "Frida Dirks", "2002-3-23"),
	(785, "Hannelore Weidemann", "2002-3-24"),
	(786, "Sophia Hornig", "2001-10-1"),
	(787, "Franz Franken", "2000-9-12"),
	(788, "Karl Moosmann", "2003-3-23"),
	(789, "Rita Frenzel", "2001-7-18"),
	(790, "Rudolph Leupold", "2002-10-12"),
	(791, "Clara Schrader", "2001-6-26"),
	(792, "Stefanie Geis", "2002-3-25"),
	(793, "Berit Helbing", "2003-3-3"),
	(794, "Johanna Kreft", "2001-11-13"),
	(795, "Matthias Schötz", "2000-2-9"),
	(796, "Else Korb", "2003-8-12"),
	(797, "Nadja Schwabe", "2003-10-8"),
	(798, "Bernd Weidlich", "2000-7-17"),
	(799, "Viola Brugger", "2002-9-6"),
	(800, "Angela Eckl", "2001-4-10"),
	(801, "Bianka Zeidler", "2001-6-13"),
	(802, "Ralf Henne", "2001-9-13"),
	(803, "Svetlana Borchers", "2002-12-21"),
	(804, "Clara Kilian", "2002-1-19"),
	(805, "Maike John", "2003-5-4"),
	(806, "Helena Schmitz", "2002-4-19"),
	(807, "Jacob David", "2001-2-13"),
	(808, "Fabian Kromer", "2001-5-4"),
	(809, "Björn Damm", "2001-1-24"),
	(810, "Marcus Duda", "2002-7-12"),
	(811, "Erich Kellermann", "2002-6-9"),
	(812, "Tamara Bock", "2001-6-6"),
	(813, "Christiane Thies", "2003-6-28"),
	(814, "Max Mehl", "2002-12-5"),
	(815, "Vera Maack", "2001-10-10"),
	(816, "Sascha Raith", "2001-1-11"),
	(817, "Karina Wilms", "2000-5-3"),
	(818, "Jennifer Brede", "2002-12-21"),
	(819, "Tobias Höft", "2002-12-25"),
	(820, "Helmuth Rottmann", "2001-7-11"),
	(821, "Rosemarie Dost", "2001-2-14"),
	(822, "Tim Brust", "2002-8-2"),
	(823, "Philipp Veith", "2002-12-13"),
	(824, "Anna Oehme", "2001-6-2"),
	(825, "Celina Zeiler", "2000-11-29"),
	(826, "Natascha Kampmann", "2003-2-28"),
	(827, "Klaudia Stratmann", "2001-9-22"),
	(828, "Michael Lochner", "2002-6-27"),
	(829, "Tatjana Leicht", "2001-6-7"),
	(830, "Sebastian Balke", "2003-6-26"),
	(831, "Lilli Wecker", "2001-3-23"),
	(832, "Irene Casper", "2003-2-27"),
	(833, "Juliane Gericke", "2001-1-4"),
	(834, "Philip Probst", "2001-2-26"),
	(835, "Joachim Möckel", "2002-1-4"),
	(836, "Christel Stern", "2002-7-27"),
	(837, "Reiner Linden", "2002-10-24"),
	(838, "Melanie Schwind", "2002-12-11"),
	(839, "Lennart Stumm", "2003-8-30"),
	(840, "Gustav Hackmann", "2002-11-5"),
	(841, "Karin Feldhaus", "2001-12-19"),
	(842, "Christina Hense", "2002-8-10"),
	(843, "Marcus Felber", "2003-3-26"),
	(844, "Sarah Leder", "2002-2-7"),
	(845, "Emil Denk", "2001-12-21"),
	(846, "Jan Basler", "2003-9-5"),
	(847, "Johann Ehlert", "2002-4-6"),
	(848, "Alexandra Kost", "2002-8-13"),
	(849, "Rudolf Wild", "2001-12-22"),
	(850, "Simon Straub", "2001-9-9"),
	(851, "Nick Franken", "2002-11-29"),
	(852, "Johannes Albers", "2001-9-27"),
	(853, "Sandra Hirschfeld", "2002-5-5"),
	(854, "Maike Albrecht", "2002-5-11"),
	(855, "Emily Pfitzner", "2001-8-24"),
	(856, "Patrick Jeske", "2003-1-2"),
	(857, "Wibke Klenk", "2000-2-6"),
	(858, "Otto Focke", "2001-4-1"),
	(859, "Berta Holl", "2001-1-6"),
	(860, "Max Höfler", "2002-12-5"),
	(861, "Olaf Wegner", "2001-9-11"),
	(862, "Walther Hertwig", "2002-5-17"),
	(863, "Eveline Schümann", "2002-1-7"),
	(864, "Hermann Raddatz", "2003-1-19"),
	(865, "Yannic Klages", "2002-7-23"),
	(866, "Carmen Bülow", "2002-4-1"),
	(867, "Caroline Strobel", "2000-7-14"),
	(868, "Svetlana Schwenke", "2001-8-24"),
	(869, "Thorsten Gebhardt", "2001-1-19"),
	(870, "Anita Kellner", "2001-11-29"),
	(871, "Jürgen Bürkle", "2004-5-13"),
	(872, "Gabriele Böning", "2003-8-30"),
	(873, "Birgitt Zimmer", "2003-3-15"),
	(874, "Christoph Schnabel", "2001-5-30"),
	(875, "Alfred Tiede", "2002-4-26"),
	(876, "Thorsten Markgraf", "2001-5-16"),
	(877, "Dennis Ruhl", "2002-10-20"),
	(878, "Christina Rüdiger", "2001-3-24"),
	(879, "Rudolf Brack", "2002-11-27"),
	(880, "Lukas Rupp", "2001-3-19"),
	(881, "Anne Zöllner", "2001-4-23"),
	(882, "Constanze Neu", "2001-2-28"),
	(883, "Dörte Sieg", "2000-2-15"),
	(884, "Nico Rosenkranz", "2000-5-3"),
	(885, "Karla Warnke", "2003-2-8"),
	(886, "Lennart Schrader", "2003-3-12"),
	(887, "Leon Häusler", "2002-10-15"),
	(888, "Margarethe Naujoks", "2001-5-26"),
	(889, "Axel Fuchs", "2001-7-30"),
	(890, "Janine Lehmann", "2001-10-11"),
	(891, "Cathrin Benner", "2001-12-23"),
	(892, "André Forstner", "2002-12-28"),
	(893, "Ivonne Bräutigam", "2003-7-9"),
	(894, "Lucas Hoff", "2003-2-7"),
	(895, "Ulrike Rolf", "2000-1-17"),
	(896, "Dominic Braun", "2002-5-9"),
	(897, "Luisa Preiß", "2000-5-24"),
	(898, "Lilly Wollmann", "2001-10-25"),
	(899, "David Voges", "2001-9-20"),
	(900, "Richard Hoff", "2002-9-25"),
	(901, "Monika Friedl", "2002-11-23"),
	(902, "Anne Nehls", "2002-9-12"),
	(903, "Sabine Thiemann", "2001-10-15"),
	(904, "Harri Hauber", "2001-12-30"),
	(905, "Henni Fick", "2001-5-11"),
	(906, "Maik Rücker", "2000-10-14"),
	(907, "Doris Wiens", "2001-11-6"),
	(908, "Walter Helfrich", "2000-11-27"),
	(909, "Erwin Rott", "2000-7-4"),
	(910, "Christine Gast", "2001-6-23"),
	(911, "Sven Michaelis", "2001-8-29"),
	(912, "Philipp Hornig", "2001-8-23"),
	(913, "Sebastian Jost", "2000-1-9"),
	(914, "Anett Höfer", "2000-1-23"),
	(915, "Werner Kupka", "1999-7-30"),
	(916, "Erna Umbach", "2001-1-4"),
	(917, "Annica Brust", "2001-6-13"),
	(918, "Rainer Weitzel", "2001-3-26"),
	(919, "Niko Hildenbrand", "2002-7-17"),
	(920, "Elsa Rottmann", "2000-11-10"),
	(921, "Björn Lex", "1999-12-3"),
	(922, "Luka Radermacher", "2002-1-18"),
	(923, "Lydia Diefenbach", "2001-3-7"),
	(924, "Yannic Seiffert", "2000-3-15"),
	(925, "Doreen Rusch", "2002-4-4"),
	(926, "Viktoria Frings", "2000-11-5"),
	(927, "Britta Böck", "2001-6-27"),
	(928, "Elsa Holzapfel", "2002-7-26"),
	(929, "Angelika Böhringer", "2000-10-31"),
	(930, "Rainer Pflug", "2001-8-13"),
	(931, "Kurt Duwe", "2000-7-22"),
	(932, "Willi Seel", "2001-1-6"),
	(933, "Friedrich Höll", "2002-10-28"),
	(934, "Victoria Majewski", "2001-5-1"),
	(935, "Margarete Bohn", "2002-12-9"),
	(936, "Hermann Reinsch", "2000-5-28"),
	(937, "Irma Schroll", "2001-4-12"),
	(938, "Sofia Heilig", "2002-9-29"),
	(939, "Katarina Kamm", "2000-1-20"),
	(940, "Nadin Birke", "2002-5-6"),
	(941, "Melissa Gebel", "2001-7-3"),
	(942, "Günter Jehle", "2000-2-16"),
	(943, "Hellmut Widmann", "2001-3-6"),
	(944, "Karl Stumm", "2001-5-3"),
	(945, "Rosemarie Rummel", "2003-4-21"),
	(946, "Emil Evers", "2000-6-21"),
	(947, "Curt Schipper", "2002-7-18"),
	(948, "Jeanette Diemer", "2000-9-3"),
	(949, "Fynn Loos", "2000-7-12"),
	(950, "Reiner Heinz", "2001-8-25"),
	(951, "Inge Sorge", "2001-1-1"),
	(952, "Niels Hundertmark", "2000-6-28"),
	(953, "Tobias Kluth", "2002-6-26"),
	(954, "Franz Jessen", "2001-1-12"),
	(955, "Johanna Jungmann", "2001-10-4"),
	(956, "Bettina Eggert", "2001-7-26"),
	(957, "Albert Kern", "2001-8-19"),
	(958, "Regina Dogan", "2001-1-26"),
	(959, "Bernd Rosenkranz", "2000-9-16"),
	(960, "Daniel Lammers", "2000-4-16"),
	(961, "Anne Reil", "2001-3-6"),
	(962, "Anett Lutter", "2001-4-22"),
	(963, "Marion Thiem", "2001-11-6"),
	(964, "Ayse Kretschmann", "2002-5-23"),
	(965, "Friedrich Lieder", "2001-12-7"),
	(966, "Ivonne Friedmann", "2001-5-18"),
	(967, "Luca Nitz", "2001-4-9"),
	(968, "Tanja Damm", "1999-10-19"),
	(969, "Claus Bastian", "2000-1-19"),
	(970, "Johann Rumpf", "2001-6-17"),
	(971, "Moritz Haas", "2000-3-22"),
	(972, "Adolf Weinert", "2001-4-19"),
	(973, "Janin Dittrich", "2001-1-30"),
	(974, "Susan Marx", "2000-8-28"),
	(975, "Yvonne Utz", "2002-10-17"),
	(976, "Thomas Hecker", "2003-4-26"),
	(977, "Karoline Reif", "2001-11-3"),
	(978, "Robert Kunst", "2000-11-19"),
	(979, "Marcus Röll", "2001-8-13"),
	(980, "Maja Seibt", "2000-6-18"),
	(981, "Gisela Grund", "2003-8-24"),
	(982, "Rebekka Mager", "2002-8-25"),
	(983, "Maik Wehner", "2002-3-21"),
	(984, "Walter Seelig", "2001-4-3"),
	(985, "Mark Wiegand", "2001-5-10"),
	(986, "Emilia Sahm", "2002-5-11"),
	(987, "Kerstin Wohlfahrt", "2000-5-30"),
	(988, "Imke Giesen", "1999-7-22"),
	(989, "Kurt Jehle", "2001-10-1"),
	(990, "Steffi Dahmen", "2001-7-10"),
	(991, "Kathleen Schönberger", "2001-4-10"),
	(992, "Dominik Engelhard", "2001-3-25"),
	(993, "Ariane Rosenberg", "2000-10-1"),
	(994, "Katarina Hinrichs", "2000-6-18"),
	(995, "Irina Wegmann", "1999-3-23"),
	(996, "Ludwig Dietl", "2000-6-25"),
	(997, "Marion Rohr", "2000-12-16"),
	(998, "Mario Härtl", "2000-12-9"),
	(999, "Sina Kasten", "2001-9-1"),
	(1000, "Robert Brenner", "2001-6-6"),
	(1001, "Sybille Hammann", "2001-1-13"),
	(1002, "Markus Sievert", "2001-4-6"),
	(1003, "Ulrich Fritsche", "2000-9-21"),
	(1004, "Diana Laumann", "2001-5-11"),
	(1005, "Jens Ries", "2003-5-30"),
	(1006, "Inge Henrichs", "2001-5-4"),
	(1007, "Tom Jung", "2000-7-17"),
	(1008, "Ulrich Gaida", "2000-3-16"),
	(1009, "Jens Kellner", "2001-10-30"),
	(1010, "Juliane Häberle", "2000-3-15"),
	(1011, "Bernd Witt", "2001-9-13"),
	(1012, "Iris Brauner", "2001-9-28"),
	(1013, "Elke Bürkle", "2003-3-21"),
	(1014, "Annette Heiß", "2000-6-24"),
	(1015, "Jeanette Yildirim", "2001-3-6"),
	(1016, "Nadja Nolte", "2000-12-12"),
	(1017, "Jakob Hauer", "2000-5-24"),
	(1018, "Ariane Wendt", "2000-4-22"),
	(1019, "Mia Niehues", "2000-5-29"),
	(1020, "Franz Kleinschmidt", "2000-2-14"),
	(1021, "Dorothea Philipp", "2000-2-6"),
	(1022, "Dominik Felber", "2001-3-29"),
	(1023, "Wibke Steffan", "2001-12-8"),
	(1024, "Rita Oehler", "2001-3-5"),
	(1025, "Sylke Bartel", "2002-11-16"),
	(1026, "Hildegard Weyer", "2001-7-28"),
	(1027, "Lukas Wolters", "2000-2-15"),
	(1028, "Gabriela Langer", "2001-8-23"),
	(1029, "Margot Tremmel", "2001-4-6"),
	(1030, "Amelie Hill", "2000-8-28"),
	(1031, "August Zielke", "2001-6-20"),
	(1032, "Adolf Eppler", "2001-11-24"),
	(1033, "Artur Oehler", "2001-10-29"),
	(1034, "Benjamin Buss", "2001-8-24"),
	(1035, "Artur Gerhards", "2001-12-23"),
	(1036, "Sophie Grafe", "2002-1-31"),
	(1037, "Rebekka Frühauf", "1999-7-17"),
	(1038, "Peter Welz", "2001-3-22"),
	(1039, "Erik Hardt", "2001-2-24"),
	(1040, "Sylke Voigtländer", "2000-3-24"),
	(1041, "René Baumbach", "2001-12-20"),
	(1042, "Ben Schell", "2001-10-1"),
	(1043, "Walter Ullmann", "2001-1-10"),
	(1044, "Janette Reichmann", "2001-2-5"),
	(1045, "Jakob Stern", "2000-10-19"),
	(1046, "Marianne Fuchs", "2001-6-4"),
	(1047, "Philipp Behnke", "2002-10-22"),
	(1048, "Mike Nguyen", "2001-3-25"),
	(1049, "Martin Franken", "2001-3-16"),
	(1050, "Leoni Krell", "2000-11-2"),
	(1051, "Susann Claus", "2000-8-3"),
	(1052, "Hanna Eger", "2001-9-8"),
	(1053, "Lukas Titze", "2000-7-18"),
	(1054, "Ramona Dunker", "2000-7-17"),
	(1055, "Ingrid Hinrichsen", "2001-8-23"),
	(1056, "Lotte Pick", "2000-1-10"),
	(1057, "Nicole Münz", "2001-11-17"),
	(1058, "Klaus Friedrich", "2000-1-12"),
	(1059, "Ella Holland", "2000-10-22"),
	(1060, "Bettina Späth", "2000-12-10"),
	(1061, "Dorothea Arndt", "2000-11-1"),
	(1062, "Marta Winkel", "2001-9-14"),
	(1063, "Esther Kuschel", "1999-6-11"),
	(1064, "Minna Keim", "2001-2-28"),
	(1065, "Horst Kölbl", "2002-4-17"),
	(1066, "Jacob Simons", "2002-4-9"),
	(1067, "Ella Arslan", "1999-6-24"),
	(1068, "Helmuth Kusch", "2001-1-4"),
	(1069, "Jürgen Schnieders", "2001-5-29"),
	(1070, "Matthias Tamm", "2001-2-22"),
	(1071, "Yvonne Brandes", "2000-1-13"),
	(1072, "Mia Fries", "2000-3-14"),
	(1073, "Bärbel Spangenberg", "2001-6-20"),
	(1074, "Mike Rummel", "2001-3-31"),
	(1075, "Heidi Ertl", "2001-2-18"),
	(1076, "Leoni Pöschl", "2001-5-4"),
	(1077, "Moritz Focke", "2002-1-26"),
	(1078, "Sofie Rohr", "2001-10-18"),
	(1079, "Lothar Leistner", "2003-12-25"),
	(1080, "Tatjana Krapp", "2000-8-6"),
	(1081, "Swantje Göllner", "2001-2-25"),
	(1082, "Irene Teuber", "2001-7-23"),
	(1083, "Thorsten Wollschläger", "2002-5-9"),
	(1084, "Veronica Ziegler", "2000-2-24"),
	(1085, "Kristine Albers", "2001-5-1"),
	(1086, "Harri Schaefer", "2000-1-16"),
	(1087, "Kurt Reith", "2001-7-13"),
	(1088, "Claus Zielke", "2000-10-10"),
	(1089, "Gerda Erler", "2000-2-4"),
	(1090, "Katharina Maack", "2001-2-23"),
	(1091, "Swantje Brauer", "2000-5-8"),
	(1092, "Clara Zimmer", "2001-3-6"),
	(1093, "Karlheinz Kuntze", "2000-4-20"),
	(1094, "Christina Schwenk", "2002-10-14"),
	(1095, "Mandy Matt", "2001-10-25"),
	(1096, "Lara Hammel", "2001-7-8"),
	(1097, "Jörg Weidner", "2001-7-18"),
	(1098, "Georg Reinsch", "2001-12-13"),
	(1099, "Heinz Herb", "2001-10-17"),
	(1100, "Nicole Pech", "2002-8-24"),
	(1101, "Torsten Kortmann", "2001-10-26"),
	(1102, "Inge Ludewig", "1999-9-26"),
	(1103, "Margrit Faller", "1998-4-22"),
	(1104, "Peter Dreher", "2001-7-20"),
	(1105, "Jasmin Püschel", "2000-1-11"),
	(1106, "Sebastian Herold", "1998-10-31"),
	(1107, "Florian Schorn", "1999-11-19"),
	(1108, "Susann Busche", "2000-6-30"),
	(1109, "Michael Franck", "2001-11-3"),
	(1110, "Niclas Knopp", "2000-12-13"),
	(1111, "Bruno Kellermann", "1999-7-7"),
	(1112, "Lili Klier", "1999-5-27"),
	(1113, "Yannik Schell", "1999-5-8"),
	(1114, "Lieselotte Schünemann", "1999-9-27"),
	(1115, "Kristin Behrmann", "2000-9-3"),
	(1116, "Marko Ruppel", "2001-11-17"),
	(1117, "Emma Haack", "2000-2-1"),
	(1118, "Christin Brehmer", "1999-8-18"),
	(1119, "Kay Kind", "2000-12-7"),
	(1120, "Michaela Schröder", "1998-7-9"),
	(1121, "Matthias Eberle", "2000-11-9"),
	(1122, "Beatrice Pfeffer", "1999-2-20"),
	(1123, "Dominic Neumeister", "2001-8-25"),
	(1124, "Lennart Radloff", "2001-11-14"),
	(1125, "Katarina Scheerer", "2000-11-29"),
	(1126, "Jannik Bastian", "2001-2-27"),
	(1127, "Carla Manske", "1999-3-30"),
	(1128, "Jakob Krieg", "1999-8-14"),
	(1129, "Evelin Merk", "2001-12-15"),
	(1130, "Lars Michl", "2000-8-31"),
	(1131, "Joachim Alber", "2000-7-20"),
	(1132, "Jonathan Rohrbach", "1999-6-22"),
	(1133, "Norbert Stoiber", "2000-6-19"),
	(1134, "Christiane Pohlmann", "2000-5-29"),
	(1135, "Melanie Nolden", "1999-10-7"),
	(1136, "Tanja Neef", "2000-4-13"),
	(1137, "Curt Kirschner", "2000-11-15"),
	(1138, "Kai Nguyen", "1999-1-21"),
	(1139, "Barbara Knauer", "1999-6-16"),
	(1140, "Kai Görg", "2001-10-31"),
	(1141, "Jonas Bethge", "1999-1-22"),
	(1142, "Christiane Hammerschmidt", "2000-4-12"),
	(1143, "Dagmar Neubert", "2000-2-28"),
	(1144, "Kirsten Selzer", "2000-11-3"),
	(1145, "Patrizia Fechner", "2000-9-3"),
	(1146, "Uwe Steinhauser", "2000-4-25"),
	(1147, "René Focke", "2000-2-2"),
	(1148, "Emilie Lüdtke", "2000-9-18"),
	(1149, "Hannelore Döll", "1999-10-24"),
	(1150, "Helmuth Hagedorn", "1998-6-26"),
	(1151, "Dorothea Topp", "1999-5-16"),
	(1152, "Barbara Wüst", "1999-6-13"),
	(1153, "Fabian Seewald", "1999-7-5"),
	(1154, "Maik Grabowski", "2001-2-18"),
	(1155, "Regina Matt", "2000-5-31"),
	(1156, "Michael Heinicke", "2000-4-6"),
	(1157, "Mike Huck", "1998-7-20"),
	(1158, "Olaf Guse", "2000-8-6"),
	(1159, "Leon Weingärtner", "2001-10-31"),
	(1160, "Charlotte Abel", "2000-6-16"),
	(1161, "Anne Kurtz", "2000-2-23"),
	(1162, "Yannik Ehrich", "1999-7-20"),
	(1163, "Elfriede Heiser", "2000-6-13"),
	(1164, "Günter Dreier", "1999-12-13"),
	(1165, "Gerhard Bade", "1999-12-1"),
	(1166, "Oskar Thum", "2000-5-11"),
	(1167, "Mathias Fink", "1997-11-29"),
	(1168, "Moritz Weißenborn", "1999-1-10"),
	(1169, "Curt Zöller", "2000-9-20"),
	(1170, "Lina Süß", "2001-11-18"),
	(1171, "Ida Behm", "2000-5-5"),
	(1172, "Katja Alber", "2000-10-27"),
	(1173, "Kati Rosenbaum", "1999-10-16"),
	(1174, "Helena Meise", "2000-3-16"),
	(1175, "Arthur Althoff", "1999-8-15"),
	(1176, "Hugo Pfau", "1999-12-19"),
	(1177, "Justin Sasse", "1999-10-1"),
	(1178, "Maximilian Behrmann", "2000-11-19"),
	(1179, "Hertha Baumert", "2001-9-15"),
	(1180, "Harry Tolksdorf", "2000-12-25"),
	(1181, "Jörg Casper", "2001-11-25"),
	(1182, "Oscar Kiesewetter", "1999-1-27"),
	(1183, "Gert Willer", "1999-7-25"),
	(1184, "Minna Polster", "1999-11-8"),
	(1185, "Uwe Hinze", "1998-5-26"),
	(1186, "Cathleen Rößler", "2000-3-5"),
	(1187, "Ben Jäkel", "1999-3-5"),
	(1188, "Stephan Bähr", "1999-4-28"),
	(1189, "Dirk Dreher", "1999-9-22"),
	(1190, "Annett Merkel", "2000-6-4"),
	(1191, "Karlheinz Licht", "2001-2-14"),
	(1192, "Gabriele Benecke", "1999-5-22"),
	(1193, "Ernst Lüdecke", "2000-12-31"),
	(1194, "Niklas Bensch", "1999-11-28"),
	(1195, "August Heese", "1998-4-2"),
	(1196, "Felix Kühnert", "2000-7-1"),
	(1197, "Jörg Wall", "1999-8-17"),
	(1198, "Marvin Wiedenmann", "2000-7-28"),
	(1199, "Claus Baumer", "2001-12-4"),
	(1200, "Ulrich Stöckel", "2000-12-28"),
	(1201, "Jennifer Wilde", "1998-10-16"),
	(1202, "Volker Diekmann", "1999-9-4"),
	(1203, "Fabian Ehrmann", "1999-5-12"),
	(1204, "Louise Kuck", "2000-3-2"),
	(1205, "Dorothee Gaul", "1999-3-29"),
	(1206, "Diana Hennig", "2000-3-12"),
	(1207, "Anita Demir", "1998-7-16"),
	(1208, "Gertrud Kranz", "2000-9-1"),
	(1209, "Christel Wichert", "2000-12-16"),
	(1210, "Luisa Struck", "1999-9-11"),
	(1211, "Brigitte Kempf", "2001-3-13"),
	(1212, "Joseph Köllner", "1998-6-20"),
	(1213, "Mathias Arlt", "2000-2-10"),
	(1214, "Mareike Feil", "2000-2-24"),
	(1215, "Malgorzata Kohl", "2000-6-1"),
	(1216, "Hans Albrecht", "2000-2-15"),
	(1217, "Dagmar Hilmer", "2000-11-17"),
	(1218, "Uwe Kirsch", "1998-3-21"),
	(1219, "Erwin Schöner", "1999-4-12"),
	(1220, "August Baumer", "2000-9-27"),
	(1221, "Emilia Schimmel", "2001-7-31"),
	(1222, "Martina Heim", "2000-6-30"),
	(1223, "Karina Peschel", "2000-2-8"),
	(1224, "Kristian Hümmer", "1998-2-5"),
	(1225, "Tatjana Mezger", "2000-8-24"),
	(1226, "Luise Matthiesen", "1999-3-15"),
	(1227, "Joanna Lüdtke", "2000-10-15"),
	(1228, "Christoph Forster", "2001-7-13"),
	(1229, "Carina Fuhrmann", "1999-12-3"),
	(1230, "Anika Göllner", "1999-10-26"),
	(1231, "Tom Bendig", "1999-9-9"),
	(1232, "Erwin Möllers", "1999-12-27"),
	(1233, "Cathleen Franken", "1999-10-25"),
	(1234, "Felix Tischler", "2001-3-24"),
	(1235, "Gisela Kullmann", "2000-8-21"),
	(1236, "Helmut Eckhardt", "1999-10-21"),
	(1237, "Corinna Herzig", "2001-9-28"),
	(1238, "Moritz Steinhauer", "1999-9-29"),
	(1239, "Anneliese Schnieders", "2000-6-29"),
	(1240, "Niclas Reber", "2000-9-12"),
	(1241, "Jennifer Siegert", "1999-6-9"),
	(1242, "Ilona Große", "1999-9-2"),
	(1243, "Sigrid Ehrlich", "2000-10-20"),
	(1244, "Rolf Neubert", "1999-6-8"),
	(1245, "Helmuth Welker", "2001-8-5"),
	(1246, "Marcel Baer", "1999-10-1"),
	(1247, "Catrin Eilers", "2000-1-12"),
	(1248, "Heinz Gutmann", "2001-10-1"),
	(1249, "Richard Pfeil", "1999-4-22"),
	(1250, "Natalia Boy", "2001-2-14"),
	(1251, "Ralph Lüdemann", "2000-11-20"),
	(1252, "Margrit Busse", "2000-12-6"),
	(1253, "Justin Pietschmann", "1999-11-20"),
	(1254, "Kay Heinen", "2000-3-15"),
	(1255, "Carsten Unruh", "1999-3-18"),
	(1256, "Kathi Drewes", "2000-8-21"),
	(1257, "Günter Riedel", "2000-6-23"),
	(1258, "Yvonne Großkopf", "2000-1-18"),
	(1259, "Minna Wörner", "1998-12-17"),
	(1260, "Nick Schümann", "2000-6-2"),
	(1261, "Alfred Beller", "2000-1-7"),
	(1262, "Thorsten Reineke", "2001-8-28"),
	(1263, "Wera Brandl", "2001-10-10"),
	(1264, "Isabella Wolf", "1998-6-7"),
	(1265, "Andreas Krafft", "1999-1-11"),
	(1266, "Marco Wick", "2000-9-21"),
	(1267, "Josef Zapf", "2000-8-15"),
	(1268, "Emma Klassen", "2000-11-18"),
	(1269, "Olaf Kämpf", "1999-10-2"),
	(1270, "Larissa Klostermann", "1999-5-22"),
	(1271, "Eveline Plath", "2001-1-5"),
	(1272, "Emma Schorr", "1999-8-23"),
	(1273, "Wolfgang Kaluza", "2000-1-30"),
	(1274, "Sylwia Habermann", "2000-5-20"),
	(1275, "Hannah Wiens", "2000-4-30"),
	(1276, "Anika Schülke", "2000-8-17"),
	(1277, "Jürgen Ring", "2000-11-13"),
	(1278, "Bertha Hinrichsen", "2000-3-15"),
	(1279, "Auguste Bauer", "1999-9-13"),
	(1280, "Janin Brunke", "2000-4-25"),
	(1281, "Walter Hill", "1999-1-15"),
	(1282, "Joachim Golz", "2000-8-29"),
	(1283, "Meike Zitzmann", "1999-12-25"),
	(1284, "Yannick Hase", "1998-1-12"),
	(1285, "Benjamin Rüdiger", "2000-1-17"),
	(1286, "Manfred Marks", "2000-6-21"),
	(1287, "Karl Markert", "2000-4-23"),
	(1288, "Louise Paschke", "2000-5-24"),
	(1289, "Niclas Feiler", "1999-2-20"),
	(1290, "Sylke Hempel", "1998-3-28"),
	(1291, "Jeannette Gotthardt", "1999-9-26"),
	(1292, "Lucas Keck", "2000-7-6"),
	(1293, "Marta Selzer", "1997-12-31"),
	(1294, "Sarah Lichtenberg", "1998-6-4"),
	(1295, "Evelin Nelles", "2000-6-12"),
	(1296, "Corinna Heger", "2001-5-3"),
	(1297, "Sophia Stich", "2001-3-5"),
	(1298, "Mathias Neumüller", "1999-10-10"),
	(1299, "Oskar Menne", "1999-1-28"),
	(1300, "Ingeborg Pfennig", "2000-10-8");

INSERT INTO Klasse (klasse, klasse_jahr, klassenleiter) VALUES 
	("5a", 2016, "halo"), 
	("5b", 2016, "thre"), 
	("5c", 2016, "wizi"), 
	("5d", 2016, "bisc"), 
	("5e", 2016, "olho"), 
	("5f", 2016, "repo"), 
	("5g", 2016, "nioz"), 
	("5h", 2016, "jasc"), 
	("6a", 2016, "tobe"), 
	("6b", 2016, "embl"), 
	("6c", 2016, "pare"), 
	("6d", 2016, "klwe"), 
	("6e", 2016, "kari"), 
	("6f", 2016, "rasc"), 
	("6g", 2016, "sisc"), 
	("6h", 2016, "hail"), 
	("7a", 2016, "anhe"), 
	("7b", 2016, "wosp"), 
	("7c", 2016, "kaei"), 
	("7d", 2016, "haja"), 
	("7e", 2016, "kaga"), 
	("7f", 2016, "sigr"), 
	("7g", 2016, "hera"), 
	("7h", 2016, "daem"), 
	("8a", 2016, "syhe"), 
	("8b", 2016, "howe"), 
	("8c", 2016, "pasc"), 
	("8d", 2016, "maga"), 
	("8e", 2016, "jabl"), 
	("8f", 2016, "brse"), 
	("8g", 2016, "krbi"), 
	("8h", 2016, "hobo"), 
	("9a", 2016, "brso"), 
	("9b", 2016, "axme"), 
	("9c", 2016, "imsc"), 
	("9d", 2016, "heho"), 
	("9e", 2016, "embo"), 
	("9f", 2016, "faeb"), 
	("9g", 2016, "fiec"), 
	("9h", 2016, "masc"), 
	("10a", 2016, "jath"), 
	("10b", 2016, "noka"), 
	("10c", 2016, "joni"), 
	("10d", 2016, "kana"), 
	("10e", 2016, "bihe"), 
	("10f", 2016, "joba"), 
	("10g", 2016, "leku"), 
	("10h", 2016, "osma");

INSERT INTO Besucht (schuelerID, klasse, klasse_jahr) VALUES 
	(101, "5a", 2016), 
	(102, "5a", 2016), 
	(103, "5a", 2016), 
	(104, "5a", 2016), 
	(105, "5a", 2016), 
	(106, "5a", 2016), 
	(107, "5a", 2016), 
	(108, "5a", 2016), 
	(109, "5a", 2016), 
	(110, "5a", 2016), 
	(111, "5a", 2016), 
	(112, "5a", 2016), 
	(113, "5a", 2016), 
	(114, "5a", 2016), 
	(115, "5a", 2016), 
	(116, "5a", 2016), 
	(117, "5a", 2016), 
	(118, "5a", 2016), 
	(119, "5a", 2016), 
	(120, "5a", 2016), 
	(121, "5a", 2016), 
	(122, "5a", 2016), 
	(123, "5a", 2016), 
	(124, "5a", 2016), 
	(125, "5a", 2016), 
	(126, "5b", 2016), 
	(127, "5b", 2016), 
	(128, "5b", 2016), 
	(129, "5b", 2016), 
	(130, "5b", 2016), 
	(131, "5b", 2016), 
	(132, "5b", 2016), 
	(133, "5b", 2016), 
	(134, "5b", 2016), 
	(135, "5b", 2016), 
	(136, "5b", 2016), 
	(137, "5b", 2016), 
	(138, "5b", 2016), 
	(139, "5b", 2016), 
	(140, "5b", 2016), 
	(141, "5b", 2016), 
	(142, "5b", 2016), 
	(143, "5b", 2016), 
	(144, "5b", 2016), 
	(145, "5b", 2016), 
	(146, "5b", 2016), 
	(147, "5b", 2016), 
	(148, "5b", 2016), 
	(149, "5b", 2016), 
	(150, "5b", 2016), 
	(151, "5c", 2016), 
	(152, "5c", 2016), 
	(153, "5c", 2016), 
	(154, "5c", 2016), 
	(155, "5c", 2016), 
	(156, "5c", 2016), 
	(157, "5c", 2016), 
	(158, "5c", 2016), 
	(159, "5c", 2016), 
	(160, "5c", 2016), 
	(161, "5c", 2016), 
	(162, "5c", 2016), 
	(163, "5c", 2016), 
	(164, "5c", 2016), 
	(165, "5c", 2016), 
	(166, "5c", 2016), 
	(167, "5c", 2016), 
	(168, "5c", 2016), 
	(169, "5c", 2016), 
	(170, "5c", 2016), 
	(171, "5c", 2016), 
	(172, "5c", 2016), 
	(173, "5c", 2016), 
	(174, "5c", 2016), 
	(175, "5c", 2016), 
	(176, "5d", 2016), 
	(177, "5d", 2016), 
	(178, "5d", 2016), 
	(179, "5d", 2016), 
	(180, "5d", 2016), 
	(181, "5d", 2016), 
	(182, "5d", 2016), 
	(183, "5d", 2016), 
	(184, "5d", 2016), 
	(185, "5d", 2016), 
	(186, "5d", 2016), 
	(187, "5d", 2016), 
	(188, "5d", 2016), 
	(189, "5d", 2016), 
	(190, "5d", 2016), 
	(191, "5d", 2016), 
	(192, "5d", 2016), 
	(193, "5d", 2016), 
	(194, "5d", 2016), 
	(195, "5d", 2016), 
	(196, "5d", 2016), 
	(197, "5d", 2016), 
	(198, "5d", 2016), 
	(199, "5d", 2016), 
	(200, "5d", 2016), 
	(201, "5e", 2016), 
	(202, "5e", 2016), 
	(203, "5e", 2016), 
	(204, "5e", 2016), 
	(205, "5e", 2016), 
	(206, "5e", 2016), 
	(207, "5e", 2016), 
	(208, "5e", 2016), 
	(209, "5e", 2016), 
	(210, "5e", 2016), 
	(211, "5e", 2016), 
	(212, "5e", 2016), 
	(213, "5e", 2016), 
	(214, "5e", 2016), 
	(215, "5e", 2016), 
	(216, "5e", 2016), 
	(217, "5e", 2016), 
	(218, "5e", 2016), 
	(219, "5e", 2016), 
	(220, "5e", 2016), 
	(221, "5e", 2016), 
	(222, "5e", 2016), 
	(223, "5e", 2016), 
	(224, "5e", 2016), 
	(225, "5e", 2016), 
	(226, "5f", 2016), 
	(227, "5f", 2016), 
	(228, "5f", 2016), 
	(229, "5f", 2016), 
	(230, "5f", 2016), 
	(231, "5f", 2016), 
	(232, "5f", 2016), 
	(233, "5f", 2016), 
	(234, "5f", 2016), 
	(235, "5f", 2016), 
	(236, "5f", 2016), 
	(237, "5f", 2016), 
	(238, "5f", 2016), 
	(239, "5f", 2016), 
	(240, "5f", 2016), 
	(241, "5f", 2016), 
	(242, "5f", 2016), 
	(243, "5f", 2016), 
	(244, "5f", 2016), 
	(245, "5f", 2016), 
	(246, "5f", 2016), 
	(247, "5f", 2016), 
	(248, "5f", 2016), 
	(249, "5f", 2016), 
	(250, "5f", 2016), 
	(251, "5g", 2016), 
	(252, "5g", 2016), 
	(253, "5g", 2016), 
	(254, "5g", 2016), 
	(255, "5g", 2016), 
	(256, "5g", 2016), 
	(257, "5g", 2016), 
	(258, "5g", 2016), 
	(259, "5g", 2016), 
	(260, "5g", 2016), 
	(261, "5g", 2016), 
	(262, "5g", 2016), 
	(263, "5g", 2016), 
	(264, "5g", 2016), 
	(265, "5g", 2016), 
	(266, "5g", 2016), 
	(267, "5g", 2016), 
	(268, "5g", 2016), 
	(269, "5g", 2016), 
	(270, "5g", 2016), 
	(271, "5g", 2016), 
	(272, "5g", 2016), 
	(273, "5g", 2016), 
	(274, "5g", 2016), 
	(275, "5g", 2016), 
	(276, "5h", 2016), 
	(277, "5h", 2016), 
	(278, "5h", 2016), 
	(279, "5h", 2016), 
	(280, "5h", 2016), 
	(281, "5h", 2016), 
	(282, "5h", 2016), 
	(283, "5h", 2016), 
	(284, "5h", 2016), 
	(285, "5h", 2016), 
	(286, "5h", 2016), 
	(287, "5h", 2016), 
	(288, "5h", 2016), 
	(289, "5h", 2016), 
	(290, "5h", 2016), 
	(291, "5h", 2016), 
	(292, "5h", 2016), 
	(293, "5h", 2016), 
	(294, "5h", 2016), 
	(295, "5h", 2016), 
	(296, "5h", 2016), 
	(297, "5h", 2016), 
	(298, "5h", 2016), 
	(299, "5h", 2016), 
	(300, "5h", 2016), 
	(301, "6a", 2016), 
	(302, "6a", 2016), 
	(303, "6a", 2016), 
	(304, "6a", 2016), 
	(305, "6a", 2016), 
	(306, "6a", 2016), 
	(307, "6a", 2016), 
	(308, "6a", 2016), 
	(309, "6a", 2016), 
	(310, "6a", 2016), 
	(311, "6a", 2016), 
	(312, "6a", 2016), 
	(313, "6a", 2016), 
	(314, "6a", 2016), 
	(315, "6a", 2016), 
	(316, "6a", 2016), 
	(317, "6a", 2016), 
	(318, "6a", 2016), 
	(319, "6a", 2016), 
	(320, "6a", 2016), 
	(321, "6a", 2016), 
	(322, "6a", 2016), 
	(323, "6a", 2016), 
	(324, "6a", 2016), 
	(325, "6a", 2016), 
	(326, "6b", 2016), 
	(327, "6b", 2016), 
	(328, "6b", 2016), 
	(329, "6b", 2016), 
	(330, "6b", 2016), 
	(331, "6b", 2016), 
	(332, "6b", 2016), 
	(333, "6b", 2016), 
	(334, "6b", 2016), 
	(335, "6b", 2016), 
	(336, "6b", 2016), 
	(337, "6b", 2016), 
	(338, "6b", 2016), 
	(339, "6b", 2016), 
	(340, "6b", 2016), 
	(341, "6b", 2016), 
	(342, "6b", 2016), 
	(343, "6b", 2016), 
	(344, "6b", 2016), 
	(345, "6b", 2016), 
	(346, "6b", 2016), 
	(347, "6b", 2016), 
	(348, "6b", 2016), 
	(349, "6b", 2016), 
	(350, "6b", 2016), 
	(351, "6c", 2016), 
	(352, "6c", 2016), 
	(353, "6c", 2016), 
	(354, "6c", 2016), 
	(355, "6c", 2016), 
	(356, "6c", 2016), 
	(357, "6c", 2016), 
	(358, "6c", 2016), 
	(359, "6c", 2016), 
	(360, "6c", 2016), 
	(361, "6c", 2016), 
	(362, "6c", 2016), 
	(363, "6c", 2016), 
	(364, "6c", 2016), 
	(365, "6c", 2016), 
	(366, "6c", 2016), 
	(367, "6c", 2016), 
	(368, "6c", 2016), 
	(369, "6c", 2016), 
	(370, "6c", 2016), 
	(371, "6c", 2016), 
	(372, "6c", 2016), 
	(373, "6c", 2016), 
	(374, "6c", 2016), 
	(375, "6c", 2016), 
	(376, "6d", 2016), 
	(377, "6d", 2016), 
	(378, "6d", 2016), 
	(379, "6d", 2016), 
	(380, "6d", 2016), 
	(381, "6d", 2016), 
	(382, "6d", 2016), 
	(383, "6d", 2016), 
	(384, "6d", 2016), 
	(385, "6d", 2016), 
	(386, "6d", 2016), 
	(387, "6d", 2016), 
	(388, "6d", 2016), 
	(389, "6d", 2016), 
	(390, "6d", 2016), 
	(391, "6d", 2016), 
	(392, "6d", 2016), 
	(393, "6d", 2016), 
	(394, "6d", 2016), 
	(395, "6d", 2016), 
	(396, "6d", 2016), 
	(397, "6d", 2016), 
	(398, "6d", 2016), 
	(399, "6d", 2016), 
	(400, "6d", 2016), 
	(401, "6e", 2016), 
	(402, "6e", 2016), 
	(403, "6e", 2016), 
	(404, "6e", 2016), 
	(405, "6e", 2016), 
	(406, "6e", 2016), 
	(407, "6e", 2016), 
	(408, "6e", 2016), 
	(409, "6e", 2016), 
	(410, "6e", 2016), 
	(411, "6e", 2016), 
	(412, "6e", 2016), 
	(413, "6e", 2016), 
	(414, "6e", 2016), 
	(415, "6e", 2016), 
	(416, "6e", 2016), 
	(417, "6e", 2016), 
	(418, "6e", 2016), 
	(419, "6e", 2016), 
	(420, "6e", 2016), 
	(421, "6e", 2016), 
	(422, "6e", 2016), 
	(423, "6e", 2016), 
	(424, "6e", 2016), 
	(425, "6e", 2016), 
	(426, "6f", 2016), 
	(427, "6f", 2016), 
	(428, "6f", 2016), 
	(429, "6f", 2016), 
	(430, "6f", 2016), 
	(431, "6f", 2016), 
	(432, "6f", 2016), 
	(433, "6f", 2016), 
	(434, "6f", 2016), 
	(435, "6f", 2016), 
	(436, "6f", 2016), 
	(437, "6f", 2016), 
	(438, "6f", 2016), 
	(439, "6f", 2016), 
	(440, "6f", 2016), 
	(441, "6f", 2016), 
	(442, "6f", 2016), 
	(443, "6f", 2016), 
	(444, "6f", 2016), 
	(445, "6f", 2016), 
	(446, "6f", 2016), 
	(447, "6f", 2016), 
	(448, "6f", 2016), 
	(449, "6f", 2016), 
	(450, "6f", 2016), 
	(451, "6g", 2016), 
	(452, "6g", 2016), 
	(453, "6g", 2016), 
	(454, "6g", 2016), 
	(455, "6g", 2016), 
	(456, "6g", 2016), 
	(457, "6g", 2016), 
	(458, "6g", 2016), 
	(459, "6g", 2016), 
	(460, "6g", 2016), 
	(461, "6g", 2016), 
	(462, "6g", 2016), 
	(463, "6g", 2016), 
	(464, "6g", 2016), 
	(465, "6g", 2016), 
	(466, "6g", 2016), 
	(467, "6g", 2016), 
	(468, "6g", 2016), 
	(469, "6g", 2016), 
	(470, "6g", 2016), 
	(471, "6g", 2016), 
	(472, "6g", 2016), 
	(473, "6g", 2016), 
	(474, "6g", 2016), 
	(475, "6g", 2016), 
	(476, "6h", 2016), 
	(477, "6h", 2016), 
	(478, "6h", 2016), 
	(479, "6h", 2016), 
	(480, "6h", 2016), 
	(481, "6h", 2016), 
	(482, "6h", 2016), 
	(483, "6h", 2016), 
	(484, "6h", 2016), 
	(485, "6h", 2016), 
	(486, "6h", 2016), 
	(487, "6h", 2016), 
	(488, "6h", 2016), 
	(489, "6h", 2016), 
	(490, "6h", 2016), 
	(491, "6h", 2016), 
	(492, "6h", 2016), 
	(493, "6h", 2016), 
	(494, "6h", 2016), 
	(495, "6h", 2016), 
	(496, "6h", 2016), 
	(497, "6h", 2016), 
	(498, "6h", 2016), 
	(499, "6h", 2016), 
	(500, "6h", 2016), 
	(501, "7a", 2016), 
	(502, "7a", 2016), 
	(503, "7a", 2016), 
	(504, "7a", 2016), 
	(505, "7a", 2016), 
	(506, "7a", 2016), 
	(507, "7a", 2016), 
	(508, "7a", 2016), 
	(509, "7a", 2016), 
	(510, "7a", 2016), 
	(511, "7a", 2016), 
	(512, "7a", 2016), 
	(513, "7a", 2016), 
	(514, "7a", 2016), 
	(515, "7a", 2016), 
	(516, "7a", 2016), 
	(517, "7a", 2016), 
	(518, "7a", 2016), 
	(519, "7a", 2016), 
	(520, "7a", 2016), 
	(521, "7a", 2016), 
	(522, "7a", 2016), 
	(523, "7a", 2016), 
	(524, "7a", 2016), 
	(525, "7a", 2016), 
	(526, "7b", 2016), 
	(527, "7b", 2016), 
	(528, "7b", 2016), 
	(529, "7b", 2016), 
	(530, "7b", 2016), 
	(531, "7b", 2016), 
	(532, "7b", 2016), 
	(533, "7b", 2016), 
	(534, "7b", 2016), 
	(535, "7b", 2016), 
	(536, "7b", 2016), 
	(537, "7b", 2016), 
	(538, "7b", 2016), 
	(539, "7b", 2016), 
	(540, "7b", 2016), 
	(541, "7b", 2016), 
	(542, "7b", 2016), 
	(543, "7b", 2016), 
	(544, "7b", 2016), 
	(545, "7b", 2016), 
	(546, "7b", 2016), 
	(547, "7b", 2016), 
	(548, "7b", 2016), 
	(549, "7b", 2016), 
	(550, "7b", 2016), 
	(551, "7c", 2016), 
	(552, "7c", 2016), 
	(553, "7c", 2016), 
	(554, "7c", 2016), 
	(555, "7c", 2016), 
	(556, "7c", 2016), 
	(557, "7c", 2016), 
	(558, "7c", 2016), 
	(559, "7c", 2016), 
	(560, "7c", 2016), 
	(561, "7c", 2016), 
	(562, "7c", 2016), 
	(563, "7c", 2016), 
	(564, "7c", 2016), 
	(565, "7c", 2016), 
	(566, "7c", 2016), 
	(567, "7c", 2016), 
	(568, "7c", 2016), 
	(569, "7c", 2016), 
	(570, "7c", 2016), 
	(571, "7c", 2016), 
	(572, "7c", 2016), 
	(573, "7c", 2016), 
	(574, "7c", 2016), 
	(575, "7c", 2016), 
	(576, "7d", 2016), 
	(577, "7d", 2016), 
	(578, "7d", 2016), 
	(579, "7d", 2016), 
	(580, "7d", 2016), 
	(581, "7d", 2016), 
	(582, "7d", 2016), 
	(583, "7d", 2016), 
	(584, "7d", 2016), 
	(585, "7d", 2016), 
	(586, "7d", 2016), 
	(587, "7d", 2016), 
	(588, "7d", 2016), 
	(589, "7d", 2016), 
	(590, "7d", 2016), 
	(591, "7d", 2016), 
	(592, "7d", 2016), 
	(593, "7d", 2016), 
	(594, "7d", 2016), 
	(595, "7d", 2016), 
	(596, "7d", 2016), 
	(597, "7d", 2016), 
	(598, "7d", 2016), 
	(599, "7d", 2016), 
	(600, "7d", 2016), 
	(601, "7e", 2016), 
	(602, "7e", 2016), 
	(603, "7e", 2016), 
	(604, "7e", 2016), 
	(605, "7e", 2016), 
	(606, "7e", 2016), 
	(607, "7e", 2016), 
	(608, "7e", 2016), 
	(609, "7e", 2016), 
	(610, "7e", 2016), 
	(611, "7e", 2016), 
	(612, "7e", 2016), 
	(613, "7e", 2016), 
	(614, "7e", 2016), 
	(615, "7e", 2016), 
	(616, "7e", 2016), 
	(617, "7e", 2016), 
	(618, "7e", 2016), 
	(619, "7e", 2016), 
	(620, "7e", 2016), 
	(621, "7e", 2016), 
	(622, "7e", 2016), 
	(623, "7e", 2016), 
	(624, "7e", 2016), 
	(625, "7e", 2016), 
	(626, "7f", 2016), 
	(627, "7f", 2016), 
	(628, "7f", 2016), 
	(629, "7f", 2016), 
	(630, "7f", 2016), 
	(631, "7f", 2016), 
	(632, "7f", 2016), 
	(633, "7f", 2016), 
	(634, "7f", 2016), 
	(635, "7f", 2016), 
	(636, "7f", 2016), 
	(637, "7f", 2016), 
	(638, "7f", 2016), 
	(639, "7f", 2016), 
	(640, "7f", 2016), 
	(641, "7f", 2016), 
	(642, "7f", 2016), 
	(643, "7f", 2016), 
	(644, "7f", 2016), 
	(645, "7f", 2016), 
	(646, "7f", 2016), 
	(647, "7f", 2016), 
	(648, "7f", 2016), 
	(649, "7f", 2016), 
	(650, "7f", 2016), 
	(651, "7g", 2016), 
	(652, "7g", 2016), 
	(653, "7g", 2016), 
	(654, "7g", 2016), 
	(655, "7g", 2016), 
	(656, "7g", 2016), 
	(657, "7g", 2016), 
	(658, "7g", 2016), 
	(659, "7g", 2016), 
	(660, "7g", 2016), 
	(661, "7g", 2016), 
	(662, "7g", 2016), 
	(663, "7g", 2016), 
	(664, "7g", 2016), 
	(665, "7g", 2016), 
	(666, "7g", 2016), 
	(667, "7g", 2016), 
	(668, "7g", 2016), 
	(669, "7g", 2016), 
	(670, "7g", 2016), 
	(671, "7g", 2016), 
	(672, "7g", 2016), 
	(673, "7g", 2016), 
	(674, "7g", 2016), 
	(675, "7g", 2016), 
	(676, "7h", 2016), 
	(677, "7h", 2016), 
	(678, "7h", 2016), 
	(679, "7h", 2016), 
	(680, "7h", 2016), 
	(681, "7h", 2016), 
	(682, "7h", 2016), 
	(683, "7h", 2016), 
	(684, "7h", 2016), 
	(685, "7h", 2016), 
	(686, "7h", 2016), 
	(687, "7h", 2016), 
	(688, "7h", 2016), 
	(689, "7h", 2016), 
	(690, "7h", 2016), 
	(691, "7h", 2016), 
	(692, "7h", 2016), 
	(693, "7h", 2016), 
	(694, "7h", 2016), 
	(695, "7h", 2016), 
	(696, "7h", 2016), 
	(697, "7h", 2016), 
	(698, "7h", 2016), 
	(699, "7h", 2016), 
	(700, "7h", 2016), 
	(701, "8a", 2016), 
	(702, "8a", 2016), 
	(703, "8a", 2016), 
	(704, "8a", 2016), 
	(705, "8a", 2016), 
	(706, "8a", 2016), 
	(707, "8a", 2016), 
	(708, "8a", 2016), 
	(709, "8a", 2016), 
	(710, "8a", 2016), 
	(711, "8a", 2016), 
	(712, "8a", 2016), 
	(713, "8a", 2016), 
	(714, "8a", 2016), 
	(715, "8a", 2016), 
	(716, "8a", 2016), 
	(717, "8a", 2016), 
	(718, "8a", 2016), 
	(719, "8a", 2016), 
	(720, "8a", 2016), 
	(721, "8a", 2016), 
	(722, "8a", 2016), 
	(723, "8a", 2016), 
	(724, "8a", 2016), 
	(725, "8a", 2016), 
	(726, "8b", 2016), 
	(727, "8b", 2016), 
	(728, "8b", 2016), 
	(729, "8b", 2016), 
	(730, "8b", 2016), 
	(731, "8b", 2016), 
	(732, "8b", 2016), 
	(733, "8b", 2016), 
	(734, "8b", 2016), 
	(735, "8b", 2016), 
	(736, "8b", 2016), 
	(737, "8b", 2016), 
	(738, "8b", 2016), 
	(739, "8b", 2016), 
	(740, "8b", 2016), 
	(741, "8b", 2016), 
	(742, "8b", 2016), 
	(743, "8b", 2016), 
	(744, "8b", 2016), 
	(745, "8b", 2016), 
	(746, "8b", 2016), 
	(747, "8b", 2016), 
	(748, "8b", 2016), 
	(749, "8b", 2016), 
	(750, "8b", 2016), 
	(751, "8c", 2016), 
	(752, "8c", 2016), 
	(753, "8c", 2016), 
	(754, "8c", 2016), 
	(755, "8c", 2016), 
	(756, "8c", 2016), 
	(757, "8c", 2016), 
	(758, "8c", 2016), 
	(759, "8c", 2016), 
	(760, "8c", 2016), 
	(761, "8c", 2016), 
	(762, "8c", 2016), 
	(763, "8c", 2016), 
	(764, "8c", 2016), 
	(765, "8c", 2016), 
	(766, "8c", 2016), 
	(767, "8c", 2016), 
	(768, "8c", 2016), 
	(769, "8c", 2016), 
	(770, "8c", 2016), 
	(771, "8c", 2016), 
	(772, "8c", 2016), 
	(773, "8c", 2016), 
	(774, "8c", 2016), 
	(775, "8c", 2016), 
	(776, "8d", 2016), 
	(777, "8d", 2016), 
	(778, "8d", 2016), 
	(779, "8d", 2016), 
	(780, "8d", 2016), 
	(781, "8d", 2016), 
	(782, "8d", 2016), 
	(783, "8d", 2016), 
	(784, "8d", 2016), 
	(785, "8d", 2016), 
	(786, "8d", 2016), 
	(787, "8d", 2016), 
	(788, "8d", 2016), 
	(789, "8d", 2016), 
	(790, "8d", 2016), 
	(791, "8d", 2016), 
	(792, "8d", 2016), 
	(793, "8d", 2016), 
	(794, "8d", 2016), 
	(795, "8d", 2016), 
	(796, "8d", 2016), 
	(797, "8d", 2016), 
	(798, "8d", 2016), 
	(799, "8d", 2016), 
	(800, "8d", 2016), 
	(801, "8e", 2016), 
	(802, "8e", 2016), 
	(803, "8e", 2016), 
	(804, "8e", 2016), 
	(805, "8e", 2016), 
	(806, "8e", 2016), 
	(807, "8e", 2016), 
	(808, "8e", 2016), 
	(809, "8e", 2016), 
	(810, "8e", 2016), 
	(811, "8e", 2016), 
	(812, "8e", 2016), 
	(813, "8e", 2016), 
	(814, "8e", 2016), 
	(815, "8e", 2016), 
	(816, "8e", 2016), 
	(817, "8e", 2016), 
	(818, "8e", 2016), 
	(819, "8e", 2016), 
	(820, "8e", 2016), 
	(821, "8e", 2016), 
	(822, "8e", 2016), 
	(823, "8e", 2016), 
	(824, "8e", 2016), 
	(825, "8e", 2016), 
	(826, "8f", 2016), 
	(827, "8f", 2016), 
	(828, "8f", 2016), 
	(829, "8f", 2016), 
	(830, "8f", 2016), 
	(831, "8f", 2016), 
	(832, "8f", 2016), 
	(833, "8f", 2016), 
	(834, "8f", 2016), 
	(835, "8f", 2016), 
	(836, "8f", 2016), 
	(837, "8f", 2016), 
	(838, "8f", 2016), 
	(839, "8f", 2016), 
	(840, "8f", 2016), 
	(841, "8f", 2016), 
	(842, "8f", 2016), 
	(843, "8f", 2016), 
	(844, "8f", 2016), 
	(845, "8f", 2016), 
	(846, "8f", 2016), 
	(847, "8f", 2016), 
	(848, "8f", 2016), 
	(849, "8f", 2016), 
	(850, "8f", 2016), 
	(851, "8g", 2016), 
	(852, "8g", 2016), 
	(853, "8g", 2016), 
	(854, "8g", 2016), 
	(855, "8g", 2016), 
	(856, "8g", 2016), 
	(857, "8g", 2016), 
	(858, "8g", 2016), 
	(859, "8g", 2016), 
	(860, "8g", 2016), 
	(861, "8g", 2016), 
	(862, "8g", 2016), 
	(863, "8g", 2016), 
	(864, "8g", 2016), 
	(865, "8g", 2016), 
	(866, "8g", 2016), 
	(867, "8g", 2016), 
	(868, "8g", 2016), 
	(869, "8g", 2016), 
	(870, "8g", 2016), 
	(871, "8g", 2016), 
	(872, "8g", 2016), 
	(873, "8g", 2016), 
	(874, "8g", 2016), 
	(875, "8g", 2016), 
	(876, "8h", 2016), 
	(877, "8h", 2016), 
	(878, "8h", 2016), 
	(879, "8h", 2016), 
	(880, "8h", 2016), 
	(881, "8h", 2016), 
	(882, "8h", 2016), 
	(883, "8h", 2016), 
	(884, "8h", 2016), 
	(885, "8h", 2016), 
	(886, "8h", 2016), 
	(887, "8h", 2016), 
	(888, "8h", 2016), 
	(889, "8h", 2016), 
	(890, "8h", 2016), 
	(891, "8h", 2016), 
	(892, "8h", 2016), 
	(893, "8h", 2016), 
	(894, "8h", 2016), 
	(895, "8h", 2016), 
	(896, "8h", 2016), 
	(897, "8h", 2016), 
	(898, "8h", 2016), 
	(899, "8h", 2016), 
	(900, "8h", 2016), 
	(901, "9a", 2016), 
	(902, "9a", 2016), 
	(903, "9a", 2016), 
	(904, "9a", 2016), 
	(905, "9a", 2016), 
	(906, "9a", 2016), 
	(907, "9a", 2016), 
	(908, "9a", 2016), 
	(909, "9a", 2016), 
	(910, "9a", 2016), 
	(911, "9a", 2016), 
	(912, "9a", 2016), 
	(913, "9a", 2016), 
	(914, "9a", 2016), 
	(915, "9a", 2016), 
	(916, "9a", 2016), 
	(917, "9a", 2016), 
	(918, "9a", 2016), 
	(919, "9a", 2016), 
	(920, "9a", 2016), 
	(921, "9a", 2016), 
	(922, "9a", 2016), 
	(923, "9a", 2016), 
	(924, "9a", 2016), 
	(925, "9a", 2016), 
	(926, "9b", 2016), 
	(927, "9b", 2016), 
	(928, "9b", 2016), 
	(929, "9b", 2016), 
	(930, "9b", 2016), 
	(931, "9b", 2016), 
	(932, "9b", 2016), 
	(933, "9b", 2016), 
	(934, "9b", 2016), 
	(935, "9b", 2016), 
	(936, "9b", 2016), 
	(937, "9b", 2016), 
	(938, "9b", 2016), 
	(939, "9b", 2016), 
	(940, "9b", 2016), 
	(941, "9b", 2016), 
	(942, "9b", 2016), 
	(943, "9b", 2016), 
	(944, "9b", 2016), 
	(945, "9b", 2016), 
	(946, "9b", 2016), 
	(947, "9b", 2016), 
	(948, "9b", 2016), 
	(949, "9b", 2016), 
	(950, "9b", 2016), 
	(951, "9c", 2016), 
	(952, "9c", 2016), 
	(953, "9c", 2016), 
	(954, "9c", 2016), 
	(955, "9c", 2016), 
	(956, "9c", 2016), 
	(957, "9c", 2016), 
	(958, "9c", 2016), 
	(959, "9c", 2016), 
	(960, "9c", 2016), 
	(961, "9c", 2016), 
	(962, "9c", 2016), 
	(963, "9c", 2016), 
	(964, "9c", 2016), 
	(965, "9c", 2016), 
	(966, "9c", 2016), 
	(967, "9c", 2016), 
	(968, "9c", 2016), 
	(969, "9c", 2016), 
	(970, "9c", 2016), 
	(971, "9c", 2016), 
	(972, "9c", 2016), 
	(973, "9c", 2016), 
	(974, "9c", 2016), 
	(975, "9c", 2016), 
	(976, "9d", 2016), 
	(977, "9d", 2016), 
	(978, "9d", 2016), 
	(979, "9d", 2016), 
	(980, "9d", 2016), 
	(981, "9d", 2016), 
	(982, "9d", 2016), 
	(983, "9d", 2016), 
	(984, "9d", 2016), 
	(985, "9d", 2016), 
	(986, "9d", 2016), 
	(987, "9d", 2016), 
	(988, "9d", 2016), 
	(989, "9d", 2016), 
	(990, "9d", 2016), 
	(991, "9d", 2016), 
	(992, "9d", 2016), 
	(993, "9d", 2016), 
	(994, "9d", 2016), 
	(995, "9d", 2016), 
	(996, "9d", 2016), 
	(997, "9d", 2016), 
	(998, "9d", 2016), 
	(999, "9d", 2016), 
	(1000, "9d", 2016), 
	(1001, "9e", 2016), 
	(1002, "9e", 2016), 
	(1003, "9e", 2016), 
	(1004, "9e", 2016), 
	(1005, "9e", 2016), 
	(1006, "9e", 2016), 
	(1007, "9e", 2016), 
	(1008, "9e", 2016), 
	(1009, "9e", 2016), 
	(1010, "9e", 2016), 
	(1011, "9e", 2016), 
	(1012, "9e", 2016), 
	(1013, "9e", 2016), 
	(1014, "9e", 2016), 
	(1015, "9e", 2016), 
	(1016, "9e", 2016), 
	(1017, "9e", 2016), 
	(1018, "9e", 2016), 
	(1019, "9e", 2016), 
	(1020, "9e", 2016), 
	(1021, "9e", 2016), 
	(1022, "9e", 2016), 
	(1023, "9e", 2016), 
	(1024, "9e", 2016), 
	(1025, "9e", 2016), 
	(1026, "9f", 2016), 
	(1027, "9f", 2016), 
	(1028, "9f", 2016), 
	(1029, "9f", 2016), 
	(1030, "9f", 2016), 
	(1031, "9f", 2016), 
	(1032, "9f", 2016), 
	(1033, "9f", 2016), 
	(1034, "9f", 2016), 
	(1035, "9f", 2016), 
	(1036, "9f", 2016), 
	(1037, "9f", 2016), 
	(1038, "9f", 2016), 
	(1039, "9f", 2016), 
	(1040, "9f", 2016), 
	(1041, "9f", 2016), 
	(1042, "9f", 2016), 
	(1043, "9f", 2016), 
	(1044, "9f", 2016), 
	(1045, "9f", 2016), 
	(1046, "9f", 2016), 
	(1047, "9f", 2016), 
	(1048, "9f", 2016), 
	(1049, "9f", 2016), 
	(1050, "9f", 2016), 
	(1051, "9g", 2016), 
	(1052, "9g", 2016), 
	(1053, "9g", 2016), 
	(1054, "9g", 2016), 
	(1055, "9g", 2016), 
	(1056, "9g", 2016), 
	(1057, "9g", 2016), 
	(1058, "9g", 2016), 
	(1059, "9g", 2016), 
	(1060, "9g", 2016), 
	(1061, "9g", 2016), 
	(1062, "9g", 2016), 
	(1063, "9g", 2016), 
	(1064, "9g", 2016), 
	(1065, "9g", 2016), 
	(1066, "9g", 2016), 
	(1067, "9g", 2016), 
	(1068, "9g", 2016), 
	(1069, "9g", 2016), 
	(1070, "9g", 2016), 
	(1071, "9g", 2016), 
	(1072, "9g", 2016), 
	(1073, "9g", 2016), 
	(1074, "9g", 2016), 
	(1075, "9g", 2016), 
	(1076, "9h", 2016), 
	(1077, "9h", 2016), 
	(1078, "9h", 2016), 
	(1079, "9h", 2016), 
	(1080, "9h", 2016), 
	(1081, "9h", 2016), 
	(1082, "9h", 2016), 
	(1083, "9h", 2016), 
	(1084, "9h", 2016), 
	(1085, "9h", 2016), 
	(1086, "9h", 2016), 
	(1087, "9h", 2016), 
	(1088, "9h", 2016), 
	(1089, "9h", 2016), 
	(1090, "9h", 2016), 
	(1091, "9h", 2016), 
	(1092, "9h", 2016), 
	(1093, "9h", 2016), 
	(1094, "9h", 2016), 
	(1095, "9h", 2016), 
	(1096, "9h", 2016), 
	(1097, "9h", 2016), 
	(1098, "9h", 2016), 
	(1099, "9h", 2016), 
	(1100, "9h", 2016), 
	(1101, "10a", 2016), 
	(1102, "10a", 2016), 
	(1103, "10a", 2016), 
	(1104, "10a", 2016), 
	(1105, "10a", 2016), 
	(1106, "10a", 2016), 
	(1107, "10a", 2016), 
	(1108, "10a", 2016), 
	(1109, "10a", 2016), 
	(1110, "10a", 2016), 
	(1111, "10a", 2016), 
	(1112, "10a", 2016), 
	(1113, "10a", 2016), 
	(1114, "10a", 2016), 
	(1115, "10a", 2016), 
	(1116, "10a", 2016), 
	(1117, "10a", 2016), 
	(1118, "10a", 2016), 
	(1119, "10a", 2016), 
	(1120, "10a", 2016), 
	(1121, "10a", 2016), 
	(1122, "10a", 2016), 
	(1123, "10a", 2016), 
	(1124, "10a", 2016), 
	(1125, "10a", 2016), 
	(1126, "10b", 2016), 
	(1127, "10b", 2016), 
	(1128, "10b", 2016), 
	(1129, "10b", 2016), 
	(1130, "10b", 2016), 
	(1131, "10b", 2016), 
	(1132, "10b", 2016), 
	(1133, "10b", 2016), 
	(1134, "10b", 2016), 
	(1135, "10b", 2016), 
	(1136, "10b", 2016), 
	(1137, "10b", 2016), 
	(1138, "10b", 2016), 
	(1139, "10b", 2016), 
	(1140, "10b", 2016), 
	(1141, "10b", 2016), 
	(1142, "10b", 2016), 
	(1143, "10b", 2016), 
	(1144, "10b", 2016), 
	(1145, "10b", 2016), 
	(1146, "10b", 2016), 
	(1147, "10b", 2016), 
	(1148, "10b", 2016), 
	(1149, "10b", 2016), 
	(1150, "10b", 2016), 
	(1151, "10c", 2016), 
	(1152, "10c", 2016), 
	(1153, "10c", 2016), 
	(1154, "10c", 2016), 
	(1155, "10c", 2016), 
	(1156, "10c", 2016), 
	(1157, "10c", 2016), 
	(1158, "10c", 2016), 
	(1159, "10c", 2016), 
	(1160, "10c", 2016), 
	(1161, "10c", 2016), 
	(1162, "10c", 2016), 
	(1163, "10c", 2016), 
	(1164, "10c", 2016), 
	(1165, "10c", 2016), 
	(1166, "10c", 2016), 
	(1167, "10c", 2016), 
	(1168, "10c", 2016), 
	(1169, "10c", 2016), 
	(1170, "10c", 2016), 
	(1171, "10c", 2016), 
	(1172, "10c", 2016), 
	(1173, "10c", 2016), 
	(1174, "10c", 2016), 
	(1175, "10c", 2016), 
	(1176, "10d", 2016), 
	(1177, "10d", 2016), 
	(1178, "10d", 2016), 
	(1179, "10d", 2016), 
	(1180, "10d", 2016), 
	(1181, "10d", 2016), 
	(1182, "10d", 2016), 
	(1183, "10d", 2016), 
	(1184, "10d", 2016), 
	(1185, "10d", 2016), 
	(1186, "10d", 2016), 
	(1187, "10d", 2016), 
	(1188, "10d", 2016), 
	(1189, "10d", 2016), 
	(1190, "10d", 2016), 
	(1191, "10d", 2016), 
	(1192, "10d", 2016), 
	(1193, "10d", 2016), 
	(1194, "10d", 2016), 
	(1195, "10d", 2016), 
	(1196, "10d", 2016), 
	(1197, "10d", 2016), 
	(1198, "10d", 2016), 
	(1199, "10d", 2016), 
	(1200, "10d", 2016), 
	(1201, "10e", 2016), 
	(1202, "10e", 2016), 
	(1203, "10e", 2016), 
	(1204, "10e", 2016), 
	(1205, "10e", 2016), 
	(1206, "10e", 2016), 
	(1207, "10e", 2016), 
	(1208, "10e", 2016), 
	(1209, "10e", 2016), 
	(1210, "10e", 2016), 
	(1211, "10e", 2016), 
	(1212, "10e", 2016), 
	(1213, "10e", 2016), 
	(1214, "10e", 2016), 
	(1215, "10e", 2016), 
	(1216, "10e", 2016), 
	(1217, "10e", 2016), 
	(1218, "10e", 2016), 
	(1219, "10e", 2016), 
	(1220, "10e", 2016), 
	(1221, "10e", 2016), 
	(1222, "10e", 2016), 
	(1223, "10e", 2016), 
	(1224, "10e", 2016), 
	(1225, "10e", 2016), 
	(1226, "10f", 2016), 
	(1227, "10f", 2016), 
	(1228, "10f", 2016), 
	(1229, "10f", 2016), 
	(1230, "10f", 2016), 
	(1231, "10f", 2016), 
	(1232, "10f", 2016), 
	(1233, "10f", 2016), 
	(1234, "10f", 2016), 
	(1235, "10f", 2016), 
	(1236, "10f", 2016), 
	(1237, "10f", 2016), 
	(1238, "10f", 2016), 
	(1239, "10f", 2016), 
	(1240, "10f", 2016), 
	(1241, "10f", 2016), 
	(1242, "10f", 2016), 
	(1243, "10f", 2016), 
	(1244, "10f", 2016), 
	(1245, "10f", 2016), 
	(1246, "10f", 2016), 
	(1247, "10f", 2016), 
	(1248, "10f", 2016), 
	(1249, "10f", 2016), 
	(1250, "10f", 2016), 
	(1251, "10g", 2016), 
	(1252, "10g", 2016), 
	(1253, "10g", 2016), 
	(1254, "10g", 2016), 
	(1255, "10g", 2016), 
	(1256, "10g", 2016), 
	(1257, "10g", 2016), 
	(1258, "10g", 2016), 
	(1259, "10g", 2016), 
	(1260, "10g", 2016), 
	(1261, "10g", 2016), 
	(1262, "10g", 2016), 
	(1263, "10g", 2016), 
	(1264, "10g", 2016), 
	(1265, "10g", 2016), 
	(1266, "10g", 2016), 
	(1267, "10g", 2016), 
	(1268, "10g", 2016), 
	(1269, "10g", 2016), 
	(1270, "10g", 2016), 
	(1271, "10g", 2016), 
	(1272, "10g", 2016), 
	(1273, "10g", 2016), 
	(1274, "10g", 2016), 
	(1275, "10g", 2016), 
	(1276, "10h", 2016), 
	(1277, "10h", 2016), 
	(1278, "10h", 2016), 
	(1279, "10h", 2016), 
	(1280, "10h", 2016), 
	(1281, "10h", 2016), 
	(1282, "10h", 2016), 
	(1283, "10h", 2016), 
	(1284, "10h", 2016), 
	(1285, "10h", 2016), 
	(1286, "10h", 2016), 
	(1287, "10h", 2016), 
	(1288, "10h", 2016), 
	(1289, "10h", 2016), 
	(1290, "10h", 2016), 
	(1291, "10h", 2016), 
	(1292, "10h", 2016), 
	(1293, "10h", 2016), 
	(1294, "10h", 2016), 
	(1295, "10h", 2016), 
	(1296, "10h", 2016), 
	(1297, "10h", 2016), 
	(1298, "10h", 2016), 
	(1299, "10h", 2016), 
	(1300, "10h", 2016);