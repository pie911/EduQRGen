// Placeholder for src/main/java/com/eduqrgen/App.java
package com.eduqrgen;

import com.eduqrgen.database.QRDatabase;
import com.eduqrgen.pdf.PDFHandler;
import com.eduqrgen.qr.QRCodeGenerator;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.List;

/**
 * Main JavaFX Application for EduQRGen
 */
public class App extends Application {
    private File selectedPDF;
    private final QRDatabase qrDatabase = new QRDatabase(); // ✅ Database Connection
    private final PDFHandler pdfHandler = new PDFHandler(); // ✅ PDF Processing
    private final QRCodeGenerator qrGenerator = new QRCodeGenerator(); // ✅ QR Code Generator
    private final Stack<List<ImageView>> undoStack = new Stack<>(); // ✅ Undo Functionality
    private final Stack<List<ImageView>> redoStack = new Stack<>(); // ✅ Redo Functionality

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("EduQRGen - QR Code PDF Generator");

        // ✅ Layout Setup
        BorderPane root = new BorderPane();
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        vbox.setStyle("-fx-background-color: #f4f4f4;");

        Label title = new Label("EduQRGen - Generate QR Codes for PDFs");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button uploadButton = new Button("Upload PDF");
        uploadButton.setOnAction(e -> selectPDF(primaryStage));

        Button generateQRButton = new Button("Generate QR Codes");
        generateQRButton.setDisable(true); // Disabled until a PDF is uploaded
        generateQRButton.setOnAction(e -> generateQRCodes());

        Button saveButton = new Button("Save Modified PDF");
        saveButton.setDisable(true);
        saveButton.setOnAction(e -> saveModifiedPDF());

        Button undoButton = new Button("Undo");
        undoButton.setDisable(true);
        undoButton.setOnAction(e -> undoAction());

        Button redoButton = new Button("Redo");
        redoButton.setDisable(true);
        redoButton.setOnAction(e -> redoAction());

        HBox buttonBar = new HBox(10, uploadButton, generateQRButton, saveButton, undoButton, redoButton);
        buttonBar.setPadding(new Insets(10));

        // ✅ Drag & Drop Feature
        Label dragDropLabel = new Label("Drag and drop a PDF file here.");
        dragDropLabel.setStyle("-fx-border-color: #aaa; -fx-padding: 20px; -fx-border-radius: 5px;");
        dragDropLabel.setOnDragOver(event -> handleDragOver(event));
        dragDropLabel.setOnDragDropped(event -> handleFileDrop(event, primaryStage));

        vbox.getChildren().addAll(title, buttonBar, dragDropLabel);
        root.setCenter(vbox);

        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    /**
     * Opens file chooser to select a PDF file
     */
    private void selectPDF(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        selectedPDF = fileChooser.showOpenDialog(stage);

        if (selectedPDF != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Selected: " + selectedPDF.getName(), ButtonType.OK);
            alert.show();
        }
    }

    /**
     * Handles drag & drop file selection
     */
    private void handleDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

    private void handleFileDrop(DragEvent event, Stage stage) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            selectedPDF = db.getFiles().get(0);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Selected: " + selectedPDF.getName(), ButtonType.OK);
            alert.show();
        }
        event.setDropCompleted(true);
        event.consume();
    }

    /**
     * Generates QR codes for all URLs found in the PDF
     */
    private void generateQRCodes() {
        if (selectedPDF == null) {
            showAlert(Alert.AlertType.WARNING, "No PDF selected.");
            return;
        }

        List<String> urls = pdfHandler.extractURLs(selectedPDF);
        if (urls.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No URLs found in this PDF.");
            return;
        }

        List<ImageView> qrImages = qrGenerator.generateQRs(urls);
        undoStack.push(qrImages);
        redoStack.clear();

        // Display QR codes (You can enhance this UI)
        for (ImageView qr : qrImages) {
            qr.setFitWidth(100);
            qr.setFitHeight(100);
        }

        showAlert(Alert.AlertType.INFORMATION, "QR Codes Generated!");
    }

    /**
     * Saves the modified PDF with embedded QR codes
     */
    private void saveModifiedPDF() {
        if (selectedPDF == null) {
            showAlert(Alert.AlertType.WARNING, "No PDF selected.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File outputFile = fileChooser.showSaveDialog(null);

        if (outputFile != null) {
            pdfHandler.embedQRCodes(selectedPDF, outputFile);
            showAlert(Alert.AlertType.INFORMATION, "PDF saved successfully!");
        }
    }

    /**
     * Undo last QR Code placement
     */
    private void undoAction() {
        if (!undoStack.isEmpty()) {
            List<ImageView> lastAction = undoStack.pop();
            redoStack.push(lastAction);
            showAlert(Alert.AlertType.INFORMATION, "Undo last action.");
        }
    }

    /**
     * Redo last undone action
     */
    private void redoAction() {
        if (!redoStack.isEmpty()) {
            List<ImageView> lastUndo = redoStack.pop();
            undoStack.push(lastUndo);
            showAlert(Alert.AlertType.INFORMATION, "Redo last action.");
        }
    }

    /**
     * Utility method to show alerts
     */
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
