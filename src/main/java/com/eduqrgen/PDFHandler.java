package com.eduqrgen;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;

public class PDFHandler {

    public static List<String> extractLinks(PDDocument document) throws IOException {
        List<String> links = new ArrayList<>();
        for (PDPage page : document.getPages()) {
            for (PDAnnotation annotation : page.getAnnotations()) {
                if (annotation instanceof PDAnnotationLink) {
                    PDAnnotationLink link = (PDAnnotationLink) annotation;
                    if (link.getAction() instanceof PDActionURI) {
                        PDActionURI uri = (PDActionURI) link.getAction();
                        links.add(uri.getURI());
                    }
                }
            }
        }
        return links;
    }

    public static void savePDFWithQRCodes(PDDocument document, List<String> links) throws IOException {
        for (PDPage page : document.getPages()) {
            PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);
            PDRectangle mediaBox = page.getMediaBox();
            float x = mediaBox.getWidth() - 100; // Position QR code on the right margin
            float y = mediaBox.getHeight() - 100; // Position QR code at the top

            for (String link : links) {
                try {
                    BufferedImage qrCodeImage = QRCodeGenerator.generateQRCode(link, 100);
                    File tempFile = File.createTempFile("qrcode", ".png");
                    ImageIO.write(qrCodeImage, "png", tempFile);
                    PDImageXObject pdImage = PDImageXObject.createFromFileByContent(tempFile, document);
                    contentStream.drawImage(pdImage, x, y, 100, 100);
                    y -= 110; // Move down for the next QR code
                    tempFile.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            contentStream.close();
        }
        document.save(new File("modified_" + document.getDocumentInformation().getTitle() + ".pdf"));
    }
}