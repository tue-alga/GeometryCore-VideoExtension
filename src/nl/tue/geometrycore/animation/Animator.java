/*
 * GeometryCore video extension   
 * Copyright (C) 2019   Wouter Meulemans (w.meulemans@tue.nl)
 * 
 * Licensed under GNU GPL v3. See provided license documents (license.txt and gpl-3.0.txt) for more information.
 */
package nl.tue.geometrycore.animation;

/**
 *
 * @author Wouter Meulemans (W.meulemans@tue.nl)
 */
public abstract class Animator {

    private final int _frames;

    public Animator(int frames) {
        _frames = frames;
    }

    public void run() {
        double div = _frames - 1;
        for (int f = 0; f < _frames; f++) {
            render(f, f / div);
        }
    }

    public abstract void render(int frame, double fraction);

}
