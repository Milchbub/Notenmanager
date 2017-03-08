package de.tum.sep.siglerbischoff.notenverwaltung.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;

import de.tum.sep.siglerbischoff.notenverwaltung.model.PDFFehler;


class PDFController {

	public static void print(File file) throws PDFFehler, FileNotFoundException {
		try {
			File in = new File(ClassLoader.getSystemResource("print.html").toURI());
			try {
				String html = new String(Files.readAllBytes(in.toPath()));
				
				//TODO: hier müssen irgendwie die zu druckenden Daten in "html" eingebaut werden
				
			} catch (IOException e) {
				throw new PDFFehler(e);
			}
			
			ITextRenderer renderer = new ITextRenderer();
			
			try {
				renderer.setDocument(in);
			} catch (IOException e) {
				throw new PDFFehler(e);
			}
			
			renderer.layout();
			renderer.createPDF(new FileOutputStream(file), true);
		} catch(URISyntaxException e) {
			throw new PDFFehler(e);
		} catch (DocumentException e) {
			throw new PDFFehler(e);
		}
	}
}
