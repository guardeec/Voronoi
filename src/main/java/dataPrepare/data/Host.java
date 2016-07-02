package dataPrepare.data;

/**
 * Created by anna on 27.08.15.
 */
public class Host implements HostImpl{
    private int radius;
    private Coordinate coordinate;
    private Object metrics;

    public Host() {
    }
    public Host(int radius, Coordinate coordinate) {
        this.radius = radius;
        this.coordinate = coordinate;
    }
    public Host(Host host){
        this.radius = host.getRadius();
        this.coordinate = new Coordinate(host.getCoordinate());
    }

    public int getRadius() {
        return radius;
    }
    public void setRadius(int radius) {
        this.radius = radius;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }
    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

//    public Object getMetrics() {
//        return metrics;
//    }
//    public void setMetrics(Object metrics) {
//        this.metrics = metrics;
//    }

    @Override
    public int hashCode() {
        final int prime = 7;
        int result = 101;
        //result = prime*result + radius;
        result = prime*result + coordinate.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj==null){
            return false;
        }
        if (obj==this){
            return true;
        }
        if (this.getClass() != obj.getClass()){
            return false;
        }
        return this.coordinate.equals(((Host) obj).getCoordinate()) && this.radius == ((Host) obj).getRadius();
    }


}
