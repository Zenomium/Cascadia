package graphical.view.components;

import com.github.forax.zen.ApplicationContext;

/**
 * Provides access to different types of dialog boxes
 */
public class Dialogs {
	private final DialogBox dialog;
	private final DialogBox errorDialog;

	/**
	 * Constructor for Dialogs 
	 * - Initializes different types of dialog boxes
	 * @param context
	 */
	public Dialogs(ApplicationContext context) {
		this.dialog = new DialogBox(context, null);
		this.errorDialog = new DialogBox(context, null);
	}

	/**
	 * Interactive Dialog box getter
	 * @return 
	 */
	public DialogBox getDialog() {
		return dialog;
	}

	/**
	 * Error dialog box getter
	 * @return
	 */
	public DialogBox getErrorDialog() {
		return errorDialog;
	}

}
