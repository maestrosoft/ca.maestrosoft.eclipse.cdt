package ca.maestrosoft.eclipse.cdt.plugin.studio.option.cmdgenerator;

import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IHoldsOptions;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IOptionCommandGenerator;
import org.eclipse.cdt.utils.cdtvariables.IVariableSubstitutor;

import ca.maestrosoft.eclipse.cdt.plugin.studio.option.RelativePath;

public class PrecompiledFile extends RelativePath implements IOptionCommandGenerator {
	
	@Override
   public String generateCommand(IOption optionCmd, IVariableSubstitutor macroSubstitutor) {
		
		try {
			IHoldsOptions optionHolder = optionCmd.getOptionHolder();

			if(optionCmd.getValueType() == IOption.ENUMERATED) {
			   
            String command = optionCmd.getEnumCommand(optionCmd.getSelectedEnum());
            optionCmd = optionHolder.getOptionBySuperClassId("ca.maestro.eclipse.cdt.plugin.studio.cl.option.precompiled.file");

				String optionRefId = optionCmd.getBaseId();
				if(optionCmd.getSelectedEnum().equals(optionRefId + ".suggest")) {
					return command + '"' + getRelativePath(new String[] {optionCmd.getEnumCommand(optionCmd.getSelectedEnum())}, "${workspace_loc}/${ProjName}/${ConfigName}", macroSubstitutor)[0] + '"';
				}
				IOption optionCached = optionHolder.getOptionBySuperClassId(optionRefId + ".cache");
				if(optionCached != null) {
			      if(optionCached.getStringValue().trim().isEmpty()) {
			         return "";
			      }
					return command + '"' + stripRelative(optionCached.getStringValue(), macroSubstitutor) + '"';
				}
			}
		} 
		catch (BuildException e) {
		}
		return "";
	}
}
