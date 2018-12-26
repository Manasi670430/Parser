public class CommonEdge {

    private String source;
    private String destination;

    public CommonEdge(String source, String destination) {
        this.source = source;
        this.destination = destination;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof CommonEdge)) return false;

        CommonEdge that = (CommonEdge) o;

        if (getSource() != null ? !getSource().equals(that.getSource()) : that.getSource() != null) return false;
        return getDestination() != null ? getDestination().equals(that.getDestination()) : that.getDestination() == null;
    }

    @Override
    public int hashCode() {
        int result = getSource() != null ? getSource().hashCode() : 0;
        result = 31 * result + (getDestination() != null ? getDestination().hashCode() : 0);
        return result;
    }

    public String getSource() {

        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
