package dataPrepare.methods;

import com.google.common.collect.Lists;
import dataPrepare.data.TestPolymorph;
import dataPrepare.data.graph.Coordinate;
import dataPrepare.data.voronoi.Polygon;
import dataPrepare.data.voronoi.Voronoi;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by guardeec on 11.10.16.
 */
public class AddExternalPoints {

    public void addOnePointPerEdge(Polygon polygon, Voronoi voronoi){
        Coordinate startPoint = null;
        for (int i=0; i<polygon.getPoints().size(); i++){
            if (
                    (boolean)polygon.getPoints().get(i).getMetric("stopPolymorph")
                            &&
                            !(boolean)polygon.getPoints().get((i+1)%polygon.getPoints().size()).getMetric("stopPolymorph")
                    ){
                startPoint = polygon.getPoints().get(i);
                break;
            }
        }
        List<Edge> edges = new LinkedList<>();
        if (startPoint==null){
            for (int i=0; i<polygon.getPoints().size(); i++){
                Edge edge = new Edge(
                        polygon.getPoints().get(i),
                        polygon.getPoints().get((i+1)%polygon.getPoints().size())
                );
                edges.add(edge);
            }
        }else {
            for (int i=polygon.getPoints().indexOf(startPoint);; i++){
                Edge edge = new Edge(
                        polygon.getPoints().get(i%polygon.getPoints().size()),
                        polygon.getPoints().get((i+1)%polygon.getPoints().size())
                );
                edges.add(edge);
                if ((boolean)polygon.getPoints().get((i+1)%polygon.getPoints().size()).getMetric("stopPolymorph")){
                    break;
                }
            }
        }

        for (Edge edge : edges){
            Coordinate coordinate = new Coordinate(
                    (edge.getCoordinate1().getX()+edge.getCoordinate2().getX())/2,
                    (edge.getCoordinate1().getY()+edge.getCoordinate2().getY())/2
            );
            coordinate.addMetric("stopPolymorph", false);
            coordinate.addMetric("BLUE", false);

            voronoi.getDots().add(coordinate);

            for (Polygon polygonToSeparate : voronoi.getPolygons()){
                for (int i=0; i<polygonToSeparate.getPoints().size(); i++){
                    if (polygonToSeparate.getPoints().get(i)==edge.coordinate1 && polygonToSeparate.getPoints().get((i+1)%polygonToSeparate.getPoints().size())==edge.coordinate2){
                        polygonToSeparate.getPoints().add(i+1, coordinate);
                    }else {
                        if (polygonToSeparate.getPoints().get(i)==edge.coordinate2 && polygonToSeparate.getPoints().get((i+1)%polygonToSeparate.getPoints().size())==edge.coordinate1){
                            polygonToSeparate.getPoints().add(i+1, coordinate);
                        }
                    }
                }
            }
        }


    }

