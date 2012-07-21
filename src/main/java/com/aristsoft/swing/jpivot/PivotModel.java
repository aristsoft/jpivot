package com.aristsoft.swing.jpivot;

import static com.aristsoft.swing.jpivot.ModelChangeEvent.EVENT_BEGIN_SPLIT_CELL;
import static com.aristsoft.swing.jpivot.ModelChangeEvent.EVENT_END_SPLIT_CELL;
import static com.aristsoft.swing.jpivot.ModelChangeEvent.EVENT_HEADER_CELL_CLICK;
import static com.aristsoft.swing.jpivot.ModelChangeEvent.EVENT_REPAINT;
import static com.aristsoft.swing.jpivot.ModelChangeEvent.EVENT_SELECTED_CELL_CHANGED;
import static com.aristsoft.swing.jpivot.ModelChangeEvent.EVENT_UPDATE;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeSupport;
import java.util.List;

import com.aristsoft.swing.jpivot.Header.Type;

public abstract class PivotModel extends ModelArea implements Defaults {

    public static int MASK_PAINT_INFO = 1;
    public static int MASK_PAINT_TOP_HEADER = 2;
    public static int MASK_PAINT_LEFT_HEADER = 4;
    public static int MASK_PAINT_DATA = 8;

    public static int MASK_PAINT_TOP_ALL = MASK_PAINT_INFO | MASK_PAINT_TOP_HEADER;

    public static int MASK_PAINT_BOTTOM_ALL = MASK_PAINT_LEFT_HEADER | MASK_PAINT_DATA;

    public static int MASK_PAINT_ALL = MASK_PAINT_TOP_ALL | MASK_PAINT_BOTTOM_ALL;

    Header headerLeft = new Header(this, Header.Type.LEFT);
    Header headerTop = new Header(this, Header.Type.TOP);

    DataTable dataTable = new DataTable(this);
    HeaderInfo headerInfo = new HeaderInfo(this);

    boolean showRoot = true;
    boolean minimizeOnCollapseX = false;
    boolean minimizeOnCollapseY = true;
    boolean transposed = false;

    Point cellSel = new Point();
    SplitContext splitContext = new SplitContext();

    PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    Point leftTop = new Point(LEFT_OFFSET, TOP_OFFSET);

    class SplitContext {
        HeaderCell cell = null;
        Point screenPoint = new Point();
        Dimension sz = new Dimension();
        boolean draging = false;

        boolean isReady() {
            return cell != null;
        }

        void reset() {
            draging = false;
            cell = null;
        }

        void begin() {
            if (draging)
                throw new IllegalStateException();
            sz.setSize(cell.getSize());
            draging = true;
            fireChange(EVENT_BEGIN_SPLIT_CELL, cell);
        }

        void end() {
            if (!draging)
                throw new IllegalStateException();
            fireChange(EVENT_END_SPLIT_CELL, cell);
            reset();
        }
    }

    protected PivotModel() {
        super(null);
        this.model = this;
        resetCellSelection();
    }

    @Override
    void reset() {
        super.reset();
        headerInfo.reset();
        getHeaderTop().reset();
        getHeaderLeft().reset();
        dataTable.reset();
    }

    public void update() {
        reset();

        rect.x = leftTop.x;
        rect.y = leftTop.y;

        headerInfo.rect.x = rect.x;
        headerInfo.rect.y = rect.y;

        upadteHeaderLeft();
        upadteHeaderTop();

        updateHeaderInfo();

        upadteHeaderTop();
        upadteHeaderLeft();

        updateDataTable();

        Dimension sz = new Dimension(leftTop.x + headerInfo.rect.width
                + dataTable.rect.width, leftTop.y + headerInfo.rect.height
                + dataTable.rect.height);

        fireChange(EVENT_UPDATE, sz);
    }

