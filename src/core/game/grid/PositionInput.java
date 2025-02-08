package core.game.grid;

import java.io.IO;
import java.util.Objects;

/**
 * Manages the user input of coordinates of the grid.
 */
public class PositionInput {
	private int x;
	private int y;

	/**
	 * Creates a new instance of position input.
	 * 
	 * @param x X coordinate of the grid
	 * @param y Y coordinate of the grid
	 */
	public PositionInput(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Sets x and y coordinates to user input.
	 * 
	 * @param grid In-game grid
	 */
	public void readPositions(Grid grid) {
		Objects.requireNonNull(grid, "Grid cannot be null");
		x = verifyPositions(grid, "x");
		y = verifyPositions(grid, "y");
	}

	/**
	 * Checks the validity of the input positions on the grid.
	 * 
	 * @param grid     In-game grid
	 * @param strCoord Coordinate to set
	 * @return Input coordinate by user
	 */
	public int verifyPositions(Grid grid, String strCoord) {
		Objects.requireNonNull(grid, "Grid cannot be null");
		Objects.requireNonNull(strCoord, "Coordinate type cannot be null");
		int coord = 0;
		do {
			try {
				var input = IO.readln("Entrez la coordonnée " + strCoord + " :");
				coord = Integer.parseInt(input);
				if (coord < 0 || coord >= grid.getSize()) {
					System.out.println("La coordonnée " + strCoord + " doit être entre 0 et " + (grid.getSize() - 1));
				}
			} catch (NumberFormatException e) {
				System.out.println("/!\\ Entrez un nombre entier /!\\");
				coord = -1;
			}
		} while (coord < 0 || coord >= grid.getSize());
		return coord;
	}

	/**
	 * Getter of the X coordinate.
	 * 
	 * @return X coordinate
	 */
	public int getX() {
		return x;
	}

	/**
	 * Getter of the Y coordinate.
	 * 
	 * @return Y coordinate
	 */
	public int getY() {
		return y;
	}
}