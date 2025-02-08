package terminal.ui;

import java.io.IO;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import core.game.Game;
import core.game.grid.Grid;
import core.game.grid.Tile;
import core.game.player.Player;
import graphical.model.GameMode;

/**
 * Manages User interface.
 */
public class GameUI {
	private Game game;

	public GameUI(Game game) {
		this.game = game;
	}

	/**
	 * Displays the starting menu.
	 */
	public void displayStartMenu() {
		IO.println("\u001B[32m**************");
		IO.println("*  CASCADIA  *");
		IO.println("**************\u001B[0m\n");
		IO.println("Bienvenue à Cascadia!\nQuelle variante du jeu souhaitez vous jouer?");
		IO.println("1. Variante familiale");
		IO.println("2. Variante intermédiaire");
		IO.println("3. Version normale");
	}

	/**
	 * Displays the proposed/available tiles to the player.
	 * 
	 * @param tiles List of available tiles
	 */
	public void displayTileChoices(List<Tile> tiles) {
		Objects.requireNonNull(tiles, "tiles cannot be null");
		System.out.println("Tuiles disponibles :");
		for (int i = 0; i < tiles.size(); i++) { // Affiche les tuiles
			Tile tile = tiles.get(i);
			System.out.println((i + 1) + ". " + tile.toString());
		}
	}

	/**
	 * Asks a player if he wants to redraw tiles if 3 proposed wildlife tokens are
	 * identical.
	 * 
	 * @return boolean
	 */
	public boolean askForRedrawPreference() {
		System.out.println("Voulez-vous repiocher si 3 jetons animaux sont identiques ? (o/n)");
		return "o".equalsIgnoreCase(IO.readln(""));
	}

	/**
	 * Displays the game ending (scores display)
	 * 
	 * @param players     List of players in the game
	 * @param playerGrids Map associating each player with their grid
	 */
	public void displayEndGame(List<Player> players, Map<Player, Grid> playerGrids) {
		Objects.requireNonNull(players, "players cannot be null");
		Objects.requireNonNull(playerGrids, "playerGrids cannot be null");

		System.out.println("\u001B[32m--------------Fin de jeu--------------------\u001B[0m");

		for (Player player : players) {
			playerGrids.get(player).displayGrid();
		}

		game.calculateAllPlayersScores(players, playerGrids, GameMode.TERMINAL);
		displayScorePanel(players);

		Player winner = determineWinner(players);
		printWinner(winner);
	}

	private void displayScorePanel(List<Player> players) {
		System.out.println("\n________SCORE________|" + createHeaderRow(players));
		System.out.println(" Total jetons faune  |" + createScoreRow(players, Player::getWildlifeScore));
		System.out.println(" Total habitat       |" + createScoreRow(players, Player::getHabitatScore));
		System.out.println(" Total bonus         |" + createScoreRow(players, Player::getBonusPoints));
		System.out.println(" Score total         |" + createScoreRow(players, Player::getTotalScore));
	}

	private void printWinner(Player winner) {
		if (winner != null) {
			System.out.println(winner.getName() + " est le vainqueur de la partie\n");
		} else {
			System.out.println("La partie est un match nul\n");
		}
	}

	/**
	 * Creates the header row with player names
	 */
	private String createHeaderRow(List<Player> players) {
		StringBuilder header = new StringBuilder();
		for (Player player : players) {
			header.append(String.format(" %-8s |", player.getName()));
		}
		return header.toString();
	}

	/**
	 * Creates a score row using the score getter method
	 */
	private String createScoreRow(List<Player> players, Function<Player, Integer> scoreGetter) {
		StringBuilder row = new StringBuilder();
		for (Player player : players) {
			row.append(String.format(" %-8d |", scoreGetter.apply(player)));
		}
		return row.toString();
	}

	/**
	 * Determines the winner of the game
	 * 
	 * @return the winning player or null
	 */
	public Player determineWinner(List<Player> players) {
		Player winner = players.get(0);
		boolean isTie = false;

		for (int i = 1; i < players.size(); i++) {
			Player currentPlayer = players.get(i);
			if (currentPlayer.getTotalScore() > winner.getTotalScore()) {
				winner = currentPlayer;
				isTie = false;
			} else if (currentPlayer.getTotalScore() == winner.getTotalScore()) {
				isTie = true;
			}
		}

		return isTie ? null : winner;
	}
}
