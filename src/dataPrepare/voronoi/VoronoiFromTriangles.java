package dataPrepare.voronoi;

import dataPrepare.data.Coordinate;
import dataPrepare.data.Graph;
import dataPrepare.data.Polygon;
import dataPrepare.data.Triangle;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by anna on 02.09.15.
 */
public class VoronoiFromTriangles {

    //степень расширения внешних граней прямоугольника
    static final float scaleLendth = 1;

    /*
    помещение вороной в прямоугольник
    в фазе 1, внешние полигоны растягиваются до граней прямоугльника
     */
    private static Polygon setPolygonInCubeFase1(ArrayList<Integer> hull, Graph graph, Polygon polygon, int hostId){

        //определим 4 крайние точки
        int leftDot=hull.get(0);
        int rightDot=hull.get(0);
        int downDot=hull.get(0);
        int upDot=hull.get(0);
        for (int i=0; i<hull.size(); i++){
            if(graph.getHost(leftDot).getX()> graph.getHost(hull.get(i)).getX()){
                leftDot=hull.get(i);
            }
            if(graph.getHost(rightDot).getX()<graph.getHost(hull.get(i)).getX()){
                rightDot=hull.get(i);
            }
            if(graph.getHost(downDot).getY()>graph.getHost(hull.get(i)).getY()){
                downDot=hull.get(i);
            }
            if(graph.getHost(upDot).getY()<graph.getHost(hull.get(i)).getY()){
                upDot=hull.get(i);
            }
        }

        //из крайних точек определим координаты сторон четырёхугольника в который будем вписывать вороной
        Coordinate leftUpCorner = new Coordinate(   graph.getHost(leftDot).getX(),
                                                    graph.getHost(upDot).getY()
        );
        Coordinate leftDownCorner = new Coordinate( graph.getHost(leftDot).getX(),
                                                    graph.getHost(downDot).getY()
        );
        Coordinate rightUpCorner = new Coordinate(  graph.getHost(rightDot).getX(),
                                                    graph.getHost(upDot).getY()
        );
        Coordinate rightDownCorner = new Coordinate(graph.getHost(rightDot).getX(),
                                                    graph.getHost(downDot).getY()
        );

        //найдём какая сторона четырёхугольника лежит ближе всего к хосту
        double lendthToLeftSide = graph.getHost(hostId).getX()-leftUpCorner.getX();
        double lendthToRightSide = rightUpCorner.getX()-graph.getHost(hostId).getX();
        double lendthToUpSide = leftUpCorner.getY()-graph.getHost(hostId).getY();
        double lendthToDownSide = graph.getHost(hostId).getY()-rightDownCorner.getY();
        double[] lendth = {lendthToLeftSide, lendthToRightSide, lendthToUpSide, lendthToDownSide};
        int closestLine=0;
        for (int i=0; i<4; i++){
            if (lendth[closestLine]>lendth[i]){
                closestLine=i;
            }
        }

        //создаём новую точку на стороне четырёхугольника которая перпендикулярна хосту
        Coordinate dotOnSide=null;
        switch (closestLine){
            case 0: dotOnSide = new Coordinate( leftDownCorner.getX()-scaleLendth,
                                                graph.getHost(hostId).getY()
                                );
                    break;
            case 1: dotOnSide = new Coordinate( rightDownCorner.getX()+scaleLendth,
                                                graph.getHost(hostId).getY()
                                );
                    break;
            case 2: dotOnSide = new Coordinate( graph.getHost(hostId).getX(),
                                                leftUpCorner.getY()+scaleLendth
                                );
                    break;
            case 3: dotOnSide = new Coordinate( graph.getHost(hostId).getX(),
                                                leftDownCorner.getY()-scaleLendth
                                );
                    break;
        }
        polygon.setPoints(dotOnSide.getX(), dotOnSide.getY());

        //сформируем полигон: последовательность точек
        polygon.sort(graph.getHost(hostId).getX(), graph.getHost(hostId).getY());

        //найдём точки которые необходимо вытянуть на прямоугольник
        rightDot=0;
        leftDot=0;
        upDot=0;
        downDot=0;
        for (int i=0; i<polygon.getPoints().size(); i++){
            if (polygon.getPoint(rightDot).getX()<polygon.getPoint(i).getX()){
                rightDot=i;
            }
            if (polygon.getPoint(leftDot).getX()>polygon.getPoint(i).getX()){
                leftDot=i;
            }
            if (polygon.getPoint(upDot).getY()<polygon.getPoint(i).getY()){
                upDot=i;
            }
            if (polygon.getPoint(downDot).getY()>polygon.getPoint(i).getY()){
                downDot=i;
            }
        }

        //вытянем эти точки
        switch (closestLine){
            case 0: polygon.setPoints(dotOnSide.getX(), polygon.getPoint(downDot).getY());
                polygon.setPoints(dotOnSide.getX(),polygon.getPoint(upDot).getY());
                break;
            case 1: polygon.setPoints(dotOnSide.getX(), polygon.getPoint(downDot).getY());
                polygon.setPoints(dotOnSide.getX(),polygon.getPoint(upDot).getY());
                break;
            case 2: polygon.setPoints(polygon.getPoint(leftDot).getX(),dotOnSide.getY());
                polygon.setPoints(polygon.getPoint(rightDot).getX(),dotOnSide.getY());
                break;
            case 3: polygon.setPoints(polygon.getPoint(rightDot).getX(),dotOnSide.getY());
                polygon.setPoints(polygon.getPoint(leftDot).getX(),dotOnSide.getY());
                break;
        }
        polygon.deletePoint(polygon.getPoint(dotOnSide));

        return polygon;
    }

