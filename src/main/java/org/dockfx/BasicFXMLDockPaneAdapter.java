package org.dockfx;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Loads from FXML.
 * */
public class BasicFXMLDockPaneAdapter extends DockPane {

    protected static class DockableFXML {
        private final String fxml;
        public final Class<? extends DockableNode> mClass;
        public final DockNode dockNode;
        public final DockableNode controller;

        private DockableFXML(Class<? extends DockableNode> mClass, DockableNode controller, String fxml, DockNode dockNode) {
            this.fxml = fxml;
            this.dockNode = dockNode;
            this.controller = controller;
            this.mClass = mClass;
        }
    }

    protected final List<DockableFXML> dockables = new ArrayList<>();
    protected ResourceBundle resourceBundle;

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

        DockableFXML dockableFXML = new DockableFXML(controllerClass, controller, FXML, dockNode);

        dockNode.dock(this, controller.getDocPos());
        dockables.add(dockableFXML);

        return controller;
    }

    public List<DockableNode> getDockableNode(Class<? extends  DockableNode> nodeClass) {
        ArrayList<DockableNode> res = new ArrayList<>();
        dockables.forEach(dockableFXML -> {
            if(dockableFXML.mClass == nodeClass) {
                res.add(dockableFXML.controller);
            }
        });
        return res;
    }

    public List<DockNode> getDockNode(Class<? extends  DockableNode> nodeClass) {
        ArrayList<DockNode> res = new ArrayList<>();
        dockables.forEach(dockableFXML -> {
            if(dockableFXML.mClass == nodeClass) {
                res.add(dockableFXML.dockNode);
            }
        });
        return res;
    }

    public void createNodeBar(ObservableList<Node> childrenList, boolean showText) {
        dockables.forEach(dockableFXML -> {
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

    public void closeAll() {
        dockables.forEach(dockableFXML -> dockableFXML.dockNode.close());
    }

    public void closeFloating() {
        dockables.forEach(dockableFXML -> {
            if(dockableFXML.dockNode.isFloating()) {
                dockableFXML.dockNode.close();
            }
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
        getDockNode(nodeClass).forEach(dockNode -> {
            if (dockNode != null) {
                if (dockNode.isClosed()) {
                    dockNode.restore(this);
                }
            }
        });
    }
}
