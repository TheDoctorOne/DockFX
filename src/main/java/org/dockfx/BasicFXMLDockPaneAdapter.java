package org.dockfx;

import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Loads from FXML.
 * */
public class BasicFXMLDockPaneAdapter extends DockPane {

    private static class DockableFXML {
        private final String fxml;
        public final DockNode dockNode;
        public final DockableNode controller;

        private DockableFXML(DockableNode controller, String fxml, DockNode dockNode) {
            this.fxml = fxml;
            this.dockNode = dockNode;
            this.controller = controller;
        }
    }

    private final Map<Class<? extends DockableNode>, DockableFXML> dockables = new HashMap<>();
    private ResourceBundle resourceBundle;

    /**
     * FXMLs will use this resourceBundle. Set this first, will not update previously loaded FXMLs.
     * */
    public void setResources(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    /**
     * @param controllerClass Class of the controller will be used as key for mapping. So every FXML File is singleton.
     * @param FXML FXML path relative to controllerClass. Will be loaded with `controllerClass.getResource`
     * @return the controller object.
     * */
    public DockableNode addDockableFXML(Class<? extends DockableNode> controllerClass, String FXML) throws IOException {
        FXMLLoader loader = new FXMLLoader(controllerClass.getResource(FXML));
        if(resourceBundle != null) {
            loader.setResources(resourceBundle);
        }
        Parent parent = loader.load();
        DockableNode controller = loader.getController();
        controller.setDockPane(this);

        DockNode dockNode = new DockNode(parent, controller.getDockTitle(), controller.getGraphic());
        dockNode.setPrefSize(parent.prefWidth(-1), parent.prefHeight(-1));

        dockNode.closedProperty().addListener(
                (observable, oldValue, newValue) -> controller.getCloseProperty().set(newValue)
        );

        DockableFXML dockableFXML = new DockableFXML(controller, FXML, dockNode);

        dockNode.dock(this, controller.getDocPos());
        dockables.put(controllerClass, dockableFXML);

        return controller;
    }

    public DockableNode getDockableNode(Class<? extends  DockableNode> nodeClass) {
        DockableFXML obj = dockables.get(nodeClass);
        return obj != null ? obj.controller : null;
    }

    public DockNode getDockNode(Class<? extends  DockableNode> nodeClass) {
        DockableFXML obj = dockables.get(nodeClass);
        return obj != null ? obj.dockNode : null;
    }

    public void createNodeBar(ObservableList<Node> childrenList, boolean showText) {
        dockables.values().forEach(dockableFXML -> {
            DockableNode dNode = dockableFXML.controller;
            Button b = new Button(
                    showText ? dNode.getDockTitle() : ""
                    , dNode.getGraphic()
            );

            b.setOnAction(event -> {
                if(dockableFXML.dockNode.isClosed()) {
                    dockableFXML.dockNode.restore(dNode.getDockPane());
                }
                b.setDisable(true);
            });

            dNode.getCloseProperty().addListener((observable, oldValue, newValue) -> b.setDisable(!newValue));

            b.setDisable(!dNode.getCloseProperty().getValue());
            childrenList.add(b);
        });
    }

    public void createNodeBar(VBox vBox, double spacing, boolean showText) {
        vBox.getChildren().clear();
        vBox.setSpacing(spacing);
        createNodeBar(vBox.getChildren(), showText);
    }

    public void createNodeBar(HBox hBox, double spacing, boolean showText) {
        hBox.getChildren().clear();
        hBox.setSpacing(spacing);
        createNodeBar(hBox.getChildren(), showText);
    }

    /**
     * Covers the anchor pane.
     * */
    public void wrapInAnchorPane(AnchorPane anchorPane) {
        anchorPane.getChildren().add(this);
        AnchorPane.setBottomAnchor(this, 0d);
        AnchorPane.setLeftAnchor(this, 0d);
        AnchorPane.setRightAnchor(this, 0d);
        AnchorPane.setTopAnchor(this, 0d);
    }

    public void showIfClosed(Class<? extends  DockableNode> nodeClass) {
        DockNode dockNode = getDockNode(nodeClass);
        if(dockNode != null) {
            if(dockNode.isClosed()) {
                dockNode.restore(this);
            }
        }
    }
}
