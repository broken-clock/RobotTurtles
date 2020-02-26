package srcIA.src.Cartes;

public class Carte {
    private TypeCarte typeCarte;

    public Carte(TypeCarte _TypeCarte) {
        typeCarte = _TypeCarte;
    }

    public TypeCarte getTypeCarte() {
        return this.typeCarte;
    }

    // Permet de définir la relation d'égalité entre deux objets de type Carte
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Carte)) return false;
        return this.typeCarte == ((Carte) o).typeCarte;
    }
}
