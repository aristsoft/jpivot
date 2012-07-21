package com.aristsoft.swing.jpivot;

public class HeaderInfo extends ModelArea {

    HeaderInfo(PivotModel model) {
        super(model);
    }
    
    @Override
    void reset() {
        zeroRect(rect);
    }

}
