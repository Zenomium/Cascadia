package graphical.controller.events;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.KeyboardEvent;
import com.github.forax.zen.PointerEvent;

import core.game.grid.Tile;
import core.game.player.Player;
import graphical.controller.components.ErrorHandler;
import graphical.controller.components.GraphicalGridController;
import graphical.controller.components.GraphicalTileController;
import graphical.controller.components.TurnController;
import graphical.model.GameScene;
import graphical.model.GameStateManager;
import graphical.view.resources.*;
import graphical.view.renderers.*;
import graphical.view.components.*;

/**
 * Manages events on game page.
 * - Checks interactions between player and game screen
 * - Updates screen rendering based on events
 * - Updates state of game based on events of user
 */
public class GameEventManager {
	private final ApplicationContext context;
	private final List<GraphicalTile> tiles;
	private final List<GraphicalCard> cards;
	private final Map<Player, List<GraphicalTile>> playerGridTiles;
	private final Map<Player, GraphicalGrid> playerGrids;
	private final TileImageManager tileImageManager;
	private final ScoringCardsImageManager cardsImageManager;
	private final GameStateManager gameStateManager;
	
	private final Map<Player, GraphicalTileController> tileControllers;
	private final Map<Player, GraphicalGridController> gridControllers;
	private final Dialogs dialogs;
	private final TurnController turnController;
	
    private static final int SCORING_CARDS_SIZE_X = 180;
    private static final int SCORING_CARDS_SIZE_Y = 140;
	private static final int NUMBER_OF_TURNS = 20;

	private BufferedImage table;
	private RenderGame gameRenderer;
	private boolean initialized = false;
	private boolean redrawFor3 = false;
	private boolean dialogAnswered = false;
	private boolean isShowingError = false;
	private long errorStartTime = 0;

	/**
	 * Constructor for GameEventManager
	 * @param context
	 * @param gameStateManager
	 */
	public GameEventManager(ApplicationContext context, GameStateManager gameStateManager) {
		this.context = context;
		this.gameStateManager = gameStateManager;
		this.tiles = new ArrayList<>();
		this.cards = new ArrayList<>();
		this.playerGridTiles = new HashMap<>();
		this.playerGrids = new HashMap<>();
		this.tileControllers = new HashMap<>();
		this.gridControllers = new HashMap<>();
		this.tileImageManager = new TileImageManager(gameStateManager);
		this.cardsImageManager = new ScoringCardsImageManager();
		this.dialogs = new Dialogs(context);
		this.turnController = new TurnController(gameStateManager.getPlayers(), NUMBER_OF_TURNS );
	}

	private void initializePlayersAndGrids() {
		gameStateManager.initializePlayers();
		for (Player player : gameStateManager.getPlayers()) {
			GraphicalGrid playerGrid = new GraphicalGrid(gameStateManager.getGame().getGridForPlayer(player),
					tileImageManager, gameStateManager);
			playerGrids.put(player, playerGrid);

			List<GraphicalTile> gridTiles = playerGrid.createGrid();
			playerGridTiles.put(player, gridTiles);

			GraphicalTileController tileController = new GraphicalTileController(
					gameStateManager.getGame().getGridForPlayer(player), gameStateManager);
			tileControllers.put(player, tileController);

			GraphicalGridController gridController = new GraphicalGridController(
					gameStateManager.getGame().getGridForPlayer(player), playerGrid, tileController, gameStateManager);
			gridControllers.put(player, gridController);
		}
	}

	private void initializeResources() {
		ResourceLoader resourceLoader = new ResourceLoader();
		String[] imageNames = { "table.jpg" };
		resourceLoader.loadImages(imageNames);
		table = resourceLoader.getImage("table");
	}

	private void initializeGame() {
	    initializePlayersAndGrids();
	    initializeResources();
	    tileImageManager.loadTileImages();
	    cardsImageManager.loadScoringCardImages();

	    // Initialize scoring cards
	    List<String> cardNames = cardsImageManager.getScoringCardNames(); // Fetch card names from the game logic
	    int nbOfCards=5;
	    for (String cardName : cardNames) {
	    	if (nbOfCards>0) {
	    		cards.add(new GraphicalCard(cardName, cardsImageManager, SCORING_CARDS_SIZE_X, SCORING_CARDS_SIZE_Y));
	    		nbOfCards--;
	    	}
	    }

	    updateGameRenderer();
	    initialized = true;
	    dialogs.getDialog().show("Voulez-vous repiocher si 3 jetons animaux sont identiques ?");
	}


	private void updateGameRenderer() {
		Player currentPlayer = turnController.getCurrentPlayer();
		gameRenderer = new RenderGame(table, gameStateManager.getScreenWidth(), gameStateManager.getScreenHeight(),
				gameStateManager.getGame().getGridForPlayer(currentPlayer), tileImageManager, tiles, cards, gameStateManager);
	}

	private void game() {
		tiles.clear();
		Player currentPlayer = turnController.getCurrentPlayer();

		List<Tile> currentPickedTiles = gameStateManager.getGame()
				.drawValidTiles(gameStateManager.getGame().getGridForPlayer(currentPlayer), redrawFor3);
		if (!gameStateManager.getGame().areTilesPlayable(gameStateManager.getGame().getGridForPlayer(currentPlayer),
				currentPickedTiles) || Tile.getMaxCombinationCount(currentPickedTiles) == 4
				|| (Tile.getMaxCombinationCount(currentPickedTiles) == 3 && redrawFor3)) {
			currentPickedTiles = gameStateManager.getGame()
					.drawValidTiles(gameStateManager.getGame().getGridForPlayer(currentPlayer), redrawFor3);
		}

		playerGrids.get(currentPlayer).displayTileChoices(currentPickedTiles, tiles, tileImageManager);
	}

