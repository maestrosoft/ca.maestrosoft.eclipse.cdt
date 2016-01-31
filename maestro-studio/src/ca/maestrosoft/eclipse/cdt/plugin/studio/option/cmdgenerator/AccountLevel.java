package ca.maestrosoft.eclipse.cdt.plugin.studio.option.cmdgenerator;

import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IOptionCommandGenerator;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.utils.cdtvariables.IVariableSubstitutor;

import ca.maestrosoft.eclipse.cdt.plugin.studio.option.RelativePath;

public class AccountLevel extends RelativePath implements IOptionCommandGenerator {

	@Override
	public String generateCommand(IOption optionCmd, IVariableSubstitutor macroSubstitutor) {

      String optionRefId = optionCmd.getBaseId();
      ITool tool = getTool(macroSubstitutor);
           
      IOption optionUACLevel = tool.getOptionBySuperClassId(optionRefId + ".uaclevel");
      IOption optionUACBypass = tool.getOptionBySuperClassId(optionRefId + ".uacbypass");

      try {      
         String levelCommand = optionUACLevel.getEnumCommand(optionUACLevel.getSelectedEnum());
         String bypassCommand = optionUACBypass.getEnumCommand(optionUACBypass.getSelectedEnum());
         String accountCommand = optionCmd.getEnumCommand(optionCmd.getSelectedEnum());
         
         if(optionCmd.getSelectedEnum().equals(optionRefId + ".no")) {
            return accountCommand;
         }

         return accountCommand + '"' + levelCommand + " " + bypassCommand + '"';
      } 
      catch (BuildException e) {
      }
      return "";
	}

}


