package graphical.view.components;

import java.awt.*;
import java.util.Objects;

import core.game.grid.Tile;
import core.game.mechanics.Wildlife;
import graphical.model.GameMode;
import graphical.view.resources.*;

/**
 * Graphical representation of a tile. Draws the tile on the screen
 */
public class GraphicalTile {
    private final Tile tile;
    private final TileImageManager imageManager;
    private final int size;

    private int x;
    private int y;

    /**
     * Constructor for GraphicalTile
     * 
     * @param tile Tile
     * @param imageManager Resource manager
     * @param size Size of tile
     */
    public GraphicalTile(Tile tile, TileImageManager imageManager, int size) {
        this.tile = tile;
        this.imageManager = imageManager;
        this.size = size;
    }

    /**
     * Renders a tile ready to be played.
     * 
     * @param graphics
     * @param x        X coordinate of tile
     * @param y        Y coordinate of tile
     * @param mode     Current game mode
     */
    public void renderAvailableTile(Graphics2D graphics, int x, int y, GameMode mode) {
        if (mode == GameMode.GRAPHICAL_HEXAGON_TILES) {
            renderAvailableHexagonTile(graphics, x, y);
        } else if (mode == GameMode.GRAPHICAL_SQUARE_TILES) {
        	graphics.drawImage(imageManager.getHabitatImage(tile.getHabitat()), x, y, size, size, null);
      		int wildlifeSize = (int) (size * 0.6);
      		int offset = (size - wildlifeSize) / 2;
      		int margin = 170;
      		drawAuthorizedWildlife(graphics, x, y, wildlifeSize, offset);

      		if (!tile.getWildlife().isEmpty()) {
      			graphics.drawImage(imageManager.getWildlifeImage(tile.getWildlife().getFirst()), x + margin, y + offset,
      					wildlifeSize, wildlifeSize, null);
      		}
        }
    }

    /**
     * Renders a tile on the grid.
     * 
     * @param graphics
     * @param x
     * @param y
     * @param mode     Current game mode
     */
    public void renderTileOnGrid(Graphics2D graphics, int x, int y, GameMode mode) {
        if (mode == GameMode.GRAPHICAL_HEXAGON_TILES) {
            renderHexagonTile(graphics, x, y);
        } else if (mode == GameMode.GRAPHICAL_SQUARE_TILES) {
            renderSquareTile(graphics, x, y);
        }
    }

    /**
     * Renders the tile in hexagonal shape.
     * 
     * @param graphics
     * @param x
     * @param y
     */
    private void renderHexagonTile(Graphics2D graphics, int x, int y) {
        Polygon hexagon = createHexagon(x + 75, y + 75);

        // Clip the graphics to the hexagon shape
        Shape originalClip = graphics.getClip();
        graphics.setClip(hexagon);

        // Draw the tile's habitat image
        graphics.drawImage(imageManager.getHabitatImage(tile.getHabitat()), (x - size / 2) + 75, (y - size / 2) + 75, size, size, null);

        // Reset the clip
        graphics.setClip(originalClip);

        // Draw the outline of the hexagon
        graphics.setColor(Color.BLACK);
        graphics.setStroke(new BasicStroke(2));
        graphics.draw(hexagon);

        // Draw wildlife if present
        int wildlifeSize = (int) (size * 0.6);
        int offset = (size - wildlifeSize) / 2;
        drawAuthorizedWildlife(graphics, x, y, wildlifeSize, offset);
        if (!tile.getWildlife().isEmpty()) {
            graphics.drawImage(imageManager.getWildlifeImage(tile.getWildlife().getFirst()), x + offset, y + offset,
                    wildlifeSize, wildlifeSize, null);
        }
    }
    
    /**
     * Renders the available tile in hexagonal shape.
     * 
     * @param graphics
     * @param x
     * @param y
     */
    private void renderAvailableHexagonTile(Graphics2D graphics, int x, int y) {
        Polygon hexagon = createHexagon(x + 75, y + 75);

        // Clip the graphics to the hexagon shape
        Shape originalClip = graphics.getClip();
        graphics.setClip(hexagon);

        // Draw the tile's habitat image
        graphics.drawImage(imageManager.getHabitatImage(tile.getHabitat()), (x - size / 2) + 75, (y - size / 2) + 75, size, size, null);

        // Reset the clip
        graphics.setClip(originalClip);

        // Draw the outline of the hexagon
        graphics.setColor(Color.BLACK);
        graphics.setStroke(new BasicStroke(2));
        graphics.draw(hexagon);

        // Draw wildlife if present
        int wildlifeSize = (int) (size * 0.6);
        int offset = (size - wildlifeSize) / 2;
        drawAuthorizedWildlife(graphics, x, y, wildlifeSize, offset);
        if (!tile.getWildlife().isEmpty()) {
            graphics.drawImage(imageManager.getWildlifeImage(tile.getWildlife().getFirst()), x + offset + 140, y + offset,
                    wildlifeSize, wildlifeSize, null);
        }
    }

