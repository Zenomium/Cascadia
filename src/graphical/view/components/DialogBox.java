package graphical.view.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.Objects;

import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.KeyboardEvent;
import com.github.forax.zen.PointerEvent;

/**
 * Interactive graphical dialog box. Renders the dialog box, and manages the
 * interaction between the player and the dialog box.
 */
public class DialogBox {
	private final ApplicationContext context;
	private final int x;
	private final int y;
	private final int width;
	private final int height;
	private boolean visible = false;
	private boolean response = false;
	private boolean hasResponse = false;
	private String message;
	private static final long ERROR_DISPLAY_DURATION = 2000;

	/**
	 * Constructor for DialogBox
	 * @param context
	 * @param message
	 */
	public DialogBox(ApplicationContext context, String message) {
		this.width = 500;
		this.height = 150;
		this.context = context;
		this.x = (context.getScreenInfo().width() - width) / 2;
		this.y = (context.getScreenInfo().height() - height) / 2;
		this.message = message;
		this.visible = true;
		this.hasResponse = false;
	}

	public void show(String message) {
		this.message = Objects.requireNonNull(message);
		this.visible = true;
		this.hasResponse = false;
	}

	/**
	 * Renders a prompt dialog box.
	 * 
	 * @param graphics
	 */
	public void renderPromptDialog(Graphics2D graphics) {
		graphics.setColor(Color.WHITE);
		graphics.fillRect(x, y, width, height);

		graphics.setColor(Color.BLACK);
		graphics.drawRect(x, y, width, height);

		graphics.setFont(new Font("Arial", Font.PLAIN, 16));
		FontMetrics fm = graphics.getFontMetrics();
		int messageX = x + (width - fm.stringWidth(message)) / 2;
		graphics.drawString(message, messageX, y + 50);

		graphics.setColor(Color.GRAY);
		graphics.fillRect(x + 50, y + 80, 80, 30);
		graphics.fillRect(x + width - 130, y + 80, 80, 30);

		graphics.setColor(Color.WHITE);
		graphics.drawString("Oui", x + 75, y + 100);
		graphics.drawString("Non", x + width - 105, y + 100);
		manageDialogEvents();
	}

	/**
	 * Renders an error dialog box
	 * 
	 * @param graphics
	 */
	public void renderPopUpDialog(Graphics2D graphics) {
		graphics.setColor(Color.WHITE);
		graphics.fillRect(x, y / 6, width, height / 2);

		graphics.setColor(Color.RED);
		graphics.drawRect(x, y / 6, width, height / 2);

		graphics.setFont(new Font("Arial", Font.PLAIN, 20));
		FontMetrics fm = graphics.getFontMetrics();
		int messageX = x + (width - fm.stringWidth(message)) / 2;
		graphics.drawString(message, messageX, y / 6 + 50);
		graphics.setColor(Color.YELLOW);
	}

	/**
	 * Manages events between user and a dialog box.
	 */
	public void manageDialogEvents() {
		var event = context.pollOrWaitEvent(10);
		if (event == null)
			return;
		switch (event) {
		case PointerEvent e -> {
			if (e.action() == PointerEvent.Action.POINTER_UP) {
				int x = e.location().x();
				int y = e.location().y();
				handleClick(x, y);
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
	 * Handles visibilty of a prompt dialog
	 * @param graphics
	 * @param isShowingError true if there is an error
	 * @param errorStartTime starting time of error box displaying
	 */
	public void handlePromptDialog(Graphics2D graphics, boolean isShowingError, long errorStartTime) {
		if (isShowingError) {
			renderPopUpDialog(graphics);
			isShowingError = false;
			toggleVisibility();
		}
	}

	/**
	 * Handles visibilty of a pop-up dialog
	 * @param graphics
	 * @param isShowingError true if there is an error
	 * @param errorStartTime starting time of error box displaying
	 * @return true if dialog box is displayed
	 */
	public boolean handlePopUpDialog(Graphics2D graphics, boolean isShowingError, long errorStartTime) {
		if (isShowingError) {
			renderPopUpDialog(graphics);
			if (System.currentTimeMillis() - errorStartTime > ERROR_DISPLAY_DURATION) {
				toggleVisibility();
				return false;
			}
		}
		return isShowingError;
	}

	/**
	 * Handles mouse click on interactive dialog box
	 * @param clickX X coordinate of mouse click
	 * @param clickY Y coordinate of mouse click
	 * @return true if user answered yes, either false
	 */
	public boolean handleClick(int clickX, int clickY) {
		if (clickY >= y + 80 && clickY <= y + 110) {
			if (clickX >= x + 50 && clickX <= x + 130) {
				response = true;
				hasResponse = true;
				return true;
			}
			if (clickX >= x + width - 130 && clickX <= x + width - 50) {
				response = false;
				hasResponse = true;
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether a dialog box is displayed or not
	 * @return true if dialog box is visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Toggles a dialog box's visibility
	 */
	public void toggleVisibility() {
		visible = !visible;
	}

	/**
	 * Returns whether a dialog box has been answered or not
	 * @return true if dialog box got a response
	 */
	public boolean hasResponse() {
		return hasResponse;
	}

	/**
	 * Getter of user's responsse
	 * @return true if user answered yes, or false if answered no
	 */
	public boolean getResponse() {
		return response;
	}

	/**
	 * Allows to change message of pop-up error dialog box
	 * @param message
	 */
	public void changeMessage(String message) {
		Objects.requireNonNull(message);
		this.message = message;
	}

}