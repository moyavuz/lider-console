package tr.org.liderahenk.liderconsole.core.dialogs;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;

public interface IProfileDialog {

	/**
	 * This method can be used to init some objects if necessary. Triggered on
	 * dialog instance creation.
	 */
	void init();

	/**
	 * This is the main method that can be used to profile specific input
	 * widgets.
	 * 
	 * @param composite
	 *            parent composite instance with one-column grid layout.
	 */
	void createDialogArea(Composite parent);

	/**
	 * Triggered on 'OK' button pressed. Implementation of this method provide
	 * necessary profile data that need to be saved on database.
	 * 
	 * @return
	 */
	Map<String, Object> getProfileData();

}