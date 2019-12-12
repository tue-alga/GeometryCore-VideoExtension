/*
 * GeometryCore video extension   
 * Copyright (C) 2019   Wouter Meulemans (w.meulemans@tue.nl)
 * 
 * Licensed under GNU GPL v3. See provided license documents (license.txt and gpl-3.0.txt) for more information.
 */
package nl.tue.geometrycore.interpolation;

/**
 *
 * @author Wouter Meulemans (W.meulemans@tue.nl)
 */
public interface EasingFunction {
    
    public double getLambda(double fraction);
}
