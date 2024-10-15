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

import com.moebiusgames.pdfbox.table.PDFUtils.FontModifier;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDSimpleFont;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

public class PDFTableCell {

    private static final float INDENT_WIDTH = 10 * PDFUtils.MM_TO_POINTS_72DPI;

    private final PDFTableRow row;
    private final int index;
    private int size = 1;
    private int fontSize = PDFTable.NOT_SET;
    private PDFont font = null;
    private Align align = null;
    private String content = "";
    private TextType textType = TextType.PLAIN;
    private LaidoutContent laidoutContent = null;
    private PDFTableBorder borderLeft;
    private PDFTableBorder borderRight;
    private PDFTableBorder borderTop;
    private PDFTableBorder borderBottom;
    private float paddingLeft = PDFTable.NOT_SET;
    private float paddingRight = PDFTable.NOT_SET;
    private float paddingTop = PDFTable.NOT_SET;
    private float paddingBottom = PDFTable.NOT_SET;
    private Color fontColor = PDFTable.NOT_SET_COLOR;
    private Color backgroundColor = PDFTable.NOT_SET_COLOR;
    private float lineSpacingFactor = PDFTable.NOT_SET;
    private Boolean underline = null;
    private final PDFTable table;

    PDFTableCell(PDFTableRow row, int cellIndex, final PDFTable table) {
        this.table = table;
        this.row = row;
        this.index = cellIndex;
    }

    public PDFTableCell setContent(String content) {
        this.content = content;
        return this;
    }

    public String getContent() {
        return content;
    }

    public PDFont getFont() {
        if (!hasFont()) {
            return table.getColumn(index).getFont();
        }
        return this.font;
    }

    public int getFontSize() {
        if (!hasFontSize()) {
            return table.getColumn(index).getFontSize();
        }
        return this.fontSize;
    }

    public Align getAlign() {
        return align;
    }

    public PDFTableCell setAlign(Align align) {
        this.align = align;
        return this;
    }

    public boolean hasAlign() {
        return this.align != null;
    }

    public TextType getTextType() {
        return textType;
    }

    public PDFTableCell setTextType(TextType textType) {
        this.textType = textType;
        return this;
    }

    public PDFTableCell setFont(PDFont font) {
        this.font = font;
        return this;
    }

    public PDFTableCell setFontSize(int fontSize) {
        if (fontSize <= 0 && fontSize != PDFTable.NOT_SET) {
            throw new IllegalArgumentException("font size must be positive");
        }
        this.fontSize = fontSize;
        return this;
    }

    public boolean hasFont() {
        return this.font != null;
    }

    public boolean hasFontSize() {
        return this.fontSize != PDFTable.NOT_SET;
    }

    /**
     * returns the size in columns of this cell
     *
     * @return return the size of the cell
     */
    public int getSize() {
        return size;
    }

    public PDFTableBorder getBorderLeft() {
        return borderLeft == null
                ? table.getColumn(index).getBorderLeft()
                : this.borderLeft;
    }

    public boolean hasBorderLeft() {
        return this.borderLeft != null;
    }

    public PDFTableCell setBorderLeft(PDFTableBorder borderLeft) {
        this.borderLeft = borderLeft;
        return this;
    }

    public PDFTableBorder getBorderRight() {
        return borderRight == null
                ? table.getColumn(index).getBorderRight()
                : this.borderRight;
    }

    public boolean hasBorderRight() {
        return this.borderRight != null;
    }

    public PDFTableCell setBorderRight(PDFTableBorder borderRight) {
        this.borderRight = borderRight;
        return this;
    }

    public PDFTableBorder getBorderTop() {
        return borderTop == null
                ? table.getColumn(index).getBorderTop()
                : this.borderTop;
    }

    public boolean hasBorderTop() {
        return this.borderTop != null;
    }

    public PDFTableCell setBorderTop(PDFTableBorder borderTop) {
        this.borderTop = borderTop;
        return this;
    }

    public PDFTableBorder getBorderBottom() {
        return borderBottom == null
                ? table.getColumn(index).getBorderBottom()
                : this.borderBottom;
    }

    public boolean hasBorderBottom() {
        return this.borderLeft != null;
    }

    public PDFTableCell setBorderBottom(PDFTableBorder borderBottom) {
        this.borderBottom = borderBottom;
        return this;
    }

