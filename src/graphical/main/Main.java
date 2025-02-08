package graphical.main;

import java.awt.Color;
import com.github.forax.zen.Application;

import graphical.model.GameStateManager;

public class Main {
	public static void main(String[] args) {
		Application.run(Color.BLACK, context -> {
			GameStateManager stateManager = new GameStateManager(context);
			stateManager.run();
		});
	}
}