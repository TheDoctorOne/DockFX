package org.dockfx;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Basic FXML Wrapper, loader. <br>
 * Keeps all the loaded FXML files inside a list. Be sure to remove them, if you don't need them anymore.
 * <br>
 * See <code>unloadDockable(DockableNode)</code> method.
 * */
public class BasicFXMLDockPaneAdapter extends DockPane {

    /**
     * Holds the initialization parameters of the Loaded FXML.
     * */
    public static class DockableFXML {
        private final String fxml;
        private final String customTitle;
        public final Class<? extends DockableNodeController> mClass;
        public final DockNode dockNode;
        public final DockableNodeController controller;

        protected DockableFXML(String customTitle
                , Class<? extends DockableNodeController> mClass
                , DockableNodeController controller
                , String fxml
                , DockNode dockNode
        ) {
            this.fxml = fxml;
            this.customTitle = customTitle;
            this.dockNode = dockNode;
            this.controller = controller;
            this.mClass = mClass;
        }

        /**
         * This is for internal use only. As it may provide wrong title. <br>
         * Use <code>dockNode.getTitle()</code> for more accurate result.
         * */
        protected String getTitle() {
            return customTitle == null ? controller.getDockTitle() : customTitle;
        }

        // Need this for "contains()" checks.
        @Override
        public boolean equals(Object obj) {
            if(obj instanceof DockableFXML) {
                // Most accurate duplication check(?).
                // Other stuff would be same but dockNode has to be different, so display can be unique?.
                return ((DockableFXML) obj).dockNode.equals(this.dockNode);
            }
            if(obj instanceof DockableNodeController) {
                return ((DockableNodeController) obj).getDockNode().equals(this.dockNode);
            }
            return super.equals(obj);
        }
    }

    protected final List<DockableFXML> dockables = new ArrayList<>();
    protected ResourceBundle resourceBundle;

    public BasicFXMLDockPaneAdapter() {
        super();
        this.undockedNodes.addListener((ListChangeListener<DockNode>) c -> {
            if(c.next()) {
                List<? extends DockNode> removed = c.getRemoved();
                List<? extends DockNode> added = c.getAddedSubList();

                for (DockNode add : added) {
                    checkAndAddNode(add);
                }

                for (DockNode remove : removed) {
                    if (remove.isClosed()) {
                        dockables.removeAll(Collections.singletonList(remove));
                    }
                }
            }
        });
    }

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
    public DockableNodeController addDockableFXML(Class<? extends DockableNodeController> controllerClass, String FXML) throws IOException {
        return addDockableFXML(null, controllerClass, FXML);
    }
    /**
     * @param title Title of the Dock. If <code>null</code> uses <code>{@link DockableNodeController}.getDockTitle()</code>.
     * @param controllerClass Class of the controller will be used as key for mapping. So every FXML File is singleton.
     * @param FXML FXML path relative to controllerClass. Will be loaded with `controllerClass.getResource`
     * @return the controller object.
     * */
    public DockableNodeController addDockableFXML(String title, Class<? extends DockableNodeController> controllerClass, String FXML) throws IOException {
        return addDockableFXML(title, 2, controllerClass, FXML);
    }

    /**
     * @param title Title of the Dock. If <code>null</code> uses <code>{@link DockableNodeController}.getDockTitle()</code>.
     * @param divideRatio The divide ratio of this dock. Should be higher or equal to 2. (Default is 2 means half.)
     * @param controllerClass Class of the controller will be used as key for mapping. So every FXML File is singleton.
     * @param FXML FXML path relative to controllerClass. Will be loaded with `controllerClass.getResource`
     * @return the controller object.
     * */
    public DockableNodeController addDockableFXML(String title, double divideRatio, Class<? extends DockableNodeController> controllerClass, String FXML) throws IOException {
        FXMLLoader loader = new FXMLLoader(controllerClass.getResource(FXML));
        if(resourceBundle != null) {
            loader.setResources(resourceBundle);
        }
        Parent parent = loader.load();
        if(parent instanceof Region) { // This causes issues, when set, while managing tabs.
            ((Region) parent).setMinHeight(0);
            ((Region) parent).setMinWidth(0);
        }
        DockableNodeController controller = loader.getController();
        controller.setDockPane(this);

        DockNode dockNode = new DockNode(parent, title == null ? controller.getDockTitle() : title, controller.getGraphic());
        dockNode.setPrefSize(parent.prefWidth(-1), parent.prefHeight(-1));
        dockNode.setScreenDivideRatioOnDock(divideRatio);
        controller.setDockNode(dockNode);

        dockNode.closedProperty().addListener(
                (observable, oldValue, newValue) -> controller.getCloseProperty().set(newValue)
        );

        DockableFXML dockableFXML = new DockableFXML(title, controllerClass, controller, FXML, dockNode);

        dockables.add(dockableFXML); // Call this first!. Because dock method also updates the dockables to avoid duplicates.
        dockNode.dock(this, controller.getDocPos());

        return controller;
    }

