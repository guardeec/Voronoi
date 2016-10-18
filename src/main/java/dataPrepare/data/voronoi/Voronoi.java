package dataPrepare.data.voronoi;

import com.google.gson.Gson;
import dataPrepare.Test2;
import dataPrepare.data.Dejkstra;
import dataPrepare.data.Test;
import dataPrepare.data.TestD3;
import dataPrepare.data.TestPolymorph;
import dataPrepare.data.graph.Coordinate;
import dataPrepare.data.graph.Graph;
import dataPrepare.data.triangulation.TriangleDotsImpl;
import dataPrepare.data.triangulation.TriangleVoronoiImpl;
import dataPrepare.data.graph.Host;
import dataPrepare.draw.SaveVoronoi;
import dataPrepare.methods.AddExternalPoints;
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

    private float fx, fy;


    public Voronoi(Graph graph, float frameSizeX, float frameSizeY, Polygon incapsulation){
        this.graph = graph;
        this.polygons = new LinkedList<>();
        this.dots = new LinkedList<>();
        this.separators = new LinkedList<>();

        graph = ConvexHull.make(graph);

        List<Host> boxHosts = addBox(graph, frameSizeX, frameSizeY);

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

        makeField(triangles, graph, this);

        for (Host host : boxHosts){
            graph.removeHost(host);
        }

        if (incapsulation==null){




            List<Polygon> polygonsList = polygons;
            for (Coordinate coordinate : this.getDots()){
                coordinate.addMetric("stop", false);
            }

            boolean flag = false;
            while (!flag){

                Collections.shuffle(polygonsList);
                for (Coordinate coordinate : this.getDots()){
                    coordinate.addMetric("stop", false);
                }

                for (Polygon polygon : polygonsList){
                    int freeDots = 0;
                    for (Coordinate coordinate : polygon.getPoints()){
                        boolean stop = (boolean) coordinate.getMetric("stop");
                        if (!stop){
                            freeDots++;
                        }
                    }
                    if (freeDots==0){
                        break;
                    }
                    if (freeDots!=0 && polygon==polygonsList.get(polygonsList.size()-1)){
                        flag = true;
                        break;
                    }
                    if (freeDots!=0){
                        for (Coordinate coordinate : polygon.getPoints()){
                            coordinate.addMetric("stop", true);
                        }
                    }
                }
            }


        }

    }

    private List<Coordinate> getConvegHullOfVoronoi(List<Polygon> polygons){
        List<Coordinate> dots = new LinkedList<>();
        for (Polygon polygon : polygons){
            for (Coordinate coordinate : polygon.getPoints()){
                if (!dots.contains(coordinate)){
                    dots.add(coordinate);
                }
            }
        }

        List<Coordinate> convexHull = ConvexHull.getFromCoordinates(dots);

        List<List<Coordinate>> edgesOfConvexHull = new LinkedList<>();
        for (int i=0;i<convexHull.size(); i++){
            List<Coordinate> edge = new LinkedList<>();
            edge.add(convexHull.get(i));
            edge.add(convexHull.get((i+1)%convexHull.size()));
            edgesOfConvexHull.add(edge);
        }

        List<List<Coordinate>> edgesOfVoronoi = new LinkedList<>();
        for (List<Host> edge : this.voronoiLikeAGraph(polygons).getEdges()){
            List<Coordinate> edgeOfVoronoi = new LinkedList<>();
            edgeOfVoronoi.add(edge.get(0).getCoordinate());
            edgeOfVoronoi.add(edge.get(1).getCoordinate());
            edgesOfVoronoi.add(edgeOfVoronoi);
        }
        Iterator<List<Coordinate>> edgeInterator = edgesOfConvexHull.iterator();
        while (edgeInterator.hasNext()){
            List<Coordinate> edgeOfConvexHull = edgeInterator.next();
            for (List<Coordinate> edgeOfVoronoi : edgesOfVoronoi){
                if (
                        (edgeOfVoronoi.get(0).equals(edgeOfConvexHull.get(0))&&edgeOfVoronoi.get(1).equals(edgeOfConvexHull.get(1)))
                                ||
                                (edgeOfVoronoi.get(0).equals(edgeOfConvexHull.get(1))&&edgeOfVoronoi.get(1).equals(edgeOfConvexHull.get(0)))
                        ){
                    edgeInterator.remove();
                }
            }
        }


        for (List<Coordinate> edge : edgesOfConvexHull){
            Dejkstra dejkstra = new Dejkstra(dots, edgesOfVoronoi);
            dejkstra.execute(edge.get(0));
            List<Coordinate> path = dejkstra.getPath(edge.get(1), dots);

            if (convexHull.contains(path.get(0))){
                int index = convexHull.indexOf(path.get(0));
                path.remove(path.size()-1);
                path.remove(0);
                convexHull.addAll(index+1, path);
            }

            for (Coordinate coordinate : path){
                Coordinate first = edge.get(0);
                Coordinate second = edge.get(1);
                float distanceToLine = Math.abs(
                        ((second.getX()-first.getX())*(coordinate.getY()-first.getY())-(second.getY()-first.getY())*(coordinate.getX()-first.getX()))
                                /
                                (float) Math.sqrt(Math.pow(second.getX()-first.getX(),2)+Math.pow(second.getY()-first.getY(),2))
                );
                float distanceToStartDot = distanceBetweenCoordinates(coordinate, first);
            }

        }


        return convexHull;
    }

    private float distanceBetweenCoordinates(Coordinate coordinate1, Coordinate coordinate2){
        return (float) Math.sqrt( Math.pow(coordinate1.getX()-coordinate2.getX(), 2) + Math.pow(coordinate1.getY()-coordinate2.getY(), 2));
    }


    private List<Host> addBox(Graph graph, float frameSizeX, float frameSizeY){
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

        List<Host> boxHosts = new LinkedList<>();
        boxHosts.add(LUHOST);
        boxHosts.add(LDHOST);
        boxHosts.add(RUHOST);
        boxHosts.add(RDHOST);
        return boxHosts;
    }

    public Voronoi(Graph graph, float frameSizeX, float frameSizeY){
        this.graph = graph;
        this.polygons = new LinkedList<>();
        this.dots = new LinkedList<>();
        this.separators = new LinkedList<>();
        //opt
        fx = frameSizeX;
        fy = frameSizeY;

        graph = ConvexHull.make(graph);

        List<Host> boxHosts = addBox(graph, frameSizeX, frameSizeY);

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

        makeField(triangles, graph, this);
        for (Host host : boxHosts){
            graph.removeHost(host);
        }




        new AddExternalPoints().add(this);
        List<Coordinate> ring = getConvegHullOfVoronoi(polygons);
        new AddExternalPoints().add(ring, this);

        for (Coordinate coordinate : dots){
            coordinate.addMetric("stopPolymorph", false);
            coordinate.addMetric("BLUE", false);
        }
        for (Coordinate coordinate : dots){
            coordinate.addMetric("fixed", false);
        }


        new TestD3().force_OUTPUT(this, TestD3.get2OUTPUT());
//        List<Coordinate> ringCoords = getConvegHullOfVoronoi(polygons);
//        for (Coordinate coordinate : ringCoords){
//            coordinate.addMetric("stopPolymorph", true);
//        }
//        new TestD3().force_INPUT(this);

        TestPolymorph.checkOnPlanar(this);
        SaveVoronoi.getInstance().saveStatement(polygons);

        List<Polygon> polygonsDeepOne = new LinkedList<>();
        for (Polygon polygon : polygons){
            if ((int)polygon.getHost().getMetrics().get("deep")==0){
                polygonsDeepOne.add(polygon);
            }
        }
        new TestPolymorph().polymorph(polygonsDeepOne, this, null);


        polygonsDeepOne = new LinkedList<>();
        for (Polygon polygon : polygons){
            if ((int)polygon.getHost().getMetrics().get("deep")==1){
                polygonsDeepOne.add(polygon);
            }
        }
        new TestPolymorph().polymorph(polygonsDeepOne, this, null);

//        polygonsDeepOne = new LinkedList<>();
//        for (Polygon polygon : polygons){
//            if ((int)polygon.getHost().getMetrics().get("deep")==2){
//                polygonsDeepOne.add(polygon);
//            }
//        }
//        new TestPolymorph().polymorph(polygonsDeepOne, this, null);
        new TestD3().force_INPUT(this);

//        polygonsDeepOne = new LinkedList<>();
//        for (Polygon polygon : polygons){
//            if ((int)polygon.getHost().getMetrics().get("deep")==1){
//                polygonsDeepOne.add(polygon);
//            }
//        }
//        new TestPolymorph().polymorph(polygonsDeepOne, this, null);
//
//        polygonsDeepOne = new LinkedList<>();
//        for (Polygon polygon : polygons){
//            if ((int)polygon.getHost().getMetrics().get("deep")==2){
//                polygonsDeepOne.add(polygon);
//            }
//        }
//        new TestPolymorph().polymorph(polygonsDeepOne, this, null);

        SaveVoronoi.getInstance().saveStatement(polygons);
    }

    private void calcDeep(){
        int counter = 0;
        List<Polygon> polygonsToAnalyse = new LinkedList<>();
        for (Polygon polygon : polygons){
            polygonsToAnalyse.add(polygon);
        }
        while (polygonsToAnalyse.size()>0){

            List<Coordinate> convexHull = getConvegHullOfVoronoi(polygonsToAnalyse);
            List<Polygon> chPolygons = new LinkedList<>();
            for (Polygon polygon : polygonsToAnalyse){
                for (Coordinate coordinate : convexHull){
                    if (polygon.getPoints().contains(coordinate)){
                        polygon.getHost().addMetric("deep", counter);
                        chPolygons.add(polygon);
                        break;
                    }
                }
            }
            polygonsToAnalyse.removeAll(chPolygons);
            counter++;
            //analysedDots.removeAll(convexHull);
        }
    }

    public int getDeepOfVoronoi(){
        int i=0;
        for (Polygon polygon : polygons){
            if (((int)polygon.getHost().getMetrics().get("deep"))>i){
                i=(int)polygon.getHost().getMetrics().get("deep");
            }
        }
        return i;
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
//    public Graph voronoiLikeAGraph(Voronoi voronoi){
//        Graph graph = new Graph();
//
//        Set<Coordinate> coordinateSet = new HashSet<>();
//        for (Polygon polygon : voronoi.getPolygons()){
//            for (Coordinate coordinate : polygon.getPoints()){
//                coordinateSet.add(coordinate);
//            }
//        }
//
//        for (Coordinate coordinate : coordinateSet){
//            graph.setHost(new Host(1, coordinate));
//        }
//
//        for (Polygon polygon : voronoi.getPolygons()){
//            for (int i=0; i<polygon.getPoints().size(); i++){
//                Host from = getHostFromCoordinates(graph, polygon.getPoints().get(i%polygon.getPoints().size()));
//                Host to = getHostFromCoordinates(graph, polygon.getPoints().get((i+1)%polygon.getPoints().size()));
//                if (!(graph.getRelations().get(from).contains(to) || graph.getRelations(to).contains(from))){
//                    graph.setRelation(from, to);
//                }
//
//            }
//        }
//        return graph;
//    }

    public Graph voronoiLikeAGraph(Voronoi voronoi){
        Graph graph = new Graph();


        for (Coordinate coordinate : dots){
            graph.setHost(new Host(1, coordinate));
        }

        for (Polygon polygon : voronoi.getPolygons()){
            for (int i=0; i<polygon.getPoints().size(); i++){
                Host from = getHostFromCoordinates(graph, polygon.getPoints().get(i%polygon.getPoints().size()));
                Host to = getHostFromCoordinates(graph, polygon.getPoints().get((i+1)%polygon.getPoints().size()));
                if (!(graph.getRelations().get(from).contains(to) || graph.getRelations(to).contains(from))){
                    graph.setRelation(from, to);
                }

            }
        }
        return graph;
    }

    public Graph voronoiLikeAGraph(List<Polygon> polygons){
        Graph graph = new Graph();

        Set<Coordinate> coordinateSet = new HashSet<>();
        for (Polygon polygon : polygons){
            for (Coordinate coordinate : polygon.getPoints()){
                coordinateSet.add(coordinate);
            }
        }

        for (Coordinate coordinate : coordinateSet){
            graph.setHost(new Host(1, coordinate));
        }

        for (Polygon polygon : polygons){
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
