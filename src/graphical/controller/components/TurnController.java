package graphical.controller.components;

import core.game.player.Player;
import java.util.List;

/**
 * Controls the turn-based logic in a game by managing the players and turn transitions.
 * Handles switching between players and tracking the current turn number.
 */
public class TurnController {
	private final List<Player> players;
	private final int maxTurns;
	private int currentPlayerIndex;
	private int currentTurn;

	/**
	 * Constructor for TurnController
	 * @param players
	 * @param maxTurns
	 */
	public TurnController(List<Player> players, int maxTurns) {
		this.players = players;
		this.maxTurns = maxTurns;
		this.currentPlayerIndex = 0;
		this.currentTurn = 1;
	}

	/**
	 * Getter of current player
	 * @return current Player
	 */
	public Player getCurrentPlayer() {
		return players.get(currentPlayerIndex);
	}
	
	/**
	 * Switches turns 
	 */
	public void nextTurn() {
		currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
		if (currentPlayerIndex == 0) {
			currentTurn++;
		}
	}

	/**
	 * Returns whether a game is over or not
	 * @return true if game is over
	 */
	public boolean isGameOver() {
		return currentTurn >= maxTurns && currentPlayerIndex == players.size() - 1;
	}

	/**
	 * Getter of current turn number
	 * @return Current number of turns
	 */
	public int getCurrentTurn() {
		return currentTurn;
	}

}