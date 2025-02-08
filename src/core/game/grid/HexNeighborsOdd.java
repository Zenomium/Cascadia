package core.game.grid;

public class HexNeighborsOdd implements NeighborStrategy {
	@Override
	public int[][] getNeighbors() {
		return new int[][] { 
            {1, 1}, {1, 0},   // Top right, Top left
            {0, -1}, {-1, 0}, // Left, Bottom left
            {-1, 1}, {0, 1},  // Bottom right, Right
        };
	}
}
