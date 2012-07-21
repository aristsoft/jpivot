package com.aristsoft.swing.jpivot;

import java.util.ArrayList;
import java.util.List;

public class Header extends ModelArea {

    Type type = Type.NONE;
    HeaderCell cell = new HeaderCell(this, null, null, 0);

    List<HeaderCell> leafCells = new ArrayList<HeaderCell>();

    public enum Type {
        NONE, TOP, LEFT
    }

    Header(PivotModel model, Type type) {
        super(model);
        this.type = type;
    }

    void setWidthIfGrater(int x) {
        if (rect.x + rect.width < x)
            rect.width = x - rect.x;
    }

    void setHeightIfGrater(int y) {
        if (rect.y + rect.height < y)
            rect.height = y - rect.y;
    }

    void clearLeafCells() {
        leafCells.clear();
    }

    void addLeafCell(HeaderCell cell) {
        leafCells.add(cell);
    }

    boolean isTop() {
        boolean v = type == Type.TOP;
        if (model.transposed)
            v = !v;
        return v;
    }

    boolean isLeft() {
        boolean v = type == Type.LEFT;
        if (model.transposed)
            v = !v;
        return v;
    }

    public HeaderCell getCell() {
        return cell;
    }

    @Override
    void reset() {
        zeroRect(rect);
    }
    
}
