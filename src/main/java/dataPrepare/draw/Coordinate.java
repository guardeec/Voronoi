package dataPrepare.draw;

/**
 * Created by anna on 02.09.15.
 */
public class Coordinate implements CoordinateVoronoiImpl{
    private float x;
    private float y;
    private int hash;
    private int charge;
    private boolean stop;


    @Override
    public void setX(float x){
        this.x = x;
    }
    @Override
    public void setY(float y){
        this.y = y;
    }
    @Override
    public float getX(){
        return this.x;
    }
    @Override
    public float getY(){
        return this.y;
    }

    @Override
    public void setHash(int hash) {
        this.hash = hash;
    }
    @Override
    public int getHash() {
        return hash;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime*result + (int) this.getX();
        result = prime*result + (int) this.getY();
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
        Coordinate coordinate = (Coordinate) obj;
        return this.getX() == coordinate.getX() && this.getY() == coordinate.getY();
    }
}
