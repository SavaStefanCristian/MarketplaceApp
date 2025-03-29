package org.marketplace.utils;


import javafx.scene.image.Image;

import java.io.*;
import java.nio.file.Files;

public class ImageUtils {
    public static byte[] imageFileToByteArray(File file) throws IOException {
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new IOException(file.getName());
        }
    }

    public static Image byteArrayToFXImage(byte[] imageBytes) {
        return new Image(new ByteArrayInputStream(imageBytes));
    }
}