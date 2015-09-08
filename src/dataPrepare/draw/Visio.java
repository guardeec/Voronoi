package dataPrepare.draw;/**
 * Created by anna on 27.08.15.
 */

import dataPrepare.data.Graph;
import dataPrepare.data.Polygon;
import dataPrepare.data.Triangle;
import dataPrepare.prepare.GenerateGraph;
import dataPrepare.voronoi.ConvexHull;
import dataPrepare.voronoi.Triangulate;
import dataPrepare.voronoi.VoronoiFromTriangles;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import sun.security.x509.CRLNumberExtension;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Visio extends Application {

    javafx.scene.shape.Polygon selected=null;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        //устанавливаем размеры и гуи
        int scale = 25;
        int frameSizeX = 57*scale;
        int frameSizeY = 39*scale;
        Group group = new Group();
        Scene scene = new Scene(group, frameSizeX, frameSizeY);

        //сгенерировать:
        //обычный граф
        Graph graph = GenerateGraph.generateConstantGraph();
        //звезда
       // Graph graph = GenerateGraph.generateStar();
        //колько
       // Graph graph = GenerateGraph.generateRing();
        //полносвязный граф
       // Graph graph = GenerateGraph.generateTriangulatedGraph();
        //линейный
       // Graph graph = GenerateGraph.generateLine();
        //вертекс
       // Graph graph = GenerateGraph.generateVertex();

        //вывод матрицы графа на консоль
        graph.printGraphToConsole();

        /*
        Начало алгоритма
         */

        //шаг 1 - выпуклая оболочка
        Graph graphToVoronoi = ConvexHull.make(graph);
        //шаг 2 - триангуляция + установление связи
        ArrayList<Triangle> triangles = Triangulate.make(graphToVoronoi);
        for (Triangle triangle:triangles){
            triangle.setCoordinates(graphToVoronoi);
        }
        graphToVoronoi = Triangulate.addToGraph(graphToVoronoi,triangles);
        //шаг 3 - диаграмма вороного
        List<Polygon> voronoi = VoronoiFromTriangles.makeField(triangles, graph);
        boolean[] graphDraw = {false, false, true};



        /*
        отрисовка графа (шаг 1)
         */
        if(graphDraw[0]){
            for (int i=0; i<graph.getHostsNumber(); i++){
                float startX = graph.getHost(i).getX()*scale;
                float startY = graph.getHost(i).getY()*scale;
                Circle circle = new Circle(startX, startY, 3);
                circle.setFill(Color.AQUA);
                group.getChildren().add(circle);
                int[] relations = graph.getRelations(i);
                for (int q=0; q<relations.length; q++) {
                    float endX = graph.getHost(relations[q]).getX()*scale;
                    float endY = graph.getHost(relations[q]).getY()*scale;
                    Line line = new Line();
                    line.setStartX(startX);
                    line.setStartY(startY);
                    line.setEndX(endX);
                    line.setEndY(endY);
                    line.setStroke(Color.GRAY);
                    if (i==32){
                        line.setStroke(Color.GREEN);
                    }
                    group.getChildren().add(line);
                }
            }
        }

        /*
        отрисовка тривнгуляции (шаг 2)
         */
        if(graphDraw[1]){
            for (int i=0; i<graphToVoronoi.getHostsNumber(); i++){
                float startX = graphToVoronoi.getHost(i).getX()*scale;
                float startY = graphToVoronoi.getHost(i).getY()*scale;
                Circle circle = new Circle(startX, startY, 3);
                circle.setFill(Color.AQUA);
                group.getChildren().add(circle);
                int[] relations = graphToVoronoi.getRelations(i);
                for (int q=0; q<relations.length; q++) {
                    float endX = graphToVoronoi.getHost(relations[q]).getX()*scale;
                    float endY = graphToVoronoi.getHost(relations[q]).getY()*scale;
                    Line line = new Line();
                    line.setStartX(startX);
                    line.setStartY(startY);
                    line.setEndX(endX);
                    line.setEndY(endY);
                    line.setStroke(Color.BLACK);
                    group.getChildren().add(line);
                }
            }
            //рисуем центры треугольников
            for(Triangle triangle:triangles){
                Circle circle = new Circle(triangle.getCenterCoordinates()[0]*scale, triangle.getCenterCoordinates()[1]*scale, 3);
                circle.setFill(Color.RED);
                group.getChildren().add(circle);
            }
        }

        /*
        отрисовка вороного (шаг 3)
         */
        if(graphDraw[2]){
            for (int i =0; i<voronoi.size(); i++){
                javafx.scene.shape.Polygon polygon = new javafx.scene.shape.Polygon();
                Double[] points = new Double[voronoi.get(i).getPoints().size()*2];
                for (int q=0, counter=0; q<voronoi.get(i).getPoints().size(); q++){
                    points[counter] = (double) voronoi.get(i).getPoints().get(q).getX()*scale;
                    counter++;
                    points[counter] = (double) voronoi.get(i).getPoints().get(q).getY()*scale;
                    counter++;
                }
                polygon.setId(Integer.toString(i));
                polygon.getPoints().addAll(points);

                polygon.setFill(Color.rgb(74,137,220));
                polygon.setOpacity(1);
                if (i<graph.getHostsNumber()){
                    polygon.setStroke(Color.BLACK);
                    polygon.setStrokeWidth(1);
                }else {
                    polygon.setStroke(Color.RED);
                    polygon.setOpacity(1);
                    polygon.setStrokeWidth(1);
                }
                if (i<graph.getHostsNumber()){
                    polygon.setId(Integer.toString(i));
                    polygon.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            /*
                            поиск пути
                             */
                            double opacity = 1;
                            for (int q = 0; q < graph.getHostsNumber(); q++) {
                                group.getChildren().get(q).setOpacity(0);
                            }
                            double counter = calcTheStepsForColor(group,voronoi,Integer.parseInt(polygon.getId()));
                            double opacityDiffrence = 1/counter;
                            for (int q = 0; q < graph.getHostsNumber(); q++) {
                                group.getChildren().get(q).setOpacity(0);
                            }
                            colorThePolygons(group, voronoi, Integer.parseInt(polygon.getId()), 1, opacityDiffrence);
                            /*
                            выделение хоста
                             */
                            /*
                            Timeline timeline = new Timeline();
                            if (selected==null){
                                System.out.println(polygon.getId());
                                System.out.println(selected);
                                timeline.getKeyFrames().addAll(
                                        new KeyFrame(Duration.ZERO,
                                                new KeyValue(polygon.translateXProperty(), 0),
                                                new KeyValue(polygon.translateYProperty(), 0)

                                        ),
                                        new KeyFrame(new Duration(100),
                                                new KeyValue(polygon.translateXProperty(), -10),
                                                new KeyValue(polygon.translateYProperty(), -10)
                                        )
                                );

                                selected=polygon;
                            }else {
                                timeline.getKeyFrames().addAll(

                                        new KeyFrame(Duration.ZERO,
                                                new KeyValue(polygon.translateXProperty(), 0),
                                                new KeyValue(polygon.translateYProperty(), 0),
                                                new KeyValue(selected.translateXProperty(), -10),
                                                new KeyValue(selected.translateYProperty(), -10)

                                        ),
                                        new KeyFrame(new Duration(100),
                                                new KeyValue(polygon.translateXProperty(), -10),
                                                new KeyValue(polygon.translateYProperty(), -10),
                                                new KeyValue(selected.translateXProperty(), 0),
                                                new KeyValue(selected.translateYProperty(), 0)
                                        )
                                );

                                selected=polygon;
                            }

                            timeline.play();*/
                        }
                    });
                }
                group.getChildren().add(polygon);
            }
        }
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    /*
    раскараска путей
     */
    private void colorThePolygons(Group group, List<Polygon> voronoi, int startId, double opacity, double opacityDiffrence){

        if (group.getChildren().get(startId).getOpacity()<opacity){
            group.getChildren().get(startId).setOpacity(opacity);
            opacity=opacity-opacityDiffrence;
            for (int i=0; i<voronoi.get(startId).getRelations().length; i++){
                colorThePolygons(group, voronoi, voronoi.get(startId).getRelations()[i], opacity, opacityDiffrence);
            }
        }
    }

    /*
    раскраска путей для поиска по максимально отдалённому хосту
     */
    private void colorThePolygonsForCalc(Group group, List<Polygon> voronoi, int startId, double opacity, double opacityDiffrence){

        if (group.getChildren().get(startId).getOpacity()<opacity){
            group.getChildren().get(startId).setOpacity(opacity);
            opacity=opacity-opacityDiffrence;
            for (int i=0; i<voronoi.get(startId).getRelations().length; i++){
                colorThePolygons(group, voronoi, voronoi.get(startId).getRelations()[i], opacity, opacityDiffrence);
            }
        }
    }

    /*
    поиск максимально отдалённого хоста
     */
    private double calcTheStepsForColor(Group group, List<Polygon> voronoi, int startId){
        colorThePolygonsForCalc(group, voronoi, startId, 1, 0.001);
        double minOpacity=2;
        for (int i=0; i<group.getChildren().size(); i++){
            double opacity = group.getChildren().get(i).getOpacity();
            if (opacity>0 && minOpacity>opacity){
                minOpacity=opacity;
            }
        }
        Double steps = (1-minOpacity)/0.001;
        return steps;
    }

}
