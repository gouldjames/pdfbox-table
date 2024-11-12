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
import java.awt.Desktop;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.junit.Test;

public class PDFTableTest {

    public PDFTableTest() {
    }

//    @Test
    public void jSoupTest() {
        final String aContent = "<div>Hello this is some <b><i>very</i> Bold</b> text!</div>";

        final Document document = Jsoup.parse(aContent);

        List<Node> childNodes = document.body().childNodes();

        //search all text nodes!
        final Stack<Node> nodeStack = new Stack<>();
        final List<Node> textNodes = new LinkedList<>();
        nodeStack.add(document.body());

        while (!nodeStack.isEmpty()) {
            Node node = nodeStack.pop();
            if (node.nodeName().equals("#text")) {
                textNodes.add(node);
            }
            final List<Node> childs = node.childNodesCopy();
            Collections.reverse(childs);
            childs.forEach(nodeStack::push);
        }

        textNodes.forEach(node -> System.out.println(node.nodeName() + " => \"" + node.outerHtml() + "\""));
////        recursive(document.body(), 0);

        Node node0 = textNodes.get(0);
        System.out.println(System.identityHashCode(node0));

//        System.out.println(node0);
    }

    @Test
    public void layoutTest3() throws Exception {
        File targetFile = new File("D:/Temp/layout3.pdf");
        PDDocument doc = new PDDocument();
        try {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            final float fontHeight = Utils.getFontHeight(PDType1Font.HELVETICA, 10);
            final float fontHeight2 = (PDType1Font.HELVETICA.getFontDescriptor().getFontBoundingBox().getHeight() / 1000f) * 10f;

            System.out.println(fontHeight + " VS " + fontHeight2);
            System.out.println(PDType1Font.HELVETICA.getFontDescriptor().getAscent() + " VS " + PDType1Font.HELVETICA.getFontDescriptor().getFontBoundingBox().getHeight());

            final PDPageContentStream stream = new PDPageContentStream(doc, page);
            stream.beginText();
            stream.newLineAtOffset(10 * PDFUtils.MM_TO_POINTS_72DPI, 10 * PDFUtils.MM_TO_POINTS_72DPI);
            stream.setFont(PDType1Font.HELVETICA, 10);
            stream.showText("Heggo");
            stream.endText();

            stream.beginText();
            stream.newLineAtOffset(30 * PDFUtils.MM_TO_POINTS_72DPI, 10 * PDFUtils.MM_TO_POINTS_72DPI);
            stream.setFont(PDType1Font.HELVETICA, 12);
            stream.showText("World");
            stream.endText();

            stream.setLineWidth(1);
            stream.setStrokingColor(Color.BLACK);
            stream.moveTo(10 * PDFUtils.MM_TO_POINTS_72DPI, 10 * PDFUtils.MM_TO_POINTS_72DPI);
            stream.lineTo(50 * PDFUtils.MM_TO_POINTS_72DPI, 10 * PDFUtils.MM_TO_POINTS_72DPI);
            stream.stroke();

            stream.close();

            FileOutputStream fOut = new FileOutputStream(targetFile);
            doc.save(fOut);
            fOut.close();
        } finally {
            try {
                doc.close();
            } catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        }
    }

//    @Test
    public void layoutTest2() throws IOException {
        final File targetFile = new File("D:/Temp/table_html.pdf");
        final String simpleText = "Hello this is some <b><i>very</i> Bold</b> text!";
        final String complexText = "Jemand musste <font size=\"7\">Jogef K.</font> verleumdet <br/><font size=\"6\">haben</font>, denn <font size=\"5\">ohne</font> dass er etwas <i>Böses</i> getan hätte, wurde eR eines Morgens verhaftet. "
                + "<div><b>»Wie ein Hund!«</b> sagte er, es war,</div> als sollte die <i>Scham</i> ihn überleben. Als Gregor Samsa eines Morgens aus unruhigen Träumen erwachte, "
                + "fand er sich in seinem Bett zu einem ungeheueren Ungeziefer verwandelt. Und es war ihnen wie eine Bestätigung ihrer neuen Träume und guten Absichten, "
                + "als am Ziele ihrer Fahrt die Tochter als erste sich erhob und ihren jungen Körper dehnte. »Es ist ein eigentümlicher Apparat«, sagte der Offizier zu dem "
                + "Forschungsreisenden und überblickte mit einem gewissermaßen bewundernden Blick den ihm doch wohlbekannten Apparat. Sie hätten noch ins Boot springen können, "
                + "aber der Reisende hob ein schweres, geknotetes Tau vom Boden, drohte ihnen damit und hielt sie dadurch von dem Sprunge ab. In den letzten Jahrzehnten ist das "
                + "Interesse an Hungerkünstlern sehr zurückgegangen. Aber sie überwanden sich, umdrängten den Käfig und wollten sich gar nicht fortrühren. Jemand musste Josef K. "
                + "verleumdet haben, denn ohne dass er etwas <i>Böses</i> getan hätte, wurde er eines Morgens verhaftet. »Wie ein Hund!« sagte er, es war, als sollte die Scham ihn "
                + "überleben. Als Gregor Samsa eines Morgens aus unruhigen Träumen erwachte, fand er sich in seinem Bett zu einem ungeheueren Ungeziefer verwandelt. Und es "
                + "war ihnen wie eine Bestätigung ihrer neuen <i>Träume</i> und <b>guten Absichten</b>, als am Ziele ihrer Fahrt die Tochter als erste sich erhob und ihren jungen Körper "
                + "dehnte. »Es ist ein eigentümlicher Apparat«, sagte der Offizier zu dem Forschungsreisenden und überblickte mit einem gewissermaßen bewundernden Blick den"
                + " ihm doch wohlbekannten Apparat. Sie hätten noch ins Boot springen können, aber der Reisende hob ein schweres, geknotetes Tau vom Boden, drohte ihnen damit "
                + "und hielt sie dadurch von dem Sprunge ab. In den letzten Jahrzehnten ist das Interesse an Hungerkünstlern sehr zurückgegangen. Aber sie überwanden sich, "
                + "umdrängten den Käfig und wollten sich gar nicht fortrühren. Jemand musste <b>Josef K.</b> verleumdet haben, denn ohne dass er etwas Böses getan hätte, wurde er eines"
                + " Morgens verhaftet. »Wie ein Hund!« sagte er, es war, <b>als sollte die Scham</b> ihn überleben. Als Gregor Samsa eines Morgens aus unruhigen Träumen erwachte, fand "
                + "er sich in seinem Bett zu einem ungeheueren Ungeziefer verwandelt. Und es war ihnen wie eine Bestätigung ihrer neuen Träume und guten Absichten, als am Ziele "
                + "ihrer Fahrt die Tochter als erste sich erhob und ihren jungen Körper dehnte. »Es ist ein eigentümlicher Apparat«, sagte der Offizier zu dem Forschungsreisenden "
                + "und überblickte mit einem gewissermaßen bewundernden Blick den ihm doch wohlbekannten Apparat. Sie hätten noch ins Boot springen können, aber der Reisende hob "
                + "ein schweres, geknotetes Tau vom Boden, drohte ihnen damit und hielt sie dadurch von <b>dem Sprunge</b> ab. In den letzten Jahrzehnten ist das Interesse an Hungerkünstlern"
                + " sehr zurückgegangen. Aber sie überwanden sich, umdrängten den Käfig und wollten sich gar nicht fortrühren. Jemand musste Josef K. verleumdet haben, denn ohne dass er"
                + " etwas Böses getan hätte, wurde er eines Morgens verhaftet. <b>»Wie ein Hund!«</b> sagte er, es war, als sollte die Scham ihn überleben. Als Gregor Samsa eines Morgens aus"
                + " unruhigen Träumen erwachte, fand er sich in seinem <b>Bett zu einem ungeheueren Ungeziefer verwandelt</b>. Und es war ihnen wie eine Bestätigung ihrer neuen Träume und guten "
                + "Absichten, als am Ziele ihrer Fahrt die Tochter als erste sich erhob und ihren jungen Körper dehnte. <b>»Es ist ein eigentümlicher <i>Apparat</i>«</b>, sagte der Offizier zu dem "
                + "Forschungsreisenden und überblickte mit einem gewissermaßen bewundernden Blick den ihm doch wohlbekannten Apparat. Sie hätten noch ins Boot springen können, aber der"
                + " Reisende hob ein schweres, geknotetes Tau vom Boden, drohte ihnen damit und hielt sie dadurch von dem Sprunge ab. In den letzten Jahrzehnten ist das Interesse an "
                + "Hungerkünstlern sehr zurückgegangen. Aber sie überwanden sich, umdrängten den <b>Käfig und wollten sich gar nicht fortrühren</b>. Jemand musste Josef K. verleumdet haben, denn"
                + " ohne dass er etwas Böses getan hätte, wurde er eines Morgens verhaftet. »Wie ein Hund!« sagte er, es war, als sollte die Scham ihn überleben. Als Gregor Samsa eines "
                + "Morgens aus unruhigen Träumen erwachte, fand er sich in <b>seinem Bett zu einem ungeheueren Ungeziefer verwandelt</b>. Und es war ihnen wie eine Bestätigung ihrer neuen Träume"
                + " und guten Absichten, als am Ziele ihrer Fahrt die Tochter als erste sich erhob und ihren jungen Körper dehnte. »<i>Es ist ein eigentümlicher Apparat</i>«, sagte der Offizier"
                + " zu dem Forschungsreisenden und überblickte mit einem gewissermaßen bewundernden Blick den ihm doch wohlbekannten Apparat. Sie hätten noch ins Boot springen können, aber "
                + "der Reisende hob ein schweres, geknotetes Tau vom Boden, drohte ihnen damit und hielt sie dadurch von dem Sprunge ab. In den letzten Jahrzehnten ist das Interesse an Hungerkünstlern sehr zurückgegangen. Aber sie überwanden sich, umdrängten den Käfig und wollten sich gar nicht fortrühren. Jemand musste Josef K. verleumdet haben, denn ohne dass er etwas Böses getan hätte, wurde er eines Morgens verhaftet. »Wie ein Hund!« sagte er, es war, als sollte die Scham ihn überleben. Als Gregor Samsa eines Morgens aus unruhigen Träumen erwachte, fand er sich in seinem Bett zu einem ungeheueren Ungeziefer verwandelt. Und es war ihnen wie eine Bestätigung ihrer neuen Träume und guten Absichten, als am Ziele ihrer Fahrt die Tochter als erste sich erhob und ihren jungen Körper dehnte. »Es ist ein eigentümlicher Apparat«, sagte der Offizier zu dem Forschungsreisenden und überblickte mit einem gewissermaßen bewundernden Blick den ihm doch wohlbekannten Apparat. Sie hätten noch ins Boot springen können, aber der Reisende hob ein schweres, geknotetes Tau vom Boden, drohte ihnen damit und hielt sie dadurch von dem Sprunge ab. In den letzten Jahrzehnten ist das Interesse an Hungerkünstlern sehr zurückgegangen. Aber sie überwanden sich, umdrängten den Käfig und wollten sich gar nicht fortrühren. Jemand musste Josef K. verleumdet haben, denn ohne dass er etwas Böses getan hätte, wurde er eines Morgens verhaftet. »Wie ein Hund!« sagte er, es war, als sollte die Scham ihn überleben. Als Gregor Samsa eines Morgens aus unruhigen Träumen erwachte, fand er sich in seinem Bett zu einem ungeheueren Ungeziefer verwandelt. Und es war ihnen wie eine Bestätigung ihrer neuen Träume und guten Absichten, als am Ziele ihrer Fahrt die Tochter als erste sich erhob und ihren jungen Körper dehnte. »Es ist ein eigentümlicher Apparat«, sagte der Offizier zu dem Forschungsreisenden und überblickte mit einem gewissermaßen bewundernden Blick den ihm doch wohlbekannten Apparat. Sie hätten noch ins Boot springen können, aber der Reisende hob ein schweres, geknotetes Tau vom Boden, drohte ihnen damit und hielt sie dadurch von dem Sprunge ab. In den letzten Jahrzehnten ist das Interesse an Hungerkünstlern sehr zurückgegangen. Aber sie überwanden sich, umdrängten den Käfig und wollten sich gar nicht fortrühren.Jemand musste Josef K. verleumdet haben, denn ohne dass er etwas Böses getan hätte, wurde er eines Morgens verhaftet. »Wie ein Hund!« sagte er, es war, als sollte die Scham ihn überleben. Als Gregor Samsa eines Morgens aus unruhigen Träumen erwachte, fand er sich in seinem Bett zu einem ungeheueren Ungeziefer verwandelt. Und es war ihnen wie eine Bestätigung ihrer neuen Träume und guten Absichten, als am Ziele ihrer Fahrt die Tochter als erste sich erhob und ihren jungen Körper dehnte. »Es ist ein eigentümlicher Apparat«, sagte der Offizier zu dem Forschungsreisenden und überblickte mit einem gewissermaßen bewundernden Blick den ihm doch wohlbekannten Apparat. Jemand musste Josef K. verleumdet haben, denn ohne dass er etwas Böses getan hätte, wurde er eines Morgens verhaftet. »Wie ein Hund!« sagte er, es war, als sollte die Scham ihn überleben. Als Gregor Samsa eines Morgens aus unruhigen Träumen erwachte, fand er sich in seinem Bett zu einem ungeheueren Ungeziefer verwandelt. Und es war ihnen wie eine Bestätigung ihrer neuen Träume und guten Absichten, als am Ziele ihrer Fahrt die Tochter als erste sich erhob und ihren jungen Körper dehnte. »Es ist ein eigentümlicher Apparat«, sagte der Offizier zu dem Forschungsreisenden und überblickte mit einem gewissermaßen bewundernden Blick den ihm doch wohlbekannten Apparat. Sie hätten noch ins Boot springen können, aber der Reisende hob ein schweres, geknotetes Tau vom Boden, drohte ihnen damit und hielt sie dadurch von dem Sprunge ab. In den letzten Jahrzehnten ist das Interesse an Hungerkünstlern sehr zurückgegangen. Aber sie überwanden sich, umdrängten den Käfig und wollten sich gar nicht fortrühren. Jemand musste Josef K. verleumdet haben, denn ohne dass er etwas Böses getan hätte, wurde er eines Morgens verhaftet. »Wie ein Hund!« sagte er, es war, als sollte die Scham ihn überleben. Als Gregor Samsa eines Morgens aus unruhigen Träumen erwachte, fand er sich in seinem Bett zu einem ungeheueren Ungeziefer verwandelt. Und es war ihnen wie eine Bestätigung ihrer neuen Träume und guten Absichten, als am Ziele ihrer Fahrt die Tochter als erste sich erhob und ihren jungen Körper dehnte. »Es ist ein eigentümlicher Apparat«, sagte der Offizier zu dem Forschungsreisenden und überblickte mit einem gewissermaßen bewundernden Blick den ihm doch wohlbekannten Apparat. Sie hätten noch ins Boot springen können, aber der Reisende hob ein schweres, geknotetes Tau vom Boden, drohte ihnen damit und hielt sie dadurch von dem Sprunge ab. In den letzten Jahrzehnten ist das Interesse an Hungerkünstlern sehr zurückgegangen. Aber sie überwanden sich, umdrängten den Käfig und wollten sich gar nicht fortrühren. Jemand musste Josef K. verleumdet haben, denn ohne dass er etwas Böses getan hätte, wurde er eines Morgens verhaftet. »Wie ein Hund!« sagte er, es war, als sollte die Scham ihn überleben. Als Gregor Samsa eines Morgens aus unruhigen Träumen erwachte, fand er sich in seinem Bett zu einem ungeheueren Ungeziefer verwandelt. Und es war ihnen wie eine Bestätigung ihrer neuen Träume und guten Absichten, als am Ziele ihrer Fahrt die Tochter als erste sich erhob und ihren jungen Körper dehnte. »Es ist ein eigentümlicher Apparat«, sagte der Offizier zu dem Forschungsreisenden und überblickte mit einem gewissermaßen bewundernden Blick den ihm doch wohlbekannten Apparat. Sie hätten noch ins Boot springen können, aber der Reisende hob ein schweres, geknotetes Tau vom Boden, drohte ihnen damit und hielt sie dadurch von dem Sprunge ab. In den letzten Jahrzehnten ist das Interesse an Hungerkünstlern sehr zurückgegangen. Aber sie überwanden sich, umdrängten den Käfig und wollten sich gar nicht fortrühren. Jemand musste Josef K. verleumdet haben, denn ohne dass er etwas Böses getan hätte, wurde er eines Morgens verhaftet. »Wie ein Hund!« sagte er, es war, als sollte die Scham ihn überleben. Als Gregor Samsa eines Morgens aus unruhigen Träumen erwachte, fand er sich in seinem Bett zu einem ungeheueren Ungeziefer verwandelt. Und es war ihnen wie eine Bestätigung ihrer neuen Träume und guten Absichten, als am Ziele ihrer Fahrt die Tochter als erste sich erhob und ihren jungen Körper dehnte. »Es ist ein eigentümlicher Apparat«, sagte der Offizier zu dem Forschungsreisenden und überblickte mit einem gewissermaßen bewundernden Blick den ihm doch wohlbekannten Apparat. Sie hätten noch ins Boot springen können, aber der Reisende hob ein schweres, geknotetes Tau vom Boden, drohte ihnen damit und hielt sie dadurch von dem Sprunge ab. In den letzten Jahrzehnten ist das Interesse an Hungerkünstlern sehr zurückgegangen. Aber sie überwanden sich, umdrängten den Käfig und wollten sich gar nicht fortrühren. Jemand musste Josef K. verleumdet haben, denn ohne dass er etwas Böses getan hätte, wurde er eines Morgens verhaftet. »Wie ein Hund!« sagte er, es war, als sollte die Scham ihn überleben. Als Gregor Samsa eines Morgens aus unruhigen Träumen erwachte, fand er sich in seinem Bett zu einem ungeheueren Ungeziefer verwandelt. Und es war ihnen wie eine Bestätigung ihrer neuen Träume und guten Absichten, als am Ziele ihrer Fahrt die Tochter als erste sich erhob und ihren jungen Körper dehnte. »Es ist ein eigentümlicher Apparat«, sagte der Offizier zu dem Forschungsreisenden und überblickte mit einem gewissermaßen bewundernden Blick den ihm doch wohlbekannten Apparat. Sie hätten noch ins Boot springen können, aber der Reisende hob ein schweres, <b>geknotetes Tau vom Boden, drohte ihnen damit und hielt sie dadurch von dem Sprunge ab</b>. In den letzten Jahrzehnten ist das Interesse an Hungerkünstlern sehr zurückgegangen. Aber sie überwanden sich, umdrängten den Käfig und wollten sich gar nicht fortrühren. Jemand musste Josef K. verleumdet haben, denn ohne dass er etwas Böses getan hätte, wurde er eines Morgens verhaftet. »Wie ein Hund!« sagte er, es war, als sollte die Scham ihn überleben. Als Gregor Samsa eines Morgens aus unruhigen Träumen erwachte, fand er sich in seinem Bett zu einem ungeheueren Ungeziefer verwandelt. Und es war ihnen wie eine Bestätigung ihrer neuen Träume und guten Absichten, als am Ziele ihrer Fahrt die Tochter als erste sich erhob und ihren jungen Körper dehnte. »Es ist ein eigentümlicher Apparat«, sagte der Offizier zu dem Forschungsreisenden und überblickte mit einem gewissermaßen bewundernden Blick den ihm doch wohlbekannten Apparat. Sie hätten noch ins Boot springen können, aber der Reisende hob ein schweres, geknotetes Tau vom Boden, drohte ihnen damit und hielt sie dadurch von dem Sprunge ab. In den letzten Jahrzehnten ist das Interesse an Hungerkünstlern sehr zurückgegangen. Aber sie überwanden sich, umdrängten den Käfig und wollten sich gar nicht fortrühren. Jemand musste Josef K. verleumdet haben, denn ohne dass er etwas Böses getan hätte, wurde er eines Morgens verhaftet. »Wie ein Hund!« sagte er, es war, als sollte die Scham ihn überleben. Als Gregor Samsa eines Morgens aus unruhigen Träumen erwachte, fand er sich in seinem Bett zu einem ungeheueren Ungeziefer verwandelt. Und es war ihnen wie eine Bestätigung ihrer neuen Träume und guten Absichten, als am Ziele ihrer Fahrt die Tochter als erste sich erhob und ihren jungen Körper dehnte. »Es ist ein eigentümlicher Apparat«, sagte der Offizier zu dem Forschungsreisenden und überblickte mit einem gewissermaßen bewundernden Blick den ihm doch wohlbekannten Apparat. Sie hätten noch ins Boot springen können, aber der Reisende hob ein schweres, geknotetes Tau vom Boden, drohte ihnen damit und hielt sie dadurch von dem Sprunge ab. In den letzten Jahrzehnten ist das Interesse an Hungerkünstlern sehr zurückgegangen. Aber sie überwanden sich, umdrängten den Käfig und wollten sich gar nicht fortrühren.Jemand musste Josef K. verleumdet haben, denn ohne dass er etwas Böses getan hätte, wurde er eines Morgens verhaftet. »Wie ein Hund!« sagte er, es war, als sollte die Scham ihn überleben. Als Gregor Samsa eines Morgens aus unruhigen Träumen erwachte, fand er sich in seinem Bett zu einem ungeheueren Ungeziefer verwandelt. Und es war ihnen wie eine Bestätigung ihrer neuen Träume und guten Absichten, als am Ziele ihrer Fahrt die Tochter als erste sich erhob und ihren jungen Körper dehnte. »Es ist ein eigentümlicher Apparat«, sagte der Offizier zu dem Forschungsreisenden und überblickte mit einem gewissermaßen bewundernden Blick den ihm doch wohlbekannten Apparat.";

        final String html2Text = "<div><font size=\"1\">Size XX-Small</font></div>\n"
                + "<div><font size=\"2\">Size X-Small</font></div>\n"
                + "<div><font size=\"3\" color=\"green\">Size Small</font></div>\n"
                + "<div><font size=\"4\" color=\"red\">Size Medium</font></div>\n"
                + "<div><font size=\"5\">Size Large</font></div>\n"
                + "<div><font size=\"6\">Size X-Large</font></div>\n"
                + "<div><font size=\"7\">Size XX-Large</font><br></div>\n"
                + "<blockquote>\n"
                + "   <div>Dies ist</div>\n"
                + "   <div><b>ein dreizeiliger</b></div>\n"
                + "   <div>test!</div>\n"
                + "   <div><br></div>\n"
                + "   <div>Absatz2</div>\n"
                + "   <div><br></div>\n"
                + "</blockquote>\n"
                + "<blockquote>\n"
                + "   <blockquote>\n"
                + "      <div>Absatz3<br></div>\n"
                + "   </blockquote>\n"
                + "</blockquote>\n"
                + "<div><br></div>\n"
                + "<div>Mit Absatz!</div>\n"
                + "<div><br></div>\n"
                + "<div>Bullet Points:</div>\n"
                + "<div>\n"
                + "   <ul>\n"
                + "      <li>Erstens</li>\n"
                + "      <ul>\n"
                + "         <li>Zweitens</li>\n"
                + "      </ul>\n"
                + "      <li>Drittens</li>\n"
                + "      <ul>\n"
                + "         <li>Viertens</li>\n"
                + "         <ul>\n"
                + "            <li>Fünftens<br></li>\n"
                + "         </ul>\n"
                + "      </ul>\n"
                + "   </ul>\n"
                + "   <div>Fertig!<br></div>\n"
                + "</div>";

        final String sth = "<div>Bullet Points:</div>\n"
                + "<div>\n"
                + "   <ul>\n"
                + "      <li>Erstens</li>\n"
                + "   </ul>"
                + "</div>";

        final String bulletPoints = "Test"
                + "   <ul>\n"
                + "      <li>Erstens &gt;&gt;&gt;</li>\n"
                + "      <ul>\n"
                + "         <li>Zweitens</li>\n"
                + "      </ul>\n"
                + "      <li>Drittens</li>\n"
                + "      <ul>\n"
                + "         <li>Viertens</li>\n"
                + "         <ul>\n"
                + "            <li>Fünftens</li>\n"
                + "            <ul>\n"
                + "                <li><b>»Wie ein Hund!«</b> sagte er, es war, als sollte die <b>Scham </b>ihn überleben. Als Gregor Samsa eines Morgens aus</li>\n"
                + "            </ul>\n"
                + "         </ul>\n"
                + "      </ul>\n"
                + "   </ul>\n"
                + "   <div>Fertig!</div>";

        String blockQuotes = ""
                + "<blockquote>\n"
                + "   <div>Dies ist</div>\n"
                + "   <div><b>ein dreizeiliger</b></div>\n"
                + "   <div>test!</div>\n"
                + "   <div><br></div>\n"
                + "   <div>Absatz2</div>\n"
                + "   <div><br></div>\n"
                + "</blockquote>\n"
                + "<blockquote>\n"
                + "   <blockquote>\n"
                + "      <div><b>»Wie ein Hund!«</b> sagte er, es war, als sollte die Scham ihn überleben. Als Gregor Samsa eines Morgens aus Tau vom Boden, drohte ihnen damit und hielt sie dadurch von dem Sprunge ab. In den letzten Jahrzehnten ist das Interesse an</div>\n"
                + "   </blockquote>\n"
                + "</blockquote>";

        String underlined = "<div><u>some underlined text</u> and here some non underlined text.<br>\n"
                + "<u><strong><i>and here it's underlined, bold and italic!</i></strong></u></div>\n"
                + "<div>here is plain text again</div>\n"
                + "<div><u>First line of underlined text<br>\n"
                + "second line</u></div>";

        String colors = "<p><u>Hex Colors</u></p>\n"
                + "<p><span style=\"color:#009688;\">Color</span></p>\n"
                + "<p><u>RGB Format</u></p>\n"
                + "<p><span style=\"color:rgb(255,0,0);\">Color</span></p>\n"
                + "<p>HSL format</p>\n"
                + "<p><span style=\"color:hsl(240,75%,60%);\">Color</span></p>\n"
                + "<p>“Document colors”</p>\n"
                + "<p><span style=\"color:rgb(255,0,0);\">Color</span></p>\n"
                + "<h2><span style=\"color:hsl(60,75%,60%);\">Title and Color</span></h2>\n"
                + "<h3><span style=\"color:hsl(30,75%,60%);\">Title 2 and Color</span></h3>\n"
                + "<ul><li><span style=\"color:#009688;\">c1</span></li>\n"
                + "<li><span style=\"color:rgb(255,0,0);\">c2</span></li>\n"
                + "<li><span style=\"color:hsl(240,75%,60%);\">c3</span></li></ul>\n"
                + "<p>all color points</p>\n"
                + "<ul><li><span style=\"color:hsl(240,75%,60%);\">p1</span></li>\n"
                + "<li><span style=\"color:hsl(240,75%,60%);\">p2</span></li>\n"
                + "<li><span style=\"color:hsl(240,75%,60%);\">p3</span></li></ul>\n"
                + "<p><span style=\"color:hsl(240,75%,60%);\"\n"
                + "<p>&nbsp;</p>\n";


                PDDocument doc = new PDDocument();
        try {
            PDPage firstPage = new PDPage(PDRectangle.A4);
            PDFRenderContext context = new PDFRenderContext(doc, firstPage);

//            final int COL_SOMETHING = 0;
            final int COL_DOCUMENTATION = 0;

            PDFTable reportTable = PDFTable.createWithSomeFixedColumnWidths(PDRectangle.A4.getWidth() - 20 * PDFUtils.MM_TO_POINTS_72DPI,
//                    33 * PDFUtils.MM_TO_POINTS_72DPI,
                    PDFTable.AUTO_DETERMINE_COLUMN_WIDTH
            );
//            PDFTable reportTable = PDFTable.createWithSomeFixedColumnWidths(14 * PDFUtils.MM_TO_POINTS_72DPI,
//                    PDFTable.AUTO_DETERMINE_COLUMN_WIDTH
//                    );
            reportTable.setColumnHeadersMode(PDFTable.ColumnHeadersMode.COLUMN_HEADERS_ON_EVERY_PAGE);

            for (int i = 0; i < reportTable.getColumns(); ++i) {
                reportTable.getColumn(i).setFontSize(11);
            }

//            reportTable.getColumn(COL_SOMETHING).setHeading("Something");
            reportTable.getColumn(COL_DOCUMENTATION).setHeading("Inhalt");

            for (int i = 0; i < 1; ++i) {
                PDFTableRow row = reportTable.addRow();
//                row.getCell(COL_SOMETHING).setContent(complexText);
//                row.getCell(COL_DOCUMENTATION).setContent(underlined);
//                row.getCell(COL_DOCUMENTATION).setContent(html2Text);
                row.getCell(COL_DOCUMENTATION).setContent(colors);
                row.getCell(COL_DOCUMENTATION).setTextType(TextType.HTML);
            }

            reportTable.render(context, 10 * PDFUtils.MM_TO_POINTS_72DPI, PDRectangle.A4.getHeight() - 10 * PDFUtils.MM_TO_POINTS_72DPI);

            final PDFTableCell cell = reportTable.getRow(0).getCell(COL_DOCUMENTATION);
            cell.updateContentLayout();
            System.out.println(cell.getLaidoutContent());

            context.closeAllPages();

            FileOutputStream fOut = new FileOutputStream(targetFile);
            doc.save(fOut);
            fOut.close();
        } finally {
            try {
                doc.close();
            } catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        }

        Desktop.getDesktop().open(targetFile);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }

    }

