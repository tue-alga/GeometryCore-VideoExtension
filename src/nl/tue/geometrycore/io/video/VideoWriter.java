/*
 * GeometryCore video extension   
 * Copyright (C) 2019   Wouter Meulemans (w.meulemans@tue.nl)
 * 
 * Licensed under GNU GPL v3. See provided license documents (license.txt and gpl-3.0.txt) for more information.
 */
package nl.tue.geometrycore.io.video;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import nl.tue.geometrycore.geometry.linear.Rectangle;
import nl.tue.geometrycore.io.raster.RasterWriter;
import org.jcodec.api.awt.AWTSequenceEncoder;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Rational;

/**
 *
 * @author Wouter Meulemans (w.meulemans@tue.nl)
 */
public class VideoWriter {

    private RasterWriter frame;
    private SeekableByteChannel out;
    private AWTSequenceEncoder encoder;
    private final int width, height;
    private final File file;
    private final int fps;

    public static VideoWriter customVideo(File file, int width, int height, int fps) {
        return new VideoWriter(file, width, height, fps);
    }
    
    public static VideoWriter fullHDvideo(File file) {
        return new VideoWriter(file, 1920, 1080, 30);
    }

    public static VideoWriter halfHDvideo(File file) {
        return new VideoWriter(file, 1280, 720, 30);
    }

    private VideoWriter(File file, int width, int height, int fps) {
        this.file = file;
        this.width = width;
        this.height = height;
        this.fps = fps;
    }

    public double getAspectRatio() {
        return width / (double) height;
    }

    public void extendToAspectRatio(Rectangle r) {

        double dx = Math.max(0, (width * r.height() / height - r.width()) / 2.0);
        double dy = Math.max(0, (height * r.width() / width - r.height()) / 2.0);
        r.grow(dx, dy);
    }

    public void initialize() throws IOException {
        out = (SeekableByteChannel) NIOUtils.writableFileChannel(file.getAbsolutePath());
        // for Android use: AndroidSequenceEncoder
        encoder = new AWTSequenceEncoder(out, Rational.R(fps, 1));
    }

    public RasterWriter startFrame(Rectangle worldView) throws IOException {
        frame = RasterWriter.imageWriter(worldView, width, height);
        frame.initialize();
        return frame;
    }

    public RasterWriter startFrame(Rectangle worldView, Color background) throws IOException {
        frame = RasterWriter.imageWriter(worldView, width, height, background);
        frame.initialize();
        return frame;
    }

    public void endFrame() throws IOException {
        endFrame(1);
    }

    public BufferedImage endFrame(int cnt) throws IOException {
        BufferedImage image = frame.closeWithResult();
        // Encode the image
        while (cnt > 0) {
            encoder.encodeImage(image);
            cnt--;
        }
        frame = null;
        return image;
    }

    public void injectImage(int cnt, BufferedImage image) throws IOException {
        while (cnt > 0) {
            encoder.encodeImage(image);
            cnt--;
        }
    }

    public void close() throws IOException {
        encoder.finish();
        NIOUtils.closeQuietly(out);
    }
}
