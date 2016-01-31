package ca.maestrosoft.eclipse.cdt.plugin.studio.option.cmdgenerator;

import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IHoldsOptions;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IOptionCommandGenerator;
import org.eclipse.cdt.utils.cdtvariables.IVariableSubstitutor;

public class HeapSizeCommit implements IOptionCommandGenerator {

	@Override
	public String generateCommand(IOption optionCmd, IVariableSubstitutor macroSubstitutor) {

		try {
			if(optionCmd.getValueType() == IOption.STRING) {
			   
            String command  = optionCmd.getCommand();
            String size = optionCmd.getStringValue();
            String optionRefId = optionCmd.getBaseId();
            IHoldsOptions optionHolder = optionCmd.getOptionHolder();
            
            if(size != "") {
               IOption optionCommit = optionHolder.getOptionBySuperClassId(optionRefId + ".commit");               
               String commit = optionCommit.getStringValue();
               if(commit != "") {
                  return command + '"'  + size + "," + commit + '"' ;
               }
               return command +  '"' + size + '"';
            }
			}
		} 
		catch (BuildException e) {
		}
		return "";
	}

}
