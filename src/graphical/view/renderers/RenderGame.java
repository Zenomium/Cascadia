package graphical.view.renderers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

import core.game.grid.AllNeighbors;
import core.game.grid.Grid;
import core.game.grid.HexNeighborsOdd;
import core.game.grid.HexNeighborsPair;
import core.game.grid.NeighborStrategy;
import core.game.player.Player;
import graphical.view.resources.*;
import graphical.model.GameMode;
import graphical.model.GameStateManager;
import graphical.view.components.*;

/**
 * Various rendering methods for game screen.
 */
public class RenderGame {
	private BufferedImage table;
	private final int width;
	private final int height;
	private final GraphicalGrid translatedGrid;
	private final List<GraphicalTile> graphicalTiles;
	private final List<GraphicalTile> availableTiles;
	private final List<GraphicalCard> scoringCards;
	private final GameStateManager gameStateManager;
	private final Grid grid;
	
	private static final int TILE_SIZE = 150;
	private static final int TILE_WIDTH = (int)(TILE_SIZE * Math.sqrt(3)/1.75);
    private static final int TILE_HEIGHT = (int)(TILE_SIZE * 1.15);
	private static final int GRID_OFFSET_X = 750;
	private static final int GRID_OFFSET_Y = 200;


	/**
	 * Constructor for RenderGame
	 * @param table
	 * @param width
	 * @param height
	 * @param grid
	 * @param imageManager
	 * @param availableTiles
	 */
	public RenderGame(BufferedImage table, int width, int height, Grid grid, TileImageManager imageManager,
			List<GraphicalTile> availableTiles, List<GraphicalCard> scoringCards, GameStateManager gameStateManager) {
		this.table = Objects.requireNonNull(table);
		this.width = width;
		this.height = height;
		this.grid = grid;
		this.translatedGrid = new GraphicalGrid(grid, imageManager, gameStateManager);
		this.graphicalTiles = translatedGrid.createGrid();
		this.availableTiles = Objects.requireNonNull(availableTiles);
		this.scoringCards = Objects.requireNonNull(scoringCards);
		this.gameStateManager = Objects.requireNonNull(gameStateManager);
	}

	/**
	 * Renders game screen.
	 * @param graphics
	 */
	public void render(Graphics2D graphics) {
	    // Draw the game table
	    graphics.drawImage(table, 0, 0, width, height, null);

	    // Render the grid with available tiles
	    translatedGrid.renderAvailableTilesOnGrid(graphics);
	    translatedGrid.renderGrid(graphics, graphicalTiles);

	    // Display scoring cards
	    displayScoringCards(graphics, scoringCards);

	    // Render available tiles
	    renderAvailableTiles(graphics);
	}

	
    /**
     * Displays scoring cards on the game screen.
     * @param graphics
     * @param scoringCards randomly chosed cards
     */
    public void displayScoringCards(Graphics2D graphics, List<GraphicalCard> scoringCards) {
        int startX = 500;
        int startY = 20;
        int tileSizeX = 150;
        int spacing = 40;

        for (GraphicalCard scoringCard : scoringCards) {
            scoringCard.setPosition(startX, startY);
            scoringCard.renderCardOnGrid(graphics, scoringCard, startX, startY);
            startX += tileSizeX + spacing;
        }
    }

    /**
     * Renders available tiles for selection.
     * @param graphics
     */
    private void renderAvailableTiles(Graphics2D graphics) {
        for (GraphicalTile availableTile : availableTiles) {
            if (gameStateManager.getCurrentGameMode() == GameMode.GRAPHICAL_HEXAGON_TILES) {
            	availableTile.renderAvailableTile(graphics, availableTile.getX(), availableTile.getY(), gameStateManager.getCurrentGameMode());
                // Contour de sélection hexagonal
                graphics.setColor(Color.BLACK);
                graphics.setStroke(new BasicStroke(5));
                int[] xPoints = new int[6];
                int[] yPoints = new int[6];
                for (int i = 0; i < 6; i++) {
                    double angle = Math.PI / 3 * i;
                    if (i==0) {
                    	xPoints[i] = availableTile.getX() + (int)(TILE_WIDTH/2.20 * Math.cos(angle)) + 85; // pour coller avec la forme des tuiles
                    } else if (i==3) {
                    	xPoints[i] = availableTile.getX() + (int)(TILE_WIDTH/2.20 * Math.cos(angle)) + 65; // pour coller avec la forme des tuiles
                    } else {
                    xPoints[i] = availableTile.getX() + (int)(TILE_WIDTH/2.20 * Math.cos(angle)) + 75;
                    }
                    yPoints[i] = availableTile.getY() + (int)(TILE_HEIGHT/2.20 * Math.sin(angle)) + 75;
                }
                graphics.drawPolygon(xPoints, yPoints, 6);
            } else {
                availableTile.renderAvailableTile(graphics, availableTile.getX(), availableTile.getY(), gameStateManager.getCurrentGameMode());
                graphics.setColor(Color.BLACK);
                graphics.setStroke(new BasicStroke(5));
                graphics.drawRect(availableTile.getX() - 1, availableTile.getY() - 1, 
                                availableTile.getSize() + 1, availableTile.getSize() + 1);
            }
        }
    }

