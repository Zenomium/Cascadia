package graphical.view.renderers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

import core.game.player.Player;
import graphical.model.GameStateManager;

/**
 * Various rendering methods of the ending screen.
 */
public class RenderGameEnd {
	private final BufferedImage panel;
	private final BufferedImage backButton;
	private final int width;
	private final int height;
	private final GameStateManager stateManager;

	/**
	 * Constructor for RenderGameEnd
	 * @param panel
	 * @param backButton
	 * @param width
	 * @param height
	 */
	public RenderGameEnd(GameStateManager stateManager, BufferedImage panel, BufferedImage backButton, int width, int height) {
		this.panel = Objects.requireNonNull(panel);
		this.backButton = Objects.requireNonNull(backButton);
		this.width = width;
		this.height = height;
		this.stateManager = Objects.requireNonNull(stateManager);
	}

	/**
	 * Renders the ending screen.
	 * 
	 * @param graphics
	 */
	public void render(Graphics2D graphics) {
		graphics.drawImage(panel, 0, 0, width, height, null);
		graphics.setColor(Color.WHITE);
		graphics.setFont(new Font("Arial", Font.BOLD, 100));
		renderPrimaryButton(graphics);
	}

	/**
	 * Renders the back-to-menu button.
	 * 
	 * @param graphics
	 */
	private void renderPrimaryButton(Graphics2D graphics) {
		int buttonX = width / 2 + 280;
		int buttonWidth = 300;
		int buttonHeight = 75;
		int backButtonY = height / 2 + 340;

		graphics.drawImage(backButton, buttonX, backButtonY, buttonWidth, buttonHeight, null);
		graphics.setFont(new Font("DIALOG", Font.BOLD, 30));
		graphics.setColor(Color.WHITE);

		FontMetrics metrics = graphics.getFontMetrics();
		drawCenteredString(graphics, metrics, "RETOUR MENU", buttonX, buttonWidth, backButtonY, buttonHeight);
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

	/**
	 * Renders the score table.
	 * 
	 * @param graphics
	 * @param players  All players which their scores are displayed.
	 */
	public void renderScores(Graphics2D graphics, List<Player> players) {
	    int baseX = width / 5;
	    int baseY = height / 3;
	    int columnWidth = 200;
	    int rowHeight = 50;

	    int fontSize = Math.max(20, 30 - (players.size() - 2) * 5); // Réduire la taille avec plus de joueurs
	    graphics.setFont(new Font("Arial", Font.PLAIN, fontSize));
	    graphics.setColor(Color.BLACK);

	    // Récupérer les métriques de police
	    FontMetrics metrics = graphics.getFontMetrics();

	    int x = drawPlayersHeader(graphics, baseX, baseY, columnWidth, players, metrics);
	    graphics.setStroke(new BasicStroke(5));
	    graphics.drawLine(baseX, height / 3 + 5, baseX * 4, height / 3 + 5);
	    graphics.setStroke(new BasicStroke(1));
	    int y = drawWildlifeScore(graphics, x, baseX, baseY, columnWidth, rowHeight, players, metrics);
	    y = drawHabitatScore(graphics, x, y, baseX, baseY, columnWidth, rowHeight, players, metrics);
	    y = drawBonus(graphics, x, y, baseX, baseY, columnWidth, rowHeight, players, metrics);
	    y = drawTotalScore(graphics, x, y, baseX, baseY, columnWidth, rowHeight, players, metrics);
	    drawWinner(graphics, players, baseX, y, rowHeight);
	}


	private int drawPlayersHeader(Graphics2D graphics, int baseX, int baseY, int columnWidth, List<Player> players, FontMetrics metrics) {
	    Objects.requireNonNull(players);
	    int x = baseX;
	    x += columnWidth;
	    for (Player player : players) {
	        int textWidth = metrics.stringWidth(player.getName());
	        int textX = x + (int)(columnWidth / 1.5) - (int)(textWidth / 1.5);
	        graphics.drawString(player.getName(), textX, baseY);
	        x += (int)(columnWidth / 1.5);
	    }
	    return x;
	}


	private int drawWildlifeScore(Graphics2D graphics, int x, int baseX, int baseY, int columnWidth, int rowHeight,
			List<Player> players, FontMetrics metrics) {
		int y = baseY + rowHeight;
		graphics.drawString("Total Faune", baseX, y);
		x = baseX + columnWidth;
		for (Player player : players) {
			int textWidth = metrics.stringWidth(String.valueOf(player.getWildlifeScore()));
			int textX = x + (int)(columnWidth / 1.5) - (int)(textWidth / 1.5);
			graphics.drawString(String.valueOf(player.getWildlifeScore()), textX, y);
			x += (int)(columnWidth / 1.5);
		}
		return y;
	}

	private int drawHabitatScore(Graphics2D graphics, int x, int y, int baseX, int baseY, int columnWidth, int rowHeight,
			List<Player> players, FontMetrics metrics) {
		y += rowHeight;
		graphics.drawString("Total Habitat", baseX, y);
		x = baseX + columnWidth;
		for (Player player : players) {
			int textWidth = metrics.stringWidth(String.valueOf(player.getHabitatScore()));
			int textX = x + (int)(columnWidth / 1.5) - (int)(textWidth / 1.5);
			graphics.drawString(String.valueOf(player.getHabitatScore()), textX, y);
			x += (int)(columnWidth / 1.5);
		}
		return y;
	}

	private int drawBonus(Graphics2D graphics, int x, int y, int baseX, int baseY, int columnWidth, int rowHeight,
			List<Player> players, FontMetrics metrics) {
		y += rowHeight;
		graphics.drawString("Total Bonus", baseX, y);
		x = baseX + columnWidth;
		for (Player player : players) {
			int textWidth = metrics.stringWidth(String.valueOf(player.getBonusPoints()));
			int textX = x + (int)(columnWidth / 1.5) - (int)(textWidth / 1.5);
			graphics.drawString(String.valueOf(player.getBonusPoints()), textX, y);
			x += (int)(columnWidth / 1.5);
		}
		return y;
	}

	private int drawTotalScore(Graphics2D graphics, int x, int y, int baseX, int baseY, int columnWidth, int rowHeight,
			List<Player> players, FontMetrics metrics) {
		y += rowHeight;
		graphics.drawString("Score Total", baseX, y);
		x = baseX + columnWidth;
		for (Player player : players) {
			int textWidth = metrics.stringWidth(String.valueOf(player.getTotalScore()));
			int textX = x + (int)(columnWidth / 1.5) - (int)(textWidth / 1.5);
			graphics.drawString(String.valueOf(player.getTotalScore()), textX, y);
			x += (int)(columnWidth / 1.5);
		}
		return y;		
	}

	private void drawWinner(Graphics2D graphics, List<Player> players, int baseX, int y, int rowHeight) {
		Player winner = stateManager.getGameUI().determineWinner(players);
		y += rowHeight;
		if (winner != null) {
			graphics.drawString(winner.getName() + " est le vainqueur de la partie", baseX, y);			
		} else {
			graphics.drawString("La partie est un match nul", baseX, y);								
		}
	}
	
}