    public PDFTableCell setPadding(float padding) {
        this.setPaddingLeft(padding);
        this.setPaddingRight(padding);
        this.setPaddingTop(padding);
        this.setPaddingBottom(padding);
        return this;
    }

    public float getPaddingLeft() {
        return paddingLeft == PDFTable.NOT_SET
                ? table.getColumn(this.index).getPaddingLeft()
                : this.paddingLeft;
    }

    public PDFTableCell setPaddingLeft(float paddingLeft) {
        if ((paddingLeft < 0 && paddingLeft != PDFTable.NOT_SET)
                || (paddingLeft + this.paddingRight >= this.getWidth())) {
            throw new IllegalArgumentException("invalid value");
        }
        this.paddingLeft = paddingLeft;
        return this;
    }

    public float getPaddingRight() {
        return paddingRight == PDFTable.NOT_SET
                ? table.getColumn(this.index).getPaddingRight()
                : this.paddingRight;
    }

    public PDFTableCell setPaddingRight(float paddingRight) {
        if ((paddingRight < 0 && paddingRight != PDFTable.NOT_SET)
                || (this.paddingLeft + paddingRight >= this.getWidth())) {
            throw new IllegalArgumentException("invalid value");
        }
        this.paddingRight = paddingRight;
        return this;
    }

    public float getPaddingTop() {
        return paddingTop == PDFTable.NOT_SET
                ? table.getColumn(this.index).getPaddingTop()
                : this.paddingTop;
    }

    public PDFTableCell setPaddingTop(float paddingTop) {
        if (paddingTop < 0 && paddingTop != PDFTable.NOT_SET) {
            throw new IllegalArgumentException("invalid value");
        }
        this.paddingTop = paddingTop;
        return this;
    }

    public float getPaddingBottom() {
        return paddingBottom == PDFTable.NOT_SET
                ? table.getColumn(this.index).getPaddingBottom()
                : this.paddingBottom;
    }

    public PDFTableCell setPaddingBottom(float paddingBottom) {
        if (paddingBottom < 0 && paddingBottom != PDFTable.NOT_SET) {
            throw new IllegalArgumentException("invalid value");
        }
        this.paddingBottom = paddingBottom;
        return this;
    }

    public float getLineSpacingFactor() {
        return lineSpacingFactor == PDFTable.NOT_SET
                ? table.getColumn(index).getLineSpacingFactor()
                : lineSpacingFactor;
    }

    public PDFTableCell setLineSpacingFactor(float lineSpacingFactor) {
        if (lineSpacingFactor < 0 && lineSpacingFactor != PDFTable.NOT_SET) {
            throw new IllegalArgumentException("invalid value");
        }
        this.lineSpacingFactor = lineSpacingFactor;
        return this;
    }

    public Color getFontColor() {
        return fontColor == PDFTable.NOT_SET_COLOR
                ? table.getColumn(index).getFontColor()
                : fontColor;
    }

    public PDFTableCell setFontColor(Color fontColor) {
        if (this.fontColor == null) {
            throw new IllegalArgumentException("null value not allowed");
        }
        this.fontColor = fontColor;
        return this;
    }

    public Color getBackgroundColor() {
        return backgroundColor == PDFTable.NOT_SET_COLOR
                ? table.getColumn(index).getBackgroundColor()
                : backgroundColor;
    }

    public PDFTableCell setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public Boolean getUnderline() {
        return underline == null
                ? table.getColumn(index).getUnderline()
                : underline;
    }

    public PDFTableCell setUnderline(Boolean underline) {
        this.underline = underline;
        return this;
    }

    /**
     * returns the width of this cell in user space units
     *
     * @return the width of the cell
     */
    public float getWidth() {
        float total = 0;
        for (int i = this.index; i < this.index + this.size; ++i) {
            total += table.getColumn(i).getWidth();
        }
        return total;
    }

    /**
     * returns the width of segment n of this (multi column) cell
     *
     * @param n number of segments
     * @return the width of the given n next columns
     */
    public float getWidth(int n) {
        if (n < 0 || n >= this.getSize()) {
            throw new IllegalArgumentException("n mus be between 0 and " + (this.getSize() - 1));
        }
        return table.getColumn(index + n).getWidth();
    }

    /**
     * returns the size that there is for text in this
     * cell
     *
     * @return the size for text in this cell
     */
    public float getTextSpaceWidth() {
        return this.getWidth() - (this.getPaddingLeft() + this.getPaddingRight());
    }

