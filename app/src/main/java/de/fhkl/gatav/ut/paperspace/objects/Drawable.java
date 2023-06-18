package de.fhkl.gatav.ut.paperspace.objects;

import android.graphics.Canvas;

public interface Drawable {

        /**
         * Element zeichnen
         * @param canvas Zeichenfläche, auf die zu zeichnen ist
         */
        void draw(Canvas canvas);

        /**
         * Zustand des Elements aktualisieren.
         *
         * Solche Zustandsänderungen können bspw. Animation der Position oder der
         * grafischen Darstellung an sich sein.
         * @param //fracsec Teil einer Sekunde, der seit dem letzten Update des gesamten Spielzustandes
         *                vergangen ist
         */
        void update();
    }


