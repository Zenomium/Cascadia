package graphical.view.resources;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Viewer class for scoring cards.
 * Loads and manages images of scoring cards.
 */
public class ScoringCardsImageManager {
    private final HashMap<String, BufferedImage> scoringCardImages;
    private final ResourceLoader resourceLoader;

    /**
     * Constructor for ScoringCardsImageManager.
     */
    public ScoringCardsImageManager() {
		this.resourceLoader = new ResourceLoader();
        this.scoringCardImages = new HashMap<>();
    }

    /**
     * Loads scoring card images.
     */
    public void loadScoringCardImages() {
        // Load images for scoring cards
        loadScoringCardImage("scoringBear", "cards/scoringBear.png");
        loadScoringCardImage("scoringHawk", "cards/scoringHawk.png"); 
        loadScoringCardImage("scoringFox", "cards/scoringFox.png");
        loadScoringCardImage("scoringSalmon", "cards/scoringSalmon.png");
        loadScoringCardImage("scoringWapiti", "cards/scoringWapiti.png");
    }

    /**
     * Loads a single scoring card image.
     *
     * @param cardName the name of the scoring card
     * @param filename the path to the image file
     */
    private void loadScoringCardImage(String cardName, String filename) {
        Objects.requireNonNull(cardName);
        Objects.requireNonNull(filename);
        resourceLoader.loadImages(new String[]{filename});
        scoringCardImages.put(cardName, resourceLoader.getImage(filename.substring(0, filename.lastIndexOf('.'))));
    }

    /**
     * Retrieves the image of a specific scoring card.
     *
     * @param cardName the name of the scoring card
     * @return the BufferedImage of the scoring card, or null if not found
     */
    public BufferedImage getScoringCardImage(String cardName) {
        Objects.requireNonNull(cardName);
        return scoringCardImages.get(cardName);
    }

    public List<String> getScoringCardNames() {
        return new ArrayList<>(scoringCardImages.keySet());
    }

}