    /**
     * Renders available tiles on grid.
     * @param graphics
     */
    public void renderAvailableTilesOnGrid(Graphics2D graphics) {
            
        for (int x = 0; x < grid.getSize(); x++) {
            for (int y = 0; y < grid.getSize(); y++) {
				NeighborStrategy strategy;
				if (gameStateManager.getCurrentGameMode() == GameMode.GRAPHICAL_HEXAGON_TILES) {
				    // Déterminer si la colonne est impaire ou paire
				    boolean isOddColumn = (x % 2 != 0); // 'x' est la coordonnée de la colonne
				    strategy = isOddColumn ? new HexNeighborsOdd() : new HexNeighborsPair();
				} else {
				    strategy = new AllNeighbors();
				}

                if (grid.getTile(x, y) == null && grid.nonEmptyNeighbor(x, y, strategy)) {
                    if (gameStateManager.getCurrentGameMode() == GameMode.GRAPHICAL_HEXAGON_TILES) {
                        // Calcul de la position pour les hexagones
                        double posX = GRID_OFFSET_X + x * (TILE_WIDTH * 0.75);
                        double posY = GRID_OFFSET_Y + y * (TILE_HEIGHT * 0.75);
                        if (x % 2 != 0) {
                            posY += TILE_HEIGHT * 0.375;
                        }
                        renderAvailableHexagonTile(graphics, (int)posX, (int)posY);
                    } else {
                        int pixelX = GRID_OFFSET_X + x * TILE_SIZE;
                        int pixelY = GRID_OFFSET_Y + y * TILE_SIZE;
                        renderAvailableSquareTile(graphics, pixelX, pixelY);
                    }
                }
            }
        }
    }

    /**
     * Renders available tiles for selection in hexagonal version.
     * @param graphics
     */
    private void renderAvailableHexagonTile(Graphics2D graphics, int x, int y) {
        int[] xPoints = new int[6];
        int[] yPoints = new int[6];

        for (int i = 0; i < 6; i++) {
            double angle = Math.PI / 3 * i;
            xPoints[i] = x + (int)(TILE_WIDTH/2 * Math.cos(angle));
            yPoints[i] = y + (int)(TILE_HEIGHT/2 * Math.sin(angle));
        }

        graphics.setColor(new Color(144, 238, 144, 50));
        graphics.fillPolygon(xPoints, yPoints, 6);
        graphics.setColor(Color.GREEN);
        graphics.drawPolygon(xPoints, yPoints, 6);
    }

    /**
     * Renders available tiles for selection in square version.
     * @param graphics
     */
    private void renderAvailableSquareTile(Graphics2D graphics, int x, int y) {
        graphics.setColor(new Color(144, 238, 144, 50));
        graphics.fillRect(x, y, TILE_SIZE, TILE_SIZE);
        graphics.setColor(Color.GREEN);
        graphics.drawRect(x, y, TILE_SIZE, TILE_SIZE);
    }

	/**
	 * Renders turn info of game.
	 * @param graphics
	 * @param currentPlayer
	 * @param currentTurn
	 */
	public void renderTurnInfo(Graphics2D graphics, Player currentPlayer, int currentTurn) {
		Objects.requireNonNull(currentPlayer);
		String turnInfo = String.format("TOUR %d - %s", currentTurn, currentPlayer.getName());
		graphics.setFont(new Font("Arial", Font.ITALIC, 40));
		graphics.setColor(java.awt.Color.WHITE);
		graphics.drawString(turnInfo, 20, 60);
	}

}