    public void updateDataTable() {
        dataTable.rect.x = headerInfo.rect.x + headerInfo.rect.width;
        dataTable.rect.y = headerInfo.rect.y + headerInfo.rect.height;

        if (model.transposed) {
            dataTable.rect.width = getHeaderTop().rect.width;
            dataTable.rect.height = getHeaderLeft().rect.height;
        } else {
            dataTable.rect.width = getHeaderTop().rect.width;
            dataTable.rect.height = getHeaderLeft().rect.height;
        }

        List<HeaderCell> xCells = getHeaderTop().leafCells;
        List<HeaderCell> yCells = getHeaderLeft().leafCells;

        int xCount = xCells.size();
        int yCount = yCells.size();

        for (int x = 0; x < xCount; x++) {
            HeaderCell xCell = xCells.get(x);
            xCell.setXIndex(x);
            for (int y = 0; y < yCount; y++) {
                HeaderCell yCell = yCells.get(y);
                yCell.setYIndex(y);
                TableCell cell = new TableCell(xCell, yCell);
                dataTable.cells.put(xCell, yCell, cell);
                cell.rect.setBounds(xCell.rect.x, yCell.rect.y, xCell.rect.width,
                        yCell.rect.height);
            }
        }
    }

    public void updateHeaderInfo() {
        headerInfo.rect.width = getHeaderLeft().rect.width;
        headerInfo.rect.height = getHeaderTop().rect.height;
    }

    private void upadteHeaderTop() {
        getHeaderTop().clearLeafCells();
        getHeaderTop().rect.x = headerInfo.rect.x + headerInfo.rect.width;
        getHeaderTop().rect.y = headerInfo.rect.y;

        getHeaderTop().rect.height = 0;
        getHeaderTop().rect.width = upadteHeaderCell(getHeaderTop().cell,
                getHeaderTop().rect.x, getHeaderTop().rect.y);

        getHeaderTop().rect.x = getHeaderTop().cell.rect.x;
        getHeaderTop().rect.y = getHeaderTop().cell.rect.y;

    }

    private void upadteHeaderLeft() {
        getHeaderLeft().clearLeafCells();
        getHeaderLeft().rect.x = headerInfo.rect.x;
        getHeaderLeft().rect.y = headerInfo.rect.y + headerInfo.rect.height;

        getHeaderLeft().rect.width = 0;
        getHeaderLeft().rect.height = upadteHeaderCell(getHeaderLeft().cell,
                getHeaderLeft().rect.x, getHeaderLeft().rect.y);

        getHeaderLeft().rect.x = getHeaderLeft().cell.rect.x;
        getHeaderLeft().rect.y = getHeaderLeft().cell.rect.y;
    }

    private static int upadteHeaderCell(HeaderCell item, int left, int top) {
        int sz = item.totalLeafSz();
        int result = 0;
        item.rect.x = left;
        item.rect.y = top;

        if (item.header.isLeft()) {
            item.rect.width = item.isDefaultOrientation() ? LEFT_WIDTH : item
                    .transposedSz(Type.LEFT);
            item.rect.height = sz;
            item.header.setWidthIfGrater(item.rect.x + item.rect.width);
            result = item.rect.height;
        } else if (item.header.isTop()) {
            item.rect.height = item.isDefaultOrientation() ? CELL_HEIGHT : item
                    .transposedSz(Type.TOP);
            item.rect.width = sz;
            item.header.setHeightIfGrater(item.rect.y + item.rect.height);
            result = item.rect.width;
        } else {
            throw new IllegalStateException();
        }

        if (item.isLeaf() || item.isCollapsed()) {
            item.header.addLeafCell(item);
        }

        int dv = 0;
        if (item.header.isLeft()) {
            dv = top;
        } else if (item.header.isTop()) {
            dv = left;
        } else
            throw new IllegalStateException();

        if (!item.isCollapsed()) {
            for (int i = 0; i < item.childs.size(); i++) {
                HeaderCell child = item.childs.get(i);
                if (item.header.isLeft()) {
                    dv += upadteHeaderCell(child, left + item.rect.width, dv);
                } else if (item.header.isTop()) {
                    dv += upadteHeaderCell(child, dv, top + item.rect.height);
                }
            }
        } else {
            for (HeaderCell child : item.childs) {
                child.reset();
            }
        }
        return result;
    }

