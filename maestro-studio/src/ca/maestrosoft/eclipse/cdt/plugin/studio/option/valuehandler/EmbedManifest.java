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
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.core.ManagedOptionValueHandler;
import org.eclipse.cdt.managedbuilder.macros.IBuildMacroProvider;

import ca.maestrosoft.eclipse.cdt.plugin.studio.option.RelativePath;

public class EmbedManifest extends RelativePath implements IManagedOptionValueHandler {

   private ManagedOptionValueHandler optionHandler = ManagedOptionValueHandler.getManagedOptionValueHandler();

   // This handle will be called when an option cannot not be reevaluated through the BuildOptionSettingsUI.propertyChange
   // because it belong to another category.
   @Override
   public boolean handleValue(IBuildObject buildObject, IHoldsOptions holder, IOption handleOption, String extraArgument, int event) {

      // When loaded the ConfigurationCache manager cannot resolve/set any macro yet
      if(event != IManagedOptionValueHandler.EVENT_LOAD) { 

         IConfiguration configuration = (IConfiguration) buildObject;      
         IUserVarSupplier varSupplier = CCorePlugin.getUserVarSupplier();     
         ICConfigurationDescription configDesc = ManagedBuildManager.getDescriptionForConfiguration(configuration);
         
         if(configDesc != null && configDesc.getConfigurationData() != null) {
            try {
               if(!handleOption.getStringValue().equals("")) {
                  String optionValue = getRelativePath(new String[] {handleOption.getStringValue()}, "${workspace_loc}/${ProjName}/${ConfigName}", IBuildMacroProvider.CONTEXT_TOOL, holder)[0];
                  varSupplier.createMacro("EmbedManifest", ICdtVariable.VALUE_TEXT, optionValue, configDesc);
               }
            } 
            catch (BuildException e) {
               e.printStackTrace();
            }
            catch(NullPointerException e) {         
            }
         }
      }
      return optionHandler.handleValue(buildObject, holder, handleOption, extraArgument, event);
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
