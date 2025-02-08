package core.game.mechanics;

import core.game.grid.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Represents the draw pile in the game. The class manages the collection of
 * tokens and tiles players can draw from during a game.
 */
public class Deck {
	private final List<Tile> tiles;

	/**
	 * Creates a new draw pile and initialises a new set of items.
	 */
	public Deck() {
		tiles = new ArrayList<>();
		Random rand = new Random();

		// Génération de 43 tuiles aléatoires
		for (int i = 0; i < 1000; i++) {
			// Sélection aléatoire de l'habitat
			Habitat habitat = Habitat.values()[rand.nextInt(Habitat.values().length)];

			// Sélection aléatoire d'un animal placé
			List<Wildlife> wildlifeTokens = new ArrayList<>();
			wildlifeTokens.add(Wildlife.values()[rand.nextInt(Wildlife.values().length)]);

			// Création de la tuile avec habitat et animal placé
			Tile tile = new Tile(habitat, wildlifeTokens);

			List<Wildlife> authorizedWildlife = new ArrayList<>();
			while (authorizedWildlife.size() < 2) { // 2 animaux autorisés par tuile
				Wildlife randomWildlife = Wildlife.values()[rand.nextInt(Wildlife.values().length)];
				if (!authorizedWildlife.contains(randomWildlife)) {
					authorizedWildlife.add(randomWildlife);
				}
			}

			// Ajouter les animaux autorisés à la tuile
			for (var wildlife : authorizedWildlife) {
				tile.addAuthorizedWildlife(wildlife);
			}

			// Ajouter la tuile à la liste des tuiles
			tiles.add(tile);
		}

		// Mélanger la pioche
		Collections.shuffle(tiles);
	}

	/**
	 * Allows to draw n tiles from the pile.
	 * 
	 * @param num The number of tiles to draw.
	 * @return List of drawn tiles
	 */
	public List<Tile> drawTiles(int num) {
		List<Tile> drawnTiles = new ArrayList<>();
		for (int i = 0; i < num && !tiles.isEmpty(); i++) {
			Tile originalTile = tiles.remove(0);
			// Création d'une copie de la tuile pour éviter des conflits
			Tile copiedTile = new Tile(originalTile.getHabitat(), new ArrayList<>(originalTile.getWildlife()));
			for (var authorized : originalTile.getAuthorizedWildlife()) {
				copiedTile.addAuthorizedWildlife(authorized);
			}
			drawnTiles.add(copiedTile);
		}
		return drawnTiles;
	}

	/**
	 * Getter method for the list of tiles.
	 * 
	 * @return List containing the tiles
	 */
	public List<Tile> getTiles() {
		return tiles;
	}
}