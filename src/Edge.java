import java.util.*;

public class Edge {

    private TreeSet<String> vertices;
    private String srcCardnality;
    private String destCardnality;

    public String getSrcCardnality() {
        return srcCardnality;
    }

    public void setSrcCardnality(String srcCardnality) {
        this.srcCardnality = srcCardnality;
    }

    public String getDestCardnality() {
        return destCardnality;
    }

    public void setDestCardnality(String destCardnality) {
        this.destCardnality = destCardnality;
    }

    public Edge() {

        this.vertices = new TreeSet<String>();
    }

    public Set<String> getVertices() {
        return vertices;
    }

    public void setVertices(TreeSet<String> vertices) {
        this.vertices = vertices;
    }

    public void addVertex(String vertex) {
        this.vertices.add(vertex);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Edge)) return false;

        Edge edge = (Edge) o;

        return getVertices().equals(edge.getVertices());
    }

    @Override
    public int hashCode() {
        return getVertices().hashCode();
    }
}
