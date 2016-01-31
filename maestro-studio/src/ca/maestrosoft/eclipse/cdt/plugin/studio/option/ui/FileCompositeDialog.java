package ca.maestrosoft.eclipse.cdt.plugin.studio.option.ui;

import gov.nih.nci.cbiit.cmts.util.ResourceUtils;

import java.net.URL;
import java.util.ArrayList;

import org.eclipse.cdt.core.cdtvariables.CdtVariableException;
import org.eclipse.cdt.core.cdtvariables.ICdtVariable;
import org.eclipse.cdt.internal.core.resources.ResourceLookup;
import org.eclipse.cdt.internal.ui.newui.Messages;
import org.eclipse.cdt.managedbuilder.core.IHoldsOptions;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.internal.macros.OptionContextData;
import org.eclipse.cdt.managedbuilder.macros.IBuildMacroProvider;
import org.eclipse.cdt.managedbuilder.ui.properties.ToolSettingsPrefStore;
import org.eclipse.cdt.ui.CDTSharedImages;
import org.eclipse.cdt.ui.newui.CDTStatusInfo;
import org.eclipse.cdt.ui.newui.TypedCDTViewerFilter;
import org.eclipse.cdt.utils.cdtvariables.CdtVariableResolver;
import org.eclipse.cdt.utils.cdtvariables.IVariableContextInfo;
import org.eclipse.cdt.utils.cdtvariables.IVariableSubstitutor;
import org.eclipse.cdt.utils.cdtvariables.SupplierBasedCdtVariableManager;
import org.eclipse.cdt.utils.cdtvariables.SupplierBasedCdtVariableSubstitutor;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import ca.maestrosoft.eclipse.cdt.plugin.studio.Activator;


@SuppressWarnings("restriction")
public final class FileCompositeDialog extends Dialog {
      
   public static String FileListControl_abspath;
   public static String FileListControl_relpath;

   private static final String ABSPATH_STR = "Absolute path";
   private static final String RELPATH_STR = "Relative Path";
   private static final String CMPLEDIT_STR = "Edit Done";
   
   public static final String IMG_FILELIST_RELPATH  = "icons/elcl16/list-relpath.gif"; //$NON-NLS-1$
   public static final String IMG_FILELIST_ABSPATH  = "icons/elcl16/list-abspath.gif"; //$NON-NLS-1$
   public static final String IMG_FILELIST_CMPLEDIT = "icons/elcl16/edit-done.gif"; //$NON-NLS-1$

   private final Image IMG_RELPATH  = FileCompositeDialog.getImage(IMG_FILELIST_RELPATH);
   private final Image IMG_ABSPATH  = FileCompositeDialog.getImage(IMG_FILELIST_ABSPATH); 
   private final Image IMG_CMPLEDIT = FileCompositeDialog.getImage(IMG_FILELIST_CMPLEDIT); 
   private final Image IMG_EDIT     = CDTSharedImages.getImage(CDTSharedImages.IMG_FILELIST_EDIT);


   /* Variable names */
   /* See CdtMacroSupplier: used for making absolute paths relative if desired */
   private static final String WORKSPACELOC_VAR = "workspace_loc"; //$NON-NLS-1$
   private static final String PROJECTNAME_VAR = "ProjName"; //$NON-NLS-1$
   private static final IPath PROJECTNAME_PATH = new Path(VariablesPlugin.getDefault().getStringVariableManager().generateVariableExpression(PROJECTNAME_VAR, null));

   /* Names, messages and titles */
   private static final String WORKSPACE_DIR_DIALOG_TITLE = Messages.BrowseEntryDialog_wsp_dir_dlg_title;
   private static final String WORKSPACE_FILE_DIALOG_TITLE = Messages.BrowseEntryDialog_wsp_file_dlg_title;
   private static final String WORKSPACE_DIR_DIALOG_MSG = Messages.FileListControl_BrowseEntryDialog_wsp_dir_dlg_msg;
   private static final String WORKSPACE_FILE_DIALOG_MSG = Messages.FileListControl_BrowseEntryDialog_wsp_file_dlg_msg;
   private static final String WORKSPACE_FILE_DIALOG_ERR = Messages.FileListControl_BrowseEntryDialog_wsp_file_dlg_err;
   private static final String FILESYSTEM_DIR_DIALOG_MSG = Messages.BrowseEntryDialog_fs_dir_dlg_msg;
   private static final String EDIT_STR = Messages.FileListControl_edit;
   

