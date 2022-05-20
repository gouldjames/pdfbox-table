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
import org.apache.pdfbox.pdmodel.PDPageContentStream;

/**
 * Simple helper for bullet points
 */
enum BulletPoint {

    CIRCLE((stream, x, y, size) -> {
        Utils.drawCircle(
                stream,
                x,
                y,
                size / 2f
        );
        stream.fill();
    }),
    CIRCLE_HOLLOW((stream, x, y, size) -> {
        Utils.drawCircle(
                stream,
                x,
                y,
                size / 2f
        );
        stream.stroke();
    }),
    RECTANGLE((stream, x, y, size) -> {
        stream.addRect(x - size / 2f, y - size / 2f, size, size);
        stream.fill();
    });

    static interface DrawFunction {
        void draw(PDPageContentStream stream, float x, float y, float size) throws IOException;
    }

    private final DrawFunction drawFunction;

    private BulletPoint(DrawFunction drawFunction) {
        this.drawFunction = drawFunction;
    }

    /**
     * draws the corresponding bullet point at the given x, y position (these
     * are assumed to be the center of the bullet point)
     *
     * @param stream
     * @param x the x pos for the center of the bullet point
     * @param y the y pos for the center of the bullet point
     * @param size
     */
    public void draw(PDPageContentStream stream, float x, float y, float size) throws IOException {
        this.drawFunction.draw(stream, x, y, size);
    }

    public static BulletPoint getForIndent(int indent) {
        indent = Math.min(BulletPoint.values().length - 1, indent - 1);
        return BulletPoint.values()[indent];
    }

}
