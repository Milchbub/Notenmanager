package de.tum.sep.siglerbischoff.notenverwaltung.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;

import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;

import de.tum.sep.siglerbischoff.notenverwaltung.model.Klasse;
import de.tum.sep.siglerbischoff.notenverwaltung.model.KlasseNotenModel;
import de.tum.sep.siglerbischoff.notenverwaltung.model.PDFFehler;


final class PDFController {

	public static void print(File file, KlasseNotenModel klasseNotenModel, Klasse klasse) throws PDFFehler, FileNotFoundException {
		try {
			InputStream in = ClassLoader.getSystemResourceAsStream("print.html");
			String html;
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				StringBuilder builder = new StringBuilder();
				String aux = "";

				while ((aux = reader.readLine()) != null) {
				    builder.append(aux);
				}

				html = builder.toString();
			} catch (IOException e) {
				throw new PDFFehler(e);
			}

			//TODO: hier müssen irgendwie die zu druckenden Daten in "html" eingebaut werden
			html = html.replaceFirst("%Titel%", "Durchschnittsnoten der Klasse " + klasse.gebeName());
			html = html.replaceFirst("%Text%", "Schuljahr " + klasse.gebeJahr() + "/" + (klasse.gebeJahr() - 1999));
			
			StringBuilder tabelle = new StringBuilder("<tr>");
			for(int i = 0; i < klasseNotenModel.getColumnCount(); i++) {
				tabelle.append("<th>");
				tabelle.append(klasseNotenModel.getColumnName(i));
				tabelle.append("</th>");
			}
			tabelle.append("</tr>");
			for(int y = 0; y < klasseNotenModel.getRowCount(); y++) {
				tabelle.append("<tr>");
				for(int x = 0; x < klasseNotenModel.getColumnCount(); x++) {
					tabelle.append("<td" + (x > 0 ? " class=\"noten\">" : ">"));
					tabelle.append(klasseNotenModel.getValueAt(y, x));
					tabelle.append("</td>");
				}
				tabelle.append("</tr>");
			}
			
			html = html.replaceFirst("%Tabelle%", tabelle.toString());
				
			ITextRenderer renderer = new ITextRenderer();
			renderer.setDocumentFromString(html);
			
			renderer.layout();
			renderer.createPDF(new FileOutputStream(file), true);
		} catch (DocumentException e) {
			throw new PDFFehler(e);
		}
	}
}
