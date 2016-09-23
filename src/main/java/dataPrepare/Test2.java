package dataPrepare;

import dataPrepare.data.graph.Graph;
import dataPrepare.data.triangulation.Triangle;
import dataPrepare.data.triangulation.TriangleVoronoiImpl;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;

import java.util.List;

/**
 * Created by guardeec on 11.07.16.
 */
public class Test2 {
    private static Test2 ourInstance = new Test2();

    public static Test2 getInstance() {
        return ourInstance;
    }

    private Test2() {
    }

    private Group group;
    private Graph graph;
    private ObservableList<Node> ol;
    private List<TriangleVoronoiImpl> triangles;

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public ObservableList<Node> getOl() {
        return ol;
    }

    public void setOl(ObservableList<Node> ol) {
        this.ol = ol;
    }

    public List<TriangleVoronoiImpl> getTriangles() {
        return triangles;
    }

    public void setTriangles(List<TriangleVoronoiImpl> triangles) {
        this.triangles = triangles;
    }
}
