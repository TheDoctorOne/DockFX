package org.dockfx;

import javafx.beans.property.BooleanProperty;
import javafx.scene.Node;
import javafx.scene.image.ImageView;

/**
 * setDockNode({@link DockNode}) and setDockPane({@link DockPane}) will be <br>
 * called by {@link BasicFXMLDockPaneAdapter} when loading the FXML File.  <br>
 * Be sure to return them with getDockNode() and getDockPane()
 * */
public interface DockableNodeController {

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

    /**
     * The initial title. Changing the return value won't effect the Title. <br>
     * See getDockNode().setTitle()
     * */
    String getDockTitle();

    /**
     * The initial DocPos. Changing the return value won't effect the Title.
     * */
    DockPos getDocPos();

    /**
     * Owning dock node.
     * */
    void setDockNode(DockNode dockNode);

    /**
     * @return Owning dock node.
     * */
    DockNode getDockNode();

    void setDockPane(DockPane dockPane);

    DockPane getDockPane();

    /**
     * Dockable's Icon
     * */
    default Node getGraphic() {
        return new ImageView(DockPane.class.getResource("docknode.png").toExternalForm());
    }

}
