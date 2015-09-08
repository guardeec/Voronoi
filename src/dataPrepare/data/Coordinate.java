package dataPrepare.data;

/**
 * Created by anna on 02.09.15.
 */
public class Coordinate{
    float x;
    float y;
    public Coordinate(float x, float y){
        this.x = x;
        this.y = y;
    }
    public void setX(float x){
        this.x = x;
    }
    public void setY(float y){
        this.y = y;
    }
    public float getX(){
        return this.x;
    }
    public float getY(){
        return this.y;
    }

    public boolean contains(Coordinate coordinate){
        if (x==coordinate.getX() && y==coordinate.getY()){
            return  true;
        }
        else return false;
    }
}
