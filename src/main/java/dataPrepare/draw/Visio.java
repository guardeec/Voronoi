package dataPrepare.draw;/**
 * Created by anna on 27.08.15.
 */

import com.google.gson.Gson;
import dataPrepare.*;
import dataPrepare.Test2;
import dataPrepare.data.Test;
import dataPrepare.data.TestPolymorph;
import dataPrepare.data.graph.Graph;
import dataPrepare.data.graph.Coordinate;
import dataPrepare.data.graph.Host;
import dataPrepare.data.triangulation.TriangleVoronoiImpl;
import dataPrepare.data.voronoi.Voronoi;
import dataPrepare.prepare.GenereatePlanarGraphUsingTriangulation;
import dataPrepare.methods.ConvexHull;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

import java.time.ZonedDateTime;
import java.util.*;

public class Visio extends Application {

    private final int scale = 1;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        int frameSizeX = 1000;
        int frameSizeY = 1000;
        Group group = new Group();
        Test2.getInstance().setGroup(group);
        Scene scene = new Scene(group, frameSizeX, frameSizeY);

        GenereatePlanarGraphUsingTriangulation generator = new GenereatePlanarGraphUsingTriangulation();
        Graph graph = generator.generate(100, 1000, 1000, 0f);



        Voronoi voronoi = new Voronoi(graph, 1000, 1000);




        Map<dataPrepare.data.voronoi.Polygon, Float> sizes = new HashMap<>();
        for (dataPrepare.data.voronoi.Polygon polygon : voronoi.getPolygons()){
            sizes.put(polygon, 50000f);
        }
        TestPolymorph testPolymorph = new TestPolymorph(sizes);

        new Thread(new Runnable() {
            @Override public void run() {
                while (true) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    Platform.runLater(new Runnable() {
                        @Override public void run() {
                            List<Node> ol = testPolymorph.testDraw(voronoi, group.getChildren());
                            group.getChildren().removeAll(group.getChildren());
                            group.getChildren().addAll(ol);

                        }
                    });
                }
            }
        });//.start();

        List<Node> ol = testPolymorph.testDraw(voronoi, group.getChildren());
        group.getChildren().removeAll(group.getChildren());
        group.getChildren().addAll(ol);

        System.out.println(voronoi.getDots().size());

        //drawGraph(graph, group, 1);
        drawVoronoi(voronoi, group);
        System.out.println(graph.checkOnPlanar());

        primaryStage.setScene(scene);
        primaryStage.show();
        //drawGraph(graph, group,1);



    }

    private Group drawGraph(Graph graph, Group group, int color){
        for (Host host : graph.getHosts()){
            for (Host relatedHost : graph.getRelations(host)) {
                Line line = new Line(
                        host.getCoordinate().getX() * scale,
                        host.getCoordinate().getY() * scale,
                        relatedHost.getCoordinate().getX() * scale,
                        relatedHost.getCoordinate().getY() * scale
                );
                if (color==0){
                    line.setStroke(Color.RED);
                }else {
                    line.setStroke(Color.GRAY);
                }
                line.setStrokeWidth(4);

                group.getChildren().add(line);
            }
            Circle circle = new Circle(
                    host.getCoordinate().getX() * scale,
                    host.getCoordinate().getY() * scale,
                    4
            );
            circle.setFill(Color.BLACK);
            group.getChildren().add(circle);
        }
        return group;
    }

    private Group drawVoronoi(Voronoi voronoi, Group group){
        for (dataPrepare.data.voronoi.Polygon cell : voronoi.getPolygons()){
            Polygon polygon = new Polygon();
            for (Coordinate cellCoordinates : cell.getPoints()){
                Double[] points = new Double[2];
                points[0] = (double) cellCoordinates.getX();
                points[1] = (double) cellCoordinates.getY();
                polygon.getPoints().addAll(points);
                polygon.setOpacity(0.3);
            }

            if (ConvexHull.get(voronoi.getGraph()).contains(cell.getHost())){
                polygon.setFill(Color.RED);
            }
            group.getChildren().add(polygon);
        }
        return group;
    }

    private void getTime(){
        ZonedDateTime zdt = ZonedDateTime.now();
        java.util.Date date = java.util.Date.from( zdt.toInstant() );
        System.out.println(date);
    }
}
