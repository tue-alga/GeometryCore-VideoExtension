/*
 * GeometryCore video extension   
 * Copyright (C) 2019   Wouter Meulemans (w.meulemans@tue.nl)
 * 
 * Licensed under GNU GPL v3. See provided license documents (license.txt and gpl-3.0.txt) for more information.
 */
package nl.tue.geometrycore.interpolation;

import nl.tue.geometrycore.geometry.BaseGeometry;
import nl.tue.geometrycore.geometry.Vector;
import nl.tue.geometrycore.geometry.curved.Circle;
import nl.tue.geometrycore.geometry.linear.LineSegment;
import nl.tue.geometrycore.geometry.linear.PolyLine;
import nl.tue.geometrycore.geometry.linear.Polygon;
import nl.tue.geometrycore.geometry.linear.Rectangle;
import nl.tue.geometrycore.interpolation.ease.LinearEasing;

/**
 *
 * @author Wouter Meulemans (W.meulemans@tue.nl)
 */
public class Interpolator {

    private EasingFunction _ease;

    public Interpolator() {
        _ease = new LinearEasing();
    }

    public Interpolator(EasingFunction ease) {
        _ease = ease;
    }

    public EasingFunction getEase() {
        return _ease;
    }

    public void setEase(EasingFunction ease) {
        _ease = ease;
    }
    
    public double between(double fraction, double a, double b) {
        double lambda = _ease.getLambda(fraction);
        return (1-lambda) * a + lambda * b;                
    }

    public Vector between(double fraction, Vector a, Vector b) {
        double lambda = _ease.getLambda(fraction);
        return Vector.add(
                Vector.multiply(1 - lambda, a),
                Vector.multiply(lambda, b)
        );
    }

    public Rectangle between(double fraction, Rectangle a, Rectangle b) {
        double lambda = _ease.getLambda(fraction);
        Vector c = Vector.add(
                Vector.multiply(1 - lambda, a.center()),
                Vector.multiply(lambda, b.center())
        );
        double w = (1 - lambda) * a.width() + lambda * b.width();
        double h = (1 - lambda) * a.height() + lambda * b.height();
        return Rectangle.byCenterAndSize(c, w, h);
    }

    public Circle between(double fraction, Circle a, Circle b) {
        double lambda = _ease.getLambda(fraction);
        Vector c = Vector.add(
                Vector.multiply(1 - lambda, a.getCenter()),
                Vector.multiply(lambda, b.getCenter())
        );
        double r = (1 - lambda) * a.getRadius() + lambda * b.getRadius();
        return new Circle(c, r);
    }

    /**
     * NB: line segments are directed.
     * 
     * @param fraction
     * @param a
     * @param b
     * @return 
     */
    public LineSegment between(double fraction, LineSegment a, LineSegment b) {
        return new LineSegment(between(fraction, a.getStart(), b.getStart()),
                between(fraction, a.getEnd(), b.getEnd()));
    }

    /**
     * Assumes same number of vertices.
     *
     * @param fraction
     * @param a
     * @param b
     * @return
     */
    public Polygon between(double fraction, Polygon a, Polygon b) {
        Polygon p = new Polygon();
        for (int i = 0; i < a.vertexCount(); i++) {
            p.addVertex(between(fraction, a.vertex(i), b.vertex(i)));
        }
        return p;

    }
    
    /**
     * Assumes same number of vertices.
     *
     * @param fraction
     * @param a
     * @param b
     * @return
     */
    public PolyLine between(double fraction, PolyLine a, PolyLine b) {
        PolyLine p = new PolyLine();
        for (int i = 0; i < a.vertexCount(); i++) {
            p.addVertex(between(fraction, a.vertex(i), b.vertex(i)));
        }
        return p;

    }

    public <T extends BaseGeometry> T scale(double fraction, T geom, double scale, Vector scaleOrigin) {
        T result = (T) geom.clone();
        double lambda = _ease.getLambda(fraction);
        double s = lambda * scale;
        result.scale(s, scaleOrigin);
        return result;
    }
    
    public <T extends BaseGeometry> T scale(double fraction, T geom, double startscale, double scale, Vector scaleOrigin) {
        T result = (T) geom.clone();
        double lambda = _ease.getLambda(fraction);
        double s = startscale + lambda * (scale - startscale);
        result.scale(s, scaleOrigin);
        return result;
    }

    public <T extends BaseGeometry> T translate(double fraction, T geom, Vector delta) {
        T result = (T) geom.clone();
        double lambda = _ease.getLambda(fraction);
        Vector d = Vector.multiply(lambda, delta);
        result.translate(d);
        return result;
    }

    public <T extends BaseGeometry> T rotate(double fraction, T geom, double ccwangle, Vector rotateOrigin) {
        T result = (T) geom.clone();
        double lambda = _ease.getLambda(fraction);
        double a = lambda * ccwangle;
        result.rotate(a, rotateOrigin);
        return result;
    }

}
