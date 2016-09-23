package dataPrepare.draw;/**
 * Created by anna on 27.08.15.
 */

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class Test2 extends Application {

    private Scene scene;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Web View");
        scene = new Scene(new Browser(),750,500, Color.web("#666970"));
        primaryStage.setScene(scene);
        //scene.getStylesheets().add("webviewsample/BrowserToolbar.css");



        primaryStage.show();
    }

    class Browser extends Region {

        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();

        public Browser() {
            getStyleClass().add("browser");
            webEngine.load("http://localhost:8080/graph");
            getChildren().add(browser);
        }

        private Node createSpacer() {
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            return spacer;
        }

        @Override
        protected void layoutChildren() {
            double w = getWidth();
            double h = getHeight();
            layoutInArea(browser, 0, 0, w, h, 0, HPos.CENTER, VPos.CENTER);
        }

        @Override
        protected double computePrefWidth(double height) {
            return 750;
        }

        @Override
        protected double computePrefHeight(double width) {
            return 500;
        }
    }
}