	private void handleTurnEnd(GraphicalTileController currentTileController,
			GraphicalGridController currentGridController) {
		if (turnController.isGameOver()) {
			gameStateManager.setCurrentScene(GameScene.GAME_END);
			return;
		}
		currentTileController.resetTurnState();
		currentGridController.resetGridState();

		turnController.nextTurn();
		updateGameRenderer();
		game();
	}

	private void manageGameEvents() {
		var event = context.pollOrWaitEvent(10);
		if (event == null)
			return;

		Player currentPlayer = turnController.getCurrentPlayer();
		GraphicalTileController currentTileController = tileControllers.get(currentPlayer);
		GraphicalGridController currentGridController = gridControllers.get(currentPlayer);
		List<GraphicalTile> currentGridTiles = playerGridTiles.get(currentPlayer);

		switch (event) {
		case PointerEvent e -> {
			if (e.action() == PointerEvent.Action.POINTER_UP) {
				currentTileController.detectTileClick(tiles, gameStateManager.getGame().getGridForPlayer(currentPlayer),
						e.location().x(), e.location().y());

				currentGridController.detectClickOnGrid(currentTileController.tileToBePlaced(), currentGridTiles,
						e.location().x(), e.location().y());

				currentTileController.detectTokenClick(tiles, e.location().x(), e.location().y(),
						currentTileController.selectedWildlifeGetter());

				if (currentTileController.isWaitingForTokenPlacement()) {
					currentGridController.detectClickOnGridWildlife(currentTileController.selectedWildlifeGetter(),
							currentGridTiles, e.location().x(), e.location().y());
				}
				if (currentTileController.isTurnComplete()) {
					handleTurnEnd(currentTileController, currentGridController);
					currentTileController.resetTurnState();
				}
			}
		}
		case KeyboardEvent e -> {
		}
		default -> throw new IllegalArgumentException();
		}
	}

	/**
	 * Renders game events.
	 */
	public void renderGame() {
	    if (!initialized)
	        initializeGame();
	    context.renderFrame(graphics -> {
	        if (dialogs.getDialog().isVisible() && !dialogAnswered) {
	            dialogs.getDialog().renderPromptDialog(graphics);
	            if (dialogs.getDialog().hasResponse()) {
	                redrawFor3 = dialogs.getDialog().getResponse();
	                dialogAnswered = true;
	                game();
	            }
	        } else {
	            gameRenderer.render(graphics);
	            gameRenderer.displayScoringCards(graphics, cards);
	            manageGameEvents();
	            Player currentPlayer = turnController.getCurrentPlayer();
	            GraphicalTileController currentTileController = tileControllers.get(currentPlayer);
	            gameRenderer.renderTurnInfo(graphics, currentPlayer, turnController.getCurrentTurn());
	            handleErrorsAndDialogs(graphics, currentTileController, currentPlayer);
	        }
	    });
	}


	private void handleErrorsAndDialogs(Graphics2D graphics, GraphicalTileController currentTileController,
			Player currentPlayer) {
		Objects.requireNonNull(currentTileController);
		Objects.requireNonNull(currentPlayer);
		dialogDisplayHandler("Jeton faune implaçable. Veuillez choisir une autre tuile.", currentTileController);
		dialogs.getErrorDialog().handlePromptDialog(graphics, isShowingError, errorStartTime);
		isShowingError = dialogs.getErrorDialog().handlePopUpDialog(graphics, isShowingError, errorStartTime);

		if (currentTileController.selectedTileGetter() != null) {
			currentTileController.selectedTileGetter().renderSelectedTile(graphics,
					currentTileController.selectedTileGetter());
			dialogDisplayHandler("Placement impossible. Veuillez choisir une autre case", gridControllers.get(currentPlayer));
			isShowingError = dialogs.getErrorDialog().handlePopUpDialog(graphics, isShowingError, errorStartTime);
			currentTileController.selectedTileGetter().renderWildlifeTile(graphics,
					currentTileController.selectedWildlifeGetter());

			if (currentTileController.isWaitingForTokenPlacement()) {
				dialogDisplayHandler("Placement impossible. Veuillez choisir une autre case",
						gridControllers.get(currentPlayer));
				isShowingError = dialogs.getErrorDialog().handlePopUpDialog(graphics, isShowingError, errorStartTime);
				currentTileController.selectedTileGetter().renderSelectedToken(graphics,
						currentTileController.selectedWildlifeGetter());
			}
		}

		playerGrids.get(currentPlayer).renderGrid(graphics, playerGridTiles.get(currentPlayer));
	}

	private void dialogDisplayHandler(String message, ErrorHandler errorHandle) {
		Objects.requireNonNull(message);
		Objects.requireNonNull(errorHandle);
		if (errorHandle.hasError() && !isShowingError) {
			dialogs.getErrorDialog().show(message);
			isShowingError = true;
			errorStartTime = System.currentTimeMillis();
		}
	}

}