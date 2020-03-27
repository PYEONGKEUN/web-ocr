package com.poseungcar.webocr.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class ImageTools {

    public static BufferedImage resize(InputStream inputStream, int width, int height)
            throws IOException {
        BufferedImage inputImage = ImageIO.read(inputStream);

        BufferedImage outputImage =
                new BufferedImage(width, height, inputImage.getType());

        Graphics2D graphics2D = outputImage.createGraphics();
        graphics2D.drawImage(inputImage, 0, 0, width, height, null);
        graphics2D.dispose();

        return outputImage;
    }
}
