package dataPrepare.data;

import com.google.gson.Gson;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by guardeec on 27.01.16.
 */
public class ParamsGraph {
    private Map<String, List> graph;

    /*
    В конструкторе инициализуются списки нодов и линков
     */
    public ParamsGraph() {
        graph = new LinkedHashMap<String, List>();
        List<Map> nodes = new LinkedList<Map>();
        List<Map> links = new LinkedList<Map>();
        graph.put("nodes", nodes);
        graph.put("links", links);
    }

    /*
    Добавление ноды
     */
    //нужно определииь типы нодов
    public void addNode(String name, int group, int radius, int charge, boolean stop){
        Map<String, Object> node = new LinkedHashMap<String, Object>();
        node.put("name", name);         //имя ноды
        node.put("group", group);       //тип ноды - от типа зависит изображение которое будет вешатся на ноду
        node.put("radius", radius);     //радиус ноды
        node.put("charge", charge);
        node.put("stop", stop);
        graph.get("nodes").add(node);
    }

    /*
    Добавление Линков
     */
    public void addLink(int source, int target, int width, float color, float opacity, int length, int streight){
        Map<String, Object> link = new LinkedHashMap<String, Object>();
        link.put("source", source);     //имя ноды - сорс линка
        link.put("target", target);     //имя ноды - таргет линка
        link.put("width", width);       //толщина линка

        Map<String, Integer> colors = new LinkedHashMap<>();
        Integer[] rgb = getColor(color);
        colors.put("red", rgb[0]);
        colors.put("green", rgb[1]);
        colors.put("blue", rgb[2]);
        
        link.put("color", colors);       //цвет линка
        link.put("opacity", opacity);   //прозрачность линка
        link.put("length", length);
        link.put("streight", streight);
        graph.get("links").add(link);
    }

    //получить JSON представление инстанса
    //необходимо для парсинга даты в html файле
    public String getJSON(){
        Gson gson = new Gson();
        return gson.toJson(graph);
    }

    private Integer[] getColor(float param){
        int numberOfColors = 2;
        Integer[] color;

        if (param>1 || param<0){
            throw new IllegalArgumentException("params must be 0...1");
        }else {
            color = new Integer[3];
            int deg = (int) (param * 60 * numberOfColors);
            int step = deg/60+1;
            int padding = (deg % 60)*(255-85)/60;

            switch (step){
                case 1:{
                    color[0]=255;
                    color[1]=85+padding;
                    color[2]=85;
                    break;
                }
                case 2:{
                    color[0]=255-padding;
                    color[1]=255;
                    color[2]=85;
                    break;
                }
                case 3:{
                    color[0]=85;
                    color[1]=255;
                    color[2]=85+padding;
                    break;
                }
                case 4:{
                    color[0]=85;
                    color[1]=255-padding;
                    color[2]=255;
                    break;
                }
                case 5:{
                    color[0]=85+padding;
                    color[1]=85;
                    color[2]=255;
                    break;
                }
                case 6:{
                    color[0]=255;
                    color[1]=85;
                    color[2]=255-padding;
                    break;
                }
            }
        }
        return color;
    }
}
