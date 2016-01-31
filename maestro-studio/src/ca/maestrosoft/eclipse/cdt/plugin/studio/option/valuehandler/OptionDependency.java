package ca.maestrosoft.eclipse.cdt.plugin.studio.option.valuehandler;

import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IBuildObject;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IHoldsOptions;
import org.eclipse.cdt.managedbuilder.core.IManagedOptionValueHandler;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IResourceInfo;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.core.ManagedOptionValueHandler;

public class OptionDependency implements IManagedOptionValueHandler {

   private ManagedOptionValueHandler optionHandler = ManagedOptionValueHandler.getManagedOptionValueHandler();

   // This handle will be called when an option cannot not be reevaluated through the BuildOptionSettingsUI.propertyChange
   // because it belong to another category.
   @Override
   public boolean handleValue(IBuildObject buildObject, IHoldsOptions holder, IOption handleOption, String extraArgument, int event) {
      
      String handleOptionId = null;  
      String handleOptionValue = null;
      IConfiguration configuration = null;
      
      if(buildObject instanceof IConfiguration) {
         configuration = (IConfiguration) buildObject;
      }
      else if(buildObject instanceof IResourceInfo) {
         configuration = ((IResourceInfo)(buildObject)).getParent();
      }
      
      try {
         if(handleOption.getValueType() != IOption.ENUMERATED) {
            return optionHandler.handleValue(configuration, holder, handleOption, extraArgument, event);
         }
      } 
      catch (BuildException ex) {
         ex.printStackTrace();
         return false;
      }
      String optionSetting[] = extraArgument.split("=");
      handleOptionId = optionSetting[0];
      handleOptionValue = optionSetting[1];
      //
      // This is really a very hardcoded stuff for now.
      //
      try {
         // Get all the tools belonging to the toolchain to which belong this option
         if(holder instanceof ITool) {
            if((((ITool)(holder)).getParent() instanceof IToolChain)) {
               IToolChain toolChain = (IToolChain)((ITool)(holder)).getParent();
               ITool allTool[] = toolChain.getTools(); 
               // Enumerate all the options belonging to each tool and get the options 
               for(ITool tool : allTool) {
                  IOption allOptions[] = tool.getOptions();
                  for(IOption linkedOption : allOptions ) {
                     if(handleOptionId.equals(linkedOption.getBaseId())) {
                        // For now only enumerated option is supported.
                        IOption optionCached = tool.getOptionById(linkedOption.getBaseId() + ".cache"); // Dont change the option if a cached option is defined by the user
                        if(optionCached != null && optionCached.getStringValue().equals("")) {   //
                           if(handleOption.getSelectedEnum().equals(handleOption.getBaseId() + ".yes")) {  // This is harcoded for now.
                              if(linkedOption.getValueType() == IOption.ENUMERATED) {
                                 ManagedBuildManager.setOption(configuration, tool, linkedOption, handleOptionId + "." + handleOptionValue);
                              }
                           }
                           if(handleOption.getSelectedEnum().equals(handleOption.getBaseId() + ".no")) { // This is harcoded for now.
                              if(linkedOption.getValueType() == IOption.ENUMERATED) {
                                 ManagedBuildManager.setOption(configuration, tool, linkedOption, handleOptionId + "." + "default");
                              }                           
                           }
                        }
                     } 
                  }
               }
            }
         }
      } 
      catch (BuildException e) {
         e.printStackTrace();
      }
      return optionHandler.handleValue(configuration, holder, handleOption, extraArgument, event);      
   }

   @Override
   public boolean isDefaultValue(IBuildObject configuration, IHoldsOptions holder, IOption option, String extraArgument) {
      return optionHandler.isDefaultValue(configuration, holder, option, extraArgument);
   }

   @Override
   public boolean isEnumValueAppropriate(IBuildObject configuration, IHoldsOptions holder, IOption option, String extraArgument, String enumValue) {
      return optionHandler.isEnumValueAppropriate(configuration, holder, option, extraArgument, enumValue);
   }

}
