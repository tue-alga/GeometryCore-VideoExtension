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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.tue.geometrycore.geometry.Vector;
import nl.tue.geometrycore.geometry.linear.Rectangle;
import nl.tue.geometrycore.geometryrendering.glyphs.PointStyle;
import nl.tue.geometrycore.geometryrendering.styling.Hashures;
import nl.tue.geometrycore.geometryrendering.styling.SizeMode;
import nl.tue.geometrycore.geometryrendering.styling.TextAnchor;
import nl.tue.geometrycore.io.ReadItem;
import nl.tue.geometrycore.io.ipe.IPEReader;
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

    private RasterWriter _frame;
    private SeekableByteChannel _out;
    private AWTSequenceEncoder _encoder;
    private final int _width, _height;
    private final File _file;
    private final int _fps;

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
        _file = file;
        _width = width;
        _height = height;
        _fps = fps;
    }

    public double getAspectRatio() {
        return _width / (double) _height;
    }

    public int getWidth() {
        return _width;
    }

    public int getHeight() {
        return _height;
    }

    public int getFramesPerSecond() {
        return _fps;
    }

    public void initialize() throws IOException {
        _out = (SeekableByteChannel) NIOUtils.writableFileChannel(_file.getAbsolutePath());
        // for Android use: AndroidSequenceEncoder
        _encoder = new AWTSequenceEncoder(_out, Rational.R(_fps, 1));
    }

    public RasterWriter startFrame(Rectangle worldView) throws IOException {
        if (_frame != null) {
            System.err.println("Warning: frame not ended before starting a new frame; ending previous frame");
            endFrame();
        }
        _frame = RasterWriter.imageWriter(worldView, _width, _height);
        _frame.initialize();
        return _frame;
    }

    public RasterWriter startFrame(Rectangle worldView, Color background) throws IOException {
        if (_frame != null) {
            System.err.println("Warning: frame not ended before starting a new frame; ending previous frame");
            endFrame();
        }
        _frame = RasterWriter.imageWriter(worldView, _width, _height, background);
        _frame.initialize();
        return _frame;
    }

    public RasterWriter getCurrentFrame() {
        return _frame;
    }

    public BufferedImage endFrame() throws IOException {
        return endFrame(1);
    }

    public BufferedImage endFrame(int cnt) throws IOException {
        BufferedImage image = _frame.closeWithResult();
        // Encode the image
        while (cnt > 0) {
            _encoder.encodeImage(image);
            cnt--;
        }
        _frame = null;
        return image;
    }

    public void injectImage(int cnt, BufferedImage image) throws IOException {
        while (cnt > 0) {
            _encoder.encodeImage(image);
            cnt--;
        }
    }

    public RasterWriter startFrame(File file) throws IOException {
        return startFrame(file, -1, null);
    }

    public RasterWriter startFrame(File file, int page, Color background) throws IOException {
        if (file.getName().endsWith(".ipe")) {
            IPEReader read = IPEReader.fileReader(file);
            List<ReadItem> items = read.read(page);

            RasterWriter write = startFrame(read.getPageBounds(), background);
            write.setSizeMode(SizeMode.VIEW);
            
            for (ReadItem item : items) {
                write.setAlpha(item.getAlpha());
                write.setStroke(item.getStroke(), item.getStrokewidth(), item.getDash());
                write.setFill(item.getFill(), Hashures.SOLID);
                write.setPointStyle(PointStyle.CIRCLE_WHITE, item.getSymbolsize());
                write.setTextStyle(item.getAnchor(), item.getSymbolsize());

                if (item.getString() == null) {
                    write.draw(item.getGeometry());
                } else {
                    write.draw((Vector) item.getGeometry(), item.getString());
                }
            }

            read.close();
            return write;
        } else {
            Logger.getLogger(VideoWriter.class.getName()).log(Level.WARNING, null, "Unsupported file extension: " + file.getName());
            return null;
        }
    }

    public void close() throws IOException {
        _encoder.finish();
        NIOUtils.closeQuietly(_out);
    }
}
