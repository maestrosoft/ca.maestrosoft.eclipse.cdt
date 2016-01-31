package ca.maestrosoft.eclipse.cdt.plugin.studio.toolchain.manager;

import java.util.List;
import java.util.Map;

import org.eclipse.cdt.make.core.scannerconfig.IDiscoveredPathManager.IDiscoveredPathInfo;
import org.eclipse.cdt.make.core.scannerconfig.IScannerInfoCollector3;
import org.eclipse.cdt.make.core.scannerconfig.InfoContext;
import org.eclipse.cdt.make.core.scannerconfig.ScannerInfoTypes;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import ca.maestrosoft.eclipse.cdt.plugin.studio.toolchain.manager.ToolChainVersionClass.ToolChainVersion;

public class ToolchainScannerConfiguration implements IScannerInfoCollector3, IScannerToolchainVersion, IScannerToolchainSet {
   
   public static final String COPY_RIGHT_1 = "WARNING: This code is copyright protected. Any attempt to reverse engineer, debug or de-compile this file or its dependent files is strictly prohibited " +
         "and is a breach of the Maestro license and is unlawful.";   

	protected ToolChainVersion  toolchainVersion;
	protected ToolchainSet      toolchainSet;
	protected IProject          project;
	protected InfoContext       context;
	protected IConfiguration 	 configuration;
	protected IManagedBuildInfo buildInfo;
	
	@Override
	public void setProject(IProject project) {
		this.project = project;
	}

	@Override
	public void updateScannerConfiguration(IProgressMonitor monitor)
			throws CoreException {
	}

	@Override
	public IDiscoveredPathInfo createPathInfoObject() {
		return new ToolchainPathInfo(getToolchainVersion(), getToolchainSet());
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void contributeToScannerConfig(Object resource, Map scannerInfo) {
	}

	@Override
	public List<?> getCollectedScannerInfo(Object resource, ScannerInfoTypes type) {
		return null;
	}

	@Override
	public void setInfoContext(InfoContext context) {
		this.context = context;
		buildInfo = ManagedBuildManager.getBuildInfo(context.getProject());
	   configuration = buildInfo.getDefaultConfiguration();
	}

	@Override
	public ToolChainVersion getToolchainVersion() {
		return null;
	}

	@Override
	public ToolchainSet getToolchainSet() {
		return null;
	}

}