    /*
    помещение вороной в прямоугольник
    в фазе 2, полигоны вокруг углов стягиваются, окончательно заполняя всё прсотранство прямоугольника
     */
    private static List<Polygon> setPolygonInCubeFase2(List<Polygon> voronoi, ArrayList<Integer> hull, Graph graph){

        //определим 4 крайние точки
        int leftDot=hull.get(0);
        int rightDot=hull.get(0);
        int downDot=hull.get(0);
        int upDot=hull.get(0);
        for (int i=0; i<hull.size(); i++){
            if(graph.getHost(leftDot).getX()> graph.getHost(hull.get(i)).getX()){
                leftDot=hull.get(i);
            }
            if(graph.getHost(rightDot).getX()<graph.getHost(hull.get(i)).getX()){
                rightDot=hull.get(i);
            }
            if(graph.getHost(downDot).getY()>graph.getHost(hull.get(i)).getY()){
                downDot=hull.get(i);
            }
            if(graph.getHost(upDot).getY()<graph.getHost(hull.get(i)).getY()){
                upDot=hull.get(i);
            }
        }

        //из крайних точек определим координаты сторон четырёхугольника в который будем вписывать вороной
        Coordinate leftDownCornerCoordinate = new Coordinate(   graph.getHost(leftDot).getX()-scaleLendth,
                graph.getHost(upDot).getY()+scaleLendth
        );
        Coordinate leftUpCornerCoordinate = new Coordinate( graph.getHost(leftDot).getX()-scaleLendth,
                graph.getHost(downDot).getY()-scaleLendth
        );
        Coordinate rightDownCornerCoordinate = new Coordinate(  graph.getHost(rightDot).getX()+scaleLendth,
                graph.getHost(upDot).getY()+scaleLendth
        );
        Coordinate rightUpCornerCoordinate = new Coordinate(graph.getHost(rightDot).getX()+scaleLendth,
                graph.getHost(downDot).getY()-scaleLendth
        );

        //найдём точки которые лежат на прямоугольнике
        ArrayList<Integer[]> dotsOnLeftFr = new ArrayList<>();
        ArrayList<Integer[]> dotsOnRightFr = new ArrayList<>();
        ArrayList<Integer[]> dotsOnDownFr = new ArrayList<>();
        ArrayList<Integer[]> dotsOnUpFr = new ArrayList<>();
        for (int i=0; i<voronoi.size(); i++){
            for (int q=0; q<voronoi.get(i).getPoints().size(); q++){
                if (    voronoi.get(i).getPoint(q).getX()==leftDownCornerCoordinate.getX()){
                    Integer[] polygonAndDot = {i,q};
                    dotsOnLeftFr.add(polygonAndDot);
                }
                if (    voronoi.get(i).getPoint(q).getX()==rightDownCornerCoordinate.getX()){
                    Integer[] polygonAndDot = {i,q};
                    dotsOnRightFr.add(polygonAndDot);
                }
                if (    voronoi.get(i).getPoint(q).getY()==rightDownCornerCoordinate.getY()){
                    Integer[] polygonAndDot = {i,q};
                    dotsOnDownFr.add(polygonAndDot);
                }
                if (    voronoi.get(i).getPoint(q).getY()==rightUpCornerCoordinate.getY()){
                    Integer[] polygonAndDot = {i,q};
                    dotsOnUpFr.add(polygonAndDot);
                }
            }
        }

        //заполним левый нижний угол
        int[] upDot_2_leftDownCorner={0,0};
        float lendth = leftDownCornerCoordinate.getY()+rightDownCornerCoordinate.getY();
        for (int i=0; i<dotsOnLeftFr.size(); i++){
            Integer[] polygonAndDot = dotsOnLeftFr.get(i);
            float lendthToDot=leftDownCornerCoordinate.getY()-voronoi.get(polygonAndDot[0]).getPoint(polygonAndDot[1]).getY();
            if(lendth>lendthToDot){
                lendth=lendthToDot;
                upDot_2_leftDownCorner[0]=polygonAndDot[0];
                upDot_2_leftDownCorner[1]=polygonAndDot[1];
            }
        }
        voronoi.get(upDot_2_leftDownCorner[0]).changePoint(upDot_2_leftDownCorner[1], leftDownCornerCoordinate);
        int[] rightDot_2_leftDownCorner={0,0};
        lendth = leftDownCornerCoordinate.getY()+rightDownCornerCoordinate.getY();
        for (int i=0; i<dotsOnDownFr.size(); i++){
            Integer[] polygonAndDot = dotsOnDownFr.get(i);
            float lendthToDot= voronoi.get(polygonAndDot[0]).getPoint(polygonAndDot[1]).getX()-leftDownCornerCoordinate.getX();

            if(lendth>lendthToDot){
                lendth=lendthToDot;
                rightDot_2_leftDownCorner[0]=polygonAndDot[0];
                rightDot_2_leftDownCorner[1]=polygonAndDot[1];
            }
        }
        voronoi.get(rightDot_2_leftDownCorner[0]).changePoint(rightDot_2_leftDownCorner[1], leftDownCornerCoordinate);

        //заполним левый верхний угол
        int[] downDot_2_leftUpCorner={0,0};
        lendth = leftDownCornerCoordinate.getY()+rightDownCornerCoordinate.getY();
        for (int i=0; i<dotsOnLeftFr.size(); i++){
            Integer[] polygonAndDot = dotsOnLeftFr.get(i);
            float lendthToDot=voronoi.get(polygonAndDot[0]).getPoint(polygonAndDot[1]).getY()-leftUpCornerCoordinate.getY();

            if(lendth>lendthToDot){
                lendth=lendthToDot;
                downDot_2_leftUpCorner[0]=polygonAndDot[0];
                downDot_2_leftUpCorner[1]=polygonAndDot[1];
            }
        }
        voronoi.get(downDot_2_leftUpCorner[0]).changePoint(downDot_2_leftUpCorner[1], leftUpCornerCoordinate);
        int[] rightDot_2_leftUpCorner={0,0};
        lendth = leftDownCornerCoordinate.getY()+rightDownCornerCoordinate.getY();
        for (int i=0; i<dotsOnUpFr.size(); i++){
            Integer[] polygonAndDot = dotsOnUpFr.get(i);
            float lendthToDot= voronoi.get(polygonAndDot[0]).getPoint(polygonAndDot[1]).getX()-leftUpCornerCoordinate.getX();

            if(lendth>lendthToDot){
                lendth=lendthToDot;
                rightDot_2_leftUpCorner[0]=polygonAndDot[0];
                rightDot_2_leftUpCorner[1]=polygonAndDot[1];
            }
        }
        voronoi.get(rightDot_2_leftUpCorner[0]).changePoint(rightDot_2_leftUpCorner[1], leftUpCornerCoordinate);

        //заполним правый нижний угол
        int[] upDot_2_RightDownCorner={0,0};
        lendth = leftDownCornerCoordinate.getY()+rightDownCornerCoordinate.getY();
        for (int i=0; i<dotsOnRightFr.size(); i++){
            Integer[] polygonAndDot = dotsOnRightFr.get(i);
            float lendthToDot=rightUpCornerCoordinate.getY()-voronoi.get(polygonAndDot[0]).getPoint(polygonAndDot[1]).getY();
            if(lendth>lendthToDot){
                lendth=lendthToDot;
                upDot_2_RightDownCorner[0]=polygonAndDot[0];
                upDot_2_RightDownCorner[1]=polygonAndDot[1];
            }
        }
        voronoi.get(upDot_2_RightDownCorner[0]).changePoint(upDot_2_RightDownCorner[1], rightDownCornerCoordinate);
        int[] leftDot_2_rightDownCorner={0,0};
        lendth = leftDownCornerCoordinate.getY()+rightDownCornerCoordinate.getY();
        for (int i=0; i<dotsOnDownFr.size(); i++){
            Integer[] polygonAndDot = dotsOnDownFr.get(i);
            float lendthToDot= rightUpCornerCoordinate.getX()-voronoi.get(polygonAndDot[0]).getPoint(polygonAndDot[1]).getX();

            if(lendth>lendthToDot){
                lendth=lendthToDot;
                leftDot_2_rightDownCorner[0]=polygonAndDot[0];
                leftDot_2_rightDownCorner[1]=polygonAndDot[1];
            }
        }
        voronoi.get(leftDot_2_rightDownCorner[0]).changePoint(leftDot_2_rightDownCorner[1], rightDownCornerCoordinate);

        //заполним правый верхний угол
        int[] downDot_2_rightUpCorner={0,0};
        lendth = leftDownCornerCoordinate.getY()+rightDownCornerCoordinate.getY();
        for (int i=0; i<dotsOnRightFr.size(); i++){
            Integer[] polygonAndDot = dotsOnRightFr.get(i);
            float lendthToDot=voronoi.get(polygonAndDot[0]).getPoint(polygonAndDot[1]).getY()-rightUpCornerCoordinate.getY();

            if(lendth>lendthToDot){
                lendth=lendthToDot;
                downDot_2_rightUpCorner[0]=polygonAndDot[0];
                downDot_2_rightUpCorner[1]=polygonAndDot[1];
            }
        }
        voronoi.get(downDot_2_rightUpCorner[0]).changePoint(downDot_2_rightUpCorner[1], rightUpCornerCoordinate);
        int[] leftDot_2_rightUpCorner={0,0};
        lendth = leftDownCornerCoordinate.getY()+rightDownCornerCoordinate.getY();
        for (int i=0; i<dotsOnUpFr.size(); i++){
            Integer[] polygonAndDot = dotsOnUpFr.get(i);
            float lendthToDot= rightUpCornerCoordinate.getX() - voronoi.get(polygonAndDot[0]).getPoint(polygonAndDot[1]).getX();

            if(lendth>lendthToDot){
                lendth=lendthToDot;
                leftDot_2_rightUpCorner[0]=polygonAndDot[0];
                leftDot_2_rightUpCorner[1]=polygonAndDot[1];
            }
        }
        voronoi.get(leftDot_2_rightUpCorner[0]).changePoint(leftDot_2_rightUpCorner[1], rightUpCornerCoordinate);

        return voronoi;
    }

