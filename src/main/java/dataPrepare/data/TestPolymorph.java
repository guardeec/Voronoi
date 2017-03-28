package dataPrepare.data;

import dataPrepare.data.debug.SaveVoronoi;
import dataPrepare.data.graph.Coordinate;
import dataPrepare.data.graph.Host;
import dataPrepare.data.polymorph.Chain;
import dataPrepare.data.voronoi.Polygon;
import dataPrepare.data.voronoi.Voronoi;
import dataPrepare.methods.*;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import org.apache.commons.math3.stat.descriptive.rank.Median;

import java.util.*;

/**
 * Created by guardeec on 11.07.16.
 */
public class TestPolymorph {


    public void newTest(Voronoi voronoi){
        List<Polygon> chain = new Chain().getChain(voronoi);



        //for (Polygon polygon : chain){polygon.getHost().addMetric("cellSize", 1000f);}
        List<Polygon> test = new LinkedList<>();
        for (int i=0; i<15; i++){
            test.add(chain.get(i));
        }
        prepare(chain);





        for (Coordinate coordinate : voronoi.getDots()){
            coordinate.setX(coordinate.getX()+10);
            coordinate.setY(coordinate.getY()+10);
        }

//

//        float min = chain.get(0).getArea();
//        for (Polygon polygon : chain){if (min>polygon.getArea()){min=polygon.getArea();}}
//        System.out.println("min "+min);
        //for (Polygon polygon : chain){polygon.getHost().addMetric("cellSize", min*3);}



        double[] v = new double[chain.size()];
        for (int i=0; i<chain.size(); i++){v[i]=(double)chain.get(i).getArea();}
        float median = (float) new Median().evaluate(v);
        System.out.println("min "+median);
        for (Polygon polygon : chain){polygon.getHost().addMetric("cellSize", median);}

        int c = 1;
        for (Polygon polygon : chain){
            polymorphTest(polygon, voronoi);
            System.out.println(c+" from "+chain.size()+" with liefactor: "+checkLieFactor(polygon)+" area: "+polygon.getArea());
            c++;
        }


        int r=0, sum=0;

        for (Polygon polygon : voronoi.getPolygons()){
            r++;
            System.out.println("#"+r+" lie:"+checkLieFactor(polygon));
            sum+=checkLieFactor(polygon);
        }
        System.out.println("TOTAL: "+sum/r);

        try {
            List<Color> colors = Help.allColors();
            for (Polygon polygon : voronoi.getPolygons()){polygon.getHost().addMetric("color",colors.get((int)polygon.getHost().getMetrics().get("cycleId")*10));}
        } catch (ClassNotFoundException | IllegalAccessException e) {e.printStackTrace();}
    }

    private void prepare(List<Polygon> polygons){
        for (Polygon polygon : polygons){
            for (Coordinate coordinate : polygon.getPoints()){
                coordinate.addMetric("stopPolymorph", false);coordinate.addMetric("c", 0);
            }
        }
    }


    private void addColorToDot(Coordinate target, int color, Voronoi voronoi){
        for (Coordinate coordinate : voronoi.getDots()){
            coordinate.addMetric("c", 0);
        }
        target.addMetric("c", color);
    }

