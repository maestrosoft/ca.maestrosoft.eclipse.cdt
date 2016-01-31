package ca.maestrosoft.eclipse.cdt.plugin.studio.option.cmdgenerator;

import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IOptionCommandGenerator;
import org.eclipse.cdt.utils.cdtvariables.IVariableSubstitutor;

import ca.maestrosoft.eclipse.cdt.plugin.studio.option.RelativePath;

public class ResourceNameDll extends RelativePath implements IOptionCommandGenerator {

	@Override
	public String generateCommand(IOption option, IVariableSubstitutor macroSubstitutor) {
	   
		try {
			if(option.getValueType() == IOption.STRING) {
				return option.getCommand() + '"' + stripRelative(option.getStringValue(), macroSubstitutor) + ";#2" + '"';
			}
		} 
		catch (BuildException e) {
		}
		return "";
	}

}
