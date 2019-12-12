/*
 * GeometryCore video extension   
 * Copyright (C) 2019   Wouter Meulemans (w.meulemans@tue.nl)
 * 
 * Licensed under GNU GPL v3. See provided license documents (license.txt and gpl-3.0.txt) for more information.
 */
package nl.tue.geometrycore.interpolation.ease;

import nl.tue.geometrycore.interpolation.EasingFunction;

/**
 *
 * @author Wouter Meulemans (W.meulemans@tue.nl)
 */
public class QuadraticEasing implements EasingFunction {
    
    @Override
    public double getLambda(double fraction) {
        if (fraction <= 0.5) {
            return 2 * fraction * fraction;
        } else {
            return 1 - 2 * (1-fraction) * (1-fraction);
        }
    }
    
}
