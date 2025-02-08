package graphical.controller.events;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IO;
import java.util.Objects;

import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.KeyboardEvent;
import com.github.forax.zen.PointerEvent;

import graphical.model.GameMode;
import graphical.model.GameScene;
import graphical.model.GameStateManager;
import graphical.view.resources.*;
import graphical.view.renderers.*;

import terminal.main.Main;

/**
 * Manages events on option page.
 * - Checks interactions between user and option screen
 * - Updates screen rendering based on events
 * - Updates state of game based on user choices
 */
public class OptionEventManager {
	private final ApplicationContext context;
	private final GameStateManager stateManager;
	private GameMode mode = GameMode.GRAPHICAL_SQUARE_TILES; // Default mode of the game is set to graphical with square
																														// tiles
	private BufferedImage panel;
	private BufferedImage backButton;
	private BufferedImage qtyButton;
	private int selectedVariant = 3; // Default variant is set to normal variant
	private int nbOfPlayers = 2; // Default number of players is set to 2

	/**
	 * Constructor for OptionEventManager
	 * @param context
	 * @param stateManager
	 */
	public OptionEventManager(ApplicationContext context, GameStateManager stateManager) {
		this.context = context;
		this.stateManager = Objects.requireNonNull(stateManager);
		initializeResources();
	}

	/**
	 * Initializes image resources of the option page.
	 */
	private void initializeResources() {
		ResourceLoader resourceLoader = new ResourceLoader();
		String[] imageNames = { "optionPanel.jpeg", "button.png", "quantityButton.png" };
		resourceLoader.loadImages(imageNames);
		panel = resourceLoader.getImage("optionPanel");
		backButton = resourceLoader.getImage("button");
		qtyButton = resourceLoader.getImage("quantityButton");
	}

	/**
	 * Renders the option page.
	 */
	public void renderOptionsPage() {
		RenderOptions optionRenderer = new RenderOptions(panel, backButton, qtyButton, stateManager.getScreenWidth(),
				stateManager.getScreenHeight());
		context.renderFrame(graphics -> {
			optionRenderer.render(graphics);
			optionRenderer.drawNumberOfPlayerInput(graphics, nbOfPlayers, stateManager.getScreenWidth() / 2 + 10,
					stateManager.getScreenHeight() / 3 - 50); // Draws the number of
			// player selected
			renderSelectedVariant(graphics, optionRenderer);
			renderSelectedGameMode(graphics, optionRenderer);
		});
		manageOptionEvents();
	}

	private void renderSelectedVariant(Graphics2D graphics, RenderOptions optionRenderer) {
		int buttonX = stateManager.getScreenWidth() / 2 - 20;
		int buttonY = stateManager.getScreenHeight() / 3;
		int buttonDiameter = 20;
		switch (selectedVariant) { // Renders tick of selected variant
		case 1 -> optionRenderer.renderSelectedRadioButton(graphics, buttonX, buttonY + 80, buttonDiameter); // first variant selected
		
		case 2 -> optionRenderer.renderSelectedRadioButton(graphics, buttonX, buttonY + 155, buttonDiameter); // second variant selected
		
		case 3 -> optionRenderer.renderSelectedRadioButton(graphics, buttonX, buttonY + 230, buttonDiameter); // third variant selected
		
		default -> throw new IllegalArgumentException("Unvalid selection");
		}
	}

	private void renderSelectedGameMode(Graphics2D graphics, RenderOptions optionRenderer) {
		int buttonXGame = stateManager.getScreenWidth() / 2 - 20;
		int buttonYGame = stateManager.getScreenHeight() / 3 + 235;
		int buttonDiameterGame = 20;
		switch (mode) {
		case GameMode.TERMINAL ->
			optionRenderer.renderSelectedRadioButton(graphics, buttonXGame, buttonYGame + 80, buttonDiameterGame); // first
																																																							// mode
																																																							// selected
		case GameMode.GRAPHICAL_SQUARE_TILES ->
			optionRenderer.renderSelectedRadioButton(graphics, buttonXGame, buttonYGame + 155, buttonDiameterGame); // second
																																																							// mode
																																																							// selected
		case GameMode.GRAPHICAL_HEXAGON_TILES ->
			optionRenderer.renderSelectedRadioButton(graphics, buttonXGame, buttonYGame + 230, buttonDiameterGame); // third
																																																							// mode
																																																							// selected
		default -> throw new IllegalArgumentException("Unvalid selection");
		}
	}

	/**
	 * Manages events on option page.
	 */
	private void manageOptionEvents() {
		var event = context.pollOrWaitEvent(10);
		if (event == null)
			return;
		switch (event) {
		case PointerEvent e -> {
			if (e.action() == PointerEvent.Action.POINTER_UP) {
				int x = e.location().x();
				int y = e.location().y();
				detectBackToMenu(x, y);
				detectChangeNumberPlayer(x, y);
				detectVariantChoice(x, y);
				detectGameModeChoice(x, y);
			}
		}
		case KeyboardEvent e -> {
			switch (e.action()) {
			case KEY_RELEASED, KEY_PRESSED -> {
			} // Ignore key events
			}
		}
		default -> throw new IllegalArgumentException();
		}
	}

