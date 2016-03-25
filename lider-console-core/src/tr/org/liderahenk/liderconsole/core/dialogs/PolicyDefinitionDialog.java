package tr.org.liderahenk.liderconsole.core.dialogs;

import java.util.List;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.liderconsole.core.constants.LiderConstants;
import tr.org.liderahenk.liderconsole.core.editors.PolicyDefinitionEditor;
import tr.org.liderahenk.liderconsole.core.i18n.Messages;
import tr.org.liderahenk.liderconsole.core.model.Policy;
import tr.org.liderahenk.liderconsole.core.model.Profile;
import tr.org.liderahenk.liderconsole.core.rest.utils.ProfileUtils;
import tr.org.liderahenk.liderconsole.core.utils.SWTResourceManager;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;

/**
 * This dialog provides policy definition GUI. All plugins may contribute to
 * this class by specifying 'tr.org.liderahenk.liderconsole.core.policymenu'
 * extension point in their plugin.xml files.
 * 
 * @author <a href="mailto:mine.dogan@agem.com.tr">Mine Doğan</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class PolicyDefinitionDialog extends DefaultLiderDialog {

	private static final Logger logger = LoggerFactory.getLogger(PolicyDefinitionDialog.class);

	private Policy selectedPolicy;
	private PolicyDefinitionEditor editor;

	private Text txtLabel;
	private Text txtDesc;
	private Button btnActive;

	public PolicyDefinitionDialog(Shell parentShell, PolicyDefinitionEditor editor) {
		super(parentShell);
		this.editor = editor;
	}

	public PolicyDefinitionDialog(Shell parentShell, Policy selectedPolicy, PolicyDefinitionEditor editor) {
		super(parentShell);
		this.selectedPolicy = selectedPolicy;
		this.editor = editor;
	}

	/**
	 * Create policy input widgets
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		parent.setLayout(new GridLayout(1, false));

		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		composite.setLayout(new GridLayout(2, false));

		// Policy label
		Label lblLabel = new Label(composite, SWT.NONE);
		lblLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lblLabel.setText(Messages.getString("POLICY_LABEL"));

		txtLabel = new Text(composite, SWT.BORDER);
		txtLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		if (selectedPolicy != null && selectedPolicy.getLabel() != null) {
			txtLabel.setText(selectedPolicy.getLabel());
		}

		// Policy description
		Label lblDesc = new Label(composite, SWT.NONE);
		lblDesc.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lblDesc.setText(Messages.getString("DESCRIPTION"));

		txtDesc = new Text(composite, SWT.BORDER);
		txtDesc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		if (selectedPolicy != null && selectedPolicy.getDescription() != null) {
			txtDesc.setText(selectedPolicy.getDescription());
		}

		// Policy active
		btnActive = new Button(composite, SWT.CHECK);
		btnActive.setText(Messages.getString("ACTIVE"));
		btnActive.setSelection(selectedPolicy != null && selectedPolicy.isActive());
		new Label(composite, SWT.NONE);

		// Create child composite for policy
		Composite childComposite = new Composite(parent, GridData.FILL);
		childComposite.setLayout(new GridLayout(3, true));
		childComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		// Find policy contributions
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry.getExtensionPoint(LiderConstants.EXTENSION_POINTS.POLICY_MENU);
		IConfigurationElement[] config = extensionPoint.getConfigurationElements();

		if (config != null) {

			// Command service will be used to trigger handler class related to
			// specified 'profileCommandId'
			final ICommandService commandService = PlatformUI.getWorkbench().getService(ICommandService.class);

			// Iterate over each extension point provided by plugins
			for (IConfigurationElement e : config) {
				try {
					// Read extension point attributes
					String label = e.getAttribute("label");
					String pluginName = e.getAttribute("pluginName");
					String pluginVersion = e.getAttribute("pluginVersion");
					final String profileCommandId = e.getAttribute("profileCommandId");

					// Plugin label
					Label pluginLabel = new Label(childComposite, SWT.NONE);
					pluginLabel.setText(label);

					// Profiles combo
					Combo combo = new Combo(childComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
					combo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
					// Populate combo with active profiles
					List<Profile> profiles = ProfileUtils.list(pluginName, pluginVersion, null, true);
					if (profiles != null) {
						for (int i = 0; i < profiles.size(); i++) {
							Profile profile = profiles.get(i);
							combo.add(profile.getLabel() + " " + profile.getCreateDate());
							combo.setData(i + "", profile);
						}
						combo.select(0); // select first profile by default
					}

					// Add profile button
					Button btnAdd = new Button(childComposite, SWT.NONE);
					btnAdd.setText(Messages.getString("ADD"));
					btnAdd.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
					btnAdd.setImage(SWTResourceManager.getImage(LiderConstants.PLUGIN_IDS.LIDER_CONSOLE_CORE,
							"icons/16/add.png"));
					btnAdd.addSelectionListener(new SelectionListener() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							// Trigger profile handler class in order to allow
							// user add/create new profiles
							Command command = commandService.getCommand(profileCommandId);
							try {
								command.executeWithChecks(new ExecutionEvent());
								// Refresh profile combo
								// TODO
								// TODO
								// TODO
							} catch (Exception e1) {
								logger.error(e1.getMessage(), e1);
							}
						}

						@Override
						public void widgetDefaultSelected(SelectionEvent e) {
						}
					});

				} catch (Exception e1) {
					logger.error(e1.getMessage(), e1);
				}
			}
		}

		applyDialogFont(composite);
		return composite;
	}

	/**
	 * Handle OK button press
	 */
	@Override
	protected void okPressed() {

		setReturnCode(OK);

		// Check if label is empty
		if (txtLabel.getText().isEmpty()) {
			Notifier.warning(null, Messages.getString("FILL_LABEL_FIELD"));
			return;
		}

		// TODO
		// TODO
		// TODO

	}

}