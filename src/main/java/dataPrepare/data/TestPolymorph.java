package dataPrepare.data;

import dataPrepare.data.graph.Coordinate;
import dataPrepare.data.graph.Host;
import dataPrepare.data.voronoi.Polygon;
import dataPrepare.data.voronoi.Voronoi;
import dataPrepare.draw.SaveVoronoi;
import dataPrepare.methods.ConvexHull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by guardeec on 11.07.16.
 */
public class TestPolymorph {


    public TestPolymorph() {
    }

    private float stepSize=45;

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
                    return distanceBetweenCoordinates(coordinate, collisionPoint);
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
        float dEdge = distanceBetweenCoordinates(edge[0], edge[1]);
        float dBetweenCollisonPointAndEdgeVerteses=distanceBetweenCoordinates(coordinate, edge[0])+distanceBetweenCoordinates(coordinate, edge[1]);
        return (Math.round(dEdge*100)/100==Math.round(dBetweenCollisonPointAndEdgeVerteses*100)/100);
    }

    public void polymorph(List<dataPrepare.data.voronoi.Polygon> polygons, Voronoi voronoi, Integer a){

        for (Polygon polygon : polygons){

            if (!checkOnBlocked(polygon)){
                OUT:while (polygon.getArea()>(float)polygon.getHost().getMetrics().get("cellSize")){
                    for (int i=1; i<polygon.getPoints().size()+1; i++){
                        Coordinate target = polygon.getPoints().get(i%polygon.getPoints().size());
                        if (!(boolean)target.getMetric("stopPolymorph")){
                            Coordinate left = polygon.getPoints().get((i-1));
                            Coordinate right = polygon.getPoints().get((i+1)%polygon.getPoints().size());
                            Coordinate coordinate = new Coordinate(target.getX(), 0f);
                            float angle = (getAngle(coordinate, target, right)-90+360)%360;
                            if (target.getX()>right.getX()){
                                angle=(360-angle+360+180)%360;
                            }
                            //System.out.println(getDistance(coordinate, (angle + getAngle(left, target, right) / 2) % 360, polygon));
                            float d = getDistance(target, (angle+getAngle(left, target, right)/2)%360, polygon);
                            if (d>0){
                                dotMovement(target, (angle+getAngle(left, target, right)/2)%360, d*getFine(target, polygon));
                                SaveVoronoi.getInstance().saveStatement(voronoi.getPolygons());
                            }
                        }
                        if (polygon.getArea()<=(float)polygon.getHost().getMetrics().get("cellSize")){
                            break OUT;
                        }
                    }
                    normilizeOnSimmetry(polygon, voronoi);
                }
            }


            for (Coordinate coordinate : polygon.getPoints()){
                coordinate.addMetric("stopPolymorph", true);
            }
            SaveVoronoi.getInstance().saveStatement(voronoi.getPolygons());
        }
    }

    private void normilizeOnSimmetry(Polygon polygon, Voronoi voronoi){
        //находим стартовую точку
        Integer startBlockedPoint = null;
        for (int i=0; i<polygon.getPoints().size(); i++){
            if ((boolean)polygon.getPoints().get(i).getMetric("stopPolymorph")
                    && (boolean) polygon.getPoints().get((i+1)%polygon.getPoints().size()).getMetric("stopPolymorph")
                    ){
                startBlockedPoint=(i+1)%polygon.getPoints().size();
                if (!(boolean)polygon.getPoints().get((i+2)%polygon.getPoints().size()).getMetric("stopPolymorph")){
                    break;
                }
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
        }

        //находим нормализованное расстояние которое должно быть между точками
        float d = 0;
        for(int i=0; i<curveToNormal.size()-1; i++){
            d += distanceBetweenCoordinates(curveToNormal.get(i), curveToNormal.get(i+1));
        }
        d = d/(curveToNormal.size()-1);

        //находим точки которые будем нормализовать
        List<Coordinate> dotsToNormal = new LinkedList<>();
        for (int i=1; i<curveToNormal.size()-1; i++){
            dotsToNormal.add(curveToNormal.get(i));
        }

        //нормализуем
        List<Integer> coordinatesPositions = new LinkedList<>();
        List<Float> coordinatesPositionsPadding = new LinkedList<>();
        float segmentDistance=0;
        float dDistance=d;
        for(int i=0; i<curveToNormal.size()-2;i++){
            segmentDistance += distanceBetweenCoordinates(curveToNormal.get(i), curveToNormal.get(i+1));
            float padding = segmentDistance-(segmentDistance-dDistance);
            if (padding>0){
                coordinatesPositions.add(i);
                coordinatesPositionsPadding.add(padding);
            }
        }
        List<Coordinate> movedCoords = new LinkedList<>();
        for(Integer i : coordinatesPositions){
            Coordinate coordinate = new Coordinate(curveToNormal.get(i).getX(), curveToNormal.get(i).getY());
            Coordinate next = new Coordinate(curveToNormal.get(i+1).getX(), curveToNormal.get(i+1).getY());
            Coordinate zero = new Coordinate(coordinate.getX(), 0);
            float angle = (getAngle(zero, coordinate, next)-90+360)%360;
            if (coordinate.getX()>next.getX()){
                angle=(360-angle+360+180)%360;
            }
            dotMovement(coordinate, angle, coordinatesPositionsPadding.get(i));
            movedCoords.add(coordinate);
        }

        for (int i=0; i<dotsToNormal.size(); i++){
            dotsToNormal.get(i).setX(movedCoords.get(i).getX());
            dotsToNormal.get(i).setY(movedCoords.get(i).getY());
            SaveVoronoi.getInstance().saveStatement(voronoi.getPolygons());
        }
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



    private boolean checkOnBlocked(Polygon polygon){
        for (Coordinate coordinate : polygon.getPoints()){
            if (!(boolean)coordinate.getMetric("stopPolymorph")){
                return false;
            }
        }
        System.out.println("BLOCKED "+polygon.getHost().getMetrics().get("id"));
        return true;
    }

    private float getAngle(Coordinate a, Coordinate b, Coordinate c){
        double x1 = a.getX() - b.getX(), x2 = c.getX() - b.getX();
        double y1 = a.getY() - b.getY(), y2 = c.getY() - b.getY();
        double d1 = Math.sqrt (x1 * x1 + y1 * y1);
        double d2 = Math.sqrt (x2 * x2 + y2 * y2);
        return (float) Math.toDegrees(Math.acos ((x1 * x2 + y1 * y2) / (d1 * d2)));
    }

    public void polymorph(List<dataPrepare.data.voronoi.Polygon> polygons, Voronoi voronoi){
        for (dataPrepare.data.voronoi.Polygon polygon : polygons){
            polygon.getHost().addMetric("opacity", 0.5f);
            int tick=0;
            while (polymorpOfCell_TICK(polygon, voronoi)){
                tick++;
                if (tick>500){
                    break;
                }
                SaveVoronoi.getInstance().saveStatement(voronoi.getPolygons());
            }
            for (Coordinate coordinate : polygon.getPoints()){
                coordinate.addMetric("stopPolymorph", true);
            }
            System.out.println("AREA: "+polygon.getArea());
        }
    }

    private boolean polymorpOfCell_TICK(dataPrepare.data.voronoi.Polygon polygon, Voronoi voronoi){
        List<Decision> decisions = new LinkedList<>();

        for (Coordinate coordinate : polygon.getPoints()){
            if (!(boolean)coordinate.getMetric("stopPolymorph")){
                for (float angle=360; angle>0; angle-=stepSize){
                    Decision decision = dotMovementReflect(voronoi,polygon,coordinate,1,angle);
                    if (decision!=null){
                        decisions.add(decision);
                    }
                }
            }
        }

        if (decisions.size()==0 || checkLieFactor(polygon)<1.4f){
            return false;
        }



        sortByConvex(decisions);
        if (decisions.get(0).solution.convexMeasure==0){
            sortByConvexAndRemoveNotConvex(decisions);
            //sortBySimmetryAndAnglesPRIORITY_ANGLES(decisions);
            sortBySimmetry(decisions);
        }else {
            //sortBySimmetryAndAnglesPRIORITY_SIMMETRY(decisions);
            //sortBySimmetryAndAngles(decisions);
            sortByLieFactor(decisions);
        }



        dotMovement(decisions.get(0).coordinate, decisions.get(0).angle, decisions.get(0).distance);
        return true;
    }

    private void sortByConvex(List<Decision> decisions){
        decisions.sort((o1, o2) -> {
            if (o1.solution.convexMeasure<=o2.solution.convexMeasure){
                return -1;
            }else {
                return 1;
            }
        });
    }
    private void sortByConvexAndRemoveNotConvex(List<Decision> decisions){
        sortByConvex(decisions);
        Iterator<Decision> decisionIterator = decisions.iterator();
        while (decisionIterator.hasNext()){
            Decision decision = decisionIterator.next();
            if (decision.solution.convexMeasure>0){
                decisionIterator.remove();
            }
        }
    }
    private void sortByAngles(List<Decision> decisions){
        decisions.sort((o1, o2) -> {
            if (o1.solution.angles<=o2.solution.angles){
                return 1;
            }else {
                return -1;
            }
        });
    }
    private void sortBySimmetry(List<Decision> decisions){
        decisions.sort((o1, o2) -> {
            if (o1.solution.simmetryTest<=o2.solution.simmetryTest){
                return -1;
            }else {
                return 1;
            }
        });
    }
    private void sortBySimmetryAndAngles(List<Decision> decisions){
        List<Decision> decisionListSimmetryAndAngle = decisions.stream().collect(Collectors.toCollection(LinkedList::new));
        Iterator<Decision> decisionIteratorSimmetryAndAngle = decisionListSimmetryAndAngle.iterator();
        while (decisionIteratorSimmetryAndAngle.hasNext()){
            Decision decision = decisionIteratorSimmetryAndAngle.next();
            boolean removed=false;
            if (decision.solution.simmetry>decision.status.simmetry){
                decisionIteratorSimmetryAndAngle.remove();
                removed = true;
            }
            if (decision.solution.angles>decision.status.angles && !removed){
                decisionIteratorSimmetryAndAngle.remove();
            }
        }

        if (decisionListSimmetryAndAngle.size()!=0){
            decisions=decisionListSimmetryAndAngle;
        }
        sortBySimmetry(decisions);
    }
    private void sortBySimmetryAndAnglesPRIORITY_ANGLES(List<Decision> decisions){
        sortByAngles(decisions);
        if (decisions.get(0).solution.angles<decisions.get(0).status.angles){
            sortBySimmetry(decisions);
        }else {
            Iterator<Decision> iterator = decisions.iterator();
            while (iterator.hasNext()){
                Decision decision = iterator.next();
                if (decision.solution.angles<decision.status.angles){
                    iterator.remove();
                }
            }
            sortBySimmetry(decisions);
        }
    }
    private void sortBySimmetryAndAnglesPRIORITY_SIMMETRY(List<Decision> decisions){
        sortBySimmetry(decisions);
        if (decisions.get(0).solution.simmetry<decisions.get(0).status.simmetry){
            sortByAngles(decisions);
        }else {
            Iterator<Decision> iterator = decisions.iterator();
            while (iterator.hasNext()){
                Decision decision = iterator.next();
                if (decision.solution.simmetry<decision.status.simmetry){
                    iterator.remove();
                }
            }
            sortByAngles(decisions);
        }
    }
    private void sortByLieFactor(List<Decision> decisions){
        decisions.sort((o1, o2) -> {
            if (o1.solution.lieFactor<=o2.solution.lieFactor){
                return -1;
            }else {
                return 1;
            }
        });
    }

    private float anglesTest(Polygon polygon){
        float smallestAngle = 360;
        for (int i=0; i<polygon.getPoints().size(); i++){
            Coordinate a = polygon.getPoints().get(i);
            Coordinate b = polygon.getPoints().get((i+1)%polygon.getPoints().size());
            Coordinate c = polygon.getPoints().get((i+2)%polygon.getPoints().size());
            double x1 = a.getX() - b.getX(), x2 = c.getX() - b.getX();
            double y1 = a.getY() - b.getY(), y2 = c.getY() - b.getY();
            double d1 = Math.sqrt (x1 * x1 + y1 * y1);
            double d2 = Math.sqrt (x2 * x2 + y2 * y2);
            double angle= Math.acos ((x1 * x2 + y1 * y2) / (d1 * d2));
            if (angle<smallestAngle){
                smallestAngle=(float) angle;
            }
        }
        return smallestAngle;
    }
    private float simmetryTest(Polygon polygon){
        float biggest=distanceBetweenCoordinates(polygon.getPoints().get(0), polygon.getPoints().get(1));
        float smallest = distanceBetweenCoordinates(polygon.getPoints().get(0), polygon.getPoints().get(1));
        for (int i=0; i<polygon.getPoints().size(); i++){
            Coordinate coordinate1 = polygon.getPoints().get(i);
            Coordinate coordinate2 = polygon.getPoints().get((i+1)%polygon.getPoints().size());
            float distance = distanceBetweenCoordinates(coordinate1, coordinate2);
            if (((boolean)coordinate1.getMetric("stopPolymorph") && (boolean)coordinate2.getMetric("stopPolymorph"))){

            }else {
                if (biggest<distance){
                    biggest=distance;
                }
                if (smallest>distance){
                    smallest=distance;
                }
            }
        }
        return biggest/smallest;
    }
    private static float checkLieFactor(dataPrepare.data.voronoi.Polygon polygon){
        if ((float)polygon.getHost().getMetrics().get("cellSize")>polygon.getArea()){
            return (float)polygon.getHost().getMetrics().get("cellSize")/polygon.getArea();
        }else {
            return polygon.getArea()/(float)polygon.getHost().getMetrics().get("cellSize");
        }
    }
    private float simmetry(dataPrepare.data.voronoi.Polygon polygon){
        float biggestDistane = 0;
        for (Coordinate coordinate : polygon.getPoints()){
            for (Coordinate coordinate1 : polygon.getPoints()){
                float currentDistance = distanceBetweenCoordinates(coordinate, coordinate1);
                if (biggestDistane<currentDistance){
                    biggestDistane=currentDistance;
                }
            }
        }
        float sqareOfCircle = (float) (Math.PI*Math.pow(1/2, 2));
        return sqareOfCircle/polygon.getArea();
    }
    private float convexMeasure(dataPrepare.data.voronoi.Polygon polygon){
        float squareOfCH =
                new dataPrepare.data.voronoi.Polygon(
                    ConvexHull.getFromCoordinates(polygon.getPoints()),
                    polygon.getHost()
                ).getArea();
        return Math.abs(squareOfCH-polygon.getArea());
    }
    private static boolean checkOnPlanar(Voronoi voronoi){
        List<List<Host>> edges = voronoi.voronoiLikeAGraph(voronoi).getEdges();
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

        if (r >= 0 && r <= 1 && s >= 0 && s <= 1)
            return true;
        else
            return false;
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

    private void dotMovement(Coordinate coordinate, float angle, float distance, dataPrepare.data.voronoi.Polygon polygon, Voronoi voronoi){
        double angleR = Math.toRadians((double) angle);
        double x1 = coordinate.getX();
        double y1 = coordinate.getY();
        coordinate.setX((float) (x1+distance*Math.cos(angleR)));
        coordinate.setY((float) (y1+distance*Math.sin(angleR)));
        forceMovement((float) x1, (float) y1, coordinate, polygon, voronoi);
    }

    private Decision dotMovementReflect(Voronoi voronoi, dataPrepare.data.voronoi.Polygon polygon, Coordinate coordinate, float distance, float angle){
        Decision decision = new Decision();
        decision.angle=angle;
        decision.coordinate=coordinate;
        decision.distance=distance;
        Status status = new Status();
        status.convexMeasure=convexMeasure(polygon);
        status.lieFactor=checkLieFactor(polygon);
        status.simmetry=simmetry(polygon);
        status.simmetryTest=simmetryTest(polygon);
        status.angles=anglesTest(polygon);
        decision.status=status;
        float previosX = coordinate.getX();
        float previosY = coordinate.getY();

        double angleR = Math.toRadians((double) angle);
        double x1 = coordinate.getX();
        double y1 =coordinate.getY();
        coordinate.setX((float) (x1+distance*Math.cos(angleR)));
        coordinate.setY((float) (y1+distance*Math.sin(angleR)));

        if (!checkOnPlanar(voronoi)){
            coordinate.setX(previosX);
            coordinate.setY(previosY);
            return null;
        }

        Solution solution = new Solution();
        solution.lieFactor=checkLieFactor(polygon);
        solution.convexMeasure=convexMeasure(polygon);
        solution.simmetry=simmetry(polygon);
        solution.simmetryTest=simmetryTest(polygon);
        solution.angles=anglesTest(polygon);
        decision.solution=solution;

        if (status.lieFactor<=solution.lieFactor){
            coordinate.setX(previosX);
            coordinate.setY(previosY);
            return null;
        }

        coordinate.setX(previosX);
        coordinate.setY(previosY);
        return decision;
    }




    private void forceMovement(float xBefore, float yBefore, Coordinate dotBased, dataPrepare.data.voronoi.Polygon polygon, Voronoi voronoi){
        float paddingX = Math.abs(dotBased.getX()-xBefore);
        float paddingY = Math.abs(dotBased.getY()-yBefore);

        if (dotBased.getX()-xBefore>0){
            for (Coordinate coordinate : voronoi.getDots()){
                if ( !polygon.getPoints().contains(coordinate) && !(boolean)coordinate.getMetric("stopPolymorph") && xBefore<coordinate.getX()){
                    coordinate.setX(coordinate.getX()+paddingX);
                }
            }
        }
        //LEFT
        if (dotBased.getX()-xBefore<0){
            for (Coordinate coordinate : voronoi.getDots()){
                if ( !polygon.getPoints().contains(coordinate)&& !(boolean)coordinate.getMetric("stopPolymorph") && xBefore>coordinate.getX()){
                    coordinate.setX(coordinate.getX()-paddingX);
                }
            }
        }
        //UP
        if (dotBased.getY()-yBefore>0){
            for (Coordinate coordinate : voronoi.getDots()){
                if ( !polygon.getPoints().contains(coordinate)&& !(boolean)coordinate.getMetric("stopPolymorph") && yBefore<coordinate.getY()){
                    coordinate.setY(coordinate.getY()+paddingY);
                }
            }
        }
        //DOWN
        if (dotBased.getY()-yBefore<0){
            for (Coordinate coordinate : voronoi.getDots()){
                if ( !polygon.getPoints().contains(coordinate) && yBefore>coordinate.getY()){
                    coordinate.setY(coordinate.getY()-paddingY);
                }
            }
        }
    }

    private class Decision {
        public Coordinate coordinate;
        public float angle;
        public float distance;

        public Solution solution;
        public Status status;
    }

    private class Solution{
        public float lieFactor;
        public float convexMeasure;
        public float simmetry;

        public float angles;
        public float simmetryTest;
    }

    private class Status{
        public float lieFactor;
        public float convexMeasure;
        public float simmetry;

        public float angles;
        public float simmetryTest;
    }




}
