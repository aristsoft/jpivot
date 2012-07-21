package com.aristsoft.swing.jpivot;

public class DefaultModel extends PivotModel {

    public DefaultModel() {
        super();
        init();
        update();
    }

    protected void init() {
        populateExample(headerLeft, headerLeft.cell, 1, 3);
        populateExample(headerTop, headerTop.cell, 1, 3);
        headerTop.cell.setSize(80, 2);
        headerLeft.cell.collapse(2);
    }

    public static void populateExample(Header h, HeaderCell parent, int level,
            int maxLevel) {
        if (level > maxLevel) {
            return;
        }
        for (int j = 0; j < (level + 1); j++) {
            HeaderCell child = parent.addChild(null);
            populateExample(h, child, level + 1, maxLevel);
        }
    }

    @Override
    public Object getHeaderCell(HeaderCell cell) {
        return getDefaultHeaderCell(cell);
    }

    public static String getDefaultHeaderCell(HeaderCell cell) {
        return "" + cell.getIndexPath() + ":" + cell.getLevel();
    }

    @Override
    public Object getDataCell(HeaderCell xCell, HeaderCell yCell) {
        return getDefaultDataCellValue(xCell, yCell);
    }

    public static String getDefaultDataCellValue(HeaderCell xCell, HeaderCell yCell) {
        return xCell.xIndex + "," + yCell.yIndex;
    }
}
