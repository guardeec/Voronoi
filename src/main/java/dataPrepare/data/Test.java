package dataPrepare.data;

import dataPrepare.Test2;
import dataPrepare.data.graph.Coordinate;
import dataPrepare.data.graph.Graph;
import dataPrepare.data.graph.Host;
import dataPrepare.data.triangulation.Triangle;
import dataPrepare.data.triangulation.TriangleVoronoiImpl;
import dataPrepare.methods.ConvexHull;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.*;

/**
 * Created by guardeec on 11.07.16.
 */
public class Test {
    private List<Host> hosts;
    private Map<Host, List<TriangleVoronoiImpl>> trianglesOfHosts;
    private Map<Host, Float> sizes;
    private List<List<Host>> edges;

    public Test(){
    }

    /*
    Хосты заносятся вместе с целевыми метриками
     */
    public List<Host> getHosts() {
        return hosts;
    }
    public void setHosts(List<Host> hosts, Map<Host, Float> sizes) {
        this.hosts = hosts;
        this.sizes = sizes;
    }
    public void removeHost(Host host){
        hosts.remove(host);
    }
    public Map<Host, Float> getSizes() {
        return sizes;
    }

    /*
    Каждому хосту асоциируются треугольники из триангуляции
    при инициализации также создается список ребер треугольников
     */
    public Map<Host, List<TriangleVoronoiImpl>> getTrianglesOfHosts() {
        return trianglesOfHosts;
    }
    public void setTrianglesOfHosts(Map<Host, List<TriangleVoronoiImpl>> trianglesOfHosts) {
        for (Host host : hosts){
            List<TriangleVoronoiImpl> trianglesOfHost = trianglesOfHosts.get(host);
            trianglesOfHost.sort(new Comparator<TriangleVoronoiImpl>() {
                @Override
                public int compare(TriangleVoronoiImpl o1, TriangleVoronoiImpl o2) {
                    if (getAngle(host.getCoordinate(), getCenterOfTriangle(o1))>getAngle(host.getCoordinate(), getCenterOfTriangle(o2))){
                        return -1;
                    }
                    return 0;
                }
            });
        }

        this.edges = new LinkedList<>();
        for (Host host : hosts){
            for (TriangleVoronoiImpl triangleVoronoi : trianglesOfHosts.get(host)){
                List edge1 = new LinkedList<>(); edge1.add(triangleVoronoi.getFirstHost()); edge1.add(triangleVoronoi.getSecondHost());
                List edge2 = new LinkedList<>(); edge2.add(triangleVoronoi.getSecondHost()); edge2.add(triangleVoronoi.getThirdHost());
                List edge3 = new LinkedList<>(); edge3.add(triangleVoronoi.getThirdHost()); edge3.add(triangleVoronoi.getFirstHost());
                if (!edjesContainsEdge(edge1, edges)){
                    edges.add(edge1);
                }
                if (!edjesContainsEdge(edge2, edges)){
                    edges.add(edge2);
                }
                if (!edjesContainsEdge(edge3, edges)){
                    edges.add(edge3);
                }
            }
        }

        this.trianglesOfHosts = trianglesOfHosts;
    }
    private float getAngle(Coordinate host, Coordinate center){
        double x1 = (double) host.getX(), y1 = (double) host.getY();
        double x2 = (double) center.getX(), y2 = (double) center.getY();
        double A = Math.atan2(y1 - y2, x1 - x2) / Math.PI * 180;
        //A = (A < 0) ? A + 360 : A;
        return (float) A;
    }
    private boolean edjesContainsEdge(List<Host> edge, List<List<Host>> edges){
        for (List<Host> host : edges){
            if (
                    (
                        edge.get(0).hashCode()==host.get(0).hashCode()
                        &&
                                edge.get(1).hashCode()==host.get(1).hashCode()
                    )
                    ||
                    (
                            edge.get(1).hashCode()==host.get(0).hashCode()
                        &&
                                    edge.get(0).hashCode()==host.get(1).hashCode()
                    )
            ){
                return true;
            }
        }
        return false;
    }



