package ca.maestrosoft.eclipse.cdt.plugin.studio.option.cmdgenerator;

import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IOptionCommandGenerator;
import org.eclipse.cdt.utils.cdtvariables.IVariableSubstitutor;

import ca.maestrosoft.eclipse.cdt.plugin.studio.option.RelativePath;

public class HiddenFileOption extends RelativePath implements IOptionCommandGenerator {

	@Override
	public String generateCommand(IOption optionCmd, IVariableSubstitutor macroSubstitutor) {

		try {
			if(optionCmd.getValueType() == IOption.STRING) {
				String optionString = optionCmd.getStringValue();
				if(optionString != "") {
					return optionCmd.getCommand().replace("${value}", "") + '"' + stripRelative(optionString, macroSubstitutor) + '"';
				}
			}
		} 
		catch (BuildException e) {
		}
		return "";
	}

}
