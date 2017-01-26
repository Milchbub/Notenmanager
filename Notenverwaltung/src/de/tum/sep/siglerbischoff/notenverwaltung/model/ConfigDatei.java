package de.tum.sep.siglerbischoff.notenverwaltung.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Properties;

public class ConfigDatei extends Properties {
	
	private static final long serialVersionUID = 1L;

	public ConfigDatei() throws IOException {
		Properties defaults = new Properties();
		defaults.setProperty("dbhost", "localhost");
		defaults.setProperty("dbname", "Notenmanager");
		this.defaults = defaults;
		
		File file = new File(URLDecoder.decode(
				ClassLoader.getSystemClassLoader().getResource(".").getPath(), "UTF-8") + "/.config");
		if(!file.createNewFile()) {
			try (FileInputStream fis = new FileInputStream(file)) {
				load(fis);
			}
		} else {
			try (FileOutputStream fos = new FileOutputStream(file)) {
				defaults.store(fos, "Konfigurationsdatei für den Notenmanager. ");
			}
		}
	}
	
}
