package src.Cartes;

import java.lang.reflect.Type;

public class Carte {
    private TypeCarte typeCarte;

    public Carte(TypeCarte _TypeCarte) {
        typeCarte = _TypeCarte;
    }

    public TypeCarte getTypeCarte() {
        return this.typeCarte;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Carte)) return false;
        return this.typeCarte == ((Carte) o).typeCarte;
    }
}
