package ca.maestrosoft.eclipse.cdt.plugin.studio.option.cmdgenerator;

import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IOptionCommandGenerator;
import org.eclipse.cdt.utils.cdtvariables.IVariableSubstitutor;

public class StringOption implements IOptionCommandGenerator {

	@Override
	public String generateCommand(IOption optionCmd, IVariableSubstitutor macroSubstitutor) {

		try {
			if(optionCmd.getValueType() == IOption.STRING) {
            String optionString = optionCmd.getStringValue();
            if(optionString != "") {
               return optionCmd.getCommand() + '"' + optionString + '"';
            }
			}
		} 
		catch (BuildException e) {
		}
		return "";
	}

}
