package ca.maestrosoft.eclipse.cdt.plugin.studio.option.nameprovider;

import gov.nih.nci.cbiit.cmts.util.ResourceUtils;

import java.util.ArrayList;

import org.eclipse.cdt.core.cdtvariables.CdtVariableException;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.macros.BuildMacroException;
import org.eclipse.cdt.managedbuilder.macros.IBuildMacroProvider;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class NameProvider  {
   
   IBuildMacroProvider provider = null;
   String delimiter = null;
   ITool outTool = null;
   IOption outOption = null;

   protected IPath[] getOutputNames(ITool tool, IPath[] primaryInputNames, String optionId, String extension, boolean override ) {
   	
      provider  = (IBuildMacroProvider)ManagedBuildManager.getBuildMacroProvider();
      delimiter = ManagedBuildManager.getEnvironmentVariableProvider().getDefaultDelimiter();      
		outOption = tool.getOptionBySuperClassId(optionId);
		outTool   = tool;

		if(outOption != null) {
			try {
				String outFile = null;
				Path outPath = null;
				
				if(outOption.getValueType() == IOption.ENUMERATED) {

				   if(outOption.getSelectedEnum().equals(optionId + ".suggest")) {
				      outPath  = new Path(outOption.getEnumCommand(outOption.getSelectedEnum()));
				   }
				   else {
				      IOption optionFile = tool.getOptionBySuperClassId(optionId + ".cache");
				      outPath = new Path(stripRelative(optionFile.getStringValue()));					
				   }
				}
				else if(outOption.getValueType() == IOption.STRING) {
				   outPath  = new Path(stripRelative(outOption.getStringValue()));
				}
				if(outPath != null) {
               if(outPath.hasTrailingSeparator()) {
                  //This is a directory path we need to create a full path with file.
                  outFile = primaryInputNames[0].removeFileExtension().lastSegment();
                  outPath = (Path) outPath.append(outFile).addFileExtension(extension);
               }
               else if(override) {
                  outPath = (Path) outPath.removeFileExtension().addFileExtension(extension);
               }
               outFile = outPath.toString();

               try {
                  outFile = provider.resolveValue(outFile, " ", delimiter, IBuildMacroProvider.CONTEXT_TOOL, tool);
               } 
               catch (BuildMacroException ex) {
                  ex.printStackTrace();
               }
               return new IPath[] {new Path(outFile)};            
				}
			}
			catch (BuildException e) {
			   e.printStackTrace();
			}
		}
		return null;
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
      
      try {
         //Convert with a directory path with a trailing separator
         referencePath = provider.resolveValue(referencePath, "", delimiter, IBuildMacroProvider.CONTEXT_TOOL, outTool);
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
            resolvedPath = provider.resolveValue(curStrPath, "", delimiter, IBuildMacroProvider.CONTEXT_TOOL, outTool);
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
