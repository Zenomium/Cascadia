package core.game.player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import core.game.grid.Grid;
import core.game.mechanics.Habitat;
import graphical.model.GameMode;

/**
 * Implémentation du scoring pour le variant intermédiaire (2)
 */
public class IntermediateScoring implements PlayerScore {
    @Override
    public int wildlifeScoring(Grid grid, GameMode mode) {
        var wildlifeCounts = grid.countWildlifeInGrid(grid);
        int total = 0;
        
        for (var entry : wildlifeCounts.entrySet()) {
            for (int groupSize : entry.getValue()) {
                total += switch (groupSize) {
                    case 1 -> 0;
                    case 2 -> 5;
                    case 3 -> 8;
                    default -> 12;
                };
            }
        }
        return total;
    }

    @Override
    public int habitatScoring(Grid grid) {
        Objects.requireNonNull(grid, "grid cannot be null");
        var habitats = getMaxSizeHabitat(grid);
        return habitats.values().stream().mapToInt(Integer::intValue).sum();
    }

    @Override
    public Map<String, Integer> majorityBonusPoints(Grid grid1, Grid grid2) {
        Objects.requireNonNull(grid1, "grid1 cannot be null");
        Objects.requireNonNull(grid2, "grid2 cannot be null");

        Map<String, Integer> bonusPerPlayer = new HashMap<>();
        var p1HabitatScores = getMaxSizeHabitat(grid1);
        var p2HabitatScores = getMaxSizeHabitat(grid2);

        bonusPerPlayer.put(grid1.getPlayerOnGrid(), 0);
        bonusPerPlayer.put(grid2.getPlayerOnGrid(), 0);

        for (Habitat habitat : Habitat.values()) {
            int p1Score = p1HabitatScores.getOrDefault(habitat, 0);
            int p2Score = p2HabitatScores.getOrDefault(habitat, 0);

            if (p1Score > p2Score) {
                bonusPerPlayer.put(grid1.getPlayerOnGrid(), bonusPerPlayer.get(grid1.getPlayerOnGrid()) + 2);
            } else if (p2Score > p1Score) {
                bonusPerPlayer.put(grid2.getPlayerOnGrid(), bonusPerPlayer.get(grid2.getPlayerOnGrid()) + 2);
            } else {
                bonusPerPlayer.put(grid1.getPlayerOnGrid(), bonusPerPlayer.get(grid1.getPlayerOnGrid()) + 1);
                bonusPerPlayer.put(grid2.getPlayerOnGrid(), bonusPerPlayer.get(grid2.getPlayerOnGrid()) + 1);
            }
        }

        return bonusPerPlayer;
    }
	
    /**
     * Retourne la taille maximale des groupes d'habitats pour chaque type d'habitat.
     */
    private Map<Habitat, Integer> getMaxSizeHabitat(Grid grid) {
        Objects.requireNonNull(grid, "grid cannot be null");
        var habitatPoints = grid.countHabitatInGrid(grid);
        return habitatPoints.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().max(Integer::compareTo).orElse(0)));
    }
}
    