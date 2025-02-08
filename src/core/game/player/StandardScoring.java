package core.game.player;

import core.game.grid.Grid;
import core.game.mechanics.Habitat;
import core.game.mechanics.Wildlife;
import graphical.model.GameMode;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implémentation du scoring pour le variant normal (3)
 */
public class StandardScoring implements PlayerScore {

    @Override
    public int wildlifeScoring(Grid grid, GameMode mode) {
        Objects.requireNonNull(grid, "grid cannot be null");
        var wildlifeCounts = grid.countWildlifeInGrid(grid);
        int totalWildlifePoints = 0;
        int numberOfBearPairs = 0;
        int numberOfSingleBuzzards = 0;

        for (var entry : wildlifeCounts.entrySet()) {
            Wildlife wildlife = entry.getKey();
            for (int groupSize : entry.getValue()) {
                if (wildlife.equals(Wildlife.Ours) && groupSize == 2) {
                    numberOfBearPairs++;
                }
                if (wildlife.equals(Wildlife.Buse) && groupSize == 1) {
                    numberOfSingleBuzzards++;
                }
                totalWildlifePoints += calculateWildlifePoints(groupSize, wildlife);
            }
        }

        // Ajout des points spécifiques pour les ours, les buses et les renards
        totalWildlifePoints += calculateBearsPoints(numberOfBearPairs);
        totalWildlifePoints += calculateBuzzardsPoints(numberOfSingleBuzzards);
        totalWildlifePoints += grid.countIndividualFoxPoints();

        return totalWildlifePoints;
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
     * Calcule les points pour un groupe de faune spécifique en fonction de sa taille.
     */
    private int calculateWildlifePoints(int groupSize, Wildlife wildlife) {
        return switch (wildlife) {
            case Wapiti -> calculateWapitisPoints(groupSize);
            case Saumon -> calculateSalmonsPoints(groupSize);
            default -> 0; // Ours, buses et renards sont traités séparément
        };
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

    private int calculateWapitisPoints(int groupSize) {
        return switch (groupSize) {
            case 0 -> 0;
            case 1 -> 2;
            case 2 -> 4;
            case 3 -> 7;
            case 4 -> 10;
            case 5 -> 14;
            case 6 -> 18;
            case 7 -> 23;
            default -> 28; // 8+ Wapitis
        };
    }

    private int calculateSalmonsPoints(int groupSize) {
        return switch (groupSize) {
            case 0 -> 0;
            case 1 -> 2;
            case 2 -> 5;
            case 3 -> 8;
            case 4 -> 12;
            case 5 -> 16;
            case 6 -> 20;
            default -> 25; // 7+ saumons
        };
    }

    private int calculateBearsPoints(int numberOfPairs) {
        return switch (numberOfPairs) {
            case 0 -> 0;
            case 1 -> 4;
            case 2 -> 11;
            case 3 -> 19;
            default -> 27; // 4+ paires d'ours
        };
    }

    private int calculateBuzzardsPoints(int numberOfSingleBuzzards) {
        return switch (numberOfSingleBuzzards) {
            case 0 -> 0;
            case 1 -> 2;
            case 2 -> 5;
            case 3 -> 8;
            case 4 -> 11;
            case 5 -> 14;
            case 6 -> 18;
            case 7 -> 22;
            default -> 26; // 8+ buses isolées
        };
    }

	public static int calculateFoxesPoints(int surroundingSpeciesCount) {
        return switch (surroundingSpeciesCount) {
	        case 0 -> 0; // 1 point par espèce
	        case 1 -> 1;
	        case 2 -> 2;
	        case 3 -> 3;
	        case 4 -> 4;
	        case 5 -> 5;
	        default -> throw new IllegalStateException("Only 5 species are used");
        };
	}
}
