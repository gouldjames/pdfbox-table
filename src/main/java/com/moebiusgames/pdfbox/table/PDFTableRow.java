/*
 * The MIT License
 *
 * Copyright 2019 Moebiusgames UG (haftungsbeschraenkt).
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
import java.util.ArrayList;
import java.util.List;

public class PDFTableRow {

    private final int index;
    final List<PDFTableCell> cells = new ArrayList<>();
    private float minHeight = 0;
    private final PDFTable table;

    PDFTableRow(final PDFTable table, int index) {
        this.table = table;
        this.index = index;
        for (int i = 0; i < table.getColumns(); ++i) {
            this.cells.add(new PDFTableCell(this, i, table));
        }
    }

    public int getIndex() {
        return index;
    }

    public float getMinHeight() {
        return minHeight;
    }

    public void setMinHeight(float minHeight) {
        this.minHeight = minHeight;
    }

    public PDFTableCell getCell(int index) {
        if (index < 0 || index >= cells.size()) {
            throw new IllegalArgumentException("index out of range");
        }
        return this.cells.get(index);
    }

    public float getMaxHeight() throws IOException {
        float maxHeight = 0f;
        for (int colIndex = 0; colIndex < cells.size(); ++colIndex) {
            final PDFTableCell col = cells.get(colIndex);
            //skip multi cell place holders
            if (col != null) {
                col.updateContentLayout();
                maxHeight = Math.max(maxHeight, col.getHeight());
            }
        }
        return Math.max(this.minHeight, maxHeight);
    }

}
