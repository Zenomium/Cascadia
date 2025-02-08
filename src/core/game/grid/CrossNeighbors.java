package core.game.grid;

public class CrossNeighbors implements NeighborStrategy {
	@Override
	public int[][] getNeighbors() {
		return new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } // Haut, Bas, Droite, Gauche
		};
	}
}
