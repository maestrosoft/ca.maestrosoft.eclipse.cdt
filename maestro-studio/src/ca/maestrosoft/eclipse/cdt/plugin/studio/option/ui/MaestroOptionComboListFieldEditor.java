package ca.maestrosoft.eclipse.cdt.plugin.studio.option.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.cdtvariables.CdtVariableException;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IHoldsOptions;
import org.eclipse.cdt.managedbuilder.core.IManagedOptionValueHandler;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IResourceInfo;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.internal.macros.OptionContextData;
import org.eclipse.cdt.managedbuilder.macros.IBuildMacroProvider;
import org.eclipse.cdt.managedbuilder.ui.properties.BuildOptionComboFieldEditor;
import org.eclipse.cdt.managedbuilder.ui.properties.ICustomBuildOptionEditor;
import org.eclipse.cdt.managedbuilder.ui.properties.ToolSettingsPrefStore;
import org.eclipse.cdt.utils.cdtvariables.CdtVariableResolver;
import org.eclipse.cdt.utils.cdtvariables.IVariableContextInfo;
import org.eclipse.cdt.utils.cdtvariables.IVariableSubstitutor;
import org.eclipse.cdt.utils.cdtvariables.SupplierBasedCdtVariableSubstitutor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.osgi.util.TextProcessor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

import ca.maestrosoft.eclipse.cdt.plugin.studio.option.valuehandler.EnumeratedOption;

/*******************************************************************************
 * Copyright (c) 2002, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Rational Software - Initial API and implementation
 * ARM Ltd. - basic tooltip support
 * Maestro Software Tech. Derived implementation
 *******************************************************************************/


public class MaestroOptionComboListFieldEditor extends BuildOptionComboFieldEditor implements ICustomBuildOptionEditor, IPropertyChangeListener {

   protected IOption optionCache;
   protected String listConcatenateUpdate = "";
   protected Integer browseType;
   protected Combo srollCombo;
   protected MaestroFileListControlFieldEditor cdtFieldEditor;
   protected ICustomBuildOptionEditor fieldListener;
   protected String extraArgument;
   protected String curComboSelectName;
   protected String editEnumName;  
   protected String editEnumNameEdition; 
   protected int editEnumNameIndex;
   protected IOption optionEnumCombo;   
   protected Composite compParent;
   protected IResourceInfo optConfig;
   protected ITool cmdTool;
   
   private String [] enumComboNames = new String[0];
	private String [] enumOptionName  = new String[0];
   private boolean initialized = false;
   private IOption optionInclude;   
   private String listConcatenateCurrent = "";

   private int curComboSelectIndex;
   private IPropertyChangeListener optionBuilderListener;
	private IVariableContextInfo contextInfo;
	private String listSystemPath = "";
	private ListPreferenceStore listPreferenceStore;
	

   /**
    * Will receive parameters through init
    */
   public MaestroOptionComboListFieldEditor() {
      super("", "", new String[] {}, "", null);
   }

   /**
    * @param name
    * @param label
    * @param opts
    * @param sel
    * @param parent
    */
   public MaestroOptionComboListFieldEditor(String name, String label, String [] opts, String sel, Composite parent) {
      super(name, label, opts, sel, parent);
   }

   /**
    * @param name
    * @param label
    * @param tooltip
    * @param contextId
    * @param opts
    * @param sel
    * @param parent
    */

   public MaestroOptionComboListFieldEditor(String name, String label, String tooltip, String contextId, String [] opts, String sel, Composite parent) {
      this(name, label, opts, sel, parent);
      setToolTip(tooltip);
      if (!contextId.equals("")) {
      	PlatformUI.getWorkbench().getHelpSystem().setHelp(srollCombo, contextId);  //$NON-NLS-1$
      }
   }
   
   /**
    * @param name
    * @param label
    * @param tooltip
    * @param contextId
    * @param opts
    * @param sel
    * @param parent
    */   
   
   protected void CreateGUI(String name, String label, String tooltip, String contextId, String [] opts, String sel, Composite parent) {
      init(name, label);
      setOptions(opts);
      curComboSelectName = sel;
      compParent = parent;
      //
      // The list preference store is used to setup values that must be used by the FileListControlFieldEditor
      listPreferenceStore = new ListPreferenceStore();
      createControl(parent);
   }
   
   protected void createControl(Composite parent) {
      if(initialized ) {
         super.createControl(parent);
      }
   }
   
