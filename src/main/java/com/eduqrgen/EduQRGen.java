// Placeholder for src/main/java/com/eduqrgen/EduQRGen.java
package com.eduqrgen;

import com.eduqrgen.database.QRDatabase;
import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

/**
 * Main class to handle the EduQRGen process.
 * Supports PDF file selection, link extraction, and QR code embedding.
 */
public class EduQRGen extends Application {

    private PDFHandler pdfHandler;
    private QRCodeGenerator qrCodeGenerator;
    private QRDatabase qrDatabase;

    @Override
    public void start(Stage primaryStage) {
        // Initialize core components
        pdfHandler = new PDFHandler();
        qrCodeGenerator = new QRCodeGenerator();
        qrDatabase = new QRDatabase();

        // Open file picker to select a PDF
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select PDF File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            processPDF(selectedFile);
        } else {
            System.out.println("No file selected.");
        }
    }

    /**
     * Processes the selected PDF file:
     * - Extracts links
     * - Generates QR codes
     * - Embeds them into the PDF
     *
     * @param pdfFile Selected PDF file
     */
    private void processPDF(File pdfFile) {
        System.out.println("Processing: " + pdfFile.getName());

        // Extract links from the PDF
        List<String> extractedLinks = pdfHandler.extractLinks(pdfFile);
        if (extractedLinks.isEmpty()) {
            System.out.println("No links found in the document.");
            return;
        }

        // Generate and embed QR codes
        for (String url : extractedLinks) {
            if (!qrDatabase.isDuplicate(url)) {
                File qrImage = qrCodeGenerator.generateQRCode(url, "generated_qr_codes");
                if (qrImage != null) {
                    pdfHandler.embedQRCode(pdfFile, qrImage, url);
                    qrDatabase.saveQRCode(url, qrImage.getAbsolutePath());
                }
            } else {
                System.out.println("Skipping duplicate QR code for: " + url);
            }
        }

        System.out.println("PDF processing complete!");
    }

    public static void main(String[] args) {
        launch(args); // Start JavaFX application
    }
}
