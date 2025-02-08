package core.game;

import core.game.grid.*;
import core.game.player.*;
import graphical.model.GameMode;
import terminal.ui.GameUI;
import core.game.mechanics.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Defines the game logic of Cascadia.
 */
public class Game {
	private final Deck deck;
	private final PositionInput input;
	private GameVariant variant;
	private PlayerScore scoring;
	private final GameUI gameUI;
	private final List<Player> players;
	private final Map<Player, Grid> playerGrids;

	/**
	 * Constructs a new game instance.
	 */
	public Game(int variantNumber) {
		this.deck = new Deck();
		this.variant = new GameVariant(variantNumber);
		input = new PositionInput(0, 0);
		this.gameUI = new GameUI(this);
		this.players = new ArrayList<>();
		this.playerGrids = new HashMap<>();
	}

	/**
	 * Constructor overload for default variant.
	 */
	public Game() {
		this(1);
	}

	/**
	 * Adds a player to the list of players in-Game
	 * 
	 * @param playerName Name of player
	 */
	public void addPlayer(String playerName, GameMode gamemode) {
		Player player = new Player(playerName);
		players.add(player);
		Grid grid = new Grid(5, deck, player, gamemode);
		playerGrids.put(player, grid);
	}

	/**
	 * Grid getter per player
	 * 
	 * @param player Player's grid
	 * @return Grid of a specific player
	 */
	public Grid getGridForPlayer(Player player) {
		return playerGrids.get(player);
	}

	/**
	 * Returns a list that contains all players in-Game
	 * 
	 * @return List of all players
	 */
	public List<Player> getPlayers() {
		return Collections.unmodifiableList(players);
	}

	/**
	 * Returns all grids used In-game
	 * 
	 * @return All grids
	 */
	public Map<Player, Grid> getGrids() {
		return playerGrids;
	}

	/**
	 * Sets variant to a specified variant
	 * 
	 * @param selectedVariant Variant specified by user
	 */
	public void setVariant(int selectedVariant) {
		this.variant = new GameVariant(selectedVariant);
		this.setScoring(ScoringStrategyFactory.createStrategy(this.variant));
	}

	/**
	 * Removes null tiles from tiles list and draws a new tile from the deck if
	 * available.
	 * 
	 * @param tiles List of tiles
	 */
	public void removeTiles(List<Tile> tiles) {
		tiles.removeIf(t -> t == null); // on retire la tuile qu'on a marqué null car jouée
		if (!deck.getTiles().isEmpty()) {
			tiles.add(deck.drawTiles(1).get(0));
		}
	}

	/**
	 * Core method of Cascadia that manages the steps from the start to the end of a
	 * game.
	 */
	public void startGame() {
		int roundCount = 1;
		int numberOfTurns = 20;
		gameUI.displayStartMenu();
		variant.variantChoice(); // Choice of variant
		boolean redrawFor3 = gameUI.askForRedrawPreference(); // Choice of redraw preference
		List<Tile> currentTiles = drawValidTiles(getGridForPlayer(players.get(0)), redrawFor3);
		while (roundCount <= numberOfTurns && !deck.getTiles().isEmpty()) {
			System.out.println("------ Tour " + roundCount + " ------");
			for (Player player : players) {
				if (!deck.getTiles().isEmpty()) {
					Grid currentGrid = getGridForPlayer(player);
					if (!areTilesPlayable(currentGrid, currentTiles) || Tile.getMaxCombinationCount(currentTiles) == 4
							|| (Tile.getMaxCombinationCount(currentTiles) == 3 && redrawFor3)) {
						currentTiles = drawValidTiles(currentGrid, redrawFor3);
					}
					executePlayerTurn(player.getName(), player, currentGrid, currentTiles);
				}
			}
			roundCount++;
		}
		gameUI.displayEndGame(players, playerGrids);
	}

	// Draws valid tiles according to the state of the game.
	public List<Tile> drawValidTiles(Grid grid, boolean redrawFor3) {
		List<Tile> pickedTiles;
		do {
			pickedTiles = deck.drawTiles(4);
		} while (!areTilesPlayable(grid, pickedTiles) || Tile.getMaxCombinationCount(pickedTiles) == 4
				|| (Tile.getMaxCombinationCount(pickedTiles) == 3 && redrawFor3));
		return pickedTiles;
	}

	// Checks if at least one of the proposed tiles can be placed on the grid.
	public boolean areTilesPlayable(Grid grid, List<Tile> tiles) {
		for (Tile tile : tiles) {
			if (tile != null && Tile.isTilePlayable(grid, tile)) {
				return true;
			}
		}
		return false;
	}

	// Manages a complete round of a player.
	public void executePlayerTurn(String playerName, Player player, Grid grid, List<Tile> pickedTiles) {
		System.out.println(playerName + " :");
		gameUI.displayTileChoices(pickedTiles);
		player_Round(player, grid, pickedTiles);
		removeTiles(pickedTiles);
	}

	/**
	 * Calculates score for all players
	 * 
	 * @param players     All players
	 * @param playerGrids All grids of player
	 */
	public void calculateAllPlayersScores(List<Player> players, Map<Player, Grid> playerGrids, GameMode mode) {
		for (Player currentPlayer : players) {
			currentPlayer.setScoringStrategy(this.variant);
			List<Grid> opponentGrids = new ArrayList<>();
			for (Player opponent : players) {
				if (!opponent.equals(currentPlayer)) {
					opponentGrids.add(playerGrids.get(opponent));
				}
			}
			currentPlayer.calculatePlayerScores(playerGrids.get(currentPlayer), opponentGrids, mode);
		}
	}

	/**
	 * Manages the UI round of a player
	 * 
	 * @param player         Player of the round
	 * @param grid           In-game grid of player
	 * @param availableTiles List of available tiles proposed to the player
	 */
	public void player_Round(Player player, Grid grid, List<Tile> availableTiles) {
		Objects.requireNonNull(player, "player cannot be null");
		Objects.requireNonNull(grid, "grid cannot be null");
		Objects.requireNonNull(availableTiles, "availableTiles cannot be null");
		grid.displayGrid();
		System.out.println("Sélectionnez une tuile (1-" + availableTiles.size() + ") :");
		Tile chosenTile = Tile.chooseTile(grid, availableTiles);
		Wildlife animal = chosenTile.getWildlife().get(0);

		do { // Placement of a habitat tile
			System.out.println(player.getName() + ", où voulez-vous placer la tuile " + chosenTile.getHabitat() + " ?");
			input.readPositions(grid);
		} while (!player.placeTile(chosenTile, grid, input.getY(), input.getX()));

		grid.displayGrid();

		do { // Placement of a wildlife token
			System.out.println(player.getName() + ", où voulez-vous placer l'animal " + animal + " ?");
			input.readPositions(grid);
		} while (!player.placeWildlife(grid, input.getY(), input.getX(), animal));
	}

	/**
	 * @return the scoring
	 */
	public PlayerScore getScoring() {
		return scoring;
	}

	/**
	 * @param scoring the scoring to set
	 */
	public void setScoring(PlayerScore scoring) {
		this.scoring = scoring;
	}

}
