package com.aristsoft.swing.jpivot;

import java.awt.Rectangle;

public class Area {
    Rectangle rect = new Rectangle();

    Area() {
    }

    void reset() {
        crearRect(rect);
    }

    static void crearRect(Rectangle r) {
        r.x = -1;
        r.y = -1;
        r.width = 0;
        r.height = 0;
    }
    
    static void zeroRect(Rectangle r) {
        r.x = 0;
        r.y = 0;
        r.width = 0;
        r.height = 0;
    }
}
