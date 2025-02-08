package graphical.controller.events;

import java.awt.image.BufferedImage;
import java.util.Objects;

import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.KeyboardEvent;
import com.github.forax.zen.PointerEvent;

import graphical.view.resources.*;
import graphical.view.renderers.*;

import graphical.main.Main;
import graphical.model.GameStateManager;

/**
 * Manages the end-of-game screen 
 * - Rendering and event handling.
 * - Displays the final game results
 */
public class EndEventManager {
	private final ApplicationContext context;
	private final GameStateManager stateManager;
	private BufferedImage panel;
	private BufferedImage backButton;

	/**
	 * Constructor for EndEventManager
	 * @param context
	 * @param stateManager
	 */
	public EndEventManager(ApplicationContext context, GameStateManager stateManager) {
		this.context = context;
		this.stateManager = Objects.requireNonNull(stateManager);
		initializeResources();
	}

	/**
	 * Initializes image resources of the option page.
	 */
	private void initializeResources() {
		ResourceLoader resourceLoader = new ResourceLoader();
		String[] imageNames = { "optionPanel.jpeg", "button.png" };
		resourceLoader.loadImages(imageNames);
		panel = resourceLoader.getImage("optionPanel");
		backButton = resourceLoader.getImage("button");
	}

	public void renderGameEnd() {
		RenderGameEnd endRenderer = new RenderGameEnd(stateManager, panel, backButton, stateManager.getScreenWidth(),
				stateManager.getScreenHeight());
		stateManager.getGame().calculateAllPlayersScores(stateManager.getPlayers(), stateManager.getAllGrids(), stateManager.getCurrentGameMode());
		context.renderFrame(graphics -> {
			endRenderer.render(graphics);
			endRenderer.renderScores(graphics, stateManager.getPlayers());
		});
		manageEndEvents();
	}

	/**
	 * Manages events on option page.
	 */
	private void manageEndEvents() {
		var event = context.pollOrWaitEvent(10);
		if (event == null)
			return;
		switch (event) {
		case PointerEvent e -> {
			if (e.action() == PointerEvent.Action.POINTER_UP) {
				int x = e.location().x();
				int y = e.location().y();
				detectBackToMenu(x, y);
			}
		}
		case KeyboardEvent e -> {
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
		int buttonX = stateManager.getScreenWidth() / 2 + 280;
		int buttonWidth = 300;
		int buttonHeight = 75;
		int backButtonY = stateManager.getScreenHeight() / 2 + 340;

		if (isButtonClicked(clickX, clickY, buttonX, backButtonY, buttonWidth, buttonHeight)) {
			context.dispose();
			Main.main(new String[] {});
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
}