   public void setPropertyChangeListener(IPropertyChangeListener listener) {
      super.setPropertyChangeListener(listener);
      optionBuilderListener = listener;      
  }
   
   

   /**
    * Sets the field editor's tool tip text to the argument, which
    * may be null indicating that no tool tip text should be shown.
    *
    * @param string the new tool tip text (or null)
    *
    * @exception SWTException <ul>
    *    <li>ERROR_WIDGET_DISPOSED - if the field editor has been disposed</li>
    *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the field editor</li>
    * </ul>
    */
   public void setToolTip(String tooltip) {
      srollCombo.setToolTipText(tooltip);
      getLabelControl().setToolTipText(tooltip);
   }

   /**
    * Returns the field editor's tool tip text, or null if it has
    * not been set.
    *
    * @return the field editor's tool tip text
    *
    * @exception SWTException <ul>
    *    <li>ERROR_WIDGET_DISPOSED - if the field editor has been disposed</li>
    *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the field editor</li>
    * </ul>
    */
   public String getToolTipText() {
      return srollCombo.getToolTipText();
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.preference.FieldEditor#adjustForNumColumns(int)
    */
   @Override
   protected void adjustForNumColumns(int numColumns) {
      // For now grab the excess space
      GridData gd = (GridData)srollCombo.getLayoutData();
      gd.horizontalSpan = numColumns - 1;
      gd.grabExcessHorizontalSpace = true;
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.preference.FieldEditor#doFillIntoGrid(org.eclipse.swt.widgets.Composite, int)
    */
   @Override
   protected void doFillIntoGrid(Composite parent, int numColumns) {
      if(!initialized) {
         return;
      }      
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      gd.horizontalSpan = numColumns;
      parent.setLayoutData(gd);

      // Add the label
      Label label = getLabelControl(parent);
      GridData labelData = new GridData();
      labelData.horizontalSpan = 1;
      labelData.grabExcessHorizontalSpace = false;
      label.setLayoutData(labelData);

      // Create the combo selector
      srollCombo = new ScrollCombo(parent, SWT.H_SCROLL | SWT.DROP_DOWN | SWT.BORDER);
      srollCombo.setFont(parent.getFont());
      
      // Setup the layout attribute 
      GridData comgoGDData = new GridData(GridData.FILL_HORIZONTAL);
      comgoGDData.horizontalSpan = numColumns - 1;
      comgoGDData.grabExcessHorizontalSpace = true;
      srollCombo.setLayoutData(comgoGDData);
      
      // Create the combo item
      for (String comboOption : getOptions()) {
         srollCombo.add(comboOption);
      }
      srollCombo.select(curComboSelectIndex);  
      //
      // This event come from higher level and tell the widget that it is being disposed for any reason!
      // This event can be useful to do any post-processing cleanup 
      srollCombo.addDisposeListener(new DisposeListener() {
         public void widgetDisposed(DisposeEvent event) {
         	srollCombo = null;
         }
     });
      
      
      // listen to changed value.
      srollCombo.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent evt) {
            PropertyChangeEvent listUpdateEvent = null;
            String oldValue = curComboSelectName;
            String itemList = null;
            int oldIndex = srollCombo.indexOf(curComboSelectName);
            int index = srollCombo.getSelectionIndex();            
            if(index == -1) {
            	return;
            }
            curComboSelectIndex = index;
            curComboSelectName = srollCombo.getItem(index);
            if(curComboSelectName.equals(editEnumNameEdition)) {

            	int retCode = OpenListDialog();

            	if(retCode != Dialog.CANCEL) {
            		listUpdateEvent = new PropertyChangeEvent(fieldListener, VALUE, Concatenate(listSystemPath, listConcatenateCurrent), Concatenate(listSystemPath, listConcatenateUpdate));
            		itemList = stripRelative(new String[] {listConcatenateUpdate})[0];
            		srollCombo.setItem(editEnumNameIndex, itemList);
            		srollCombo.select(editEnumNameIndex);
            		curComboSelectIndex = editEnumNameIndex;
            		listConcatenateCurrent = listConcatenateUpdate;
            		enumComboNames[curComboSelectIndex] = itemList;
            		curComboSelectName = itemList;
            	}
            	else {
            		curComboSelectIndex = oldIndex;
            		srollCombo.select(oldIndex);
            		curComboSelectName = oldValue;
            	}
            }
            setPresentsDefaultValue(false);

            if(listUpdateEvent != null) {
               // Update the optionCache with new concatenated list
            	if(fieldListener instanceof MaestroFileListControlFieldEditor) {
            		((MaestroFileListControlFieldEditor)(fieldListener)).forceStringListValue(Concatenate(listSystemPath, listConcatenateCurrent).split(";"));
            	}
            	optionBuilderListener.propertyChange(listUpdateEvent);
            	if(optionInclude != null) {
            		itemList = Concatenate(listSystemPath, listConcatenateCurrent);
            		itemList = itemList.replaceAll("\\[|\\]", "");
            		
            		cdtFieldEditor.forceStringListValue(getAbsolutePath(itemList.split(";")));
            		listUpdateEvent = new PropertyChangeEvent(cdtFieldEditor, VALUE, null, null);
            	   // Update the optionInclude with new concatenated list
            		optionBuilderListener.propertyChange(listUpdateEvent);
            		cdtFieldEditor.dispose();
            	}
            }
            // Update the optionEnumCombo enumerated option with new selection
            fireValueChanged(VALUE, enumOptionName[oldIndex], enumOptionName[curComboSelectIndex]);            
         }

      });