    public void addPointsWhilePolymorph(Polygon polygon, Voronoi voronoi, float padding){
        Coordinate startPoint = null;
        for (int i=0; i<polygon.getPoints().size(); i++){
            if (
                (boolean)polygon.getPoints().get(i).getMetric("stopPolymorph")
                &&
                !(boolean)polygon.getPoints().get((i+1)%polygon.getPoints().size()).getMetric("stopPolymorph")
            ){
                startPoint = polygon.getPoints().get(i);
                break;
            }
        }
        List<Edge> edges = new LinkedList<>();
        if (startPoint==null){
            for (int i=0; i<polygon.getPoints().size(); i++){
                Edge edge = new Edge(
                        polygon.getPoints().get(i),
                        polygon.getPoints().get((i+1)%polygon.getPoints().size())
                );
                edges.add(edge);
            }
        }else {
            for (int i=polygon.getPoints().indexOf(startPoint);; i++){
                Edge edge = new Edge(
                        polygon.getPoints().get(i%polygon.getPoints().size()),
                        polygon.getPoints().get((i+1)%polygon.getPoints().size())
                );
                edges.add(edge);
                if ((boolean)polygon.getPoints().get((i+1)%polygon.getPoints().size()).getMetric("stopPolymorph")){
                    break;
                }
            }
        }

        float curveDistance=0;
        for (Edge edge : edges){
            curveDistance+=distanceBetweenCoordinates(edge.getCoordinate1(), edge.getCoordinate2());
        }
        int numberOfDots = edges.size()+1;
        int numberOfDotsThatMustInsert = Math.round((Math.round(curveDistance/padding)-numberOfDots)/(numberOfDots-1));



        for (Edge edge : edges){
            List<Coordinate> newDots = new LinkedList<>();
            for (int i=0; i<numberOfDotsThatMustInsert-1; i++){
                Coordinate coordinate = new Coordinate(edge.getCoordinate1().getX(), edge.getCoordinate1().getY());
                coordinate.addMetric("stopPolymorph", false);
                coordinate.addMetric("BLUE", false);
                Coordinate zero = new Coordinate(edge.getCoordinate1().getX(), 0);
                float distance = distanceBetweenCoordinates(edge.getCoordinate1(), edge.getCoordinate2())/(numberOfDotsThatMustInsert);
                float angle = (getAngle(zero, edge.getCoordinate1(), edge.getCoordinate2())-90+360)%360;
                if (edge.getCoordinate1().getX()>edge.getCoordinate2().getX()){
                    angle=(360-angle+360+180)%360;
                }

                dotMovement(coordinate, angle, distance*(i+1));

                voronoi.getDots().add(coordinate);
                newDots.add(coordinate);
//                for (Polygon polygonOfVoronoi : voronoi.getPolygons()){
//                    if (polygonOfVoronoi.getPoints().contains(edge.coordinate1)&&polygonOfVoronoi.getPoints().contains(edge.getCoordinate2())){
//                        int index1 = polygonOfVoronoi.getPoints().indexOf(edge.coordinate1);
//                        int index2 = polygonOfVoronoi.getPoints().indexOf(edge.coordinate2);
//                        int numberOfPolygonDots = polygon.getPoints().size();
//                        System.out.println(index1+" "+index2);
//                        polygonOfVoronoi.getPoints().add((index2+numberOfPolygonDots-i-1)%polygonOfVoronoi.getPoints().size(), coordinate);
//                    }
//                    for (int q=0; q<polygonOfVoronoi.getPoints().size(); q++){
//                        System.out.println(polygon.getPoints().size());
//                        if (polygonOfVoronoi.getPoints().get(q)==edge.coordinate1 && polygonOfVoronoi.getPoints().get((q+1+i)%polygonOfVoronoi.getPoints().size())==edge.coordinate2){
//                            polygonOfVoronoi.getPoints().add((q+i+1)%polygonOfVoronoi.getPoints().size(), coordinate);
//                            break;
//                        }else {
//                            if (polygonOfVoronoi.getPoints().get(q)==edge.coordinate2 && polygonOfVoronoi.getPoints().get((q+1+i)%polygonOfVoronoi.getPoints().size())==edge.coordinate1){
//                                polygonOfVoronoi.getPoints().add((q+i+1)%polygonOfVoronoi.getPoints().size(), coordinate);
//                                break;
//                            }
//                        }
//                    }
                //}
            }
            for (Polygon polygonOfVoronoi : voronoi.getPolygons()){
                if (polygonOfVoronoi.getPoints().contains(edge.coordinate1)&&polygonOfVoronoi.getPoints().contains(edge.getCoordinate2())){
                    int index1 = polygonOfVoronoi.getPoints().indexOf(edge.coordinate1);
                    int index2 = polygonOfVoronoi.getPoints().indexOf(edge.coordinate2);
                    if ((index1+1)%polygonOfVoronoi.getPoints().size()==index2){
                        for (int i=0; i<newDots.size();i++){
                            polygonOfVoronoi.getPoints().add((index2+i+polygonOfVoronoi.getPoints().size())%polygonOfVoronoi.getPoints().size(), newDots.get(i));
                        }
                    }else {

                        for (int i=0; i<newDots.size();i++){
                            List<Coordinate> revercedDots = Lists.reverse(newDots);
                            polygonOfVoronoi.getPoints().add((index1+i+polygonOfVoronoi.getPoints().size())%polygonOfVoronoi.getPoints().size(), revercedDots.get(i));
                        }
                    }
                 }
            }
        }

//        for (Edge edge : edges){
//            Coordinate coordinate = new Coordinate(
//                    (edge.getCoordinate1().getX()+edge.getCoordinate2().getX())/2,
//                    (edge.getCoordinate1().getY()+edge.getCoordinate2().getY())/2
//            );
//            coordinate.addMetric("stopPolymorph", false);
//            voronoi.getDots().add(coordinate);
//            for (Polygon polygonOfVoronoi : voronoi.getPolygons()){
//                for (int i=0; i<polygonOfVoronoi.getPoints().size(); i++){
//                    if (polygonOfVoronoi.getPoints().get(i)==edge.coordinate1 && polygonOfVoronoi.getPoints().get((i+1)%polygonOfVoronoi.getPoints().size())==edge.coordinate2){
//                        polygonOfVoronoi.getPoints().add(i+1, coordinate);
//                    }else {
//                        if (polygonOfVoronoi.getPoints().get(i)==edge.coordinate2 && polygonOfVoronoi.getPoints().get((i+1)%polygonOfVoronoi.getPoints().size())==edge.coordinate1){
//                            polygonOfVoronoi.getPoints().add(i+1, coordinate);
//                        }
//                    }
//                }
//            }
//        }
    }

