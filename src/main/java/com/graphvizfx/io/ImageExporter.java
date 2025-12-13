package com.graphvizfx.io;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * Utility helper that captures any {@link Pane} as a PNG snapshot.
 */
public class ImageExporter {

        /**
         * Captures the provided pane and writes it to disk as a PNG image.
         *
         * @param pane the pane to capture
         * @param file destination file (must end with .png)
         * @throws IOException when the image cannot be written
         */
    public static void export(Pane pane, File file) throws IOException {
        WritableImage img = new WritableImage((int) pane.getWidth(), (int) pane.getHeight());
        pane.snapshot(null, img);
        ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", file);
    }
}