    public void mouseDragged(MouseEvent e) {
        if (splitContext.draging) {
            int dx = e.getXOnScreen() - splitContext.screenPoint.x;
            int w = splitContext.sz.width + dx;
            if (w > HeaderCell.MIN_WIDTH && w < HeaderCell.MAX_WIDTH) {
                splitContext.cell.getSize().width = w;
                update();
                fireChange(EVENT_REPAINT);
            }
        }
    }

    public void mousePressed(MouseEvent e) {
        if (splitContext.isReady()) {
            splitContext.begin();
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (splitContext.draging) {
            splitContext.end();
            return;
        }

        if (getHeaderTop().cell.checkClicked(e.getPoint())
                || getHeaderLeft().cell.checkClicked(e.getPoint())) {
            model.update();
            fireChange(EVENT_REPAINT);
            fireChange(EVENT_HEADER_CELL_CLICK);
            return;
        }

        Point coord = new Point();
        if (findDataCell(coord, e.getPoint())) {
            setSelected(coord);
            return;
        }
    }

    private boolean findDataCell(Point coord, Point p) {
        for (TableCell cell : dataTable.cells.values()) {
            if (cell.rect.contains(p)) {
                coord.setLocation(cell.xCell.xIndex, cell.yCell.yIndex);
                return true;
            }
        }
        return false;
    }

    public void clearCellSelection() {
        getSelected().x = Integer.MIN_VALUE;
        getSelected().y = Integer.MIN_VALUE;
    }

    public void resetCellSelection() {
        getSelected().x = 0;
        getSelected().y = 0;
    }

    void cellSelectionMove(int dx, int dy) {
        int _x = (getSelected().x + dx + dataTable.xCells().size())
                % dataTable.xCells().size();
        int _y = (getSelected().y + dy + dataTable.yCells().size())
                % dataTable.yCells().size();
        Point p = new Point(_x, _y);
        setSelected(p);
    }

    public boolean handleMouseOverColumnSplitter(JPivot sender, MouseEvent e) {
        if (splitContext.draging) {
            return true;
        }

        splitContext.reset();
        boolean result = getHeaderTop().rect.contains(e.getPoint());
        if (result) {
            int x = e.getX();
            for (int i = 0; i < dataTable.xCells().size() - 1; i++) {
                HeaderCell c = dataTable.xCells().get(i);
                int GAP = 2;
                int x1 = c.rect.x + c.rect.width;
                int x2 = x1 + GAP;
                x1 -= GAP;
                result = (x >= x1 && x <= x2) && c.isResizeEnabled();
                if (result) {
                    splitContext.cell = c;
                    splitContext.screenPoint.setLocation(e.getXOnScreen(),
                            e.getYOnScreen());
                    break;
                }
            }
        }
        return result;
    }

    public void fireChange(String eventName) {
        fireChange(eventName, null);
    }

    void fireChange(String eventName, Object value) {
        ModelChangeEvent evt = new ModelChangeEvent(this, eventName, null, value);
        propertyChangeSupport.firePropertyChange(evt);
    }

    public Header getHeaderLeft() {
        return !transposed ? headerLeft : headerTop;
    }

    public Header getHeaderTop() {
        return !transposed ? headerTop : headerLeft;
    }

    public Point getSelected() {
        return cellSel;
    }

    public void setSelected(Point p) {
        getSelected().setLocation(p);
        fireChange(EVENT_REPAINT);
        fireChange(EVENT_SELECTED_CELL_CHANGED, getSelected());
    }

    public abstract Object getHeaderCell(HeaderCell cell);

    public abstract Object getDataCell(HeaderCell xCell, HeaderCell yCell);

    
}
