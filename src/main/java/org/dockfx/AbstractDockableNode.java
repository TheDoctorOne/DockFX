package org.dockfx;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public abstract class AbstractDockableNode implements DockableNode {

    protected BooleanProperty closeProperty = new SimpleBooleanProperty(false);
    protected DockPane mDockPane;

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
}
