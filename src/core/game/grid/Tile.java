package core.game.grid;

import java.io.IO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import core.game.mechanics.Habitat;
import core.game.mechanics.Wildlife;

/**
 * Represents a tile on the grid.
 */
public class Tile {
	private final Habitat habitat; // Forêt, Montagne ou Rivière
	private final List<Wildlife> wildlifeTokens; // Liste d'un seul animal (voir addWildlife)
	private final List<Wildlife> AuthorizedWildlife; // Liste des animaux posables sur la tuile

	/**
	 * Creates a new tile with a habitat and a wildlife token.
	 * 
	 * @param habitat        Habitat of tile
	 * @param wildlifeTokens Wildlife token of tile
	 */
	public Tile(Habitat habitat, List<Wildlife> wildlifeTokens) {
		this.habitat = Objects.requireNonNull(habitat, "Habitat cannot be null");
		;
		this.wildlifeTokens = new ArrayList<>(Objects.requireNonNull(wildlifeTokens, "Wildlife tokens cannot be null"));
		this.AuthorizedWildlife = new ArrayList<>();
	}

	/**
	 * Getter of habitat of the tile.
	 * 
	 * @return Habitat tile
	 */
	public Habitat getHabitat() {
		return habitat;
	}

	/**
	 * Getter of wildlife of the tile.
	 * 
	 * @return List of wildlife of the tile
	 */
	public List<Wildlife> getWildlife() {
		return wildlifeTokens;
	}

	/**
	 * Getter of the authorizezd wildlife of the tile.
	 * 
	 * @return List of authorized of the tile
	 */
	public List<Wildlife> getAuthorizedWildlife() {
		return AuthorizedWildlife;
	}

	/**
	 * Adds wildlife to a tile, and checks if authorized or not.
	 * 
	 * @param wildlife           wildlife to be added on the grid
	 * @param authorizedWildlife list of authorized wildlife
	 * @return True if wildlife is added to tile.
	 */
	public boolean addWildlife(Wildlife wildlife, List<Wildlife> authorizedWildlife) {
		Objects.requireNonNull(wildlife, "wildlife cannot be null");
		Objects.requireNonNull(authorizedWildlife, "authorizedWildlife cannot be null");
		if (wildlifeTokens.size() == 0) {
			for (Wildlife wildlifeAutho : authorizedWildlife) {
				if (wildlife.equals(wildlifeAutho)) {
					wildlifeTokens.add(wildlife);
					return true; // habitat autorisé
				}
			}
			return false; // habitat non autorisé
		} else {
			return false; // Déjà un animal sur la tuile
		}
	}

	/**
	 * Adds specified wildlife to the list of authorized wildlife.
	 * 
	 * @param wildlife wildlife to be added to list of authorized wildlife
	 */
	public void addAuthorizedWildlife(Wildlife wildlife) {
		Objects.requireNonNull(wildlife, "wildlife cannot be null");
		if (!AuthorizedWildlife.contains(wildlife)) {
			AuthorizedWildlife.add(wildlife);
		}
	}

	/**
	 * Returns tile chosen by player and verifies if it is valid for placement.
	 * 
	 * @param grid           Grid of player
	 * @param availableTiles List of available tiles for player
	 * @return Tile chosen by player
	 */
	public static Tile chooseTile(Grid grid, List<Tile> availableTiles) {
		Objects.requireNonNull(grid, "grid cannot be null");
		Objects.requireNonNull(availableTiles, "availableTiles cannot be null");

		while (true) {
			try {
				int input = Integer.parseInt(IO.readln("")) - 1;
				if (input >= 0 && input < availableTiles.size() && availableTiles.get(input) != null) {
					Tile chosenTile = availableTiles.get(input);
					if (isTilePlayable(grid, chosenTile)) { // vérifie si la tuile choisie peut être placée sur la
						// grille
						availableTiles.set(input, null); // Marque la tuile comme jouée
						return chosenTile;
					}
					System.out.println("Jeton faune implaçable.");
				}
				System.out.println(
						"Choix invalide, sélectionnez une tuile parmi celles affichées (1-" + availableTiles.size() + ") :");
			} catch (NumberFormatException e) {
				System.out.println("/!\\ Entrez un nombre entier valide /!\\");
			}

		}
	}

	/**
	 * Returns whether a tile is playable on the grid or not.
	 * 
	 * @param grid       In-game grid
	 * @param chosenTile Tile chosen by player
	 * @return Boolean that indicates if tile is playable or not
	 */
	public static boolean isTilePlayable(Grid grid, Tile chosenTile) {
//		Objects.requireNonNull(grid, "grid cannot be null");
		Objects.requireNonNull(chosenTile, "chosenTile cannot be null");
		if (grid.countAuthorizedWildlifeInGrid(grid, chosenTile.getWildlife().get(0))) {
			return true;
		}
		return false;
	}

	/**
	 * Calculates the maximum count of any wildlife type in the selected tiles.
	 * 
	 * @param selectedTiles List of tiles containing wildlives
	 * @return Maximum count of any wildlife type found in the selected tiles
	 */
	public static int getMaxCombinationCount(List<Tile> selectedTiles) {
		Objects.requireNonNull(selectedTiles, "selectedTiles cannot be null");
		// Crée un regroupement des tuiles par type d'animal et compte les occurrences
		Map<List<Wildlife>, Long> counts = selectedTiles.stream().filter(Objects::nonNull)
				.collect(Collectors.groupingBy(Tile::getWildlife, // Regrouper par type d'animal
						Collectors.counting() // Compter les occurrences
				));

		// Trouve la valeur maximale des comptes
		return counts.values().stream().mapToInt(Long::intValue) // Convertir en int pour compatibilité
				.max().orElse(0); // Retourne 0 si aucune tuile n'est présente
	}

	/**
	 * Returns a string representation of a tile.
	 */
	@Override
	public String toString() {
		return "Paysage: " + getHabitat() + ", Faune autorisée: " + getAuthorizedWildlife() + ", Animal: " + getWildlife();
	}

}
