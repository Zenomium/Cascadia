package graphical.view.renderers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * Various rendering methods of the option screen
 */
public class RenderOptions {
	private final BufferedImage panel;
	private final BufferedImage backButton;
	private final BufferedImage qtyButton;
	private final int width;
	private final int height;

	/**
	 * Constructor for RenderOptions
	 * @param panel
	 * @param backButton
	 * @param qtyButton
	 * @param width
	 * @param height
	 */
	public RenderOptions(BufferedImage panel, BufferedImage backButton, BufferedImage qtyButton, int width, int height) {
		this.panel = Objects.requireNonNull(panel);
		this.backButton = Objects.requireNonNull(backButton);
		this.qtyButton = Objects.requireNonNull(qtyButton);
		this.width = width;
		this.height = height;
	}

	/**
	 * Renders the option screen.
	 * 
	 * @param graphics
	 */
	public void render(Graphics2D graphics) {
		graphics.drawImage(panel, 0, 0, width, height, null);
		graphics.setColor(Color.WHITE);
		graphics.setFont(new Font("Arial", Font.BOLD, 100));

		renderPrimaryButton(graphics);
		renderOptionText(graphics);
		renderQuantityButton(graphics);
		renderVariantRadioButtons(graphics);
		renderGameModeRadioButtons(graphics);
	}

	/**
	 * Renders displayed text of the option screen.
	 * 
	 * @param graphics
	 */
	private void renderOptionText(Graphics2D graphics) {
		graphics.setColor(Color.GREEN);
		graphics.setFont(new Font("DIALOG", Font.BOLD, 40));
		var nbPlayersText = "- Nombre de joueurs :";
		var variantChoiceText = "- Choix du variant :";
		var gameModeText = "- Mode de jeu :";
		int textWidth = graphics.getFontMetrics().stringWidth(nbPlayersText);
		graphics.drawString(nbPlayersText, width / 3 - textWidth + 200, height / 3 - 50);
		graphics.setStroke(new BasicStroke(5));
		graphics.drawLine(width / 5, height / 2 - 175, height, height / 2 - 175);
		graphics.setStroke(new BasicStroke(1));
		graphics.drawString(variantChoiceText, width / 3 - textWidth + 200, height / 3 + 50);
		graphics.drawString(gameModeText, width / 3 - textWidth + 200, height / 3 + 285);
	}

	/**
	 * Renders the back-to-menu button.
	 * 
	 * @param graphics
	 */
	private void renderPrimaryButton(Graphics2D graphics) {
		int buttonX = width / 2 + 350;
		int buttonWidth = 200;
		int buttonHeight = 75;
		int backButtonY = height / 2 + 325;

		graphics.drawImage(backButton, buttonX, backButtonY, buttonWidth, buttonHeight, null);
		graphics.setFont(new Font("DIALOG", Font.BOLD, 30));
		graphics.setColor(Color.WHITE);

		FontMetrics metrics = graphics.getFontMetrics();
		drawCenteredString(graphics, metrics, "RETOUR", buttonX, buttonWidth, backButtonY, buttonHeight);
	}

	/**
	 * Renders the quantity button selection of player.
	 * 
	 * @param graphics
	 */
	private void renderQuantityButton(Graphics2D graphics) {
		int buttonX = width / 2 + 150;
		int buttonY = height / 3 - 85;
		int miniButtonWidth = 50;
		int miniButtonHeight = 50;
		graphics.drawImage(qtyButton, buttonX - 50, buttonY, miniButtonWidth, miniButtonHeight, null);
		graphics.drawImage(qtyButton, buttonX, buttonY, miniButtonWidth, miniButtonHeight, null);

		graphics.setFont(new Font("Arial", Font.TYPE1_FONT, 50));
		FontMetrics metrics = graphics.getFontMetrics();
		drawCenteredString(graphics, metrics, "-", buttonX - 50, miniButtonWidth, buttonY - 3, miniButtonHeight);
		drawCenteredString(graphics, metrics, "+", buttonX, miniButtonWidth, buttonY, miniButtonHeight);
	}

	private void renderRadioButton(Graphics2D graphics, int x, int y, int diameter) {
		graphics.setColor(Color.BLACK);
		graphics.drawOval(x, y, diameter, diameter);
	}

