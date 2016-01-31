package ca.maestrosoft.eclipse.cdt.plugin.studio.option;

import gov.nih.nci.cbiit.cmts.util.ResourceUtils;

import java.util.ArrayList;

import org.eclipse.cdt.core.cdtvariables.CdtVariableException;
import org.eclipse.cdt.managedbuilder.core.IBuildObject;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.internal.macros.IMacroContextInfo;
import org.eclipse.cdt.managedbuilder.macros.IBuildMacroProvider;
import org.eclipse.cdt.managedbuilder.macros.IFileContextData;
import org.eclipse.cdt.managedbuilder.macros.IOptionContextData;
import org.eclipse.cdt.utils.cdtvariables.CdtVariableResolver;
import org.eclipse.cdt.utils.cdtvariables.IVariableContextInfo;
import org.eclipse.cdt.utils.cdtvariables.IVariableSubstitutor;
import org.eclipse.cdt.utils.cdtvariables.SupplierBasedCdtVariableSubstitutor;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;

public abstract class RelativePath {
   
   protected Path getProjectPath(IVariableSubstitutor macroSubstitutor) {

      IProject project = null;
      IConfiguration config = null;
      Path projectPath = null;
      
      if(macroSubstitutor instanceof SupplierBasedCdtVariableSubstitutor) {
         IVariableContextInfo varsContextInfo  = ((SupplierBasedCdtVariableSubstitutor)(macroSubstitutor)).getMacroContextInfo();
         if(varsContextInfo instanceof IMacroContextInfo) {
            Object contextInfo = ((IMacroContextInfo)(varsContextInfo)).getContextData();
            if(contextInfo instanceof IFileContextData) { 
               IOptionContextData contextData = ((IFileContextData)(contextInfo)).getOptionContextData();
               IBuildObject buildObject = contextData.getParent();
               if(buildObject instanceof ITool) {
                  buildObject = ((ITool)(buildObject)).getParent();
                  if(buildObject instanceof IToolChain) {
                     buildObject = ((IToolChain)(buildObject)).getParent();
                     if(buildObject instanceof IConfiguration) {
                        config = (IConfiguration)buildObject;
                        project = config.getOwner().getProject();
                        projectPath = new Path(project.getLocationURI().getPath());
                        
                        return projectPath;
                     }
                  }
               }
            }
         }
      }
      return null;
   }
   
   protected ITool getTool(IVariableSubstitutor macroSubstitutor) {

      if(macroSubstitutor instanceof SupplierBasedCdtVariableSubstitutor) {
         IVariableContextInfo varsContextInfo  = ((SupplierBasedCdtVariableSubstitutor)(macroSubstitutor)).getMacroContextInfo();
         if(varsContextInfo instanceof IMacroContextInfo) {
            Object contextInfo = ((IMacroContextInfo)(varsContextInfo)).getContextData();
            if(contextInfo instanceof IFileContextData) { 
               IOptionContextData contextData = ((IFileContextData)(contextInfo)).getOptionContextData();
               IBuildObject buildObject = contextData.getParent();
               if(buildObject instanceof ITool) {
                  return ((ITool)(buildObject));
               }
            }
         }
      }
      return null;
   }


	
   protected  String stripRelative(String optionName, IVariableSubstitutor macroSubstitutor) {

   	optionName = optionName.replaceAll("\"", "");
      int index = optionName.indexOf('>');
      if(index != -1) {
         return optionName.substring(index+1);
      }
      index = optionName.indexOf(']');
      if(index != -1) {
         return getRelativePath(new String[] {optionName.replaceAll("\\[|\\]", "")}, "${workspace_loc}/${ProjName}/${ConfigName}", macroSubstitutor)[0];
      }
      return getRelativePath(new String[] {optionName}, "${workspace_loc}/${ProjName}/${ConfigName}", macroSubstitutor)[0];
   }
	
	
   protected String[] getRelativePath(String[] inputPath, String referencePath, IVariableSubstitutor macroSubstitutor) {
   	
   	Path absReferencePath = new Path("");
   	java.util.List<String> listPath = new ArrayList<String>();
   	
      try {
         //Convert with a directory path with a trailing separator
      	referencePath = CdtVariableResolver.resolveToString(referencePath, macroSubstitutor);
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
   			resolvedPath = CdtVariableResolver.resolveToString(curStrPath, macroSubstitutor);
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
   
      

   
   protected String[] getRelativePath(String[] inputPath, String referencePath, int contextType, Object context) {
      
      if( inputPath == null ) {
         return new String[] {""};
      }         
      Path absReferencePath = new Path("");
      java.util.List<String> listPath = new ArrayList<String>();
      
      String delimiter = ManagedBuildManager.getEnvironmentVariableProvider().getDefaultDelimiter();
      IBuildMacroProvider provider  = (IBuildMacroProvider)ManagedBuildManager.getBuildMacroProvider();
      
      try {
         //Convert with a directory path with a trailing separator
         String resolvedPath = provider.resolveValue(referencePath, "", delimiter, contextType, context);
         absReferencePath = new Path(resolvedPath + "/");
         if(!absReferencePath.hasTrailingSeparator()) {
            absReferencePath.addTrailingSeparator();
         }
      } 
      catch (CdtVariableException e) {
         e.printStackTrace();
      }
      catch(NullPointerException exception) {
         return inputPath;
      }
      
      for(String curStrPath : inputPath) {

         String resolvedPath = null;
         try {
            resolvedPath = provider.resolveValue(curStrPath, "", delimiter, contextType, context);
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
         catch(NullPointerException exception) {
            resolvedPath = curStrPath;
         }
         listPath.add(resolvedPath);
      }
      return listPath.toArray(new String[]{});
   }
   
   
   
   protected String[] getRelativePath(String[] inputPath, String referencePath, Object context) {
      return getRelativePath( inputPath, referencePath, IBuildMacroProvider.CONTEXT_TOOL, context );      
   }
   

}
