package dataPrepare.data.graph;

/**
 * Created by anna on 02.09.15.
 */
public class Coordinate{
    private float x;
    private float y;

    public Coordinate(float x, float y){
        this.x = x;
        this.y = y;
    }
    public Coordinate(Coordinate coordinate){
        this.x = coordinate.getX();
        this.y = coordinate.getY();
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
    public void changeX(Float value){
        this.x+=value;
    }
    public void changeY(float value){
        this.y+=value;
    }
    public void multiplyX(float value){
        this.x*=value;
    }
    public void multiplyY(float value){
        this.y*=value;
    }

    public boolean contains(Coordinate coordinate){
        if (x==coordinate.getX() && y==coordinate.getY()){
            return  true;
        }
        else return false;
    }

//    @Override
//    public int hashCode() {
//        final int prime = 31;
//        int result = 1;
//        result = prime*result + (int) this.getX();
//        result = prime*result + (int) this.getY();
//        return result;
//    }

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
