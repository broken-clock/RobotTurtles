package src.Tuiles;

public enum Orientations {
    UP,
    LEFT,
    DOWN,
    RIGHT;

    public static Orientations getOrientationSuivante(Orientations orientation) {
        Orientations[] orientations = Orientations.values();
        int index = orientation.ordinal();
        int indexSuivant = index + 1;
        indexSuivant %= orientations.length;
        return orientations[indexSuivant];
    }

    public static Orientations getOrientationPrecedente(Orientations orientation) {
        Orientations[] orientations = Orientations.values();
        int index = orientation.ordinal();
        int indexSuivant = index - 1;
        indexSuivant %= orientations.length;
        return orientations[indexSuivant];
    }
}
