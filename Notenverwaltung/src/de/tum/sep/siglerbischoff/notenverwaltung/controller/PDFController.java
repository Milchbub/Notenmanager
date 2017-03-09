package de.tum.sep.siglerbischoff.notenverwaltung.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
			File in = new File(ClassLoader.getSystemResource("print.html").toURI());
			String html;
			try {
				html = new String(Files.readAllBytes(in.toPath()));
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
		} catch(URISyntaxException e) {
			throw new PDFFehler(e);
		} catch (DocumentException e) {
			throw new PDFFehler(e);
		}
	}
}
