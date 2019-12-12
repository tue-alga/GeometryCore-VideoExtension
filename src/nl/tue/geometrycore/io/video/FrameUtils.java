/*
 * GeometryCore video extension   
 * Copyright (C) 2019   Wouter Meulemans (w.meulemans@tue.nl)
 * 
 * Licensed under GNU GPL v3. See provided license documents (license.txt and gpl-3.0.txt) for more information.
 */
package nl.tue.geometrycore.io.video;

import nl.tue.geometrycore.datastructures.list2d.List2D;
import nl.tue.geometrycore.geometry.Vector;
import nl.tue.geometrycore.geometry.linear.Rectangle;

/**
 *
 * @author Wouter Meulemans (W.meulemans@tue.nl)
 */
public class FrameUtils {

    public static void extendToAspectRatio(Rectangle rect, double aspectRatio) {
        double dx = Math.max(0, (aspectRatio * rect.height() - rect.width()) / 2.0);
        double dy = Math.max(0, (rect.width() / aspectRatio - rect.height()) / 2.0);
        rect.grow(dx, dy);
    }

    public static void extendToAspectRatio(Rectangle rect, Rectangle target) {
        extendToAspectRatio(rect, target.width() / target.height());
    }

    public static void extendToAspectRatio(Rectangle rect, VideoWriter video) {
        extendToAspectRatio(rect, video.getAspectRatio());
    }

    public static void extendToAspectRatio(Rectangle rect, double width, double height) {
        extendToAspectRatio(rect, width / height);
    }

    public static List2D<Rectangle> partition(Rectangle rect, int columns, int rows, double outermargin, double innermargin) {

        List2D<Rectangle> result = new List2D(columns, rows);

        double w = (rect.width() - 2 * outermargin - (columns - 1) * innermargin) / columns;
        double h = (rect.height() - 2 * outermargin - (rows - 1) * innermargin) / rows;

        double l = outermargin;
        for (int c = 0; c < columns; c++) {
            double t = outermargin;
            for (int r = 0; r < rows; r++) {
                result.set(columns, rows, Rectangle.byCornerAndSize(new Vector(l, t), w, h));
                t += h + innermargin;
            }
            l += w + innermargin;
        }

        return result;
    }
}
