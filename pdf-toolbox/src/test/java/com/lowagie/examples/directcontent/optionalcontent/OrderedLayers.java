/*
 * $Id: OrderedLayers.java 3838 2009-04-07 18:34:15Z mstorer $
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
package org.openpdf.examples.directcontent.optionalcontent;

import org.openpdf.text.Document;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.PageSize;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.ColumnText;
import org.openpdf.text.pdf.PdfArray;
import org.openpdf.text.pdf.PdfContentByte;
import org.openpdf.text.pdf.PdfDictionary;
import org.openpdf.text.pdf.PdfLayer;
import org.openpdf.text.pdf.PdfLayerMembership;
import org.openpdf.text.pdf.PdfName;
import org.openpdf.text.pdf.PdfOCProperties;
import org.openpdf.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.FileOutputStream;

/**
 * Demonstrates how to order optional content groups.
 */
public class OrderedLayers {

    /**
     * Demonstrates how to order optional content groups.
     *
     * @param args no arguments needed
     */
    public static void main(String[] args) {
        System.out.println("Ordering optional content groups");
        try {
            // step 1
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            // step 2
            PdfWriter writer = PdfWriter.getInstance(document,
                    new FileOutputStream("orderedlayers.pdf"));
            writer.setPdfVersion(PdfWriter.VERSION_1_5);
            writer.setViewerPreferences(PdfWriter.PageModeUseOC);
            // step 3
            document.open();
            // step 4
            PdfContentByte cb = writer.getDirectContent();
            Phrase explanation = new Phrase("Ordered layers", new Font(
                    Font.HELVETICA, 20, Font.BOLD, Color.red));
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, explanation, 50,
                    650, 0);
            PdfLayer l1 = new PdfLayer("Layer 1", writer);
            PdfLayer l2 = new PdfLayer("Layer 2", writer);
            PdfLayer l3 = new PdfLayer("Layer 3", writer);
            PdfLayerMembership m1 = new PdfLayerMembership(writer);
            m1.addMember(l2);
            m1.addMember(l3);
            Phrase p1 = new Phrase("Text in layer 1");
            Phrase p2 = new Phrase("Text in layer 2 or layer 3");
            Phrase p3 = new Phrase("Text in layer 3");
            cb.beginLayer(l1);
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, p1, 50, 600, 0);
            cb.endLayer();
            cb.beginLayer(m1);
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, p2, 50, 550, 0);
            cb.endLayer();
            cb.beginLayer(l3);
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, p3, 50, 500, 0);
            cb.endLayer();
            cb.sanityCheck();

            PdfOCProperties p = writer.getOCProperties();
            PdfArray order = new PdfArray();
            order.add(l1.getRef());
            order.add(l2.getRef());
            order.add(l3.getRef());
            PdfDictionary d = new PdfDictionary();
            d.put(PdfName.ORDER, order);
            p.put(PdfName.D, d);
            // step 5
            document.close();
        } catch (Exception de) {
            de.printStackTrace();
        }
    }

}