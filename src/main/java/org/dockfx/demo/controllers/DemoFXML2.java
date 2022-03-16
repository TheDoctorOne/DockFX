package org.dockfx.demo.controllers;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import org.dockfx.DockPane;
import org.dockfx.DockPos;
import org.dockfx.DockableNode;

import java.net.URL;
import java.util.ResourceBundle;

public class DemoFXML2 implements DockableNode,Initializable {

    @FXML
    private AnchorPane mainPane;

    private final BooleanProperty closeProperty = new SimpleBooleanProperty(false);

    private DockPane dockPane;

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
        mainPane.setStyle("-fx-background-color: blue");
    }

    /**
     * On dockable closed
     */
    @Override
    public BooleanProperty getCloseProperty() {
        return closeProperty;
    }

    @Override
    public String getDockTitle() {
        return "Demo FXML2";
    }

    @Override
    public DockPos getDocPos() {
        return DockPos.CENTER;
    }

    @Override
    public void setDockPane(DockPane dockPane) {
        this.dockPane = dockPane;
    }

    @Override
    public DockPane getDockPane() {
        return dockPane;
    }
}
