package graphical.controller.components;

import java.util.List;
import java.util.Objects;

import core.game.grid.Grid;
import core.game.grid.Tile;
import core.game.mechanics.Wildlife;
import graphical.model.GameStateManager;
import graphical.view.resources.*;
import graphical.view.components.*;

/**
 * Manages interactions with the graphical tile, including tile and wildlife selection,
 * and updates the tile's state based on user actions.
 */
public class GraphicalTileController implements ErrorHandler {
	private final TileImageManager tileImageManager;
	private GraphicalTile tileToPlace = null;
	private boolean waitingForTileSelection = true;
	private GraphicalTile selectedTile = null;
	private Wildlife selectedWildlife;
	private boolean waitingForTokenPlacement = false;
	private boolean showError = false;
	private boolean tilePlaced = false;
	private boolean tokenPlaced = false;

	/**
	 * Constructor for GraphicalTileController
	 * @param grid
	 * @param gameStateManager
	 */
	public GraphicalTileController(Grid grid, GameStateManager gameStateManager) {
		this.tileImageManager = new TileImageManager(gameStateManager);
	}

	/**
	 * Returns true if a turn is complete
	 */
	public boolean isTurnComplete() {
		return tilePlaced && tokenPlaced;
	}

	/**
	 * Resets the game state after a turn is completed
	 */
	public void resetTurnState() {
		tilePlaced = false;
		tokenPlaced = false;
		waitingForTileSelection = true;
		waitingForTokenPlacement = false;
		selectedTile = null;
		selectedWildlife = null;
		tileToPlace = null;
	}

	/**
	 * Sets variable to true if tile is placed on grid
	 * @param placed boolean variable describing state of tile placement
	 */
	public void setTilePlaced(boolean placed) {
		this.tilePlaced = placed;
	}

	/**
	 * Sets variable to true if token is placed on grid
	 * @param placed boolean variable describing state of tile placement
	 */
	public void setTokenPlaced(boolean placed) {
		this.tokenPlaced = placed;
	}

	/**
	 * Returns true if tile is placed on grid
	 * @return true or false if tile is not placed on grid
	 */
	public boolean isTilePlaced() {
		return tilePlaced;
	}

	/**
	 * Returns true if token is placed on grid.
	 * @return true or false if token is not placed on grid
	 */
	public boolean isTokenPlaced() {
		return tokenPlaced;
	}

	/**
	 * Detects mouse click on tile.
	 * @param tiles Displayed tiles on screen
	 * @param grid Displayed grid
	 * @param clickX x coordinate of player
	 * @param clickY y coordinate of player
	 */
	public void detectTileClick(List<GraphicalTile> tiles, Grid grid, int clickX, int clickY) {
		Objects.requireNonNull(tiles);
		Objects.requireNonNull(grid);

		if (!waitingForTileSelection)
			return;
		for (GraphicalTile tile : tiles) {
			if (tile.containsHabitat(clickX, clickY)) {
				Tile selectedTileObj = tile.getTile();
				System.out.println("User has clicked on " + selectedTileObj);
				if (Tile.isTilePlayable(grid, selectedTileObj)) {
					selectedTile = tile;
					selectedWildlife = selectedTile.getTile().getWildlife().get(0);
					waitingForTileSelection = false;
					tileToPlace = new GraphicalTile(handleSelectedTile(), tileImageManager, 150);
					break;
				} else {
					showError = true;
					System.out.println("Jeton faune implaçable.");
					break;
				}
			}
		}
	}

	/**
	 * Detects mouse click on token
	 * @param tiles Displayed tiles
	 * @param clickX X coordinate of mouse click
	 * @param clickY Y coordinate of mouse click
	 * @param selectedWildlife Wildlife selected by player
	 */
	public void detectTokenClick(List<GraphicalTile> tiles, int clickX, int clickY, Wildlife selectedWildlife) {
		Objects.requireNonNull(tiles);
		for (GraphicalTile tile : tiles) {
			if (tile.containsWildlife(clickX, clickY) && tile.getTile().getWildlife().isEmpty()) {
				System.out.println("User has clicked on " + selectedWildlife);
				waitingForTokenPlacement = true;
			}
		}
	}

	private Tile handleSelectedTile() {
		if (selectedTile != null) {
			Tile chosenTile = selectedTile.getTile();
			System.out.println("User selected : " + chosenTile);
		}
		return selectedTile.getTile();
	}

	/**
	 * Getter of selected tile
	 * @return Selected tile
	 */
	public GraphicalTile selectedTileGetter() {
		return selectedTile;
	}

	/**
	 * Getter of tile to be placed
	 * @return Tile to be placed
	 */
	public GraphicalTile tileToBePlaced() {
		return tileToPlace;
	}

	/**
	 * Getter of selected wildlife
	 * @return
	 */
	public Wildlife selectedWildlifeGetter() {
		return selectedWildlife;
	}

	/**
	 * Returns whether token is waiting to be placed
	 * @return true if token is waiting for placement
	 */
	public boolean isWaitingForTokenPlacement() {
		return waitingForTokenPlacement;
	}

	/**
	 * Toggles state of token to be placed
	 */
	public void toggleWaitingForTokenPlacement() {
		waitingForTokenPlacement = !waitingForTokenPlacement;
	}

	/**
	 * Allows to know if a selected tile can be placed or if a placement is forbidden 
	 */
	@Override
	public boolean hasError() {
		boolean error = showError;
		showError = false;
		return error;
	}

}
