package org.dockfx.demo.controllers;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.dockfx.BasicFXMLDockPaneAdapter;
import org.dockfx.DockPane;
import org.dockfx.DockableNode;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DemoMainFXML extends Application implements Initializable {

    @FXML
    private HBox dummyBar;

    @FXML
    private Menu menu;

    @FXML
    private AnchorPane dockAnchorPane;

    private final BasicFXMLDockPaneAdapter dockPaneAdapter = new BasicFXMLDockPaneAdapter();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("initialize");
        dockPaneAdapter.wrapInAnchorPane(dockAnchorPane);
        try {
            DockableNode dNode2 = dockPaneAdapter.addDockableFXML(DemoFXML2.class, "DemoFXML2.fxml");
            dummyBar.setSpacing(3);
            dummyBar.getChildren().clear();
            dockPaneAdapter.createNodeBar(dummyBar, Button.class, false);

            DockableNode dNode = dockPaneAdapter.addDockableFXML("Demo1", DemoFXML.class, "DemoFXML.fxml");
            DockableNode dNode3 = dockPaneAdapter.addDockableFXML("Demo2", DemoFXML.class, "DemoFXML.fxml");
            dockPaneAdapter.createMenuItems(DemoFXML.class, menu.getItems(), CheckMenuItem.class, true);

        } catch (IOException e) {
            e.printStackTrace();
        }
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);

        DockPane.initializeDefaultUserAgentStylesheet();
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("DemoMain.fxml"))));
        primaryStage.sizeToScene();

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
