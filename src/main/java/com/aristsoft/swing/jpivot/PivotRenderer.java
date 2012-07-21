package com.aristsoft.swing.jpivot;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public interface PivotRenderer {

    void renderHeaderCell(Graphics2D g2, HeaderCell cell, Rectangle rect, Object value);

    void renderDataCell(Graphics2D g2, HeaderCell xCell, HeaderCell yCell,
            Rectangle rect, CellAttribute attribute, Object value);

    Object formatDataCell(HeaderCell xCell, HeaderCell yCell,
            CellAttribute attribute, Object value);
}
