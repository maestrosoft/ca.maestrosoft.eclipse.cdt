package ca.maestrosoft.eclipse.cdt.plugin.studio.option.cmdgenerator;

import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IOptionCommandGenerator;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.utils.cdtvariables.IVariableSubstitutor;

import ca.maestrosoft.eclipse.cdt.plugin.studio.option.RelativePath;

public class SubsytemVersion extends RelativePath implements IOptionCommandGenerator {

	@Override
	public String generateCommand(IOption optionCmd, IVariableSubstitutor macroSubstitutor) {

      String optionRefId = optionCmd.getBaseId();
      ITool tool = getTool(macroSubstitutor);
           
      IOption optionVersion = tool.getOptionBySuperClassId(optionRefId + ".version");
      
      try {
         if(optionVersion.getStringValue().length() == 0) {
            return null;
         }
         String command = optionCmd.getEnumCommand(optionCmd.getSelectedEnum());
         return command + ',' + optionVersion.getStringValue();
      } 
      catch (BuildException e) {
		}
		return "";
	}

}
