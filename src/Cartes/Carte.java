package src.Cartes;

import java.lang.reflect.Type;

public class Carte {
    private TypeCarte typeCarte;

    public Carte(TypeCarte _TypeCarte) {
        typeCarte = _TypeCarte;
    }

    public TypeCarte getTypeCarte() {
        return TypeCarte.CARTE_BLEUE;
    }
}
