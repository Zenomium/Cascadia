package core.game.grid;

public interface NeighborStrategy {
	// Retourne un tableau de coordonnées des déplacements possibles
	int[][] getNeighbors();
}
