// Placeholder for src/main/java/com/eduqrgen/QRCodeGenerator.java
package com.eduqrgen;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Generates QR codes for given URLs.
 */
public class QRCodeGenerator {

    private static final int QR_CODE_SIZE = 200; // Pixel size of QR code

    /**
     * Generates a QR code and saves it as an image file.
     *
     * @param url       URL to encode in the QR code.
     * @param outputDir Directory where the QR code should be saved.
     * @return File object pointing to the generated QR image.
     */
    public File generateQRCode(String url, String outputDir) {
        try {
            String fileName = "QRCode_" + url.hashCode() + ".png";
            Path outputPath = FileSystems.getDefault().getPath(outputDir, fileName);
            
            BitMatrix bitMatrix = createQRMatrix(url);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", outputPath);
            
            return outputPath.toFile();
        } catch (WriterException | IOException e) {
            System.err.println("Error generating QR code: " + e.getMessage());
            return null;
        }
    }

    /**
     * Generates a QR code and returns it as a BufferedImage.
     *
     * @param url URL to encode in the QR code.
     * @return BufferedImage containing the generated QR code.
     */
    public BufferedImage generateQRImage(String url) {
        try {
            BitMatrix bitMatrix = createQRMatrix(url);
            return MatrixToImageWriter.toBufferedImage(bitMatrix);
        } catch (WriterException e) {
            System.err.println("Error generating QR code image: " + e.getMessage());
            return null;
        }
    }

    /**
     * Creates a BitMatrix for a QR code from the given URL.
     *
     * @param url URL to encode.
     * @return BitMatrix representing the QR code.
     * @throws WriterException If QR code generation fails.
     */
    private BitMatrix createQRMatrix(String url) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 1); // Reduces white space around QR code
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        return qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE, hints);
    }
}
