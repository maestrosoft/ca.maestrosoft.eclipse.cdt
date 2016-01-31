package ca.maestrosoft.eclipse.cdt.plugin.studio.option.cmdgenerator;

import java.util.ArrayList;

import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IHoldsOptions;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IOptionCommandGenerator;
import org.eclipse.cdt.utils.cdtvariables.IVariableSubstitutor;

import ca.maestrosoft.eclipse.cdt.plugin.studio.option.RelativePath;

public class IncludeCdt extends RelativePath implements IOptionCommandGenerator {

   @Override
   public String generateCommand(IOption optionCmd, IVariableSubstitutor macroSubstitutor) {

      String allValue = "";
      String[] optionList = null;

      String command = optionCmd.getCommand();
      Object optionListArg = optionCmd.getValue();
      String optionRefId = optionCmd.getBaseId();
      IHoldsOptions optionHolder = optionCmd.getOptionHolder();
      
      // If cache is defined then it already contains the cdt path. 
      optionRefId = optionRefId.substring(0, optionRefId.lastIndexOf("."));       
      IOption optionCached = optionHolder.getOptionBySuperClassId(optionRefId + ".cache");
      try {
         if(optionCached.getStringListValue().length != 0) {
            return "";
         }
      } 
      catch (BuildException e) {
      }

      if(!(optionListArg instanceof ArrayList<?>)) {
         return "";
      }
      ((ArrayList<String>)(optionListArg)).trimToSize();
      optionList = ((ArrayList<String>)(optionListArg)).toArray(new String[0]);

      if(optionList.length != 0) {
         for(String argValue : optionList) {
            allValue += command + '"' + stripRelative(argValue, macroSubstitutor) + '"' + " ";
         }
         allValue = allValue.trim();
         return allValue;
      }
      return "";
   }
}


