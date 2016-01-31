package ca.maestrosoft.eclipse.cdt.plugin.studio.option.valuehandler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IBuildObject;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IHoldsOptions;
import org.eclipse.cdt.managedbuilder.core.IManagedOptionValueHandler;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IOptionApplicability;
import org.eclipse.cdt.managedbuilder.core.IResourceInfo;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.core.ManagedOptionValueHandler;
import org.eclipse.cdt.managedbuilder.macros.BuildMacroException;
import org.eclipse.cdt.managedbuilder.macros.IBuildMacroProvider;

public class SymbolsOption implements IManagedOptionValueHandler {

   private ManagedOptionValueHandler optionHandler = ManagedOptionValueHandler.getManagedOptionValueHandler();

   // This handle will be called for symbol values to resolve macro when the option is loaded
   // because it belong to another category.
   @Override
   public boolean handleValue(IBuildObject buildObject, IHoldsOptions holder, IOption handleOption, String extraArgument, int event) {

      IConfiguration configuration = null;

      if(buildObject instanceof IConfiguration) {
         configuration = (IConfiguration) buildObject;
      }
      else if(buildObject instanceof IResourceInfo) {
         configuration = ((IResourceInfo)(buildObject)).getParent();
      }

      // When loaded the ConfigurationCache manager cannot resolve/set any macro yet
      if(event != IManagedOptionValueHandler.EVENT_LOAD) { 
 
         ICConfigurationDescription configDesc = ManagedBuildManager.getDescriptionForConfiguration(configuration);
         if(configDesc != null && configDesc.getConfigurationData() != null) {

            IOptionApplicability applicability = handleOption.getApplicabilityCalculator();
            if(applicability.isOptionUsedInCommandLine(configuration, holder, handleOption)) {

               try {
                  if(handleOption.getValueType() == IOption.PREPROCESSOR_SYMBOLS) {

                     String delimiter = ManagedBuildManager.getEnvironmentVariableProvider().getDefaultDelimiter();
                     IBuildMacroProvider provider  = (IBuildMacroProvider)ManagedBuildManager.getBuildMacroProvider();

                     boolean upper = false;
                     boolean setOption = false;
                     String resolvedSymbol;
                     List<String> listSymbol = new ArrayList<String>();
                     String symbols[] = handleOption.getDefinedSymbols();

                     if( symbols == null || symbols.length == 0) {
                        return optionHandler.handleValue(configuration, holder, handleOption, extraArgument, event);
                     }
                     for( String defSymbol : symbols ) {
                        try {
                           resolvedSymbol = defSymbol;
                           int startIdx = defSymbol.lastIndexOf("${");
                           if(startIdx != -1) {
                              int endIdx = defSymbol.lastIndexOf("}");
                              if(endIdx != -1) {
                                 String defMacro = defSymbol.substring(startIdx, endIdx+1);
                                 if(defMacro.lastIndexOf("@UPPER") != -1) {
                                    upper = true;
                                    defMacro = defMacro.replaceFirst("@UPPER", "");
                                 }
                                 String substitutedMacro = provider.resolveValue(defMacro, null, delimiter, IBuildMacroProvider.CONTEXT_CONFIGURATION, configuration);
                                 if(upper) {
                                    substitutedMacro = substitutedMacro.toUpperCase();
                                 }
                                 resolvedSymbol = defSymbol.substring(0, startIdx) + substitutedMacro + defSymbol.substring(endIdx+1);
                              }
                           }
                        } 
                        catch (BuildMacroException e) {
                           resolvedSymbol = defSymbol;
                        }
                        if(resolvedSymbol == null) {
                           resolvedSymbol = defSymbol;
                        }
                        if(!resolvedSymbol.equals(defSymbol)) {
                           setOption = true;  // To prevent infinite recursion.
                        }
                        listSymbol.add(resolvedSymbol);                        
                     }
                     if(setOption) {
                        ManagedBuildManager.setOption(configuration, holder, handleOption, listSymbol.toArray(new String[listSymbol.size()]));
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