    /**
     * Renders the tile in square shape.
     * 
     * @param graphics
     * @param x
     * @param y
     */
    private void renderSquareTile(Graphics2D graphics, int x, int y) {
    	graphics.drawImage(imageManager.getHabitatImage(tile.getHabitat()), x, y, size, size, null);
  		int wildlifeSize = (int) (size * 0.6);
  		int offset = (size - wildlifeSize) / 2;
  		drawAuthorizedWildlife(graphics, x, y, wildlifeSize, offset);
  		if (!tile.getWildlife().isEmpty()) {
  			graphics.drawImage(imageManager.getWildlifeImage(tile.getWildlife().getFirst()), x + 25, y + 25, wildlifeSize, wildlifeSize,
  					null);
  		}
    }

    /**
     * Creates a hexagonal shape centered at the given coordinates.
     * 
     * @param x Center X coordinate
     * @param y Center Y coordinate
     * @return Polygon representing the hexagon
     */
    private Polygon createHexagon(int x, int y) {
        int halfSize = size / 2;

        Polygon hexagon = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i);
            int dx = (int) (x + halfSize * Math.cos(angle));
            int dy = (int) (y + halfSize * Math.sin(angle));
            hexagon.addPoint(dx, dy);
        }
        return hexagon;
    }

	/**
	 * Specially renders a tile if selected
	 * 
	 * @param graphics
	 * @param tile
	 */
	public void renderSelectedTile(Graphics2D graphics, GraphicalTile tile) {
		Objects.requireNonNull(tile);
		graphics.setColor(Color.YELLOW);
		graphics.setStroke(new BasicStroke(8));
		graphics.drawRect(tile.getX(), tile.getY(), tile.getSize(), tile.getSize());
	}

	/**
	 * Renders wildlife token 
	 * @param graphics
	 * @param selectedWildlife selected token
	 */
	public void renderWildlifeTile(Graphics2D graphics, Wildlife selectedWildlife) {
		Objects.requireNonNull(selectedWildlife);
		int wildlifeSize = (int) (size * 0.6);
		int offset = (size - wildlifeSize) / 2;
		int margin = 170;
		drawAuthorizedWildlife(graphics, x, y, wildlifeSize, offset);

		graphics.drawImage(imageManager.getWildlifeImage(selectedWildlife), x + margin, y + offset, wildlifeSize,
				wildlifeSize, null);
	}

	/**
	 * Specially renders a token if selected
	 * @param graphics
	 * @param selectedWildlife Selected token
	 */
	public void renderSelectedToken(Graphics2D graphics, Wildlife selectedWildlife) {
		int wildlifeSize = (int) (size * 0.6);
		int offset = (size - wildlifeSize) / 2;
		int margin = 170;
		graphics.drawOval(x + margin, y + offset, wildlifeSize, wildlifeSize);
	}

	/**
	 * Draws authorized wildlife on a tile
	 * 
	 * @param graphics
	 * @param x            X coordinate of tile
	 * @param y            Y coordinate of tile
	 * @param wildlifeSize
	 * @param offset
	 */
	public void drawAuthorizedWildlife(Graphics2D graphics, int x, int y, int wildlifeSize, int offset) {
		int horizontalSpacing = -30;
		int verticalOffset = 10;
		int i = 0;
		for (var wildlife : tile.getAuthorizedWildlife()) {
			int xPos = x + (i * horizontalSpacing) + 10;
			int yPos = y + offset + (i * verticalOffset) + 10;
			graphics.drawImage(imageManager.getWildlifeImage(wildlife), xPos + this.size / 3, yPos, wildlifeSize / 2,
					wildlifeSize / 2, null);
			i++;
		}
	}

	/**
	 * Returns whether a mouse click is on a habitat tile or not.
	 * 
	 * @param clickX X coordinate of mouse click
	 * @param clickY Y coordinate of mouse click
	 * @return true if a mouse click is on a habitat tile
	 */
	public boolean containsHabitat(int clickX, int clickY) {
		return clickX >= x && clickX <= x + size && clickY >= y && clickY <= y + size;
	}

	/**
	 * Returns whether a mouse click is on a wildlife tile or not.
	 * 
	 * @param clickX X coordinate of mouse click
	 * @param clickY Y coordinate of mouse click
	 * @return true if a mouse click is on a wildlife tile
	 */
	public boolean containsWildlife(int clickX, int clickY) {
		int wildlifeSize = (int) (size * 0.6);
		int offset = (size - wildlifeSize) / 2;
		int margin = 170;
		return clickX >= x + margin && clickX <= x + margin + wildlifeSize && clickY >= y + offset
				&& clickY <= y + offset + wildlifeSize;
	}

	/**
	 * Sets position of tile on grid based on x and y
	 * @param x
	 * @param y
	 */
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * X coordinate getter of tile
	 * @return X coordinate
	 */
	public int getX() {
		return x;
	}

	/**
	 * Y coordinate getter of tile
	 * @return Y coordinate
	 */
	public int getY() {
		return y;
	}

	/**
	 * Size getter of tile
	 * @return Size of tile
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Tile getter
	 * @return Tile
	 */
	public Tile getTile() {
		return tile;
	}

}