    public List<Node> drawTest(Group group){
        List<Node> nodes = new LinkedList<>();
        for (List<Host> edge : edges){
            Line line = new Line();
            line.setId(edge.toString());
            line.setStartX(edge.get(0).getCoordinate().getX());
            line.setStartY(edge.get(0).getCoordinate().getY());
            line.setEndX(edge.get(1).getCoordinate().getX());
            line.setEndY(edge.get(1).getCoordinate().getY());
            line.setStroke(Color.GREEN);
            line.setStrokeWidth(3);
            nodes.add(line);
        }
        for (Host host : hosts){
            Circle circle = new Circle();
            circle.setId(host.toString());
            circle.setCenterX(host.getCoordinate().getX());
            circle.setCenterY(host.getCoordinate().getY());
            circle.setRadius(3);
        }


        for (Host host : hosts){
            List<Coordinate> coordinateList = new LinkedList<>();
            for (TriangleVoronoiImpl triangleVoronoi : trianglesOfHosts.get(host)){
                Coordinate center = getCenterOfTriangle(triangleVoronoi);
                coordinateList.add(center);
            }

            for (int z=0; z<coordinateList.size(); z++){
                Line line = new Line();
                line.setId(coordinateList.get(z).toString()+coordinateList.get((z+1)%coordinateList.size()).toString());
                line.setStartX(coordinateList.get(z).getX());
                line.setStartY(coordinateList.get(z).getY());
                line.setEndX(coordinateList.get((z+1)%coordinateList.size()).getX());
                line.setEndY(coordinateList.get((z+1)%coordinateList.size()).getY());
                line.setStroke(Color.RED);
                line.setStrokeWidth(2);
                nodes.add(line);
            }
        }

        System.out.println(checkOnPlanar());
        System.out.println(getTargetFunction());
        System.out.println("Start Polymorph");

        List<Host> hostsToMove = new LinkedList<>();
        for (List<Host> edge : edges){
            for (Host hostOfEdge : edge){
                if (!hostsToMove.contains(hostOfEdge)){
                    hostsToMove.add(hostOfEdge);
                }
            }
        }
        System.out.println("Host number = "+hostsToMove.size());

        //float previosTargetF = getTargetFunction();
        List<Float> previosTargetF = getTestTargetFunction();



        List<List<Host>> layers = new LinkedList<>();
        Graph graph = new Graph();
        for (Host host : hostsToMove){
            graph.setHost(host);
        }
        while (graph.getHosts().size()>0){
            layers.add(ConvexHull.get(graph));
            for (Host host : layers.get(layers.size()-1)){
                graph.removeHost(host);
            }
        }
        System.out.println("layers: "+layers.size());
        //System.exit(0);




        /*
        Формируем список решений на основе движения хоста
         */
        List<Decision> decisions = new LinkedList<>();
        for (Host host : hostsToMove){
            /*
            Двигаем на 0, 45, 90, 135, 180, 225, 270, 315 градусов
             */
            for (int angle=0; angle<360; angle+=30){
                Decision decision = getDecisionAfterMovement(host, angle, 10, previosTargetF);
                if (decision!=null){
                    decisions.add(decision);
                }
            }
        }
        /*
        Если нет решений, то прекращаем работу
        Иначе принимаем решение
         */
        //makeDecision(decisions);

        for (List<Host> edge : edges){
            for (Node node : nodes){
                if (Objects.equals(node.getId(), edge.toString())){
                    ((Line) node).setStartX(edge.get(0).getCoordinate().getX());
                    ((Line) node).setStartY(edge.get(0).getCoordinate().getY());
                    ((Line) node).setEndX(edge.get(1).getCoordinate().getX());
                    ((Line) node).setEndY(edge.get(1).getCoordinate().getY());
                }
            }
        }
        for (Host host : hosts){
            int index=0;
            for (List<Host> layer : layers){
                if (layer.contains(host)){
                    index=layers.indexOf(layer);
                }
            }
            index++;
            List<Coordinate> coordinateList = new LinkedList<>();
            for (TriangleVoronoiImpl triangleVoronoi : trianglesOfHosts.get(host)){
                Coordinate center = getCenterOfTriangle(triangleVoronoi);
                coordinateList.add(center);
            }

            for (int z=0; z<coordinateList.size(); z++){
                for (Node node : nodes){
                    if (Objects.equals(node.getId(), coordinateList.get(z).toString() + coordinateList.get((z + 1)%coordinateList.size()).toString())){
                        ((Line) node).setStartX(coordinateList.get(z).getX());
                        ((Line) node).setStartY(coordinateList.get(z).getY());
                        ((Line) node).setEndX(coordinateList.get((z+1)%coordinateList.size()).getX());
                        ((Line) node).setEndY(coordinateList.get((z+1)%coordinateList.size()).getY());
                    }
                }
            }
            for (Node node : nodes){
                if (node.getId().equals(host.toString())){
                    ((Circle) node).setRadius(index*3);
                    System.out.println("AAAAAAAAAAAA");
                }
            }
        }
        return nodes;
    }

