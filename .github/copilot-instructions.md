# PDFBox Table Library - AI Assistant Instructions

This Java library extends Apache PDFBox with multipage table support. Understanding its architecture and patterns will help you be effective when contributing or modifying the code.

## Core Architecture

**Primary Components:**
- `PDFTable` - Main table class with column/row structure and multipage rendering logic
- `PDFRenderContext` - Manages document pages and content streams across page breaks
- `PDFTableCell` - Individual cell with rich formatting, HTML-like content, and text wrapping
- `PDFTableColumn`/`PDFTableRow` - Container classes for table structure
- `PDFLabel` - Single-cell text component (wrapper around single-row PDFTable)

**Key Design Pattern:** The library uses a builder-like fluent API where table structure is defined first, then content is populated, and finally rendering occurs in a separate step.

## Critical Coordinate System

**Points vs Millimeters:** All positioning uses PDF points (72 DPI). Convert millimeters using:
```java
float mmInPoints = myMM * PDFUtils.MM_TO_POINTS_72DPI;
```

**Coordinate Origin:** PDF coordinates start at bottom-left (0,0), but this library works top-down from page height. Y-coordinates decrease as you move down the page.

## Table Creation Patterns

**Column Width Strategy:**
```java
// Fixed widths for specific columns, auto-calculate remaining
PDFTable.createWithSomeFixedColumnWidths(totalWidth, 
    fixedWidth1, fixedWidth2, PDFTable.AUTO_DETERMINE_COLUMN_WIDTH);
```

**Render Context Pattern:**
```java
PDFRenderContext context = new PDFRenderContext(doc, firstPage);
// ... add content ...
context.closeAllPages(); // CRITICAL: Always call before saving document
```

## Page Breaking Logic

**Automatic Pagination:** Tables automatically break across pages when content exceeds available space. The library tracks `pos.y - pageSettings.getMarginBottom()` to determine when a new page is needed.

**Header Behavior:** Use `ColumnHeadersMode.COLUMN_HEADERS_ON_EVERY_PAGE` for tables that span multiple pages.

**Content Stream Management:** Each page maintains an open `PDPageContentStream`. The render context manages these streams and must be closed before document save.

## Cell Content & Formatting

**HTML-like Content:** Cells support basic HTML tags (`<b>`, `<i>`, `<u>`) via JSoup parsing. Set `TextType.HTML` to enable.

**Text Wrapping:** Content automatically wraps within cell boundaries. The library calculates text height to determine row heights.

**Styling Inheritance:** Cell properties inherit from column defaults, then can be overridden at the cell level.

## Build & Test

**Maven Commands:**
- `mvn clean compile` - Basic compilation
- `mvn test` - Run the single test class
- `mvn clean package` - Build JAR for local use

**Test Strategy:** Minimal test coverage with `PDFTextFieldTest`. New features should include visual PDF output verification since this is a rendering library.

## Dependencies & Constraints

**Core Dependencies:**
- Apache PDFBox 3.0.2 (PDF manipulation)
- JSoup 1.15.3 (HTML parsing in cells)
- Log4j 1.2.17 (logging - provided scope for IBM TRIRIGA integration)

**Java Version:** Targets Java 8 (`maven.compiler.source/target = 1.8`)

**Platform Notes:** This appears to be a fork with IBM TRIRIGA-specific modifications (version `1.8.2-CST`, log4j provided scope).

## Common Gotchas

1. **Stream Management:** Forgetting `context.closeAllPages()` results in corrupted PDFs
2. **Coordinate Conversion:** Mixing millimeters and points without conversion
3. **Page Margins:** Not accounting for `pageSettings.getMarginBottom()` in custom positioning
4. **Font Loading:** Font variants are cached in `PDFUtils.BOLD_FONT_VARIANTS` - extend this map for new fonts

## Extension Points

When adding features, consider:
- Cell content types (current: text, HTML, bullet points)
- Border styles (current: basic line width/color)  
- Column alignment options
- Cell background/foreground color combinations
- Font style variations beyond bold

Focus on the multipage rendering logic in `PDFTable.renderRow()` when modifying table flow behavior.