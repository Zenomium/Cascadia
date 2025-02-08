package core.game.grid;

public class HexNeighborsPair implements NeighborStrategy {
	@Override
	public int[][] getNeighbors() {
		return new int[][] { 
			{1, 0}, {1, -1},  // Top left, Top right
			{0, -1}, {-1, -1}, // Left, Right
			{-1, 0}, {0, 1},  // Bottom left, Bottom right
		};
	}
}
