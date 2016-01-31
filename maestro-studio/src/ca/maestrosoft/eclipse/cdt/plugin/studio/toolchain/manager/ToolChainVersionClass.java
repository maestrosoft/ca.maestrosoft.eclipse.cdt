package ca.maestrosoft.eclipse.cdt.plugin.studio.toolchain.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.core.envvar.IEnvironmentVariable;
import org.eclipse.cdt.managedbuilder.envvar.IBuildEnvironmentVariable;

import ca.maestrosoft.eclipse.cdt.plugin.studio.Activator;
import ca.maestrosoft.eclipse.cdt.plugin.studio.helper.registry.WindowsReg;
import ca.maestrosoft.eclipse.cdt.plugin.studio.helper.registry.WindowsSDK;

public final class ToolChainVersionClass {

   public static final String COPY_RIGHT_1 = "WARNING: This code is copyright protected. Any attempt to reverse engineer, debug or de-compile this file or its dependent files is strictly prohibited "
         + "and is a breach of the Maestro license and is unlawful.";

   private static final Map<String, String> visualPredefinedMacro = new HashMap<String, String>();

   static {
      // Should be symbols common to all toolchains
      visualPredefinedMacro.put("_M_IX86", "600");
      visualPredefinedMacro.put("_WIN32", "1");
      visualPredefinedMacro.put("__cdecl", "");
      visualPredefinedMacro.put("__restrict", "");
      visualPredefinedMacro.put("__sptr", "");
      visualPredefinedMacro.put("__stdcall", "");
      visualPredefinedMacro.put("__unaligned", "");
      visualPredefinedMacro.put("__uptr", "");
      visualPredefinedMacro.put("__w64", "");
      visualPredefinedMacro.put("__int8", "char");
      visualPredefinedMacro.put("__int16", "short");
      visualPredefinedMacro.put("__int32", "int");
      visualPredefinedMacro.put("__int64", "long long");
      visualPredefinedMacro.put("_int8", "char");
      visualPredefinedMacro.put("_int16", "short");
      visualPredefinedMacro.put("_int32", "int");
      visualPredefinedMacro.put("_int64", "long long");
      visualPredefinedMacro.put("__ECLIPSE_IDE__", "");
   }

   public enum ToolChainVersion {

      Visual_0000("", "", "", WindowsSDK.WinSDK_0_0.toString()),
    //Visual_2005("Microsoft\\VisualStudio\\SxS\\VC7",  "8.0", "..\\Common7\\IDE", WindowsSDK.WinSDK_6_0.toString()),  // Visual 2005
      Visual_2008("Microsoft\\VisualStudio\\SxS\\VC7",  "9.0", "..\\Common7\\IDE", WindowsSDK.WinSDK_6_0A.toString()), // Visual 2008
      Visual_2010("Microsoft\\VisualStudio\\SxS\\VC7", "10.0", "..\\Common7\\IDE", WindowsSDK.WinSDK_7_0A.toString()), // Visual 2010		
      Visual_2012("Microsoft\\VisualStudio\\SxS\\VC7", "11.0", "..\\Common7\\IDE", WindowsSDK.WinSDK_8_0.toString()),  // Visual 2012
      Visual_2013("Microsoft\\VisualStudio\\SxS\\VC7", "12.0", "..\\Common7\\IDE", WindowsSDK.WinSDK_8_1.toString());  // Visual 2013

		private class BuildEnvironmentVariable implements IBuildEnvironmentVariable {

			private final String varName;
			private final String varValue;
			private final int    varOperation;   

			private BuildEnvironmentVariable(String varName, String varValue, int varOperation) {
				this.varName      = varName;
				this.varValue     = varValue;
				this.varOperation = varOperation;
			}		

			@Override
			public String getName() {
				return varName;
			}

			@Override
			public String getValue() {
				return varValue;
			}

			@Override
			public int getOperation() {
				return varOperation;
			}

			@Override
			public String getDelimiter() {
				return ";";
			}
		}


		private final Map<ToolchainSet, Boolean>                                toolchainSetMap = new HashMap<ToolchainSet, Boolean>();
		private final Map<ToolchainSet, Map<String, IBuildEnvironmentVariable>> envVariablesMap = new HashMap<ToolchainSet, Map<String, IBuildEnvironmentVariable>>();
		private final Map<ToolchainSet, Map<String, String>>                    symbolsMap      = new HashMap<ToolchainSet, Map<String, String>>();

		private String toolChainVersionKey;
		private String toolchainVersionName;
		private String toolchainVersionPath;
		private String toolchainIDEPath;
		private String optionValue;
		
		private WindowsSDK SDKPrefered;
		private WindowsSDK SDKAttached;
		