    /**
     * merges this cell with the next n cells
     *
     * @param n number of next cells to merge
     */
    public void merge(int n) {
        if (n <= 0 || index + n >= row.cells.size()) {
            throw new IllegalArgumentException("invalid n");
        }
        for (int i = index + 1; i <= index + n; ++i) {
            row.cells.set(i, null);
        }
        //clean up after our cell
        for (int i = index + n + 1; i < row.cells.size(); ++i) {
            final PDFTableCell cell = row.cells.get(i);
            if (cell == null) {
                row.cells.set(i, new PDFTableCell(row, i, table));
            } else {
                break;
            }
        }
        this.size = n + 1;
    }

    float getMinRequiredHeight() throws IOException {
        if (laidoutContent == null) {
            throw new IllegalStateException("No layout found in col");
        }
        return laidoutContent.getHeight() + getPaddingBottom() + getPaddingTop();
    }

    float getHeight() throws IOException {
//        final float leading = Utils.getFontHeight(getFont(), getFontSize()) * (1.0f + getLineSpacingFactor());
        if (laidoutContent == null) {
            throw new IllegalStateException("No layout found in col");
        }
//        return leading * laidoutContent.size() + getPaddingBottom() + getPaddingTop();
        return laidoutContent.getHeight();
    }

    void updateContentLayout() {
        switch (this.textType) {
            case PLAIN:
                this.laidoutContent = layoutPlainContent(this.content);
                break;
            case HTML:
                this.laidoutContent = layoutHTMLContent(this.content);
                break;
        }
    }

    LaidoutContent getLaidoutContent() {
        return laidoutContent;
    }

    private LaidoutContent layoutPlainContent(String aContent) {
        LaidoutContent aLaidoutContent = new LaidoutContent();
        plainContentToBlocks(aContent, aLaidoutContent);
        return aLaidoutContent;
    }

    private LaidoutContent layoutHTMLContent(String aContent) {
        final Document document = Jsoup.parse(aContent);

        LaidoutContent newLaidoutContent = new LaidoutContent();
        traverseHTMLLayout(newLaidoutContent, new Pos(), new LayoutFrame(),
                new NewLineLayout(), null, document.body());
        newLaidoutContent.trim();

        return newLaidoutContent;
    }

    private void traverseHTMLLayout(LaidoutContent laidoutContent, Pos xPos, LayoutFrame inFrame,
            NewLineLayout newLineLayout, Node predecessorNode, Node node) {
        LayoutFrame frame = new LayoutFrame(inFrame);

        if (predecessorNode != null) {
            switch (predecessorNode.nodeName().toLowerCase()) {
                case "ul":
                case "blockquote":
                    //only add new line when this is
                    //after <ul> but also not still within
                    //another <ul> tag! This results in
                    //a new line after the last </ul>
                    if (frame.indent == 0) {
                        newLineLayout.newLines++;
                    }
                    break;
            }
        }

        switch (node.nodeName().toLowerCase()) {
            case "div":
            case "p":
                newLineLayout.setConditionalNewLine(1);
                break;
            case "br":
                applyNewLineLayout(newLineLayout, laidoutContent, xPos, frame);
                laidoutContent.addRow(frame);
                xPos.pos = 0;
                break;
            case "blockquote":
                //add newline in front of blockquote
                //if there is not already one
                if (frame.indent == 0) {
                    newLineLayout.setConditionalNewLine(2);
                }
                frame.indent++;
                break;
            case "b":
            case "strong":
                frame.bold = true;
                break;
            case "i":
                frame.italic = true;
                break;
            case "u":
                frame.underline = true;
                break;
            case "font":
                if (node.hasAttr("size")) {
                    try {
                        frame.htmlSize = Integer.valueOf(node.attr("size"));
                    } catch (NumberFormatException e) {
                        //ignore for now
                    }
                }
                if (node.hasAttr("color")) {
                    frame.color = Utils.htmlColorToColor(node.attr("color"));
                }
                break;
            case "span":
                if (node.hasAttr("style")) {
                    if(node.attr("style").contains("color")) {
                     String style = node.attr("style").replaceAll("\\s", "");
                     String color = style.substring(style.indexOf("color:") + 1);
                     color = color.substring(0, color.indexOf(";"));

                     frame.color = Utils.htmlColorToColor(color);
                    }
                }
                break;
            case "ul":
                //if this is the first <ul>, we add an empty line
                newLineLayout.setConditionalNewLine(frame.indent == 0 ? 2 : 1);
                frame.indent++;
                break;
            case "li":
                frame.bulletPoint = true;
                newLineLayout.setConditionalNewLine(1);
                break;
        }
        if (node.nodeName().equals("#text")) {
            htmlContentToBlocks(htmlToText(node.outerHtml()),
                    laidoutContent, xPos, frame, newLineLayout);
        }

        //need to use the nodes copy here as (for some reason) the "normal" child's first
        //#text node contains a newline character in front of it.
        final List<Node> childNodes = node.childNodesCopy();
        for (int i = 0; i < childNodes.size(); ++i) {
            final Node aNode = childNodes.get(i);
            final Node aPredecessorNode = i > 0 ? childNodes.get(i - 1) : null;
            traverseHTMLLayout(laidoutContent, xPos, frame, newLineLayout, aPredecessorNode, aNode);
        }
    }

