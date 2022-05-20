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
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

/**
 * A simple class that contains of a page
 * and a content stream (as there must be just
 * one content stream per page obviously)
 */
public class PDFPageWithStream {

    private final PDDocument doc;
    private final PDPage page;
    private PDPageContentStream stream;

    private float renderedYPosition;

    public PDFPageWithStream(PDDocument doc, PDPage page) {
        this(doc, page, null);
    }

    public PDFPageWithStream(PDDocument doc, PDPage page, PDPageContentStream stream) {
        this.doc = doc;
        this.page = page;
        this.stream = stream;
        this.renderedYPosition = page.getMediaBox().getHeight();
    }

    public PDDocument getDoc() {
        return doc;
    }

    public PDPage getPage() {
        return page;
    }

    public boolean isFreshPage() {
        return this.stream == null;
    }

    public PDPageContentStream getOrCreateStream() throws IOException {
        if (this.stream == null) {
            this.stream = new PDPageContentStream(doc, page);
        }
        return this.stream;
    }

    /**
     * returns the position where the last render stopped
     * on this particular page
     * @return
     */
    public float getRenderedYPosition() {
        return renderedYPosition;
    }

    public void setRenderedYPosition(float renderedYPosition) {
        this.renderedYPosition = renderedYPosition;
    }

    public void close() throws IOException {
        if (this.stream != null) {
            this.stream.close();
        }
    }

}
