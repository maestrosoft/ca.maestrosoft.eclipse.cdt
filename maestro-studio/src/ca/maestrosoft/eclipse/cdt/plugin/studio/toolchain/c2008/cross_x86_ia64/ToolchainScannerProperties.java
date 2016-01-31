package ca.maestrosoft.eclipse.cdt.plugin.studio.toolchain.c2008.cross_x86_ia64;

import ca.maestrosoft.eclipse.cdt.plugin.studio.toolchain.manager.IScannerToolchainSet;
import ca.maestrosoft.eclipse.cdt.plugin.studio.toolchain.manager.IScannerToolchainVersion;
import ca.maestrosoft.eclipse.cdt.plugin.studio.toolchain.manager.ToolchainScannerConfiguration;
import ca.maestrosoft.eclipse.cdt.plugin.studio.toolchain.manager.ToolchainSet;
import ca.maestrosoft.eclipse.cdt.plugin.studio.toolchain.manager.ToolChainVersionClass.ToolChainVersion;


public class ToolchainScannerProperties extends ToolchainScannerConfiguration implements IScannerToolchainSet, IScannerToolchainVersion {

	
	private final ToolchainScannerSet toolchainSet = new ToolchainScannerSet();
	
	@Override
	public ToolChainVersion getToolchainVersion() {
		return toolchainSet.getToolchainVersion();
	}

	@Override
	public ToolchainSet getToolchainSet() {
		return toolchainSet.getToolchainSet();
	}
	
}
