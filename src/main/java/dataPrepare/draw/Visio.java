package dataPrepare.draw;/**
 * Created by anna on 27.08.15.
 */

import com.google.gson.Gson;
import dataPrepare.*;
import dataPrepare.Test2;
import dataPrepare.data.Test;
import dataPrepare.data.TestD3;
import dataPrepare.data.TestPolymorph;
import dataPrepare.data.graph.Graph;
import dataPrepare.data.graph.Coordinate;
import dataPrepare.data.graph.Host;
import dataPrepare.data.triangulation.TriangleVoronoiImpl;
import dataPrepare.data.voronoi.Voronoi;
import dataPrepare.prepare.GenerateGraph;
import dataPrepare.prepare.GenereatePlanarGraphUsingTriangulation;
import dataPrepare.methods.ConvexHull;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
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

        Graph graph;
        graph = GenerateGraph.generateConstantGraph();

        addMetric(graph);
        //new TestD3().force_INPUT(graph);
        new TestD3().force_OUTPUT(graph, TestD3.getGraph());
        drawGraph(graph, group, 1);

        SaveVoronoi.getInstance().startSaving("test");
        Voronoi voronoi = new Voronoi(graph, frameSizeX, frameSizeY);
        drawVoronoi(voronoi, group);
        SaveVoronoi.getInstance().endSaving();


        //drawGraph(graph,group, 0);
        //drawGraph(voronoi.voronoiLikeAGraph(voronoi),group, 0);
        //drawVoronoi(voronoi, group);
        //drawVoronoiTest(voronoi, group);

        //List<List<dataPrepare.data.voronoi.Polygon>> polygons = new LinkedList<>();
        //polygons.add(voronoi.getPolygons());
        //polygons.add(voronoi.getPolygons());

        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void addMetric(Graph graph){
        int i=0;
        for (Host host : graph.getHosts()){
            host.addMetric("cellSize", 3000f);
            host.addMetric("id", i);
            host.addMetric("order", Float.parseFloat(getOrder(i)));
            host.addMetric("deep", getDeep(i));
            i++;
        }
    }

    private Integer getDeep(int i){
        switch (i){
            case 41:{return 0;}
            case 6:{return 0;}
            case 43:{return 0;}
            case 7:{return 0;}
            case 40:{return 0;}
            case 22:{return 0;}
            case 18:{return 0;}
            case 1:{return 0;}
            case 2:{return 0;}

            case 39:{return 1;}
            case 5: {return 1;}
            case 36:{return 1;}
            case 38:{return 1;}
            case 23:{return 1;}
            case 3: {return 1;}
            case 21:{return 1;}
            case 4: {return 1;}
            case 17:{return 1;}
            case 16:{return 1;}
            case 14:{return 1;}
            case 15:{return 1;}
            case 12:{return 1;}
            case 13:{return 1;}
            case 0: {return 1;}
            case 42:{return 1;}
            case 37:{return 1;}
            case 35:{return 1;}

            case 9:{return 2;}
            case 10:{return 2;}
            case 28:{return 2;}
            case 25:{return 2;}
            case 27:{return 2;}
            case 29:{return 2;}
            case 26:{return 2;}
            case 24:{return 2;}
            case 20:{return 2;}
            case 19:{return 2;}
            case 32:{return 2;}
            case 31:{return 2;}
            case 30:{return 2;}
            case 34:{return 2;}
            case 33:{return 2;}
            case 11:{return 2;}
            case 8:{return 2;}


        }
        return null;
    }

    private String getOrder(int i){
//        switch (i){
//            case 41:{return "1";}
//            case 39:{return "1";}
//            case 9:{return "1";}
//
//            case 6:{return "2";}
//            case 5:{return "2";}
//            case 10:{return "2";}
//
//            case 43:{return "3";}
//            case 36:{return "3";}
//            case 28:{return "3";}
//
//            case 7:{return "4";}
//            case 38:{return "4";}
//            case 25:{return "4";}
//
//            case 40:{return "5";}
//            case 23:{return "5";}
//            case 27:{return "5";}
//
//            case 22:{return "6";}
//            case 3:{return "6";}
//            case 29:{return "6";}
//
//            case 18:{return "7";}
//            case 21:{return "7";}
//            case 26:{return "7";}
//
//            case 1:{return "8";}
//            case 4:{return "8";}
//            case 24:{return "8";}
//
//            case 2:{return "9";}
//            case 17:{return "9";}
//            case 20:{return "9";}
//
//            case 16:{return "10";}
//            case 19:{return "10";}
//
//            case 14:{return "11";}
//            case 32:{return "11";}
//
//            case 15:{return "12";}
//            case 31:{return "12";}
//
//            case 12:{return "13";}
//            case 30:{return "13";}
//
//            case 13:{return "14";}
//            case 34:{return "14";}
//
//            case 0:{return "15";}
//            case 33:{return "15";}
//
//            case 42:{return "16";}
//            case 11:{return "16";}
//
//            case 37:{return "17";}
//            case 8:{return "17";}
//
//            case 35:{return "18";}
//        }

        switch (i){
            case 7:{return "1";}
            case 39:{return "1";}
            case 9:{return "1";}

            case 43:{return "2";}
            case 5:{return "2";}
            case 10:{return "2";}

            case 1:{return "3";}
            case 36:{return "3";}
            case 28:{return "3";}

            case 2:{return "4";}
            case 38:{return "4";}
            case 25:{return "4";}

            case 18:{return "5";}
            case 23:{return "5";}
            case 27:{return "5";}

            case 22:{return "6";}
            case 3:{return "6";}
            case 29:{return "6";}

            case 40:{return "7";}
            case 21:{return "7";}
            case 26:{return "7";}

            case 6:{return "8";}
            case 4:{return "8";}
            case 24:{return "8";}

            case 41:{return "9";}
            case 17:{return "9";}
            case 20:{return "9";}

            case 16:{return "10";}
            case 19:{return "10";}

            case 14:{return "11";}
            case 32:{return "11";}

            case 15:{return "12";}
            case 31:{return "12";}

            case 12:{return "13";}
            case 30:{return "13";}

            case 13:{return "14";}
            case 34:{return "14";}

            case 0:{return "15";}
            case 33:{return "15";}

            case 42:{return "16";}
            case 11:{return "16";}

            case 37:{return "17";}
            case 8:{return "17";}

            case 35:{return "18";}
        }
        return null;
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
        Color[] colors = {Color.RED, Color.HOTPINK, Color.GREEN, Color.LIGHTGREEN, Color.BLUE, Color.LIGHTBLUE, Color.DARKRED};
        for (dataPrepare.data.voronoi.Polygon cell : voronoi.getPolygons()){
            Polygon polygon = new Polygon();
            for (Coordinate cellCoordinates : cell.getPoints()){
                Double[] points = new Double[2];
                points[0] = (double) cellCoordinates.getX();
                points[1] = (double) cellCoordinates.getY();
                polygon.getPoints().addAll(points);
                polygon.setFill(colors[(int)cell.getHost().getMetrics().get("deep")]);
                //polygon.setOpacity(1f-(Float)cell.getHost().getMetrics().get("order")*0.1);
                polygon.setOpacity(1);
            }
            polygon.setOnMouseClicked(event -> {
                System.out.println(cell.getHost().getMetrics().get("order"));
            });


            if (ConvexHull.get(voronoi.getGraph()).contains(cell.getHost())){
                //polygon.setFill(Color.RED);
            }
            group.getChildren().add(polygon);

            for (int i=0; i<=cell.getPoints().size(); i++){

                Coordinate start = cell.getPoints().get(i%cell.getPoints().size());
                Coordinate end = cell.getPoints().get((i+1)%cell.getPoints().size());
                Line line = new Line(start.getX(), start.getY(), end.getX(), end.getY());
                line.setStrokeWidth(1);
                group.getChildren().add(line);
            }
        }
        return group;
    }

}
