package graphical.view.components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import core.game.grid.AllNeighbors;
import core.game.grid.Grid;
import core.game.grid.HexNeighborsOdd;
import core.game.grid.HexNeighborsPair;
import core.game.grid.NeighborStrategy;
import core.game.grid.Tile;
import graphical.model.GameMode;
import graphical.model.GameStateManager;
import graphical.view.resources.*;

/**
 * Converts a grid to graphical version.
 */
public class GraphicalGrid {
	private final Grid grid;
	private final TileImageManager imageManager;
	private final GameStateManager gameStateManager;
	private static final int TILE_SIZE = 150;
	private static final int GRID_OFFSET_X = 750;
	private static final int GRID_OFFSET_Y = 200;
	private static final int TILE_WIDTH = (int)(TILE_SIZE * Math.sqrt(3)/1.75);
    private static final int TILE_HEIGHT = (int)(TILE_SIZE * 1.15);

	/**
	 * Constructor for GraphicalGrid
	 * @param grid Grid
	 * @param imageManager Resource manager
	 */
	public GraphicalGrid(Grid grid, TileImageManager imageManager, GameStateManager gameStateManager) {
		this.grid = grid;
		this.imageManager = Objects.requireNonNull(imageManager);
		this.gameStateManager = Objects.requireNonNull(gameStateManager);
	}

	/**
	 * Creates a grid consisting of graphical hexagonal tiles in "odd-q" layout.
	 * @return List of graphical tiles
	 */
	public List<GraphicalTile> createGrid() {
	    List<GraphicalTile> tiles = new ArrayList<>();
	    if (grid != null) {
	        for (int x = 0; x < grid.getSize(); x++) {
	            for (int y = 0; y < grid.getSize(); y++) {
	                Tile tile = grid.getTile(x, y);
	                if (tile != null) {
	                    GraphicalTile graphicalTile = new GraphicalTile(tile, imageManager, TILE_SIZE);
	                    if (gameStateManager.getCurrentGameMode() == GameMode.GRAPHICAL_HEXAGON_TILES) {
	                        // Calcul de la largeur et hauteur des hexagones
	                        double tileWidth = (TILE_SIZE * Math.sqrt(3))/(1.75);  // Largeur d'un hexagone
	                        double tileHeight = TILE_SIZE*1.15;            		   // Hauteur d'un hexagone
	                        
	                        // Position horizontale - décalage de 3/4 de la largeur pour chaque colonne
	                        double posX = GRID_OFFSET_X + x * (tileWidth * 0.75);
	                        
	                        // Position verticale - hauteur complète pour chaque ligne
	                        // Pour les colonnes impaires, on ajoute un décalage de la moitié de la hauteur
	                        double posY = GRID_OFFSET_Y + y * (tileHeight * 0.75);
	                        if (x % 2 != 0) {
	                            posY += tileHeight * 0.375; // Décalage pour les colonnes impaires
	                        }
	                        
	                        graphicalTile.setPosition((int)posX, (int)posY);
	                    } else if (gameStateManager.getCurrentGameMode() == GameMode.GRAPHICAL_SQUARE_TILES) {
	                        graphicalTile.setPosition(
	                            GRID_OFFSET_X + x * TILE_SIZE,
	                            GRID_OFFSET_Y + y * TILE_SIZE
	                        );
	                    }
	                    tiles.add(graphicalTile);
	                }
	            }
	        }
	    }
	    return tiles;
	}


	/**
	 * Renders grid on screen
	 * 
	 * @param graphics
	 */
	public void renderGrid(Graphics2D graphics, List<GraphicalTile> graphicalTiles) {
		Objects.requireNonNull(graphicalTiles);
		for (GraphicalTile graphicalTile : graphicalTiles) {
			graphicalTile.renderTileOnGrid(graphics, graphicalTile.getX(), graphicalTile.getY(), gameStateManager.getCurrentGameMode());
		}
	}

	/**
	 * Renders available spaces on grid for tile placement
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
                        renderAvailableHexagonTileOnGrid(graphics, (int)posX, (int)posY);
                    } else {
					int pixelX = GRID_OFFSET_X + x * TILE_SIZE;
					int pixelY = GRID_OFFSET_Y + y * TILE_SIZE;
					renderAvailableTileOnGrid(graphics, pixelX, pixelY);
                    }
				}
			}
		}
	}

	private void renderAvailableTileOnGrid(Graphics2D graphics, int x, int y) {
		graphics.setColor(new Color(144, 238, 144, 50));
		graphics.fillRect(x, y, TILE_SIZE, TILE_SIZE);
		graphics.setColor(Color.GREEN);
		graphics.drawRect(x, y, TILE_SIZE, TILE_SIZE);
	}

	private void renderAvailableHexagonTileOnGrid(Graphics2D graphics, int x, int y) {
		int[] xPoints = new int[6];
		int[] yPoints = new int[6];

		for (int i = 0; i < 6; i++) {
		double angle = Math.PI / 3 * i;
		xPoints[i] = x + (int)(TILE_WIDTH/2.20 * Math.cos(angle)) + 75;
		yPoints[i] = y + (int)(TILE_HEIGHT/2.20 * Math.sin(angle)) + 75;
		}

		graphics.setColor(new Color(144, 238, 144, 50));
		graphics.fillPolygon(xPoints, yPoints, 6);
		graphics.setColor(Color.GREEN);
		graphics.drawPolygon(xPoints, yPoints, 6);
		}

	/**
	 * Updates graphical tiles according to grid modifications
	 * @param graphicalTiles
	 */
	public void updateGraphicalTiles(List<GraphicalTile> graphicalTiles) {
		graphicalTiles.clear();
		graphicalTiles.addAll(createGrid());
	}

	/**
	 * Displays proposed tile choices to player
	 * @param pickedTiles Picked tiles from deck
	 * @param tiles List of graphical tiles
	 * @param tileImageManager Image viewer of tile
	 */
	public void displayTileChoices(List<Tile> pickedTiles, List<GraphicalTile> tiles, TileImageManager tileImageManager) {
		int x = 200;
		int y = 200;
		int tileSize = 150;
		int spacing = 20;
		Objects.requireNonNull(pickedTiles);
		for (Tile tile : pickedTiles) {
			System.out.println("tile : " + tile);
			GraphicalTile graphicalTile = new GraphicalTile(tile, tileImageManager, tileSize);
			graphicalTile.setPosition(x, y);
			tiles.add(graphicalTile);
			y += tileSize + spacing;
		}
	}

	
}
