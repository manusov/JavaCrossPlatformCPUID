/*---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
Data element, argument for viewer built, contain:
name string, one tree model.
*/

package cpuid.applications.guimodels;

import javax.swing.tree.DefaultTreeModel;

public class VM2 extends ViewableModel 
{
String x0;                // name string
DefaultTreeModel x1;      // one tree model

// constructor assigns component models to entry fields
public VM2
    ( String y0, DefaultTreeModel y1 )
    {
    x0 = y0;
    x1 = y1;
    }

// get array of components models
@Override public Object[] getValue()
    {
    Object[] value = new Object[4];
    value[0] = x0;
    value[1] = x1;
    return value;    
    }
}
