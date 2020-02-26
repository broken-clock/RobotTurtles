package srcIA;

import org.json.*;
import srcIA.src.Interface.InterfaceConsole;
import srcIA.src.LogiqueDeJeu;
import srcIA.src.Plateau;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

// Pour recuperer CartesMain, parser ce que  retourne la fonction player.playerConnector.getPlayerSecret();

public class jsonParseCreateLogiqueDeJeu {
    public static void main(String[] args) throws Exception {
        // Creer l'objet LogiqueDeJeu
        srcIA.src.LogiqueDeJeu logiqueDeJeu = new LogiqueDeJeu();
        Plateau plateauJSON = new Plateau();

        // Parser le fichier JSON
        String content = readFile();
        JSONObject obj = new JSONObject(content);

        int idGame = obj.getInt("idGame");
        System.out.println("idGame: " + idGame);
        JSONArray players = obj.getJSONArray("players");

        // Getting players info
        ArrayList<String> playersNames = new ArrayList();
        for (int i = 0; i < players.length(); i++) {
            // Foreach player
            JSONObject player = players.getJSONObject(i);

            String playerName = player.getString("playerName");
            playersNames.add(playerName);
            System.out.println(playerName);
            int mursDePierre = 0;
            int mursDeGlace = 0;

            JSONArray tiles = player.getJSONArray("tiles");
            for (int j = 0; j < tiles.length(); j++) {
                String tileName = tiles.getJSONObject(j).getString("panelName");
                if (tileName.equals("WALL")) mursDePierre++;
                else if (tileName.equals("ICE")) mursDeGlace++;
            }
            System.out.println("murs de glace: " + mursDeGlace);
            System.out.println("murs de pierre: " + mursDePierre);

            String direction = player.getString("direction");
            System.out.println(direction);

            JSONObject positionInitiale = player.getJSONObject("initialPosition");
            int x_initial = positionInitiale.getInt("line");
            int y_initial = positionInitiale.getInt("column");
            System.out.println("position initiale: " + x_initial + "; " + y_initial);

            boolean rubyReached = player.getBoolean("rubyReached");
            System.out.println("Ruby reached: " + rubyReached);


            System.out.println("\n");
        }

        // Parsing the GRID (plateau de jeu)
        JSONObject gridFull = obj.getJSONObject("grid");
        JSONArray grid = gridFull.getJSONArray("grid");
        for (int i = 0; i < grid.length(); i++) {
            JSONArray gridLine = grid.getJSONArray(i);
            for (int j = 0; j < gridLine.length(); j++) {
                String caseName = gridLine.getJSONObject(j).getString("panelName");
                // Si la case est un joueur, on récupère la position courante de ce joueur
                if (caseName.equals("PLAYER")) {
                    String playerName = gridLine.getJSONObject(j).getString("playerName");
                    int x_courant = i;
                    int y_courant = j;
                    System.out.println("Joueur: " + playerName + ": posCourante_x: " + x_courant + "; posCourante_y: " + y_courant);
                }

                plateauJSON.setCase(i, j, transform(caseName));
            }
        }

        // Creer le plateau en fonction des donnees recuperees du json
        logiqueDeJeu.setPlateau(plateauJSON);
        afficherPlateau(logiqueDeJeu);


    }

    public static String transform(String inputString) {
        String outputString = "";
        switch (inputString) {
            case "EMPTY":
            case "PLAYER":
            case "RUBY":
                outputString = null;
                break;
            case "WALL":
                outputString = "p";
                break;
            case "ICE":
                outputString = "g";
                break;
        }
        return outputString;
    }

    public static String readFile() throws Exception {
        // Read json file
        File file = new File("gameState.json");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st, content = "";
        while ((st = br.readLine()) != null) {
            content += st;
        }
        return content;
    }

    public static void afficherPlateau(LogiqueDeJeu logiqueDeJeu) {
        int taillePlateau = logiqueDeJeu.getPlateau().getTaillePlateau();
        for (int i = 0; i < taillePlateau; i++) {
            for (int j = 0; j < taillePlateau; j++) {
                System.out.print(logiqueDeJeu.getPlateau().getCase(i, j) == null ? "." : logiqueDeJeu.getPlateau().getCase(i, j));
                System.out.print("\t");
            }
            System.out.println();
        }
    }
}
