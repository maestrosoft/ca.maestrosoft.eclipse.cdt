/*******************************************************************************
 * Copyright (c) 2004, 2010 BitMethods Inc and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * BitMethods Inc - Initial API and implementation
 * ARM Ltd. - basic tooltip support
 * Miwako Tokugawa (Intel Corporation) - Fixed-location tooltip support
 *******************************************************************************/
package ca.maestrosoft.eclipse.cdt.plugin.studio.option.ui;


import java.util.ArrayList;
import java.util.StringTokenizer;

import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IHoldsOptions;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.internal.macros.OptionContextData;
import org.eclipse.cdt.managedbuilder.macros.IBuildMacroProvider;
import org.eclipse.cdt.managedbuilder.ui.properties.FileListControlFieldEditor;
import org.eclipse.cdt.managedbuilder.ui.properties.ICustomBuildOptionEditor;
import org.eclipse.cdt.managedbuilder.ui.properties.ToolSettingsPrefStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.PlatformUI;


/**
 * Field editor that uses FileListControl for user input.
 *
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
@SuppressWarnings("restriction")
public final class MaestroFileListControlFieldEditor extends FileListControlFieldEditor implements ICustomBuildOptionEditor, IPropertyChangeListener {

	// file list control
	private MaestroFileListControl list;
	private int browseType;
	private Composite topLayout;
	private String curUpdatedLists[] = new String[] {};
   private String initLists[] = null;
   private boolean firstCall = true;
   private boolean disposed = false;
   private String[] forcedUpdateList = null;
	private static final String DEFAULT_SEPARATOR = ";"; //$NON-NLS-1$
 
 
	/**
	 * Creates a file list control field editor.
	 * @param name the name of the preference this field editor works on
	 * @param labelText the label text of the field editor
	 * @param parent the parent of the field editor's control
	 * @param type the browseType of the file list control
	 */
	public MaestroFileListControlFieldEditor(String name,	String labelText,	Composite parent,	int type) {
	   super(name, labelText, parent, type);
		browseType = type;
		// Set the browse strategy for the list editor
		list.setType(type);
	}

	/**
	 * Creates a file list control field editor.
	 * @param name the name of the preference this field editor works on
	 * @param labelText the label text of the field editor
	 * @param tooltip the tooltip text of the field editor
	 * @param contextId
	 * @param parent the parent of the field editor's control
	 * @param type the browseType of the file list control
	 */
	public MaestroFileListControlFieldEditor( String name, String labelText, String tooltip, String contextId, Composite parent, int type) {
	   this(name, labelText, parent, type);
		// can't use setToolTip(tooltip) as label not created yet
		getLabelControl(parent).setToolTipText(tooltip);
		if (!contextId.equals("")) PlatformUI.getWorkbench().getHelpSystem().setHelp(list.getListControl(), contextId);	 //$NON-NLS-1$
	}
	
 	
   protected void createControl(Composite parent) {
         super.createControl(parent);
   }	

	/**
	 * Sets the field editor's tool tip text to the argument, which
	 * may be null indicating that no tool tip text should be shown.
	 *
	 * @param tooltip the new tool tip text (or null)
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the field editor has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the field editor</li>
	 * </ul>
	 */
	public void setToolTip(String tooltip) {
		// Currently just the label has the tooltip
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
		return getLabelControl().getToolTipText();
	}

	/**
	 * Creates a file list control field editor.
	 * @param name the name of the preference this field editor works on
	 * @param labelText the label text of the field editor
	 * @param parent the parent of the field editor's control
	 * @param value the field editor's value
	 * @param type the browseType of the file list control
	 */
	public MaestroFileListControlFieldEditor(String name,	String labelText,	Composite parent,	String value, int type) {
	   super(name, labelText, parent, value, type);
		browseType = type;
//		this.values = parseString(value);
	}

	/**
	 * Sets the filter-path for the underlying Browse dialog. Only applies when browseType is 'file' or 'dir'.
	 * @param filterPath
	 *
	 * @since 7.0
	 */
	public void setFilterPath(String filterPath) {
		list.setFilterPath(filterPath);
	}

	/**
	 * Sets the filter-extensions for the underlying Browse dialog. Only applies when browseType is 'file'.
	 * @param filterExtensions
	 *
	 * @since 7.0
	 */
	public void setFilterExtensions(String[] filterExtensions) {
		list.setFilterExtensions(filterExtensions);
	}

	/**
	 * Fills this field editor's basic controls into the given parent.
	 */
	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
	   
	   if(!firstCall) {
	      super.doFillIntoGrid(new Composite(PlatformUI.getWorkbench().getModalDialogShellProvider().getShell(), SWT.NONE), numColumns);
         firstCall  = true;
	   }
	   
		topLayout = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = numColumns;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.makeColumnsEqualWidth = false;
		topLayout.setLayout(layout);
		GridData gddata = new GridData(GridData.FILL_BOTH);
		gddata.horizontalSpan = 2;
		topLayout.setLayoutData(gddata);
		// file list control
		list = new MaestroFileListControl(topLayout, getLabelText(), getType(), false);  // TODO : [GB] Get type is not initialized yet at this point and has default value.
		list.addChangeListener(new IFileListChangeListener(){

         @Override
			public void fileListChanged(MaestroFileListControl fileList, String oldValue[], String newValue[]) {
            int sizeDelta = curUpdatedLists.length - newValue.length;
			   curUpdatedLists = newValue; // Why old = new : Is this a bug -> TODO : [GB] Check
				handleFileListChange(fileList,oldValue,newValue);
				if(sizeDelta == 1) { // If a delete is done then select the select item 0
				   list.setSelection();
				}
			}

		});
		topLayout.setLayout(layout);
	}

	private void handleFileListChange(MaestroFileListControl fileList, String oldValue[], String newValue[]){
//		values = fileList.getItems();
		fireValueChanged(
				VALUE,
				createList(oldValue),
				createList(newValue));
	}

	/**
	 * Returns the browseType of this field editor's file list control
	 * @return
	 */
	private int getType() {
		return browseType;
	}

	/**
	 * @return the file list control
	 */
	protected List getListControl() {
		return list.getListControl();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#doLoad()
	 */
   @Override
	protected void doLoad() {
		if (list != null) {
			IPreferenceStore store = getPreferenceStore();
			if (store != null) {
				String s = store.getString(getPreferenceName());
				String[] array = parseString(s);
			   keepInit(array);
				list.setList(array);
				list.setSelection(0);
				curUpdatedLists = array;
				// Set the resource the editor works for
				store = ToolSettingsPrefStore.getDefault(); // Note [GB] Hack until we can think of a better ways
				if (store instanceof ToolSettingsPrefStore) {
					IConfiguration config = ((ToolSettingsPrefStore)store).getSelectedConfig();
					if (config != null) {
					   IResource project = config.getOwner();
					   if (project != null) {
					      /* Enable workspace support for list and set project */
					      list.setWorkspaceSupport(true);
					      ToolSettingsPrefStore btsStore = ((ToolSettingsPrefStore)store);
					      Object[] option = btsStore.getOption(getPreferenceName());
					      if(option != null){
					         list.setContext(btsStore.obtainMacroProvider().getMacroContextInfo(IBuildMacroProvider.CONTEXT_OPTION, new OptionContextData((IOption)option[1], (IHoldsOptions)option[0])));
					      }
						}
					}
				}
				else { // the editor is used outside of the expected context try the interface
				   // Assumption for now
				   list.setWorkspaceSupport(true); 
				}
			}
		}
		list.selectionChanged();
	}

	private void keepInit(String[] array) {
      if(initLists == null) {
         initLists = array;
      }
   }

   /* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
	 */
	@Override
	protected void doLoadDefault() {
		if (list != null) {
			list.removeAll();
			String s = getPreferenceStore().getDefaultString(getPreferenceName());
			String[] array = parseString(s);
			list.setList(array);
			curUpdatedLists = array;
			list.selectionChanged();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#doStore()
	 */
	@Override
	protected void doStore() {
		String s = createList(list.getItems());
		if (s != null)
			getPreferenceStore().setValue(getPreferenceName(), s);
	}
	
	public void forceStringListValue(String list[]) {
	   forcedUpdateList = list;
	}

	public String[] getStringListValue(){
	   try {
	      if(forcedUpdateList != null) { // Ugly temporary hack but make code much simpler
	         return forcedUpdateList;
	      }
	      if(disposed) {
	         return curUpdatedLists;
	      }
	      return list.getItems();
	   }
	   catch (SWTException swtException ) { // widget might be disposed! the last valid internal list is nonetheless a valid return case
	      return curUpdatedLists;
	   }
	}

	/**
	* Returns the number of basic controls this field editor consists of.
	*
	* @return the number of controls
	*/
	@Override
	public int getNumberOfControls() {
		return 1;
	}

	/**
	 * Answers a <code>String</code> containing the strings passed in the
	 * argument separated by the DEFAULT_SEPERATOR
	 *
	 * @param items An array of strings
	 * @return
	 */
	private String createList(String[] items) {
		StringBuffer path = new StringBuffer(""); //$NON-NLS-1$

		for (int i = 0; i < items.length; i++) {
			path.append(items[i]);
			if (i < (items.length - 1)) {
				path.append(DEFAULT_SEPARATOR);
			}
		}
		return path.toString();
	}

	/**
	 * Parse the string with the separator and returns the string array.
	 * @param stringList
	 * @return
	 */
	private String[] parseString(String stringList) {
		StringTokenizer tokenizer = new StringTokenizer(stringList, DEFAULT_SEPARATOR);
		ArrayList<String> list = new ArrayList<String>();
		while (tokenizer.hasMoreElements()) {
			list.add((String)tokenizer.nextElement());
		}
		return list.toArray(new String[list.size()]);
	}

	/**
	 * Set style
	 */
	public void setStyle() {
		((GridLayout)topLayout.getLayout()).marginWidth = 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#adjustForNumColumns(int)
	 */
	@Override
	protected void adjustForNumColumns(int numColumns) {
		((GridData)topLayout.getLayoutData()).horizontalSpan = numColumns;
	}

    @Override
	public Label getLabelControl(Composite parent) {
    	return list.getLabelControl();
    }

    @Override
	public void setEnabled(boolean enabled, Composite parent) {
    	list.setEnabled(enabled);
    }


    @Override
    public boolean init(IOption option, String extraArgument, String preferenceName, Composite parent) {
       return false; // Not Implemented for now
    }
    

    @Override
    public Control[] getToolTipSources() {
      return null;
   }

   @Override
   public void propertyChange(PropertyChangeEvent event) {
      if( event.getProperty() == ListCompositeDialog.CANCEL) {
         disposed  = true;
         String lastUpdate[] = curUpdatedLists;
         curUpdatedLists = initLists; 
         handleFileListChange(list, lastUpdate, initLists);
         return;
      }
      if( event.getProperty() == ListCompositeDialog.PATH_RELATIVE) {
         list.setPathRelative((Boolean)(event.getNewValue()));
         return;
      }
      if( event.getProperty() == ListCompositeDialog.PATH_RESOLUTION) {
         list.setPathResolution((PathResolution)event.getNewValue());
         return;
      }
   }
   

   public void setPathSelection(PathResolution pathSelection) {
      list.setPathResolution(pathSelection);
   }

   public void setPathRelative(Boolean pathRelativite) {
      list.setPathRelative(pathRelativite);
   }

}
