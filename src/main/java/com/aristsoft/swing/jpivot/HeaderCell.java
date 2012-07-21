package com.aristsoft.swing.jpivot;

import static com.aristsoft.swing.jpivot.HeaderCell.Orientation.HORIZONTAL;
import static com.aristsoft.swing.jpivot.HeaderCell.Orientation.VERTICAL;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aristsoft.swing.jpivot.Header.Type;

public class HeaderCell extends Area implements Defaults, HeaderCellModel {

    public interface Action {
        boolean doAction(HeaderCell cell);
    }

    static class CollapseAction implements Action {
        private boolean collapse = false;

        CollapseAction(boolean collapse) {
            this.collapse = collapse;
        }

        @Override
        public boolean doAction(HeaderCell cell) {
            cell.collapsed = collapse;
            return true;
        }
    };

    static class CollapseLevelAction implements Action {
        private boolean collapse = false;
        private int lvl = -1;

        CollapseLevelAction(boolean collapse, int lvl) {
            this.collapse = collapse;
            this.lvl = lvl;
        }

        @Override
        public boolean doAction(HeaderCell cell) {
            if (cell.getLevel() == lvl) {
                cell.collapsed = collapse;
            }
            return true;
        }
    };

    static class ChangeSizeAction implements Action {
        private int sz = 0;

        ChangeSizeAction(int sz) {
            this.sz = sz;
        }

        @Override
        public boolean doAction(HeaderCell cell) {
            if (cell.header.type == Type.LEFT) {
                cell.sz.width = sz;
                cell.orientation = Orientation.HORIZONTAL;
            } else if (cell.header.type == Type.TOP) {
                cell.sz.height = sz;
                cell.orientation = Orientation.VERTICAL;
            }
            return true;
        }
    };

    static class ChangeSizeActionLevelEq extends ChangeSizeAction {

        private int lvl = -1;

        ChangeSizeActionLevelEq(int sz, int lvl) {
            super(sz);
            this.lvl = lvl;
        }

        @Override
        public boolean doAction(HeaderCell cell) {
            if (cell.getLevel() == lvl) {
                super.doAction(cell);
            }
            return true;
        }
    }

    public enum Orientation {
        DEFAULT, HORIZONTAL, VERTICAL
    }

    Map<Object, Object> clientProperties = new HashMap<Object, Object>();
    Header header;
    Object userObject;
    HeaderCell parent;
    Dimension sz = new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    CellAttribute attribute = new CellAttribute();

    private int lvl = -1;
    private Orientation orientation = Orientation.DEFAULT;

    List<HeaderCell> childs = new ArrayList<HeaderCell>();

    int xIndex = -1;
    int yIndex = -1;

    HeaderCellModel model = this;
    // Provided by model
    boolean collapsed = false;
    Boolean collapsible = null;
    Color backgroundColor = null;
    Alignment align = HEADER_CELL_ALIGN;
    Boolean minimizeOnCollapse = null;

    public HeaderCell(Header header, HeaderCell parent, Object userObject,
            int level) {
        this.header = header;
        this.lvl = level;

        setUserObject(userObject);
        collapsed = lvl == COLLAPSE_LEVEL;
        if (parent != null) {
            this.backgroundColor = parent.model.getBackgroundColor();
            this.align = parent.model.getAlign();
        }

        setParent(parent);
    }

    public HeaderCell addChild(Object userObject) {
        return new HeaderCell(this.header, this, userObject, this.lvl + 1);
    }

    void setParent(HeaderCell parent) {
        this.parent = parent;
        if (parent != null)
            parent.childs.add(this);
    }

    void setXIndex(int xInd) {
        this.xIndex = xInd;
        if (parent != null)
            parent.setXIndex(xInd);
    }

    void setYIndex(int yInd) {
        this.yIndex = yInd;
        if (parent != null)
            parent.setYIndex(yInd);
    }

    private static int culcLeafChilds(HeaderCell item) {
        if (item.childs.isEmpty() || item.isCollapsed())
            return 1;
        int result = 0;
        for (HeaderCell child : item.childs) {
            result += culcLeafChilds(child);
        }
        return result;
    }

