package com.aristsoft.swing.jpivot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

public class DefaultStateStorage implements StateStorage {

    static DefaultStateStorage instance = null;

    PivotModel model;
    Point cellSel = new Point();
    Map<Object, HeaderCellModel> cells = new HashMap<Object, HeaderCellModel>();

    DefaultStateStorage(JPivot pivot) {
        if (pivot != null)
            setModel(pivot.getModel());
    }

    @Override
    public void setModel(PivotModel model) {
        this.model = model;
    }

    private void saveHeaderCell(HeaderCell cell) {
        Object key = cell.getKey();
        if (key == null)
            return;
        HeaderCellModelBean v = new HeaderCellModelBean(key);
        copy(cell, v);
        cells.put(key, v);
        for (HeaderCell child : cell.childs) {
            saveHeaderCell(child);
        }
    }

    @Override
    public void save() {
        if (model == null)
            return;
        cellSel.setLocation(model.getSelected());
        saveHeaderCell(model.getHeaderLeft().getCell());
        saveHeaderCell(model.getHeaderTop().getCell());
    }

    private void restoreHeaderCell(HeaderCell cell) {
        Object key = cell.getKey();
        if (key == null)
            return;
        HeaderCellModel v = cells.get(key);
        if (v != null) {
            copy(v, cell);
        }
        for (HeaderCell child : cell.childs) {
            restoreHeaderCell(child);
        }
    }

    @Override
    public void restore() {
        if (model == null)
            return;
        model.getSelected().x = cellSel.x;
        model.getSelected().y = cellSel.y;
        restoreHeaderCell(model.getHeaderLeft().getCell());
        restoreHeaderCell(model.getHeaderTop().getCell());
    }

    public static void copy(HeaderCellModel src, HeaderCellModel dest) {
        dest.setCollapsed(src.isCollapsed());
        dest.setSize(src.getSize());
    }

    static class HeaderCellModelBean implements HeaderCellModel {

        Dimension size = new Dimension();
        boolean collapsed = false;
        Object key = null;

        HeaderCellModelBean(Object key) {
            this.key = key;
        }

        @Override
        public boolean isCollapsed() {
            return collapsed;
        }

        @Override
        public HeaderCellModel setCollapsed(boolean value) {
            collapsed = value;
            return this;
        }

        @Override
        public Dimension getSize() {
            return size;
        }

        @Override
        public HeaderCellModel setSize(Dimension size) {
            this.size.setSize(size);
            return this;
        }

        @Override
        public Object getKey() {
            return key;
        }

        @Override
        public String toString() {
            return "collapsed :" + collapsed + " ; size :" + size;
        }

        @Override
        public Alignment getAlign() {
            // Not supported yet
            return null;
        }

        @Override
        public Boolean isCollapsible() {
            // Not supported yet
            return null;
        }

        @Override
        public Color getBackgroundColor() {
            // Not supported yet
            return null;
        }

        @Override
        public HeaderCellModel setBaseModel(HeaderCellModel base) {
            return this;

        }
    }

    public static DefaultStateStorage getInstance() {
        if (instance == null) {
            instance = new DefaultStateStorage(null);
        }
        return instance;
    }

    @Override
    public void clear() {
        cells.clear();
    }
}
