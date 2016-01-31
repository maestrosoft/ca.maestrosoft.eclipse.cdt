package ca.maestrosoft.eclipse.cdt.plugin.studio.toolchain.manager;

import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedProject;
import org.eclipse.cdt.managedbuilder.envvar.IBuildEnvironmentVariable;
import org.eclipse.cdt.managedbuilder.envvar.IConfigurationEnvironmentVariableSupplier;
import org.eclipse.cdt.managedbuilder.envvar.IEnvironmentVariableProvider;
import org.eclipse.cdt.managedbuilder.envvar.IProjectEnvironmentVariableSupplier;

import ca.maestrosoft.eclipse.cdt.plugin.studio.toolchain.manager.ToolChainVersionClass.ToolChainVersion;

public class ToolchainScannerEnvironment implements IConfigurationEnvironmentVariableSupplier, IProjectEnvironmentVariableSupplier, IScannerToolchainVersion, IScannerToolchainSet {
   
   public static final String COPY_RIGHT_1 = "WARNING: This code is copyright protected. Any attempt to reverse engineer, debug or de-compile this file or its dependent files is strictly prohibited " +
         "and is a breach of the Maestro license and is unlawful.";     
	
	private final ToolchainSet     toolchainSet;
	private final ToolChainVersion ToochainVersion;

	public ToolchainScannerEnvironment(ToolchainSet toolchainSet, ToolChainVersion ToochainVersion) {
		
		this.toolchainSet    = toolchainSet;
		this.ToochainVersion = ToochainVersion;
	}

	@Override
	public IBuildEnvironmentVariable getVariable(String variableName, IManagedProject project, IEnvironmentVariableProvider provider) {
		if(variableName != null ) {
			return ToochainVersion.getEnvVariable(toolchainSet, variableName);
		}
		return null;
	}

	@Override
	public IBuildEnvironmentVariable[] getVariables(IManagedProject project, IEnvironmentVariableProvider provider) { // TODO : [GB] Check if project might add something to environment
		return ToochainVersion.getEnvVariables(toolchainSet);
	}

	@Override
	public IBuildEnvironmentVariable getVariable(String variableName, IConfiguration configuration, IEnvironmentVariableProvider provider) {
		if(variableName != null ) {
			return ToochainVersion.getEnvVariable(toolchainSet, variableName);
		}
		return null;
	}

	@Override
	public IBuildEnvironmentVariable[] getVariables(IConfiguration configuration, IEnvironmentVariableProvider provider) {
		return ToochainVersion.getEnvVariables(toolchainSet);
	}

	@Override
	public ToolchainSet getToolchainSet() {
		return null;
	}

	@Override
	public ToolChainVersion getToolchainVersion() {
		return null;
	}
	
}
