/*
 * $Id: iTextVersion.java 3373 2008-05-12 16:21:24Z xlv $
 *
 * This code is part of the 'OpenPDF Tutorial'.
 * You can find the complete tutorial at the following address:
 * https://github.com/LibrePDF/OpenPDF/wiki/Tutorial
 *
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 *
 */

package org.openpdf.examples.general.faq;


import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Paragraph;
import org.openpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Demonstrates the use of getVersion.
 *
 * @author blowagie
 */
public class OpenPdfVersion {

    /**
     * Creates a PDF document and shows the iText version.
     *
     * @param args no arguments needed here
     */
    public static void main(String[] args) {

        System.out.println("OpenPDF version " + Document.getVersion());
        // step 1: creation of a document-object
        Document document = new Document();

        try {

            // step 2:
            // we create a writer that listens to the document
            // and directs a PDF-stream to a file
            PdfWriter.getInstance(document, new FileOutputStream("version.pdf"));

            // step 3: we open the document
            document.open();

            // step 4:
            document.add(new Paragraph("This page was made using " + Document.getVersion()));
        } catch (DocumentException | IOException de) {
            System.err.println(de.getMessage());
        }

        // step 5: we close the document
        document.close();
    }
}