package org.dockfx.demo.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import org.dockfx.AbstractDockableNodeController;
import org.dockfx.DockPos;
import org.dockfx.DockableNodeController;

import java.net.URL;
import java.util.ResourceBundle;

public class DemoFXML extends AbstractDockableNodeController implements DockableNodeController,Initializable {

    @FXML
    private AnchorPane mainPane;

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  <tt>null</tt> if the location is not known.
     * @param resources The resources used to localize the root object, or <tt>null</tt> if
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainPane.setStyle("-fx-background-color: darkslateblue");
    }

    /**
     * On dockable closed
     */

    @Override
    public String getDockTitle() {
        return "Demo FXML";
    }

    @Override
    public DockPos getDocPos() {
        return DockPos.CENTER;
    }

}
