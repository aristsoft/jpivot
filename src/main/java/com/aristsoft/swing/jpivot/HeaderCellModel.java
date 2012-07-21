package com.aristsoft.swing.jpivot;

import java.awt.Color;
import java.awt.Dimension;

public interface HeaderCellModel {

    Alignment getAlign();
    boolean isCollapsed();
    Boolean isCollapsible();
    Color getBackgroundColor();
    Dimension getSize();
    HeaderCellModel setBaseModel(HeaderCellModel base);
    Object getKey();

    HeaderCellModel setSize(Dimension size);
    HeaderCellModel setCollapsed(boolean value);

}
