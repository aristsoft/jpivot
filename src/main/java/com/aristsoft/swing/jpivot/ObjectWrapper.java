package com.aristsoft.swing.jpivot;

import java.awt.Color;
import java.awt.Dimension;

public class ObjectWrapper<T> implements HeaderCellModel {

    protected HeaderCellModel base = null;
    protected T v = null;

    public ObjectWrapper(T v) {
        this.v = v;
    }

    @Override
    public String toString() {
        return v.toString();
    }

    @Override
    public Alignment getAlign() {
        return base.getAlign();
    }

    @Override
    public boolean isCollapsed() {
        return base.isCollapsed();
    }

    @Override
    public HeaderCellModel setCollapsed(boolean value) {
        base.setCollapsed(value);
        return this;
    }

    @Override
    public Boolean isCollapsible() {
        return base.isCollapsible();
    }

    @Override
    public Color getBackgroundColor() {
        return base.getBackgroundColor();
    }

    @Override
    public Dimension getSize() {
        return base.getSize();
    }

    @Override
    public HeaderCellModel setBaseModel(HeaderCellModel base) {
        this.base = base;
        return this;
    }

    @Override
    public Object getKey() {
        return this.base.getKey();
    }

    @Override
    public HeaderCellModel setSize(Dimension size) {
        this.base.setSize(size);
        return this;
    }

    public T getWrappedObject() {
        return v;
    }
}
