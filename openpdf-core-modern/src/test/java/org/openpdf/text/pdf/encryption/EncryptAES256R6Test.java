package org.openpdf.text.pdf.encryption;

import org.openpdf.text.Document;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.PdfContentByte;
import org.openpdf.text.pdf.PdfReader;
import org.openpdf.text.pdf.PdfStamper;
import org.openpdf.text.pdf.PdfWriter;
import org.openpdf.text.pdf.parser.PdfTextExtractor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author mkl
 */
class EncryptAES256R6Test {

    final static File RESULT_FOLDER = new File("target/test-outputs", "issue375");

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        RESULT_FOLDER.mkdirs();
    }

    @Test
    void testCreateSimplePdf() throws IOException {
        File result = new File(RESULT_FOLDER, "CreateSimplePdf.pdf");

        Document document = new Document();
        try (OutputStream os = new FileOutputStream(result)) {
            PdfWriter pdfWriter = PdfWriter.getInstance(document, os);
            pdfWriter.setEncryption("user".getBytes(), "owner".getBytes(), 0, PdfWriter.ENCRYPTION_AES_256_V3);

            document.open();
            document.add(new Paragraph("Some test content"));
            document.close();
        }

        PdfReader pdfReader = new PdfReader(result.getAbsolutePath(), "owner".getBytes());
        pdfReader.setModificationAllowedWithoutOwnerPassword(false);
        Assertions.assertTrue(pdfReader.isEncrypted(), "PdfReader fails to report test file to be encrypted.");
        Assertions.assertTrue(pdfReader.isOpenedWithFullPermissions(),
                "PdfReader fails to recognize password as owner password.");
        Assertions.assertEquals(1, pdfReader.getNumberOfPages(),
                "PdfReader fails to report the correct number of pages");
        Assertions.assertEquals("Some test content", new PdfTextExtractor(pdfReader).getTextFromPage(1),
                "Wrong text extracted from page 1");
        pdfReader.close();

        pdfReader = new PdfReader(result.getAbsolutePath(), "user".getBytes());
        pdfReader.setModificationAllowedWithoutOwnerPassword(false);
        Assertions.assertTrue(pdfReader.isEncrypted(), "PdfReader fails to report test file to be encrypted.");
        Assertions.assertFalse(pdfReader.isOpenedWithFullPermissions(),
                "PdfReader fails to recognize password as user password.");
        Assertions.assertEquals(1, pdfReader.getNumberOfPages(),
                "PdfReader fails to report the correct number of pages");
        Assertions.assertEquals("Some test content", new PdfTextExtractor(pdfReader).getTextFromPage(1),
                "Wrong text extracted from page 1");
        pdfReader.close();
    }

    @Test
    void testStampPwProtectedAES256_openPDFiss375() throws IOException {
        File result = new File(RESULT_FOLDER, "pwProtectedAES256_openPDFiss375-Stamped.pdf");

        try (InputStream resource = getClass().getResourceAsStream("/issue375/pwProtectedAES256_openPDFiss375.pdf");
                OutputStream os = new FileOutputStream(result)) {
            PdfReader pdfReader = new PdfReader(resource);
            PdfStamper pdfStamper = new PdfStamper(pdfReader, os, (char) 0, true);

            Rectangle box = pdfReader.getPageSize(1);
            PdfContentByte canvas = pdfStamper.getOverContent(1);
            canvas.setRGBColorStroke(255, 0, 0);
            canvas.moveTo(box.getLeft(), box.getBottom());
            canvas.lineTo(box.getRight(), box.getTop());
            canvas.moveTo(box.getRight(), box.getBottom());
            canvas.lineTo(box.getLeft(), box.getTop());
            canvas.stroke();

            pdfStamper.close();
            pdfReader.close();
        }

        PdfReader pdfReader = new PdfReader(result.getAbsolutePath());
        Assertions.assertTrue(pdfReader.isEncrypted(), "PdfReader fails to report test file to be encrypted.");
        Assertions.assertEquals(1, pdfReader.getNumberOfPages(),
                "PdfReader fails to report the correct number of pages");
        Assertions.assertEquals("TEST", new PdfTextExtractor(pdfReader).getTextFromPage(1),
                "Wrong text extracted from page 1");
        pdfReader.close();
    }
}
