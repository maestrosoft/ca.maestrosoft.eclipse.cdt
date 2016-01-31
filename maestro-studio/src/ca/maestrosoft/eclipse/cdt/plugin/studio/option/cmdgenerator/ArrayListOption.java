package ca.maestrosoft.eclipse.cdt.plugin.studio.option.cmdgenerator;

import java.util.ArrayList;

import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IOptionCommandGenerator;
import org.eclipse.cdt.utils.cdtvariables.IVariableSubstitutor;

import ca.maestrosoft.eclipse.cdt.plugin.studio.option.RelativePath;

public class ArrayListOption extends RelativePath implements IOptionCommandGenerator {

   @Override
   public String generateCommand(IOption optionCmd, IVariableSubstitutor macroSubstitutor) {

      String allValue = "";
      String[] optionList = null;

      String command = optionCmd.getCommand();
      Object optionListArg = optionCmd.getValue();

      if(!(optionListArg instanceof ArrayList<?>)) {
         return "";
      }
      ((ArrayList<String>)(optionListArg)).trimToSize();
      optionList = ((ArrayList<String>)(optionListArg)).toArray(new String[0]);

      if(optionList.length != 0) {
         for(String argValue : optionList) {
            if(argValue.trim() != "") {
               allValue += command + '"' + stripRelative(argValue, macroSubstitutor) + '"' + " ";
            }
         }
         allValue = allValue.trim();
         return allValue;
      }
      return "";
   }
}


