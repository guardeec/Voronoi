package dataPrepare.data;

import dataPrepare.voronoi.ConvexHull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by anna on 02.09.15.
 */
public class Polygon {

    //координаты точек полигона
    List<Coordinate> points = new ArrayList<Coordinate>();

    //ссылка на хост к которому принадлежит данный полигон
    Host host;

    //хосты с которыми данный хост имеет связь
    int[] relations;

    //тип полигона - полигон с хостом или полигон аттрактор
    boolean status = false;

    /*
    обычные геттеры и сеттеры
     */
    public boolean isStatus() {
        return status;
    }
    public int[] getRelations() {
        return relations;
    }
    public void setRelations(int[] relations) {
        this.relations = relations;
    }
    public Host getHost() {
        return host;
    }
    public void setHost(Host host) {
        this.host = host;
        if (host!=null){
            this.status= true;
        }

    }

    /*
    Получение точек/точки полигона
    */
    public List<Coordinate> getPoints() {
        return points;
    }
    public Integer getPoint(Coordinate coordinate){
        for (int i=0; i<points.size(); i++){
            if (points.get(i).getX()==coordinate.getX() && points.get(i).getY()==coordinate.getY()){
                return i;
            }
        }
        return null;
    }
    public Coordinate getPoint(int id){
        return points.get(id);
    }
    public boolean containsPoint(Coordinate coordinate){
        Integer point = getPoint(coordinate);
        if (point!=null){
            return true;
        }else {
            return false;
        }
    }
    public List<Integer> getNearbyPolygons(List<Polygon> voronoi){
        List<Integer> nearbyPolygons = new ArrayList<>();
        for (int i=0; i<voronoi.size(); i++){
            int counter=0;
            for (int q=0; q<voronoi.get(i).points.size(); q++){
                if (voronoi.get(i).points!=points){
                    for (int z=0; z<points.size(); z++){
                        if (voronoi.get(i).points.get(q).contains(points.get(z))){
                            counter++;
                        }
                    }
                }
            }
            if (counter==2){
                nearbyPolygons.add(i);
            }
            counter=0;
        }
        return nearbyPolygons;
    }
    public List<Coordinate> getContainsPoints(Polygon polygon){
        List<Coordinate> coordinates = new ArrayList<>();
        for (int i=0; i<points.size(); i++){
            for (int q=0; q<polygon.getPoints().size(); q++){
                if (points.get(i).getX()==polygon.getPoint(q).getX() && points.get(i).getY()==polygon.getPoint(q).getY()){
                    Coordinate coordinate = new Coordinate(points.get(i).getX(), points.get(i).getY());
                    coordinates.add(coordinate);
                }
            }
        }
        return coordinates;
    }

    /*
    удаление точки полигона
     */
    public void deletePoint(int id){
        points.remove(id);
    }

    /*
    изменение точки полигона
     */
    public void changePoint(int id, Coordinate coordinate){
        points.set(id, coordinate);
    }

    /*
    добавление точки полигона
     */
    public void setPoints(float x, float y) {
        points.add(new Coordinate(x, y));
    }

    /*
    построение планарного полигона
    по идее - нужно выполнять после каждой операции,
    но необходимо знать вокруг чего строить сортировку
     */
    public void sort(float x, float y){
        List<Coordinate> sortedPoints = new ArrayList<>();
        //считаем градус от каждой точки
        List<Double> degs = new ArrayList<>();
        for (int i=0; i<points.size(); i++){
            double x1 = x, y1 = y;
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

    /*
    построение планарного полигона вокруг хоста
     */
    public void sortArowndHost(){
        if(host!=null){
            sort(host.getX(), host.getY());
        }
    }

}
