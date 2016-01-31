package ca.maestrosoft.eclipse.cdt.plugin.studio.toolchain.c2013.cross_x86_ia64;

import org.eclipse.cdt.managedbuilder.core.IManagedIsToolChainSupported;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.osgi.framework.Version;

import ca.maestrosoft.eclipse.cdt.plugin.studio.toolchain.c2013.ToolchainScannerVersion;
import ca.maestrosoft.eclipse.cdt.plugin.studio.toolchain.manager.ToolchainSet;

public class ToolchainScannerSet extends ToolchainScannerVersion implements IManagedIsToolChainSupported {

	public ToolchainSet getToolchainSet() {
		try {
			return ToolchainSet.valueOf("Microsoft_Cross_x86_ia64");  // TODO : [GB] Before releasing Itanium I need to revisit compiler option that doesn't make sense like sse2
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
