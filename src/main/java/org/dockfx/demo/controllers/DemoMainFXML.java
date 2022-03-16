package org.dockfx.demo.controllers;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
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
    private AnchorPane dockAnchorPane;

    private final BasicFXMLDockPaneAdapter dockPaneAdapter = new BasicFXMLDockPaneAdapter();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("initialize");
        dockPaneAdapter.wrapInAnchorPane(dockAnchorPane);
        try {
            DockableNode dNode = dockPaneAdapter.addDockableFXML(DemoFXML.class,"DemoFXML.fxml");
            DockableNode dNode2 = dockPaneAdapter.addDockableFXML(DemoFXML2.class,"DemoFXML2.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }

        dockPaneAdapter.createNodeBar(dummyBar, 3, true);

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