   public static final int BROWSE_NONE = 0;
   public static final int BROWSE_FILE = 1;
   public static final int BROWSE_DIR = 2;


   private Composite composite;
   private IPropertyChangeListener propertyChangeListener;
   public static final String CANCEL = "Cancel_Dialog";
   public static final String PATH_RELATIVE = "Relative path";
   public static final String PATH_RESOLUTION = "Path Resolution";
   public static final String OK = "Dispose_Dialog";
   private static final int DEF_PATHRESOL = 2;
   
   
   private boolean relPathResolution;
   private PathResolution pathResolution;
   private Combo pathCombo;
   private Integer browseType;   
   private StringFieldEditor stringFieldEditor;
   private Composite container;
   private Rectangle initBound;
   private Composite dialog;
   private IVariableContextInfo contextInfo;
   private Path pathResVariable;
   private Path absolutePathRes;
   private Boolean relativePath;

   //private int type;
   private String[] values = new String[0];
   private String filterPath;
   private String[] filterExtensions;
   private Text editPathText;
   private ToolItem absPathItem;
   private ToolItem relPathItem;
   private ToolItem editPathItem;
   private String initPathText;
   private boolean editableText;
   private FocusListener diagFocusListener;
   private String absSegment;
   private String dirEOF;
   private String extraArgument;
   
   /**
    * Create the dialog.
    * @param parentShell
    */
   public FileCompositeDialog(Shell parentShell) {
      super(parentShell);
      setShellStyle(SWT.CLOSE | SWT.RESIZE | SWT.TITLE);
      
      diagFocusListener = new FocusListener() {
         public void focusGained(FocusEvent e) {
            switchEditableText(false);
         }
         public void focusLost(FocusEvent e) {
         }
      };
      
   }
   
   public static Image getImage(String imgFilelistRelpath) {

      URL urlImage = FileLocator.find(Activator.getDefault().getBundle(), new Path(imgFilelistRelpath), null);
      ImageDescriptor imageDesc = ImageDescriptor.createFromURL(urlImage);

      ImageRegistry registry = Activator.getDefault().getImageRegistry();
      Image image = registry.get(imgFilelistRelpath);
      if(image == null) {
         registry.put(imgFilelistRelpath, imageDesc);
      }
      return registry.get(imgFilelistRelpath);
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
      
      
      
      parent.addControlListener(new ControlAdapter() {
         @Override
         public void controlResized(ControlEvent e) {
            Rectangle rect = getShell().getBounds();
            if(initBound == null && rect.height != 0) {
               initBound = rect;
            }
            if(initBound != null && rect.height != initBound.height) {
               getShell().setBounds(rect.x, rect.y, rect.width, initBound.height);
            }
         }
      });

      dialog = parent;
      container = (Composite) super.createDialogArea(parent);

      composite = new Composite(container, SWT.BORDER);
      composite.setLayout(new GridLayout(2, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
      
      Label editPathLabel = new Label(composite, SWT.NONE);
      GridData gd_lblNewLabel = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
      gd_lblNewLabel.widthHint = 281;
      editPathLabel.setLayoutData(gd_lblNewLabel);
      if(browseType == BROWSE_DIR) {
         editPathLabel.setText("Directory Path");  // TODO [GB] Need to change this text
      }
      else if(browseType == BROWSE_FILE) {
         editPathLabel.setText("File Path");  // TODO [GB] Need to change this text
      }

      
      ToolBar toolBar = new ToolBar(composite, SWT.FLAT | SWT.RIGHT);
      GridData gd_toolBar = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
      gd_toolBar.widthHint = 91;
      toolBar.setLayoutData(gd_toolBar);
      
      editPathItem = new ToolItem(toolBar, SWT.PUSH); 
      editPathItem.setImage(IMG_EDIT);
      editPathItem.setToolTipText(EDIT_STR);
      editPathItem.setEnabled(true);
      
      editPathItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            switchEditableText(!editableText);
          }
      });

