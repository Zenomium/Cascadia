package graphical.view.resources;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Objects;

import core.game.mechanics.Habitat;
import core.game.mechanics.Wildlife;
import graphical.model.GameMode;
import graphical.model.GameStateManager;

/**
 * Viewer class of tile.
 * Translates core tiles to graphic.
 */
public class TileImageManager {
	private final HashMap<Habitat, BufferedImage> habitatImages;
	private final HashMap<Wildlife, BufferedImage> wildlifeImages;
	private final ResourceLoader resourceLoader;
	private final GameStateManager gameStateManager;

	/**
	 * Constructor for TileImageManager
	 * @param gameStateManager State of game
	 */
	public TileImageManager(GameStateManager gameStateManager) {
		this.gameStateManager = gameStateManager;
		this.resourceLoader = new ResourceLoader();
		this.habitatImages = new HashMap<>();
		this.wildlifeImages = new HashMap<>();
	}

	/**
	 * Loads tile images based on game mode
	 */
	public void loadTileImages() {
		if (gameStateManager.getCurrentGameMode() == GameMode.GRAPHICAL_HEXAGON_TILES) {
			loadHexagonImages();
		} else if (gameStateManager.getCurrentGameMode() == GameMode.GRAPHICAL_SQUARE_TILES) {
			loadSquareImages();
		}
	}

	private void loadHexagonImages() {
		// Habitats loader
		loadHabitatImage(Habitat.Forêt, "habitats/forest-hexagonal.png");
		loadHabitatImage(Habitat.Fleuve, "habitats/river-hexagonal.png");
		loadHabitatImage(Habitat.Prairie, "habitats/prairie-hexagonal.png");
		loadHabitatImage(Habitat.Marais, "habitats/wetland-hexagonal.png");
		loadHabitatImage(Habitat.Montagne, "habitats/mountain-hexagonal.png");
		// Wildlife tokens loader
		loadWildlifeImage(Wildlife.Ours, "wildlife/bear.png");
		loadWildlifeImage(Wildlife.Buse, "wildlife/hawk.png");
		loadWildlifeImage(Wildlife.Wapiti, "wildlife/elk.png");
		loadWildlifeImage(Wildlife.Renard, "wildlife/fox.png");
		loadWildlifeImage(Wildlife.Saumon, "wildlife/salmon.png");
	}

	private void loadSquareImages() {
		// Habitats loader
		loadHabitatImage(Habitat.Forêt, "habitats/forest-square.jpg");
		loadHabitatImage(Habitat.Fleuve, "habitats/river-square.jpg");
		loadHabitatImage(Habitat.Prairie, "habitats/prairie-square.jpeg");
		loadHabitatImage(Habitat.Marais, "habitats/wetland-square.jpg");
		loadHabitatImage(Habitat.Montagne, "habitats/mountain-square.jpg");
		// Wildlife tokens loader
		loadWildlifeImage(Wildlife.Ours, "wildlife/bear.png");
		loadWildlifeImage(Wildlife.Buse, "wildlife/hawk.png");
		loadWildlifeImage(Wildlife.Wapiti, "wildlife/elk.png");
		loadWildlifeImage(Wildlife.Renard, "wildlife/fox.png");
		loadWildlifeImage(Wildlife.Saumon, "wildlife/salmon.png");
	}


	private void loadHabitatImage(Habitat habitat, String filename) {
		Objects.requireNonNull(habitat);
		Objects.requireNonNull(filename);
		resourceLoader.loadImages(new String[] { filename });
		habitatImages.put(habitat, resourceLoader.getImage(filename.substring(0, filename.lastIndexOf('.'))));
	}

	private void loadWildlifeImage(Wildlife wildlife, String filename) {
		Objects.requireNonNull(wildlife);
		Objects.requireNonNull(filename);
		resourceLoader.loadImages(new String[] { filename });
		wildlifeImages.put(wildlife, resourceLoader.getImage(filename.substring(0, filename.lastIndexOf('.'))));
	}

	public BufferedImage getHabitatImage(Habitat habitat) {
		Objects.requireNonNull(habitat);
		return habitatImages.get(habitat);
	}

	public BufferedImage getWildlifeImage(Wildlife wildlife) {
		Objects.requireNonNull(wildlife);
		return wildlifeImages.get(wildlife);
	}
}