	private void renderVariantRadioButtons(Graphics2D graphics) {
		int buttonX = width / 2 - 20;
		int buttonY = height / 3 - 50;
		int buttonDiameter = 20;

		for (int i = 0; i < 3; i++) {
			renderRadioButton(graphics, buttonX, buttonY + i * 75 + 80, buttonDiameter);
			graphics.setFont(new Font("Arial", Font.BOLD, 35));
			graphics.setColor(Color.GREEN);
			drawVariantChoices(graphics, i, buttonX, buttonY);
		}
	}

	private void renderGameModeRadioButtons(Graphics2D graphics) {
		int buttonX = width / 2 - 20;
		int buttonY = height / 3 + 185;
		int buttonDiameter = 20;

		for (int i = 0; i < 3; i++) {
			renderRadioButton(graphics, buttonX, buttonY + i * 75 + 80, buttonDiameter);
			graphics.setFont(new Font("Arial", Font.BOLD, 35));
			graphics.setColor(Color.GREEN);
			drawGameModeChoices(graphics, i, buttonX, buttonY);
		}
	}

	/**
	 * Draws the selected number of player.
	 * @param graphics
	 * @param number Number of player
	 * @param coordX X coordinate of mouse click
	 * @param coordY Y coordinate of mouse click
	 */
	public void drawNumberOfPlayerInput(Graphics2D graphics, int number, int coordX, int coordY) {
		var numberString = String.valueOf(number);
		graphics.drawString(numberString, coordX - 20, coordY + 5);
	}

	/**
	 * Renders the tick of a selected variant.
	 * 
	 * @param graphics
	 * @param x          X coordinate of radio button
	 * @param y          Y coordinate of radio button
	 * @param diameter   Diameter of radio button
	 */
	public void renderSelectedRadioButton(Graphics2D graphics, int x, int y, int diameter) {
		graphics.setColor(Color.GREEN);
		var innerDiameter = (int) (diameter * 0.5);
		int innerX = x + (diameter - innerDiameter) / 2;
		int innerY = y + (diameter - innerDiameter) / 2 - 50;
		graphics.fillOval(innerX, innerY, innerDiameter, innerDiameter);
	}

	/**
	 * Draws variant choices on screen
	 * 
	 * @param graphics
	 * @param number   Number identifier of variant
	 * @param buttonX  X coordinate of variant
	 * @param buttonY  Y coordinate of variant
	 */
	private void drawVariantChoices(Graphics2D graphics, int number, int buttonX, int buttonY) {
		switch (number) {
		case 0 -> graphics.drawString("Variante Familiale", buttonX + 40, buttonY + number * 100 + 100);
		case 1 -> graphics.drawString("Variante Intermédiaire", buttonX + 40, buttonY + number * 100 + 75);
		case 2 -> graphics.drawString("Variante Normale", buttonX + 40, buttonY + number * 100 + 50);
		default -> throw new IllegalArgumentException("Unvalid number");
		}
	}

	/**
	 * Draws game mode choices on screen
	 * 
	 * @param graphics
	 * @param number   Number identifier of game mode
	 * @param buttonX  X coordinate of text
	 * @param buttonY  Y coordinate of text
	 */
	private void drawGameModeChoices(Graphics2D graphics, int number, int buttonX, int buttonY) {
		switch (number) {
		case 0 -> graphics.drawString("Version sur Terminal", buttonX + 40, buttonY + number * 100 + 100);
		case 1 -> graphics.drawString("Graphique et tuiles carrées", buttonX + 40, buttonY + number * 100 + 75);
		case 2 -> graphics.drawString("Graphique et tuiles hexagonales", buttonX + 40, buttonY + number * 100 + 50);
		default -> throw new IllegalArgumentException("Unvalid number");
		}
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
		Objects.requireNonNull(text);
		int textWidth = metrics.stringWidth(text);
		int textHeight = metrics.getAscent();
		int textX = buttonX + buttonWidth / 2 - textWidth / 2;
		int textY = buttonY + buttonHeight / 2 + textHeight / 2 - 10;
		graphics.drawString(text, textX, textY);
	}

}
