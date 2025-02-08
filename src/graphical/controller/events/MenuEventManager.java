package graphical.controller.events;

import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.KeyboardEvent;
import com.github.forax.zen.PointerEvent;

import graphical.model.GameMode;
import graphical.model.GameScene;
import graphical.model.GameStateManager;
import graphical.view.resources.*;
import graphical.view.renderers.*;

import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * Manages events on menu
 * - Checks interactions between user and menu screen
 * - Updates state of game based on choice of user
 */
public class MenuEventManager {
	private final ApplicationContext context;
	private final GameStateManager stateManager;
	private ResourceLoader resourceLoader;
	private BufferedImage background;
	private BufferedImage button;

	/**
	 * Constructor for MenuEventManager
	 * @param context
	 * @param stateManager
	 */
	public MenuEventManager(ApplicationContext context, GameStateManager stateManager) {
		this.context = context;
		this.stateManager = Objects.requireNonNull(stateManager);
		initializeResources();
	}

	/**
	 * Initializes image resources of page.
	 */
	private void initializeResources() {
		resourceLoader = new ResourceLoader();
		String[] imageNames = { "menu.jpg", "button.png" };
		resourceLoader.loadImages(imageNames);

		background = resourceLoader.getImage("menu");
		button = resourceLoader.getImage("button");
	}

	/**
	 * Renders the menu page.
	 */
	public void renderMainMenu() {
		RenderMenu menuRenderer = new RenderMenu(background, button, stateManager.getScreenWidth(),
				stateManager.getScreenHeight());
		if (background == null || button == null) {
			System.err.println("Resource error");
			return;
		}
		context.renderFrame(graphics -> {
			menuRenderer.render(graphics);
		});

		manageMainMenuEvents();
	}

	/**
	 * Manages main menu events.
	 */
	private void manageMainMenuEvents() {
		var event = context.pollOrWaitEvent(10);
		if (event == null)
			return;
		switch (event) {
		case PointerEvent e -> {
			if (e.action() == PointerEvent.Action.POINTER_UP) {
				int x = e.location().x();
				int y = e.location().y();
				detectPageSwitching(x, y);
			}
		}
		case KeyboardEvent e -> {
		}
		default -> throw new IllegalArgumentException();
		}
	}

	public void gameStartConfigDebug() {
		System.out.println("Number of players :" + stateManager.getNbOfPlayers());
		System.out.println("Selected Variant:" + stateManager.getVariant());
		GameMode selectedMode = stateManager.getCurrentGameMode();
		switch (selectedMode) {
		case GRAPHICAL_SQUARE_TILES -> System.out.println(selectedMode);
		case GRAPHICAL_HEXAGON_TILES -> System.out.println(selectedMode);
		default -> throw new IllegalArgumentException("Invalid game mode");
		}
	}

	/**
	 * Detects page switching from Menu.
	 * 
	 * @param clickX X coordinate of mouse click
	 * @param clickY Y coordinate of mouse click
	 */
	private void detectPageSwitching(int clickX, int clickY) {
		int buttonX = stateManager.getScreenWidth() / 3 + 150;
		int buttonWidth = 325;
		int buttonHeight = 110;
		int startButtonY = stateManager.getScreenHeight() / 3 + 285;
		int optionButtonY = stateManager.getScreenHeight() / 3 + 400;
		int quitButtonY = stateManager.getScreenHeight() / 3 + 515;

		if (isButtonClicked(clickX, clickY, buttonX, quitButtonY, buttonWidth, buttonHeight)) {
			stateManager.setCurrentScene(GameScene.QUIT_GAME); // Quit game
		} else if (isButtonClicked(clickX, clickY, buttonX, optionButtonY, buttonWidth, buttonHeight)) {
			stateManager.setCurrentScene(GameScene.OPTIONS); // Switch to options scene
		} else if (isButtonClicked(clickX, clickY, buttonX, startButtonY, buttonWidth, buttonHeight)) {
			stateManager.setCurrentScene(GameScene.PLAY_GAME); // Switch to game scene
			gameStartConfigDebug();
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
	 * @return
	 */
	private boolean isButtonClicked(int clickX, int clickY, int buttonX, int buttonY, int buttonWidth, int buttonHeight) {
		return (clickX >= buttonX && clickX <= buttonX + buttonWidth)
				&& (clickY >= buttonY && clickY <= buttonY + buttonHeight);
	}
}