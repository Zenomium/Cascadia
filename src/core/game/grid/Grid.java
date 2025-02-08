package core.game.grid;

import core.game.player.Player;
import core.game.player.StandardScoring;
import graphical.model.GameMode;
import core.game.mechanics.Deck;
import core.game.mechanics.Habitat;
import core.game.mechanics.Wildlife;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a grid of Cascadia
 */
public class Grid {
    private final Tile[][] list;
    private final int size;
    private final Player player;
    private final Deck deck;
    private final GameMode gameMode;

    /**
     * Constructor of Cascadia grid
     * @param size Size of grid
     * @param deck In-game deck
     * @param player Player on grid
     * @param gameMode Current game mode
     */
    public Grid(int size, Deck deck, Player player, GameMode gameMode) {
        this.size = size;
        this.deck = deck;
        this.list = new Tile[size][size];
        this.player = player;
        this.gameMode = gameMode;
        initializeGrid();
    }

    private void initializeGrid() {       
        // Placer la tuile de départ en forme de L
        Tile tuile1 = deck.drawTiles(1).get(0);
        addTile(tuile1, 0, 0, true);
        Tile tuile2 = deck.drawTiles(1).get(0);
        addTile(tuile2, 0, 1, true);
        Tile tuile3 = deck.drawTiles(1).get(0);
        addTile(tuile3, 1, 0, true);
    }

	/**
	 * Adds tile on grid
	 * 
	 * @param newTile       Tile to be placed on grid
	 * @param x             X coordinate of grid
	 * @param y             Y coordinate of grid
	 * @param startingTiles Boolean used to distinguish between Starter tiles and
	 *                      tiles placed by players
	 * @return Returns true if the tile is placed or not
	 */
    public boolean addTile(Tile newTile, int x, int y, boolean startingTiles) {
    	NeighborStrategy strategy;
    	if (gameMode == GameMode.GRAPHICAL_HEXAGON_TILES) {
    	    // Déterminer si la colonne est impaire ou paire
    	    boolean isOddColumn = (x % 2 != 0); // 'x' est la coordonnée de la colonne
    	    strategy = isOddColumn ? new HexNeighborsOdd() : new HexNeighborsPair();
    	} else {
    	    strategy = new AllNeighbors();
    	}

            
        if (list[x][y] == null && (startingTiles || nonEmptyNeighbor(x, y, strategy))) {
            newTile.getWildlife().clear();
            list[x][y] = newTile;
            return true;
        }
        return false;
    }

    // Modification de countWildlifeGroup pour utiliser la bonne stratégie
    private int countWildlifeGroup(Grid grid, int x, int y, Wildlife wildlifeType, boolean[][] visited) {
    	NeighborStrategy strategy;
    	if (gameMode == GameMode.GRAPHICAL_HEXAGON_TILES) {
    	    // Déterminer si la colonne est impaire ou paire
    	    boolean isOddColumn = (x % 2 != 0); // 'x' est la coordonnée de la colonne
    	    strategy = isOddColumn ? new HexNeighborsOdd() : new HexNeighborsPair();
    	} else {
    	    strategy = new AllNeighbors();
    	}


        if (x < 0 || y < 0 || x >= grid.getSize() || y >= grid.getSize()) {
            return 0;
        }
        
        Tile tile = grid.getTile(x, y);
        if (visited[x][y] || tile == null || tile.getWildlife().isEmpty() || 
            tile.getWildlife().get(0) != wildlifeType) {
            return 0;
        }

        visited[x][y] = true;
        int groupSize = 1;

        for (int[] neighbor : strategy.getNeighbors()) {
            groupSize += countWildlifeGroup(grid, x + neighbor[0], y + neighbor[1], 
                wildlifeType, visited);
        }

        return groupSize;
    }

    // Modification de countHabitatGroup pour utiliser la bonne stratégie
    private int countHabitatGroup(Grid grid, int x, int y, Habitat habitatType, boolean[][] visited) {
    	NeighborStrategy strategy;
    	if (gameMode == GameMode.GRAPHICAL_HEXAGON_TILES) {
    	    // Déterminer si la colonne est impaire ou paire
    	    boolean isOddColumn = (x % 2 != 0); // 'x' est la coordonnée de la colonne
    	    strategy = isOddColumn ? new HexNeighborsOdd() : new HexNeighborsPair();
    	} else {
    	    strategy = new AllNeighbors();
    	}

        if (x < 0 || y < 0 || x >= grid.getSize() || y >= grid.getSize()) {
            return 0;
        }
        
        Tile tile = grid.getTile(x, y);
        if (visited[x][y] || tile == null || tile.getHabitat() != habitatType) {
            return 0;
        }

        visited[x][y] = true;
        int groupSize = 1;

        for (int[] neighbor : strategy.getNeighbors()) {
            groupSize += countHabitatGroup(grid, x + neighbor[0], y + neighbor[1], 
                habitatType, visited);
        }

        return groupSize;
    }