    protected void checkAndAddNode(Node node) {
        if(node instanceof DockNode && !checkListContains(dockables, node)) {
            DockNode tmp = (DockNode) node;
            dockables.add(new DockableFXML(tmp.getTitle(), DockNode.class, tmp, null, tmp));
        }
    }


    @Override
    void dock(Node node, DockPos dockPos, Node sibling) {
        checkAndAddNode(node);
        super.dock(node, dockPos, sibling);
    }

    public List<DockableNodeController> getDockableNode(Class<? extends DockableNodeController> nodeClass) {
        ArrayList<DockableNodeController> res = new ArrayList<>();
        dockables.forEach(dockableFXML -> {
            if(dockableFXML.mClass == nodeClass) {
                res.add(dockableFXML.controller);
            }
        });
        return res;
    }

    public List<DockNode> getDockNode(Class<? extends DockableNodeController> nodeClass) {
        ArrayList<DockNode> res = new ArrayList<>();
        dockables.forEach(dockableFXML -> {
            if(dockableFXML.mClass == nodeClass) {
                res.add(dockableFXML.dockNode);
            }
        });
        return res;
    }


    public List<DockableFXML> getDockableFXML(Class<? extends DockableNodeController> nodeClass) {
        List<DockableFXML> res = new ArrayList<>();
        dockables.forEach(dockableFXML -> {
            if(dockableFXML.mClass == nodeClass) {
                res.add(dockableFXML);
            }
        });
        return res;
    }

    public DockableFXML getDockableFXML(DockableNodeController node) {
        for(DockableFXML dockableFXML : dockables) {
            if(dockableFXML.controller == node) {
                return dockableFXML;
            }
        }
        return null;
    }

    /**
     * Unload the FXML from the memory. As it may keep the reference. <br>
     * Therefore Garbage Collector won't be able to clear the memory. <br>
     * Which may cause memory leak. <br>
     *     <br>
     * <b>Be aware! This method only removes the <i>node</i> from the <br>
     * internal list and will not close or remove the <i>node</i> itself.</b>
     * */
    public boolean unloadDockable(DockableNodeController node) {
        return dockables.removeAll(Collections.singletonList(getDockableFXML(node)));
    }

    public void createMenuItems(ObservableList<MenuItem> childrenList, Class<? extends MenuItem> base, boolean showText) {
        createMenuItems(dockables, childrenList, base, showText);
    }

    public void createNodeBar(ObservableList<Node> childrenList, Class<? extends ButtonBase> base, boolean showText) {
        createNodeBar(dockables, childrenList, base, showText);
    }

    public void createMenuItems(Class<? extends DockableNodeController> filter, ObservableList<MenuItem> childrenList, Class<? extends MenuItem> base, boolean showText) {
        createMenuItems(filterList(dockables, filter), childrenList, base, showText);
    }

    public void createNodeBar(Class<? extends DockableNodeController> filter, ObservableList<Node> childrenList, Class<? extends ButtonBase> base, boolean showText) {
        createNodeBar(filterList(dockables, filter), childrenList, base, showText);
    }

    private static <T> boolean checkListContains(List<T> list, Object o) {
        for(T t : list) {
            if(t.equals(o))
                return true;
        }
        return false;
    }

