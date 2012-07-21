package com.aristsoft.swing.jpivot;

import java.beans.PropertyChangeEvent;

@SuppressWarnings("serial")
class ModelChangeEvent extends PropertyChangeEvent {

    static final String EVENT_REPAINT = "repaint";
    static final String EVENT_UPDATE = "update";
    static final String EVENT_HEADER_CELL_CLICK = "headerCellClick";
    static final String EVENT_BEGIN_SPLIT_CELL = "beginSplitCell";
    static final String EVENT_END_SPLIT_CELL = "endSplitCell";
    static final String EVENT_SELECTED_CELL_CHANGED = "selectedCellChanged";

    ModelChangeEvent(PivotModel source, String propertyName, Object oldValue,
            Object newValue) {
        super(source, propertyName, oldValue, newValue);
    }

}
