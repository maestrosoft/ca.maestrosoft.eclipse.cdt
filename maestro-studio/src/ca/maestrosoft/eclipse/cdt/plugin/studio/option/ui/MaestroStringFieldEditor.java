package ca.maestrosoft.eclipse.cdt.plugin.studio.option.ui;

import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.ui.properties.ICustomBuildOptionEditor;
import org.eclipse.cdt.managedbuilder.ui.properties.StringFieldEditorM;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


public class MaestroStringFieldEditor extends StringFieldEditorM implements ICustomBuildOptionEditor, IPropertyChangeListener {

   private String lastValue;

   public MaestroStringFieldEditor(String name, String labelText, Composite parent) {
      super(name, labelText, parent);
   }
   
   
   protected void createControl(Composite parent) {
      doFillIntoGrid(parent, 2);
  }
   

   public void setPathSelection(PathResolution pathSelection) {
      
   }

   public void setPathRelative(Boolean pathRelativite) {
   }
   
   public void setStringValue(String value) {
      super.setStringValue(value);
      lastValue = value;
   }
   
   // Hack to overcome the fact that the string value is lost after the dispose of the string field editor in a close dialog
   public String getStringValue() {
      if(getTextControl() == null) {
         if(lastValue != null) {
           return lastValue; 
         }
      }
      return super.getStringValue();
   }
   

   @Override
   public void propertyChange(PropertyChangeEvent event) {
      
   }

   @Override
   public boolean init(IOption option, String extraArgument, String preferenceName, Composite parent) {
      return false; // Not Implemented for now
   }

   @Override
   public Control[] getToolTipSources() {
      return null;
   }
   

}
