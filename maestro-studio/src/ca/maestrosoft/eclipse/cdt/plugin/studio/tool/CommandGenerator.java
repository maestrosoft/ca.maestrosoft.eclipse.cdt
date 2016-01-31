package ca.maestrosoft.eclipse.cdt.plugin.studio.tool;

import gov.nih.nci.cbiit.cmts.util.ResourceUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.cdt.core.cdtvariables.CdtVariableException;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IBuildObject;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedCommandLineGenerator;
import org.eclipse.cdt.managedbuilder.core.IManagedCommandLineInfo;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedCommandLineGenerator;
import org.eclipse.cdt.managedbuilder.macros.IBuildMacroProvider;
import org.eclipse.core.runtime.Path;

@SuppressWarnings("restriction")
public class CommandGenerator implements IManagedCommandLineGenerator {
	
   ITool tool = null;
   
   public CommandGenerator() {
   }

   @Override
   public IManagedCommandLineInfo generateCommandLineInfo(ITool cmdTool, String commandName, String[] flags, String outputFlag, String outputPrefix, String outputName, String[] inputResources, String commandLinePattern) {

   	ManagedCommandLineGenerator commandManager = ManagedCommandLineGenerator.getCommandLineGenerator();

   	tool = cmdTool;
   	IBuildObject builder = cmdTool.getParent();
   	String baseId = cmdTool.getBaseId();

   	if(builder instanceof IToolChain) {
   		IToolChain toolChain = (IToolChain)builder;
   		ITool allTool[] = toolChain.getTools();
   		IConfiguration configuration = toolChain.getParent();
   		//
   		// This is how we do generate our output for some tools : Link for example.
   		// 1- The output attribute has a provider name classes.
   		// 2- The real output name is defined by an option of the tool
   		// 3- When the name provider for the output is called it return the name provided by the option.
   		// note that we don't use the option capability of the output type. This because our option can use a caching mechanism.
   		// 4- The input to output toolchain tools are resolved through the output name provided by the name provider class.
   		// 5- The tool has a commandLineGenerator class defined that is called when the tool is executed
   		// and the output flag as well as the outname is blanked since the option is used to provide the output
   		// to the tool. The only exception is when the output type is defined as a secondary output since per design
   		// a secondary output name is not included in the command.
   		if(baseId.startsWith("ca.maestro.eclipse.cdt.plugin.studio.link")) {
   			
   			outputFlag = "";
   			outputName = "";

   			for(ITool tool : allTool) {
   				if(tool.getBaseId().startsWith("ca.maestro.eclipse.cdt.plugin.studio.pass2.link")) {
   					IOption allOptions[] = tool.getOptions();
   					for(IOption linkedOption : allOptions ) {
   						if(linkedOption.getId().startsWith("ca.maestro.eclipse.cdt.plugin.studio.pass2.link.general.flags")) {
   			   			List<String> optList = new ArrayList<String>(Arrays.asList(flags));
   			   			optList.add(0, "FLAGS");
   							ManagedBuildManager.setOption(configuration, tool, linkedOption, optList.toArray(new String[optList.size()]));
   						}
   						else if(linkedOption.getId().startsWith("ca.maestro.eclipse.cdt.plugin.studio.pass2.link.general.inputs")) {
   						   if(inputResources == null) {
   						      inputResources = new String[0];  
   						   }
                        int i=0;
   							List<String> inputList = new ArrayList<String>();
   							inputList.add(i++, "INPUTS;");
   							
   							for(String input : inputResources) {
   							   if(i == inputResources.length) {
   							      inputList.add(i++, input);
   							      break;
   							   }
   							   input += ";";
   							   inputList.add(i++, input);
   							}
   							ManagedBuildManager.setOption(configuration, tool, linkedOption, inputList.toArray(new String[inputList.size()]));
   						}
   					}
   					break;
   				}
   			}
   		}
   		else if(baseId.startsWith("ca.maestro.eclipse.cdt.plugin.studio.bscmake")) {
   			
   			outputFlag = "";
   			outputName = "";
   		}
   		else if(baseId.startsWith("ca.maestro.eclipse.cdt.plugin.studio.document")) {
   			
   			outputFlag = "";
   			outputName = "";
   		}   		
   		else if(baseId.startsWith("ca.maestro.eclipse.cdt.plugin.studio.manifest")) {
   			
   			outputFlag = "";
   			outputName = "";
   			
   			List<String> manifestList = new ArrayList<String>();
   			if(inputResources != null) {
   			   if( inputResources.length == 2) {
   					manifestList.add(stripRelative(inputResources[1]));
   			   }
   			   else { // This case will happen when no incremental listing is used.
   			      manifestList.add(stripRelative(inputResources[0])); 
   			   }
				}
				// The input manifest file to the tool must be merged with additionnal manifest files that might be provided
				try {
					IOption optionFile = cmdTool.getOptionBySuperClassId("ca.maestro.eclipse.cdt.plugin.studio.manifest.option.io.input.cache");
					String optionList[] = optionFile.getLibraryFiles();

					if(optionList != null) {
						for(String manifestSupp : optionList) {
							manifestList.add(stripRelative(manifestSupp));
						}
					}
				} 
				catch (BuildException e) {
					e.printStackTrace();
				}
				inputResources = manifestList.toArray(new String[manifestList.size()]);
   		}
   		else if(baseId.startsWith("ca.maestro.eclipse.cdt.plugin.studio.resources")) {
   			
   			outputFlag = "";
   			outputName = "";
   			
   			Integer optionNum = 1;
   			String optionPattern = "#" + optionNum;
   			
   			for(int index=0; index < flags.length; index++) {
   				commandLinePattern = commandLinePattern.replace(optionPattern, flags[index]);
   				optionNum += 1;
   				optionPattern = "#" + optionNum;
   			}
   		}
   		else if(baseId.startsWith("ca.maestro.eclipse.cdt.plugin.studio.pass2.link")) {
   			
   			outputFlag = "";
   			outputName = "";
   			String LinkFlag = "";
   			String LinkInput = "";
   			
   			for(String pattern : flags) {
   				if(pattern.startsWith("FLAGS ")) {
   					LinkFlag = pattern.replaceFirst("FLAGS ", "");
   				}
   				else if(pattern.startsWith("INPUTS; ")) {
   					LinkInput = pattern.replaceFirst("INPUTS; ", "");
   				}
   			}
   			flags = new String[] { LinkFlag };
   			inputResources = LinkInput.split("; ");
   		}
    	}
   	return commandManager.generateCommandLineInfo(cmdTool, commandName, flags, outputFlag, outputPrefix, outputName, inputResources, commandLinePattern);
   }