    private void polymorphTest(Polygon polygon, Voronoi voronoi){


        boolean switcher = true;
        float lieBefore = checkLieFactor(polygon);

        int counter = 0;

        OUT:while (checkLieFactor(polygon) > 1.1f && !checkOnBlocked(polygon)) {
            if (polygon.getArea()<(float)polygon.getHost().getMetrics().get("cellSize")){break ;}

            //System.out.print("s="+switcher+" ");

            for (int i = 1; i < polygon.getPoints().size() + 1; i++) {
                Coordinate target = polygon.getPoints().get(i % polygon.getPoints().size());
                if (!(boolean) target.getMetric("stopPolymorph")) {

                    Coordinate left = polygon.getPoints().get((i - 1));
                    Coordinate right = polygon.getPoints().get((i + 1) % polygon.getPoints().size());
                    Coordinate coordinate = new Coordinate(target.getX(), 0f);


                    float angle = (getAngle(coordinate, target, right) - 90 + 360) % 360;
                    if (target.getX() > right.getX()) {angle = (360 - angle + 360 + 180) % 360;}



                    if (switcher){
                        addColorToDot(target, 1, voronoi);
                        float d = getDistance(target, (angle + getAngle(left, target, right) / 2) % 360, polygon);
                        if (d>10 && dotMovementReflect(voronoi, target, (angle + getAngle(left, target, right) / 2) % 360, d * getFine(target, polygon))){
                            dotMovement(target, (angle + getAngle(left, target, right) / 2) % 360, d * getFine(target, polygon));
                        }else {
                            if (DistanceFromDot.distanceBetweenCoordinates(left, target)>40){AddExternalPoints.brokeEdgeTest(voronoi, left, target);}
                            if (DistanceFromDot.distanceBetweenCoordinates(right, target)>40){AddExternalPoints.brokeEdgeTest(voronoi, target, right);}
                            target.addMetric("stopPolymorph", true); break;
                        }
                    }else {
                        addColorToDot(target, 2, voronoi);
                        //System.out.print(" "+checkLieFactor(polygon));
                        Point2D z = new Point2D(left.getX(), 0f);
                        Point2D p1 = new Point2D(left.getX(), left.getY());
                        Point2D p3 = new Point2D(right.getX(), right.getY());

                        float a = (float) p1.angle(z, p3)+90;
                        if (p3.getX() < z.getX()) {a = (360 - a + 360 + 180) % 360;}
                        a=(a+270)%360;

                        float d = getDistance(target, a, polygon);


                        if (d>10 && dotMovementReflect(voronoi, target, a, d * getFine(target, polygon))){
                            dotMovement(target, a, d * getFine(target, polygon));
                        }else {
                            if (DistanceFromDot.distanceBetweenCoordinates(left, target)>40){AddExternalPoints.brokeEdgeTest(voronoi, left, target);}
                            if (DistanceFromDot.distanceBetweenCoordinates(right, target)>40){AddExternalPoints.brokeEdgeTest(voronoi, target, right);}
                            target.addMetric("stopPolymorph", true); break;
                        }
                    }

                    if (!normilizeOnSimmetry(polygon, voronoi)){
                        System.out.println("GET IT");
                        addColorToDot(target, 0, voronoi);
                    }


                    //System.out.print("#"+counter+" l "+checkLieFactor(polygon));
                    SaveVoronoi.getInstance().saveStatement(voronoi);
                    if (checkLieFactor(polygon) < 1.1f) {break OUT;}
                }
            }

            counter++;

            //System.out.println("#"+counter+" lf="+checkLieFactor(polygon)+" lfB="+lieBefore);

            if (checkLieFactor(polygon)>=lieBefore && counter%30==0){
                switcher^=true;
            }
            lieBefore = checkLieFactor(polygon);
        }
        System.out.println();
        for (Coordinate coordinate : polygon.getPoints()){coordinate.addMetric("stopPolymorph", true);}
        if (!checkOnPlanar(voronoi)){new AddExternalPoints().addeXternalPointsForPlanarityWithPadding(voronoi);}
    }


    private boolean isConvex(Polygon polygon){
        return ConvexHull.getFromCoordinates(polygon.getPoints()).size() == polygon.getPoints().size();
    }


    private static float padding = 10;


    public TestPolymorph() {

    }


    private float getDistance(Coordinate coordinate, float angle, Polygon polygon){
        List<Coordinate[]> edges = new LinkedList<>();
        for(int i=0; i<polygon.getPoints().size(); i++){
            Coordinate[] edge = {
                    polygon.getPoints().get(i),
                    polygon.getPoints().get((i+1)%polygon.getPoints().size())
            };
            if (!(edge[0]==coordinate||edge[1]==coordinate)){
                edges.add(edge);
            }
        }
        for (Coordinate[] edge : edges){
            Coordinate collisionPoint = getCollisionPoint(
                    coordinate,
                    dotMovement(coordinate, angle),
                    edge[0],
                    edge[1]
                    );
            if (collisionPoint!=null){
                if(dotBelongsToEdge(collisionPoint, edge)){
                    return DistanceFromDot.distanceBetweenCoordinates(coordinate, collisionPoint);
                }
            }
        }
        return -1;
    }

