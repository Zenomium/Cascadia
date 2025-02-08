package graphical.controller.components;

import java.util.List;
import java.util.Objects;

import core.game.grid.Grid;
import core.game.mechanics.Wildlife;
import core.game.player.Player;
import graphical.model.GameMode;
import graphical.model.GameStateManager;
import graphical.view.components.*;

/**
 * Manages interactions with the graphical grid, including tile and wildlife placement,
 * and updates the grid's state based on user actions.
 */
public class GraphicalGridController implements ErrorHandler {
	private final Player player;
	private final Grid grid;
	private final GraphicalGrid translatedGrid;
	private final GraphicalTileController tileController;
	private final GameStateManager gameStateManager;
	
	private boolean showError = false;
	private boolean isGridClickable;
	private static final int TILE_SIZE = 150;
	private static final int GRID_OFFSET_X = 750;
	private static final int GRID_OFFSET_Y = 200;
	private static final int TILE_WIDTH = (int)(TILE_SIZE * Math.sqrt(3)/1.75);
    private static final int TILE_HEIGHT = (int)(TILE_SIZE * 1.15);

	/**
	 * Constructor for GraphicalGridController
	 * @param grid core Grid
	 * @param translatedGrid Graphical converted grid
	 * @param tileController Tile controller
	 * @param gameStateManager State manager
	 */
	public GraphicalGridController(Grid grid, GraphicalGrid translatedGrid, GraphicalTileController tileController,
			GameStateManager gameStateManager) {
		this.player = new Player("Joueur");
		this.grid = grid;
		this.translatedGrid = translatedGrid;
		this.tileController = tileController;
		this.isGridClickable = true;
		this.gameStateManager = gameStateManager;
	}

	/**
     * Converts mouse coordinates into coordinates of hexagonal grid
     * @param clickX X coordinate of mouse click
     * @param clickY Y coordinate of mouse click
     * @return int[] with coordinates of the grid {x, y}, or null if beyond grid
     */
    private int[] pixelToHexCoord(int clickX, int clickY) {
        // Ajuster les coordonnées relatives à l'origine de la grille
        double relX = clickX - GRID_OFFSET_X;
        double relY = clickY - GRID_OFFSET_Y;
        
        // Calculer la colonne approximative
        int col = (int) Math.floor(relX / (TILE_WIDTH * 0.75));
        
        // Ajuster Y pour la colonne (décalage pour les colonnes impaires)
        double adjY = relY;
        if (col % 2 != 0) {
            adjY -= TILE_HEIGHT * 0.375;
        }
        
        // Calculer la ligne
        int row = (int) Math.floor(adjY / (TILE_HEIGHT * 0.75));
        
        // Vérifier si le point est à l'intérieur de l'hexagone
        if (isInsideHexagon(relX - (col * TILE_WIDTH * 0.75), 
                          adjY - (row * TILE_HEIGHT * 0.75))) {
            return new int[]{col, row};
        }
        
        return null;
    }
    
    /**
     * Vérifie si un point est à l'intérieur d'un hexagone
     */
    private boolean isInsideHexagon(double relX, double relY) {
        // Centre de l'hexagone
        double centerX = TILE_WIDTH / 2;
        double centerY = TILE_HEIGHT / 2;
        
        // Distance du point au centre
        double dx = Math.abs(relX - centerX) / (TILE_WIDTH/2);
        double dy = Math.abs(relY - centerY) / (TILE_HEIGHT/2);
        
        // Équation qui définit l'intérieur d'un hexagone
        return dx * 0.866025404 + dy * 0.5 <= 1;
    }