    private void plainContentToBlocks(String aContent, LaidoutContent laidoutContent) {
        final String cleanedContent = filterPDFContent(aContent.replace("\n", " \n ").replace("\\s+", " "));
        final LinkedList<String> words = new LinkedList<>(Arrays.asList(cleanedContent.split("[ ]+")));

        contentToBlocks(words, laidoutContent, new Pos(), new LayoutFrame(), new NewLineLayout());
    }

    private void htmlContentToBlocks(String aContent, LaidoutContent laidoutContent,
            Pos xPos, LayoutFrame frame, NewLineLayout newLineLayout) {
        final String cleanedContent = aContent.replace("\n", " ").replace("\\s+", " ");
        final LinkedList<String> words = new LinkedList<>(Arrays.asList(cleanedContent.split("[ ]+")));

        //don't ommit the last space character!
        if (cleanedContent.endsWith(" ")) {
            words.add("");
        }

        contentToBlocks(words, laidoutContent, xPos, frame, newLineLayout);
    }

    private void contentToBlocks(List<String> words, LaidoutContent laidoutContent,
            Pos xPos, LayoutFrame frame, NewLineLayout newLineLayout) {

        applyNewLineLayout(newLineLayout, laidoutContent, xPos, frame);

        //handle indent
        if (xPos.pos == 0) {
            final float indent = INDENT_WIDTH * frame.indent;
            xPos.pos += indent;
        }

        float textSpaceWidth = getTextSpaceWidth() - xPos.pos;

        while (!words.isEmpty()) {
            final StringBuilder contentBuilder = new StringBuilder();
            final LaidoutContentBlock block = new LaidoutContentBlock(frame);

            //reset bullet points after use!
            frame.bulletPoint = false;

            String lastWord = null;
            while (block.getWidth() <= textSpaceWidth
                    && !words.isEmpty()) {
                if (lastWord != null) {
                    contentBuilder.append(" ");
                }
                lastWord = words.remove(0);
                if (lastWord.equals("\n")) {
                    break;
                }
                contentBuilder.append(lastWord);
                block.setContent(contentBuilder.toString());
            }

            final float textSize = block.getWidth();
            boolean newLine = false;

            xPos.pos += textSize;
            if (textSize > textSpaceWidth
                    && lastWord != null
                    && !lastWord.equals("\n")) {
                words.add(0, lastWord); //add last word again
                contentBuilder.delete(contentBuilder.length() - lastWord.length(), contentBuilder.length());

                newLine = true;
            }

            if (lastWord != null && lastWord.equals("\n")) {
                newLine = true;
            }

            String line = contentBuilder.toString();
            //if we have an empty line but still there are words left, then
            //the word with index 0 is just too long to fit into the given width
            //so we split it into two here, or if the word already just consists
            //of one char then we have to give it up.
            if (line.trim().isEmpty()
                    && (lastWord == null || !lastWord.equals("\n"))
                    && !words.isEmpty()
                    && textSpaceWidth == getTextSpaceWidth()) {
                final String problematicWord = words.remove(0);
                if (problematicWord.length() <= 1) {
                    //give up - not even one char will fit, so we just
                    //add it even if it won't fit. This will lead to layout
                    //problems but cells with just one char fitting in are
                    //problematic by themselves.
                    line = problematicWord;
                } else {
                    //cut into two
                    words.add(0, problematicWord.substring(problematicWord.length() / 2));
                    words.add(0, problematicWord.substring(0, problematicWord.length() / 2));
                    continue;
                }
            }

            final LaidoutContentRow aRow = laidoutContent.getCurrentRow();
            block.setContent(line);
            aRow.addBlock(block);

            if (newLine) {
                laidoutContent.addRow();
                xPos.pos = INDENT_WIDTH * frame.indent;
            }

            //reset text space with
            textSpaceWidth = getTextSpaceWidth() - xPos.pos;
        }
    }

