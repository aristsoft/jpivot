package com.aristsoft.swing.jpivot;

import static com.aristsoft.swing.jpivot.ModelChangeEvent.EVENT_END_SPLIT_CELL;
import static com.aristsoft.swing.jpivot.ModelChangeEvent.EVENT_HEADER_CELL_CLICK;
import static com.aristsoft.swing.jpivot.ModelChangeEvent.EVENT_REPAINT;
import static com.aristsoft.swing.jpivot.ModelChangeEvent.EVENT_UPDATE;
import static com.aristsoft.swing.jpivot.ModelChangeEvent.EVENT_SELECTED_CELL_CHANGED;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.EventListenerList;

import com.aristsoft.swing.jpivot.event.PivotListener;

@SuppressWarnings("serial")
public class JPivot extends JPanel implements Pivot {

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    @SuppressWarnings("unused")
    private static final String uiClassID = "JPivotUI";

    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(JPivot.class.getName());

    PivotRenderer renderer = null;
    PivotModel model = null;
    StateStorage stateStorage = DefaultStateStorage.getInstance();
    boolean useStateStorage = true;
    private EventListenerList listenerList = new EventListenerList();
    int paintMask = PivotModel.MASK_PAINT_ALL;

    private FocusListener focusListener = new FocusListener() {

        @Override
        public void focusGained(FocusEvent e) {
            repaint();
        }

        @Override
        public void focusLost(FocusEvent e) {
            repaint();
        }

    };

    private MouseListener mouseListener = new MouseAdapter() {

        @Override
        public void mousePressed(MouseEvent e) {
            if (model == null)
                return;
            model.mousePressed(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (!JPivot.this.hasFocus())
                JPivot.this.grabFocus();
            if (model == null)
                return;
            model.mouseReleased(e);
        }
    };

    private KeyListener keyListener = new KeyAdapter() {

        void move(int dx, int dy) {
            model.cellSelectionMove(dx, dy);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (model == null)
                return;
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP :
                    move(0, -1);
                    break;
                case KeyEvent.VK_DOWN :
                    move(0, 1);
                    break;
                case KeyEvent.VK_LEFT :
                    move(-1, 0);
                    break;
                case KeyEvent.VK_RIGHT :
                    move(1, 0);
                    break;
                default :
                    break;
            }
        }
    };

