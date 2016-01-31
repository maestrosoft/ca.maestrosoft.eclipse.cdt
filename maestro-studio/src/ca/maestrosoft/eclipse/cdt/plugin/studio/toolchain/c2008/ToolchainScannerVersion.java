package ca.maestrosoft.eclipse.cdt.plugin.studio.toolchain.c2008;

import ca.maestrosoft.eclipse.cdt.plugin.studio.toolchain.manager.ToolchainSet;
import ca.maestrosoft.eclipse.cdt.plugin.studio.toolchain.manager.ToolChainVersionClass.ToolChainVersion;

public abstract class ToolchainScannerVersion {

	public ToolChainVersion getToolchainVersion() {

		try {
			return ToolChainVersion.valueOf("Visual_2008");
		}
		catch (Exception exception ) {
			return ToolChainVersion.Visual_0000;
		}
	}
	
	public abstract ToolchainSet getToolchainSet();

}