    public int getSz() {
        int result = 0;
        int _sz = 0;
        boolean _minOnCol = false;
        if (header.isLeft()) {
            _sz = getSize().height;
            _minOnCol = header.model.minimizeOnCollapseY;
        } else if (header.isTop()) {
            _sz = getSize().width;
            _minOnCol = header.model.minimizeOnCollapseX;
        } else {
            throw new IllegalStateException();
        }
        boolean v = (_minOnCol && isCollapsed())
                || (isMinimizeOnCollapse() && isCollapsed());
        result = v ? MIN_WIDTH : _sz;
        return result;
    }

    public int transposedSz(Type type) {
        int result = 0;
        if (type == Type.LEFT) {
            result = isDefaultOrientation()
                    ? LEFT_WIDTH
                    : (!header.model.transposed
                            ? getSize().width
                            : getSize().height);
        } else if (type == Type.TOP) {
            result = isDefaultOrientation()
                    ? CELL_HEIGHT
                    : (!header.model.transposed
                            ? getSize().height
                            : getSize().width);
        } else {
            throw new IllegalArgumentException();
        }
        return result;
    }

    private static int culcLeafSz(HeaderCell item) {
        int result = 0;
        if (item.childs.isEmpty() || item.isCollapsed()) {
            return item.getSz();
        }

        for (HeaderCell child : item.childs) {
            result += culcLeafSz(child);
        }
        return result;
    }

    int totalLeafChilds() {
        return culcLeafChilds(this);
    }

    int totalLeafSz() {
        return culcLeafSz(this);
    }

    boolean checkClicked(Point p) {
        return checkClicked(this, p);
    }

    static boolean checkClicked(HeaderCell cell, Point p) {
        if (cell.rect.contains(p)) {
            if (cell.isRoot() && !cell.header.model.showRoot)
                return false;

            if (cell.isCollapsible()) {
                cell.model.setCollapsed(!cell.isCollapsed());
            }
            return true;
        }
        for (HeaderCell child : cell.childs) {
            boolean res = checkClicked(child, p);
            if (res)
                return true;
        }
        return false;
    }

    @Override
    void reset() {
        super.reset();
        for (HeaderCell child : childs) {
            child.reset();
        }
    }

    boolean isLeaf() {
        return childs.isEmpty();
    }

    boolean isRoot() {
        return parent == null;
    }

    public int getLevel() {
        int result = -1;
        HeaderCell cur = this;
        while (cur != null) {
            result += 1;
            cur = cur.parent;
        }
        return result;
    }

    public Object getUserObject() {
        return userObject;
    }

    public void setUserObject(Object value) {
        this.userObject = value;
        if (this.userObject != null
                && this.userObject instanceof HeaderCellModel) {
            model = (HeaderCellModel) this.userObject;
            model.setBaseModel(this);
        } else {
            model = this;
        }
    }

    public Header getHeader() {
        return header;
    }

    boolean isResizeEnabled() {
        boolean result = false;
        if (header.isLeft()) {
            result = false;
        } else if (header.isTop()) {
            result = (header.model.minimizeOnCollapseX && !isCollapsed())
                    || !header.model.minimizeOnCollapseX;
        }
        return result;
    }

    public HeaderCell setCollapsible(boolean value) {
        collapsible = value;
        if (!collapsible) {
            collapsed = false;
        }
        return this;
    }

    public static Orientation transposeOrientation(Orientation o) {
        Orientation result = o;
        if (result == Orientation.HORIZONTAL) {
            result = Orientation.VERTICAL;
        } else if (result == Orientation.VERTICAL) {
            result = Orientation.HORIZONTAL;
        }
        return result;
    }

