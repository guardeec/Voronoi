package dataPrepare.data;

import dataPrepare.voronoi.ConvexHull;
import dataPrepare.voronoi.Triangulate;

import java.util.*;

/**
 * Created by Guardeec on 29.03.16.
 */
public class Voronoi {

    private List<Polygon> polygons;
    private List<Separator> separators;
    private List<Coordinate> dots;
    private Graph graph;

    private List<Coordinate> convexHullDots;

    public Voronoi() {
        this.polygons = new LinkedList<>();
        this.dots = new LinkedList<>();
    }

    public Voronoi(Graph graph){
        this.graph = graph;
        this.polygons = new LinkedList<>();
        this.dots = new LinkedList<>();
        this.separators = new LinkedList<>();
        this.convexHullDots = new LinkedList<>();

        graph = ConvexHull.make(graph);
        List<Host> convexHullHosts = ConvexHull.get(graph);

        Host LUHOST = new Host(1, new Coordinate(0,500));
        Host LDHOST = new Host(1, new Coordinate(0,0));
        Host RUHOST = new Host(1, new Coordinate(500,500));
        Host RDHOST = new Host(1, new Coordinate(500,0));
        graph.setHost(LUHOST);
        graph.setHost(LDHOST);
        graph.setHost(RUHOST);
        graph.setHost(RDHOST);
        graph.setRelation(LUHOST,RUHOST);
        graph.setRelation(LDHOST,RDHOST);
        graph.setRelation(LUHOST,LDHOST);
        graph.setRelation(RUHOST,RDHOST);
        for (Host host : convexHullHosts){
            System.out.println(host.getCoordinate().getX());
        }
        for (Host host : convexHullHosts){
            switch (check(host.getCoordinate(), 500f, 500f)){
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
        makeField(triangles, graph, this);

        graph.removeHost(LUHOST);
        graph.removeHost(LDHOST);
        graph.removeHost(RUHOST);
        graph.removeHost(RDHOST);
    }

    private String check(Coordinate coordinate, float x, float y){
        float LU = getDistance(coordinate, new Coordinate(0,y));
        float LD = getDistance(coordinate, new Coordinate(0,0));
        float RU = getDistance(coordinate, new Coordinate(x,y));
        float RD = getDistance(coordinate, new Coordinate(x,0));
        if (LU>LD && LU>RU && LU>RD){
            return "LU";
        }
        if (LD>LU && LD>RU && LD>RD){
            return "LD";
        }
        if (RU>LD && RU>LU && RU>RD){
            return "RU";
        }
        if (RD>LU && RD>LD && RD>RU){
            return "RD";
        }
        System.out.println(111);
        return null;
    }

//    public void addPolygon(Polygon polygon){
//        //заменяем точки-дубликаты в полигонах при повторении
//        for (int i=0; i<polygon.getPoints().size(); i++){
//            boolean dotIsUnique = true;
//            for (int q=0; q<this.dots.size(); q++){
//                if (polygon.getPoints().get(i).equals(dots.get(q))){
//                    polygon.getPoints().set(i,dots.get(q));
//                    dotIsUnique = false;
//                }
//            }
//            if (dotIsUnique){
//                dots.add(polygon.getPoints().get(i));
//            }
//        }
//
//        // выявляем отношения
//        polygon.setRelations(graph.getRelations(polygon.getHost().getId()));
//        for (Polygon previosPoligon : polygons){
//            for (int id : polygon.getRelations()){
//                if (previosPoligon.getHost().getId()==id){
//                    List<Coordinate> separatorCoordinates = previosPoligon.getGeneralPoints(polygon);
//                    separators.add(new Separator(separatorCoordinates.get(0), separatorCoordinates.get(1)));
//                }
//            }
//        }
//
//        this.polygons.add(polygon);
//    }

    protected void setPolygon(Polygon polygon){
        if (polygon.getPoints().size()>=3){
            this.polygons.add(polygon);
            for (Coordinate coordinate : polygon.getPoints()){
                this.dots.add(coordinate);
            }
        }

    }

    private void addDot(Coordinate dot){
        boolean flag = true;
        for (Coordinate coordinate : this.dots){
            if (coordinate.equals(dot)){
                flag = false;
            }
        }
        if (flag){
            dots.add(dot);
        }
    }

    public List<Polygon> getPolygons(){
        return this.polygons;
    }

    public List<Coordinate> getDots(){
        return this.dots;
    }

    public List<Polygon> getPolygonByDot(Coordinate coordinate){
        List<Polygon> polygonsThatContainsDot = new LinkedList<>();
        for (int i=0; i<polygons.size(); i++){
            if (polygons.get(i).getPoints().contains(coordinate)){
                polygonsThatContainsDot.add(polygons.get(i));
            }
        }
        return polygonsThatContainsDot;
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
                if (polygon.getHost().getMetrics()!=null){
                    from.setMetrics(new Integer(1));
                    to.setMetrics(new Integer(1));
                }
                graph.setRelation(from, to);
            }
        }
        return graph;
    }

    private Host getHostFromCoordinates(Graph graph, Coordinate coordinate){
        for (Host host : graph.getHosts()){
            if (host.getCoordinate().equals(coordinate)){
                return host;
            }
        }
        return null;
    }

