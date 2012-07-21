package com.aristsoft.swing.jpivot;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.aristsoft.swing.jpivot.event.PivotListener;

@SuppressWarnings("serial")
public class JPivotExt extends JPanel implements Pivot {

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(JPivot.class.getName());

    private JPivot pivotTop;
    @SuppressWarnings("unused")
	private JPivot pivotLeft;
    private JPivot pivotData;

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignore) {
        }

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                JFrame frame = new JFrame();
                frame.setTitle(JPivotExt.class.getName());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());
                final JPivotExt jpivot = new JPivotExt();
                JScrollPane sp = new JScrollPane(jpivot);
                frame.getContentPane().add(sp, BorderLayout.CENTER);

                JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
                frame.getContentPane().add(toolbar, BorderLayout.NORTH);

                frame.setBounds(10, 10, 800, 600);
                frame.setVisible(true);
            }
        });
    }

    public JPivotExt() {
        this(new DefaultModel());
    }

    public JPivotExt(PivotModel model) {
        super();
        pivotTop = new JPivot(model);
        pivotTop.setPaintMask(PivotModel.MASK_PAINT_TOP_ALL);
        pivotLeft = new JPivot(model);
        pivotTop.setPaintMask(PivotModel.MASK_PAINT_LEFT_HEADER);
        pivotData = new JPivot(model);
        pivotData.setPaintMask(PivotModel.MASK_PAINT_DATA);
        //add(pivotTop, BorderLayout.NORTH);
        //add(pivotLeft, BorderLayout.WEST);
        add(pivotData, BorderLayout.CENTER);
    }

    @Override
    public void addPivotListener(PivotListener l) {
        // TODO Auto-generated method stub
    }

    @Override
    public void removePivotListener(PivotListener l) {
        // TODO Auto-generated method stub

    }

    @Override
    public PivotModel getModel() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setModel(PivotModel model) {
        // TODO Auto-generated method stub

    }

    @Override
    public PivotRenderer getRenderer() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setRenderer(PivotRenderer renderer) {
        // TODO Auto-generated method stub

    }

    @Override
    public StateStorage getStateStorage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setStateStorage(StateStorage storage) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isUseStateStorage() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setUseStateStorage(boolean value) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isMinimizeOnCollapseX() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isMinimizeOnCollapseY() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setMinimizeOnCollapseX(boolean v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setMinimizeOnCollapseY(boolean v) {
        // TODO Auto-generated method stub

    }

}