   protected  String stripRelative(String optionName ) {

      optionName = optionName.replaceAll("\"", "");
      int index = optionName.indexOf('>');
      if(index != -1) {
         return optionName.substring(index+1);
      }
      index = optionName.indexOf(']');
      if(index != -1) {
         return getRelativePath(new String[] {optionName.replaceAll("\\[|\\]", "")}, "${workspace_loc}/${ProjName}/${ConfigName}")[0];
      }
      return getRelativePath(new String[] {optionName}, "${workspace_loc}/${ProjName}/${ConfigName}")[0];
   }
   
   
   protected String[] getRelativePath(String[] inputPath, String referencePath) {
      
      Path absReferencePath = new Path("");
      java.util.List<String> listPath = new ArrayList<String>();
      
      IBuildMacroProvider provider = (IBuildMacroProvider)ManagedBuildManager.getBuildMacroProvider();
      String delimiter = ManagedBuildManager.getEnvironmentVariableProvider().getDefaultDelimiter();

      
      try {
         //Convert with a directory path with a trailing separator
         referencePath = provider.resolveValue(referencePath, "", delimiter, IBuildMacroProvider.CONTEXT_TOOL, tool);
         absReferencePath = new Path(referencePath + "/");
         if(!absReferencePath.hasTrailingSeparator()) {
            absReferencePath.addTrailingSeparator();
         }
      } 
      catch (CdtVariableException e) {
         e.printStackTrace();
      }
      for(String curStrPath : inputPath) {

         String resolvedPath = null;
         try {
            resolvedPath = provider.resolveValue(curStrPath, "", delimiter, IBuildMacroProvider.CONTEXT_TOOL, tool);
            resolvedPath = (new Path(resolvedPath)).makeAbsolute().toString();
            resolvedPath = resolvedPath.replaceAll("\"", "");
            resolvedPath = ResourceUtils.getRelativePath(resolvedPath, absReferencePath.makeAbsolute().toString(), "/");
         }
         catch (CdtVariableException exception) {
            resolvedPath = curStrPath; 
         }
         catch(IllegalArgumentException exception) {
            resolvedPath = curStrPath;
         }
         listPath.add(resolvedPath);
      }
      return listPath.toArray(new String[]{});
   }
}
