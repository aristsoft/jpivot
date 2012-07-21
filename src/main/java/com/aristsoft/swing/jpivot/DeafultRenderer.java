package com.aristsoft.swing.jpivot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;

public class DeafultRenderer implements PivotRenderer, Defaults {

    private static final int _Y = 2;
    private static final int _X = 2;

    public static final int MARGIN = 2;

    public static String toString(Object value) {
        return value == null ? "" : value.toString();
    }

    @Override
    public void renderHeaderCell(Graphics2D g2, HeaderCell cell, Rectangle r,
            Object value) {
        String s = toString(value);
        g2.setColor(Color.DARK_GRAY);
        if (cell.isHorizOrientation()) {
            drawStringHLeft(g2, s, r);
        } else if (cell.isVertOrientation()) {
            drawStringVTop(g2, s, r);
        }
    }

    @Override
    public Object formatDataCell(HeaderCell xCell, HeaderCell yCell,
            CellAttribute attribute, Object value) {
        return value;
    }

    @Override
    public void renderDataCell(Graphics2D g2, HeaderCell xCell,
            HeaderCell yCell, Rectangle rect, CellAttribute attribute,
            Object value) {
        drawStringHCenter(g2, toString(value), rect);
    }

    public static void drawString(Graphics g, int x, int y, double angle,
            String s) {
        if (s == null || s.length() == 0)
            return;
        AffineTransform at = new AffineTransform();
        Font f = g.getFont();
        at.rotate(Math.toRadians(angle));
        Font fx = new Font(f.getName(), f.getStyle(), f.getSize())
                .deriveFont(at);
        AttributedString as = new AttributedString(s);
        as.addAttribute(TextAttribute.FONT, fx);
        g.drawString(as.getIterator(), x, y);
    }

    public static Dimension getStringBounds(Graphics2D g2, String s) {
        FontRenderContext context = g2.getFontRenderContext();
        Font f = g2.getFont();
        Rectangle2D b = f.getStringBounds(s, context);
        return new Dimension((int) b.getWidth(), (int) b.getHeight());
    }

    public static void drawStringHoriz(Graphics2D g2, String s, int x, int y) {
        Dimension sz = getStringBounds(g2, s);
        g2.drawString(s, x, y + sz.height);
    }

    public static void drawStringVert(Graphics2D g2, String s, int x, int y) {
        Dimension sz = getStringBounds(g2, s);
        drawString(g2, x + sz.height, y + sz.width, 270, s);
    }

    /*
     * Draw text in rectangle
     */

    public static void drawStringHLeft(Graphics2D g2, String s, Rectangle r) {
        Dimension sz = getStringBounds(g2, s);
        int x = r.x + MARGIN;
        int h1 = r.height;
        int h2 = sz.height;
        int y = r.y + (h1 / 2 - h2 / 2);
        g2.drawString(s, x, y + sz.height - _Y);
    }

    /*
     * Horizontal - Center
     */
    public static void drawStringHCenter(Graphics2D g2, String s, Rectangle r) {
        Dimension sz = getStringBounds(g2, s);
        int w1 = r.width;
        int w2 = sz.width;
        int x = r.x + (w1 / 2 - w2 / 2);
        int h1 = r.height;
        int h2 = sz.height;
        int y = r.y + (h1 / 2 - h2 / 2);
        g2.drawString(s, x, y + sz.height - _Y);
    }

    /*
     * Vertical - Center
     */
    public static void drawStringVCenter(Graphics2D g2, String s, Rectangle r) {
        Dimension sz = getStringBounds(g2, s);
        int h1 = r.width;
        int w1 = r.height;
        int w2 = sz.height;
        int h2 = sz.width;

        int x = r.x + (w1 / 2 - w2 / 2);
        int y = r.y + (h1 / 2 - h2 / 2);

        drawStringVert(g2, s, x - _X, y);
    }

    public static void drawStringVTop(Graphics2D g2, String s, Rectangle r) {
        Dimension sz = getStringBounds(g2, s);
        int w1 = r.height;
        int w2 = sz.height;

        int x = r.x + (w1 / 2 - w2 / 2);
        int y = r.y;

        drawStringVert(g2, s, x - _X, y);
    }
}
