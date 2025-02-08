package graphical.view.renderers;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * Various rendering methods of the menu screen.
 */
public class RenderMenu {
	private BufferedImage backgroundImage;
	private BufferedImage buttonImage;
	private int width;
	private int height;

	/**
	 * Constructor for RenderMenu
	 * @param background
	 * @param button
	 * @param screenWidth
	 * @param screenHeight
	 */
	public RenderMenu(BufferedImage background, BufferedImage button, int screenWidth, int screenHeight) {
		this.backgroundImage = Objects.requireNonNull(background);
		this.buttonImage = Objects.requireNonNull(button);
		this.width = screenWidth;
		this.height = screenHeight;
	}

	/**
	 * Renders the menu screen.
	 * 
	 * @param graphics
	 */
	public void render(Graphics2D graphics) {
		int buttonX = width / 3 + 150;
		int buttonWidth = 325;
		int buttonHeight = 110;

		int startButtonY = height / 3 + 285;
		int optionButtonY = height / 3 + 400;
		int quitButtonY = height / 3 + 515;

		// Draws menu BG
		graphics.drawImage(backgroundImage, 0, 0, width, height, null);

		// Draws menu buttons on screen
		graphics.drawImage(buttonImage, buttonX, startButtonY, buttonWidth, buttonHeight, null);
		graphics.drawImage(buttonImage, buttonX, optionButtonY, buttonWidth, buttonHeight, null);
		graphics.drawImage(buttonImage, buttonX, quitButtonY, buttonWidth, buttonHeight, null);

		// Renders menu texts
		renderMenuText(graphics, buttonX, buttonWidth, buttonHeight, startButtonY, optionButtonY, quitButtonY);
	}

	/**
	 * Renders displayed texts of the menu screen.
	 * 
	 * @param graphics
	 * @param buttonX       X coordinate of a button
	 * @param buttonWidth   Width of button
	 * @param buttonHeight  Height of button
	 * @param startButtonY  Y coordinate of the start button
	 * @param optionButtonY Y coordinate of the option button
	 * @param quitButtonY   Y coordinate of the quit button
	 */
	private void renderMenuText(Graphics2D graphics, int buttonX, int buttonWidth, int buttonHeight, int startButtonY,
			int optionButtonY, int quitButtonY) {
		graphics.setFont(new Font("Arial", Font.ITALIC, 150));
		graphics.setColor(Color.WHITE);
		graphics.drawString("CASCADIA", width / 2 - width / 5, height / 2 - height / 5); // Draws title on the menu

		graphics.setFont(new Font("DIALOG", Font.TYPE1_FONT, 40));
		graphics.setColor(Color.WHITE);

		FontMetrics metrics = graphics.getFontMetrics();

		// Draws button texts at a centered position of a button
		drawCenteredString(graphics, metrics, "JOUER", buttonX, buttonWidth, startButtonY, buttonHeight);
		drawCenteredString(graphics, metrics, "OPTIONS", buttonX, buttonWidth, optionButtonY, buttonHeight);
		drawCenteredString(graphics, metrics, "QUITTER", buttonX, buttonWidth, quitButtonY, buttonHeight);
	}

	/**
	 * Draws centered text.
	 * 
	 * @param graphics
	 * @param metrics
	 * @param text         Text to be displayed
	 * @param buttonX      X coordinate of button
	 * @param buttonWidth  Width of button
	 * @param buttonY      Y coordinate of button
	 * @param buttonHeight Height of button
	 */
	private void drawCenteredString(Graphics2D graphics, FontMetrics metrics, String text, int buttonX, int buttonWidth,
			int buttonY, int buttonHeight) {
		int textWidth = metrics.stringWidth(text);
		int textHeight = metrics.getAscent();
		int textX = buttonX + buttonWidth / 2 - textWidth / 2;
		int textY = buttonY + buttonHeight / 2 + textHeight / 2 - 10;
		graphics.drawString(text, textX, textY);
	}
}