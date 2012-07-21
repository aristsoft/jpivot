package com.aristsoft.swing.jpivot;

public interface StateStorage {

    void setModel(PivotModel model);
    
    void restore();
    
    void save();
    
    void clear();
    
}
