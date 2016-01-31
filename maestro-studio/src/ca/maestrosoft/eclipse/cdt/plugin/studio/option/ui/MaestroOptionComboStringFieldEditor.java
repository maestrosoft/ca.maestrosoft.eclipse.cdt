package ca.maestrosoft.eclipse.cdt.plugin.studio.option.ui;

import org.eclipse.cdt.managedbuilder.ui.properties.ToolSettingsPrefStore;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;



/*******************************************************************************
 * Maestro Software Tech. implementation
 *******************************************************************************/


public class MaestroOptionComboStringFieldEditor extends MaestroOptionComboListFieldEditor {

   /**
    * Will receive parameters through init
    */
   public MaestroOptionComboStringFieldEditor() {
      super("", "", new String[] {}, "", null);
   }

   /**
    * @param name
    * @param label
    * @param opts
    * @param sel
    * @param parent
    */
   public MaestroOptionComboStringFieldEditor(String name, String label, String [] opts, String sel, Composite parent) {
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

   public MaestroOptionComboStringFieldEditor(String name, String label, String tooltip, String contextId, String [] opts, String sel, Composite parent) {
      this(name, label, opts, sel, parent);
   }
   

   protected int OpenListDialog() {

      if(optionCache != null) { 
         
         MaestroStringFieldEditor fieldEditor = new MaestroStringFieldEditor(optionCache.getBaseId(), optionCache.getName(), compParent);
         fieldEditor.setPreferenceStore(ToolSettingsPrefStore.getDefault());
         fieldEditor.setPropertyChangeListener(this);
         fieldEditor.load(); 
                  
         if(fieldListener != null) {
            ((MaestroStringFieldEditor)(fieldListener)).dispose();
         }
         fieldListener = fieldEditor;
         
         FileCompositeDialog licenseDialog  = new FileCompositeDialog(srollCombo.getShell());
         licenseDialog.setStringFieldEditor(fieldEditor);
         licenseDialog.setExtraArgument(extraArgument);
         licenseDialog.setType(browseType);
         licenseDialog.create();
                  
         return licenseDialog.open();
      }
      else {
         return -1;  
      }
   }

   public void dispose() {
      if(fieldListener != null) {
         ((MaestroStringFieldEditor)(fieldListener)).dispose();
      }
      fieldListener = null;
   }

   @Override
   public void propertyChange(PropertyChangeEvent event) {
      
      Object source = event.getSource();

      if(source instanceof MaestroStringFieldEditor) {
         MaestroStringFieldEditor listEditor = (MaestroStringFieldEditor) source;
         listConcatenateUpdate = listEditor.getStringValue();
      }
   }


}