    public Graph getGraph() {
        return graph;
    }

    //private class VoronoiFromTriangles {

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
//                System.out.println("Polygon ");
//                for (Coordinate coordinate : polygon.getPoints()){
//                    System.out.println("X "+coordinate.getX()+" Y "+coordinate.getY());
//                }
//                System.out.println();
                if (!convexHullHosts.contains(host)){
                    voronoi.setPolygon(polygon);
                }

            }

            //moveConvexHullDots(voronoi);
            return voronoi;
        }

        private Voronoi moveConvexHullDots(Voronoi voronoi){
            List<Host> convexHullHosts = ConvexHull.get(voronoi.getGraph());
            List<Polygon> convexHullPolygons = new LinkedList<>();
            for (Polygon polygon : voronoi.getPolygons()){
                if (convexHullHosts.contains(polygon.getHost())){
                    convexHullPolygons.add(polygon);
                }
            }

            Set<Coordinate> dotsToMove = new HashSet<>();
            for (Polygon firstPolygon : convexHullPolygons){
                for (Polygon secondPolygon : convexHullPolygons){
                    if (firstPolygon!=secondPolygon){
                        for (Coordinate coordinateOfFirstPolygon : firstPolygon.getPoints()){
                            for (Coordinate coordinateOfSecondPolygon : secondPolygon.getPoints()){
                                if (coordinateOfFirstPolygon.equals(coordinateOfSecondPolygon)){
                                    dotsToMove.add(coordinateOfFirstPolygon);
                                }
                            }
                        }
                    }
                }
            }

            for (Polygon polygon : convexHullPolygons){
                System.out.println("P "+polygon.getPoints().size());
            }

            Coordinate centerOfVoronoi = getCenterOfVoronoi(voronoi);
            float dotMovementSize = getSizeOfDotMovementBasedOnSphereHull(voronoi.getDots(), centerOfVoronoi)*1f;

            for (Coordinate coordinate : dotsToMove){
                float angle = getAngle(centerOfVoronoi, coordinate);

                float switchSin = 1;
                float switchCos = 1;
                if (angle>90 && angle<180){
                    switchSin = -1;
                    switchCos = -1;
                }
                if (angle>270 && angle<360){
                    switchSin = -1;
                    switchCos = -1;
                }
                float currentDistance = getDistance(centerOfVoronoi, coordinate);
                float movementDistance = dotMovementSize-currentDistance;


                coordinate.changeX(switchSin*movementDistance*(float)Math.cos(Math.toRadians(angle)));
                coordinate.changeY(switchCos*movementDistance*(float)Math.sin(Math.toRadians(angle)));
                //coordinate.setX(switchSin*300*(float)Math.cos(Math.toRadians(angle)));
                //coordinate.setY(switchCos*300*(float)Math.sin(Math.toRadians(angle)));

                //coordinate.setX(centerOfVoronoi.getX());
                //coordinate.setY(centerOfVoronoi.getY());
            }

//            float minusX = 0;
//            float minusY = 0;
//            for (Coordinate coordinate : voronoi.getDots()){
//                if (minusX>coordinate.getX()){
//                    minusX=coordinate.getX();
//                }
//                if (minusY>coordinate.getY()){
//                    minusY=coordinate.getY();
//                }
//            }
//            minusX*=-1;
//            minusY*=-1;
//            for (Coordinate coordinate : voronoi.getDots()){
//                coordinate.changeX(minusX);
//                coordinate.changeY(minusY);
//            }


            return voronoi;
        }

        private float getAngle(Coordinate from, Coordinate to) {
            float angle = (float) Math.toDegrees(Math.atan2(to.getX() - from.getX(), to.getY() - from.getY()));
            angle = angle + (float) Math.ceil( -angle / 360 ) * 360;
            return angle;
        }

        private float getDistance(Coordinate from, Coordinate to){
            float distance = (float) Math.sqrt(
                    Math.pow((double) (from.getX()-to.getX()),2)
                            +
                            Math.pow((double) (from.getY()-to.getY()),2)
            );
            return distance;
        }

        private float getSizeOfDotMovementBasedOnSphereHull(List<Coordinate> dots, Coordinate centerOfSphere){
            float dotMovementSize = 0;
            for (Coordinate dot : dots){
                float size = getDistance(centerOfSphere, dot);
                if (dotMovementSize<size){
                    dotMovementSize=size;
                }
            }
            return dotMovementSize;
        }

        private Coordinate getCenterOfVoronoi(Voronoi voronoi){
            float left = voronoi.getDots().get(0).getX();
            float right = voronoi.getDots().get(0).getX();
            float up = voronoi.getDots().get(0).getY();
            float down = voronoi.getDots().get(0).getY();
            for (Coordinate coordinate : voronoi.getDots()){
                if (coordinate.getX()<left){
                    left = coordinate.getX();
                }
                if (coordinate.getX()>right){
                    right = coordinate.getX();
                }
                if (coordinate.getY()<down){
                    down = coordinate.getY();
                }
                if (coordinate.getY()>up){
                    up = coordinate.getY();
                }
            }
            return new Coordinate((up-down)/2, (right-left)/2);
        }

    //}


}
