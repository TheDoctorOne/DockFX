package org.dockfx;

import javafx.beans.property.BooleanProperty;
import javafx.scene.image.ImageView;



/**
 * Currently name is misleading. {@link DockNode} is the only real DockableNode.<br>
 * This interface is just a helper for the {@link BasicFXMLDockPaneAdapter}.
 * */
public interface DockableNode {

    /**
     * On dockable closed property. <br>
     * Ex: <br>
     * <code>
     *     private BooleanProperty closeProperty = new SimpleBooleanProperty(false); <br><br>
     *     BooleanProperty getCloseProperty() {<br>
     *         return closeProperty;<br>
     *     }<br>
     * </code>
     * */
    BooleanProperty getCloseProperty();

    String getDockTitle();

    DockPos getDocPos();

    /**
     * Dockable's Icon
     * */
    default ImageView getGraphic() { return null; }

}
