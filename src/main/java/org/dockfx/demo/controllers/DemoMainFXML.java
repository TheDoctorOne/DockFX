package org.dockfx.demo.controllers;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.dockfx.*;
import org.dockfx.demo.DockFX;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DemoMainFXML extends Application implements Initializable {

    public static String CONFIG_FILE = "dock.pref";

    @FXML
    private HBox dummyBar;

    @FXML
    private Menu menu;

    @FXML
    private AnchorPane dockAnchorPane;

    private final BasicFXMLDockPaneAdapter dockPaneAdapter = new BasicFXMLDockPaneAdapter();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dockPaneAdapter.wrapInAnchorPane(dockAnchorPane);
        try {

            DockNode n1 = DockFX.generateRandomDockableTree("Random Tree", DockFX.getDockImage());
            n1.dock(dockPaneAdapter, DockPos.LEFT);

            DockableNode n2 = dockPaneAdapter.addDockableFXML(DemoFXML2.class, "DemoFXML2.fxml");
            double customDivideRatio = 5;
            dockPaneAdapter.addDockableFXML("FXML2", customDivideRatio, DemoFXML2.class, "DemoFXML2.fxml");

            dummyBar.setSpacing(3);
            dummyBar.getChildren().clear();
            dockPaneAdapter.createNodeBar(dummyBar, Button.class, true);

            DockableNode dNode = dockPaneAdapter.addDockableFXML("Demo1", DemoFXML.class, "DemoFXML.fxml");
            dockPaneAdapter.addDockableFXML("Demo2", DemoFXML.class, "DemoFXML.fxml");
            dockPaneAdapter.createMenuItems(DemoFXML.class, menu.getItems(), CheckMenuItem.class, true);
            dockPaneAdapter.createMenuItems(DockNode.class, menu.getItems(), MenuItem.class, true);

            List<BasicFXMLDockPaneAdapter.DockableFXML> dFxml = dockPaneAdapter.getDockableFXML(DemoFXML.class);
            System.out.println(dFxml.size());

            // Update divide ratio after dock.
            dNode.getDockNode().setScreenDivideRatioOnDock(4);


        } catch (IOException e) {
            e.printStackTrace();
        }
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);

        DockPane.initializeDefaultUserAgentStylesheet();
    }

    public BasicFXMLDockPaneAdapter getDockPaneAdapter() {
        return dockPaneAdapter;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("DemoMain.fxml"));
        Parent parent = loader.load();
        DemoMainFXML controller = loader.getController();
        primaryStage.setScene(new Scene(parent));
        primaryStage.sizeToScene();

        primaryStage.setOnCloseRequest(event -> {
            controller.getDockPaneAdapter().storePreference(CONFIG_FILE);
            System.exit(0);
        });

        primaryStage.setOnShown(event -> {
            controller.getDockPaneAdapter().loadPreference(CONFIG_FILE, false);
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
