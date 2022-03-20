package org.dockfx;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public abstract class AbstractDockableNode implements DockableNode {

    protected BooleanProperty closeProperty = new SimpleBooleanProperty(false);
    protected DockPane mDockPane;
    protected DockNode mDockNode;

    @Override
    public BooleanProperty getCloseProperty() {
        return closeProperty;
    }

    @Override
    public void setDockPane(DockPane dockPane) {
        mDockPane = dockPane;
    }

    @Override
    public DockPane getDockPane() {
        return mDockPane;
    }

    /**
     * Owning dock node.
     *
     * @param dockNode
     */
    @Override
    public void setDockNode(DockNode dockNode) {
        mDockNode = dockNode;
    }

    /**
     * @return Owning dock node.
     */
    @Override
    public DockNode getDockNode() {
        return mDockNode;
    }

}
