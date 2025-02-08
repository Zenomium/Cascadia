package graphical.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.github.forax.zen.ApplicationContext;

import core.game.Game;
import core.game.grid.Grid;
import core.game.player.Player;
import graphical.controller.events.EndEventManager;
import graphical.controller.events.GameEventManager;
import graphical.controller.events.MenuEventManager;
import graphical.controller.events.OptionEventManager;
import terminal.ui.GameUI;

/**
 * Manages overall state configuration of the game.
 * - Handles game mode, variant and screen
 */
public class GameStateManager {
	private final ApplicationContext context;
	private final MenuEventManager menuManager;
	private final OptionEventManager optionManager;
	private final GameEventManager gameManager;
	private final EndEventManager endManager;
	private final GameUI gameUI;	
	private Game game;
	private final List<Player> players;

	private GameScene currentScene = GameScene.MAIN_MENU;
	private GameMode currentGameMode = GameMode.GRAPHICAL_SQUARE_TILES; // Default game mode is set to graphical square
																																			// tiles
	private int selectedVariant = 3; // Default variant is set to family variant
	private int nbOfPlayers = 2; // Default number of players is set to 2
	private int width;
	private int height;

	/**
	 * Constructor for GameStateManager.
	 * - Initializes necessary managers
	 * @param context
	 */
	public GameStateManager(ApplicationContext context) {
		this.context = Objects.requireNonNull(context);
		this.game = new Game();
		this.players = new ArrayList<>();
		this.gameUI = new GameUI(this.game);
		this.optionManager = new OptionEventManager(context, this);
		this.gameManager = new GameEventManager(context, this);
		this.menuManager = new MenuEventManager(context, this);
		this.endManager = new EndEventManager(context, this);

		var screenInfo = context.getScreenInfo();
		this.width = screenInfo.width();
		this.height = screenInfo.height();
	}

	/**
	 * Sets current scene to a new scene
	 * 
	 * @param scene Scene
	 */
	public void setCurrentScene(GameScene scene) {
		this.currentScene = Objects.requireNonNull(scene);
	}

	/**
	 * Getter of current scene
	 * 
	 * @return
	 */
	public GameScene getCurrentScene() {
		return currentScene;
	}

	/**
	 * Sets game mode to a newly configured mode
	 * 
	 * @param mode
	 */
	public void setGameMode(GameMode mode) {
		this.currentGameMode = Objects.requireNonNull(mode);
	}

	/**
	 * Getter of current of game mode
	 * 
	 * @return
	 */
	public GameMode getCurrentGameMode() {
		return currentGameMode;
	}

	/**
	 * Sets the number of players to a newly configured number
	 * 
	 * @param nbOfPlayers New number of players
	 */
	public void setNbOfPlayers(int nbOfPlayers) {
		this.nbOfPlayers = nbOfPlayers;
	}

	/**
	 * Getter of the number of players
	 * 
	 * @return Number of Players
	 */
	public int getNbOfPlayers() {
		return nbOfPlayers;
	}

	/**
	 * Sets the variant to a newly configured variant.
	 * 
	 * @param selectedVariant Selected variant by user
	 */
	public void setVariant(int selectedVariant) {
		game.setVariant(selectedVariant);
	}

	/**
	 * Getter of variant
	 * @return Current Variant
	 */
	public int getVariant() {
		return selectedVariant;
	}

	/**
	 * Initializes the players
	 */
	public void initializePlayers() {
		players.clear();
		for (int i = 1; i <= nbOfPlayers; i++) {
			String playerName = "Joueur " + i;
			game.addPlayer(playerName, getCurrentGameMode());
			players.add(game.getPlayers().get(i - 1));
		}
	}

	/**
	 * Getter of all players
	 * @return List that contains all players
	 */
	public List<Player> getPlayers() {
		return players;
	}

	/**
	 * Getter of all grids used in-Game
	 * @return Map that contains all grid affiliated with their respective players
	 */
	public Map<Player, Grid> getAllGrids() {
		return game.getGrids();
	}

	/**
	 * Getter of game instance.
	 * @return
	 */
	public Game getGame() {
		return game;
	}

	/**
	 * Getter of game UI instance.
	 */
	public GameUI getGameUI() {
		return gameUI;
	}
	
	/**
	 * Getter of screen width
	 * @return Width of screen
	 */
	public int getScreenWidth() {
		return width;
	}

	/**
	 * Getter of screen height
	 * @return Height of screen
	 */
	public int getScreenHeight() {
		return height;
	}

	/**
	 * Executes the game loop and renders the scene based on the current scene
	 */
	public void run() {
		while (true) {
			switch (currentScene) {
			case MAIN_MENU -> menuManager.renderMainMenu();
			case PLAY_GAME -> gameManager.renderGame();
			case OPTIONS -> optionManager.renderOptionsPage();
			case GAME_END -> endManager.renderGameEnd();
			case QUIT_GAME -> context.dispose();
			default -> throw new IllegalArgumentException();
			}
		}
	}
}