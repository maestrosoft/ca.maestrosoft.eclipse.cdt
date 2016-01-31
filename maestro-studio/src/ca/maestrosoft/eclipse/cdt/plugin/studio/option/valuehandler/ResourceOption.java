package ca.maestrosoft.eclipse.cdt.plugin.studio.option.valuehandler;

import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IBuildObject;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IHoldsOptions;
import org.eclipse.cdt.managedbuilder.core.IManagedOptionValueHandler;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IOptionApplicability;
import org.eclipse.cdt.managedbuilder.core.IResourceInfo;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.core.ManagedOptionValueHandler;

public class ResourceOption implements IManagedOptionValueHandler {
	
	private ManagedOptionValueHandler optionHandler = ManagedOptionValueHandler.getManagedOptionValueHandler();

	// This handle will be called when an option cannot not be reevaluated through the BuildOptionSettingsUI.propertyChange
	// because it belong to another category.
	@Override
	public boolean handleValue(IBuildObject buildObject, IHoldsOptions holder, IOption handleOption, String extraArgument, int event) {
		
      String handleOptionId = handleOption.getBaseId();  
      String handleOptionValue = null;
      IConfiguration configuration = null;
            
      if(buildObject instanceof IConfiguration) {
         configuration = (IConfiguration) buildObject;
      }
      else if(buildObject instanceof IResourceInfo) {
         configuration = ((IResourceInfo)(buildObject)).getParent();
      }
      
      try {
         if(handleOption.getValueType() == IOption.ENUMERATED) {
            if(handleOption.getSelectedEnum().equals(handleOptionId + ".suggest")) {
               handleOptionValue = handleOption.getEnumCommand(handleOption.getSelectedEnum());
            }
            else {
               IOption cachedOption = ((ITool)(holder)).getOptionBySuperClassId(handleOptionId + ".cache");
               handleOptionValue = cachedOption.getStringValue();             
            }
         }
         else if(handleOption.getValueType() == IOption.STRING) {
            handleOptionValue = handleOption.getStringValue(); 
         }
         else {
            return optionHandler.handleValue(configuration, holder, handleOption, extraArgument, event); 
         }
      }
      catch (BuildException ex) {
	      ex.printStackTrace();
	      return false;
      }
      
      if(event != IManagedOptionValueHandler.EVENT_LOAD) { 

         ICConfigurationDescription configDesc = ManagedBuildManager.getDescriptionForConfiguration(configuration);
         if(configDesc != null && configDesc.getConfigurationData() != null) {

            IOptionApplicability applicability = handleOption.getApplicabilityCalculator();
            if(applicability.isOptionVisible(configuration, holder, handleOption)) {

               // Get all the tools belonging to the toolchain to which belong this option
               if(holder instanceof ITool) {
                  if((((ITool)(holder)).getParent() instanceof IToolChain)) {
                     IToolChain toolChain = (IToolChain)((ITool)(holder)).getParent();
                     ITool allTool[] = toolChain.getTools(); 
                     // Enumerate all the options belonging to each tool and get the options 
                     for(ITool tool : allTool) {
                        IOption allOptions[] = tool.getOptions();

                        for(IOption linkedOption : allOptions ) {
                           String extraArgArray[] = ((IOption)(linkedOption)).getValueHandlerExtraArgument().split(";");

                           for(String extraArg : extraArgArray) {
                              if(handleOptionId.equals(extraArg)) {
                                 // For now only enumerated option is supported.
                                 try {
                                    // Fore now the linked option must be a string option per design.
                                    if(linkedOption.getValueType() == IOption.STRING) {
                                       ManagedBuildManager.setOption(configuration, tool, linkedOption, handleOptionValue);
                                    }
                                 } 
                                 catch (BuildException e) {
                                    e.printStackTrace();
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
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
