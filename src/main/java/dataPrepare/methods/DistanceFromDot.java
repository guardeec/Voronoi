package dataPrepare.methods;

import dataPrepare.data.graph.Coordinate;

/**
 * Created by guardeec on 17.10.16.
 */
public class DistanceFromDot {

    public static float distanceBetweenCoordinates(Coordinate coordinate1, Coordinate coordinate2){
        return (float) Math.sqrt( Math.pow(coordinate1.getX()-coordinate2.getX(), 2) + Math.pow(coordinate1.getY()-coordinate2.getY(), 2));
    }

    //Compute the dot product AB . AC
    private static double DotProduct(double[] pointA, double[] pointB, double[] pointC)
    {
        double[] AB = new double[2];
        double[] BC = new double[2];
        AB[0] = pointB[0] - pointA[0];
        AB[1] = pointB[1] - pointA[1];
        BC[0] = pointC[0] - pointB[0];
        BC[1] = pointC[1] - pointB[1];
        double dot = AB[0] * BC[0] + AB[1] * BC[1];

        return dot;
    }

    //Compute the cross product AB x AC
    private static double CrossProduct(double[] pointA, double[] pointB, double[] pointC)
    {
        double[] AB = new double[2];
        double[] AC = new double[2];
        AB[0] = pointB[0] - pointA[0];
        AB[1] = pointB[1] - pointA[1];
        AC[0] = pointC[0] - pointA[0];
        AC[1] = pointC[1] - pointA[1];
        double cross = AB[0] * AC[1] - AB[1] * AC[0];

        return cross;
    }

    //Compute the distance from A to B
    private static double Distance(double[] pointA, double[] pointB)
    {
        double d1 = pointA[0] - pointB[0];
        double d2 = pointA[1] - pointB[1];

        return Math.sqrt(d1 * d1 + d2 * d2);
    }

    //Compute the distance from AB to C
//if isSegment is true, AB is a segment, not a line.
    public static float LineToPointDistance2D(Coordinate lineFirstPoint, Coordinate lineSecondPoint, Coordinate dot) {
        double[] pointA = {(double) lineFirstPoint.getX(), (double) lineFirstPoint.getY()};
        double[] pointB = {(double) lineSecondPoint.getX(), (double) lineSecondPoint.getY()};
        double[] pointC = {(double) dot.getX(),  (double) dot.getY() };

        double dist = CrossProduct(pointA, pointB, pointC) / Distance(pointA, pointB);
//        if (isSegment) {
//            double dot1 = DotProduct(pointA, pointB, pointC);
//            if (dot1 > 0)
//                return Distance(pointB, pointC);
//
//            double dot2 = DotProduct(pointB, pointA, pointC);
//            if (dot2 > 0)
//                return Distance(pointA, pointC);
//        }
        //return (float) Math.abs(dist);

        double dot1 = DotProduct(pointA, pointB, pointC);
        if (dot1 > 0)
            return (float) Distance(pointB, pointC);

        double dot2 = DotProduct(pointB, pointA, pointC);
        if (dot2 > 0)
            return (float) Distance(pointA, pointC);

        return (float) Math.abs(dist);
    }
}
