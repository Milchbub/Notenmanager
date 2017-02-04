package de.tum.sep.siglerbischoff.notenverwaltung.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import org.junit.Test;
import de.tum.sep.siglerbischoff.notenverwaltung.model.DatenbankFehler;
import de.tum.sep.siglerbischoff.notenverwaltung.model.*;

public class ModelTest {

	// Es wird geprueft, ob ein erstelltes MysqlDAO eine korrekte Verbindung zur DB aufbaut.
	// Test geschieht ueber eine absichtlich falsch gestaltete Passwortpruefung.
	@Test
	public void databaseConnectionTest() {
		Model model = null;
		try {
			model = new Model();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Login login = new Login("sigl", new char[] {'t', 'u', '2' , '0', '1', '7'});
		Benutzer benutzer;
		try {
			benutzer = model.passwortPruefen(login);
			assertEquals("Test user return after login ->",
					"Magdalena Sigler", benutzer.gebeName());
		} catch (DatenbankFehler e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Error while accessing database within testing!");
		}
	}
	
	// Es wird getestet, ob das erstellen der Model-Klasse korrekt arbeitet. Mit dem erstellen einer
	// Model Instanz wird auch das MySqlDAO erzeugt, was damit auch auch richtige Funktion überprüft
	// wird. Verglichen wird hierbei die Class eines erzeugten Model mit der Class des Model, welche
	// im ContextClassLoader herumschwirrt. Diese wiederum wird ueber die findClassInPackage Methode
	// gefunden.
	@Test
	public void createModelTest() {
		Model model = null;
		try {
			model = new Model();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Sinnbildliche Erklaerung:
		// assertEquals("Message fuer was auch immer", Erwarteter Wert, Wert aus Programm)
		assertEquals("created Model class ->",
				findClassInPackage("Model", "de.tum.sep.siglerbischoff.notenverwaltung.model"),
				model.getClass());
	}

	public Class findClassInPackage(String klassenName, String paketName) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;
		String packageName = paketName;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = null;
		try {
			resources = classLoader.getResources(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		ArrayList<Class> classes = new ArrayList<Class>();
		for (File directory : dirs) {
			try {
				classes.addAll(findClasses(directory, packageName));
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Class[] classArray = classes.toArray(new Class[classes.size()]);
		
		Class targetClass = null;
		for (Class klasse : classArray){
			if (klasse.getSimpleName().equals(klassenName))
				targetClass = klasse;
		}
		return targetClass;
	}

	private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
	    List<Class> classes = new ArrayList<Class>();
	    if (!directory.exists()) {
	        return classes;
	    }
	    File[] files = directory.listFiles();
	    for (File file : files) {
	    	if (file.isDirectory()) {
	    		assert !file.getName().contains(".");
	    		classes.addAll(findClasses(file, packageName + "." + file.getName()));
	    	} else if (file.getName().endsWith(".class")) {
	    		classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
	    		}
	    	}
	    return classes;
	}
	
}