    private void applyNewLineLayout(NewLineLayout newLineLayout, LaidoutContent laidoutContent, Pos xPos, LayoutFrame frame) {
        //check if we need to use a new row
        while (newLineLayout.newLines > 0) {
            if (newLineLayout.newLines > 0) {
                newLineLayout.newLines--;
            } else {
                newLineLayout.conditionalNewlines--;
            }
            xPos.pos = 0;
            laidoutContent.addRow(frame);
        }

        if (newLineLayout.conditionalNewlines > 0) {
            int emptyLines = 0;
            if (laidoutContent.getNumRows() >= newLineLayout.conditionalNewlines) {
                for (int i = 1; i <= newLineLayout.conditionalNewlines; ++i) {
                    if (laidoutContent.rows.get(laidoutContent.getNumRows() - i).isEmpty()) {
                        emptyLines++;
                    }
                }
            }

            if (emptyLines < newLineLayout.conditionalNewlines) {
                for (int i = 0; i < newLineLayout.conditionalNewlines - emptyLines; ++i) {
                    xPos.pos = 0;
                    laidoutContent.addRow(frame);
                }
            }

            //if we have conditional lines xPos has to be 0 afterwards!
            xPos.pos = 0;
        }

        //clear conditional new lines!
        newLineLayout.conditionalNewlines = 0;
    }

    void render(PDPageContentStream stream, float x, float y, int laidOutRowIndexFrom,
            int laidOutRowIndexTo, float rowHeight, boolean pageBreakBefore) throws IOException {
        renderBackground(stream, x, y, rowHeight);
        renderBorders(stream, x, y, rowHeight, pageBreakBefore);
        renderText(stream, x, y, laidOutRowIndexFrom, laidOutRowIndexTo);
    }

    private void renderBackground(PDPageContentStream stream, float x, float y, float rowHeight) throws IOException {
        final Color colorBackground = getBackgroundColor();
        if (colorBackground != null && colorBackground.getAlpha() > 0) {
            stream.setNonStrokingColor(colorBackground);
            stream.setLineWidth(0);
            stream.addRect(x + getBorderLeft().getLineWidth() / 2f, y - rowHeight + getBorderBottom().getLineWidth() / 2f,
                    getWidth() - (getBorderLeft().getLineWidth() + getBorderRight().getLineWidth()) / 2f,
                    rowHeight - (getBorderBottom().getLineWidth() + getBorderTop().getLineWidth()) / 2f
            );
            stream.fill();
        }
    }

    private void renderBorders(PDPageContentStream stream, float x, float y, float rowHeight, boolean pageBreakBefore) throws IOException {
        //only draw top border if we are the top most cell
        if (this.row.getIndex() == 0 || pageBreakBefore) {
            this.getBorderTop().render(stream, x, y, x + getWidth(), y);
        }
        //only draw left border if we are the left most cell
        if (this.index == 0) {
            this.getBorderLeft().render(stream, x, y - rowHeight, x, y);
        }
        //so only if we don't have a custom border right but
        //our neighbor to the right has one then we use
        //their border to draw the right border
        PDFTableBorder rightBorder = getBorderRight();
        if (!hasBorderRight() && this.index + 1 < row.cells.size() && row.getCell(index + 1) != null && row.getCell(index + 1).hasBorderLeft()) {
            rightBorder = row.getCell(index + 1).getBorderLeft();
        }
        rightBorder.render(stream, x + getWidth(), y - rowHeight, x + getWidth(), y);
        //same goes for the bottom border
        float lastWidth = x;
        for (int i = 0; i < this.getSize(); ++i) {
            PDFTableBorder bottomBorder = getBorderBottom();
            if (!hasBorderBottom() && this.row.getIndex() + 1 < table.getRows()) {
                //                        && rows.get(this.row.index + 1).getCell(index + i) != null
                //                        && rows.get(this.row.index + 1).getCell(index + i).hasBorderTop()) {
                final PDFTableRow bottomRow = table.getRow(row.getIndex() + 1);
                int bottomRowColIndex = index;
                while (bottomRow.getCell(bottomRowColIndex) == null && bottomRowColIndex > 0) {
                    bottomRowColIndex--;
                }
                if (bottomRow.getCell(bottomRowColIndex) != null) {
                    bottomBorder = bottomRow.getCell(bottomRowColIndex).getBorderTop();
                }
            }
            bottomBorder.render(stream, lastWidth, y - rowHeight, lastWidth + getWidth(i), y - rowHeight);
            lastWidth += getWidth(i);
        }
    }

