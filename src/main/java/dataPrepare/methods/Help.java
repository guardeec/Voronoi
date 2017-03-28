package dataPrepare.methods;

import dataPrepare.data.graph.Graph;
import dataPrepare.data.graph.Host;
import javafx.scene.paint.Color;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by guardeec on 17.03.17.
 */
public class Help {
    public static List<Color> allColors() throws ClassNotFoundException, IllegalAccessException {
        List<Color> colors = new ArrayList<>();
        Class clazz = Class.forName("javafx.scene.paint.Color");
        if (clazz != null) {
            Field[] field = clazz.getFields();
            for (int i = 0; i < field.length; i++) {
                Field f = field[i];
                Object obj = f.get(null);
                if(obj instanceof Color){
                    colors.add((Color) obj);
                }

            }
        }
        return colors;
    }

    private static boolean checkConnectionInGraph(Graph graph){
        Host startHost = graph.getHosts().get(0);
        List<Host> relatedHosts = graph.getRelations(startHost);
        startHost.setRadius(2);

        checkConnectionInGraphLoop(graph, relatedHosts);

        boolean graphIsConnected = true;
        for (Host host : graph.getHosts()){
            if (host.getRadius()<2){
                graphIsConnected = false;
            }
        }

        for (Host host : graph.getHosts()){
            host.setRadius(1);
        }
        return graphIsConnected;
    }

    private static void checkConnectionInGraphLoop(Graph graph, List<Host> relatedHosts){
        Iterator<Host> iterator = relatedHosts.iterator();
        while (iterator.hasNext()){
            Host host = iterator.next();
            if (host.getRadius()<2){
                host.setRadius(2);
                checkConnectionInGraphLoop(graph, graph.getRelations(host));
            }
        }
    }
}
