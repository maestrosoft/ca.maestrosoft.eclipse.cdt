package ca.maestrosoft.eclipse.cdt.plugin.studio.toolchain.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.make.core.scannerconfig.IDiscoveredPathManager.IDiscoveredPathInfo;
import org.eclipse.cdt.make.core.scannerconfig.IDiscoveredPathManager.IDiscoveredScannerInfoSerializable;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import ca.maestrosoft.eclipse.cdt.plugin.studio.toolchain.manager.ToolChainVersionClass.ToolChainVersion;

public class ToolchainPathInfo implements IDiscoveredPathInfo {
	
	public static final String COPY_RIGHT_1 = "WARNING: This code is copyright protected. Any attempt to reverse engineer, debug or de-compile this file or its dependent files is strictly prohibited " +
			  								  "and is a breach of the Maestro license and is unlawful.";  	
	

	ToolChainVersion toolchainVersion;
	ToolchainSet     toolchainSet;
	
	public ToolchainPathInfo(ToolChainVersion toolchainVersion, ToolchainSet toolchainSet) {
		
		this.toolchainSet     = toolchainSet;
		this.toolchainVersion = toolchainVersion;
	}

	@Override
	public IProject getProject() {
		return null;
	}

	@Override
	public IPath[] getIncludePaths() {
		if(toolchainSet == null || toolchainVersion == null) {
			return null;
		}
		List<IPath> includePaths = new ArrayList<IPath>();
		includePaths.add(new Path(toolchainVersion.getToochainVersionPath() + toolchainSet.getIncludePath()));
		
		String winSDKIncPath = toolchainVersion.getAttachedSDK().getWinSDKIncPath();
		String winSDKIncPaths[] = winSDKIncPath.split(";");
		if(winSDKIncPath != "") {
			for(String path : winSDKIncPaths) {
				includePaths.add(new Path(path));
			}
		}
		return includePaths.toArray(new IPath[0]);
	}
	

	@Override
	public Map<String, String> getSymbols() {
		if(toolchainSet == null || toolchainVersion == null) {
			return null;
		}		
		return toolchainVersion.getToochainSymbols(toolchainSet);
	}
	

	@Override
	public IDiscoveredScannerInfoSerializable getSerializable() {
		return null;
	}

}
