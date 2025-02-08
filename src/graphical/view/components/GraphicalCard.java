package graphical.view.components;

import java.awt.Graphics2D;

import graphical.view.resources.ScoringCardsImageManager;

public class GraphicalCard {
	private final String cardName;
	private final ScoringCardsImageManager imageManager;
	private final int sizeX;
	private final int sizeY;

	private int x;
	private int y;

	/**
	 * Constructor for GraphicalCard
	 * @param cardName Attributed card name
	 * @param imageManager Resource manager
	 * @param sizeX
	 * @param sizeY
	 */
	public GraphicalCard(String cardName, ScoringCardsImageManager imageManager, int sizeX, int sizeY) {
		this.cardName = cardName;
		this.imageManager = imageManager;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}

	/**
	 * Renders a card on the grid.
	 * 
	 * @param graphics
	 * @param x scoringCardStartX
	 * @param y scoringCardStartY
	 */
	public void renderCardOnGrid(Graphics2D graphics, GraphicalCard scoringCard, int x, int y) {
		graphics.drawImage(imageManager.getScoringCardImage(scoringCard.getCardName()), x, y, sizeX, sizeY, null);
	}

	/**
	 * Sets position of card on grid based on x and y
	 * @param x
	 * @param y
	 */
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public String getCardName() {
		return cardName;
	}

	/**
	 * X coordinate getter of card
	 * @return X coordinate
	 */
	public int getX() {
		return x;
	}

	/**
	 * Y coordinate getter of card
	 * @return Y coordinate
	 */
	public int getY() {
		return y;
	}

	/**
	 * SizeX getter of card
	 * @return Size of card
	 */
	public int getSizeX() {
		return sizeX;
	}
	
	/**
	 * SizeY getter of card
	 * @return Size of card
	 */
	public int getSizeY() {
		return sizeY;
	}


}
