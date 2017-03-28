package dataPrepare.draw;/**
 * Created by anna on 27.08.15.
 */

import dataPrepare.Test2;
import dataPrepare.data.TestPolymorph;
import dataPrepare.data.d3.RunD3;
import dataPrepare.data.debug.SaveVoronoi;
import dataPrepare.data.graph.Graph;
import dataPrepare.data.graph.Coordinate;
import dataPrepare.data.graph.Host;
import dataPrepare.data.graphviz.MakePlanar;
import dataPrepare.data.voronoi.Attractor;
import dataPrepare.data.voronoi.Voronoi;
import dataPrepare.prepare.GenerateGraph;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

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


        graph = GenerateGraph.generateTreeSuperStatic();

   //     graph = GenerateGraph.generateConstantGraph();
        //graph = new GenereatePlanarGraphUsingTriangulation().generate(50, frameSizeX, frameSizeY, 0f);
        //RunD3.makeSymmetric(graph, -50, 50);
        //RunD3.normolize(graph, frameSizeX, frameSizeY);
        graph = GenerateGraph.generateTreeStatic();
//
        String type = "twopi";

        MakePlanar.make(graph, type);
        norm(graph, frameSizeX, frameSizeY);

        RunD3.normolize(graph, frameSizeX, frameSizeY);

//        int i=0;
//        while (graph.checkOnPlanar()){
//            i++;
//            graph = GenerateGraph.generateTreeStatic();
//            MakePlanar.make(graph, type);
//            norm(graph, frameSizeX, frameSizeY);
//            RunD3.normolize(graph, frameSizeX, frameSizeY);
//            if (i%100==0){
//                System.out.println("#"+i);
//            }
//        }


        Voronoi voronoi = new Voronoi(graph, frameSizeX, frameSizeY);

        RunD3.normolize(voronoi, frameSizeX, frameSizeY);
        //testBig(voronoi, 1);
        //drawGraph(voronoi.voronoiLikeAGraph(voronoi.getPolygons()), group, 0);
//        float min = voronoi.getPolygons().get(0).getArea();
//        for (dataPrepare.data.voronoi.Polygon polygon : voronoi.getPolygons()){
//            if (min>polygon.getArea()){min=polygon.getArea();}
//        }
//        while (min<600){
//            graph = GenerateGraph.generateTreeStatic();
//            MakePlanar.make(graph);
//            norm(graph, frameSizeX, frameSizeY);
//            voronoi = new Voronoi(graph, frameSizeX, frameSizeY);
//            RunD3.normolize(voronoi, frameSizeX, frameSizeY);
//            min = voronoi.getPolygons().get(0).getArea();
//            for (dataPrepare.data.voronoi.Polygon polygon : voronoi.getPolygons()){
//                if (min>polygon.getArea()){min=polygon.getArea();}
//            }
//            for (dataPrepare.data.voronoi.Polygon polygon : voronoi.getPolygons()){
//                polygon.getHost().addMetric("cellSize", min);
//            }
//            System.out.println("MIN: "+min);
//        }
//
//

        SaveVoronoi.getInstance().startSaving("save");
        new TestPolymorph().newTest(voronoi);
        SaveVoronoi.getInstance().endSaving();
//
//        RunD3.normolize(voronoi, frameSizeX, frameSizeY);

        //drawGraph(voronoi.voronoiLikeAGraph(voronoi.getPolygons()), group, 1);


        //drawGraph(graph, group, 1);
        drawVoronoi(voronoi, group);


        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void testBig(Voronoi voronoi, float k){
        for (Coordinate coordinate : voronoi.getDots()){
            coordinate.setX(coordinate.getX()*k);
            coordinate.setY(coordinate.getY()*k);
        }
    }

    private void norm(Graph graph, float x, float y){
        float biggestX = 0, biggestY = 0;
        for (Host host : graph.getHosts()){
            if (biggestX<host.getCoordinate().getX()){biggestX=host.getCoordinate().getX();}
            if (biggestY<host.getCoordinate().getY()){biggestY=host.getCoordinate().getY();}
        }
        float k;
        if (biggestX<biggestY){k=y/biggestY*0.99f;}else {k=x/biggestX*0.99f;}
        for (Host host : graph.getHosts()){
            host.getCoordinate().setX(host.getCoordinate().getX()*k);
            host.getCoordinate().setY(host.getCoordinate().getY()*k);
        }
    }



    private Group drawGraph(Graph graph, Group group, int color){
        for (Host host : graph.getHosts()){
            if (graph.getRelations(host)!=null){
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
                    line.setStrokeWidth(2);

                    group.getChildren().add(line);
                }
            }


        }
        for (Host host : graph.getHosts()) {
            Circle circle = new Circle(
                    host.getCoordinate().getX() * scale,
                    host.getCoordinate().getY() * scale,
                    1
            );
            circle.setFill(Color.BLACK);
            circle.setOnMouseClicked(event -> {
                System.out.println(host.getMetrics().get("id"));
            });
            group.getChildren().add(circle);
        }
        return group;
    }



    private Group drawVoronoi(Voronoi voronoi, Group group){
        //Color[] colors = {Color.RED, Color.HOTPINK, Color.GREEN, Color.LIGHTGREEN, Color.BLUE, Color.LIGHTBLUE, Color.DARKRED};
        for (dataPrepare.data.voronoi.Polygon cell : voronoi.getPolygons()){
            Polygon polygon = new Polygon();
            for (Coordinate cellCoordinates : cell.getPoints()){
                Double[] points = new Double[2];
                points[0] = (double) cellCoordinates.getX();
                points[1] = (double) cellCoordinates.getY();
                polygon.getPoints().addAll(points);
                polygon.setFill(Color.WHITE);
                polygon.setOpacity(0);
                polygon.setOnMouseClicked(event -> {
                    System.out.println(TestPolymorph.checkLieFactor(cell));
                });
                if (cell.getHost().getMetrics().containsKey("color")){
                    //polygon.setFill((Color)cell.getHost().getMetrics().get("color"));
                    polygon.setOpacity(1);
                }

                polygon.setFill(Color.RED);
                polygon.setOpacity(1-1/TestPolymorph.checkLieFactor(cell));


            }
            group.getChildren().add(polygon);

            for (int i=0; i<=cell.getPoints().size(); i++){
                Coordinate start = cell.getPoints().get(i%cell.getPoints().size());
                Coordinate end = cell.getPoints().get((i+1)%cell.getPoints().size());
                Line line = new Line(start.getX(), start.getY(), end.getX(), end.getY());
                line.setStrokeWidth(5);
                line.setStroke(Color.DARKGRAY);
                line.setOpacity(0.7);
                for (Attractor attractor : voronoi.getAttractors()){
                    if (attractor.isAttractor(start, end)){
                        line.setStrokeWidth(1);
                        line.setStroke(Color.LIGHTGRAY);
                        line.setOpacity(0.7);
                        break;
                    }
                }


                group.getChildren().add(line);
            }
        }
        return group;
    }

}
