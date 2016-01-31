package ca.maestrosoft.eclipse.cdt.plugin.studio.option.ui;

import org.eclipse.core.runtime.Path;

public enum PathResolution {
   
   Workspace("${workspace_loc}"),
   project("${workspace_loc}/${ProjName}"),
   configuration("${workspace_loc}/${ProjName}/${ConfigName}"),
   output("${workspace_loc}/${ProjName}/${ConfigName}/src"),
   none("");

   private Path variablePath;
   
   public String getPathResVariable() {
      return variablePath.toString();
   }
   
   PathResolution(String varName) {
      variablePath = new Path(varName);
   }
}