    private MouseMotionListener mouseMotionListener = new MouseMotionListener() {

        @Override
        public void mouseMoved(MouseEvent e) {
            if (model == null)
                return;
            if (model.handleMouseOverColumnSplitter(JPivot.this, e)) {
                setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
            } else {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (model == null)
                return;
            model.mouseDragged(e);
        }
    };

    private static boolean is(String s, String... v) {
        boolean result = false;
        for (int i = 0; i < v.length; i++) {
            result = s.equals(v[i]);
            if (result)
                break;
        }
        return result;
    }

    private PropertyChangeListener modelChangeListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            ModelChangeEvent evt = (ModelChangeEvent) event;
            String e = evt.getPropertyName();

            if (e.equals(EVENT_UPDATE)) {
                Dimension sz = (Dimension) evt.getNewValue();
                JPivot.this.setPreferredSize(sz);
                JPivot.this.setSize(sz);
                return;
            }

            boolean doRepaint = is(e, EVENT_HEADER_CELL_CLICK, EVENT_REPAINT);
            if (doRepaint) {
                JPivot.this.repaint();
            }

            boolean doSaveState = is(e, EVENT_HEADER_CELL_CLICK, EVENT_UPDATE,
                    EVENT_SELECTED_CELL_CHANGED, EVENT_END_SPLIT_CELL);
            if (doSaveState) {
                if (isUseStateStorage())
                    getStateStorage().save();
            }
        }

    };

    static void debug(String s) {
        System.out.println(s);
    }

    public JPivot() {
        this(new DefaultModel());
    }

    public JPivot(PivotModel model) {
        super();
        setBorder(BorderFactory.createEmptyBorder());
        setFocusable(true);
        addMouseListener(mouseListener);
        addKeyListener(keyListener);
        addMouseMotionListener(mouseMotionListener);
        addFocusListener(focusListener);
        setPreferredSize(new Dimension());
        setRenderer(new DeafultRenderer());
        setModel(model);
    }

    @Override
    public void updateUI() {
        setUI(JPivotUI.createUI(this));
    }

    public boolean isPaintMask(int mask) {
        return (mask & paintMask) != 0;
    }

    public int getPaintMask() {
        return paintMask;
    }

    public void setPaintMask(int mask) {
        paintMask = mask;
        if (getModel() != null)
            getModel().fireChange(EVENT_REPAINT);
    }

    @Override
    public void addPivotListener(PivotListener l) {
        listenerList.add(PivotListener.class, l);
    }

    @Override
    public void removePivotListener(PivotListener l) {
        listenerList.remove(PivotListener.class, l);
    }

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignore) {
        }

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                JFrame frame = new JFrame();
                frame.setTitle(JPivot.class.getName());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());
                final JPivot jpivot = new JPivot();
                JScrollPane sp = new JScrollPane(jpivot);
                frame.getContentPane().add(sp, BorderLayout.CENTER);

                JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
                final JCheckBox cbTranspose = new JCheckBox("Transpose");
                toolbar.add(cbTranspose);
                cbTranspose.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        jpivot.setTransposed(cbTranspose.isSelected());
                    }
                });
                frame.getContentPane().add(toolbar, BorderLayout.NORTH);

                frame.setBounds(10, 10, 800, 600);
                frame.setVisible(true);
            }
        });
    }

    @Override
    public PivotModel getModel() {
        return model;
    }

    @Override
    public void setModel(PivotModel model) {
        PivotModel oldModel = this.model;
        if (model == oldModel)
            return;
        if (oldModel != null) {
            oldModel.propertyChangeSupport
                    .removePropertyChangeListener(modelChangeListener);
        }
        this.model = model;
        if (model != null) {
            model.propertyChangeSupport
                    .addPropertyChangeListener(modelChangeListener);
        }

        if (getStateStorage() != null) {
            getStateStorage().setModel(model);
        }

        if (isUseStateStorage()) {
            getStateStorage().restore();
        }

        firePropertyChange("model", oldModel, model);
        model.update();

        repaint();
    }

    @Override
    public PivotRenderer getRenderer() {
        return renderer;
    }

    @Override
    public void setRenderer(PivotRenderer renderer) {
        PivotRenderer oldRenderer = this.renderer;
        this.renderer = renderer;
        firePropertyChange("renderer", oldRenderer, renderer);
        repaint();
    }

    @Override
    public StateStorage getStateStorage() {
        return stateStorage;
    }

    @Override
    public void setStateStorage(StateStorage storage) {
        StateStorage oldStorage = this.stateStorage;
        this.stateStorage = storage;
        firePropertyChange("stateStorage", oldStorage, storage);
    }

    @Override
    public boolean isUseStateStorage() {
        return useStateStorage && (stateStorage != null);
    }

    @Override
    public void setUseStateStorage(boolean value) {
        useStateStorage = value;
    }

    @Override
    public boolean isMinimizeOnCollapseX() {
        return model != null ? model.minimizeOnCollapseX : false;
    }

    @Override
    public boolean isMinimizeOnCollapseY() {
        return model != null ? model.minimizeOnCollapseY : false;
    }

    @Override
    public void setMinimizeOnCollapseX(boolean v) {
        if (model != null && model.minimizeOnCollapseX != v) {
            model.minimizeOnCollapseX = v;
            model.update();
            repaint();
            firePropertyChange("minimizeOnCollapseX", !v, v);
        }
    }

    @Override
    public void setMinimizeOnCollapseY(boolean v) {
        if (model != null && model.minimizeOnCollapseY != v) {
            model.minimizeOnCollapseY = v;
            model.update();
            repaint();
            firePropertyChange("minimizeOnCollapseY", !v, v);
        }
    }

    public boolean isTransposed() {
        return model != null ? model.transposed : false;
    }

    public void setTransposed(boolean v) {
        if (model == null)
            return;
        model.transposed = v;
        model.update();
        repaint();
        firePropertyChange("transposed", !v, v);
    }
}
