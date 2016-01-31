package ca.maestrosoft.eclipse.cdt.plugin.studio.toolchain.c2012.cross_x86_amd64;

import org.eclipse.cdt.managedbuilder.core.IManagedIsToolChainSupported;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.osgi.framework.Version;

import ca.maestrosoft.eclipse.cdt.plugin.studio.toolchain.c2012.ToolchainScannerVersion;
import ca.maestrosoft.eclipse.cdt.plugin.studio.toolchain.manager.ToolchainSet;

public class ToolchainScannerSet extends ToolchainScannerVersion implements IManagedIsToolChainSupported  {

	public ToolchainSet getToolchainSet() {
		try {
			return ToolchainSet.valueOf("Microsoft_Cross_x86_64");
		}
		catch (Exception exception ) {
			return null;
		}
	}

	@Override
	public boolean isSupported(IToolChain toolChain, Version version, String instance) {

		return getToolchainVersion().isToochainInstalled(getToolchainSet());  
	}

}