    private void renderText(PDPageContentStream stream, float x, float y,
            int laidOutRowIndexFrom, int laidOutRowIndexTo) throws IOException {

        final Align currentAlign = align == null ? table.getColumn(this.index).getAlign() : align;
        if (getTextType() == TextType.HTML && currentAlign == Align.RIGHT) {
            throw new UnsupportedOperationException("Can't use align right with HTML content");
        }

        float offsetX = 0;
        float offsetY = 0;
        for (int rowIdx = laidOutRowIndexFrom; rowIdx < laidOutRowIndexTo; ++rowIdx) {
            final LaidoutContentRow aRow = laidoutContent.getRow(rowIdx);

            //we shift the row to the right, when it is right aligned
            final float rowShiftX = currentAlign == Align.RIGHT
                    ? getTextSpaceWidth() - aRow.getWidth()
                    : 0;

            //this centeres the text within the row
            final float yOffsetHalf = (aRow.getMaxHeight() - aRow.getMaxFontCapHeight()) / 2f;

            //offset to baseline
            offsetY -= (aRow.getMaxFontCapHeight() + yOffsetHalf);
            for (int i = 0; i < aRow.getBlocks().size(); ++i) {
                LaidoutContentBlock block = aRow.getBlocks().get(i);

                //indent only works on the first block ofc
                if (i == 0) {
                    if (currentAlign == Align.LEFT) {
                        offsetX += block.getIndent() * INDENT_WIDTH;
                    } else {
                        offsetX -= block.getIndent() * INDENT_WIDTH;
                    }

                    //add bullet point graphics if neccessary
                    if (block.isBulletPoint()) {
                        final float bulletSize = block.getFontCapHeight() * 0.7f;
                        float bulletY = y - getPaddingTop() + offsetY + block.getFontCapHeight() / 2f ;
                        float bulletX = x + getPaddingLeft() + offsetX + rowShiftX - (bulletSize / 2f + 1 * PDFUtils.MM_TO_POINTS_72DPI);

                        stream.setNonStrokingColor(Color.BLACK);
                        stream.setStrokingColor(Color.BLACK);

                        BulletPoint.getForIndent(block.getIndent()).draw(stream,
                                bulletX,
                                bulletY,
                                bulletSize);
                    }
                }

                stream.setNonStrokingColor(block.getFontColor());
                stream.setFont(block.getFont(), block.getFontSize());

                final float leading = Utils.getFontHeight(block.getFont(), block.getFontSize())
                        * (1.0f + getLineSpacingFactor());

                stream.beginText();
                stream.newLineAtOffset(
                        x + getPaddingLeft() + offsetX + rowShiftX,
                        y - getPaddingTop() + offsetY
                );
                stream.setLeading(leading);
                stream.showText(block.getContent());
                stream.endText();

                if (block.getUnderline()) {
                    float tx = x + getPaddingLeft() + offsetX + rowShiftX;
                    float ty = y - getPaddingTop() + offsetY;

                    float stringWidth = block.getFontSize() * block.getFont().getStringWidth(block.getContent()) / 1000;
                    float lineEndPoint = tx + stringWidth;

                    stream.setLineWidth(1);
                    stream.moveTo(tx, ty - 2);
                    stream.lineTo(lineEndPoint, ty - 2);
                    stream.stroke();
                }

                offsetX += block.getWidth();
            }
            offsetX = 0;

            //reset the offset to the baseline
            offsetY += (aRow.getMaxFontCapHeight() + yOffsetHalf);

            //subtract the line's full height
            offsetY -= aRow.getMaxHeight();
        }

    }