    private Coordinate getCollisionPoint(Coordinate a1, Coordinate a2, Coordinate b1, Coordinate b2){
        float a = a1.getY()  - a2.getY();
        float b = a2.getX() - a1.getX();
        float c = b1.getY() - b2.getY();
        float d = b2.getX() - b1.getX();
        float D = a*d-c*b;
        if (D!=0){
            float c1 = a2.getY() * a1.getX() - a2.getX() * a1.getY();
            float c2 = b2.getY() * b1.getX() - b2.getX() * b1.getY();

            float x = (b*c2-d*c1)/D;
            float y = (c*c1-a*c2)/D;
            if (x<0 || y<0){
                return null;
            }
            return new Coordinate(
                    x,
                    y
            );
        }else {
            return null;
        }
    }
    private boolean dotBelongsToEdge(Coordinate coordinate, Coordinate[] edge){
        float dEdge = DistanceFromDot.distanceBetweenCoordinates(edge[0], edge[1]);
        float dBetweenCollisonPointAndEdgeVerteses=DistanceFromDot.distanceBetweenCoordinates(coordinate, edge[0])+DistanceFromDot.distanceBetweenCoordinates(coordinate, edge[1]);
        return (Math.round(dEdge*100)/100==Math.round(dBetweenCollisonPointAndEdgeVerteses*100)/100);
    }


    private int getNumberOfFreePoints(Polygon polygon){
        int counter = 0;
        for (Coordinate coordinate : polygon.getPoints()){
            if ((boolean) coordinate.getMetric("stopPolymorph") || (boolean) coordinate.getMetric("BLUE")){
                counter++;
            }
        }
        return polygon.getPoints().size()-counter;
    }

    private List<Coordinate> savePolygonCoordinatesWhileNormolize(List<Coordinate> coordinates){
        List<Coordinate> newList = new LinkedList<>();
        for (Coordinate coordinate : coordinates){
            newList.add(new Coordinate(
                    coordinate.getX(),
                    coordinate.getY()
            ));
        }
        return newList;
    }

    private boolean normilizeOnSimmetry(Polygon polygon, Voronoi voronoi){
        List<Coordinate> previosStatement = savePolygonCoordinatesWhileNormolize(polygon.getPoints());

        //находим стартовую точку
        Integer startBlockedPoint = null;
        for (int i=0; i<polygon.getPoints().size(); i++){
            if ((boolean)polygon.getPoints().get(i).getMetric("stopPolymorph")
                    && !(boolean) polygon.getPoints().get((i+1)%polygon.getPoints().size()).getMetric("stopPolymorph")
                    ){
                startBlockedPoint=(i)%polygon.getPoints().size();
                break;
            }
        }

        //находим кривую по которой будем номализоавть
        List<Coordinate> curveToNormal = new LinkedList<>();
        if (startBlockedPoint!=null){
            for (int i=startBlockedPoint; i<polygon.getPoints().size()*2; i++){
                if (!(boolean)polygon.getPoints().get((i+1)%polygon.getPoints().size()).getMetric("stopPolymorph")){
                    curveToNormal.add(polygon.getPoints().get(i%polygon.getPoints().size()));
                }else {
                    curveToNormal.add(polygon.getPoints().get(i%polygon.getPoints().size()));
                    curveToNormal.add(polygon.getPoints().get((i+1)%polygon.getPoints().size()));
                    break;
                }
            }
        }else {
            curveToNormal = new LinkedList<>();
            for (Coordinate coordinate: polygon.getPoints()){
                curveToNormal.add(coordinate);
            }
        }

        //находим нормализованное расстояние которое должно быть между точками
        float d = 0;
        for(int i=0; i<curveToNormal.size()-1; i++){
            d += DistanceFromDot.distanceBetweenCoordinates(curveToNormal.get(i), curveToNormal.get(i+1));
        }
        d = d/(curveToNormal.size()-1);
        //находим точки которые будем нормализовать
        List<Coordinate> dotsToNormal = new LinkedList<>();
        for (int i=1; i<curveToNormal.size()-1; i++){
            dotsToNormal.add(curveToNormal.get(i));
        }

        List<Coordinate> oldDots = new LinkedList<>();
        List<Coordinate> newDots = new LinkedList<>();
        for (Coordinate coordinate : dotsToNormal){
            oldDots.add(coordinate);
        }
        int pointer = 0;
        float edgeDistance=0;
        float fine = d;


        if (curveToNormal.size()>0){

            while (pointer<curveToNormal.size()-1){
                edgeDistance=DistanceFromDot.distanceBetweenCoordinates(curveToNormal.get(pointer), curveToNormal.get(pointer+1));
                if (edgeDistance>fine){
                    Coordinate coordinate = new Coordinate(
                            curveToNormal.get(pointer).getX(),
                            curveToNormal.get(pointer).getY()
                    );
                    float angle = (getAngle(new Coordinate(coordinate.getX(), 0), coordinate, curveToNormal.get(pointer+1))-90+360)%360;
                    if (coordinate.getX()>curveToNormal.get(pointer+1).getX()){
                        angle=(360-angle+360+180)%360;
                    }
                    dotMovement(coordinate, angle, fine);

                    newDots.add(coordinate);
                    curveToNormal.add(pointer+1, coordinate);
                    fine=d;
                }else {
                    fine-=edgeDistance;
                }
                pointer++;
            }

            if (newDots.size()-oldDots.size()<0){
                return false;
            }
            for (int i=0; i<oldDots.size(); i++){
                oldDots.get(i).setX(
                        newDots.get(i)
                                .getX());
                oldDots.get(i).setY(newDots.get(i).getY());
            }
        }

        if (!checkOnPlanar(voronoi)){
            for (int i=0; i<polygon.getPoints().size(); i++){
                polygon.getPoints().get(i).setX(previosStatement.get(i).getX());
                polygon.getPoints().get(i).setY(previosStatement.get(i).getY());
            }
            return false;
        }
        return true;
    }