//    @Test
    public void layoutTest() throws IOException {
        PDDocument doc = new PDDocument();
        try {
            PDPage firstPage = new PDPage(PDRectangle.A4);
            PDFRenderContext context = new PDFRenderContext(doc, firstPage);

            final int COL_DATE = 0;
            final int COL_TIME_FROM = 1;
            final int COL_TIME_TO = 2;
            final int COL_DOCUMENTATION = 3;

            PDFTable reportTable = PDFTable.createWithSomeFixedColumnWidths(PDRectangle.A4.getWidth() - 20 * PDFUtils.MM_TO_POINTS_72DPI,
                    23 * PDFUtils.MM_TO_POINTS_72DPI,
                    14 * PDFUtils.MM_TO_POINTS_72DPI,
                    14 * PDFUtils.MM_TO_POINTS_72DPI,
                    PDFTable.AUTO_DETERMINE_COLUMN_WIDTH
            );
            reportTable.setColumnHeadersMode(PDFTable.ColumnHeadersMode.COLUMN_HEADERS_ON_EVERY_PAGE);

            for (int i = 0; i < reportTable.getColumns(); ++i) {
                reportTable.getColumn(i).setFontSize(11);
            }

            reportTable.getColumn(COL_DATE).setHeading("Datum");
            reportTable.getColumn(COL_TIME_FROM).setHeading("Von");
            reportTable.getColumn(COL_TIME_FROM).setAlign(Align.RIGHT);
            reportTable.getColumn(COL_TIME_TO).setHeading("Bis");
            reportTable.getColumn(COL_TIME_TO).setAlign(Align.RIGHT);
            reportTable.getColumn(COL_DOCUMENTATION).setHeading("Inhalt");
//            reportTable.getColumn(COL_DOCUMENTATION).setAlign(Align.RIGHT);
            reportTable.getColumn(COL_DOCUMENTATION).setAlign(Align.LEFT);

            final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            for (int i = 0; i < 1; ++i) {
                PDFTableRow row = reportTable.addRow();

                row.getCell(COL_DATE).setContent(dateFormat.format(new Date()));
                row.getCell(COL_TIME_FROM).setContent(timeFormat.format(new Date()));
                row.getCell(COL_TIME_TO).setContent(timeFormat.format(new Date()));
                row.getCell(COL_DOCUMENTATION).setContent(i + "Jemand musste Josef K. verleumdet haben, \n\n\ndenn ohne dass er etwas Böses getan hätte, wurde er eines Morgens verhaftet. »Wie ein Hund!« sagte er, es war, als sollte die Scham ihn überleben. Als Gregor Samsa eines Morgens aus unruhigen Träumen erwachte, fand er sich in seinem Bett zu einem ungeheueren Ungeziefer verwandelt. Und es war ihnen wie eine Bestätigung ihrer neuen Träume und guten Absichten, als am Ziele ihrer Fahrt die Tochter als erste sich erhob und ihren jungen Körper dehnte. »Es ist ein eigentümlicher Apparat«, sagte der Offizier zu dem Forschungsreisenden und überblickte mit einem gewissermaßen bewundernden Blick den ihm doch wohlbekannten Apparat. Sie hätten noch ins Boot springen können, aber der Reisende hob ein schweres, geknotetes Tau vom Boden, drohte ihnen damit und hielt sie dadurch von dem Sprunge ab. In den letzten Jahrzehnten ist das Interesse an Hungerkünstlern sehr zurückgegangen. Aber sie überwanden sich, umdrängten den Käfig und wollten sich gar nicht fortrühren. Jemand musste Josef K. verleumdet haben, denn ohne dass er etwas Böses getan hätte, wurde er eines Morgens verhaftet. »Wie ein Hund!« sagte er, es war, als sollte die Scham ihn überleben. Als Gregor Samsa eines Morgens aus unruhigen Träumen erwachte, fand er sich in seinem Bett zu einem ungeheueren Ungeziefer verwandelt. Und es war ihnen wie eine Bestätigung ihrer neuen Träume und guten Absichten, als am Ziele ihrer Fahrt die Tochter als erste sich erhob und ihren jungen Körper dehnte. »Es ist ein eigentümlicher Apparat«, sagte der Offizier zu dem Forschungsreisenden und überblickte mit einem gewissermaßen bewundernden Blick den ihm doch wohlbekannten Apparat. Sie hätten noch ins Boot springen können, aber der Reisende hob ein schweres, geknotetes Tau vom Boden, drohte ihnen damit und hielt sie dadurch von dem Sprunge ab. In den letzten Jahrzehnten ist das Interesse an Hungerkünstlern sehr zurückgegangen. Aber sie überwanden sich, umdrängten den Käfig und wollten sich gar nicht fortrühren. Jemand musste Josef K. verleumdet haben, denn ohne dass er etwas Böses getan hätte, wurde er eines Morgens verhaftet. »Wie ein Hund!« sagte er, es war, als sollte die Scham ihn überleben. Als Gregor Samsa eines Morgens aus unruhigen Träumen erwachte, fand er sich in seinem Bett zu einem ungeheueren Ungeziefer verwandelt. Und es war ihnen wie eine Bestätigung ihrer neuen Träume und guten Absichten, als am Ziele ihrer Fahrt die Tochter als erste sich erhob und ihren jungen Körper dehnte. »Es ist ein eigentümlicher Apparat«, sagte der Offizier zu dem Forschungsreisenden und überblickte mit einem gewissermaßen bewundernden Blick den ihm doch wohlbekannten Apparat. Sie hätten noch ins Boot springen können, aber der Reisende hob ein schweres, geknotetes Tau vom Boden, drohte ihnen damit und hielt sie dadurch von dem Sprunge ab. In den letzten Jahrzehnten ist das Interesse an Hungerkünstlern sehr zurückgegangen. Aber sie überwanden sich, umdrängten den Käfig und wollten sich gar nicht fortrühren. Jemand musste Josef K. verleumdet haben, denn ohne dass er etwas Böses getan hätte, wurde er eines Morgens verhaftet. »Wie ein Hund!« sagte er, es war, als sollte die Scham ihn überleben. Als Gregor Samsa eines Morgens aus unruhigen Träumen erwachte, fand er sich in seinem Bett zu einem ungeheueren Ungeziefer verwandelt. Und es war ihnen wie eine Bestätigung ihrer neuen Träume und guten Absichten, als am Ziele ihrer Fahrt die Tochter als erste sich erhob und ihren jungen Körper dehnte. »Es ist ein eigentümlicher Apparat«, sagte der Offizier zu dem Forschungsreisenden und überblickte mit einem gewissermaßen bewundernden Blick den ihm doch wohlbekannten Apparat. Sie hätten noch ins Boot springen können, aber der Reisende hob ein schweres, geknotetes Tau vom Boden, drohte ihnen damit und hielt sie dadurch von dem Sprunge ab. In den letzten Jahrzehnten ist das Interesse an Hungerkünstlern sehr zurückgegangen. Aber sie überwanden sich, umdrängten den Käfig und wollten sich gar nicht fortrühren. Jemand musste Josef K. verleumdet haben, denn ohne dass er etwas Böses getan hätte, wurde er eines Morgens verhaftet. »Wie ein Hund!« sagte er, es war, als sollte die Scham ihn überleben. Als Gregor Samsa eines Morgens aus unruhigen Träumen erwachte, fand er sich in seinem Bett zu einem ungeheueren Ungeziefer verwandelt. Und es war ihnen wie eine Bestätigung ihrer neuen Träume und guten Absichten, als am Ziele ihrer Fahrt die Tochter als erste sich erhob und ihren jungen Körper dehnte. »Es ist ein eigentümlicher Apparat«, sagte der Offizier zu dem Forschungsreisenden und überblickte mit einem gewissermaßen bewundernden Blick den ihm doch wohlbekannten Apparat. Sie hätten noch ins Boot springen können, aber der Reisende hob ein schweres, geknotetes Tau vom Boden, drohte ihnen damit und hielt sie dadurch von dem Sprunge ab. In den letzten Jahrzehnten ist das Interesse an Hungerkünstlern sehr zurückgegangen. Aber sie überwanden sich, umdrängten den Käfig und wollten sich gar nicht fortrühren. Jemand musste Josef K. verleumdet haben, denn ohne dass er etwas Böses getan hätte, wurde er eines Morgens verhaftet. »Wie ein Hund!« sagte er, es war, als sollte die Scham ihn überleben. Als Gregor Samsa eines Morgens aus unruhigen Träumen erwachte, fand er sich in seinem Bett zu einem ungeheueren Ungeziefer verwandelt. Und es war ihnen wie eine Bestätigung ihrer neuen Träume und guten Absichten, als am Ziele ihrer Fahrt die Tochter als erste sich erhob und ihren jungen Körper dehnte. »Es ist ein eigentümlicher Apparat«, sagte der Offizier zu dem Forschungsreisenden und überblickte mit einem gewissermaßen bewundernden Blick den ihm doch wohlbekannten Apparat. Sie hätten noch ins Boot springen können, aber der Reisende hob ein schweres, geknotetes Tau vom Boden, drohte ihnen damit und hielt sie dadurch von dem Sprunge ab. In den letzten Jahrzehnten ist das Interesse an Hungerkünstlern sehr zurückgegangen. Aber sie überwanden sich, umdrängten den Käfig und wollten sich gar nicht fortrühren. Jemand musste Josef K. verleumdet haben, denn ohne dass er etwas Böses getan hätte, wurde er eines Morgens verhaftet. »Wie ein Hund!« sagte er, es war, als sollte die Scham ihn überleben. Als Gregor Samsa eines Morgens aus unruhigen Träumen erwachte, fand er sich in seinem Bett zu einem ungeheueren Ungeziefer verwandelt. Und es war ihnen wie eine Bestätigung ihrer neuen Träume und guten Absichten, als am Ziele ihrer Fahrt die Tochter als erste sich erhob und ihren jungen Körper dehnte. »Es ist ein eigentümlicher Apparat«, sagte der Offizier zu dem Forschungsreisenden und überblickte mit einem gewissermaßen bewundernden Blick den ihm doch wohlbekannten Apparat. Sie hätten noch ins Boot springen können, aber der Reisende hob ein schweres, geknotetes Tau vom Boden, drohte ihnen damit und hielt sie dadurch von dem Sprunge ab. In den letzten Jahrzehnten ist das Interesse an Hungerkünstlern sehr zurückgegangen. Aber sie überwanden sich, umdrängten den Käfig und wollten sich gar nicht fortrühren.Jemand musste Josef K. verleumdet haben, denn ohne dass er etwas Böses getan hätte, wurde er eines Morgens verhaftet. »Wie ein Hund!« sagte er, es war, als sollte die Scham ihn überleben. Als Gregor Samsa eines Morgens aus unruhigen Träumen erwachte, fand er sich in seinem Bett zu einem ungeheueren Ungeziefer verwandelt. Und es war ihnen wie eine Bestätigung ihrer neuen Träume und guten Absichten, als am Ziele ihrer Fahrt die Tochter als erste sich erhob und ihren jungen Körper dehnte. »Es ist ein eigentümlicher Apparat«, sagte der Offizier zu dem Forschungsreisenden und überblickte mit einem gewissermaßen bewundernden Blick den ihm doch wohlbekannten Apparat. Jemand musste Josef K. verleumdet haben, denn ohne dass er etwas Böses getan hätte, wurde er eines Morgens verhaftet. »Wie ein Hund!« sagte er, es war, als sollte die Scham ihn überleben. Als Gregor Samsa eines Morgens aus unruhigen Träumen erwachte, fand er sich in seinem Bett zu einem ungeheueren Ungeziefer verwandelt. Und es war ihnen wie eine Bestätigung ihrer neuen Träume und guten Absichten, als am Ziele ihrer Fahrt die Tochter als erste sich erhob und ihren jungen Körper dehnte. »Es ist ein eigentümlicher Apparat«, sagte der Offizier zu dem Forschungsreisenden und überblickte mit einem gewissermaßen bewundernden Blick den ihm doch wohlbekannten Apparat. Sie hätten noch ins Boot springen können, aber der Reisende hob ein schweres, geknotetes Tau vom Boden, drohte ihnen damit und hielt sie dadurch von dem Sprunge ab. In den letzten Jahrzehnten ist das Interesse an Hungerkünstlern sehr zurückgegangen. Aber sie überwanden sich, umdrängten den Käfig und wollten sich gar nicht fortrühren. Jemand musste Josef K. verleumdet haben, denn ohne dass er etwas Böses getan hätte, wurde er eines Morgens verhaftet. »Wie ein Hund!« sagte er, es war, als sollte die Scham ihn überleben. Als Gregor Samsa eines Morgens aus unruhigen Träumen erwachte, fand er sich in seinem Bett zu einem ungeheueren Ungeziefer verwandelt. Und es war ihnen wie eine Bestätigung ihrer neuen Träume und guten Absichten, als am Ziele ihrer Fahrt die Tochter als erste sich erhob und ihren jungen Körper dehnte. »Es ist ein eigentümlicher Apparat«, sagte der Offizier zu dem Forschungsreisenden und überblickte mit einem gewissermaßen bewundernden Blick den ihm doch wohlbekannten Apparat. Sie hätten noch ins Boot springen können, aber der Reisende hob ein schweres, geknotetes Tau vom Boden, drohte ihnen damit und hielt sie dadurch von dem Sprunge ab. In den letzten Jahrzehnten ist das Interesse an Hungerkünstlern sehr zurückgegangen. Aber sie überwanden sich, umdrängten den Käfig und wollten sich gar nicht fortrühren. Jemand musste Josef K. verleumdet haben, denn ohne dass er etwas Böses getan hätte, wurde er eines Morgens verhaftet. »Wie ein Hund!« sagte er, es war, als sollte die Scham ihn überleben. Als Gregor Samsa eines Morgens aus unruhigen Träumen erwachte, fand er sich in seinem Bett zu einem ungeheueren Ungeziefer verwandelt. Und es war ihnen wie eine Bestätigung ihrer neuen Träume und guten Absichten, als am Ziele ihrer Fahrt die Tochter als erste sich erhob und ihren jungen Körper dehnte. »Es ist ein eigentümlicher Apparat«, sagte der Offizier zu dem Forschungsreisenden und überblickte mit einem gewissermaßen bewundernden Blick den ihm doch wohlbekannten Apparat. Sie hätten noch ins Boot springen können, aber der Reisende hob ein schweres, geknotetes Tau vom Boden, drohte ihnen damit und hielt sie dadurch von dem Sprunge ab. In den letzten Jahrzehnten ist das Interesse an Hungerkünstlern sehr zurückgegangen. Aber sie überwanden sich, umdrängten den Käfig und wollten sich gar nicht fortrühren. Jemand musste Josef K. verleumdet haben, denn ohne dass er etwas Böses getan hätte, wurde er eines Morgens verhaftet. »Wie ein Hund!« sagte er, es war, als sollte die Scham ihn überleben. Als Gregor Samsa eines Morgens aus unruhigen Träumen erwachte, fand er sich in seinem Bett zu einem ungeheueren Ungeziefer verwandelt. Und es war ihnen wie eine Bestätigung ihrer neuen Träume und guten Absichten, als am Ziele ihrer Fahrt die Tochter als erste sich erhob und ihren jungen Körper dehnte. »Es ist ein eigentümlicher Apparat«, sagte der Offizier zu dem Forschungsreisenden und überblickte mit einem gewissermaßen bewundernden Blick den ihm doch wohlbekannten Apparat. Sie hätten noch ins Boot springen können, aber der Reisende hob ein schweres, geknotetes Tau vom Boden, drohte ihnen damit und hielt sie dadurch von dem Sprunge ab. In den letzten Jahrzehnten ist das Interesse an Hungerkünstlern sehr zurückgegangen. Aber sie überwanden sich, umdrängten den Käfig und wollten sich gar nicht fortrühren. Jemand musste Josef K. verleumdet haben, denn ohne dass er etwas Böses getan hätte, wurde er eines Morgens verhaftet. »Wie ein Hund!« sagte er, es war, als sollte die Scham ihn überleben. Als Gregor Samsa eines Morgens aus unruhigen Träumen erwachte, fand er sich in seinem Bett zu einem ungeheueren Ungeziefer verwandelt. Und es war ihnen wie eine Bestätigung ihrer neuen Träume und guten Absichten, als am Ziele ihrer Fahrt die Tochter als erste sich erhob und ihren jungen Körper dehnte. »Es ist ein eigentümlicher Apparat«, sagte der Offizier zu dem Forschungsreisenden und überblickte mit einem gewissermaßen bewundernden Blick den ihm doch wohlbekannten Apparat. Sie hätten noch ins Boot springen können, aber der Reisende hob ein schweres, geknotetes Tau vom Boden, drohte ihnen damit und hielt sie dadurch von dem Sprunge ab. In den letzten Jahrzehnten ist das Interesse an Hungerkünstlern sehr zurückgegangen. Aber sie überwanden sich, umdrängten den Käfig und wollten sich gar nicht fortrühren. Jemand musste Josef K. verleumdet haben, denn ohne dass er etwas Böses getan hätte, wurde er eines Morgens verhaftet. »Wie ein Hund!« sagte er, es war, als sollte die Scham ihn überleben. Als Gregor Samsa eines Morgens aus unruhigen Träumen erwachte, fand er sich in seinem Bett zu einem ungeheueren Ungeziefer verwandelt. Und es war ihnen wie eine Bestätigung ihrer neuen Träume und guten Absichten, als am Ziele ihrer Fahrt die Tochter als erste sich erhob und ihren jungen Körper dehnte. »Es ist ein eigentümlicher Apparat«, sagte der Offizier zu dem Forschungsreisenden und überblickte mit einem gewissermaßen bewundernden Blick den ihm doch wohlbekannten Apparat. Sie hätten noch ins Boot springen können, aber der Reisende hob ein schweres, geknotetes Tau vom Boden, drohte ihnen damit und hielt sie dadurch von dem Sprunge ab. In den letzten Jahrzehnten ist das Interesse an Hungerkünstlern sehr zurückgegangen. Aber sie überwanden sich, umdrängten den Käfig und wollten sich gar nicht fortrühren. Jemand musste Josef K. verleumdet haben, denn ohne dass er etwas Böses getan hätte, wurde er eines Morgens verhaftet. »Wie ein Hund!« sagte er, es war, als sollte die Scham ihn überleben. Als Gregor Samsa eines Morgens aus unruhigen Träumen erwachte, fand er sich in seinem Bett zu einem ungeheueren Ungeziefer verwandelt. Und es war ihnen wie eine Bestätigung ihrer neuen Träume und guten Absichten, als am Ziele ihrer Fahrt die Tochter als erste sich erhob und ihren jungen Körper dehnte. »Es ist ein eigentümlicher Apparat«, sagte der Offizier zu dem Forschungsreisenden und überblickte mit einem gewissermaßen bewundernden Blick den ihm doch wohlbekannten Apparat. Sie hätten noch ins Boot springen können, aber der Reisende hob ein schweres, geknotetes Tau vom Boden, drohte ihnen damit und hielt sie dadurch von dem Sprunge ab. In den letzten Jahrzehnten ist das Interesse an Hungerkünstlern sehr zurückgegangen. Aber sie überwanden sich, umdrängten den Käfig und wollten sich gar nicht fortrühren.Jemand musste Josef K. verleumdet haben, denn ohne dass er etwas Böses getan hätte, wurde er eines Morgens verhaftet. »Wie ein Hund!« sagte er, es war, als sollte die Scham ihn überleben. Als Gregor Samsa eines Morgens aus unruhigen Träumen erwachte, fand er sich in seinem Bett zu einem ungeheueren Ungeziefer verwandelt. Und es war ihnen wie eine Bestätigung ihrer neuen Träume und guten Absichten, als am Ziele ihrer Fahrt die Tochter als erste sich erhob und ihren jungen Körper dehnte. »Es ist ein eigentümlicher Apparat«, sagte der Offizier zu dem Forschungsreisenden und überblickte mit einem gewissermaßen bewundernden Blick den ihm doch wohlbekannten Apparat. Sie hätten noch ins");
            }

            reportTable.render(context, 10 * PDFUtils.MM_TO_POINTS_72DPI, PDRectangle.A4.getHeight() - 10 * PDFUtils.MM_TO_POINTS_72DPI);
            context.closeAllPages();

            FileOutputStream fOut = new FileOutputStream("D:/temp/table.pdf");
            doc.save(fOut);
            fOut.close();
        } finally {
            try {
                doc.close();
            } catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        }
    }

