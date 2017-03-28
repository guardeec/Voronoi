package dataPrepare.data.d3;

import com.google.gson.Gson;
import dataPrepare.data.graph.Coordinate;
import dataPrepare.data.graph.Graph;
import dataPrepare.data.graph.Host;
import dataPrepare.data.voronoi.Polygon;
import dataPrepare.data.voronoi.Voronoi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by guardeec on 13.03.17.
 */
public class RunD3 {
    private static String runNodeD3(String JSON, int force, int linkDistance){
        JSON = "node test.js "+""+JSON+" "+force+" "+linkDistance;
        String result = "";
        Process p;
        try {
            p = Runtime.getRuntime().exec(JSON);
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            String line = null;
            while ((line=in.readLine())!=null){
                result+=line;
            }

            while ((line=err.readLine())!=null){
                result+=line;
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void normolize(Graph graph, float screenSizeX, float screenSizeY){
        float smallestX = graph.getHosts().get(0).getCoordinate().getX(),
                smallestY=graph.getHosts().get(0).getCoordinate().getY(),
                biggestX = graph.getHosts().get(0).getCoordinate().getX(),
                biggestY = graph.getHosts().get(0).getCoordinate().getY();
        for (Host host : graph.getHosts()){
            if(smallestX>host.getCoordinate().getX()){smallestX=host.getCoordinate().getX();}
            if(smallestY>host.getCoordinate().getY()){smallestY=host.getCoordinate().getY();}
            if(biggestX<host.getCoordinate().getX()){biggestX=host.getCoordinate().getX();}
            if(biggestY<host.getCoordinate().getY()){biggestY=host.getCoordinate().getY();}
        }

        if (smallestX<0){
            for (Host host : graph.getHosts()){
                host.getCoordinate().setX(host.getCoordinate().getX()-smallestX);
            }
            biggestX-=smallestX;
        }
        if (smallestY<0){
            for (Host host : graph.getHosts()){
                host.getCoordinate().setY(host.getCoordinate().getY()-smallestY);
            }
            biggestY-=smallestY;
        }
        if (biggestX>screenSizeX){
            float k = screenSizeX/biggestX;
            for (Host host : graph.getHosts()){
                host.getCoordinate().setX(host.getCoordinate().getX()*k);
            }
        }
        if (biggestY>screenSizeY){
            float k = screenSizeY/biggestY;
            for (Host host : graph.getHosts()){
                host.getCoordinate().setY(host.getCoordinate().getY()*k);
            }
        }

    }

    public static void normolize(Voronoi voronoi, float screenSizeX, float screenSizeY){
        float smallestX = voronoi.getDots().get(0).getX(),
                smallestY=voronoi.getDots().get(0).getY(),
                biggestX = voronoi.getDots().get(0).getX(),
                biggestY = voronoi.getDots().get(0).getY();
        for (Coordinate coordinate : voronoi.getDots()){
            if(smallestX>coordinate.getX()){smallestX=coordinate.getX();}
            if(smallestY>coordinate.getY()){smallestY=coordinate.getY();}
            if(biggestX<coordinate.getX()){biggestX=coordinate.getX();}
            if(biggestY<coordinate.getY()){biggestY=coordinate.getY();}
        }

        if (smallestX<0){
            for (Coordinate coordinate : voronoi.getDots()){
                coordinate.setX(coordinate.getX()-smallestX);
            }
            biggestX-=smallestX;
        }
        if (smallestY<0){
            for (Coordinate coordinate : voronoi.getDots()){
                coordinate.setY(coordinate.getY()-smallestY);
            }
            biggestY-=smallestY;
        }
        for (Coordinate coordinate : voronoi.getDots()){
            coordinate.setY(coordinate.getY()*screenSizeY/biggestY);
            coordinate.setX(coordinate.getX()*screenSizeX/biggestX);
        }


//
//        if (smallestX<0){
//            for (Coordinate coordinate : voronoi.getDots()){
//                coordinate.setX(coordinate.getX()-smallestX);
//            }
//            biggestX-=smallestX;
//        }
//        if (smallestY<0){
//            for (Coordinate coordinate : voronoi.getDots()){
//                coordinate.setY(coordinate.getY()-smallestY);
//            }
//            biggestY-=smallestY;
//        }
//        if (biggestX>screenSizeX){
//            float k = screenSizeX/biggestX;
//            for (Coordinate coordinate : voronoi.getDots()){
//                coordinate.setX(coordinate.getX()*k);
//            }
//        }
//        if (biggestY>screenSizeY){
//            float k = screenSizeY/biggestY;
//            for (Coordinate coordinate : voronoi.getDots()){
//                coordinate.setY(coordinate.getY()*k);
//            }
//        }

//        for (Coordinate coordinate : voronoi.getDots()){
//            coordinate.setY(coordinate.getY()/2);
//            coordinate.setX(coordinate.getX()/2);
//        }
    }

    public static void makeSymmetric(Voronoi voronoi, List<Coordinate> fixedNodes, int force, int linkDistance){
        List symmetric = new Gson().fromJson(runNodeD3(getGraph(voronoi, fixedNodes), force, linkDistance), List.class);
        for (int i=0; i<voronoi.getDots().size(); i++){
            HostD3 newCoordinate = new Gson().fromJson(new Gson().toJson(symmetric.get(i)), HostD3.class);
            Coordinate oldCoordinate = voronoi.getDots().get(i);
            oldCoordinate.setX(newCoordinate.getX());
            oldCoordinate.setY(newCoordinate.getY());
        }
    }

    public static void makeSymmetric(Graph graph, int force, int linkDistance){
        GraphD3 graphD3 = new GraphD3();
        for (int i=0; i<graph.getHosts().size(); i++){
            graph.getHosts().get(i).addMetric("d3ID", i);
            graphD3.addNode(i, graph.getHosts().get(i).getCoordinate().getX(), graph.getHosts().get(i).getCoordinate().getY(), false);
        }
        for (int i=0; i<graph.getHosts().size(); i++){
            int fromId = (int) graph.getHosts().get(i).getMetrics().get("d3ID");
            for (Host host : graph.getRelations(graph.getHosts().get(i))){
                graphD3.addLink(fromId, (int) host.getMetrics().get("d3ID"));
            }
        }
        List symmetric = new Gson().fromJson(runNodeD3(new Gson().toJson(graphD3), force, linkDistance), List.class);
        for (int i=0; i<graph.getHostNumber(); i++){
            HostD3 newCoordinate = new Gson().fromJson(new Gson().toJson(symmetric.get(i)), HostD3.class);
            Coordinate oldCoordinate = graph.getHosts().get(i).getCoordinate();
            oldCoordinate.setX(newCoordinate.getX());
            oldCoordinate.setY(newCoordinate.getY());
        }
    }

    private static String getGraph(Voronoi voronoi, List<Coordinate> fixedNodes){
        GraphD3 graph = new GraphD3();
        for (int i=0; i<voronoi.getDots().size(); i++){
            boolean fixed = false;
            if (fixedNodes.contains(voronoi.getDots().get(i))){
                fixed = true;
            }
            graph.addNode(i, voronoi.getDots().get(i).getX(), voronoi.getDots().get(i).getY(), fixed);
        }
        for (Polygon polygon : voronoi.getPolygons()){
            for (int i=0; i<polygon.getPoints().size(); i++){
                int from = graph.getHost(polygon.getPoints().get(i%polygon.getPoints().size()).getX(), polygon.getPoints().get(i%polygon.getPoints().size()).getY());
                int to = graph.getHost(polygon.getPoints().get((i+1)%polygon.getPoints().size()).getX(), polygon.getPoints().get((i+1)%polygon.getPoints().size()).getY());
                graph.addLink(from, to);
            }
        }


        return new Gson().toJson(graph);
    }


}
