package com.aristsoft.swing.jpivot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.PanelUI;

import com.jhlabs.image.BrushedMetalFilter;
import com.jhlabs.image.GradientFilter;

public class JPivotUI extends PanelUI implements Defaults {

    private static final Logger LOG = Logger
            .getLogger(JPivotUI.class.getName());

    private JPivotUI() {
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        new Painter(g, (JPivot) c).paint();
    }

    @Override
    public void update(Graphics g, JComponent c) {
        if (c.isOpaque()) {
            g.setColor(c.getBackground());
            g.fillRect(0, 0, c.getWidth(), c.getHeight());
        }
        paint(g, c);
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        return c.getPreferredSize();
    }

    public static ComponentUI createUI(JComponent c) {
        if (c instanceof JPivot)
            return new JPivotUI();
        else
            return null;
    }

    static BufferedImageOp getDefaultImageOp() {
        return new BrushedMetalFilter();
    }

    enum GradientDirection {
        TOP_TO_BOTTOM, LEFT_TO_RIGTH
    }

    static GradientFilter getGradientFilter(Rectangle r, GradientDirection gd,
            Color col) {
        int color1 = Color.WHITE.getRGB();
        int color2 = col.getRGB();
        Point p1 = null;
        Point p2 = null;
        if (gd == GradientDirection.TOP_TO_BOTTOM) {
            p1 = new Point(r.width / 2, 0);
            p2 = new Point(p1);
            p2.translate(0, r.height);
        } else if (gd == GradientDirection.LEFT_TO_RIGTH) {
            p1 = new Point(0, r.height / 2);
            p2 = new Point(p1);
            p2.translate(r.width, 0);
        }
        boolean repeat = false;
        int type = GradientFilter.LINEAR;
        int interpolation = GradientFilter.INT_SMOOTH;
        GradientFilter filter = new GradientFilter(p1, p2, color1, color2,
                repeat, type, interpolation);
        return filter;
    }

    static FilteredImage getDefaultFilteredImage(Rectangle r) {
        return new FilteredImage(r, getDefaultImageOp());
    }

    static class FilteredImage extends BufferedImage {

        private static int IMAGE_TYPE = BufferedImage.TYPE_INT_RGB;

        FilteredImage(Rectangle r, BufferedImageOp op) {
            super(r.width, r.height, IMAGE_TYPE);
            doFilter(new BufferedImage(r.width, r.height, IMAGE_TYPE), r, op);
        }

        private void doFilter(BufferedImage src, Rectangle r, BufferedImageOp op) {
            op.filter(src, this);
        }

    }

    private class Painter {

        private Graphics2D g;

        private JPivot cp;

        Painter(Graphics graphics, JPivot c) {
            g = (Graphics2D) graphics;
            cp = c;
            g.setColor(Color.BLACK);
        }

        private void paintPlus(Point p, int size, Color c) {
            g.setColor(c);
            g.drawRect(p.x, p.y, size, size);
            int s2 = size / 2;
            int x2 = p.x + s2;
            int y2 = p.y + s2;
            int gap = (int) Math.round(size * 0.2);
            g.drawLine(p.x + gap, y2, p.x + size - gap, y2);
            g.drawLine(x2, p.y + gap, x2, p.y + size - gap);
        }

        private void paintMinus(Point p, int size, Color c) {
            g.setColor(c);
            g.drawRect(p.x, p.y, size, size);
            int s2 = size / 2;
            int y2 = p.y + s2;
            int gap = (int) Math.round(size * 0.2);
            g.drawLine(p.x + gap, y2, p.x + size - gap, y2);
        }

        void drawRectangle(Rectangle r) {
            g.drawRect(r.x, r.y, r.width, r.height);
        }

        void drawRectangle(Rectangle r, Color c) {
            g.setColor(c);
            drawRectangle(r);
        }

        @SuppressWarnings("unused")
        void drawFilteredImage(Rectangle r) {
            g.drawImage(getDefaultFilteredImage(r), r.x, r.y, null);
        }

        void drawImage(Rectangle r, BufferedImage image) {
            g.drawImage(image, r.x, r.y, null);
        }

