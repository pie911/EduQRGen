// Placeholder for src/main/java/com/eduqrgen/PDFHandler.java
package com.eduqrgen;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles PDF processing:
 * - Extracts URLs from PDFs
 * - Embeds QR codes into PDFs
 */
public class PDFHandler {

    private static final Pattern URL_PATTERN = Pattern.compile(
            "(https?:\\/\\/\\S+)", Pattern.CASE_INSENSITIVE);

    /**
     * Extracts all URLs from a given PDF file
     *
     * @param pdfFile PDF file to process
     * @return List of URLs found in the PDF
     */
    public List<String> extractURLs(File pdfFile) {
        List<String> urls = new ArrayList<>();
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            Matcher matcher = URL_PATTERN.matcher(text);
            while (matcher.find()) {
                String url = matcher.group().trim();
                if (!urls.contains(url)) {
                    urls.add(url);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading PDF: " + e.getMessage());
        }
        return urls;
    }

    /**
     * Embeds QR codes into a PDF and saves the modified version
     *
     * @param inputPdf  Original PDF file
     * @param outputPdf Output file to save modified PDF
     */
    public void embedQRCodes(File inputPdf, File outputPdf) {
        try (PDDocument document = PDDocument.load(inputPdf)) {
            List<String> urls = extractURLs(inputPdf);
            QRCodeGenerator qrGenerator = new QRCodeGenerator();

            if (urls.isEmpty()) {
                System.out.println("No URLs found in the PDF.");
                return;
            }

            int pageIndex = 0;
            for (String url : urls) {
                if (pageIndex >= document.getNumberOfPages()) break;

                PDPage page = document.getPage(pageIndex);
                PDRectangle mediaBox = page.getMediaBox();

                // Generate QR code image
                BufferedImage qrImage = qrGenerator.generateQRImage(url);
                File qrFile = new File("temp_qr_" + pageIndex + ".png");
                ImageIO.write(qrImage, "png", qrFile);

                // Add QR Code to PDF
                addImageToPage(document, page, qrFile, mediaBox.getWidth() - 100, mediaBox.getHeight() - 100);

                pageIndex++;
            }

            document.save(outputPdf);
            System.out.println("Modified PDF saved: " + outputPdf.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error embedding QR codes: " + e.getMessage());
        }
    }

    /**
     * Adds an image to a specific page in the PDF
     *
     * @param document  PDF document
     * @param page      Target page
     * @param imageFile Image file to add
     * @param x         X-coordinate position
     * @param y         Y-coordinate position
     * @throws IOException If there is an error writing to PDF
     */
    private void addImageToPage(PDDocument document, PDPage page, File imageFile, float x, float y) throws IOException {
        PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);
        BufferedImage bufferedImage = ImageIO.read(imageFile);
        contentStream.drawImage(PDImageUtils.convertBufferedImageToPDImage(document, bufferedImage), x, y, 80, 80);
        contentStream.close();
    }

}