    public int countSurroundingSpecies(int x, int y) {
    	NeighborStrategy strategy;
    	if (gameMode == GameMode.GRAPHICAL_HEXAGON_TILES) {
    	    // Déterminer si la colonne est impaire ou paire
    	    boolean isOddColumn = (x % 2 != 0); // 'x' est la coordonnée de la colonne
    	    strategy = isOddColumn ? new HexNeighborsOdd() : new HexNeighborsPair();
    	} else {
    	    strategy = new AllNeighbors();
    	}

            
        Set<Wildlife> uniqueSpecies = new HashSet<>();

        for (int[] neighbor : strategy.getNeighbors()) {
            int nx = x + neighbor[0];
            int ny = y + neighbor[1];

            if (nx >= 0 && ny >= 0 && nx < size && ny < size) {
                Tile neighborTile = getTile(nx, ny);
                if (neighborTile != null && !neighborTile.getWildlife().isEmpty()) {
                    uniqueSpecies.add(neighborTile.getWildlife().get(0));
                }
            }
        }

        return uniqueSpecies.size();
    }



	/**
	 * Tile on grid getter
	 * 
	 * @param x X coordinate on grid
	 * @param y Y coordinate on grid
	 * @return Tile on X and Y coordinates
	 */
	public Tile getTile(int x, int y) {
		return list[x][y];
	}

	/**
	 * Size of grid getter
	 * 
	 * @return Size of grid in Integer
	 */
	public int getSize() {
		return this.size;
	}

	/**
	 * Player on grid getter
	 * 
	 * @return String of player's name
	 */
	public String getPlayerOnGrid() {
		return player.getName();
	}

	/**
	 * Displaying method of grid
	 */
	public void displayGrid() {
		int habitatLength = 8; // Longueur d'affichage pour le nom de l'habitat
		int caseWidth = 15; // Largeur d'une case
		NeighborStrategy strategy = new AllNeighbors(); // Verifie en 8
		System.out.println("Grille " + player.getName() + " :");
		for (int i = 0; i < list.length; i++) {
			for (int j = 0; j < list[i].length; j++) {
				if (list[i][j] != null) { // Si une tuile est placée
					Tile tile = list[i][j];
					authorizedWildlifeFormattedDisplay(tile, habitatLength);
				} else if (nonEmptyNeighbor(i, j, strategy)) {
					// Case vide mais voisine à une tuile non vide
					System.out.print("\u001B[1;32m<" + String.format("%-" + caseWidth + "s", "") + ">\u001B[0m ");
				} else {
					// Case vide et non voisine
					System.out.print("[" + String.format("%-" + caseWidth + "s", "") + "] ");
				}
			}
			System.out.println(); // Passe à la ligne suivante
		}
	}

	/**
	 * Formats the display of authorized wildlives on grid
	 * 
	 * @param tile   Tile without wildlife
	 * @param length Formatting size of tile
	 */
	public void authorizedWildlifeFormattedDisplay(Tile tile, int length) {
		StringBuilder wildlifeBuilder = new StringBuilder();
		if (!tile.getWildlife().isEmpty()) {
			Wildlife animal = tile.getWildlife().get(0);
			System.out.print("[" + String.format("%-" + length + "s", tile.getHabitat()) + "\u001B[33m(  "
					+ animal.name().charAt(0) + "  )\u001B[0m] ");
		} else {
			for (Wildlife animal : tile.getAuthorizedWildlife()) {
				if (wildlifeBuilder.length() > 0)
					wildlifeBuilder.append(",");
				wildlifeBuilder.append(animal.name().charAt(0));
			}
			String wildlife = wildlifeBuilder.toString();
			String fill = wildlife.length() == 1 ? "  " : wildlife.length() == 3 ? " " : "";
			System.out
					.print("[" + String.format("%-" + length + "s", tile.getHabitat()) + "[" + fill + wildlife + fill + "]] ");
		}
	}

	// Method to verify non empty neighbors of a tile
	public boolean nonEmptyNeighbor(int x, int y, NeighborStrategy neighborStrategy) {
		for (int[] neighbor : neighborStrategy.getNeighbors()) {
			int nx = x + neighbor[0];
			int ny = y + neighbor[1];

			// Vérifier que les coordonnées sont valides
			if (nx >= 0 && ny >= 0 && nx < list.length && ny < list[0].length) {
				if (list[nx][ny] != null) {
					return true; // Il y a un voisin non vide
				}
			}
		}
		return false; // Aucun voisin non vide trouvé
	}

