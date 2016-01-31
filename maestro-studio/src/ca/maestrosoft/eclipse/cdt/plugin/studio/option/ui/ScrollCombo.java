package ca.maestrosoft.eclipse.cdt.plugin.studio.option.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class ScrollCombo extends Combo {

   public ScrollCombo(Composite parent, int style) {
      super(parent, style);
   }
   
   

   @Override
   public Point computeSize(int wHint, int hHint, boolean flushCache) {
      // Is there a better way than subclassing ?
   	// NOTE : [GB] Yes it is possible. User a custom layout !
   	//
      if(flushCache == true && wHint == SWT.DEFAULT) {
         GC gc = new GC(this);
         gc.setFont(getFont());
         FontMetrics fontMetrics= gc.getFontMetrics();
         gc.dispose();
         
         Point size = super.computeSize(wHint, hHint, flushCache);
         int defaultWidth = fontMetrics.getAverageCharWidth()*70;
         size.x = Math.min(defaultWidth, size.x);
         return size;
      }
      return super.computeSize(wHint, hHint, flushCache);
   }
   
   // Minor subclassing shouldn't be a problem
   protected void checkSubclass () {
   }
   
}
