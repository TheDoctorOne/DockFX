package org.dockfx.demo.controllers;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.dockfx.BasicFXMLDockPaneAdapter;
import org.dockfx.DockPane;
import org.dockfx.DockableNode;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DemoMainFXML extends Application implements Initializable {

    @FXML
    private AnchorPane dockAnchorPane;

    @FXML
    private Button dummyPaneButton;

    private final BasicFXMLDockPaneAdapter dockPaneAdapter = new BasicFXMLDockPaneAdapter();

    @FXML
    public void onDummyButtonClick(ActionEvent e) {
        dockPaneAdapter.showIfClosed(DemoFXML.class);
        dummyPaneButton.setDisable(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("initialize");
        dockPaneAdapter.wrapInAnchorPane(dockAnchorPane);
        try {
            ChangeListener<? super Boolean> change = (observable, oldValue, newValue) -> {
                if(newValue) {
                    //Closed
                    dummyPaneButton.setDisable(false);
                }
            };

            DockableNode dNode = dockPaneAdapter.addDockableFXML(DemoFXML.class,"DemoFXML.fxml");
            dNode.getCloseProperty().addListener(change);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