    private static List<DockableFXML> filterList(List<DockableFXML> dockables, Class<? extends DockableNodeController> filter) {
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
                DockableNodeController dNode = dockableFXML.controller;
                ButtonBase buttonBase = base.newInstance();
                buttonBase.setText(showText ? dockableFXML.getTitle() : "");
                ImageView imageView = (ImageView) dockableFXML.dockNode.graphicProperty().getValue();
                // Wrapping it inside "new" because it is also getting used by another field. If not wrap, then it won't render the graphic. Same goes with the MenuItems.
                buttonBase.setGraphic(new ImageView(imageView.getImage()));

                dockableFXML.dockNode.graphicProperty().addListener((observable, oldValue, newValue) -> {
                    if(newValue instanceof ImageView) {
                        ImageView newImage = (ImageView) newValue;
                        buttonBase.setGraphic(new ImageView(newImage.getImage()));
                    } else if(newValue == null) {
                        buttonBase.setGraphic(null);
                    }
                });

                buttonBase.setOnAction(event -> {
                    if (dockableFXML.dockNode.isClosed()) {
                        dockableFXML.dockNode.restore(dNode.getDockPane());
                    } else {
                        dockableFXML.dockNode.close();
                    }
                    if(buttonBase instanceof CheckBox) {
                        ((CheckBox)buttonBase).setSelected(!dockableFXML.dockNode.isClosed());
                    } else {
                        buttonBase.setDisable(!dockableFXML.dockNode.isClosed());
                    }
                });

                dNode.getCloseProperty().addListener((observable, oldValue, newValue) -> {
                    if(buttonBase instanceof CheckBox) {
                        ((CheckBox)buttonBase).setSelected(!newValue);
                    } else {
                        buttonBase.setDisable(!newValue);
                    }
                });

                if(buttonBase instanceof CheckBox) {
                    ((CheckBox)buttonBase).setSelected(!dNode.getCloseProperty().getValue());
                } else {
                    buttonBase.setDisable(!dNode.getCloseProperty().getValue());
                }
                childrenList.add(buttonBase);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    private static void createMenuItems(List<DockableFXML> dockables, ObservableList<MenuItem> childrenList, Class<? extends MenuItem> base, boolean showText) {
        dockables.forEach(dockableFXML -> {
            try {
                DockableNodeController dNode = dockableFXML.controller;
                MenuItem menuItem = base.newInstance();
                menuItem.setText(showText ? dockableFXML.getTitle() : "");
                ImageView imageView = (ImageView) dockableFXML.dockNode.graphicProperty().getValue();
                menuItem.setGraphic(new ImageView(imageView.getImage()));

                dockableFXML.dockNode.graphicProperty().addListener((observable, oldValue, newValue) -> {
                    if(newValue instanceof ImageView) {
                        ImageView newImage = (ImageView) newValue;
                        menuItem.setGraphic(new ImageView(newImage.getImage()));
                    } else if(newValue == null) {
                        menuItem.setGraphic(null);
                    }
                });
                
                menuItem.setOnAction(event -> {
                    if (dockableFXML.dockNode.isClosed()) {
                        dockableFXML.dockNode.restore(dNode.getDockPane());
                    } else {
                        dockableFXML.dockNode.close();
                    }
                    if(menuItem instanceof CheckMenuItem) {
                        ((CheckMenuItem)menuItem).setSelected(!dockableFXML.dockNode.isClosed());
                    } else {
                        menuItem.setDisable(!dockableFXML.dockNode.isClosed());
                    }
                });

                dNode.getCloseProperty().addListener((observable, oldValue, newValue) -> {
                    if(menuItem instanceof CheckMenuItem) {
                        ((CheckMenuItem)menuItem).setSelected(!newValue);
                    } else {
                        menuItem.setDisable(!newValue);
                    }
                });

                if(menuItem instanceof CheckMenuItem) {
                    ((CheckMenuItem)menuItem).setSelected(!dNode.getCloseProperty().getValue());
                } else {
                    menuItem.setDisable(!dNode.getCloseProperty().getValue());
                }
                childrenList.add(menuItem);
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

    public void showIfClosed(Class<? extends DockableNodeController> nodeClass) {
        getDockNode(nodeClass).forEach(dockNode -> {
            if (dockNode != null) {
                if (dockNode.isClosed()) {
                    dockNode.restore(this);
                }
            }
        });
    }
}
