package core.game;

import java.io.IO;

/**
 * Represents the variant for Cascadia, allowing players to choose a variant,
 * and store the variant choice.
 */
public class GameVariant {
	private int variant;

	/**
	 * Constructs a variant instance according to specified variant.
	 * 
	 * @param variant
	 */
	public GameVariant(int variant) {
		if (variant < 1 || variant > 3) {
			throw new IllegalArgumentException("Variant must be between 1 and 3");
		}
		this.variant = variant;
	}

	/**
	 * Constructor overload for default variant.
	 */
	public GameVariant() {
		this.variant = 3;
	}

	/**
	 * Prompts and validates the user input for a variant.
	 * 
	 * @return Selected variant by user
	 */
	public int variantChoice() {
		while (true) {
			var input = IO.readln("");
			try {
				variant = Integer.parseInt(input);
			} catch (NumberFormatException e) {
				System.out.println("/!\\ Entrez un nombre entier /!\\");
			}
			switch (input) {
			case "1" -> {
				return variant;
			}
			case "2" -> {
				return variant;
			}
			case "3" -> {
				return variant;
			}
			default -> IO.println("Choix invalide. (1,2 ou 3)");
			}
		}
	}

	/**
	 * Sets specified variant
	 * 
	 * @param variant the variant number to set
	 */
	public void setVariant(int variant) {
		if (variant < 1 || variant > 3) {
			throw new IllegalArgumentException("Variant must be between 1 and 3");
		}
		this.variant = variant;
	}

	/**
	 * Getter of In-game variant
	 * 
	 * @return Variant choice of user
	 */
	public int getVariant() {
		return variant;
	}

}
