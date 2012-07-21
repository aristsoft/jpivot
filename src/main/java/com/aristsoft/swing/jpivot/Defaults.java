package com.aristsoft.swing.jpivot;

import java.awt.Color;

public interface Defaults {
    int LEFT_OFFSET = 2;
    int TOP_OFFSET = 2;

    int PLUS_SIZE = 10;
    int PLUS_OFFSET = PLUS_SIZE / 2;
    int HEADER_CELL_MARGIN = 2 * PLUS_OFFSET + PLUS_SIZE;

    int DEFAULT_WIDTH = 70;
    int DEFAULT_HEIGHT = 20;

    int MIN_WIDTH = HEADER_CELL_MARGIN;
    int MAX_WIDTH = 5 * DEFAULT_WIDTH;

    int CELL_HEIGHT = DEFAULT_HEIGHT;
    int LEFT_WIDTH = CELL_HEIGHT;

    int COLLAPSE_LEVEL = 5;

    int CELL_BORDER_WIDTH = 1;
    int SELECTED_CELL_BORDER_WIDTH = 2;
    Color CELL_BG_COLOR = Color.WHITE;
    Color CELL_COLOR = Color.BLACK;

    Color HEADER_CELL_BORDER_COLOR = Color.GRAY;
    Color HEADER_TEXT_COLOR = HEADER_CELL_BORDER_COLOR;
    Color HEADER_BG_COLOR = Color.LIGHT_GRAY;
    Color FOCUSED_ROOT_HEADER_BG_COLOR = Color.GRAY;

    Color CELL_BORDER_COLOR = Color.LIGHT_GRAY;
    Color FOCUSED_AND_SELECTED_CELL_BORDER_COLOR = CELL_BORDER_COLOR;
    Color SELECTED_CELL_BORDER_COLOR = CELL_BORDER_COLOR;

    Color SELECTED_HEADER_BG_COLOR = Color.ORANGE;

    Alignment HEADER_CELL_ALIGN = Alignment.Center;

}