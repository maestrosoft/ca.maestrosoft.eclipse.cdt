package ca.maestrosoft.eclipse.cdt.plugin.studio.option.ui;

import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;

public final class ListCompositeDialog extends Dialog {
   
   private Composite composite;
   private IPropertyChangeListener propertyChangeListener;
   public static final String CANCEL = "Cancel_Dialog";
   public static final String PATH_RELATIVE = "Relative path";
   public static final String PATH_RESOLUTION = "Path Resolution";
   public static final String OK = "Dispose_Dialog";
   private boolean relPathResolution;
   private PathResolution pathResolution;
   private Combo pathCombo;
   private Integer browseType;  

   /**
    * Create the dialog.
    * @param parentShell
    */
   public ListCompositeDialog(Shell parentShell) {
      super(parentShell);
      setShellStyle(SWT.CLOSE | SWT.RESIZE | SWT.TITLE);
   }

   /**
    * Create contents of the dialog.
    * @param parent
    */
   @Override
   public Control createDialogArea(Composite parent) {

      parent.addDisposeListener(new DisposeListener() {
         public void widgetDisposed(DisposeEvent e) {
            if (propertyChangeListener == null) {
               return;
            }
            if(getReturnCode() == Dialog.CANCEL) {
               propertyChangeListener.propertyChange(new PropertyChangeEvent(this, CANCEL, 0, 0));
            }
         }
      });

      Composite container = (Composite) super.createDialogArea(parent);
      composite = new Composite(container, SWT.BORDER);
      composite.setLayout(new GridLayout(1, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

      if(browseType != IOption.BROWSE_NONE) {

         Group grpPath = new Group(container, SWT.NONE);
         grpPath.setLayout(new GridLayout(4, false));
         GridData gd_grpPath = new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1);
         gd_grpPath.heightHint = 60;
         gd_grpPath.widthHint = 240;
         grpPath.setLayoutData(gd_grpPath);
         grpPath.setText("Path resolution");

         Button defRadioButton = new Button(grpPath, SWT.RADIO);
         defRadioButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               if(relPathResolution) {
                  relPathResolution = false;
                  propertyChangeListener.propertyChange(new PropertyChangeEvent(this, PATH_RELATIVE, true, false));
               }
            }
         });
         defRadioButton.setText("Default");

         new Label(grpPath, SWT.NONE);
         new Label(grpPath, SWT.NONE);
         new Label(grpPath, SWT.NONE);

         Button relRadioButton = new Button(grpPath, SWT.RADIO);
         relRadioButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               if(!relPathResolution) {
                  relPathResolution = true;
                  propertyChangeListener.propertyChange(new PropertyChangeEvent(this, PATH_RELATIVE, false, true));
               }
            }
         });

         relRadioButton.setText("Use relative path from");
         defRadioButton.setSelection(true);
         relPathResolution = false;
         pathResolution = null;

         pathCombo = new Combo(grpPath, SWT.READ_ONLY);
         pathCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent comboEvent) {
               int ordinalIndex = ((Combo)(comboEvent.getSource())).getSelectionIndex();
               PathResolution oldSelect = pathResolution;
               pathResolution = PathResolution.values()[ordinalIndex];
               propertyChangeListener.propertyChange(new PropertyChangeEvent(this, PATH_RESOLUTION, oldSelect, pathResolution));
            }
         });
         pathCombo.setItems(new String[] {"Workspace <${workspace_loc}/>", "Project <${workspace_loc}/${ProjName}/>", "Configuration <${workspace_loc}/${ProjName}/${ConfigName}/>"});
         pathCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
         pathCombo.select(2); // Default is always project configuration relative
      }
      setReturnCode(Dialog.CANCEL);

      return container;
   }

   protected void cancelPressed() {
      setReturnCode(Dialog.CANCEL);
      close();      
   }

   protected void okPressed() {
      setReturnCode(Dialog.OK);
      close();            
   }

   public Composite getFieldComposite() {
      return composite;
   }

   public void setPropertyChangeListener(IPropertyChangeListener listener) {
      propertyChangeListener = listener;
   }


   /**
    * Create contents of the button bar.
    * @param parent
    */
   @Override
   protected void createButtonsForButtonBar(Composite parent) {
      createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
      createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
   }

   /**
    * Return the initial size of the dialog.
    */
   @Override
   protected Point getInitialSize() {
      return new Point(580, 400);
   }

   public PathResolution getPathSelection() {
      
      if(pathCombo == null) {
         return PathResolution.none;
      }
      return   PathResolution.values()[pathCombo.getSelectionIndex()];
   }

   public Boolean getPathRelativite() {
      return relPathResolution;
   }

   public void setType(Integer browseType) {
      this.browseType = browseType;
   }
}