//    @Test
    public void layoutTest4() throws IOException {
        final File targetFile = new File("D:/temp/table.pdf");

        PDDocument doc = new PDDocument();
        try {
            PDFont extraFont = PDType0Font.load(doc, new File("D:/Temp/UbuntuCondensed-Regular.ttf"));

            PDPage firstPage = new PDPage(PDRectangle.A4);
            PDFRenderContext context = new PDFRenderContext(doc, firstPage);

            final int COL_DATE = 0;
            final int COL_TIME_FROM = 1;
            final int COL_TIME_TO = 2;
            final int COL_DOCUMENTATION = 3;

            PDFTable reportTable = PDFTable.createWithSomeFixedColumnWidths(PDRectangle.A4.getWidth() - 20 * PDFUtils.MM_TO_POINTS_72DPI,
                    23 * PDFUtils.MM_TO_POINTS_72DPI,
                    14 * PDFUtils.MM_TO_POINTS_72DPI,
                    14 * PDFUtils.MM_TO_POINTS_72DPI,
                    PDFTable.AUTO_DETERMINE_COLUMN_WIDTH
            );
            reportTable.setColumnHeadersMode(PDFTable.ColumnHeadersMode.COLUMN_HEADERS_ON_EVERY_PAGE);

            for (int i = 0; i < reportTable.getColumns(); ++i) {
                reportTable.getColumn(i).setFontSize(11);
            }

            reportTable.getColumn(COL_DATE).setHeading("Datum");
            reportTable.getColumn(COL_TIME_FROM).setHeading("Von");
            reportTable.getColumn(COL_TIME_FROM).setAlign(Align.RIGHT);
            reportTable.getColumn(COL_TIME_TO).setHeading("Bis");
            reportTable.getColumn(COL_TIME_TO).setAlign(Align.RIGHT);
reportTable.getColumn(COL_TIME_TO).setFont(extraFont);
            reportTable.getColumn(COL_DOCUMENTATION).setHeading("Inhalt");
//            reportTable.getColumn(COL_DOCUMENTATION).setAlign(Align.RIGHT);
            reportTable.getColumn(COL_DOCUMENTATION).setAlign(Align.LEFT);

            final Random random = new Random(999L);
            final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            for (int i = 0; i < 100; ++i) {
                PDFTableRow row = reportTable.addRow();

                row.getCell(COL_DATE).setContent(dateFormat.format(new Date()));
                row.getCell(COL_TIME_FROM).setContent(timeFormat.format(new Date()));
//                row.getCell(COL_TIME_TO).setContent("abc\u0000\nHallo, hallo?");
                row.getCell(COL_TIME_TO).setContent("abc\u0000");
                row.getCell(COL_DOCUMENTATION).setTextType(TextType.HTML);
                switch (random.nextInt(2)) {
                    case 0:
                        row.getCell(COL_DOCUMENTATION).setContent(i + " A &#9;Jemand musste Josef K. verleumdet haben");
                        break;
                    case 1:
                        row.getCell(COL_DOCUMENTATION).setContent(i + " B &nbsp; Jemand musste Josef K. verleumdet haben<br/>Zeile2");
                        break;
                }
            }
long time = System.nanoTime();
            reportTable.render(context, 10 * PDFUtils.MM_TO_POINTS_72DPI, PDRectangle.A4.getHeight() - 10 * PDFUtils.MM_TO_POINTS_72DPI);
System.out.println(String.format("took %.2fms", (System.nanoTime() - time) / 1_000_000f));

            context.closeAllPages();

            FileOutputStream fOut = new FileOutputStream(targetFile);
            doc.save(fOut);
            fOut.close();

            Desktop.getDesktop().open(targetFile);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
        } finally {
            try {
                doc.close();
            } catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        }
    }

