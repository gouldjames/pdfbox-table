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

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

final class Utils {

    private Utils() {
    }

    private static final float CIRCLE_CONSTANT = 0.552284749831f;
    private static final Map<String, Float> FONT_HEIGHT_CACHE = new HashMap<>();

    static float measureTextSize(PDFont font, int fontSize, String text) {
        try {
            return (font.getStringWidth(text) / 1000f) * fontSize;
        } catch (IOException e) {
            throw new IllegalStateException("Problem with font", e);
        }
    }

    @SuppressWarnings("deprecation")
    static synchronized float getFontHeight(PDFont font, float fontSize) {
        try {
            String key = font.getName() + "_" + font.getType() + "_" + font.getSubType();
            Float lineHeight = FONT_HEIGHT_CACHE.get(key);
            if (lineHeight == null) {
                lineHeight = 0f;
                
                for (char c : new char[]{
                    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
                }) {
                    //TODO: use font.getBoundingBox().getHeight() instead?
                    lineHeight = Math.max(lineHeight, font.getHeight(c));
                }
                lineHeight /= 1000f;
                FONT_HEIGHT_CACHE.put(key, lineHeight);
            }
            return lineHeight * fontSize;
        } catch (IOException e) {
            throw new IllegalStateException("Problem with font", e);
        }
    }

    static Color htmlColorToColor(String color) {
        if (color.startsWith("#")) {
            try {
                return new Color(Integer.parseInt(color.substring(1), 16));
            } catch (NumberFormatException e) {
            }
        } else if (color.startsWith("rgb")) {
            Pattern pattern = Pattern.compile("rgb\\( *([0-9]+), *([0-9]+), *([0-9]+)\\)");
            Color colorObject = null;
            Matcher matcher = pattern.matcher(color);
            if (matcher.matches()) {
                colorObject =
                        new Color(
                                Integer.parseInt(matcher.group(1)),
                                Integer.parseInt(matcher.group(2)),
                                Integer.parseInt(matcher.group(3)));
            }
            return colorObject;

        } else if (color.startsWith("hsl")){
            Pattern pattern = Pattern.compile("hsl\\( *([0-9]+), *([0-9]+)%, *([0-9]+)%\\)");
            Color colorObject = null;
            Matcher matcher = pattern.matcher(color);

            if (matcher.matches()) {
                colorObject =
                        ColorUtils.getHSLColor(
                                Integer.parseInt(matcher.group(1)),
                                Integer.parseInt(matcher.group(2)),
                                Integer.parseInt(matcher.group(3)));
            }
            return colorObject;
        } else {
            switch (color.trim().toLowerCase()) {
                case "black":
                    return null; //we treat black as null
                case "red":
                    return Color.RED;
                case "green":
                    return Color.GREEN;
                case "blue":
                    return Color.BLUE;
                case "yellow":
                    return Color.YELLOW;
            }
        }
        return null;
    }

    /**
     *
     * @param stream
     * @param x x pos center of the circle
     * @param y y pos center of the circle
     * @param r
     * @throws IOException
     */
    static void drawCircle(PDPageContentStream stream, float x, float y, float r) throws IOException {
        stream.moveTo(x - r, y);
        stream.curveTo(x - r, y + CIRCLE_CONSTANT * r, x - CIRCLE_CONSTANT * r, y + r, x, y + r);
        stream.curveTo(x + CIRCLE_CONSTANT * r, y + r, x + r, y + CIRCLE_CONSTANT * r, x + r, y);
        stream.curveTo(x + r, y - CIRCLE_CONSTANT * r, x + CIRCLE_CONSTANT * r, y - r, x, y - r);
        stream.curveTo(x - CIRCLE_CONSTANT * r, y - r, x - r, y - CIRCLE_CONSTANT * r, x - r, y);
    }

}
