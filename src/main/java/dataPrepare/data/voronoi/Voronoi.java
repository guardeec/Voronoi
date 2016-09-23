package dataPrepare.data.voronoi;

import com.google.gson.Gson;
import dataPrepare.Test2;
import dataPrepare.data.Test;
import dataPrepare.data.graph.Coordinate;
import dataPrepare.data.graph.Graph;
import dataPrepare.data.triangulation.TriangleDotsImpl;
import dataPrepare.data.triangulation.TriangleVoronoiImpl;
import dataPrepare.data.graph.Host;
import dataPrepare.methods.ConvexHull;
import dataPrepare.methods.Triangulate;

import java.util.*;

/**
 * Created by Guardeec on 29.03.16.
 */
public class Voronoi {

    private List<Polygon> polygons;
    private List<Separator> separators;
    private List<Coordinate> dots;
    private Graph graph;

    public Voronoi(Graph graph, float frameSizeX, float frameSizeY){
        this.graph = graph;
        this.polygons = new LinkedList<>();
        this.dots = new LinkedList<>();
        this.separators = new LinkedList<>();

        graph = ConvexHull.make(graph);
        List<Host> convexHullHosts = ConvexHull.get(graph);

        Host LUHOST = new Host(1, new Coordinate(0,frameSizeY));
        Host LDHOST = new Host(1, new Coordinate(0,0));
        Host RUHOST = new Host(1, new Coordinate(frameSizeX,frameSizeY));
        Host RDHOST = new Host(1, new Coordinate(frameSizeX,0));
        graph.setHost(LUHOST);
        graph.setHost(LDHOST);
        graph.setHost(RUHOST);
        graph.setHost(RDHOST);
        graph.setRelation(LUHOST,RUHOST);
        graph.setRelation(LDHOST,RDHOST);
        graph.setRelation(LUHOST,LDHOST);
        graph.setRelation(RUHOST,RDHOST);
        for (Host host : convexHullHosts){
            switch (getClosestDotOfConvexHullBox(host.getCoordinate(), frameSizeX, frameSizeY)){
                case "LU" :{
                    graph.setRelation(host,RDHOST);
                    break;
                }
                case "LD" :{
                    graph.setRelation(host,RUHOST);
                    break;
                }
                case "RU" :{
                    graph.setRelation(host,LDHOST);
                    break;
                }
                case "RD" :{
                    graph.setRelation(host,LUHOST);
                    break;
                }
            }
        }

        ArrayList<TriangleVoronoiImpl> triangles = Triangulate.make(graph);
        for (TriangleVoronoiImpl triangle:triangles){
            TriangleDotsImpl triangleAsDots = (TriangleDotsImpl) triangle;
            triangle.setFirstHost(
                    graph.getHosts().get(
                            triangleAsDots.getFirstDot()
                    )
            );
            triangle.setSecondHost(
                    graph.getHosts().get(
                            triangleAsDots.getSecondDot()
                    )
            );
            triangle.setThirdHost(
                    graph.getHosts().get(
                            triangleAsDots.getThirdDot()
                    )
            );
        }

        graph.removeHost(LUHOST);
        graph.removeHost(LDHOST);
        graph.removeHost(RUHOST);
        graph.removeHost(RDHOST);

        //////////////////////////////////////////////////////////////
        //Test2.getInstance().setTriangles(triangles);
        //////////////////////////////////////////////////////////////

        makeField(triangles, graph, this);


    }

    public List<Polygon> getPolygons(){
        return this.polygons;
    }
    public List<Coordinate> getDots(){
        return this.dots;
    }
    public List<Separator> getSeparators(){
        return this.separators;
    }
    public Graph voronoiLikeAGraph(Voronoi voronoi){
        Graph graph = new Graph();

        Set<Coordinate> coordinateSet = new HashSet<>();
        for (Polygon polygon : voronoi.getPolygons()){
            for (Coordinate coordinate : polygon.getPoints()){
                coordinateSet.add(coordinate);
            }
        }

        for (Coordinate coordinate : coordinateSet){
            graph.setHost(new Host(1, coordinate));
        }

        for (Polygon polygon : voronoi.getPolygons()){
            for (int i=0; i<polygon.getPoints().size(); i++){
                Host from = getHostFromCoordinates(graph, polygon.getPoints().get(i%polygon.getPoints().size()));
                Host to = getHostFromCoordinates(graph, polygon.getPoints().get((i+1)%polygon.getPoints().size()));
                graph.setRelation(from, to);
            }
        }
        return graph;
    }
    public Graph getGraph() {
        return graph;
    }