    private float getFine(Coordinate target, Polygon polygon){
        Coordinate left2=null;
        Coordinate left=null;
        Coordinate right = null;
        Coordinate right2 = null;
        for (int i=0; i<polygon.getPoints().size(); i++){
            if (polygon.getPoints().get(i)==target){
                left2 =polygon.getPoints().get((i-2+polygon.getPoints().size())%polygon.getPoints().size());
                left  =polygon.getPoints().get((i-1+polygon.getPoints().size())%polygon.getPoints().size());
                right =polygon.getPoints().get((i+1+polygon.getPoints().size())%polygon.getPoints().size());
                right2=polygon.getPoints().get((i+2+polygon.getPoints().size())%polygon.getPoints().size());
            }
        }
        float leftAngle   = getAngle(left2,  left,   target);
        float targetAngle = getAngle(left,   target, right );
        float rightAngle  = getAngle(target, right,  right2);
        if ((boolean)left.getMetric("stopPolymorph")&&(boolean)right.getMetric("stopPolymorph")){
            return 0.03f;
        }
        if (rightAngle>targetAngle && leftAngle>targetAngle){
            return 0.03f;
        }
        if (rightAngle<targetAngle && leftAngle<targetAngle){
            return 0.005f;
        }
        else {
            return 0.01f;
        }
    }

    private float distanceOfBiggestEdgeThatNotBlocked(Polygon polygon){
        float maxD = 0;
        for (int i=0; i<polygon.getPoints().size(); i++){
            if (
                    !((boolean)polygon.getPoints().get(i).getMetric("stopPolymorph")
                    &&
                    (boolean)polygon.getPoints().get((i+1)%polygon.getPoints().size()).getMetric("stopPolymorph"))
                &&
                    !((boolean)polygon.getPoints().get(i).getMetric("BLUE")
                    &&
                    (boolean)polygon.getPoints().get((i+1)%polygon.getPoints().size()).getMetric("BLUE"))
            ){
                float d = DistanceFromDot.distanceBetweenCoordinates(
                        polygon.getPoints().get(i),
                        polygon.getPoints().get((i+1)%polygon.getPoints().size())
                );
                if (d>maxD){
                    maxD=d;
                }
            }
        }
        return maxD;
    }

    private boolean checkOnBlocked(Polygon polygon){
        for (Coordinate coordinate : polygon.getPoints()){
            if (!(boolean)coordinate.getMetric("stopPolymorph")){
                return false;
            }
        }
        System.out.println("TOTAL BLOCK");
        return true;
    }

    private float getAngle(Coordinate a, Coordinate b, Coordinate c){
        double x1 = a.getX() - b.getX(), x2 = c.getX() - b.getX();
        double y1 = a.getY() - b.getY(), y2 = c.getY() - b.getY();
        double d1 = Math.sqrt (x1 * x1 + y1 * y1);
        double d2 = Math.sqrt (x2 * x2 + y2 * y2);

       double middle = (x1 * x2 + y1 * y2) / (d1 * d2);
        if (middle<-1){
            middle+=1;
        }
        return (float) Math.toDegrees(Math.acos (middle));
    }

    public static float checkLieFactor(dataPrepare.data.voronoi.Polygon polygon){
        if ((float)polygon.getHost().getMetrics().get("cellSize")>polygon.getArea()){
            return (float)polygon.getHost().getMetrics().get("cellSize")/polygon.getArea();
        }else {
            return polygon.getArea()/(float)polygon.getHost().getMetrics().get("cellSize");
        }
    }



