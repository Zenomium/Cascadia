package core.game.player;

import core.game.GameVariant;

/**
 * Factory pour créer la stratégie de scoring appropriée
 */
public class ScoringStrategyFactory {
    public static PlayerScore createStrategy(GameVariant variant) {
        return switch (variant.getVariant()) {
            case 1 -> new FamilyScoring();
            case 2 -> new IntermediateScoring();
            case 3 -> new StandardScoring();
            default -> throw new IllegalArgumentException("Invalid variant");
        };
    }
}