    public Orientation getOrientation() {
        Orientation result = null;
        Orientation o = header.model.transposed
                ? transposeOrientation(orientation)
                : orientation;

        if (header.isLeft()) {
            result = o == Orientation.DEFAULT ? VERTICAL : o;
        } else if (header.isTop()) {
            result = o == Orientation.DEFAULT ? HORIZONTAL : o;
        } else
            throw new IllegalStateException();
        return result;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    @Override
    public Dimension getSize() {
        return sz;
    }

    public HeaderCell setWidth(int width) {
        getSize().width = width;
        return this;
    }

    public int getWidth() {
        return getSize().width;
    }

    boolean isDefaultOrientation() {
        return orientation == Orientation.DEFAULT;
    }

    boolean isHorizOrientation() {
        return getOrientation() == Orientation.HORIZONTAL;
    }

    boolean isVertOrientation() {
        return getOrientation() == Orientation.VERTICAL;
    }

    @Override
    public Boolean isCollapsible() {
        if (collapsible == null) {
            boolean minOnCollapse = false;
            if (header.isTop()) {
                minOnCollapse = header.model.minimizeOnCollapseX;
            } else if (header.isLeft()) {
                minOnCollapse = header.model.minimizeOnCollapseY;
            }
            return !isLeaf() || minOnCollapse;
        }

        return collapsible;
    }

    @Override
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color col) {
        backgroundColor = col;
    }

    @Override
    public Alignment getAlign() {
        return align;
    }

    @Override
    public boolean isCollapsed() {
        return collapsed;
    }

    @Override
    public HeaderCell setCollapsed(boolean value) {
        collapsed = value;
        return this;
    }

    @Override
    public HeaderCell setBaseModel(HeaderCellModel base) {
        return this;
    }

    public static String toKey(HeaderCell cell) {
        String result = cell.getClass().getName() + "::";
        result += cell.getIndexPath() + "::";

        Object obj = cell.getUserObject();
        if (obj != null) {
            result += obj.getClass().getName() + "::" + obj.toString() + "::";
        }

        result = result + (cell.header.isTop() ? "top" : "*") + "_"
                + (cell.header.isLeft() ? "left" : "*");

        return result;
    }

    @Override
    public Object getKey() {
        Object result = null;
        if (isRoot()) {
            String s = this.getClass().getName();
            if (header.isLeft()) {
                s += "_left_root";
            } else if (header.isTop()) {
                s += "_top_root";
            }
            result = s;
        } else {
            result = toKey(this);
        }
        return result;
    }

    @Override
    public HeaderCell setSize(Dimension size) {
        sz.setSize(size);
        return this;
    }

    public Map<Object, Object> getClientProperties() {
        return clientProperties;
    }

    public Object get(Object key) {
        return getClientProperties().get(key);
    }

    public HeaderCell set(Object key, Object value) {
        getClientProperties().put(key, value);
        return this;
    }

    public int getIngex() {
        int result = 0;
        if (parent != null) {
            result = parent.childs.indexOf(this);
        }
        return result;
    }

    public String getIndexPath() {
        String result = "0";
        HeaderCell cur = this;
        while (cur != null) {
            result += "." + getIngex();
            cur = cur.parent;
        }
        return result;
    }

    public void setMinimizeOnCollapse(boolean v) {
        minimizeOnCollapse = v;
    }

    public boolean isMinimizeOnCollapse() {
        boolean result = false;
        if (minimizeOnCollapse == null) {
            if (header.isLeft()) {
                result = header.model.minimizeOnCollapseY;
            } else if (header.isTop()) {
                result = header.model.minimizeOnCollapseX;
            } else
                throw new IllegalStateException();
        } else
            result = minimizeOnCollapse;
        return result;
    }

    public boolean foreach(Action action) {
        boolean result = action.doAction(this);
        if (result) {
            for (HeaderCell child : childs) {
                if (!child.foreach(action))
                    return false;
            }
        }
        return result;
    }

    public void collapseAll() {
        foreach(new CollapseAction(true));
    }

    public void expandAll() {
        foreach(new CollapseAction(false));
    }

    public void setSize(int sz, int lvl) {
        foreach(new ChangeSizeActionLevelEq(sz, lvl));
    }

    public void collapse(int level) {
        foreach(new CollapseLevelAction(true, level));
    }

    public void expand(int level) {
        foreach(new CollapseLevelAction(false, level));
    }
}
