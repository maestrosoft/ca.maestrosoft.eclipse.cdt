package ca.maestrosoft.eclipse.cdt.plugin.studio.option.cmdgenerator;

import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IOptionCommandGenerator;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.utils.cdtvariables.IVariableSubstitutor;

import ca.maestrosoft.eclipse.cdt.plugin.studio.option.RelativePath;

public class ManifestGeneration extends RelativePath implements IOptionCommandGenerator {
	
	@Override
   public String generateCommand(IOption optionCmd, IVariableSubstitutor macroSubstitutor) {
		
		try {
		   ITool tool = getTool(macroSubstitutor);
		   String optionRefId = optionCmd.getBaseId();

			if(optionCmd.getValueType() == IOption.ENUMERATED) {
				
				if(!optionCmd.getSelectedEnum().equals(optionRefId + ".default")) {
				   return optionCmd.getCommand() + '"' + stripRelative(optionCmd.getEnumCommand(optionCmd.getSelectedEnum()), macroSubstitutor) + '"';
				}
				IOption optionCached = tool.getOptionBySuperClassId(optionRefId + ".cache");
				if(optionCached != null) {
			      if(optionCached.getStringValue().trim().isEmpty()) {
			         return "";
			      }
					return optionCached.getCommand() + '"' + stripRelative(optionCached.getStringValue(), macroSubstitutor) + '"';
				}
			}
		} 
		catch (BuildException e) {
		}
		return "";
	}
}


