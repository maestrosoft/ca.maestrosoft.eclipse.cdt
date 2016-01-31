package ca.maestrosoft.eclipse.cdt.plugin.studio.option.ui;

import org.eclipse.cdt.managedbuilder.core.IManagedCommandLineGenerator;
import org.eclipse.cdt.managedbuilder.core.IManagedCommandLineInfo;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.internal.core.Tool;
import org.eclipse.cdt.managedbuilder.internal.macros.BuildMacroProvider;
import org.eclipse.cdt.managedbuilder.internal.macros.BuildfileMacroSubstitutor;
import org.eclipse.cdt.managedbuilder.ui.properties.ICustomBuildOptionEditor;
import org.eclipse.cdt.managedbuilder.ui.properties.ToolSettingsPrefStore;
import org.eclipse.cdt.ui.newui.MultiLineTextFieldEditor;
import org.eclipse.cdt.utils.cdtvariables.SupplierBasedCdtVariableSubstitutor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

@SuppressWarnings("restriction")
public class MaestroMultiLineTextFieldEditor extends MultiLineTextFieldEditor implements ICustomBuildOptionEditor {
   
   private String optionID;

	public MaestroMultiLineTextFieldEditor() {
   }
   
   private void CreateGUI(String name, String label, Composite parent) {
      init(name, label);
      setValidateStrategy(VALIDATE_ON_KEY_STROKE);
      setErrorMessage("Please give correct input");      
      createControl(parent);
   }
   
   
   protected void createControl(Composite parent) {
      super.createControl(parent);
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      gd.widthHint = 100;
      gd.heightHint = 240;
      getTextControl().setLayoutData(gd);
      getTextControl().setEditable(false);
   }
   
   
   @Override
   protected void doLoad() {
      
      if (getTextControl() != null) {
         
         SupplierBasedCdtVariableSubstitutor macroSubstitutor = new BuildfileMacroSubstitutor(null, new String(), " ");
         
         IPreferenceStore store = getPreferenceStore();
         
         if (store instanceof ToolSettingsPrefStore) {
            BuildMacroProvider macProvider = ((ToolSettingsPrefStore)store).obtainMacroProvider();
            Object objHolder[]  = ((ToolSettingsPrefStore)store).getOption(optionID);
            Tool cmdTool = (Tool)objHolder[0];
            
            String[] flags = cmdTool.getToolCommandFlags(null, null, macroSubstitutor, macProvider);
            IManagedCommandLineGenerator cmdLGen = cmdTool.getCommandLineGenerator();
            IManagedCommandLineInfo cmdLInfo = cmdLGen.generateCommandLineInfo(cmdTool, new String(), flags, new String(), new String(), new String(), null, cmdTool.getCommandLinePattern());         
            
            setStringValue(cmdLInfo.getFlags());
          }
      }
   }
   
   public void setEnabled(boolean enabled, Composite parent) {
      return; // Overrides otherwise it will create a new Label control
  }
   
     
   
   @Override
   public boolean init(IOption option, String extraArgument, String preferenceName, Composite parent) {
   	
      optionID = option.getId();
   	
      CreateGUI(ToolSettingsPrefStore.ALL_OPTIONS_ID, option.getName(), parent);
      
      return true;
   }

   @Override
   public Control[] getToolTipSources() {
      return null;
   }



}
