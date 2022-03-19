package org.dockfx;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Loads from FXML.
 * */
public class BasicFXMLDockPaneAdapter extends DockPane {

    public static class DockableFXML {
        private final String fxml;
        private final String customTitle;
        public final Class<? extends DockableNode> mClass;
        public final DockNode dockNode;
        public final DockableNode controller;

        private DockableFXML(String customTitle, Class<? extends DockableNode> mClass, DockableNode controller, String fxml, DockNode dockNode) {
            this.fxml = fxml;
            this.customTitle = customTitle;
            this.dockNode = dockNode;
            this.controller = controller;
            this.mClass = mClass;
        }

        public String getTitle() {
            return customTitle == null ? controller.getDockTitle() : customTitle;
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
        return addDockableFXML(null, controllerClass, FXML);
    }
    /**
     * @param title Title of the Dock. If <code>null</code> uses <code>{@link DockableNode}.getDockTitle()</code>.
     * @param controllerClass Class of the controller will be used as key for mapping. So every FXML File is singleton.
     * @param FXML FXML path relative to controllerClass. Will be loaded with `controllerClass.getResource`
     * @return the controller object.
     * */
    public DockableNode addDockableFXML(String title, Class<? extends DockableNode> controllerClass, String FXML) throws IOException {
        return addDockableFXML(title, 2, controllerClass, FXML);
    }

    /**
     * @param title Title of the Dock. If <code>null</code> uses <code>{@link DockableNode}.getDockTitle()</code>.
     * @param divideRatio The divide ratio of this dock. Should be higher or equal to 2. (Default is 2 means half.)
     * @param controllerClass Class of the controller will be used as key for mapping. So every FXML File is singleton.
     * @param FXML FXML path relative to controllerClass. Will be loaded with `controllerClass.getResource`
     * @return the controller object.
     * */
    public DockableNode addDockableFXML(String title, double divideRatio, Class<? extends DockableNode> controllerClass, String FXML) throws IOException {
        FXMLLoader loader = new FXMLLoader(controllerClass.getResource(FXML));
        if(resourceBundle != null) {
            loader.setResources(resourceBundle);
        }
        Parent parent = loader.load();
        if(parent instanceof Region) { // This causes issues, when set, while managing tabs.
            ((Region) parent).setMinHeight(0);
            ((Region) parent).setMinWidth(0);
        }
        DockableNode controller = loader.getController();
        controller.setDockPane(this);

        DockNode dockNode = new DockNode(parent, title == null ? controller.getDockTitle() : title, controller.getGraphic());
        dockNode.setPrefSize(parent.prefWidth(-1), parent.prefHeight(-1));
        dockNode.setScreenDivideRatioOnDock(divideRatio);
        controller.setDockNode(dockNode);

        dockNode.closedProperty().addListener(
                (observable, oldValue, newValue) -> controller.getCloseProperty().set(newValue)
        );

        DockableFXML dockableFXML = new DockableFXML(title, controllerClass, controller, FXML, dockNode);

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


    public List<DockableFXML> getDockableFXML(Class<? extends DockableNode> nodeClass) {
        List<DockableFXML> res = new ArrayList<>();
        dockables.forEach(dockableFXML -> {
            if(dockableFXML.mClass == nodeClass) {
                res.add(dockableFXML);
            }
        });
        return res;
    }



    public void createMenuItems(ObservableList<MenuItem> childrenList, Class<? extends MenuItem> base, boolean showText) {
        createMenuItems(dockables, childrenList, base, showText);
    }

    public void createNodeBar(ObservableList<Node> childrenList, Class<? extends ButtonBase> base, boolean showText) {
        createNodeBar(dockables, childrenList, base, showText);
    }

    public void createMenuItems(Class<? extends DockableNode> filter, ObservableList<MenuItem> childrenList, Class<? extends MenuItem> base, boolean showText) {
        createMenuItems(filterList(dockables, filter), childrenList, base, showText);
    }

    public void createNodeBar(Class<? extends DockableNode> filter, ObservableList<Node> childrenList, Class<? extends ButtonBase> base, boolean showText) {
        createNodeBar(filterList(dockables, filter), childrenList, base, showText);
    }

    private static List<DockableFXML> filterList(List<DockableFXML> dockables, Class<? extends DockableNode> filter) {
        List<DockableFXML> res = new ArrayList<>();
        dockables.forEach(dockableFXML -> {
            if(dockableFXML.mClass == filter) {
                res.add(dockableFXML);
            }
        });
        return res;
    }

    private static void createNodeBar(List<DockableFXML> dockables, ObservableList<Node> childrenList,  Class<? extends ButtonBase> base, boolean showText) {
        dockables.forEach(dockableFXML -> {
            try {
                DockableNode dNode = dockableFXML.controller;
                ButtonBase b = base.newInstance();
                b.setText(showText ? dockableFXML.getTitle() : "");
                b.setGraphic(dNode.getGraphic());

                b.setOnAction(event -> {
                    if (dockableFXML.dockNode.isClosed()) {
                        dockableFXML.dockNode.restore(dNode.getDockPane());
                    } else {
                        dockableFXML.dockNode.close();
                    }
                    if(b instanceof CheckBox) {
                        ((CheckBox)b).setSelected(!dockableFXML.dockNode.isClosed());
                    } else {
                        b.setDisable(!dockableFXML.dockNode.isClosed());
                    }
                });

                dNode.getCloseProperty().addListener((observable, oldValue, newValue) -> {
                    if(b instanceof CheckBox) {
                        ((CheckBox)b).setSelected(!newValue);
                    } else {
                        b.setDisable(!newValue);
                    }
                });

                if(b instanceof CheckBox) {
                    ((CheckBox)b).setSelected(!dNode.getCloseProperty().getValue());
                } else {
                    b.setDisable(!dNode.getCloseProperty().getValue());
                }
                childrenList.add(b);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    private static void createMenuItems(List<DockableFXML> dockables, ObservableList<MenuItem> childrenList, Class<? extends MenuItem> base, boolean showText) {
        dockables.forEach(dockableFXML -> {
            try {
                DockableNode dNode = dockableFXML.controller;
                MenuItem b = base.newInstance();
                b.setText(showText ? dockableFXML.getTitle() : "");
                b.setGraphic(dNode.getGraphic());

                b.setOnAction(event -> {
                    if (dockableFXML.dockNode.isClosed()) {
                        dockableFXML.dockNode.restore(dNode.getDockPane());
                    } else {
                        dockableFXML.dockNode.close();
                    }
                    if(b instanceof CheckMenuItem) {
                        ((CheckMenuItem)b).setSelected(!dockableFXML.dockNode.isClosed());
                    } else {
                        b.setDisable(!dockableFXML.dockNode.isClosed());
                    }
                });

                dNode.getCloseProperty().addListener((observable, oldValue, newValue) -> {
                    if(b instanceof CheckMenuItem) {
                        ((CheckMenuItem)b).setSelected(!newValue);
                    } else {
                        b.setDisable(!newValue);
                    }
                });

                if(b instanceof CheckMenuItem) {
                    ((CheckMenuItem)b).setSelected(!dNode.getCloseProperty().getValue());
                } else {
                    b.setDisable(!dNode.getCloseProperty().getValue());
                }
                childrenList.add(b);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
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

    public void createNodeBar(VBox vBox, Class<? extends ButtonBase> base, boolean showText) {
        createNodeBar(vBox.getChildren(), base, showText);
    }

    public void createNodeBar(HBox hBox, Class<? extends ButtonBase> base, boolean showText) {
        createNodeBar(hBox.getChildren(), base, showText);
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
