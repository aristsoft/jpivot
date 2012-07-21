package com.aristsoft.swing.jpivot;

public class TableCell extends Area {

    HeaderCell xCell;
    HeaderCell yCell;
    CellAttribute attribute = new CellAttribute();

    TableCell(HeaderCell xCell, HeaderCell yCell) {
        super();
        this.xCell = xCell;
        this.yCell = yCell;
    }
}
