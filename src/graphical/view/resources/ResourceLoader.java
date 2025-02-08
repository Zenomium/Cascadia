package graphical.view.resources;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;

import javax.imageio.ImageIO;

/**
 * Responsible for loading and managing image resources
 */
public class ResourceLoader {
	private final HashMap<String, BufferedImage> images;

	/**
	 * Constructor for ResourceLoader
	 */
	public ResourceLoader() {
		this.images = new HashMap<>();
	}

	/**
	 * Loads images into the game's images data.
	 * 
	 * @param imageNames List of images to be loaded
	 */
	public void loadImages(String[] imageNames) {
		Objects.requireNonNull(imageNames);

		for (var imageName : imageNames) {
			try (InputStream input = ResourceLoader.class.getResourceAsStream("/images/" + imageName)) {
				if (input == null) {
					System.err.println("Error! Image not found");
				}
				var image = ImageIO.read(input);
				String key = imageName.substring(0, imageName.lastIndexOf('.'));
				images.put(key, image);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Getter method of a specified image.
	 * 
	 * @param imageName Name of image file
	 * @return image file
	 */
	public BufferedImage getImage(String imageName) {
		Objects.requireNonNull(imageName);
		var image = images.get(imageName);
		if (image == null) {
			throw new IllegalArgumentException("Image not found");
		}
		return image;
	}

}