package com.aristsoft.swing.jpivot;

import java.util.List;

public class DataTable extends ModelArea {

    DualMap<HeaderCell, TableCell> cells = new DualMap<HeaderCell, TableCell>();

    DataTable(PivotModel model) {
        super(model);
    }

    @Override
    void reset() {
        super.reset();
        cells.clear();
    }

    List<HeaderCell> xCells() {
        return !model.transposed ? cells.getKeys1() : cells.getKeys2();
    }

    List<HeaderCell> yCells() {
        return !model.transposed ? cells.getKeys2() : cells.getKeys1();
    }
}
