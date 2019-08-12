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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public final class PDFUtils {

    public static final PDFTableBorder NO_BORDER = new PDFTableBorder();

    public static final float POINTS_PER_INCH = 72;
    public static final float MM_TO_POINTS_72DPI = 1 / (10 * 2.54f) * POINTS_PER_INCH;

    private static final Map<PDFont, PDFont> BOLD_FONT_VARIANTS = new HashMap<>();

    static {
        NO_BORDER.setLineWidth(0);
        BOLD_FONT_VARIANTS.put(PDType1Font.COURIER, PDType1Font.COURIER_BOLD);
        BOLD_FONT_VARIANTS.put(PDType1Font.HELVETICA, PDType1Font.HELVETICA_BOLD);
        BOLD_FONT_VARIANTS.put(PDType1Font.TIMES_ROMAN, PDType1Font.TIMES_BOLD);
    }

    public static PDFont getBoldVariant(PDFont font) {
        PDFont result = BOLD_FONT_VARIANTS.get(font);
        if (result == null) {
            result = PDType1Font.HELVETICA_BOLD; //<- default
        }
        return result;
    }

    public static byte[] renderPDF(PDDocument doc) throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        doc.save(bOut);
        return bOut.toByteArray();
    }

    public static PDFont modifyFont(PDFont font, FontModifier... modifiers) {
        return modifyFont(font, new HashSet<>(Arrays.asList(modifiers)));
    }

    public static PDFont modifyFont(PDFont font, Set<FontModifier> modifiers) {
        final PDFont baseFont;
        if (font.equals(PDType1Font.TIMES_ROMAN)
                || font.equals(PDType1Font.TIMES_BOLD)
                || font.equals(PDType1Font.TIMES_ITALIC)
                || font.equals(PDType1Font.TIMES_BOLD_ITALIC)) {
            baseFont = PDType1Font.TIMES_ROMAN;
        } else
            if (font.equals(PDType1Font.COURIER)
                    || font.equals(PDType1Font.COURIER_BOLD)
                    || font.equals(PDType1Font.COURIER_OBLIQUE)
                    || font.equals(PDType1Font.COURIER_BOLD_OBLIQUE)) {
                baseFont = PDType1Font.COURIER;
            } else
                if (font.equals(PDType1Font.HELVETICA)
                        || font.equals(PDType1Font.HELVETICA_BOLD)
                        || font.equals(PDType1Font.HELVETICA_OBLIQUE)
                        || font.equals(PDType1Font.HELVETICA_BOLD_OBLIQUE)) {
                    baseFont = PDType1Font.HELVETICA;
                } else {
                    throw new IllegalArgumentException("This font is not supported");
                }

        if (baseFont == PDType1Font.TIMES_ROMAN) {
            if (modifiers.contains(FontModifier.BOLD)
                    && modifiers.contains(FontModifier.ITALIC)) {
                return PDType1Font.TIMES_BOLD_ITALIC;
            }

            if (modifiers.contains(FontModifier.BOLD)) {
                return PDType1Font.TIMES_BOLD;
            }

            if (modifiers.contains(FontModifier.ITALIC)) {
                return PDType1Font.TIMES_ITALIC;
            }

            return baseFont;
        } else
            if (baseFont == PDType1Font.COURIER) {
                if (modifiers.contains(FontModifier.BOLD)
                        && modifiers.contains(FontModifier.ITALIC)) {
                    return PDType1Font.COURIER_BOLD_OBLIQUE;
                }

                if (modifiers.contains(FontModifier.BOLD)) {
                    return PDType1Font.COURIER_BOLD;
                }

                if (modifiers.contains(FontModifier.ITALIC)) {
                    return PDType1Font.COURIER_OBLIQUE;
                }

                return baseFont;
            } else {
                if (modifiers.contains(FontModifier.BOLD)
                        && modifiers.contains(FontModifier.ITALIC)) {
                    return PDType1Font.HELVETICA_BOLD_OBLIQUE;
                }

                if (modifiers.contains(FontModifier.BOLD)) {
                    return PDType1Font.HELVETICA_BOLD;
                }

                if (modifiers.contains(FontModifier.ITALIC)) {
                    return PDType1Font.HELVETICA_OBLIQUE;
                }

                return baseFont;
            }
    }

    public static enum FontModifier {
        REGULAR,
        ITALIC,
        BOLD
    }

    private PDFUtils() {}

}