        private void paintHeaderCell(HeaderCell cell, GradientDirection gd) {
            Color col = getCellHeaderColor(cell);
            if (cell.header.isLeft()) {
                if (cell.yIndex == cp.model.getSelected().y) {
                    col = getSelectedHeaderBackgroundColor(cell.xIndex,
                            cell.yIndex);
                }
            } else if (cell.header.isTop()) {
                if (cell.xIndex == cp.model.getSelected().x) {
                    col = getSelectedHeaderBackgroundColor(cell.xIndex,
                            cell.yIndex);
                }
            }

            FilteredImage canvas = new FilteredImage(cell.rect,
                    getGradientFilter(cell.rect, gd, col));
            Graphics2D g2 = canvas.createGraphics();

            Shape clip = g2.getClip();
            if (cell.isCollapsible()) {
                int _v = HEADER_CELL_MARGIN;
                if (cell.isVertOrientation()) {
                    g2.setClip(0, _v, cell.rect.width, cell.rect.height);
                } else if (cell.isHorizOrientation()) {
                    g2.setClip(_v, 0, cell.rect.width, cell.rect.height);
                }
            }

            try {
                boolean doPaint = !cell.isCollapsed()
                        || cell.header.isTop()
                        || (cell.isCollapsed() && cell.isHorizOrientation() && cell.header
                                .isLeft())
                        || (cell.isCollapsed() && cell.isVertOrientation() && cell.header
                                .isTop());
                if (doPaint) {
                    Rectangle r = null;
                    int offset = cell.isCollapsible() ? HEADER_CELL_MARGIN : 0;
                    if (cell.isHorizOrientation()) {
                        r = new Rectangle(offset, 0, cell.rect.width - offset,
                                cell.rect.height);
                    } else if (cell.isVertOrientation()) {
                        r = new Rectangle(0, offset, cell.rect.height - offset,
                                cell.rect.width);
                    }
                    cp.renderer.renderHeaderCell(g2, cell, r,
                            cp.model.getHeaderCell(cell));
                }
            } catch (Exception e) {
                LOG.severe(e.getMessage());
                e.printStackTrace();
            }

            g2.setClip(clip);
            drawImage(cell.rect, canvas);
        }

        private int paintHeader(HeaderCell item, int left, int top) {
            int result = 0;
            GradientDirection gd = null;
            if (item.header.isTop()) {
                result = left + item.rect.width;
                gd = GradientDirection.TOP_TO_BOTTOM;
            } else if (item.header.isLeft()) {
                result = top + item.rect.height;
                gd = GradientDirection.LEFT_TO_RIGTH;
            } else {
                throw new IllegalStateException();
            }

            boolean doPaint = !(item.isRoot() && !cp.model.showRoot);

            if (doPaint) {
                paintHeaderCell(item, gd);
                drawRectangle(item.rect, HEADER_CELL_BORDER_COLOR);

                if (item.isCollapsible()) {
                    Point p = item.rect.getLocation();
                    p.translate(PLUS_OFFSET, PLUS_OFFSET);

                    if (item.isCollapsed())
                        paintPlus(p, PLUS_SIZE, HEADER_TEXT_COLOR);
                    else
                        paintMinus(p, PLUS_SIZE, HEADER_TEXT_COLOR);
                }
            }

            if (!item.isCollapsed()) {
                int dv = 0;
                if (item.header.isTop()) {
                    dv = left;
                } else if (item.header.isLeft()) {
                    dv = top;
                }
                for (int i = 0; i < item.childs.size(); i++) {
                    HeaderCell child = item.childs.get(i);
                    if (item.header.isTop()) {
                        dv = paintHeader(child, dv, top + item.rect.height);
                    } else if (item.header.isLeft()) {
                        dv = paintHeader(child, left + item.rect.width, dv);
                    }
                }
            }
            return result;
        }

        private boolean isRowSelected(HeaderCell yCell) {
            return cp.model.getSelected().y == yCell.yIndex;
        }

        private boolean isColSelected(HeaderCell xCell) {
            return (cp.model.getSelected().x == xCell.xIndex);
        }

        private int getSelectedCellBorderWidth(HeaderCell xCell,
                HeaderCell yCell) {
            return SELECTED_CELL_BORDER_WIDTH;
        }

        private int getCellBorderWidth(HeaderCell xCell, HeaderCell yCell) {
            return 1;
        }

        private Color getSelectedCellBorderColor(HeaderCell xCell,
                HeaderCell yCell) {
            return cp.isFocusOwner()
                    ? FOCUSED_AND_SELECTED_CELL_BORDER_COLOR
                    : getCellBorderColor(xCell, yCell);
        }

        private Color getCellBorderColor(HeaderCell xCell, HeaderCell yCell) {
            return CELL_BORDER_COLOR;
        }

        private Color getSelectedHeaderBackgroundColor(int x, int y) {
            return SELECTED_HEADER_BG_COLOR;
        }

        private Color getCellHeaderColor(HeaderCell cell) {
            Color col = HEADER_BG_COLOR;
            if (cp.isFocusOwner() && cell.isRoot()) {
                col = FOCUSED_ROOT_HEADER_BG_COLOR;
            } else {
                col = cell.getBackgroundColor() == null
                        ? HEADER_BG_COLOR
                        : cell.getBackgroundColor();
            }
            return col;
        }

        private Color getSelectedCellBackgroundColor(HeaderCell xCell,
                HeaderCell yCell) {
            return getCellBackgroundColor(xCell, yCell);
        }

