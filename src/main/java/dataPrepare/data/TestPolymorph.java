package dataPrepare.data;

import dataPrepare.data.graph.Coordinate;
import dataPrepare.data.graph.Graph;
import dataPrepare.data.graph.Host;
import dataPrepare.data.triangulation.TriangleVoronoiImpl;
import dataPrepare.data.voronoi.Voronoi;
import dataPrepare.methods.ConvexHull;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

import java.util.*;

/**
 * Created by guardeec on 11.07.16.
 */
public class TestPolymorph {

    Map<dataPrepare.data.voronoi.Polygon, Float> sizes;
    Map<dataPrepare.data.voronoi.Polygon, Float> previosTargetF;

    public TestPolymorph(Map<dataPrepare.data.voronoi.Polygon, Float> sizes) {
        this.sizes = sizes;
    }

    public List<Node> testDraw(Voronoi voronoi, List<Node> nodes){


        previosTargetF=new HashMap<>();
        for (dataPrepare.data.voronoi.Polygon polygon : voronoi.getPolygons()){
            previosTargetF.put(polygon, Math.abs(polygon.getArea()-sizes.get(polygon)));
            System.out.println("area: "+polygon+" "+polygon.getArea());
        }
        List<Coordinate> dots = voronoi.getDots();
        List<Coordinate> convexHull = getConvegHullOfVoronoi(voronoi);

        for (Coordinate coordinate : dots){
            //if (!convexHull.contains(coordinate)){
            List<Decision> decisionsForThisDot = new LinkedList<>();
            for (float i=0; i<360; i+=10){
                dotMovement(coordinate, i, 1);
                Float target = getTargetF(voronoi, coordinate);
                if (target!=null){
                    Decision decision = new Decision();
                    decision.coordinate=coordinate;
                    decision.angle=i;
                    decision.distance=1;
                    decision.target=target;
                    decisionsForThisDot.add(decision);
                }
                dotMovement(coordinate, i, -1);
            }
            decisionsForThisDot.sort(new Comparator<Decision>() {
                @Override
                public int compare(Decision o1, Decision o2) {
                    if (o1.target<o2.target){
                        return -1;
                    }else {
                        return 0;
                    }
                }
            });
            if (decisionsForThisDot.size()>0){
                dotMovement(coordinate, decisionsForThisDot.get(0).angle, decisionsForThisDot.get(0).distance);
            }//}


        }

        List<Node> nodes1 = new LinkedList<>();
        for (dataPrepare.data.voronoi.Polygon cell : voronoi.getPolygons()){
            Polygon polygon = new Polygon();
            for (Coordinate cellCoordinates : cell.getPoints()){
                Double[] points = new Double[2];
                points[0] = (double) cellCoordinates.getX();
                points[1] = (double) cellCoordinates.getY();
                polygon.getPoints().addAll(points);
                polygon.setOpacity(0.3);

                if (convexHull.contains(cellCoordinates)){
                    Circle circle = new Circle(cellCoordinates.getX(), cellCoordinates.getY(), 10);
                    circle.setFill(Color.RED);
                    nodes1.add(circle);
                }
                for (int i=0; i<convexHull.size(); i++){
                    Line line = new Line();
                    line.setStartX(convexHull.get(i).getX());
                    line.setStartY(convexHull.get(i).getY());
                    line.setEndX(convexHull.get((i+1)%convexHull.size()).getX());
                    line.setEndY(convexHull.get((i+1)%convexHull.size()).getY());
                    line.setStroke(Color.GREEN);
                    line.setStrokeWidth(13);
                    nodes1.add(line);
                }

            }

            if (ConvexHull.get(voronoi.getGraph()).contains(cell.getHost())){
                polygon.setFill(Color.RED);
            }
            nodes1.add(polygon);
        }
        System.out.println(getTargetF(voronoi));
        return nodes1;
    }

    private List<Coordinate> getConvegHullOfVoronoi(Voronoi voronoi){
        List<Coordinate> convexHull = ConvexHull.getFromCoordinates(voronoi.getDots());

        List<List<Coordinate>> edgesOfConvexHull = new LinkedList<>();
        for (int i=0;i<convexHull.size(); i++){
            List<Coordinate> edge = new LinkedList<>();
            edge.add(convexHull.get(i));
            edge.add(convexHull.get((i+1)%convexHull.size()));
            edgesOfConvexHull.add(edge);
        }

        List<List<Coordinate>> edgesOfVoronoi = new LinkedList<>();
        for (List<Host> edge : voronoi.voronoiLikeAGraph(voronoi).getEdges()){
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
            Dejkstra dejkstra = new Dejkstra(voronoi.getDots(), edgesOfVoronoi);
            dejkstra.execute(edge.get(0));
            List<Coordinate> path = dejkstra.getPath(edge.get(1), voronoi.getDots());

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


    private void dotMovement(Coordinate coordinate, float angle, float distance){
        double angleR = Math.toRadians((double) angle);
        double x1 = coordinate.getX();
        double y1 = coordinate.getY();
        coordinate.setX((float) (x1+distance*Math.cos(angleR)));
        coordinate.setY((float) (y1+distance*Math.sin(angleR)));
    }

    private class Decision {
        public Coordinate coordinate;
        public float angle;
        public float distance;
        public float target;
    }

    private Float getTargetF(Voronoi voronoi){
        if (!voronoi.voronoiLikeAGraph(voronoi).checkOnPlanar()){
            return null;
        }else {
            float target = 0;
            for (dataPrepare.data.voronoi.Polygon polygon : voronoi.getPolygons()){
                target+=Math.abs(polygon.getArea()-sizes.get(polygon));
            }
            return target;
        }
    }

    private Float getTargetF(Voronoi voronoi, Coordinate coordinate){
        if (!voronoi.voronoiLikeAGraph(voronoi).checkOnPlanar()){
            return null;
        }else {
            float target = 0;
            for (dataPrepare.data.voronoi.Polygon polygon : voronoi.getPolygons()){
                if (polygon.getPoints().contains(coordinate)){
                    if (previosTargetF.get(polygon)< Math.abs(polygon.getArea()-sizes.get(polygon))){
                        return null;
                    }
                    target+=Math.abs(polygon.getArea()-sizes.get(polygon));
                }
            }
            return target;
        }
    }




}
