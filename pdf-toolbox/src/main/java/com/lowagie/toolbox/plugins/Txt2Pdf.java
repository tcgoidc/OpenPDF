/*
 * $Id: Txt2Pdf.java 3271 2008-04-18 20:39:42Z xlv $
 * Copyright (c) 2005-2007 Bruno Lowagie, Carsten Hammer
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

/*
 * This class was originally published under the MPL by Bruno Lowagie
 * and Carsten Hammer.
 * It was a part of iText, a Java-PDF library. You can now use it under
 * the MIT License; for backward compatibility you can also use it under
 * the MPL version 1.1: http://www.mozilla.org/MPL/
 * A copy of the MPL license is bundled with the source code FYI.
 */

package org.openpdf.toolbox.plugins;

import org.openpdf.text.Document;
import org.openpdf.text.Font;
import org.openpdf.text.FontFactory;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.PdfWriter;
import org.openpdf.toolbox.AbstractTool;
import org.openpdf.toolbox.arguments.AbstractArgument;
import org.openpdf.toolbox.arguments.FileArgument;
import org.openpdf.toolbox.arguments.OptionArgument;
import org.openpdf.toolbox.arguments.PageSizeArgument;
import org.openpdf.toolbox.arguments.filters.PdfFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;

/**
 * Converts a monospaced txt file to a PDF file.
 *
 * @since 2.1.1 (imported from itexttoolbox project)
 */
public class Txt2Pdf extends AbstractTool {

    static {
        addVersion("$Id: Txt2Pdf.java 3271 2008-04-18 20:39:42Z xlv $");
    }

    /**
     * Constructs a Txt2Pdf object.
     */
    public Txt2Pdf() {
        menuoptions = MENU_EXECUTE | MENU_EXECUTE_SHOW | MENU_EXECUTE_PRINT_SILENT;
        arguments.add(new FileArgument(this, "srcfile", "The file you want to convert", false));
        arguments.add(new FileArgument(this, "destfile", "The file to which the converted text has to be written", true,
                new PdfFilter()));
        PageSizeArgument oa1 = new PageSizeArgument(this, "pagesize", "Pagesize");
        arguments.add(oa1);
        OptionArgument oa2 = new OptionArgument(this, "orientation", "Orientation of the page");
        oa2.addOption("Portrait", "PORTRAIT");
        oa2.addOption("Landscape", "LANDSCAPE");
        arguments.add(oa2);
    }

    /**
     * Converts a monospaced txt file to a PDF file.
     *
     * @param args String[]
     */
    public static void main(String[] args) {
        Txt2Pdf tool = new Txt2Pdf();
        if (args.length < 3) {
            System.err.println(tool.getUsage());
        }
        tool.setMainArguments(args);
        tool.execute();
    }

    /**
     * @see org.openpdf.toolbox.AbstractTool#createFrame()
     */
    protected void createFrame() {
        internalFrame = new JInternalFrame("Txt2Pdf", true, true, true);
        internalFrame.setSize(300, 80);
        internalFrame.setJMenuBar(getMenubar());
        System.out.println("=== Txt2Pdf OPENED ===");
    }

    /**
     * @see org.openpdf.toolbox.AbstractTool#execute()
     */
    public void execute() {
        try {
            String line = null;
            Document document;
            Font f;
            Rectangle pagesize = (Rectangle) getValue("pagesize");
            if ("LANDSCAPE".equals(getValue("orientation"))) {
                f = FontFactory.getFont(FontFactory.COURIER, 10);
                document = new Document(pagesize.rotate(), 36, 9, 36, 36);
            } else {
                f = FontFactory.getFont(FontFactory.COURIER, 11);
                document = new Document(pagesize, 72, 36, 36, 36);
            }
            BufferedReader in = new BufferedReader(new FileReader((File) getValue("srcfile")));
            PdfWriter.getInstance(document, new FileOutputStream((File) getValue("destfile")));
            document.open();
            while ((line = in.readLine()) != null) {
                document.add(new Paragraph(12, line, f));
            }
            document.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(internalFrame,
                    e.getMessage(),
                    e.getClass().getName(),
                    JOptionPane.ERROR_MESSAGE);
            System.err.println(e.getMessage());
        }
    }

    /**
     * @param arg StringArgument
     * @see org.openpdf.toolbox.AbstractTool#valueHasChanged(org.openpdf.toolbox.arguments.AbstractArgument)
     */
    public void valueHasChanged(AbstractArgument arg) {
        if (internalFrame == null) {
            // if the internal frame is null, the tool was called from the command line
            return;
        }
        // represent the changes of the argument in the internal frame
    }

    /**
     * @return File
     * @throws InstantiationException on error
     * @see org.openpdf.toolbox.AbstractTool#getDestPathPDF()
     */
    protected File getDestPathPDF() throws InstantiationException {
        return (File) getValue("destfile");
    }
}
