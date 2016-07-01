package dataPrepare.draw;/**
 * Created by anna on 27.08.15.
 */

import com.google.gson.Gson;
import dataPrepare.data.*;
import dataPrepare.prepare.GenerateGraph;
import dataPrepare.prepare.GenereatePlanarGraphUsingTriangulation;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Visio extends Application {

    private final int scale = 1;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        int frameSizeX = 500;
        int frameSizeY = 500;
        Group group = new Group();
        Scene scene = new Scene(group, frameSizeX, frameSizeY);
        GenereatePlanarGraphUsingTriangulation generator = new GenereatePlanarGraphUsingTriangulation();
        Graph graph = generator.generate(150, 500, 500, 0.5f);
        Voronoi voronoi = new Voronoi(graph);
        //drawGraph(graph, group,0);
        //drawGraph(voronoi.voronoiLikeAGraph(voronoi), group,1);
        drawVoronoi(voronoi, group);
        primaryStage.setScene(scene);
        primaryStage.show();
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

                group.getChildren().add(line);
            }
            Circle circle = new Circle(
                    host.getCoordinate().getX() * scale,
                    host.getCoordinate().getY() * scale,
                    host.getRadius()+10
            );
            circle.setFill(Color.BLACK);
            group.getChildren().add(circle);
        }
        return group;
    }

    private Group drawVoronoi(Voronoi voronoi, Group group){
        for (dataPrepare.data.Polygon cell : voronoi.getPolygons()){
            Polygon polygon = new Polygon();
            for (Coordinate cellCoordinates : cell.getPoints()){
                Double[] points = new Double[2];
                points[0] = (double) cellCoordinates.getX();
                points[1] = (double) cellCoordinates.getY();

                polygon.getPoints().addAll(points);
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