	/**
	 * Returns a map containing wildlives as keys and a list of the different group
	 * sizes of wildlife on the grid as values
	 * 
	 * @param grid In-game grid
	 * @return {@code Map<List<String>, List<Integer>>}
	 */
	public Map<Wildlife, List<Integer>> countWildlifeInGrid(Grid grid) {
		Map<Wildlife, List<Integer>> wildlifeGroups = new HashMap<>();
		boolean[][] visited = new boolean[size][size];

		for (int i = 0; i < grid.getSize(); i++) {
			for (int j = 0; j < grid.getSize(); j++) {
				Tile tile = grid.getTile(i, j);
				if (tile != null && !tile.getWildlife().isEmpty()) {
					Wildlife wildlife = tile.getWildlife().get(0);
					if (!visited[i][j]) {
						int groupSize = countWildlifeGroup(grid, i, j, wildlife, visited);
						wildlifeGroups.putIfAbsent(wildlife, new ArrayList<>());
						wildlifeGroups.get(wildlife).add(groupSize);
					}
				}
			}
		}

		return wildlifeGroups;
	}

	/**
	 * Renvoie le nombre total d'occurrences où l'espèce spécifiée est autorisée
	 * dans la grille.
	 *
	 * @param grid     : Grille de jeu
	 * @param wildlife : Espèce dont on veut compter le nombre
	 * @return int : Nombre d'occurrences
	 */
	public boolean countAuthorizedWildlifeInGrid(Grid grid, Wildlife wildlife) {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				Tile tile = grid.getTile(i, j);
				if (tile != null && tile.getAuthorizedWildlife().contains(wildlife)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns a map containing habitats as keys and a list containing the different
	 * group sizes of habitat as values
	 * 
	 * @param grid : Grille
	 */
	public Map<Habitat, List<Integer>> countHabitatInGrid(Grid grid) {
		Map<Habitat, List<Integer>> habitatGroups = new HashMap<>();
		boolean[][] visited = new boolean[size][size];

		for (int i = 0; i < grid.getSize(); i++) {
			for (int j = 0; j < grid.getSize(); j++) {
				Tile tile = grid.getTile(i, j);
				if (tile != null && tile.getHabitat() != null) {
					Habitat habitat = tile.getHabitat();
					if (!visited[i][j]) {
						int groupSize = countHabitatGroup(grid, i, j, habitat, visited);

						habitatGroups.putIfAbsent(habitat, new ArrayList<>());
						habitatGroups.get(habitat).add(groupSize);
					}
				}
			}
		}

		return habitatGroups;
	}
	
	/**
	 * Calcule les points pour un groupe de renards en fonction des espèces voisines uniques.
	 */
	public int countIndividualFoxPoints() {
	    int totalPoints = 0;
	    boolean[][] visited = new boolean[size][size];

	    for (int x = 0; x < size; x++) {
	        for (int y = 0; y < size; y++) {
	            Tile tile = getTile(x, y);
	            if (tile != null && !visited[x][y] && !tile.getWildlife().isEmpty() 
	                && tile.getWildlife().get(0) == Wildlife.Renard) {
	                
	                visited[x][y] = true;
	                NeighborStrategy strategy;
	                if (gameMode == GameMode.GRAPHICAL_HEXAGON_TILES) {
	                // Déterminer si la colonne est impaire ou paire
	                boolean isOddColumn = (x % 2 != 0); // 'x' est la coordonnée de la colonne
	                strategy = isOddColumn ? new HexNeighborsOdd() : new HexNeighborsPair();
	                } else {
	                strategy = new AllNeighbors();
	                }
	                // Compte les espèces voisines uniques
	                int surroundingSpeciesCount = countSurroundingSpecies(x, y, strategy);

	                // Utilise la variante pour calculer les points
	                totalPoints += StandardScoring.calculateFoxesPoints(surroundingSpeciesCount);
	            }
	        }
	    }
	    return totalPoints;
	}

	/**
	 * Compte les espèces uniques entourant une tuile spécifique.
	 *
	 * @param x              Coordonnée X de la tuile
	 * @param y              Coordonnée Y de la tuile
	 * @param neighborStrategy Stratégie pour définir les voisins
	 * @return Nombre d'espèces uniques autour de la tuile
	 */
	public int countSurroundingSpecies(int x, int y, NeighborStrategy neighborStrategy) {
	    Set<Wildlife> uniqueSpecies = new HashSet<>();

	    for (int[] neighbor : neighborStrategy.getNeighbors()) {
	        int nx = x + neighbor[0];
	        int ny = y + neighbor[1];

	        // Vérifie que les coordonnées sont valides
	        if (nx >= 0 && ny >= 0 && nx < size && ny < size) {
	            Tile neighborTile = getTile(nx, ny);
	            if (neighborTile != null && !neighborTile.getWildlife().isEmpty()) {
	                uniqueSpecies.add(neighborTile.getWildlife().get(0));
	            }
	        }
	    }

	    return uniqueSpecies.size();
	}

}