//    @Test
    public void realWorldTest() throws Exception {
        final File targetFile = new File("D:/temp/table_html_rw.pdf");

        PDDocument doc = new PDDocument();
        try {
            PDPage firstPage = new PDPage(PDRectangle.A4);
            PDFRenderContext context = new PDFRenderContext(doc, firstPage);

            //heading
            PDFLabel heading = new PDFLabel((int) (PDRectangle.A4.getWidth() - 40));
            heading.setText("Verlaufsdokumentation Person X");
            heading.getCell().setFontSize(16);
            heading.render(context, 10 * PDFUtils.MM_TO_POINTS_72DPI, PDRectangle.A4.getHeight() - 10 * PDFUtils.MM_TO_POINTS_72DPI);

            //general info
            PDFTable headingTable = PDFTable.createByRelativeColumnWidth(PDRectangle.A4.getWidth() - 20 * PDFUtils.MM_TO_POINTS_72DPI, 0.5f, 0.5f);
            headingTable.setColumnHeadersMode(PDFTable.ColumnHeadersMode.NO_COLUMN_HEADERS);
            headingTable.getColumn(0).setFontSize(11);
            headingTable.getColumn(1).setFontSize(11);

            PDFTableRow row = headingTable.addRow();
            row.getCell(0).setContent("Klient Nummer: ");
            row.getCell(1).setContent("1234");

            row = headingTable.addRow();
            row.getCell(0).setContent("Name der Familie/Klient: ");
            row.getCell(1).setContent("Person X");

            headingTable.render(context, 10 * PDFUtils.MM_TO_POINTS_72DPI, PDRectangle.A4.getHeight() - 70);

            final int COL_DATE = 0;
            final int COL_TIME_FROM = 1;
            final int COL_TIME_TO = 2;
            final int COL_ACRONYM = 3;
            final int COL_DOCUMENTATION = 4;

            PDFTable reportTable = PDFTable.createWithSomeFixedColumnWidths(PDRectangle.A4.getWidth() - 20 * PDFUtils.MM_TO_POINTS_72DPI,
                    23 * PDFUtils.MM_TO_POINTS_72DPI,
                    14 * PDFUtils.MM_TO_POINTS_72DPI,
                    14 * PDFUtils.MM_TO_POINTS_72DPI,
                    20 * PDFUtils.MM_TO_POINTS_72DPI,
                    PDFTable.AUTO_DETERMINE_COLUMN_WIDTH
                    );
            reportTable.setColumnHeadersMode(PDFTable.ColumnHeadersMode.COLUMN_HEADERS_ON_EVERY_PAGE);

            for (int i = 0; i < reportTable.getColumns(); ++i) {
                reportTable.getColumn(i).setFontSize(11);
            }

            reportTable.getColumn(COL_DATE).setHeading("Datum");
            reportTable.getColumn(COL_TIME_FROM).setHeading("Von");
            reportTable.getColumn(COL_TIME_TO).setHeading("Bis");
            reportTable.getColumn(COL_ACRONYM).setHeading("Betr.");
            reportTable.getColumn(COL_DOCUMENTATION).setHeading("Inhalt");

            final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            for (int i = 0; i < 1; ++i) {
                row = reportTable.addRow();

                row.getCell(COL_DATE).setContent("15.10.2018");
                row.getCell(COL_TIME_FROM).setContent("11:30");
                row.getCell(COL_TIME_TO).setContent("12:30");
                row.getCell(COL_ACRONYM).setContent("Ad");

                row.getCell(COL_DOCUMENTATION).setContent(//"<blockquote>\n" +
//                    "   <div>Dies ist</div>\n" +
//                    "   <div><b>ein dreizeiliger</b></div>\n" +
//                    "   <div>test!</div>\n" +
//                    "   <div><br></div>\n" +
//                    "   <div>Absatz2</div>\n" +
//                    "   <div><br></div>\n" +
//                    "</blockquote>\n" +
                    "<blockquote>" +
                    "She showed me the way" +
                    "    <blockquote>\n" +
                    "        <div><b>»Wie ein Hund!«</b> sagte er, es war, als sollte die Scham ihn überleben. Als Gregor Samsa eines Morgens aus Tau vom Boden, drohte ihnen damit und hielt sie dadurch von dem Sprunge ab. In den letzten Jahrzehnten ist das Interesse an</div>\n" +
                    "    </blockquote>" +
                    "</blockquote>");
                row.getCell(COL_DOCUMENTATION).setTextType(TextType.HTML);
                row.getCell(COL_DOCUMENTATION).updateContentLayout();
                System.out.println(row.getCell(COL_DOCUMENTATION).getLaidoutContent());

            }

            PDFTableBorder boldBorder = new PDFTableBorder();
            boldBorder.setLineWidth(2.5f);

            reportTable.render(context, 10 * PDFUtils.MM_TO_POINTS_72DPI, PDRectangle.A4.getHeight() - 120);

            context.closeAllPages();

            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            doc.save(bOut);

            FileOutputStream fOut = new FileOutputStream(targetFile);
            doc.save(fOut);
            fOut.close();
        } finally {
            try {
                doc.close();
            } catch (IOException ex) {
            }
        }

        Desktop.getDesktop().open(targetFile);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
    }

}
