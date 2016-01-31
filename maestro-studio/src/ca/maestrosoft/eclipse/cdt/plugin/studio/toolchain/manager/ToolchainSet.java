package ca.maestrosoft.eclipse.cdt.plugin.studio.toolchain.manager;

import java.util.HashMap;
import java.util.Map;

import ca.maestrosoft.eclipse.cdt.plugin.studio.Activator;

public enum ToolchainSet {

	Microsoft_00("", "", "", "", "", "", "Intel_x86"),
	Microsoft_x86(           "..\\..\\VC\\bin",             "include", "lib",        "..\\Common7\\IDE;..\\Common7\\Tools;Bin;vcpackages",                       "_M_X86=",    "cl", ToolchainProcessor.Intel_x86.toString()),
	Microsoft_x86_64(        "..\\..\\VC\\bin\\amd64",      "include", "lib\\amd64", "..\\Common7\\IDE;..\\Common7\\Tools;Bin\\amd64;vcpackages",         "_WIN64=;_M_X64=",   "cl", ToolchainProcessor.Intel_x64.toString()),
	Microsoft_Cross_x86_64(  "..\\..\\VC\\bin\\x86_amd64",  "include", "lib\\amd64", "..\\Common7\\IDE;..\\Common7\\Tools;Bin\\x86_amd64;bin;vcpackages", "_WIN64=;_M_X64=",   "cl", ToolchainProcessor.Intel_x64.toString()),
	Microsoft_Cross_x86_ia64("..\\..\\VC\\bin\\x86_ia64",   "include", "lib\\ia64",  "..\\Common7\\IDE;..\\Common7\\Tools;Bin\\x86_ia64;bin;vcpackages",  "_WIN64=;_M_IA64=",  "cl", ToolchainProcessor.Intel_ia64.toString()),
	Microsoft_Cross_ARM(     "..\\..\\VC\\TBD\\bin",        "include", "lib",        "..\\Common7\\IDE;..\\Common7\\Tools;TBD\\Bin;vcpackages",                  "",           "cl", ToolchainProcessor.ARM.toString());

	
	public static final String COPY_RIGHT_1 = "WARNING: This code is copyright protected. Any attempt to reverse engineer, debug or de-compile this file or its dependent files is strictly prohibited " +
			  "and is a breach of the Maestro license and is unlawful.";  	
	
	private String compilerFromIdePath;
	private String compilerExeName;
	private String includePath;
	private String libPath;
	private String optionValue;
	private String toolPath[];
	private ToolchainProcessor processor;
	private Map<String, String> defSymbolsMap = new HashMap<String, String>();
	
	ToolchainSet(String compilerFromIdePath, String includePath, String libPath, String toolPath, String defSymbols, String compilerExeName, String processor) {
		
		this.compilerExeName     = compilerExeName;		
		this.compilerFromIdePath = compilerFromIdePath;
		this.includePath         = includePath;
		this.libPath             = libPath;
		this.toolPath            = toolPath.split(";");
		this.processor           = ToolchainProcessor.valueOf(processor);
		
      if(!Activator.isLinux) {
         optionValue = compilerFromIdePath + "," + includePath + "," + libPath + "," +  toolPath + "," + defSymbols + "," + compilerExeName + "," + processor;
      }
		
				
		String pairSymbols[] = defSymbols.split(";");
		
		for(String iterPair : pairSymbols) {
			String KeySymbols[] = iterPair.split("=");
			if(KeySymbols[0] != null && !KeySymbols[0].isEmpty()) {
				if(KeySymbols.length == 2) {
					defSymbolsMap.put(KeySymbols[0], KeySymbols[1]);
				}
				else {
					defSymbolsMap.put(KeySymbols[0], "");
				}
			}
		}
	}
	

	public String getPath() {
		return compilerFromIdePath;
	}
	
	public String getCompilerExe() {
		return compilerExeName;
	}
	
	public String getIncludePath() {
		return includePath;
	}

	public String getLibPath() {
		return libPath;
	}
	
	public String[] getToolPath() {
		return toolPath;
	}
	
   public String getOptValue() {
      return optionValue;
   }	

	public Map<String, String> getSymbols() {
		return defSymbolsMap;
	}

	public ToolchainProcessor getProcessor() {
		return processor;
	}


}
