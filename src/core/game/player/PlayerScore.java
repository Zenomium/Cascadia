package core.game.player;

import core.game.grid.Grid;
import graphical.model.GameMode;

import java.util.Map;

/**
 * Interface for score calculations depending on the game mode (Variants and
 * Standard version - In prevision of phase 2).
 */
public interface PlayerScore {
	/**
	 * Calculates and displays wildlife points.
	 * 
	 * @param grid In-game grid of player
	 * @param mode Tile shape indication
	 * @return Wildlife total score
	 */
	int wildlifeScoring(Grid grid, GameMode mode);

	/**
	 * Calculates and displays habitat points.
	 * 
	 * @param grid In-game grid of player
	 * @return Habitat total score
	 */
	int habitatScoring(Grid grid);

	/**
	 * Returns a map containing the majority bonus points by player.
	 * 
	 * @param grid1 Grid of first player
	 * @param grid Grid of second player
	 * @return Map that contains bonus points per player
	 */
	Map<String, Integer> majorityBonusPoints(Grid grid1, Grid grid);


}
