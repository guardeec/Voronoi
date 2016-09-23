package dataPrepare.data.voronoi;

import dataPrepare.data.graph.Coordinate;
import dataPrepare.data.graph.Host;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by anna on 02.09.15.
 */
public class Polygon {

    List<Coordinate> points = new ArrayList<Coordinate>();
    Host host;

    public Polygon(List<Coordinate> points, Host host){
        this.points = points;
        this.host = host;
        sort();
    }

    public Host getHost() {
        return host;
    }
    public List<Coordinate> getPoints() {
        return points;
    }
    public void addPoint(Coordinate coordinate){
        this.points.add(coordinate);
        sort();
    }

    private void sort(){
        /*
        построение планарного полигона
        по идее - нужно выполнять после каждой операции,
        но необходимо знать вокруг чего строить сортировку
         */
        List<Coordinate> sortedPoints = new ArrayList<>();
        //считаем градус от каждой точки
        List<Double> degs = new ArrayList<>();
        for (int i=0; i<points.size(); i++){
            double x1 = host.getCoordinate().getX(), y1 = host.getCoordinate().getY();
            double x2 = points.get(i).getX(), y2 = points.get(i).getY();
            double A = Math.atan2(y1 - y2, x1 - x2) / Math.PI * 180;
            A = (A < 0) ? A + 360 : A;
            degs.add(A);
        }
        //сортируем
        double max = 361.0;
        for (int i=0; i<points.size(); i++){
            //найдём наименьшую точку
            int smallestDot=0;
            for (int q=0; q<degs.size(); q++){
                if(degs.get(smallestDot)>degs.get(q)){
                    smallestDot=q;
                }
            }
            sortedPoints.add(points.get(smallestDot));
            degs.set(smallestDot, max);
        }
        this.points=sortedPoints;
    }

    public float getArea(){
        List<Float> middleResult = new LinkedList<>();
        for (int i=0; i<points.size(); i++){
            middleResult.add(
                            ( points.get(i).getX() * points.get((i+1)%points.size()).getY())
                            -
                            ( points.get(i).getY() * points.get((i+1)%points.size()).getX())
            );
        }
        float square = 0;
        for (Float elem : middleResult){
            square+=elem;
        }
        if (square<0){
            square*=-1;
        }
        square/=2;
        return square;
    }



}
