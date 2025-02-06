package com.eduqrgen;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class App extends Application {

    private BorderPane root;
    private VBox qrCodeList;
    private PDDocument pdfDocument;
    private PDFRenderer pdfRenderer;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("EduQRGen - PDF QR Code Generator");

        root = new BorderPane();
        qrCodeList = new VBox();
        qrCodeList.setSpacing(10);

        // Top Menu
        HBox topMenu = new HBox();
        Button uploadButton = new Button("Upload PDF");
        Button generateQRButton = new Button("Generate QR Codes");
        topMenu.getChildren().addAll(uploadButton, generateQRButton);

        // Left PDF Viewer
        ScrollPane pdfViewer = new ScrollPane();
        pdfViewer.setFitToWidth(true);
        pdfViewer.setFitToHeight(true);

        // Right QR Code List
        ScrollPane qrCodeScrollPane = new ScrollPane(qrCodeList);
        qrCodeScrollPane.setFitToWidth(true);

        root.setTop(topMenu);
        root.setLeft(pdfViewer);
        root.setRight(qrCodeScrollPane);

        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();

        uploadButton.setOnAction(e -> uploadPDF(primaryStage, pdfViewer));
        generateQRButton.setOnAction(e -> generateQRCodes());
    }

    private void uploadPDF(Stage stage, ScrollPane pdfViewer) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            try {
                pdfDocument = PDDocument.load(selectedFile);
                pdfRenderer = new PDFRenderer(pdfDocument);
                // Render the first page as an example
                pdfViewer.setContent(new ImageView(SwingFXUtils.toFXImage(pdfRenderer.renderImage(0), null)));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void generateQRCodes() {
        if (pdfDocument != null) {
            try {
                List<String> links = PDFHandler.extractLinks(pdfDocument);
                PDFHandler.savePDFWithQRCodes(pdfDocument, links);
                // Display QR codes in the right panel
                qrCodeList.getChildren().clear();
                for (String link : links) {
                    ImageView qrCodeImageView = new ImageView(SwingFXUtils.toFXImage(QRCodeGenerator.generateQRCode(link, 100), null));
                    qrCodeList.getChildren().add(qrCodeImageView);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}