		private ToolChainVersion() {
		   
		   SDKPrefered = null;
		   SDKAttached = null;
		}

		private ToolChainVersion(String VersionKey, String VersionName, String idePath, String sdkPrefered ) {

			this.toolChainVersionKey  = VersionKey;
			this.toolchainVersionName = VersionName;
			this.toolchainIDEPath     = idePath;
			this.SDKPrefered          = WindowsSDK.valueOf(sdkPrefered);
			
			toolchainVersionPath = null;
			
			if(this.name() == "Visual_0000") {
				 return;
			}
	      if(!Activator.isLinux) {
	         optionValue = VersionKey + "," + VersionName + "," + idePath + "," +  sdkPrefered;
	      }			
			
			try {
				String localKeyPath = "SOFTWARE\\" + toolChainVersionKey;
				
				if(!WindowsReg.checkReadKey(WindowsReg.HKEY_LOCAL_MACHINE, localKeyPath)) {
				   localKeyPath = "SOFTWARE\\Wow6432Node\\" + toolChainVersionKey;
				   if(!WindowsReg.checkReadKey(WindowsReg.HKEY_LOCAL_MACHINE, localKeyPath)) {
				      localKeyPath = null;
				   }
				}				
				if(localKeyPath != null) {
					toolchainVersionPath = WindowsReg.readString(WindowsReg.HKEY_LOCAL_MACHINE, localKeyPath, toolchainVersionName);
				}
			}
			catch (IllegalArgumentException ex) { // TODO : [GB] Should test if reflexion is enabled when started.
			}
			catch (IllegalAccessException ex) {
			}
			catch (InvocationTargetException ex) {
			}

			if( (SDKPrefered != null) && (SDKPrefered.getWinSDKVersionPath() != "") ) {
				SDKAttached = SDKPrefered;	
			}
			else {
				WindowsSDK allSDKs[] = WindowsSDK.values();

				for(int i = SDKPrefered.ordinal()+1; i < allSDKs.length; i++) {
					String sdkPath = allSDKs[i].getWinSDKVersionPath();
					if(sdkPath != null && sdkPath != "") {
						SDKAttached = allSDKs[i];
						break;
					}
				}
				if(SDKAttached == null) {
					for(int i = SDKPrefered.ordinal()-1; i < 0; i--) {
						String sdkPath = allSDKs[i].getWinSDKVersionPath();
						if(sdkPath  != null) {
							SDKAttached = allSDKs[i];
							break;
						}
					}
				}
			}

			for(final ToolchainSet toolchainSet : ToolchainSet.values() ) {

				if(toolchainVersionPath == null) {
					toolchainSetMap.put(toolchainSet, false);	
				}
				else {
					// Example: Execute this command for version 2010
					// cmd /d /c chdir /d C:\Program Files (x86)\Microsoft Visual Studio 10.0\Common7\IDE && ..\..\VC\bin\cl
					//
					final String visualIDEPath = toolchainVersionPath + toolchainIDEPath;
					toolchainSetMap.put(toolchainSet, false);

					try {
						final ProcessBuilder procBuilder = new ProcessBuilder("cmd", "/d", "/c", "chdir", "/d", visualIDEPath, "&&", toolchainSet.getPath() + "\\" + toolchainSet.getCompilerExe());						
						Map<String, String> procEnv = procBuilder.environment();
						
						String envPath = "";
						for(String envVar : procEnv.keySet()) {
						   if(envVar.equalsIgnoreCase("Path")) {
						      envPath = procEnv.get(envVar);
						      break;
						   }
						}
						
			         String path = "";      
			         for(String toolPath : toolchainSet.getToolPath()) {
			            path += toolchainVersionPath + toolPath + ";"; 
			         }
			         path += envPath;
			         procEnv.put("Path", path);
						
						procBuilder.redirectErrorStream(true);
						final Process process = procBuilder.start(); 
						final BufferedReader startupBanner = new BufferedReader(new InputStreamReader(process.getInputStream()));   
						String cmdOutput   = null;
						String compilerVersion = null;
						// This code is softcoded for Microsoft compiler only for now.
						// The concept of searching the compiler should be later generalized
						searchLoop: while ((cmdOutput = startupBanner.readLine()) != null) {
							String cmdSplit[] = cmdOutput.split(" ");
							if(cmdSplit[0].compareTo("Microsoft") == 0) { 
								for(int i = 1; i < cmdSplit.length; i++ ) {
									if(cmdSplit[i].compareTo("C/C++") == 0) { 
										for(int j=i; j < cmdSplit.length; j++) {
											if(cmdSplit[j].compareTo("Version") == 0) {
												compilerVersion = cmdSplit[j+1];
												break searchLoop; 
											}
										}
									}
								}
							}
						}
						if(compilerVersion != null) {
							String toolchainVersionId[] = compilerVersion.split("\\.");
							
							// Visual studio 2012 xpress x86 toolchain versionning is only 3 part. This is a hack
							if(toolchainVersionId.length < 4) {
							   int i = 0;
							   String updToolchainVersioinId[] = new String[4];
							   for(String verId : toolchainVersionId) {
							      updToolchainVersioinId[i++] = verId;
							   }
							   while(i < 4) {
							      updToolchainVersioinId[i++] = "1";  
							   }
							   toolchainVersionId = updToolchainVersioinId;
							}
							createEnvironmentVariables(toolchainSet);
							createPredefinedMacro(toolchainSet, toolchainVersionId);
							toolchainSetMap.put(toolchainSet, true);
						}
					} catch (IOException e) {   
						toolchainSetMap.put(toolchainSet, false); 
					}   
				}
			}
		}


