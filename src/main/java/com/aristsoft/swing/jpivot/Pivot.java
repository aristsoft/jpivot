package com.aristsoft.swing.jpivot;

import com.aristsoft.swing.jpivot.event.PivotListener;

public interface Pivot {

    void addPivotListener(PivotListener l);
    
    void removePivotListener(PivotListener l);
    
    PivotModel getModel();
    
    void setModel(PivotModel model);
    
    PivotRenderer getRenderer();
    
    void setRenderer(PivotRenderer renderer);
    
    StateStorage getStateStorage();
    
    void setStateStorage(StateStorage storage);
    
    boolean isUseStateStorage();
    
    void setUseStateStorage(boolean value);
    
    boolean isMinimizeOnCollapseX();
    
    boolean isMinimizeOnCollapseY();
    
    void setMinimizeOnCollapseX(boolean v);
    
    void setMinimizeOnCollapseY(boolean v);
}
