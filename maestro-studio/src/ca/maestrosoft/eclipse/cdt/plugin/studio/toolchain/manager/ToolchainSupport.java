package ca.maestrosoft.eclipse.cdt.plugin.studio.toolchain.manager;

import org.eclipse.cdt.managedbuilder.core.IManagedIsToolChainSupported;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.osgi.framework.Version;

public class ToolchainSupport implements IManagedIsToolChainSupported {
   
   public static final String COPY_RIGHT_1 = "WARNING: This code is copyright protected. Any attempt to reverse engineer, debug or de-compile this file or its dependent files is strictly prohibited " +
         "and is a breach of the Maestro license and is unlawful.";     

	@Override
	public boolean isSupported(IToolChain toolChain, Version version, String instance) {
		return false;
	}

}