    /**
     * decodes all special entities from html to plain text
     * and also does not trim the text
     *
     * @param html the html
     * @return returns decoded html
     */
    private String htmlToText(String html) {
        StringBuilder sb = new StringBuilder();
        sb.append(Jsoup.parse(html).text());

        //add possibly removed " " from both sides
        if (!html.isEmpty()) {
            for (int i = 0; i < html.length() && html.charAt(i) == ' '; ++i) {
                sb.insert(0, ' ');
            }
            for (int i = html.length() - 1; i >= 0 && html.charAt(i) == ' '; --i) {
                sb.append(' ');
            }
        }
        return filterPDFContent(sb.toString());
    }

    /**
     * filters out all characters for which the current font
     * does not have characters for and therefore would throw
     * an exception when rendering the content.
     *
     * @param content the content to be filtered
     * @return the filtered content
     */
    private String filterPDFContent(String content) {
        //we only need to filter decendents of PDSimpleFont
        //as they are the only ones not capable of rendering
        //all unicode chars. The PDType0Fonts won't throw
        //an exception even if they don't have the char
        //but just utter a warning.
        final PDFont currentFont = getFont();
        if (currentFont instanceof PDSimpleFont) {
            final StringBuilder sb = new StringBuilder();
            final PDSimpleFont simpleFont = (PDSimpleFont) currentFont;
            for (char c : content.toCharArray()) {
                final String name = simpleFont.getGlyphList().codePointToName(c);
                if (simpleFont.getEncoding().contains(name)) {
                    sb.append(c);
                }
            }
            return sb.toString();
        }
        return content;
    }

    class LaidoutContent {
        private final List<LaidoutContentRow> rows = new ArrayList<>();

        public LaidoutContent() {
            this.rows.add(new LaidoutContentRow());
        }

        public LaidoutContentRow getCurrentRow() {
            return rows.get(rows.size() - 1);
        }

        public void addRow() {
            this.rows.add(new LaidoutContentRow());
        }

        public void addRow(LayoutFrame frame) {
            final LaidoutContentRow laidoutContentRow = new LaidoutContentRow();
            laidoutContentRow.addBlock(new LaidoutContentBlock(frame));
            this.rows.add(laidoutContentRow);
        }

        public float getHeight() {
            return rows.stream()
                    .map(row -> row.getMaxHeight())
                    .reduce((h1, h2) -> h1 + h2)
                    .orElse(0f);
        }

        public float getHeight(int startRow, int endRow) {
            float result = 0;
            for (int i = startRow; i < endRow; ++i) {
                result += rows.get(i).getMaxHeight();
            }
            return result;
        }

        private LaidoutContentRow getRow(int idx) {
            return rows.get(idx);
        }

        int getNumRows() {
            return rows.size();
        }

        private void trim() {
            rows.forEach(row -> row.trim());
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Content {\n");
            rows.forEach(row -> sb.append("\t").append(row.toString()).append("\n"));
            sb.append("}\n");
            return sb.toString();
        }

    }

    class LaidoutContentRow {

        private final List<LaidoutContentBlock> blocks = new ArrayList<>();

        public void addBlock(LaidoutContentBlock block) {
            this.blocks.add(block);
        }

        public LaidoutContentBlock getLastBlock() {
            if (this.blocks.isEmpty()) {
                return null;
            }
            return this.blocks.get(this.blocks.size() - 1);
        }

        public List<LaidoutContentBlock> getBlocks() {
            return blocks;
        }

        /**
         * returns true if there are no blocks or if this
         * row contains of only empty blocks
         * @return
         */
        public boolean isEmpty() {
            return this.blocks.isEmpty()
                    || this.blocks.stream().allMatch(block -> block.getContent().isEmpty());
        }

        /**
         * returns the max height of all contained blocks
         * @return
         */
        public float getMaxHeight() {
            return blocks.stream()
                    .map(LaidoutContentBlock::getHeight)
                    .max(Float::compare)
                    .orElse(0f);
        }

        /**
         * returns all font's max height value for this
         * row
         * @return
         */
        public float getMaxFontHeight() {
            return blocks.stream()
                    .map(LaidoutContentBlock::getFontHeight)
                    .max(Float::compare)
                    .orElse(0f);
        }

        /**
         * returns all font's max cap height value for this
         * row
         * @return
         */
        public float getMaxFontCapHeight() {
            return blocks.stream()
                    .map(LaidoutContentBlock::getFontCapHeight)
                    .max(Float::compare)
                    .orElse(0f);
        }

        /**
         * returns the max height of all contained blocks
         * @return
         */
        public float getWidth() {
            return blocks.stream()
                    .map(LaidoutContentBlock::getWidth)
                    .reduce((f1, f2) -> f1 + f2)
                    .orElse(0f);
        }

