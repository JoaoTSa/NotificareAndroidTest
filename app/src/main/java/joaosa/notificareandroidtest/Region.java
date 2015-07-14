package joaosa.notificareandroidtest;

public class Region {

    String name;
    double radius;
    double[] coordinates = new double[2];

    public Region(String name, double radius, double[] coordinates) {
        this.name = name;
        this.radius = radius;
        this.coordinates = coordinates;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getRadius() {
        return this.radius;
    }

    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }

    public double[] getCoordinates() {
        return this.coordinates;
    }
}
