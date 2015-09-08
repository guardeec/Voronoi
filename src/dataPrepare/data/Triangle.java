package dataPrepare.data;

import java.util.ArrayList;

/**
 * Created by anna on 01.09.15.
 */
public class Triangle {

    /*
    соотношение хост - точка треугольника
     */
    private int firstDot;
    private int secondDot;
    private int thirdDot;

    /*
    координаты точек + центра
     */
    private Coordinate firstDotCoordinate;
    private Coordinate secondDotCoordinate;
    private Coordinate thirdDotCoordinate;
    private Coordinate centerDotCoordinate;

    /*
    конструкторы
    без указания координат и с указанием
     */
    public Triangle() {
    }

    public Triangle(int firstDot, int secondDot, int thirdDot) {
        this.firstDot = firstDot;
        this.secondDot = secondDot;
        this.thirdDot = thirdDot;
    }

    public Triangle(int firstDot, int secondDot, int thirdDot, Graph graph) {
        this.firstDot = firstDot;
        this.secondDot = secondDot;
        this.thirdDot = thirdDot;
        setCoordinates(graph);
    }

    /*
    геттеры и сеттеры для точек, координат и центра
     */
    public int getFirstDot() {
        return firstDot;
    }

    public void setFirstDot(int firstDot) {
        this.firstDot = firstDot;
    }

    public int getSecondDot() {
        return secondDot;
    }

    public void setSecondDot(int secondDot) {
        this.secondDot = secondDot;
    }

    public int getThirdDot() {
        return thirdDot;
    }

    public void setThirdDot(int thirdDot) {
        this.thirdDot = thirdDot;
    }

    public void setCoordinates(Graph graph){
        this.firstDotCoordinate = new Coordinate(    graph.getHost(firstDot).getX(),
                graph.getHost(firstDot).getY()
        );
        this.secondDotCoordinate = new Coordinate(   graph.getHost(secondDot).getX(),
                graph.getHost(secondDot).getY()
        );
        this.thirdDotCoordinate = new Coordinate(    graph.getHost(thirdDot).getX(),
                graph.getHost(thirdDot).getY()
        );
        float centerX = (this.firstDotCoordinate.getX()+this.secondDotCoordinate.getX()+this.thirdDotCoordinate.getX())/3;
        float centerY = (this.firstDotCoordinate.getY()+this.secondDotCoordinate.getY()+this.thirdDotCoordinate.getY())/3;
        this.centerDotCoordinate = new Coordinate(centerX, centerY);
    }

    public float[][] getTriangleCoordinates(){
        float[][] triangle = {  {firstDotCoordinate.getX(), firstDotCoordinate.getY()},
                                {secondDotCoordinate.getX(), secondDotCoordinate.getY()},
                                {thirdDotCoordinate.getX(), thirdDotCoordinate.getY()}
        };
        return triangle;
    }

    public float[] getCenterCoordinates(){
        float[] center = {centerDotCoordinate.getX(), centerDotCoordinate.getY()};
        return center;
    }


}
