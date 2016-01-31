package ca.maestrosoft.eclipse.cdt.plugin.studio.option.valuehandler;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.cdtvariables.ICdtVariable;
import org.eclipse.cdt.core.cdtvariables.IUserVarSupplier;
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
import org.eclipse.cdt.managedbuilder.macros.IBuildMacroProvider;

import ca.maestrosoft.eclipse.cdt.plugin.studio.option.RelativePath;

public class LinkOutputOption extends RelativePath implements IManagedOptionValueHandler {
	
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
      
      // When loaded the ConfigurationCache manager cannot resolve/set any macro yet
      if(event != IManagedOptionValueHandler.EVENT_LOAD) { 
         
         IUserVarSupplier varSupplier = CCorePlugin.getUserVarSupplier();     
         ICConfigurationDescription configDesc = ManagedBuildManager.getDescriptionForConfiguration(configuration);
         if(configDesc != null && configDesc.getConfigurationData() != null) {

            IOptionApplicability applicability = handleOption.getApplicabilityCalculator();
            if(applicability.isOptionUsedInCommandLine(configuration, holder, handleOption)) {
               
               try {
                  if(handleOption.getSelectedEnum().equals(handleOptionId + ".suggest")) {
                     handleOptionValue = handleOption.getEnumCommand(handleOption.getSelectedEnum());
                  }
                  else {
                     IOption cachedOption = ((ITool)(holder)).getOptionBySuperClassId(handleOptionId + ".cache");
                     handleOptionValue = cachedOption.getStringValue();             
                  }                  
                  
                  if(!handleOptionValue.equals("")) {
                     String optionValue = getRelativePath(new String[] {handleOptionValue}, "${workspace_loc}/${ProjName}/${ConfigName}", IBuildMacroProvider.CONTEXT_TOOL, holder)[0];
                     varSupplier.createMacro("LinkOutputName", ICdtVariable.VALUE_TEXT, optionValue, configDesc);
                  }
                  
                  if(handleOption.getValueType() == IOption.ENUMERATED) {
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
                                       // Fore now the linked option must be a string option per design.
                                       if(linkedOption.getValueType() == IOption.STRING) {
                                          ManagedBuildManager.setOption(configuration, tool, linkedOption, handleOptionValue);
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
