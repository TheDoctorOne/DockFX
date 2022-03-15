package org.dockfx;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Will load from FXML.
 * */
public class DefaultDockPaneAdapter extends DockPane {

    public static class DockableObj {
        private final String fxml;
        public final DockNode dockNode;
        public final DockableNode controller;

        private DockableObj(DockableNode controller, String fxml, DockNode dockNode) {
            this.fxml = fxml;
            this.dockNode = dockNode;
            this.controller = controller;
        }
    }

    private final Map<Class<? extends DockableNode>, DockableObj> dockables = new HashMap<>();
    private ResourceBundle resourceBundle;

    /**
     * FXMLs will use this resourceBundle. Set this first, will not update previously loaded FXMLs.
     * */
    public void setResources(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    public void addDockableFXML(Class<? extends DockableNode> controllerClass
            , String FXML
            , String dockTitle
            , DockPos dockPos
            , ChangeListener<? super Boolean> closeListener
    ) throws IOException {
        FXMLLoader loader = new FXMLLoader(controllerClass.getResource(FXML));
        if(resourceBundle != null) {
            loader.setResources(resourceBundle);
        }
        Parent parent = loader.load();
        DockNode dockNode = new DockNode(parent, dockTitle);
        dockNode.setPrefSize(400, 300);
        DockableNode controller = loader.getController();

        dockNode.closedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                controller.onClosedEvent();
            }
            if(closeListener != null) {
                closeListener.changed(observable, oldValue, newValue);
            }
        });

        DockableObj dockableObj = new DockableObj(controller, FXML, dockNode);

        dockNode.dock(this, dockPos);
        dockables.put(controllerClass, dockableObj);
    }

    public DockableNode getDockableNode(Class<? extends  DockableNode> nodeClass) {
        DockableObj obj = dockables.get(nodeClass);
        return obj != null ? obj.controller : null;
    }

    /**
     * Covers the anchor pane.
     * */
    public void putInsideAnchorPane(AnchorPane anchorPane) {
        this.setStyle("-fx-background-color: black");
        anchorPane.getChildren().add(this);
        AnchorPane.setBottomAnchor(this, 0d);
        AnchorPane.setLeftAnchor(this, 0d);
        AnchorPane.setRightAnchor(this, 0d);
        AnchorPane.setTopAnchor(this, 0d);
    }

    public void showIfClosed(Class<? extends  DockableNode> nodeClass) {
        DockableObj obj = dockables.get(nodeClass);
        if(obj != null) {
            if(obj.dockNode.isClosed()) {
                obj.dockNode.restore();
            }
        }
    }
}
