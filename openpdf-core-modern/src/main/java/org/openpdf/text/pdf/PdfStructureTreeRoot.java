/*
 * $Id: PdfStructureTreeRoot.java 3914 2009-04-26 09:16:47Z blowagie $
 *
 * Copyright 2005 by Paulo Soares.
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the License.
 *
 * The Original Code is 'iText, a free JAVA-PDF library'.
 *
 * The Initial Developer of the Original Code is Bruno Lowagie. Portions created by
 * the Initial Developer are Copyright (C) 1999, 2000, 2001, 2002 by Bruno Lowagie.
 * All Rights Reserved.
 * Co-Developer of the code is Paulo Soares. Portions created by the Co-Developer
 * are Copyright (C) 2000, 2001, 2002 by Paulo Soares. All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 *
 * Alternatively, the contents of this file may be used under the terms of the
 * LGPL license (the "GNU LIBRARY GENERAL PUBLIC LICENSE"), in which case the
 * provisions of LGPL are applicable instead of those above.  If you wish to
 * allow use of your version of this file only under the terms of the LGPL
 * License and not to allow others to use your version of this file under
 * the MPL, indicate your decision by deleting the provisions above and
 * replace them with the notice and other provisions required by the LGPL.
 * If you do not delete the provisions above, a recipient may use your version
 * of this file under either the MPL or the GNU LIBRARY GENERAL PUBLIC LICENSE.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the MPL as stated above or under the terms of the GNU
 * Library General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library general Public License for more
 * details.
 *
 * If you didn't download this code from the following link, you should check if
 * you aren't using an obsolete version:
 * https://github.com/LibrePDF/OpenPDF
 */
package org.openpdf.text.pdf;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The structure tree root corresponds to the highest hierarchy level in a tagged PDF.
 *
 * @author Paulo Soares (psoares@consiste.pt)
 */
public class PdfStructureTreeRoot extends PdfDictionary {

    private final Map<Integer, PdfObject> parentTree = new HashMap<>();
    private final PdfIndirectReference reference;

    /** Next key to be used for adding to the parentTree */
    private int parentTreeNextKey = 0;

    /** Map which connects [page number] with corresponding [parentTree entry key]  */
    private final Map<Integer, Integer> pageKeysMap = new HashMap<>();

    /**
     * Holds value of property writer.
     */
    private final PdfWriter writer;

    /**
     * Creates a new instance of PdfStructureTreeRoot
     */
    PdfStructureTreeRoot(PdfWriter writer) {
        super(PdfName.STRUCTTREEROOT);
        this.writer = writer;
        reference = writer.getPdfIndirectReference();
    }

    /**
     * Maps the user tags to the standard tags. The mapping will allow a standard application to make some sense of the
     * tagged document whatever the user tags may be.
     *
     * @param used     the user tag
     * @param standard the standard tag
     */
    public void mapRole(PdfName used, PdfName standard) {
        PdfDictionary rm = (PdfDictionary) get(PdfName.ROLEMAP);
        if (rm == null) {
            rm = new PdfDictionary();
            put(PdfName.ROLEMAP, rm);
        }
        rm.put(used, standard);
    }

    /**
     * Gets the writer.
     *
     * @return the writer
     */
    public PdfWriter getWriter() {
        return this.writer;
    }

    /**
     * Gets the reference this object will be written to.
     *
     * @return the reference this object will be written to
     * @since 2.1.6 method removed in 2.1.5, but restored in 2.1.6
     */
    public PdfIndirectReference getReference() {
        return this.reference;
    }

    /**
     * Adds a reference to the existing (already added to the document) object to the parentTree.
     * This method can be used when the object is need to be referenced via /StructParent key.
     *
     * @return key which define the object record key (e.g. in /NUMS array)
     */
    public int addExistingObject(PdfIndirectReference reference) {
        int key = parentTreeNextKey;
        parentTree.put(key, reference);
        parentTreeNextKey++;
        return key;
    }

    void setPageMark(int pageNumber, PdfIndirectReference reference) {
        PdfArray pageArray = (PdfArray) parentTree.get(getOrCreatePageKey(pageNumber));
        pageArray.add(reference);
    }

    /**
     * Returns array ID for a page-related entry or creates a new one if not exists.
     * Can be used for STRUCTPARENTS tag value
     *
     * @param pageNumber number of page for which the ID is required
     * @return Optional with array ID, empty Optional otherwise
     */
    int getOrCreatePageKey(int pageNumber) {
        Integer entryForPageArray = pageKeysMap.get(pageNumber);
        if (entryForPageArray == null) {
            //putting page array
            PdfArray ar = new PdfArray();
            entryForPageArray = parentTreeNextKey;
            parentTree.put(entryForPageArray, ar);
            parentTreeNextKey++;
            pageKeysMap.put(pageNumber, entryForPageArray);
        }
        return entryForPageArray;
    }

    private void nodeProcess(PdfDictionary dictionary, PdfIndirectReference reference)
            throws IOException {
        PdfObject obj = dictionary.get(PdfName.K);
        if (obj != null && obj.isArray() && !((PdfArray) obj).getElements().isEmpty() && !((PdfArray) obj).getElements()
                .get(0).isNumber()) {
            PdfArray ar = (PdfArray) obj;
            for (int k = 0; k < ar.size(); ++k) {
                PdfObject pdfObj = ar.getDirectObject(k);

                if (pdfObj instanceof PdfStructureElement e) {
                    ar.set(k, e.getReference());
                    nodeProcess(e, e.getReference());
                } else if (pdfObj instanceof PdfIndirectReference) {
                    ar.set(k, pdfObj);
                }
            }
        }
        if (reference != null) {
            writer.addToBody(dictionary, reference);
        }
    }

    void buildTree() throws IOException {
        Map<Integer, PdfIndirectReference> numTree = new HashMap<>();
        for (Integer i : parentTree.keySet()) {
            PdfObject pdfObj = parentTree.get(i);
            if (pdfObj instanceof PdfIndirectReference pdfRef) {
                //saving the reference to the object which was already added to the body
                numTree.put(i, pdfRef);
            } else {
                numTree.put(i, writer.addToBody(pdfObj).getIndirectReference());
            }
        }
        PdfDictionary dicTree = PdfNumberTree.writeTree(numTree, writer);
        if (dicTree != null) {
            put(PdfName.PARENTTREE, writer.addToBody(dicTree).getIndirectReference());
        }

        nodeProcess(this, reference);
    }
}