      relPathItem = new ToolItem(toolBar, SWT.PUSH);
      relPathItem.setImage(IMG_RELPATH);
      relPathItem.setToolTipText(RELPATH_STR);
      relPathItem.setEnabled(false);
      relPathItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            toRelPath();
         }
      });

      
      absPathItem = new ToolItem(toolBar, SWT.PUSH);
      absPathItem.setImage(IMG_ABSPATH);
      absPathItem.setToolTipText(ABSPATH_STR);
      absPathItem.setEnabled(true);
      absPathItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            toAbsPath();
         }
      });
      
      editPathText = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
      editPathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
      initPathText = getText();
      editPathText.setText(initPathText);
      
      
      switchEditableText(false);
      
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
            switchEditableText(false);
            if(relPathResolution) {
               relPathResolution = false;
               relPathItem.setEnabled(false);
               pathCombo.setEnabled(false);
            }
         }
      });
      defRadioButton.setText("Default");
      defRadioButton.setSelection(true);      

      new Label(grpPath, SWT.NONE);
      new Label(grpPath, SWT.NONE);
      new Label(grpPath, SWT.NONE);

      Button relRadioButton = new Button(grpPath, SWT.RADIO);
      relRadioButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            switchEditableText(false);
            if(!relPathResolution) {
               relPathResolution = true;
               relPathItem.setEnabled(true & !editableText);
               pathCombo.setEnabled(true);
            }
         }
      });
      relRadioButton.setText("Use relative path from");

      relPathResolution = false;
      pathResolution = null;

      pathCombo = new Combo(grpPath, SWT.READ_ONLY);
      pathCombo.addFocusListener(diagFocusListener);
      pathCombo.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent comboEvent) {
            switchEditableText(false);
            int ordinalIndex = ((Combo)(comboEvent.getSource())).getSelectionIndex();
            pathResolution = PathResolution.values()[ordinalIndex];
            setPathResolution(pathResolution);
         }
      });
      
      
      pathCombo.addFocusListener(new FocusListener() {
         @Override
         public void focusGained(FocusEvent e) {
         }

         @Override
         public void focusLost(FocusEvent e) {
         }
      });
            
      
      pathCombo.setItems(new String[] {"Workspace <${workspace_loc}/>", "Project <${workspace_loc}/${ProjName}/>", "Configuration <${workspace_loc}/${ProjName}/${ConfigName}/>", "Output <${workspace_loc}/${ProjName}/${ConfigName}/src/>"});
      pathCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
      pathCombo.setEnabled(false);
      
      Integer index = DEF_PATHRESOL;
      if(extraArgument != null && !extraArgument.equals("")) {
         try {
            index = new Integer(extraArgument);
         }
         catch (NumberFormatException Ex) {
         }
      }
      pathCombo.select(index); 
      pathResolution = PathResolution.values()[index];
      setPathResolution(pathResolution);

      setReturnCode(Dialog.CANCEL);

      return container;
   }
   
   
   private void switchEditableText(boolean editable) {
      
      if(editableText == editable) {
         return;
      }      
      if(editable) {
         editableText = true;
         editPathItem.setImage(IMG_CMPLEDIT);
         editPathItem.setToolTipText(CMPLEDIT_STR);
         editPathText(editableText);
         
         absPathItem.setEnabled(false);
         relPathItem.setEnabled(false);
         return;
      }
      editableText = false;
      editPathItem.setImage(IMG_EDIT);
      editPathItem.setToolTipText(EDIT_STR);
      editPathText(editableText); 
      
      absPathItem.setEnabled(true);
      relPathItem.setEnabled(relPathResolution);
   }
   

   private void editPathText(boolean editableText) {
      
      if(editPathText.getEditable() == editableText) {
         return;
      }
      editPathText.setEditable(editableText);
      
      if(editableText) {
         absSegment = "";
         String currentText = getText();
         int absEOF = currentText.lastIndexOf(">");
         if(absEOF != -1) {
            absSegment = currentText.substring(0, absEOF+1);
            editPathText.setText(currentText.substring(absEOF+1));
         }
         return;
      }
      String filePath = absSegment + editPathText.getText();
      absSegment = "";
      setText(filePath);
   }
   
   
   
   protected void cancelPressed() {
      setReturnCode(Dialog.CANCEL);
      switchEditableText(false);
      setText(initPathText);
      close();      
   }

   protected void okPressed() {
      setReturnCode(Dialog.OK);
      switchEditableText(false);
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
      
      Button wkspButton = createButton(parent, IDialogConstants.CLIENT_ID+1, "Workspace...", false);
      {
         wkspButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent ev) {
               /* Before opening the browse dialog we try to convert the current
                * path text to a valid workspace resource, so we can set it
                * as initial selection in the dialog.
                *
                * First we remove all double-quotes. Then the build macro provider
                * will resolve all macros/variables (like workspace_loc, ...).
                *
                * If the workspace location path is a prefix of our resolved path,
                * we will remove that part and finally get a full path relative to the
                * workspace. We can use that path to set the initially selected resource.
                */
               switchEditableText(false);
               String currentPathText = getText();

               /* Remove double quotes */
               currentPathText = currentPathText.replaceAll("\"", ""); //$NON-NLS-1$ //$NON-NLS-2$

               /* Resolve variables */
               IStringVariableManager variableManager = VariablesPlugin.getDefault().getStringVariableManager();

               /* See if we can discover the project from the context *
                * and check whether the path must be resolved... */
               IProject project = null;
               IResource resource = null;
               if(contextInfo != null) {
                  try {
                     // Try to find the project
                     ICdtVariable var = SupplierBasedCdtVariableManager.getVariable(PROJECTNAME_VAR, contextInfo, true);
                     if (var != null && var.getValueType() == ICdtVariable.VALUE_TEXT)
                        project = ResourcesPlugin.getWorkspace().getRoot().getProject(var.getStringValue());

                     // Try to resolve the currentPathText
                     IVariableSubstitutor varSubs = new SupplierBasedCdtVariableSubstitutor(contextInfo, "", "");  //$NON-NLS-1$//$NON-NLS-2$
                     String value = CdtVariableResolver.resolveToString(currentPathText, varSubs);
                     if (!"".equals(value)) { //$NON-NLS-1$
                        IResource rs[] = ResourcesPlugin.getWorkspace().getRoot().findContainersForLocationURI(URIUtil.toURI(value));
                        if (rs == null || rs.length == 0)
                           resource = ResourceLookup.selectFileForLocation(new Path(value), null);
                        else
                           resource = rs[0];
                     }
                  } catch (CdtVariableException e) {
                     // It's OK not to find the project... carry on as before
                  }
               }

               /* Create workspace folder/file selection dialog and
                * set initial selection */
               ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(),
                     new WorkbenchLabelProvider(), new WorkbenchContentProvider());

               dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
               dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));

               if (browseType == BROWSE_DIR) {
                  dialog.setInitialSelection(resource);
                  Class<?>[] filteredResources = {IContainer.class, IProject.class};
                  dialog.addFilter(new TypedCDTViewerFilter(filteredResources));
                  dialog.setTitle(WORKSPACE_DIR_DIALOG_TITLE);
                  dialog.setMessage(WORKSPACE_DIR_DIALOG_MSG);
               } else {
                  dialog.setInitialSelection(resource);
                  dialog.setValidator(new ISelectionStatusValidator() {
                     @Override
                     public IStatus validate(Object[] selection) {
                        if (selection != null)
                           for (Object sel : selection)
                              if (!(sel instanceof IFile))
                                 return new CDTStatusInfo(IStatus.ERROR, WORKSPACE_FILE_DIALOG_ERR);
                        return new CDTStatusInfo();
                     }
                  });
                  dialog.setTitle(WORKSPACE_FILE_DIALOG_TITLE);
                  dialog.setMessage(WORKSPACE_FILE_DIALOG_MSG);
               }

               /* Open dialog and process result.
                * If a resource has been selected we create a workspace relative path for it.
                * Use ${ProjName} if the full path is relative to the context's location */
               if (dialog.open() == Window.OK) {

                  Object[] rs = dialog.getResult();

                  if (rs != null) {
                     int i = 0;
                     values = new String[rs.length];
                      for (Object o : rs) {
                        resource = (IResource) o;
                        if (resource.getProject().equals(project))
                           values[i] = variableManager.generateVariableExpression(WORKSPACELOC_VAR, null) + "/" + variableManager.generateVariableExpression(PROJECTNAME_VAR, null) +  resource.getProjectRelativePath().makeAbsolute().toString() + dirEOF;
                        else
                           values[i] = variableManager.generateVariableExpression(WORKSPACELOC_VAR, null) + "/" + resource.getFullPath().toString() + dirEOF;
                        
                     }
                     // If only one entry, update the text field
                     if (values.length == 1) {
                        if(relPathResolution) {
                           values = getRelativePath(values);
                        }                        
                        setText(values[0]);
                     }
                     else
                        // More then one item selected and OK pressed. Exit this edit dialog
                        buttonPressed(IDialogConstants.OK_ID);
                  }
               }
            }
         });
      }
      
      Button fileSystem = createButton(parent, IDialogConstants.CLIENT_ID+2, "File system...", false);
      {
         fileSystem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent ev) {
               String currentName;
               String result;
               
               switchEditableText(false);
               
               switch (browseType) {
               case BROWSE_DIR :
                  DirectoryDialog dialog = new DirectoryDialog(getParentShell(),
                        SWT.OPEN|SWT.APPLICATION_MODAL);
                  currentName = getText();
                  if(currentName != null && currentName.trim().length() != 0) {
                     dialog.setFilterPath(currentName);
                  } else if(filterPath != null) {
                     dialog.setFilterPath(filterPath);
                  }
                  dialog.setMessage(FILESYSTEM_DIR_DIALOG_MSG);
                  result = dialog.open();
                  if(result != null) {
                     // Directory should finish with a slash
                     result = result.replaceAll("\\\\", "/");  // Is a better way exist to get rid of windows delimiter?
                     result += "/"; 
                     if(relPathResolution) {
                        result = getRelativePath(new String[] {result})[0];
                     }                        

                     setText(result);
                  }
                  break;
               case BROWSE_FILE:
                  FileDialog browseDialog = new FileDialog(getParentShell());
                  currentName = getText();
                  if (currentName != null && currentName.trim().length() != 0) {
                     browseDialog.setFilterPath(currentName);
                  } else if (filterPath != null) {
                     browseDialog.setFilterPath(filterPath);
                  }
                  if (filterExtensions != null) {
                     browseDialog.setFilterExtensions(filterExtensions);
                  }
                  result = browseDialog.open();
                  if (result != null) {
                     result = result.replaceAll("\\\\", "/"); // Is a better way exist to get rid of windows
                     setText(result);
                  }
                  break;
               }
            }
         });
      }
   }
   
   
   private void toAbsPath() {
      String itemPath = getText();

      if(itemPath != null && itemPath != "") {
         itemPath = getAbsolutePath(new String[] {itemPath} )[0];
         setText(itemPath);
      }
   }


   private void toRelPath() {
      String itemPath = getText();

      if(itemPath != null && itemPath != "") {
         itemPath = getRelativePath(new String[] {itemPath} )[0];
         setText(itemPath);
      }
   }
      
      
   private String[] getAbsolutePath(String[] inputPath) {
      
      java.util.List<String> listPath = new ArrayList<String>();
      IVariableSubstitutor varSubs = new SupplierBasedCdtVariableSubstitutor(contextInfo, "", "");
      
      for(String curStrPath : inputPath) {
         String resolvedPath = curStrPath;
         try {
            if(curStrPath.indexOf('<') == -1) {
               listPath.add(curStrPath);
               continue;
            }
            curStrPath = curStrPath.replaceAll("<", "");
            curStrPath = curStrPath.replaceAll(">", "");
            resolvedPath = CdtVariableResolver.resolveToString(curStrPath, varSubs);
            resolvedPath = resolvedPath.replaceAll("\"", "");            
            resolvedPath = (new Path(resolvedPath)).makeAbsolute().toString();
            resolvedPath = doubleQuotePath(resolvedPath);
         }
         catch (CdtVariableException exception) {
            listPath.add(resolvedPath);
            continue;
         }
         listPath.add(resolvedPath);
      }
      return listPath.toArray(new String[]{});
   }
   

   private String[] getRelativePath(String[] inputPath) {
      
      java.util.List<String> listPath = new ArrayList<String>();
      IVariableSubstitutor varSubs = new SupplierBasedCdtVariableSubstitutor(contextInfo, "", "");
      
      for(String curStrPath : inputPath) {
         String resolvedPath = null;
         try {
            if(curStrPath.indexOf('>') != -1) {
               curStrPath = getAbsolutePath(new String[] {curStrPath})[0];
            } 
            resolvedPath = CdtVariableResolver.resolveToString(curStrPath, varSubs);
            resolvedPath = (new Path(resolvedPath)).makeAbsolute().toString();
            resolvedPath = resolvedPath.replaceAll("\"", "");
            resolvedPath = ResourceUtils.getRelativePath(resolvedPath, absolutePathRes.makeAbsolute().toString(), "/");
            resolvedPath = doubleQuotePath(resolvedPath);
         }
         catch (CdtVariableException exception) {
            resolvedPath = curStrPath; 
         }
         catch(IllegalArgumentException exception) {
            resolvedPath = doubleQuotePath(resolvedPath); 
         }
         
         resolvedPath = "<" + pathResVariable + "/>"+ resolvedPath;
         listPath.add(resolvedPath);
      }
      
      return listPath.toArray(new String[]{});
   }
   
   
   /**
    * Double-quotes a path name if it contains white spaces, backslahes
    * or a macro/variable (We don't know if a macro will contain spaces, so we
    * have to be on the safe side).
    * @param pathName The path name to double-quote.
    * @return
    */
   private String doubleQuotePath(String pathName) {
      /* Trim */
//      pathName = pathName.trim();
//
//      /* Check if path is already double-quoted */
//      boolean bStartsWithQuote = pathName.startsWith("\""); //$NON-NLS-1$
//      boolean bEndsWithQuote = pathName.endsWith("\""); //$NON-NLS-1$
//
//      /* Check for spaces, backslashes or macros */
//      int i = pathName.indexOf(" ") + pathName.indexOf("\\") //$NON-NLS-1$ //$NON-NLS-2$
//            + pathName.indexOf("${"); //$NON-NLS-1$
//
//      /* If indexof didn't fail all three times, double-quote path */
//      if (i != -3) {
//         if (!bStartsWithQuote)
//            pathName = "\"" + pathName; //$NON-NLS-1$
//         if (!bEndsWithQuote)
//            pathName = pathName + "\""; //$NON-NLS-1$
//      }

      return pathName;
   }
   
   
   private String setText(String text) {
      
      String oldText = stringFieldEditor.getStringValue();
      stringFieldEditor.setStringValue(text);
      editPathText.setText(text);
      return oldText;
   }
   
   private String getText() {
      return stringFieldEditor.getStringValue();
   }
   
   
   public void setStringFieldEditor(StringFieldEditor fieldEditor) {
      stringFieldEditor = fieldEditor;
      
      ToolSettingsPrefStore toolStore = (ToolSettingsPrefStore) fieldEditor.getPreferenceStore();
      Object[] stringOption = toolStore.getOption(fieldEditor.getPreferenceName());
      if(stringOption != null){
         setContext(toolStore.obtainMacroProvider().getMacroContextInfo(IBuildMacroProvider.CONTEXT_OPTION, new OptionContextData((IOption)stringOption[1], (IHoldsOptions)stringOption[0])));
      }
   }
   

   /**
    * Return the initial size of the dialog.
    */
   @Override
   protected Point getInitialSize() {
      return new Point(600, 271);
   }

   public PathResolution getPathSelection() {

      if(pathCombo == null) {
         return PathResolution.none;
      }
      return PathResolution.values()[pathCombo.getSelectionIndex()];
   }

   public Boolean getPathRelativite() {
      return relPathResolution;
   }

   public void setType(Integer browseType) {
      dirEOF = "";
      if (browseType == BROWSE_DIR) {
         dirEOF = "/";
      }
      this.browseType = browseType;
   }
   
   public void setPathRelative(Boolean relativePath) {
      this.relativePath = relativePath;
   }
   

   public void setPathResolution(PathResolution pathResolution) {
      
      this.pathResolution = pathResolution;

      if(contextInfo != null) {
         IVariableSubstitutor varSubs = new SupplierBasedCdtVariableSubstitutor(contextInfo, "", "");
         try {
            pathResVariable = new Path(pathResolution.getPathResVariable());
            absolutePathRes = new Path(CdtVariableResolver.resolveToString(pathResVariable.toString(), varSubs) + "/");
         }
         catch (CdtVariableException e) {
         }
      }
   }
   
   
   /**
    * Set the field editor context.
    */
   public void setContext(IVariableContextInfo info) {
      contextInfo = info;
      for(;info != null;info = info.getNext()){       
      }
      if(pathResolution != null) {
         setPathResolution(pathResolution);
      }
   }

   public void setExtraArgument(String extraArgument) {
      this.extraArgument = extraArgument;
   }
   
}
