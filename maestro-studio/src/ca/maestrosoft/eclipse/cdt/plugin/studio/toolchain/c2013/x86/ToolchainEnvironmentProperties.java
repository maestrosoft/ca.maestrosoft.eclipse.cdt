package ca.maestrosoft.eclipse.cdt.plugin.studio.toolchain.c2013.x86;

import ca.maestrosoft.eclipse.cdt.plugin.studio.toolchain.manager.IScannerToolchainSet;
import ca.maestrosoft.eclipse.cdt.plugin.studio.toolchain.manager.IScannerToolchainVersion;
import ca.maestrosoft.eclipse.cdt.plugin.studio.toolchain.manager.ToolchainScannerEnvironment;
import ca.maestrosoft.eclipse.cdt.plugin.studio.toolchain.manager.ToolchainSet;
import ca.maestrosoft.eclipse.cdt.plugin.studio.toolchain.manager.ToolChainVersionClass.ToolChainVersion;

public class ToolchainEnvironmentProperties extends ToolchainScannerEnvironment implements IScannerToolchainSet, IScannerToolchainVersion {
	
	public ToolchainEnvironmentProperties() {
		super(toolchainSet.getToolchainSet(), toolchainSet.getToolchainVersion());
	}

	private final static ToolchainScannerSet toolchainSet = new ToolchainScannerSet();
	
	@Override
	public ToolChainVersion getToolchainVersion() {
		return toolchainSet.getToolchainVersion();
	}

	@Override
	public ToolchainSet getToolchainSet() {
		return toolchainSet.getToolchainSet();
	}


}
