package com.aristsoft.swing.jpivot.event;

import java.util.EventObject;

import com.aristsoft.swing.jpivot.JPivot;

@SuppressWarnings("serial")
public class PivotEvent extends EventObject {

    public PivotEvent(JPivot source) {
        super(source);
    }
    public JPivot getPivot() {
        return (JPivot) getSource();
    }

}
