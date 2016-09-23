package dataPrepare.data.triangulation;

import dataPrepare.data.graph.Host;

public class Triangle implements TriangleDotsImpl, TriangleVoronoiImpl {

    private int firstDot;
    private int secondDot;
    private int thirdDot;

    private Host first;
    private Host second;
    private Host third;

    public Triangle() {
    }
    public Triangle(int firstDot, int secondDot, int thirdDot) {
        this.firstDot = firstDot;
        this.secondDot = secondDot;
        this.thirdDot = thirdDot;
    }

    public Triangle(Triangle triangle){
        this.firstDot = triangle.getFirstDot();
        this.secondDot = triangle.getSecondDot();
        this.thirdDot = triangle.getThirdDot();
        this.first = triangle.getFirstHost();
        this.second = triangle.getSecondHost();
        this.third = triangle.getThirdHost();
    }

    @Override
    public int getFirstDot() {
        return firstDot;
    }
    @Override
    public int getSecondDot() {
        return secondDot;
    }
    @Override
    public int getThirdDot() {
        return thirdDot;
    }

    @Override
    public Host getFirstHost() {
        return first;
    }
    @Override
    public Host getSecondHost() {
        return second;
    }
    @Override
    public Host getThirdHost() {
        return third;
    }
    @Override
    public void setFirstHost(Host first) {
        this.first = first;
    }
    @Override
    public void setSecondHost(Host second) {
        this.second = second;
    }
    @Override
    public void setThirdHost(Host third) {
        this.third = third;
    }
}