    public static boolean checkOnPlanar(Voronoi voronoi){
        List<List<Host>> edges = voronoi.voronoiLikeAGraph(voronoi).getEdges();
        boolean flag = true;
        for (Coordinate coordinate : voronoi.getDots()){
            if (!(boolean)coordinate.getMetric("BLUE")){
                //если точка красная
                if ((boolean)coordinate.getMetric("stopPolymorph")){
                    for (List<Host> edge : edges){
                        if (
                                //ребро не содержит анализируемой точки
                                !(edge.get(0).getCoordinate()==coordinate || edge.get(1).getCoordinate()==coordinate)
                                &&
                                    //ребро может двигаться
                                    (
                                        //т.е. точка 1 не содержит ни синию ни красную точку
                                        (!(boolean)edge.get(0).getCoordinate().getMetric("BLUE") && !(boolean)edge.get(0).getCoordinate().getMetric("stopPolymorph"))
                                        ||
                                        //либо точка 2 не содержит ни синиюю ни красную точку
                                        (!(boolean)edge.get(1).getCoordinate().getMetric("BLUE") && !(boolean)edge.get(1).getCoordinate().getMetric("stopPolymorph"))
                                    )
                                ){
                            float paddingDistance = DistanceFromDot.LineToPointDistance2D(
                                    edge.get(0).getCoordinate(),
                                    edge.get(1).getCoordinate(),
                                    coordinate
                            );
                            if (paddingDistance<padding){
                                flag = false;
                            }
                        }
                    }
                //если точка белая
                }else {
                    for (List<Host> edge : edges){

                        if (!(edge.get(0).getCoordinate()==coordinate || edge.get(1).getCoordinate()==coordinate)
                                && (boolean)edge.get(0).getCoordinate().getMetric("stopPolymorph")
                                && (boolean)edge.get(1).getCoordinate().getMetric("stopPolymorph")
                            ) {
                            float paddingDistance = DistanceFromDot.LineToPointDistance2D(
                                    edge.get(0).getCoordinate(),
                                    edge.get(1).getCoordinate(),
                                    coordinate
                            );
                            if (paddingDistance < padding) {
                                coordinate.addMetric("BLUE", true);
                                flag = false;
                            }
                        }
                    }
                }
                //синие не трогаем
            }
        }
        if (!flag){
            return false;
        }


        for (int i=0; i<edges.size(); i++){
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
                    if (IsLinePartsIntersected(
                            edges.get(i).get(0).getCoordinate(),
                            edges.get(i).get(1).getCoordinate(),
                            edges.get(q).get(0).getCoordinate(),
                            edges.get(q).get(1).getCoordinate()
                    )) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    private static boolean IsLinePartsIntersected(Coordinate a, Coordinate b, Coordinate c, Coordinate d) {

        double common = (b.getX() - a.getX())*(d.getY() - c.getY()) - (b.getY() - a.getY())*(d.getX() - c.getX());

        if (common == 0) return false;

        double rH = (a.getY() - c.getY())*(d.getX() - c.getX()) - (a.getX() - c.getX())*(d.getY() - c.getY());
        double sH = (a.getY() - c.getY())*(b.getX() - a.getX()) - (a.getX() - c.getX())*(b.getY() - a.getY());

        double r = rH / common;
        double s = sH / common;

        return r >= 0 && r <= 1 && s >= 0 && s <= 1;
    }





    private Coordinate dotMovement(Coordinate coordinate, float angle){
        double angleR = Math.toRadians((double) angle);
        double x1 = coordinate.getX();
        double y1 = coordinate.getY();
        return new Coordinate(
                (float) (x1+1*Math.cos(angleR)),
                (float) (y1+1*Math.sin(angleR))
        );
    }
    private void dotMovement(Coordinate coordinate, float angle, float distance){
        double angleR = Math.toRadians((double) angle);
        double x1 = coordinate.getX();
        double y1 = coordinate.getY();
        coordinate.setX((float) (x1+distance*Math.cos(angleR)));
        coordinate.setY((float) (y1+distance*Math.sin(angleR)));
    }
    private boolean dotMovementReflect(Voronoi voronoi, Coordinate coordinate, float angel, float distance){
        dotMovement(coordinate, angel, distance);
        boolean flag = checkOnPlanar(voronoi);
        dotMovement(coordinate, (angel+180)%360, distance);
        return flag;
    }

}
