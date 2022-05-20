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
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Test;

public class PDFTextFieldTest {

    public PDFTextFieldTest() {
    }

    @Test
    public void testSomeMethod() throws IOException {
        PDDocument doc = new PDDocument();
        PDPage firstPage = new PDPage(PDRectangle.A4);
        PDFRenderContext context = new PDFRenderContext(doc, firstPage);

        PDFLabel textField = new PDFLabel(80 * PDFUtils.MM_TO_POINTS_72DPI);
        textField.setText("Hello this is a\t test €");
        textField.render(context, 10 * PDFUtils.MM_TO_POINTS_72DPI, 10 * PDFUtils.MM_TO_POINTS_72DPI);

        context.closeAllPages();
        PDFUtils.renderPDF(doc);
    }

}