        private Color getSelectedRowOrColBackgroundColor(HeaderCell xCell,
                HeaderCell yCell) {
            return getCellBackgroundColor(xCell, yCell);
        }

        private Color getCellBackgroundColor(HeaderCell xCell, HeaderCell yCell) {
            return CELL_BG_COLOR;
        }

        private Color getSelectedCellColor(HeaderCell xCell, HeaderCell yCell) {
            return getCellColor(xCell, yCell);
        }

        private Color getCellColor(HeaderCell xCell, HeaderCell yCell) {
            return CELL_COLOR;
        }

        private boolean isPaintDataCell(HeaderCell xCell, HeaderCell yCell) {
            boolean xV = xCell.isCollapsed() && xCell.isMinimizeOnCollapse();
            boolean yV = yCell.isCollapsed() && yCell.isMinimizeOnCollapse();
            return !xV && !yV;
        }

        private void paintDataCell(HeaderCell xCell, HeaderCell yCell,
                Rectangle r, TableCell cell) {

            Color bg = null;
            boolean colSel = isColSelected(xCell);
            boolean rowSel = isRowSelected(yCell);
            boolean cellSel = colSel && rowSel;
            if (cellSel) {
                bg = getSelectedCellBackgroundColor(xCell, yCell);
            } else {
                if (colSel || rowSel) {
                    bg = getSelectedRowOrColBackgroundColor(xCell, yCell);
                } else {
                    bg = cell.attribute.background;
                    if (bg == null) {
                        bg = getCellBackgroundColor(xCell, yCell);
                    }
                }
            }

            g.setBackground(bg);
            int cellBorderWidth = getCellBorderWidth(xCell, yCell);
            g.clearRect(r.x + cellBorderWidth, r.y + cellBorderWidth, r.width,
                    r.height);

            if (!isPaintDataCell(xCell, yCell))
                return;

            Shape clip = g.getClip();
            g.setClip(r);
            try {
                Object rawValue = cp.model.getDataCell(xCell, yCell);
                Object formattedValue = cp.renderer.formatDataCell(xCell,
                        yCell, cell.attribute, rawValue);
                Color fg = cell.attribute.color;
                if (fg == null) {
                    if (cellSel) {
                        fg = getSelectedCellColor(xCell, yCell);
                    } else {
                        fg = getCellColor(xCell, yCell);
                    }
                }
                g.setColor(fg);
                cp.renderer.renderDataCell(g, xCell, yCell, r, cell.attribute,
                        formattedValue);
            } catch (Exception e) {
                LOG.severe(e.getMessage());
                e.printStackTrace();
            }
            g.setClip(clip);

            Color borderColor = null;
            int borderWidth = 1;
            if (cellSel) {
                borderColor = getSelectedCellBorderColor(xCell, yCell);
                borderWidth = getSelectedCellBorderWidth(xCell, yCell);
            } else {
                borderColor = cell.attribute.borderColor;
                if (borderColor == null) {
                    borderColor = getCellBorderColor(xCell, yCell);
                }
                borderWidth = cellBorderWidth;
            }

            g.setColor(borderColor);
            Stroke s = g.getStroke();
            g.setStroke(new BasicStroke(borderWidth));
            int b = borderWidth - 1;
            g.drawRect(r.x + b, r.y + b, r.width - b, r.height - b);
            g.setStroke(s);
        }

        private void paintDataTable(DataTable item) {
            for (HeaderCell xCell : cp.model.dataTable.xCells()) {
                for (HeaderCell yCell : cp.model.dataTable.yCells()) {
                    HeaderCell xc = xCell;
                    HeaderCell yc = yCell;
                    if (cp.model.transposed) {
                        xc = yCell;
                        yc = xCell;
                    }
                    TableCell cell = cp.model.dataTable.cells.get(xc, yc);
                    if (cell != null)
                        paintDataCell(xc, yc, cell.rect, cell);
                }
            }
        }

        private void paintDefault() {
            g.clearRect(cp.getX(), cp.getY(), cp.getWidth(), cp.getHeight());
        }

        private void paint() {
            if (cp == null)
                throw new IllegalStateException();

            if (cp.model == null) {
                paintDefault();
                return;
            }

            if (cp.isPaintMask(PivotModel.MASK_PAINT_TOP_HEADER)) {
                paintHeader(cp.model.getHeaderTop().cell,
                        cp.model.getHeaderTop().rect.x,
                        cp.model.getHeaderTop().rect.y);
            }

            if (cp.isPaintMask(PivotModel.MASK_PAINT_LEFT_HEADER)) {
                paintHeader(cp.model.getHeaderLeft().cell,
                        cp.model.getHeaderLeft().rect.x,
                        cp.model.getHeaderLeft().rect.y);
            }

            if (cp.isPaintMask(PivotModel.MASK_PAINT_DATA)) {
                paintDataTable(cp.model.dataTable);
            }
        }
    }
}
