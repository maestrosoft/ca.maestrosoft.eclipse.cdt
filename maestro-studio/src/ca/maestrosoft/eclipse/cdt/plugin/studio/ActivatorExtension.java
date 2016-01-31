package ca.maestrosoft.eclipse.cdt.plugin.studio;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.plugin.AbstractUIPlugin;

public class ActivatorExtension extends AbstractUIPlugin {  
      
   final protected Map<String, Map<String, String>> optionMaps = Collections.synchronizedMap(new HashMap<String, Map<String, String>>());
   //final protected static Map<String, String> toolchainPlugin;
   
   final protected Map<String, String> toolchainVersion = Collections.synchronizedMap(new HashMap<String, String>());
   final protected Map<String, String> toolchainSet     = Collections.synchronizedMap(new HashMap<String, String>());
   final protected Map<String, String> windowsSDK       = Collections.synchronizedMap(new HashMap<String, String>());
   final protected Map<String, String> extensionVersion = Collections.synchronizedMap(new HashMap<String, String>());
     
   public enum PluginVersion {

      Release_1_1("");
      
      PluginVersion(String version) {         
      }
   }   
   
   ActivatorExtension() {
      super();
   }
}   
