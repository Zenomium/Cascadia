package core.game.grid;

public class AllNeighbors implements NeighborStrategy {
	@Override
	public int[][] getNeighbors() {
		return new int[][] { { -1, -1 }, { -1, 0 }, { -1, 1 }, // Top left, Top, Top right
				{ 0, -1 }, { 0, 1 }, // Left, Right
				{ 1, -1 }, { 1, 0 }, { 1, 1 } // Bottom left, Bottom, Bottom right
		};
	}
}