    private String getClosestDotOfConvexHullBox(Coordinate coordinate, float x, float y){
        float LU = getDistance(coordinate, new Coordinate(0,y));
        float LD = getDistance(coordinate, new Coordinate(0,0));
        float RU = getDistance(coordinate, new Coordinate(x,y));
        float RD = getDistance(coordinate, new Coordinate(x,0));
        if (LU>=LD && LU>=RU && LU>=RD){
            return "LU";
        }
        if (LD>=LU && LD>=RU && LD>=RD){
            return "LD";
        }
        if (RU>=LD && RU>=LU && RU>=RD){
            return "RU";
        }
        if (RD>=LU && RD>=LD && RD>=RU){
            return "RD";
        }
        System.out.println(111);
        return null;
    }

    private void addPolygon(Polygon polygon){


        for (int i=0; i<polygon.getPoints().size(); i++){

            boolean contains = false;
            for (Coordinate dot : dots){
                if (Math.round(dot.getX())==Math.round(polygon.getPoints().get(i).getX())&&Math.round(dot.getY())==Math.round(polygon.getPoints().get(i).getY())){
                    contains = true;
                    polygon.getPoints().set(i, dot);
                }
            }
            if (!contains){
                this.dots.add(polygon.getPoints().get(i));
            }

        }
        this.polygons.add(polygon);


    }
    
    private Host getHostFromCoordinates(Graph graph, Coordinate coordinate){
        for (Host host : graph.getHosts()){
            if (host.getCoordinate().equals(coordinate)){
                return host;
            }
        }
        return null;
    }
    
    private Coordinate getCenterOfTriangle(TriangleVoronoiImpl triangle){
        return new Coordinate(
                (
                        triangle.getFirstHost().getCoordinate().getX()+
                        triangle.getSecondHost().getCoordinate().getX()+
                        triangle.getThirdHost().getCoordinate().getX()
                )/3,
                (
                        triangle.getFirstHost().getCoordinate().getY()+
                        triangle.getSecondHost().getCoordinate().getY()+
                        triangle.getThirdHost().getCoordinate().getY()
                )/3
        );
    }

    private List<TriangleVoronoiImpl> getTrianglesByHost(List<TriangleVoronoiImpl> triangles, Host host){
        List<TriangleVoronoiImpl> listOfTrianglesOfHost = new LinkedList<>();
        for (TriangleVoronoiImpl triangleVoronoi : triangles){
            if (triangleVoronoi.getFirstHost()==host || triangleVoronoi.getSecondHost()==host || triangleVoronoi.getThirdHost()==host){
                listOfTrianglesOfHost.add(triangleVoronoi);
            }
        }
        return listOfTrianglesOfHost;
    }

    public Polygon getPolygonFromTriangles(Host host, List<TriangleVoronoiImpl> triangleVoronoiList) {
        List<Coordinate> polygonCoordinates = new LinkedList<>();
        for (TriangleVoronoiImpl triangle : getTrianglesByHost(triangleVoronoiList, host)){
            polygonCoordinates.add(getCenterOfTriangle(triangle));
        }
        return new Polygon(polygonCoordinates, host);
    }

    public Voronoi makeField(List<TriangleVoronoiImpl> triangles, Graph graph, Voronoi voronoi){
        List<Host> convexHullHosts = ConvexHull.get(graph);
        for (Host host : graph.getHosts()){
            Polygon polygon = getPolygonFromTriangles(host, triangles);
            if (!convexHullHosts.contains(host)){
                voronoi.addPolygon(polygon);
            }

        }
        return voronoi;
    }


    private float getDistance(Coordinate from, Coordinate to){
        float distance = (float) Math.sqrt(
                Math.pow((double) (from.getX()-to.getX()),2)
                        +
                        Math.pow((double) (from.getY()-to.getY()),2)
        );
        return distance;
    }


}