    /*
    Определение и создание границ между полигонами, у которых нет связей
     */
    private static List<Polygon> makeBorders(List<Polygon> voronoi, Graph graph){

        List<List<Coordinate>> borderPolygons = new ArrayList<>();

        for (int i=0; i<voronoi.size(); i++){

            //создадим список всех связей и свписок настоящих свзяей
            List<Integer> nearbyPolygons = voronoi.get(i).getNearbyPolygons(voronoi);
            int[] connectedHosts = graph.getRelations(i);
            /*
            System.out.println(connectedHosts.length);


            int counter2=0;
            for (int q=0; q<graph.getAllRelations().get(i).size(); q++){
                if(graph.getAllRelations().get(i).get(q)){
                    counter2++;
                }
            }
            System.out.println(counter2);

            int number = i+1;
            System.out.print(number + " Polygons: ");
            for (int q=0; q<nearbyPolygons.size(); q++){
                int a = nearbyPolygons.get(q)+1;
                System.out.print(a+" ");
            }
            System.out.println();
            System.out.print(number + " connectedHosts: ");
            for (int q=0; q<connectedHosts.length; q++){
                int a = connectedHosts[q]+1;
                System.out.print(a + " ");
            }
            System.out.println();*/




            //оставим в списке только несуществующие связи
            List<Integer> unconnectedHosts = new ArrayList<>(nearbyPolygons);
            for (int q=0; q<nearbyPolygons.size(); q++){
                for (int z=0; z<connectedHosts.length; z++){
                    if (nearbyPolygons.get(q)==connectedHosts[z]){
                        unconnectedHosts.remove(unconnectedHosts.indexOf(nearbyPolygons.get(q)));
                    }
                }
            }

            /*
            System.out.print(number + " unCunnected: ");
            for (int q=0; q<unconnectedHosts.size(); q++){
                int a = unconnectedHosts.get(q)+1;
                System.out.print(a+" ");
            }
            System.out.println();
            System.out.println();*/


            //найдём общие точки у данного полигона и несвязанного с ним
            for (int q=0; q<unconnectedHosts.size(); q++){
                List<Coordinate> coordinates = voronoi.get(i).getContainsPoints(voronoi.get(unconnectedHosts.get(q)));
                int counter =0;
                for (int z=0; z<borderPolygons.size(); z++){
                    if (
                            (borderPolygons.get(z).get(0).getX()==coordinates.get(0).getX() && borderPolygons.get(z).get(1).getY()==coordinates.get(1).getY())
                            ||
                            (borderPolygons.get(z).get(0).getX()==coordinates.get(1).getX() && borderPolygons.get(z).get(0).getY()==coordinates.get(1).getY())
                            ){
                    }
                }
                borderPolygons.add(coordinates);


            }
        }


        for (int i=0; i<borderPolygons.size(); i++){
            List<Coordinate> coordinates = borderPolygons.get(i);
            Polygon polygon = new Polygon();
            polygon.setPoints(coordinates.get(0).getX(), coordinates.get(0).getY());
            polygon.setPoints(coordinates.get(1).getX(), coordinates.get(1).getY());
            polygon.setHost(null);
            voronoi.add(polygon);

        }

        return voronoi;
    }