    private void dotMovement(Coordinate coordinate, float angle, float distance){
        double angleR = Math.toRadians((double) angle);
        double x1 = coordinate.getX();
        double y1 = coordinate.getY();
        coordinate.setX((float) (x1+distance*Math.cos(angleR)));
        coordinate.setY((float) (y1+distance*Math.sin(angleR)));
    }

    private float distanceBetweenCoordinates(Coordinate coordinate1, Coordinate coordinate2){
        return (float) Math.sqrt( Math.pow(coordinate1.getX()-coordinate2.getX(), 2) + Math.pow(coordinate1.getY()-coordinate2.getY(), 2));
    }

    private float getAngle(Coordinate a, Coordinate b, Coordinate c){
        double x1 = a.getX() - b.getX(), x2 = c.getX() - b.getX();
        double y1 = a.getY() - b.getY(), y2 = c.getY() - b.getY();
        double d1 = Math.sqrt (x1 * x1 + y1 * y1);
        double d2 = Math.sqrt (x2 * x2 + y2 * y2);
        return (float) Math.toDegrees(Math.acos ((x1 * x2 + y1 * y2) / (d1 * d2)));
    }

    public void add(List<Coordinate> ring, Voronoi voronoi){
        List<Edge> edges = new LinkedList<>();
        for (int i=0; i<ring.size(); i++){
            edges.add(new Edge(
                ring.get(i),
                ring.get((i+1)%ring.size())
            )
            );
        }
        for (Edge edge : edges){
            Coordinate coordinate = new Coordinate(
                    (edge.getCoordinate1().getX()+edge.getCoordinate2().getX())/2,
                    (edge.getCoordinate1().getY()+edge.getCoordinate2().getY())/2
            );
            voronoi.getDots().add(coordinate);

            for (Polygon polygon : voronoi.getPolygons()){
                for (int i=0; i<polygon.getPoints().size(); i++){
                    if (polygon.getPoints().get(i)==edge.coordinate1 && polygon.getPoints().get((i+1)%polygon.getPoints().size())==edge.coordinate2){
                        polygon.getPoints().add(i+1, coordinate);
                    }else {
                        if (polygon.getPoints().get(i)==edge.coordinate2 && polygon.getPoints().get((i+1)%polygon.getPoints().size())==edge.coordinate1){
                            polygon.getPoints().add(i+1, coordinate);
                        }
                    }
                }
            }
        }
    }


    public void add(Voronoi voronoi){
        List<Edge> edges = new LinkedList<>();
        for (Polygon polygon : voronoi.getPolygons()){
            if (polygon.getPoints().size()<4){
                for (int i=0; i<polygon.getPoints().size(); i++){
                    Edge edge = new Edge(
                            polygon.getPoints().get(i),
                            polygon.getPoints().get((i+1)%polygon.getPoints().size())
                    );
                    if (checkEdge(edge, edges)){
                        edges.add(edge);
                    }
                }
            }
        }
        for (Edge edge : edges){
            Coordinate coordinate = new Coordinate(
                    (edge.getCoordinate1().getX()+edge.getCoordinate2().getX())/2,
                    (edge.getCoordinate1().getY()+edge.getCoordinate2().getY())/2
            );
            voronoi.getDots().add(coordinate);

            for (Polygon polygon : voronoi.getPolygons()){
                for (int i=0; i<polygon.getPoints().size(); i++){
                    if (polygon.getPoints().get(i)==edge.coordinate1 && polygon.getPoints().get((i+1)%polygon.getPoints().size())==edge.coordinate2){
                        polygon.getPoints().add(i+1, coordinate);
                    }else {
                        if (polygon.getPoints().get(i)==edge.coordinate2 && polygon.getPoints().get((i+1)%polygon.getPoints().size())==edge.coordinate1){
                            polygon.getPoints().add(i+1, coordinate);
                        }
                    }
                }
            }
        }
    }

    private boolean checkEdge(Edge edge, List<Edge> edges){
        for (Edge edge1 : edges){
            if (
                    (edge1.getCoordinate1()==edge.getCoordinate1() && edge1.getCoordinate2()==edge.getCoordinate2())
                            ||
                            (edge1.getCoordinate2()==edge.getCoordinate1() && edge1.getCoordinate1()==edge.getCoordinate2())
                    ){
                return false;
            }
        }
        return true;
    }

    private class Edge{
        Coordinate coordinate1;
        Coordinate coordinate2;

        public Edge(Coordinate coordinate1, Coordinate coordinate2) {
            this.coordinate1 = coordinate1;
            this.coordinate2 = coordinate2;
        }

        public Coordinate getCoordinate1() {
            return coordinate1;
        }

        public Coordinate getCoordinate2() {
            return coordinate2;
        }
    }
}
