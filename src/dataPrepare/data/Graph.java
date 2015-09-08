package dataPrepare.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anna on 27.08.15.
 */
public class Graph {
    private List<Host> nodes = new ArrayList<>();
    private List<List<Boolean>> links = new ArrayList<>();

    public Graph() {
    }

    public Graph (Graph graph){
        for (int i=0; i<graph.getHostsNumber(); i++){
            float x = graph.getHost(i).getX();
            float y = graph.getHost(i).getY();
            setHost(x, y);
        }
        for (int i=0; i<graph.getHostsNumber(); i++){
            int[] relations = graph.getRelations(i);
            for (int q=0; q<relations.length; q++){
                setRelations(i, relations[q]);
            }
        }
    }

    /*
    Создать хост
    */
    public void setHost(float x, float y){
        //добавляем новый столбец
        List<Boolean> hostRelations = new ArrayList<>();
        for (int i=0; i<nodes.size(); i++){
            hostRelations.add(false);
        }
        links.add(hostRelations);
        //добавляем новую строку
        for (int i=0; i<links.size(); i++){
            links.get(i).add(false);
        }
        //добавляем отношение нового хоста самого в себя
        links.get(links.size()-1).set(links.size()-1, true);

        //добавлем хост
        Host host = new Host(nodes.size(), x, y);
        nodes.add(host);
    }


    /*
    Вернёт хост
     */
    public Host getHost(int id){
        Host host = nodes.get(id);
        return host;
    }
    public Integer getHostId(Host host){
        for (int i=0; i<nodes.size(); i++){
            if (nodes.get(i) == host){
                return i;
            }
        }
        return null;
    }
    public List<Host> getAllHosts(){
        return nodes;
    }
    //вернёт хост по координатам
    public Integer getHostId(int x, int y){
        for (int i=0; i<nodes.size(); i++){
            if (nodes.get(i).getX()==x && nodes.get(i).getY()==y){
                return i;
            }
        }
        return null;
    }
    public Host getHost(int x, int y){
        int id = getHostId(x, y);
        return getHost(id);
    }


    /*
    Назначить связи хосту
     */
    public void setRelations(int fromId, int toId){
        if(fromId != toId && getHost(fromId) != null && getHost(toId)!=null){
            links.get(fromId).set(toId, true);
            links.get(toId).set(fromId, true);
        }
    }
    public void setRelations(Host from, Host to){
        int fromId = getHostId(from);
        int toId = getHostId(to);
        setRelations(fromId, toId);
    }

    /*
    Вернуть связи хоста
     */
    public int[] getRelations(int id){
        int relationsNumber = 0;
        for (int i=0; i<links.size(); i++){
             if(links.get(id).get(i)){
                 relationsNumber++;
             }
        }
        int[] relations = new int[relationsNumber];
        for (int i=0; i<links.size(); i++){
            if(links.get(id).get(i)){
                relations[relationsNumber-1]=i;
                relationsNumber--;
            }
        }
        return relations;
    }

    public List<List<Boolean>> getAllRelations(){
        return links;
    }


    //Вернуть количество хостов
    public Integer getHostsNumber(){
        return nodes.size();
    }

    //Вернуть количество связей
    public Integer getRelationNumber(){
        int counter =0;
        for (int i=0; i<getHostsNumber(); i++){
            counter+=getRelations(i).length-1;
        }
        return counter;
    }

    //Вывод графа в консоль:
    public void printGraphToConsole(){
        List<List<Boolean>> matrix = getAllRelations();

        for (int i=0; i<getHostsNumber(); i++){
            int[] relations = getRelations(i);
            System.out.print("Host number "+i+ ": ");
            for (int q=0; q<relations.length; q++){
                System.out.print(relations[q]+" ");
            }
            System.out.println();

        }
        System.out.println("Graph Matrix:");
        for (int i=0; i<matrix.size(); i++){
            for (int q=0; q<matrix.size(); q++){
                if(matrix.get(i).get(q)){
                    System.out.print(1+" ");
                }else {
                    System.out.print(0+" ");
                }

            }
            System.out.println();
        }
    }
}
