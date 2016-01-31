package ca.maestrosoft.eclipse.cdt.plugin.studio.helper.registry;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import ca.maestrosoft.eclipse.cdt.plugin.studio.Activator;
import ca.maestrosoft.eclipse.cdt.plugin.studio.toolchain.manager.ToolchainProcessor;
import ca.maestrosoft.eclipse.cdt.plugin.studio.toolchain.manager.ToolchainSet;

	public enum WindowsSDK {

		WinSDK_0_0( "",                                          "",                   "",                     "",   ""),
		WinSDK_6_0( "Microsoft\\Microsoft SDKs\\Windows\\v6.0",  "InstallationFolder", "Include;Include\\gl", "Lib", "Intel_x86"+"=;"+"Intel_x64"+"=x64"),
		WinSDK_6_0A("Microsoft\\Microsoft SDKs\\Windows\\v6.0A", "InstallationFolder", "Include;Include\\gl", "Lib", "Intel_x86"+"=;"+"Intel_x64"+"=x64"),
		WinSDK_7_0( "Microsoft\\Microsoft SDKs\\Windows\\v7.0",  "InstallationFolder", "Include;Include\\gl", "Lib", "Intel_x86"+"=;"+"Intel_x64"+"=x64;"+"Intel_ia64"+"=ia64"),
		WinSDK_7_0A("Microsoft\\Microsoft SDKs\\Windows\\v7.0A", "InstallationFolder", "Include;Include\\gl", "Lib", "Intel_x86"+"=;"+"Intel_x64"+"=x64;"+"Intel_ia64"+"=ia64"),
		WinSDK_7_1( "Microsoft\\Microsoft SDKs\\Windows\\v7_1",  "InstallationFolder", "Include;Include\\gl", "Lib", "Intel_x86"+"=;"+"Intel_x64"+"=x64;"+"Intel_ia64"+"=ia64"),
		WinSDK_8_0( "Microsoft\\Microsoft SDKs\\Windows\\v8.0",  "InstallationFolder", "Include\\um;Include\\um\\gl;Include\\shared", "Lib\\win8\\um", "Intel_x86"+"=x86;"+"Intel_x64"+"=x64;"),
	    WinSDK_8_1( "Microsoft\\Microsoft SDKs\\Windows\\v8.1",  "InstallationFolder", "Include\\um;Include\\um\\gl;Include\\shared", "Lib\\winv6.3\\um", "Intel_x86"+"=x86;"+"Intel_x64"+"=x64;");

		private String winSDKVersionPath;
		private String winSDKIncPath;
		private String winSDKLibPath;
		private String winSDKBinPath;
		private String optionValue;
		
		private Map<ToolchainProcessor, String> libPathMap= new HashMap<ToolchainProcessor, String>();

		WindowsSDK(String winSDKVersionKey, String winSDKVersionName, String winSDKInc, String winSDKLib, String winSDKLibProcessor ) {

		   if(!Activator.isLinux) {
		      optionValue = winSDKVersionKey + "," + winSDKVersionName + "," + winSDKInc + "," +  winSDKLib + "," + winSDKLibProcessor;
		   }

			winSDKVersionPath = null;
			try {
				String localKeyPath = "SOFTWARE\\" + winSDKVersionKey;
				
            if(!WindowsReg.checkReadKey(WindowsReg.HKEY_LOCAL_MACHINE, localKeyPath)) {
               localKeyPath = "SOFTWARE\\Wow6432Node\\" + winSDKVersionKey;
               if(!WindowsReg.checkReadKey(WindowsReg.HKEY_LOCAL_MACHINE, localKeyPath)) {
                  localKeyPath = null;
               }
            }           
				if(localKeyPath != null) {
					winSDKVersionPath = WindowsReg.readString(WindowsReg.HKEY_LOCAL_MACHINE, localKeyPath, winSDKVersionName);
				}
			}
			catch (IllegalArgumentException ex) { // TODO : [GB] Should test if reflexion is enabled when started.
			}
			catch (IllegalAccessException ex) {
			}
			catch (InvocationTargetException ex) {
			}
			
			if(winSDKVersionPath != null) {
				String incFolders[]           = winSDKInc.split(";");
				String libFolders[]           = winSDKLib.split(";");
				String libProcessorFolders[]  = winSDKLibProcessor.split(";");
						
				winSDKIncPath = "";
				for(String include : incFolders) {
					winSDKIncPath += winSDKVersionPath + include + ";";	
				}
				winSDKIncPath = winSDKIncPath.substring(0, winSDKIncPath.length()-1);
				
				winSDKLibPath = "";
				for(String library : libFolders) {
					winSDKLibPath += winSDKVersionPath + library + ";";	
				}
				winSDKLibPath = winSDKLibPath.substring(0, winSDKLibPath.length()-1);
				
				winSDKBinPath = winSDKVersionPath + "Bin";  // FIXME : [GB] We should define the bin path in the enum. This is hardcoded for now
				
				for(String iterPair : libProcessorFolders) {
					String KeyFolder[] = iterPair.split("=");
					if(KeyFolder[0] != "") {
						ToolchainProcessor processor = ToolchainProcessor.valueOf(KeyFolder[0]);
						if(KeyFolder.length == 2) {
							libPathMap.put(processor, KeyFolder[1]);
						}
						else {
							libPathMap.put(processor, "");
						}
					}
				}
			}
			else {
				winSDKVersionPath = "";
				winSDKIncPath     = "";
				winSDKLibPath     = "";
				winSDKBinPath     = "";
			}
		}
		

		public String getWinSDKLibPath(ToolchainSet toolchainSet) {
			
			ToolchainProcessor processor = toolchainSet.getProcessor();
			
			String processorPath = libPathMap.get(processor);
			if(processorPath == null) {
				processorPath =	"";
			}
			
			if(processorPath != "") {
				return winSDKLibPath + "\\" + processorPath;
			}
			return winSDKLibPath;
		}
		
      public String getWinSDKBinPath(ToolchainSet toolchainSet) {
         
         ToolchainProcessor processor = toolchainSet.getProcessor();
         
         String processorPath = libPathMap.get(processor);
         if(processorPath == null) {
            processorPath =   "";
         }
         
         if(processorPath != "") {
            if(!processorPath.equals("ia64")) {  // TODO : [GB] Hardcoded to remove.
               return winSDKBinPath + "\\" + processorPath;
            }
         }
         return winSDKBinPath;
      }
		
		public String getOptValue() {
		   return optionValue;
		}

		public String getWinSDKIncPath() {
			return winSDKIncPath;
		}
		

		public String getWinSDKVersionPath() {
			return winSDKVersionPath;
		}

	}
