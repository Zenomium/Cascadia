package core.game.player;

import core.game.grid.*;
import core.game.mechanics.Wildlife;
import core.game.GameVariant;
import graphical.model.GameMode;

import java.util.List;
import java.util.Objects;

/**
 * Represents a player of Cascadia
 */
public class Player {
    private final String name;
    private PlayerScore scoreStrategy; // Utilise la stratégie de scoring
    private int wildlifeScore;
    private int habitatScore;
    private int bonusPoints;

    /**
     * Creates a new player with initialized name.
     *
     * @param name Name of the player
     */
    public Player(String name) {
        this.name = Objects.requireNonNull(name, "Player name cannot be null");
    }

    /**
     * Sets the scoring strategy based on the game variant.
     *
     * @param variant The game variant
     */
    public void setScoringStrategy(GameVariant variant) {
        Objects.requireNonNull(variant, "Game variant cannot be null");
        this.scoreStrategy = ScoringStrategyFactory.createStrategy(variant);
    }

    /**
     * Getter of player's name
     *
     * @return Name of player
     */
    public String getName() {
        return name;
    }

    /**
     * Places a tile on the grid.
     *
     * @param tile Tile to be placed
     * @param grid In-game grid
     * @param x    X coordinate on grid
     * @param y    Y coordinate on grid
     * @return Boolean indicating if tile is placed on the grid or not
     */
    public boolean placeTile(Tile tile, Grid grid, int x, int y) {
        Objects.requireNonNull(tile, "tile cannot be null");
        Objects.requireNonNull(grid, "grid cannot be null");
        if (grid.addTile(tile, x, y, false)) {
            System.out.println(name + " a placé une tuile en (" + y + ", " + x + ").");
            return true;
        }
        System.out.println("Placement impossible. Case déjà occupée ou inaccessible.");
        return false;
    }

    /**
     * Places a wildlife on the grid.
     *
     * @param grid     In-game grid
     * @param x        X coordinate on grid
     * @param y        Y coordinate on grid
     * @param wildlife Wildlife token to be placed on the grid
     * @return Boolean indicating if token is placed on the grid or not
     */
    public boolean placeWildlife(Grid grid, int x, int y, Wildlife wildlife) {
        Objects.requireNonNull(grid, "grid cannot be null");
        Objects.requireNonNull(wildlife, "wildlife cannot be null");
        Tile tile = grid.getTile(x, y);
        if (tile != null) {
            List<Wildlife> authorizedWildlife = tile.getAuthorizedWildlife();
            if (tile.addWildlife(wildlife, authorizedWildlife)) {
                System.out.println(name + " a ajouté un " + wildlife + " sur la tuile en (" + y + ", " + x + ").");
                tile.getAuthorizedWildlife().clear(); // Plus d'animaux autorisés
                return true;
            } else {
                System.out.println("Impossible d'ajouter le jeton faune sur cette tuile.");
            }
        } else {
            System.out.println("Il n'y a pas de tuile à cet emplacement.");
        }
        return false;
    }

    /**
     * Calculates scores of the player by comparing their grid with multiple
     * opponent grids.
     *
     * @param grid          Grid of the current player
     * @param opponentGrids List of opponent grids to compare with
     * @param mode          Current game mode
     */
    public void calculatePlayerScores(Grid grid, List<Grid> opponentGrids, GameMode mode) {
        Objects.requireNonNull(grid, "grid cannot be null");
        Objects.requireNonNull(opponentGrids, "opponentGrids cannot be null");
        Objects.requireNonNull(scoreStrategy, "Scoring strategy is not set");

        this.wildlifeScore = scoreStrategy.wildlifeScoring(grid, mode);
        this.habitatScore = scoreStrategy.habitatScoring(grid);

        this.bonusPoints = 0;
        for (Grid opponentGrid : opponentGrids) {
            Objects.requireNonNull(opponentGrid, "opponent grid cannot be null");
            this.bonusPoints += scoreStrategy.majorityBonusPoints(grid, opponentGrid).getOrDefault(name, 0);
        }
    }

    /**
     * Getter of wildlife score of the player.
     *
     * @return Wildlife score
     */
    public int getWildlifeScore() {
        return wildlifeScore;
    }

    /**
     * Getter of habitat score of the player.
     *
     * @return Habitat score
     */
    public int getHabitatScore() {
        return habitatScore;
    }

    /**
     * Getter of majority bonus points of the player.
     *
     * @return Habitat majority bonus points
     */
    public int getBonusPoints() {
        return bonusPoints;
    }

    /**
     * Getter of total score of the player.
     *
     * @return Total score
     */
    public int getTotalScore() {
        return wildlifeScore + habitatScore + bonusPoints;
    }
}
