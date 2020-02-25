package src.Tuiles;

public class Obstacle extends Tuile {
    private String typeObstacle;
    private int[] coordsObstacle;

    public Obstacle(String _typeObstacle, int[] _coordsObstacle) {
        typeObstacle = _typeObstacle;
        coordsObstacle = _coordsObstacle;
    }

    public String getTypeObstacle() {
        return typeObstacle;
    }

    public int[] getCoordsObstacle() {
        return coordsObstacle;
    }
}