	/**
	 * Detects back to menu event.
	 * 
	 * @param clickX X coordinate of mouse click
	 * @param clickY Y coordinate of mouse click
	 */
	private void detectBackToMenu(int clickX, int clickY) {
		int buttonX = stateManager.getScreenWidth() / 2 + 350;
		int buttonWidth = 200;
		int buttonHeight = 75;
		int backButtonY = stateManager.getScreenHeight() / 2 + 325;

		if (isButtonClicked(clickX, clickY, buttonX, backButtonY, buttonWidth, buttonHeight)) {
			stateManager.setCurrentScene(GameScene.MAIN_MENU); // Switch back to main menu
		}
	}

	/**
	 * Detects if user selected the number of player.
	 * 
	 * @param clickX X coordinate of mouse click
	 * @param clickY Y coordinate of mouse click
	 */
	private void detectChangeNumberPlayer(int clickX, int clickY) {
		int buttonX = stateManager.getScreenWidth() / 2 + 150;
		int buttonY = stateManager.getScreenHeight() / 3 - 85;
		int miniButtonWidth = 50;
		int miniButtonHeight = 50;

		if (isButtonClicked(clickX, clickY, buttonX - 50, buttonY, miniButtonWidth, miniButtonHeight)) { // Decrease the
																																																			// number of
																																																			// player
			IO.println("User decreased the number of player : -");
			if (nbOfPlayers > 1)
				nbOfPlayers--;
		} else if (isButtonClicked(clickX, clickY, buttonX, buttonY, miniButtonWidth, miniButtonHeight)) { // Increase the
																																																				// number of
																																																				// player
			IO.println("User increased the number of player : +");
			if (nbOfPlayers < 4)
				nbOfPlayers++;
		}
		stateManager.setNbOfPlayers(nbOfPlayers);
	}

	/**
	 * Detects if user selected a variant.
	 * 
	 * @param clickX X coordinate of mouse click
	 * @param clickY Y coordinate of mouse click
	 */
	public void detectVariantChoice(int clickX, int clickY) {
		int buttonX = stateManager.getScreenWidth() / 2 - 5;
		int buttonY = stateManager.getScreenHeight() / 3 - 50;
		int buttonDiameter = 20;
		if (isRadioButtonClicked(clickX, clickY, buttonX, buttonY + 80, buttonDiameter)) {
			IO.println("User selected the family variant");
			selectedVariant = 1;
		} else if (isRadioButtonClicked(clickX, clickY, buttonX, buttonY + 155, buttonDiameter)) {
			IO.println("User selected the intermediate variant");
			selectedVariant = 2;
		} else if (isRadioButtonClicked(clickX, clickY, buttonX, buttonY + 230, buttonDiameter)) {
			IO.println("User selected the normal variant");
			selectedVariant = 3;
		}
		stateManager.setVariant(selectedVariant);
	}

	/**
	 * Detects the game mode chosen by the user.
	 * 
	 * @param clickX X coordinate of mouse click
	 * @param clickY Y coordinate of mouse click
	 */
	public void detectGameModeChoice(int clickX, int clickY) {
		int buttonX = stateManager.getScreenWidth() / 2 - 20;
		int buttonY = stateManager.getScreenHeight() / 3 + 185;
		int buttonDiameter = 20;
		if (isRadioButtonClicked(clickX, clickY, buttonX, buttonY + 80, buttonDiameter)) {
			IO.println("User selected the command-line game mode");
			mode = GameMode.TERMINAL;
			stateManager.setCurrentScene(GameScene.QUIT_GAME); // Quit the graphical game
			context.dispose();
			Main.main(new String[] {}); // Calls main method of the command-line version
		} else if (isRadioButtonClicked(clickX, clickY, buttonX, buttonY + 155, buttonDiameter)) {
			IO.println("User selected the graphical with square tiles game mode");
			mode = GameMode.GRAPHICAL_SQUARE_TILES;
			stateManager.setGameMode(mode);
		} else if (isRadioButtonClicked(clickX, clickY, buttonX, buttonY + 230, buttonDiameter)) {
			IO.println("User selected the graphical with hexagon tiles game mode");
			mode = GameMode.GRAPHICAL_HEXAGON_TILES;
			stateManager.setGameMode(mode);
		}
	}

	/**
	 * Checks if button is clicked.
	 * 
	 * @param clickX       X coordinate of mouse click
	 * @param clickY       Y coordinate of mouse click
	 * @param buttonX      X coordinate of button
	 * @param buttonY      Y coordinate of button
	 * @param buttonWidth  Width of button
	 * @param buttonHeight Height of button
	 * @return true if mouse click on button
	 */
	private boolean isButtonClicked(int clickX, int clickY, int buttonX, int buttonY, int buttonWidth, int buttonHeight) {
		return (clickX >= buttonX && clickX <= buttonX + buttonWidth)
				&& (clickY >= buttonY && clickY <= buttonY + buttonHeight);
	}

	/**
	 * Checks if a radio button is clicked.
	 * 
	 * @param clickX  X coordinate of mouse click
	 * @param clickY  Y coordinate of mouse click
	 * @param centerX X coordinate of the circle center
	 * @param centerY Y coordinate of the circle center
	 * @param radius  Radius of the radio button
	 * @return true if mouse click on button
	 */
	private boolean isRadioButtonClicked(int clickX, int clickY, int centerX, int centerY, int radius) {
		double distance = Math.sqrt(Math.pow(clickX - centerX, 2) + Math.pow(clickY - centerY, 2));
		return distance <= radius;
	}

}