    private class Decision {
        private Host host;
        private Coordinate newCoordinate;
        private float targetF;

        public Decision(Host host, Coordinate newCoordinate, float targetF) {
            this.host = host;
            this.newCoordinate = newCoordinate;
            this.targetF = targetF;
        }

        public Host getHost() {
            return host;
        }

        public void setHost(Host host) {
            this.host = host;
        }

        public Coordinate getNewCoordinate() {
            return newCoordinate;
        }

        public void setNewCoordinate(Coordinate newCoordinate) {
            this.newCoordinate = newCoordinate;
        }

        public float getTargetF() {
            return targetF;
        }

        public void setTargetF(float targetF) {
            this.targetF = targetF;
        }
    }
    private void hostMovement(Host host, float angle, float distance){
        double angleR = Math.toRadians((double) angle);
        double x1 = host.getCoordinate().getX();
        double y1 = host.getCoordinate().getY();
        host.getCoordinate().setX((float) (x1+distance*Math.cos(angleR)));
        host.getCoordinate().setY((float) (y1+distance*Math.sin(angleR)));
    }
    private Decision getDecisionAfterMovement(Host host, float angle, float distance, List<Float> previosTargetF){
        List<Float> currentTargetF = getTestTargetFunction();
        for (int i=0; i<previosTargetF.size(); i++){
            if (previosTargetF.get(i)<currentTargetF.get(i)){
                return null;
            }
        }

        float x = host.getCoordinate().getX();
        float y = host.getCoordinate().getY();
        hostMovement(host, angle, distance);
        if (checkOnPlanar()){
            Decision decision = new Decision(
                    host,
                    new Coordinate(host.getCoordinate().getX(), host.getCoordinate().getY()),
                    getTargetFunction()
            );
            host.getCoordinate().setX(x);
            host.getCoordinate().setY(y);
            return decision;
        }else {
            host.getCoordinate().setX(x);
            host.getCoordinate().setY(y);
            return null;
        }
    }
    private void makeDecision(List<Decision> decisions){
        Decision finalDecision = decisions.get(0);
        for (Decision decision : decisions){
            if (finalDecision.getTargetF()>decision.getTargetF()){
                finalDecision = decision;
            }
        }
        finalDecision.getHost().getCoordinate().setX(
                finalDecision.getNewCoordinate().getX()
        );
        finalDecision.getHost().getCoordinate().setY(
                finalDecision.getNewCoordinate().getY()
        );
    }
    /*
    На основе центров треугольников хоста строится ячейка
     */
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

    /*
    Вычисление целевой функции для диаграммы
    Вычисление целевой функции для хоста
    И вычисление получившегося размера хоста
     */
    private Float getTargetFunction(){
        float virtualSize = 0;
        float realSize = 0;
        for (Host host : hosts){
            virtualSize += sizes.get(host);
            realSize += getSquareOfPolygon(host);
        }
        float relation = realSize/virtualSize;

        float targetF = 0;
        for (Host host : hosts){
            targetF+=Math.abs(relation*sizes.get(host)-getSquareOfPolygon(host));
        }

//        float k=0;
//        float biggestEdge=lengthOfEdge(edges.get(0));
//        float smallestEdge=lengthOfEdge(edges.get(0));
//        for (List<Host> edge : edges){
//            float length = lengthOfEdge(edge);
//            if (length>biggestEdge){
//                biggestEdge=length;
//            }
//            if (length<smallestEdge){
//                smallestEdge=length;
//            }
//        }
//        k=biggestEdge/smallestEdge;

        return targetF;
    }
    private float lengthOfEdge(List<Host> edge){
        return (float) Math.sqrt( Math.pow(edge.get(0).getCoordinate().getX()-edge.get(1).getCoordinate().getX(), 2) + Math.pow(edge.get(0).getCoordinate().getY()-edge.get(1).getCoordinate().getY(), 2));
    }

