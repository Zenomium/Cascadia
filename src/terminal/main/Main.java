package terminal.main;

import core.game.*;
import graphical.model.GameMode;

/**
 * Main of Cascadia project.
 */
public class Main {
	/**
	 * Main method of Cascadia.
	 * 
	 * @param args Not used
	 */
	public static void main(String[] args) {
		Game game = new Game();
		game.addPlayer("Joueur 1", GameMode.TERMINAL);
		game.addPlayer("Joueur 2", GameMode.TERMINAL);
		game.startGame();
	}
}
