package dataPrepare.data;

import java.util.ArrayList;

/**
 * Created by anna on 27.08.15.
 */
public class Host {

    /*
    в будующем в хост будут добавляется параметры
    пока есть только его id и координаты
     */

        private int id;
        private float x;
        private float y;

        public Host() {
        }

        public Host(int id, float x, float y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public float getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

}