    private List<Float> getTestTargetFunction(){
        float virtualSize = 0;
        float realSize = 0;
        for (Host host : hosts){
            virtualSize += sizes.get(host);
            realSize += getSquareOfPolygon(host);
        }
        float relation = realSize/virtualSize;

        List<Float> targetF = new LinkedList<>();
        for (Host host : hosts){
            targetF.add(Math.abs(relation*sizes.get(host)-getSquareOfPolygon(host)));
        }

        return targetF;
    }

    private float hostSizeTargetF(Host targetHost){
        float targetHostSize = sizes.get(targetHost);
        float realHostSize = getSquareOfPolygon(targetHost);

        return Math.abs(targetHostSize-realHostSize);
    }
    private float getSquareOfPolygon(Host host){
        List<Float> middleResult = new LinkedList<>();
        for (int i=0; i<this.getTrianglesOfHosts().get(host).size(); i++){
            Coordinate coordinate1 = getCenterOfTriangle(this.getTrianglesOfHosts().get(host).get(i));
            Coordinate coordinate2 = getCenterOfTriangle(this.getTrianglesOfHosts().get(host).get((i+1)%this.getTrianglesOfHosts().get(host).size()));
            middleResult.add((coordinate1.getX()*coordinate2.getY()-coordinate1.getY()*coordinate2.getX())/2);
        }
        float size = 0;
        for (float a : middleResult){
            size+=a;
        }
        return size;
    }
    public boolean checkOnPlanar(){
        for (int i=0; i<edges.size(); i++){
            double x1 = edges.get(i).get(0).getCoordinate().getX();
            double y1 = edges.get(i).get(0).getCoordinate().getY();
            double x2 = edges.get(i).get(1).getCoordinate().getX();
            double y2 = edges.get(i).get(1).getCoordinate().getY();



            for (int q=i; q<edges.size(); q++){
                if (
                        edges.get(i)!=edges.get(q)
                        &&
                        edges.get(i).get(0)!=edges.get(i).get(1)
                        &&
                        edges.get(i).get(0)!=edges.get(q).get(0)
                        &&
                        edges.get(i).get(0)!=edges.get(q).get(1)
                        &&
                        edges.get(i).get(1)!=edges.get(q).get(0)
                        &&
                        edges.get(i).get(1)!=edges.get(q).get(1)
                        &&
                        edges.get(q).get(0)!=edges.get(q).get(1)
                        ) {

//                    if (x1<0 || x1>500 ||
//                            edges.get(i).get(0).getCoordinate().getX() < 0 ||
//                            edges.get(i).get(1).getCoordinate().getX() < 0 ||
//                            edges.get(q).get(0).getCoordinate().getX() < 0 ||
//                            edges.get(q).get(1).getCoordinate().getX() < 0 ||
//                            edges.get(i).get(0).getCoordinate().getY() < 0 ||
//                            edges.get(i).get(1).getCoordinate().getY() < 0 ||
//                            edges.get(q).get(0).getCoordinate().getY() < 0 ||
//                            edges.get(q).get(1).getCoordinate().getY() < 0 ||
//
//                            edges.get(i).get(0).getCoordinate().getX() > 500 ||
//                            edges.get(i).get(1).getCoordinate().getX() > 50 ||
//                            edges.get(q).get(0).getCoordinate().getX() > 550 ||
//                            edges.get(q).get(1).getCoordinate().getX() > 550 ||
//                            edges.get(i).get(0).getCoordinate().getY() > 550 ||
//                            edges.get(i).get(1).getCoordinate().getY() > 550 ||
//                            edges.get(q).get(0).getCoordinate().getY() > 550 ||
//                            edges.get(q).get(1).getCoordinate().getY() > 550
//                    )

                    if (IsLinePartsIntersected(
                            edges.get(i).get(0).getCoordinate(),
                            edges.get(i).get(1).getCoordinate(),
                            edges.get(q).get(0).getCoordinate(),
                            edges.get(q).get(1).getCoordinate()
                    )) {
                        return false;
                    }
                }
//                if (//0==0
//                        edges.get(i)!=edges.get(q)
//                        &&
//                        edges.get(i).get(0)!=edges.get(i).get(1)
//                        &&
//                        edges.get(i).get(0)!=edges.get(q).get(0)
//                        &&
//                        edges.get(i).get(0)!=edges.get(q).get(1)
//                        &&
//                        edges.get(i).get(1)!=edges.get(q).get(0)
//                        &&
//                        edges.get(i).get(1)!=edges.get(q).get(1)
//                        &&
//                        edges.get(q).get(0)!=edges.get(q).get(1)
//                        ){
//                    double x3 = edges.get(q).get(0).getCoordinate().getX();
//                    double y3 = edges.get(q).get(0).getCoordinate().getY();
//                    double x4 = edges.get(q).get(1).getCoordinate().getX();
//                    double y4 = edges.get(q).get(1).getCoordinate().getY();
//
//
//
//                    Double x = ((x1*y2-x2*y1)*(x4-x3)-(x3*y4-x4*y3)*(x2-x1))/((y1-y2)*(x4-x3)-(y3-y4)*(x2-x1));
//                    Double y = ((y3-y4)*x-(x3*y4-x4*y3))/(x4-x3);
//
//                    if (x1<0 || y1<0 || x2<0 || y2<0 || x3<0 || y3<0 || x4<0 || y4<0){
//                        return false;
//                    }
//
//                    double A1 = y2 - y1;
//                    double B1 = x1 - x2;
//                    double C1 = - A1 * x1 - B1 * y1;
//
//                    double A2 = y4 - y3;
//                    double B2 = x3 - x4;
//                    double C2 = -A2 * x3 - B2 * y3;
//
//                    double f1 = A1 * x3 + B1 * y3 + C1;
//                    double f2 = A1 * x4 + B1 * y4 + C1;
//                    double f3 = A2 * x1 + B2 * y1 + C2;
//                    double f4 = A2 * x2 + B2 * y2 + C2;
//
//                    boolean intersect = (f1 * f2 < 0 && f3 * f4 < 0);
//
//                    return intersect;
//
////                    if (x2<x1 && y2<y1){
////                        double xC = x1;
////                        x1 = x2;
////                        x2 = xC;
////                        double yC = y1;
////                        y1 = y2;
////                        y2 = yC;
////                    }
////
////                    if (x4<x3 && y4<y3){
////                        double xC = x3;
////                        x3 = x4;
////                        x4 = xC;
////                        double yC = y3;
////                        y3 = y4;
////                        y4 = yC;
////                    }
//
//
////                    if ((((x1<x)&&(x2>x)&&(x3<x)&&(x4>x)) && ((y1<y)&&(y2>y) &&(y3<y)&&(y4>y))) && !x.isNaN() && !y.isNaN()){
//////                        System.out.println("*****");
//////                        System.out.println("x "+x);
//////                            System.out.println("y "+y);
//////                            System.out.println("x1 "+x1);
//////                            System.out.println("y1 "+y1);
//////                            System.out.println("x2 "+x2);
//////                            System.out.println("y2 "+y2);
//////                            System.out.println("x3 "+x3);
//////                            System.out.println("y3 "+y3);
//////                            System.privateout.println("x4 "+x4);
//////                            System.out.println("y4 "+y4);
//////                        System.out.println("*****");
//////                            Line line1 = new Line(x1, y1, x2, y2);
//////                            Line line2 = new Line(x3, y3, x4, y4);
//////                            line1.setStroke(Color.RED);
//////                            line1.setOpacity(0.5);
//////                            line1.setStrokeWidth(3);
//////                            line2.setStroke(Color.GREEN);
//////                            line2.setOpacity(0.5);
//////                            line2.setStrokeWidth(3);
//////                            group.getChildren().add(line1);
//////                            group.getChildren().add(line2);
//////                            System.out.println(false);
//////                            return group;
////                        return false;
////                    }
              //  }
            }
        }
        return true;
    }
    private boolean IsLinePartsIntersected(Coordinate a, Coordinate b, Coordinate c, Coordinate d) {
        double common = (b.getX() - a.getX())*(d.getY() - c.getY()) - (b.getY() - a.getY())*(d.getX() - c.getX());

        if (common == 0) return false;

        double rH = (a.getY() - c.getY())*(d.getX() - c.getX()) - (a.getX() - c.getX())*(d.getY() - c.getY());
        double sH = (a.getY() - c.getY())*(b.getX() - a.getX()) - (a.getX() - c.getX())*(b.getY() - a.getY());

        double r = rH / common;
        double s = sH / common;

        if (r >= 0 && r <= 1 && s >= 0 && s <= 1)
            return true;
        else
            return false;
    }
}