    /*
    создание диаграмы вороного
     */
    public static List<Polygon> makeField(ArrayList<Triangle> triangles, Graph plainGraph){

        List<Polygon> voronoi = new ArrayList<Polygon>();
        ArrayList<Integer> hull = ConvexHull.get(plainGraph);

        for (int i=0; i<plainGraph.getHostsNumber(); i++){
            //определяем точки полигона
            Polygon polygon = new Polygon();
            for (int q=0; q<triangles.size(); q++){
                if(i == triangles.get(q).getFirstDot() || i == triangles.get(q).getSecondDot() || i == triangles.get(q).getThirdDot()){
                    polygon.setPoints(
                            triangles.get(q).getCenterCoordinates()[0],
                            triangles.get(q).getCenterCoordinates()[1]
                    );
                }
            }
            //если точка принадлежит выпуклой оболчке, её необходимо вытянуть
            if(hull.contains(i)){
                float x1 = plainGraph.getHost(i).getX();
                float y1 = plainGraph.getHost(i).getY();
                setPolygonInCubeFase1(hull, plainGraph, polygon, i);
            }
            //сортируем точки полигона
            polygon.sort(plainGraph.getHost(i).getX(), plainGraph.getHost(i).getY());
            voronoi.add(polygon);
        }
        //второй этап вытягивания точек выпуклой оболочки
        setPolygonInCubeFase2(voronoi, hull, plainGraph);
        for (int i=0; i<voronoi.size(); i++){
            voronoi.get(i).sort(plainGraph.getHost(i).getX(), plainGraph.getHost(i).getY());
        }

        //перенесение хостов и связей в полигоны
        for (int i=0; i<voronoi.size(); i++){
            voronoi.get(i).setHost(plainGraph.getHost(i));
            voronoi.get(i).setRelations(plainGraph.getRelations(i));

        }
        makeBorders(voronoi,plainGraph);
        return voronoi;
    }

}
