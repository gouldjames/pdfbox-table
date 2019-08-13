# PDFBox Table

A small library to add multipage table support to Apache's PDFBox (https://pdfbox.apache.org/).

# How to use?
You can then use the library in your Maven projects like this (it's on Maven Central):

    <dependency>
        <groupId>com.moebiusgames</groupId>
        <artifactId>pdfbox-table</artifactId>
        <version>1.1</version>
    </dependency>

# License
This library is licensed under MIT license and can therefore be used in any project, even
for commercial ones.

# Example
```java
public class PDFTableExample {

    private static final int COLUMN_DATE = 0;
    private static final int COLUMN_TIME_FROM = 1;
    private static final int COLUMN_TIME_TO = 2;
    private static final int COLUMN_COMMENT = 3;

    public static void main(String[] args) throws Exception {
        try (PDDocument doc = new PDDocument()) {
            PDPage firstPage = new PDPage(PDRectangle.A4);
            PDFRenderContext context = new PDFRenderContext(doc, firstPage);

            //heading
            PDFLabel heading = new PDFLabel((int) (PDRectangle.A4.getWidth() - 40));
            heading.setText("Some Heading");
            heading.getCell().setFontSize(16).setPaddingBottom(10 * PDFUtils.MM_TO_POINTS_72DPI);
            heading.render(context, 10 * PDFUtils.MM_TO_POINTS_72DPI,
                    PDRectangle.A4.getHeight() - 10 * PDFUtils.MM_TO_POINTS_72DPI);

            PDFTable reportTable = PDFTable.createWithSomeFixedColumnWidths(
                    PDRectangle.A4.getWidth() - 20 * PDFUtils.MM_TO_POINTS_72DPI, // full A4 width minus 20mm margin
                    22 * PDFUtils.MM_TO_POINTS_72DPI,       // width COLUMN_DATE
                    24 * PDFUtils.MM_TO_POINTS_72DPI,       // width COLUMN_TIME_FROM
                    24 * PDFUtils.MM_TO_POINTS_72DPI,       // width COLUMN_TIME_TO
                    PDFTable.AUTO_DETERMINE_COLUMN_WIDTH    // width COLUMN_COMMENT uses the rest that is available
                    );

            // repeat headers on every new page
            reportTable.setColumnHeadersMode(PDFTable.ColumnHeadersMode.COLUMN_HEADERS_ON_EVERY_PAGE);

            reportTable.getColumn(COLUMN_DATE)
                    .setHeading("Date")
                    .setHeadingBackgroundColor(Color.LIGHT_GRAY);

            reportTable.getColumn(COLUMN_TIME_FROM)
                    .setHeading("From")
                    .setHeadingBackgroundColor(Color.LIGHT_GRAY);

            reportTable.getColumn(COLUMN_TIME_TO)
                    .setHeading("Till")
                    .setHeadingBackgroundColor(Color.LIGHT_GRAY);

            reportTable.getColumn(COLUMN_COMMENT)
                    .setHeading("Comment")
                    .setHeadingBackgroundColor(Color.LIGHT_GRAY);


            for (int i = 0; i < 200; ++i) {
                PDFTableRow row = reportTable.addRow();

                LocalDateTime from = LocalDateTime.now();
                LocalDateTime till = from.plus(12, ChronoUnit.MINUTES);

                row.getCell(COLUMN_DATE).setContent(from.toLocalDate().format(DateTimeFormatter.ISO_DATE));
                row.getCell(COLUMN_TIME_FROM).setContent(from.format(DateTimeFormatter.ISO_LOCAL_TIME));
                row.getCell(COLUMN_TIME_TO).setContent(till.format(DateTimeFormatter.ISO_LOCAL_TIME));
                row.getCell(COLUMN_COMMENT).setContent("Some multiline comment for line number "
                        + i + " which will span over multiple lines for sure");
            }

            reportTable.render(context,
                    10 * PDFUtils.MM_TO_POINTS_72DPI   // margin left of 10mm
            );

            // make sure to call this before saving the document
            // to close all content streams on all pages
            context.closeAllPages();

            doc.save(new File("test.pdf"));
        }

    }

}
```

which will render a PDF document like this:

![pdfbox-table-example](https://moebiusgames.com/files/external/github/pdfbox-table-example2.png)