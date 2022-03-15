package org.dockfx.demo.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import org.dockfx.DockableNode;

import java.net.URL;
import java.util.ResourceBundle;

public class DemoFXML implements DockableNode,Initializable {

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
        System.out.println("Initialize demofxml");
        mainPane.setStyle("-fx-background-color: blue");
    }

    /**
     * On Dockable closed.
     */
    @Override
    public void onClosedEvent() {
        System.out.println("Demo FXML Closed.");
    }
}