      srollCombo.addKeyListener(new KeyAdapter() {
         @Override
         public void keyPressed(KeyEvent event) {
            event.doit = false;
            if(event.keyCode == SWT.ARROW_LEFT || event.keyCode == SWT.ARROW_RIGHT || event.keyCode == SWT.HOME || event.keyCode == SWT.END) {
               event.doit = true;
            }
         }
      });
   }

	private String Concatenate(String string1, String string2) {
		if(string2.equals("")) {
			return string1;
		}
		if(string1.equals("")) {
			return string2;
		}				
      return string1 + ";" + string2;
   }


   protected String[] stripRelative(String[] selectNames) {
      
      int indexName = 0;
      String newSelectNames[] = new String[selectNames.length];
      
      for(String itemName : selectNames) {
         if(itemName.equals(editEnumNameEdition)) {
            newSelectNames[indexName++] = editEnumNameEdition;
            continue;
         }
         String allElements[] = itemName.split(";");
         String stripNames = "";
         
         int elementIndex = 0;
         for(String element : allElements) {
            int index = element.indexOf('>');
            if(index != -1) {
               element = element.substring(index+1);
            }
            stripNames += (elementIndex++ == 0 ? "" : ";") + element;
         }
         newSelectNames[indexName++] = stripNames;
      }
      return newSelectNames;
   }

   protected int OpenListDialog() {

      if(optionCache != null) { 
         ListCompositeDialog listDialog  = new ListCompositeDialog(srollCombo.getShell());
         listDialog.setType(browseType);
         listDialog.create();
         Composite listComposite = listDialog.getFieldComposite();

         MaestroFileListControlFieldEditor listEditor = new MaestroFileListControlFieldEditor(optionCache.getBaseId(), optionCache.getName(), null, optionCache.getContextId(), listComposite, browseType);
         listComposite.layout(true);
         fieldListener = listEditor;

         listPreferenceStore.setValue(optionCache.getBaseId(), listConcatenateCurrent);
         
         listEditor.setPreferenceStore(listPreferenceStore);
         listEditor.setPropertyChangeListener(this);
         listEditor.setPathSelection(listDialog.getPathSelection());
         listEditor.setPathRelative(listDialog.getPathRelativite());
         listEditor.load(); 
         
         listDialog.setPropertyChangeListener(listEditor);
         
         //This is mainly for some standard option the cdt internally uses: The indexer for example look for the include folder
         //These values will be passed independently from the command option through this option.
         if(optionInclude != null) {
            cdtFieldEditor = new MaestroFileListControlFieldEditor(optionInclude.getBaseId(), optionInclude.getName(), null, optionInclude.getContextId(), listComposite, browseType);
            cdtFieldEditor.setPreferenceStore(ToolSettingsPrefStore.getDefault());
            cdtFieldEditor.setPropertyChangeListener(null);
            cdtFieldEditor.load(); // This will load the current stored value from cdt registry store
         }
         return listDialog.open();
      }
      else {
         return -1;  
      }
   }

   

   /* (non-Javadoc)
    * @see org.eclipse.jface.preference.FieldEditor#doLoad()
    */
   @Override
   protected void doLoad() {
      // set all the options to option selector
      srollCombo.removeAll();
      srollCombo.setItems(stripRelative(getOptions()));

      // get the selected option from preference store
      curComboSelectName = getPreferenceStore().getString(getPreferenceName());
      IManagedOptionValueHandler optHandler = optionEnumCombo.getValueHandler();
      if(optHandler instanceof EnumeratedOption) {
      	curComboSelectName = ((EnumeratedOption)(optHandler)).getEnumName(optionEnumCombo.getOptionHolder(), curComboSelectName);
      }
      if(curComboSelectName.equals(editEnumName) || curComboSelectName.equals(editEnumNameEdition)) {
         srollCombo.select(editEnumNameIndex);
         curComboSelectName = stripRelative(new String[] {enumComboNames[editEnumNameIndex]})[0];
         return;
      }
      // Set the index of selection in the combo box
      int index = srollCombo.indexOf(curComboSelectName);
      srollCombo.select(index >= 0 ? index : 0);
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
    */
   @Override
   protected void doLoadDefault() {
      doLoad();
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.preference.FieldEditor#doStore()
    */
   @Override
   protected void doStore() {
      // Save the selected item in the store
      int index = srollCombo.getSelectionIndex();
      curComboSelectName = editEnumNameEdition;
      if(index != editEnumNameIndex) {
         curComboSelectName = index == -1 ? new String() : srollCombo.getItem(index);
      }
      // TODO : [GB] If this is called then it will create an issue: 
      getPreferenceStore().setValue(getPreferenceName(), curComboSelectName);      
   }

   public String getSelection(){
      //
      // The BuildOptionSettingUI.propertyChange method call getSelection to resolve the enumId with the selected name.
      // The default behavior of each name is hardcoded which is not a problem. However in our case we change the
      // name of the edit enumid according to the wish of the user. Thus we fool the caller for this case and always
      // return "" which is the default name defined in our plugin.xml. 
      
   	return enumOptionName[curComboSelectIndex];
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.preference.FieldEditor#getNumberOfControls()
    */
   @Override
   public int getNumberOfControls() {
      // There is just the label from the parent and the combo
      return 2;
   }

   /**
    * Returns this field editor's text control.
    *
    * @return the text control, or <code>null</code> if no
    * text field is created yet
    */
   protected Combo getComboControl() {
      return srollCombo;
   }

   /**
    * Returns this field editor's text control.
    *
    * @return the text control, or <code>null</code> if no
    * text field is created yet
    */
   public Combo getComboControl(Composite parent) {
      checkParent(srollCombo, parent);
      return srollCombo;
   }

   /**
    * Set whether or not the controls in the field editor
    * are enabled.
    * @param enabled The enabled state.
    * @param parent The parent of the controls in the group.
    *  Used to create the controls if required.
    */
   @Override
   public void setEnabled(boolean enabled, Composite parent) {
      getLabelControl(parent).setEnabled(enabled);
      srollCombo.setEnabled(enabled);
   }
   /**
    * Set the list of enum values for this combo field editor
    */
   public void setOptions(String[] enumNames){
      //bug 235327
   	if(optionEnumCombo == null) {
   		return; // We need to wait to be initialized before setting our options
   	}
   	enumComboNames = new String[enumNames.length];
   	EnumeratedOption optEnumHandler = null;
   	if(optionEnumCombo.getValueHandler() instanceof EnumeratedOption) {
   		optEnumHandler = (EnumeratedOption)optionEnumCombo.getValueHandler();
   	}
   	
   	for (int index = 0; index < enumNames.length; index++) {
   		enumComboNames[index] = enumNames[index];
   		enumOptionName[index] = enumNames[index];
   		if(optEnumHandler != null) {
   			// enumComboNames and enumOptionName are always the same value for most cases. This custom enumerated field editor allow for
   			// an enumerated value to be defined as a reference name id for another string option that contains the true name of the enum index.
   			// This mechanism is enabled when the enumerated option has a getValueHandler(). How getValueHandler chose a return name from the
   			enumComboNames[index] = optEnumHandler.getEnumName(cmdTool, enumNames[index]);
   		}
   		enumComboNames[index] = TextProcessor.process(enumComboNames[index]);
   	}
   	enumComboNames[editEnumNameIndex] = listConcatenateCurrent;
   	// The BuildOptionSettingUI is doing some checking and become confused by the fact that the returned enum values of an option might not 
   	// return the same set of enum name when a getValueHandler is defined when another option change its values on which has a dependency
   	// with this option. In such case BuildOptionSettingUI does use the first enum as the current selection. We do set at this level the 
   	// appropriate option according to the current select index.
   	if(optEnumHandler != null) {
   		try {
            ManagedBuildManager.setOption(optConfig, cmdTool, optionEnumCombo, optionEnumCombo.getEnumeratedId(enumOptionName[curComboSelectIndex]));
         } 
   		catch (BuildException e) {
            e.printStackTrace();
         }
   	}
   }
   /**
    * Set the list of enum values for this combo field editor
    */
   public String[] getOptions(){
      return enumComboNames;
   }

   
   private String untokenizer(String[] element) {
      StringBuffer concatenate = new StringBuffer("");
      for (int i = 0; i < element.length; i++) {
         concatenate.append(element[i]);
         if (i < (element.length - 1)) {
            concatenate.append(";");
         }
      }
      return concatenate.toString();
   }
   
   
// TODO : [GB] Check what will happen when we do a cancel and option use EnumOptionHandler
   @SuppressWarnings({ "unchecked", "restriction" })
   @Override
   public boolean init(IOption option, String extraArgument, String preferenceName, Composite parent) {

      try {
         if(option.getValueType() == IOption.ENUMERATED) {
         	
            optionEnumCombo = option;
            
            String selId = optionEnumCombo.getSelectedEnum();
            String curComboSelectName = optionEnumCombo.getEnumName(selId);
            List<String> enumListNameId = new ArrayList<String>();
            String[] enumNames = optionEnumCombo.getApplicableValues();
            
            editEnumNameIndex = 0;
            editEnumName = enumNames[editEnumNameIndex];          // Should be "Default"
            editEnumNameEdition = enumNames[enumNames.length-1];  // SHould be "<Edit...>"
         	IManagedOptionValueHandler optHandler = optionEnumCombo.getValueHandler();
            //
         	// An option cache is needed because the selection and edition of the paths or file is done with
         	// an enumerated option which the first option being the edited values provided by the user. The
         	// first enumerated value is provided by the cache option
            IPreferenceStore store = ToolSettingsPrefStore.getDefault();
            String optionListId = optionEnumCombo.getBaseId() + ".cache";
            Object objHolder[]  = ((ToolSettingsPrefStore)store).getOption(optionListId);
            cmdTool = (ITool)objHolder[0];
            
         	contextInfo = ((ToolSettingsPrefStore)store).obtainMacroProvider().getMacroContextInfo(IBuildMacroProvider.CONTEXT_OPTION, new OptionContextData((IOption)objHolder[1], (IHoldsOptions)objHolder[0]));
            
            optConfig = cmdTool.getParentResourceInfo();
            optionCache = cmdTool.getOptionBySuperClassId(optionListId);
            if(optionCache == null) {
               return false;
            }
            //
            // Includes path are recognized by CDT when the property of the Option is set to include path. We cannot
            // define our cache option as include path since they have property special format that include the
            // the relative definition of the path.
            optionInclude = cmdTool.getOptionBySuperClassId(optionEnumCombo.getBaseId() + ".cdt");
            
            int optionListCdtSize = 0;
            String[] optionlistCdtValues = new String[0];
            
            if(optionInclude != null) {
               Object optionListCdtArg = optionInclude.getValue();
               ((ArrayList<String>)(optionListCdtArg)).trimToSize();
               optionlistCdtValues = ((ArrayList<String>)(optionListCdtArg)).toArray(new String[0]);  
               optionListCdtSize = optionlistCdtValues.length; 
            }
            browseType = optionCache.getBrowseType();
            if(browseType != IOption.BROWSE_FILE && browseType != IOption.BROWSE_DIR && browseType != IOption.BROWSE_NONE) {
               return false;
            }
            Object optionListArg = optionCache.getValue();
            int optionListSize = 0;
            
            if(optionListArg instanceof ArrayList<?>) {
               ((ArrayList<String>)(optionListArg)).trimToSize();
               String[] listValues = ((ArrayList<String>)(optionListArg)).toArray(new String[0]);  
               optionListSize = listValues.length; 
               listConcatenateCurrent = untokenizer(listValues);
               listSystemPath = getSystemPath(listConcatenateCurrent);
               listConcatenateCurrent = removeSystemPath(listConcatenateCurrent);
               //
               // This case happens when the option is associated to a CDT include paths option defined by the user and a template was used that 
               // add an include directory to the cdt include option. Our cached option must be updated with the template added directory
               // We also keep track of those system directories by enclosing the paths with [] brackets.
               if(optionListSize == 0 && optionListCdtSize != 0) {
               	listSystemPath = "[" + untokenizer(optionlistCdtValues) + "]";
               }
            }
            else if(optionListArg instanceof String) {
               listConcatenateCurrent =  (String) optionListArg;
            }
            listConcatenateUpdate = listConcatenateCurrent;            
            int comboIndex = 0;
                     	
            for (int i = 0; i < enumNames.length; ++i) {
               if (optHandler.isEnumValueAppropriate(optConfig, cmdTool, optionEnumCombo, optionEnumCombo.getValueHandlerExtraArgument(), enumNames[i])) {
               	enumListNameId.add(enumNames[i]);
            		boolean isCurrentEnum = enumNames[i].equals(curComboSelectName);
               	if(optHandler instanceof EnumeratedOption) {
               		enumNames[i] = ((EnumeratedOption)(optHandler)).getEnumName(cmdTool, enumNames[i]);
               	}
            		if(isCurrentEnum) {
            			curComboSelectName = enumNames[i];
            			curComboSelectIndex = comboIndex;
            		}
            		comboIndex++;
               }
            }
            initialized = true;
            enumOptionName = enumListNameId.toArray(new String[enumListNameId.size()]);
            CreateGUI(preferenceName, optionEnumCombo.getName(), null, optionEnumCombo.getContextId(), enumOptionName, curComboSelectName, parent);
            
            return true;
         }
      } 
      catch (BuildException e) {
         e.printStackTrace();
      }
      return false;
   }

	@Override
   public Control[] getToolTipSources() {
      return null;
   }

   @Override
   public void propertyChange(PropertyChangeEvent event) {
      Object source = event.getSource();

      if(source instanceof MaestroFileListControlFieldEditor){
         MaestroFileListControlFieldEditor listEditor = (MaestroFileListControlFieldEditor)source;

         String listConcatenate = "";
         String listValues[] = listEditor.getStringListValue();

         for(int i = 0; i < listValues.length; i++) {
            if(i != 0) {
               listConcatenate += ";";
            }
            listConcatenate += listValues[i];
         }
         listConcatenateUpdate  = listConcatenate;
      }
   }
   //
   // System path are include paths created by template include path element defined in the template.xml.
   // We must hide those paths from the user less they delete or change them in which case CDT will generate
   // warning when those paths will be updated and stored
   private String getSystemPath(String listPath) {
   	
      int index = listPath.lastIndexOf(']');
      if(index != -1) {
         return listPath.substring(0, index+1);
      }
	   return "";
   }
   
   
   private String removeSystemPath(String listPath) {
   	
      int index = listPath.lastIndexOf(']');
      if(index != -1) {
      	if(listPath.contains(";")){
      		index++;
      	}
         return listPath.substring(index+1);
      }
	   return listPath;
   }
   

   protected String[] getAbsolutePath(String[] inputPath) {
   	
   	java.util.List<String> listPath = new ArrayList<String>();
   	IVariableSubstitutor varSubs = new SupplierBasedCdtVariableSubstitutor(contextInfo, "", "");

   	for(String curStrPath : inputPath) {
   		String resolvedPath = null;
   		try {
            curStrPath = curStrPath.replaceAll("<", "");
            curStrPath = curStrPath.replaceAll(">", "");   			
   			resolvedPath = CdtVariableResolver.resolveToString(curStrPath, varSubs);
   			resolvedPath = resolvedPath.replaceAll("\"", ""); 
   			resolvedPath = (new Path(resolvedPath)).makeAbsolute().toString();
   		}
   		catch (CdtVariableException exception) {
   			resolvedPath = curStrPath; 
   		}
   		catch(IllegalArgumentException exception) {
   			resolvedPath = new Path(resolvedPath).toOSString();
   		}
   		listPath.add(new Path(resolvedPath).toOSString());
   	}
   	return listPath.toArray(new String[]{});   	
   }
   
   protected String doubleQuotePath(String pathName)	{
      /* Trim */
      pathName = pathName.trim();

      /* Check if path is already double-quoted */
      boolean bStartsWithQuote = pathName.startsWith("\""); //$NON-NLS-1$
      boolean bEndsWithQuote = pathName.endsWith("\""); //$NON-NLS-1$

      /* Check for spaces, backslashes or macros */
      int i = pathName.indexOf(" ") + pathName.indexOf("\\") //$NON-NLS-1$ //$NON-NLS-2$
            + pathName.indexOf("${"); //$NON-NLS-1$

      /* If indexof didn't fail all three times, double-quote path */
      if (i != -3) {
         if (!bStartsWithQuote)
            pathName = "\"" + pathName; //$NON-NLS-1$
         if (!bEndsWithQuote)
            pathName = pathName + "\""; //$NON-NLS-1$
      }
      return pathName;
   }

}