	/**
	 * Detects mouse click placement of habitat tile on grid
	 * @param tileToPlace Tile to be placed
	 * @param tilesOnGrid Displayed tiles on grid
	 * @param clickX X coordinate of mouse click
	 * @param clickY Y coordinate of mouse click
	 */
    public void detectClickOnGrid(GraphicalTile tileToPlace, List<GraphicalTile> tilesOnGrid, int clickX, int clickY) {
        if (tileToPlace == null || !isGridClickable)
            return;
            
        if (gameStateManager.getCurrentGameMode() == GameMode.GRAPHICAL_HEXAGON_TILES) {
            int[] hexCoord = pixelToHexCoord(clickX, clickY);
            if (hexCoord != null && hexCoord[0] >= 0 && hexCoord[0] < grid.getSize() 
                && hexCoord[1] >= 0 && hexCoord[1] < grid.getSize()) {
                
                if (player.placeTile(tileToPlace.getTile(), grid, hexCoord[0], hexCoord[1])) {
                    // Calculer la position exacte pour l'hexagone
                    double posX = GRID_OFFSET_X + hexCoord[0] * (TILE_WIDTH * 0.75);
                    double posY = GRID_OFFSET_Y + hexCoord[1] * (TILE_HEIGHT * 0.75);
                    if (hexCoord[0] % 2 != 0) {
                        posY += TILE_HEIGHT * 0.375;
                    }
                    
                    tileToPlace.setPosition((int)posX, (int)posY);
                    tilesOnGrid.add(tileToPlace);
                    translatedGrid.updateGraphicalTiles(tilesOnGrid);
                    tileToPlace = null;
                    tileController.setTilePlaced(true);
                    isGridClickable = false;
                } else {
                    showError = true;
                    System.out.println("Placement impossible. Veuillez choisir une autre case");
                }
            }
        } else {
            int gridX = (clickX - GRID_OFFSET_X) / TILE_SIZE;
            int gridY = (clickY - GRID_OFFSET_Y) / TILE_SIZE;
            if (gridX >= 0 && gridX < grid.getSize() && gridY >= 0 && gridY < grid.getSize()) {
    			if (player.placeTile(tileToPlace.getTile(), grid, gridX, gridY)) {
    				tileToPlace.setPosition(750 + gridX * 150, 200 + gridY * 150);
    				tilesOnGrid.add(tileToPlace);
    				translatedGrid.updateGraphicalTiles(tilesOnGrid);
    				tileToPlace = null;
    				tileController.setTilePlaced(true);
    				isGridClickable = false;
    			} else {
    				showError = true;
    				System.out.println("Placement impossible. Veuillez choisir une autre case");
    			}
    		}
        }
    }

	/**
	 * Detects mouse click placement of wildlife token on grid 
	 * @param selectedWildlife Wildlife token to be placed
	 * @param tilesOnGrid Tiles displayed on grid
	 * @param clickX X coordinate of mouse click
	 * @param clickY Y coordinate of mouse click
	 */
    public void detectClickOnGridWildlife(Wildlife selectedWildlife, List<GraphicalTile> tilesOnGrid, 
                                        int clickX, int clickY) {
        Objects.requireNonNull(selectedWildlife);
        Objects.requireNonNull(tilesOnGrid);
        
        if (gameStateManager.getCurrentGameMode() == GameMode.GRAPHICAL_HEXAGON_TILES) {
            int[] hexCoord = pixelToHexCoord(clickX, clickY);
            if (hexCoord != null && hexCoord[0] >= 0 && hexCoord[0] < grid.getSize() 
                && hexCoord[1] >= 0 && hexCoord[1] < grid.getSize()) {
                
                if (player.placeWildlife(grid, hexCoord[0], hexCoord[1], selectedWildlife)) {
                    translatedGrid.updateGraphicalTiles(tilesOnGrid);
                    tileController.setTokenPlaced(true);
                    tileController.toggleWaitingForTokenPlacement();
                } else {
                    showError = true;
                    System.out.println("Placement impossible. Veuillez choisir une autre case");
                }
            }
        } else {
            int gridX = (clickX - GRID_OFFSET_X) / TILE_SIZE;
            int gridY = (clickY - GRID_OFFSET_Y) / TILE_SIZE;
    		if (gridX >= 0 && gridX < grid.getSize() && gridY >= 0 && gridY < grid.getSize()) {
    			if (player.placeWildlife(grid, gridX, gridY, selectedWildlife)) {
    				translatedGrid.updateGraphicalTiles(tilesOnGrid);
    				tileController.setTokenPlaced(true);
    				tileController.toggleWaitingForTokenPlacement();
    			} else {
    				showError = true;
    				System.out.println("Placement impossible. Veuillez choisir une autre case");
    			}
    		}
        }
    }

	/**
	 * Resets state of grid
	 */
	public void resetGridState() {
		this.isGridClickable = true;
	}

	@Override
	public boolean hasError() {
		boolean error = showError;
		showError = false;
		return error;
	}
}