		private void createPredefinedMacro(ToolchainSet toolchainSet, String toolchainVersionId[]) {

			if ( this.ordinal() <= 8) {
				final Map<String, String> visualSymbols = new HashMap<String, String>(visualPredefinedMacro); 

				visualSymbols.putAll(toolchainSet.getSymbols()); //Add specific toolchain set symbols
				visualSymbols.put("_MSC_VER", toolchainVersionId[0] + toolchainVersionId[1]);
				visualSymbols.put("_MSC_BUILD", toolchainVersionId[3]);
				visualSymbols.put("_MSC_FULL_VER", toolchainVersionId[0] + toolchainVersionId[1] + toolchainVersionId[2]);

				symbolsMap.put(toolchainSet, visualSymbols);
			}
		}


		private void createEnvironmentVariables(ToolchainSet toolchainSet) {

			String path;
			String sdkPath;

			Map<String, IBuildEnvironmentVariable> envVariable = new HashMap<String, IBuildEnvironmentVariable>();

			sdkPath = SDKPrefered.getWinSDKIncPath();
			path = toolchainVersionPath + toolchainSet.getIncludePath();
			
			if(sdkPath != "") {
				path += ";" + sdkPath; 
			}
					
			envVariable.put("INCLUDE", new BuildEnvironmentVariable("INCLUDE", path, IEnvironmentVariable.ENVVAR_PREPEND));

			sdkPath = SDKPrefered.getWinSDKLibPath(toolchainSet);
			path = toolchainVersionPath + toolchainSet.getLibPath();
			
			if(sdkPath != "") {
				path += ";" + sdkPath; 
			}

			envVariable.put("LIB", new BuildEnvironmentVariable("LIB", path, IEnvironmentVariable.ENVVAR_PREPEND));

			sdkPath = SDKPrefered.getWinSDKBinPath(toolchainSet);
			path = "";	    
			for(String toolPath : toolchainSet.getToolPath()) {
				path += toolchainVersionPath + toolPath + ";"; 
			}
			
         if(sdkPath != "") {
            path += sdkPath + ";"; 
         }
			
			path = path.substring(0, path.length()-1);
			envVariable.put("PATH", new BuildEnvironmentVariable("PATH", path, IEnvironmentVariable.ENVVAR_PREPEND));		
			envVariablesMap.put(toolchainSet, envVariable);
		}
		

		public boolean isToochainInstalled(ToolchainSet toolchainSet) {
			if(toolchainSet == null) {
				return false;
			}
			Boolean isInstalled = toolchainSetMap.get(toolchainSet);
			if(isInstalled == null) {
				return false;
			}
			return isInstalled;
		}
		
		public Map<String, String> getToochainSymbols(ToolchainSet toolchainSet) { 
			if(toolchainSetMap.get(toolchainSet)) {
				return symbolsMap.get(toolchainSet);
			}
			return null;
		}
		

		public IBuildEnvironmentVariable getEnvVariable(ToolchainSet toolchainSet, String variable) {
			if( envVariablesMap.containsKey(toolchainSet)) {
				return envVariablesMap.get(toolchainSet).get(variable); 
			}
			return null;
		}
		

		public IBuildEnvironmentVariable[] getEnvVariables(ToolchainSet toolchainSet) {
			if( envVariablesMap.containsKey(toolchainSet)) {
				return (BuildEnvironmentVariable[]) ((envVariablesMap.get(toolchainSet)).values()).toArray(new BuildEnvironmentVariable[0]);
			}
			return null;
		}
		

		public String getToochainVersionPath() {
			return toolchainVersionPath;
		}
		
		
	   public String getOptValue() {
	      return optionValue;
	   }  		


		public WindowsSDK getAttachedSDK() {
			return SDKAttached;
		}	
	}
}
