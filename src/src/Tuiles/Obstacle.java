package src.Tuiles;

public class Obstacle extends Tuile {
    private String typeObstacle;
    private int[] coordsObstacle;

    public String getTypeObstacle() {
        return typeObstacle;
    }

    public int[] getCoordsObstacle() {
        return coordsObstacle;
    }

    public Obstacle(String _typeObstacle, int[] _coordsObstacle) {
        typeObstacle = _typeObstacle;
        coordsObstacle = _coordsObstacle;
    }

    public boolean poseValide() {
        return true;
    }
}
