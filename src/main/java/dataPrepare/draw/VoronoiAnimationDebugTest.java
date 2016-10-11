package dataPrepare.draw;/**
 * Created by guardeec on 30.09.16.
 */

import com.google.gson.Gson;
import dataPrepare.data.voronoi.Polygon;
import dataPrepare.data.voronoi.Voronoi;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.io.*;
import java.util.LinkedList;
import java.util.List;



public class VoronoiAnimationDebugTest extends Application {
    int counter = 0;
    Group group;
    private long lastUpdate = 0 ;
    private long tickTime = 502795720/30;
    BufferedReader in;
    StringBuilder sb;
    Gson gson = new Gson();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        group = new Group();
        Scene scene = new Scene(group, 1000, 1000);



        sb = new StringBuilder();
        File file = new File("test");

        try {
            in = new BufferedReader(new FileReader( file.getAbsoluteFile()));

        } catch(IOException e) {
            throw new RuntimeException(e);
        }


        AnimationTimer timer = new MyTimer();
        timer.start();


        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void draw(Group group, List<Polygon> polygons){
        group.getChildren().removeAll(group.getChildren());
        for (Polygon polygon : polygons){
            for (int i=0; i<polygon.getPoints().size(); i++){
                Line line = new Line(
                        polygon.getPoints().get(i).getX(),
                        polygon.getPoints().get(i).getY(),
                        polygon.getPoints().get((i+1)%polygon.getPoints().size()).getX(),
                        polygon.getPoints().get((i+1)%polygon.getPoints().size()).getY()
                        );
                line.setStrokeWidth(2);
                line.setStroke(Color.GRAY);
                line.setOpacity(0.8f);
                group.getChildren().add(line);

                Circle circle = new Circle();
                circle.setCenterX(polygon.getPoints().get(i).getX());
                circle.setCenterY(polygon.getPoints().get(i).getY());
                circle.setRadius(4);
                if ((boolean)polygon.getPoints().get(i).getMetric("stopPolymorph")){
                    circle.setFill(Color.RED);
                }else {
                    circle.setFill(Color.GRAY);
                }
                if (polygon.getPoints().get(i).getMetric("BLUE")!=null){
                    Circle circle1 = new Circle();
                    circle1.setCenterX(polygon.getPoints().get(i).getX());
                    circle1.setCenterY(polygon.getPoints().get(i).getY());
                    circle1.setRadius(8);
                    circle1.setFill(Color.BLUE);
                    group.getChildren().add(circle1);
                }
                group.getChildren().add(circle);

            }
        }
    }


    private class MyTimer extends AnimationTimer {

        @Override
        public void handle(long now) {
            if (now - lastUpdate >= tickTime) {
                System.out.println(now - lastUpdate);
                doHandle();
                lastUpdate = now ;
            }
        }

        private void doHandle() {

            String s= null;
            try {
                s = in.readLine();
                sb.append(s);
                sb.append("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            List list =gson.fromJson(s, List.class);
            List<Polygon> polygonList = new LinkedList<>();
            for (Object o : list){
                String p = gson.toJson(o);
                Polygon polygon = gson.fromJson(p, Polygon.class);
                polygonList.add(polygon);
            }
            draw(group, polygonList);
            counter++;
        }
    }

}