        private void trim() {
            //remove empty blocks at the end of the row, but NOT if the
            //row only consists of one empty block (because we need its
            //height to correctly render the text)
            while (blocks.size() > 1
                    && blocks.get(blocks.size() - 1).getContent().trim().isEmpty()) {
                blocks.remove(blocks.size() - 1);
            }

            //remove empty block at the beginning of the row if there are
            //more blocks
            while (blocks.size() > 1
                    && blocks.get(0).getContent().isEmpty()) {
                blocks.remove(0);
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < blocks.size(); ++i) {
                if (i > 0) {
                    sb.append(", ");
                }
                final LaidoutContentBlock block = blocks.get(i);
                sb.append(block.toString());
            }
            return sb.toString();
        }
    }

    class LaidoutContentBlock {
        private String content = "";

        private boolean bulletPoint = false;
        private PDFont font = null; //not set
        private Integer fontSize = null; //not set
        private Color fontColor = null; //not set
        private int indent = 0;
        private Boolean underline = false;

        public LaidoutContentBlock(LayoutFrame fromFrame) {
            this.indent = fromFrame.indent;
            this.underline = fromFrame.underline;

            this.bulletPoint = fromFrame.bulletPoint;
            if (fromFrame.bold != null
                    || fromFrame.italic != null) {
                Set<FontModifier> modifiers = new HashSet<>();
                if (fromFrame.bold != null && fromFrame.bold) {
                    modifiers.add(FontModifier.BOLD);
                }
                if (fromFrame.italic != null && fromFrame.italic) {
                    modifiers.add(FontModifier.ITALIC);
                }
                this.font = PDFUtils.modifyFont(getFont(), modifiers);
            }

            this.fontColor = fromFrame.color;

            if (fromFrame.htmlSize != null) {
                this.fontSize = Math.max(1, PDFTableCell.this.getFontSize() + (fromFrame.htmlSize - 4) * 2);
            }
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public void setFont(PDFont font) {
            this.font = font;
        }

        public void setFontSize(Integer fontSize) {
            this.fontSize = fontSize;
        }

        public void setFontColor(Color fontColor) {
            this.fontColor = fontColor;
        }

        public PDFont getFont() {
            return this.font == null
                    ? PDFTableCell.this.getFont()
                    : this.font;
        }

        public int getFontSize() {
            return this.fontSize == null
                    ? PDFTableCell.this.getFontSize()
                    : this.fontSize;
        }

        public Color getFontColor() {
            return this.fontColor == null
                    ? PDFTableCell.this.getFontColor()
                    : this.fontColor;
        }

        public Boolean getUnderline() {
            return this.underline == null
                    ? PDFTableCell.this.getUnderline()
                    : this.underline;
        }

        public int getIndent() {
            return indent;
        }

        public boolean isBulletPoint() {
            return bulletPoint;
        }

        public float getHeight() {
            return getFontHeight() * (1.0f + getLineSpacingFactor());
        }

        public float getFontHeight() {
            return Utils.getFontHeight(getFont(), getFontSize());
        }

        public float getFontCapHeight() {
            return (getFont().getFontDescriptor().getCapHeight() / 1000f) * getFontSize();
        }

        public float getWidth() {
            return getWidth(getContent());
        }

        public float getWidth(String text) {
            return Utils.measureTextSize(getFont(), getFontSize(), text);
        }

        @Override
        public String toString() {
            return "[" + this.content + "]";
        }

    }

    private static class Pos {
        private float pos;
    }

    private static class LayoutFrame {
        private int indent = 0;
        private boolean bulletPoint = false;
        private Color color = null;
        private Integer htmlSize = null;
        private Boolean bold = null;
        private Boolean italic = null;
        private Boolean underline = null;

        public LayoutFrame() {
        }

        public LayoutFrame(LayoutFrame frame) {
            this.indent = frame.indent;
            this.bulletPoint = frame.bulletPoint;
            this.color = frame.color;
            this.htmlSize = frame.htmlSize;
            this.bold = frame.bold;
            this.italic = frame.italic;
            this.underline = frame.underline;
        }

    }

    private static class NewLineLayout {
        private int newLines = 0;
        private int conditionalNewlines = 0;

        private void setConditionalNewLine(int amount) {
            this.conditionalNewlines = Math.max(this.conditionalNewlines, amount);
        }
    }

}
