package ca.maestrosoft.eclipse.cdt.plugin.studio.option.nameprovider;

import org.eclipse.cdt.managedbuilder.core.IManagedOutputNameProvider;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.core.runtime.IPath;


public class LinkerIncremental extends NameProvider implements IManagedOutputNameProvider {

   @Override
   public IPath[] getOutputNames(ITool tool, IPath[] primaryInputNames) {

      IPath[] output = getOutputNames(tool, primaryInputNames, "ca.maestro.eclipse.cdt.plugin.studio.link.option.general.output.name", "ilk", true);
      return output;
   }
}
