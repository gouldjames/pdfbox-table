/*
 * The MIT License
 *
 * Copyright 2019-2022 MobiusCode GmbH.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.moebiusgames.pdfbox.table;

import java.io.IOException;

public class PDFLabel {

    private final PDFTable table;
    private final PDFTableRow row;

    public PDFLabel(float width) {
        this(width, TextType.PLAIN);
    }

    public PDFLabel(float width, TextType textType) {
        this.table = new PDFTable(width);
        this.table.setColumnHeadersMode(PDFTable.ColumnHeadersMode.NO_COLUMN_HEADERS);
        row = this.table.addRow();

        final PDFTableBorder noBorder = new PDFTableBorder();
        noBorder.setLineWidth(0);
        getCell().setBorderBottom(noBorder)
            .setBorderTop(noBorder)
            .setBorderLeft(noBorder)
            .setBorderRight(noBorder)
            .setTextType(textType);
    }

    public void setText(String text) {
        getCell().setContent(text);
    }

    public final PDFTableCell getCell() {
        return this.row.getCell(0);
    }

    /**
     * renders the label on the last page of the
     * given render context directly under previously
     * rendered elements
     *
     * @param renderContext the render context
     * @param x x position to render the label at
     * @throws IOException
     */
    public void render(PDFRenderContext renderContext,
            float x) throws IOException {
        this.table.render(renderContext, x);
    }

    /**
     * renders the label on the last page of the
     * given render context
     *
     * @param renderContext the render context
     * @param x x position to render the label at
     * @param y y position to render the label at
     * @throws IOException
     */
    public void render(PDFRenderContext renderContext,
            float x, float y) throws IOException {
        this.table.render(renderContext, x, y);
    }

    /**
     * renders the label on the given page
     *
     * @param renderContext the render context
     * @param page the page to render the label on
     * @param x x position to render the label at
     * @param y y position to render the label at
     * @throws IOException
     */
    public void render(PDFRenderContext renderContext, PDFPageWithStream page,
            float x, float y) throws IOException {
        this.table.render(renderContext, page, x, y);
    }

}
