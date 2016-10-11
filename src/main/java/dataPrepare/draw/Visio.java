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

        SaveVoronoi.getInstance().startSaving("test");
        Voronoi voronoi = new Voronoi(graph, frameSizeX, frameSizeY);
        SaveVoronoi.getInstance().endSaving();

        drawVoronoi(voronoi, group);
        //drawVoronoiTest(voronoi, group);

        List<List<dataPrepare.data.voronoi.Polygon>> polygons = new LinkedList<>();
        polygons.add(voronoi.getPolygons());
        polygons.add(voronoi.getPolygons());

        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void addMetric(Graph graph){
        int i=0;
        for (Host host : graph.getHosts()){
            host.addMetric("cellSize", 2000f);
            host.addMetric("id", i);
            host.addMetric("order", Float.parseFloat(getOrder(i)));
            host.addMetric("deep", getDeep(i));
            i++;
        }
    }

    private Integer getDeep(int i){
        switch (i){
            case 41:{return 0;}
            case 35:{return 1;}
            case 42:{return 2;}
            case 15:{return 3;}
            case 12:{return 4;}
            case 38:{return 5;}

            case 29:{return 0;}
            case 40:{return 1;}
            case 24:{return 2;}
            case 1:{return  3;}
            case 11:{return 4;}
            case 14:{return 5;}

            case 28:{return 0;}
            case 7:{return  1;}
            case 23:{return 2;}
            case 8:{return  3;}
            case 0:{return  4;}
            case 13:{return 5;}

            case 2:{return  1;}
            case 26:{return 2;}
            case 17:{return 3;}
            case 36:{return 4;}
            case 10:{return 5;}

            case 4:{return 1;}
            case 43:{return 2;}
            case 18:{return 3;}
            case 34:{return 4;}
            case 9:{return 5;}

            case 27:{return 1;}
            case 16:{return 2;}
            case 19:{return 3;}
            case 20:{return 5;}

            case 30:{return 1;}
            case 3:{return 3;}
            case 21:{return 5;}

            case 22:{return 3;}
            case 32:{return 5;}

            case 25:{return 3;}
            case 33:{return 5;}

            case 31:{return 3;}
            case 37:{return 5;}

            case 5:{return 3;}

            case 6:{return 3;}

            case 39:{return 3;}
        }
        return null;
    }

    private String getOrder(int i){
        switch (i){
            case 41:{return "1";}
            case 35:{return "1";}
            case 42:{return "1";}
            case 15:{return "1";}
            case 12:{return "1";}
            case 38:{return "1";}

            case 29:{return "2";}
            case 40:{return "2";}
            case 24:{return "2";}
            case 1:{return "2";}
            case 11:{return "2";}
            case 14:{return "2";}

            case 28:{return "3";}
            case 7:{return "3";}
            case 23:{return "3";}
            case 8:{return "3";}
            case 0:{return "3";}
            case 13:{return "3";}

            case 2:{return "4";}
            case 26:{return "4";}
            case 17:{return "4";}
            case 36:{return "4";}
            case 10:{return "4";}

            case 4:{return "5";}
            case 43:{return "5";}
            case 18:{return "5";}
            case 34:{return "5";}
            case 9:{return "5";}

            case 27:{return "6";}
            case 16:{return "6";}
            case 19:{return "6";}
            case 20:{return "6";}

            case 30:{return "7";}
            case 3:{return "7";}
            case 21:{return "7";}

            case 22:{return "8";}
            case 32:{return "8";}

            case 25:{return "9";}
            case 33:{return "9";}

            case 31:{return "10";}
            case 37:{return "10";}

            case 5:{return "11";}

            case 6:{return "12";}

            case 39:{return "13";}
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
                //polygon.setOpacity(1f/(Float)cell.getHost().getMetrics().get("order"));
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



    private Group drawVoronoiTest(Voronoi voronoi, Group group){
        for (Coordinate coordinate : voronoi.getDots()){
            Circle circle = new Circle();
            circle.setCenterX(coordinate.getX());
            circle.setCenterY(coordinate.getY());
            circle.setRadius(5);
            circle.setOnMouseDragged(event -> {
                double previosPositionX = circle.getCenterX();
                double previosPositionY = circle.getCenterY();
                double xPadding = event.getX()-previosPositionX;
                double yPadding = event.getY()-previosPositionY;
                //TODO
                //обработка силового смещения

                //RIGHT
                if (event.getX()-previosPositionX>0){
                    for (int i=0; i<group.getChildren().size(); i++){
                        if (group.getChildren().get(i).getClass().equals(Circle.class)){
                            if (((Circle) group.getChildren().get(i)).getCenterX()>previosPositionX){
                                redrawVoronoy(
                                        (Circle) group.getChildren().get(i),
                                        ((Circle) group.getChildren().get(i)).getCenterX()+xPadding,
                                        ((Circle) group.getChildren().get(i)).getCenterY()+yPadding,
                                        group
                                        );
                            }
                        }
                    }
                }
                //LEFT
                if (event.getX()-previosPositionX<0){
                    for (int i=0; i<group.getChildren().size(); i++){
                        if (group.getChildren().get(i).getClass().equals(Circle.class)){
                            if (((Circle) group.getChildren().get(i)).getCenterX()<previosPositionX){
                                redrawVoronoy(
                                        (Circle) group.getChildren().get(i),
                                        ((Circle) group.getChildren().get(i)).getCenterX()+xPadding,
                                        ((Circle) group.getChildren().get(i)).getCenterY()+yPadding,
                                        group
                                );
                            }
                        }
                    }
                }
                //UP
                if (event.getY()-previosPositionY>0){
                    for (int i=0; i<group.getChildren().size(); i++){
                        if (group.getChildren().get(i).getClass().equals(Circle.class)){
                            if (((Circle) group.getChildren().get(i)).getCenterY()>previosPositionY){
                                redrawVoronoy(
                                        (Circle) group.getChildren().get(i),
                                        ((Circle) group.getChildren().get(i)).getCenterX()+xPadding,
                                        ((Circle) group.getChildren().get(i)).getCenterY()+yPadding,
                                        group
                                );
                            }
                        }
                    }
                }
                //DOWN
                if (event.getY()-previosPositionY<0){
                    for (int i=0; i<group.getChildren().size(); i++){
                        if (group.getChildren().get(i).getClass().equals(Circle.class)){
                            if (((Circle) group.getChildren().get(i)).getCenterY()<previosPositionY){
                                redrawVoronoy(
                                        (Circle) group.getChildren().get(i),
                                        ((Circle) group.getChildren().get(i)).getCenterX()+xPadding,
                                        ((Circle) group.getChildren().get(i)).getCenterY()+yPadding,
                                        group
                                );
                            }
                        }
                    }
                }

                redrawVoronoy(circle, event.getX(), event.getY(), group);
            });
            if ((boolean)coordinate.getMetric("stopPolymorph")){
                circle.setFill(Color.PINK);
            }

            group.getChildren().add(circle);
        }

        for (dataPrepare.data.voronoi.Polygon cell : voronoi.getPolygons()){
            for (int i=0; i<=cell.getPoints().size(); i++){
                Coordinate start = cell.getPoints().get(i%cell.getPoints().size());
                Coordinate end = cell.getPoints().get((i+1)%cell.getPoints().size());
                Line line = new Line(start.getX(), start.getY(), end.getX(), end.getY());
                line.setStrokeWidth(2);
                line.setOpacity(0.5);
                if (cell.getHost().getMetrics().get("deep")!=null){
                    //line.setStrokeWidth((int)cell.getHost().getMetrics().get("convex"));
                    switch (((int)cell.getHost().getMetrics().get("deep"))){
                        case 0:{
                            line.setStroke(Color.GREEN);
                            break;
                        }
                        case 1:{
                            line.setStroke(Color.BLUE);
                            break;
                        }
                        case 2:{
                            line.setStroke(Color.YELLOW);
                            break;
                        }
                        case 3:{
                            line.setStroke(Color.RED);
                            break;
                        }
                        case 4:{
                            line.setStroke(Color.PINK);
                            break;
                        }
                    }
                }
                group.getChildren().add(line);
            }
        }
        return group;
    }

    private void redrawVoronoy(Circle circle, double newPositionX, double newPositionY, Group group){
        Line line;
        for (int i=0; i<group.getChildren().size(); i++){
            if (group.getChildren().get(i).getClass().equals(Line.class)){
                line=(Line) group.getChildren().get(i);
                if (line.getStartX()==circle.getCenterX() && line.getStartY()==circle.getCenterY()){
                    line.setStartX(newPositionX);
                    line.setStartY(newPositionY);
                }
                if (line.getEndX()==circle.getCenterX() && line.getEndY()==circle.getCenterY()){
                    line.setEndX(newPositionX);
                    line.setEndY(newPositionY);
                }
            }
        }
        circle.setCenterX(newPositionX);
        circle.setCenterY(newPositionY);
    }

    private void getTime(){
        ZonedDateTime zdt = ZonedDateTime.now();
        java.util.Date date = java.util.Date.from( zdt.toInstant() );
        System.out.println(date);
    }
}
