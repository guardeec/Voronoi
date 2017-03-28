package dataPrepare.draw;/**
 * Created by guardeec on 30.09.16.
 */

import com.google.gson.Gson;
import dataPrepare.data.debug.DebugVoronoiData;
import dataPrepare.data.debug.Dot;
import dataPrepare.data.debug.Edge;
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
    private long tickTime = 502795720/3000000;
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
        File file = new File("save");
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

    public void draw(Group group, DebugVoronoiData debugVoronoiData){
        group.getChildren().clear();
        group.getChildren().removeAll(group.getChildren());


        for (Edge edge : debugVoronoiData.getEdges()){

            Dot from = edge.getFrom();
            for (Dot to : edge.getTo()){
                Line line = new Line(
                        from.getX(),from.getY(),
                        to.getX(), to.getY()
                );
                line.setStrokeWidth(2);
                line.setStroke(Color.GRAY);
                line.setOpacity(0.8f);
                group.getChildren().add(line);
            }

            if (from.getC()!=0){
                Circle marker = new Circle(from.getX(),from.getY(), 20);
                if (from.getC()==1){marker.setFill(Color.BLUE);}
                if (from.getC()==2){marker.setFill(Color.GREEN);}
                if (from.getC()==3){marker.setFill(Color.YELLOW);}
                group.getChildren().add(marker);
            }



            Circle circle = new Circle(from.getX(),from.getY(), 3);
            if (from.isBlocked()){circle.setFill(Color.RED);}else {circle.setFill(Color.BLUE);}
            group.getChildren().add(circle);



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
            DebugVoronoiData debugVoronoiData =gson.fromJson(s, DebugVoronoiData.class);

            draw(group, debugVoronoiData);
            counter++;
        }
    }
}
