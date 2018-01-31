/*---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
Data element, argument for viewer built, contain:
name string, one tree model, one table model.
*/

package cpuid.applications.guimodels;

import javax.swing.tree.DefaultTreeModel;

public class VM1 extends ViewableModel 
{
String x0;                // name string
DefaultTreeModel x1;      // one tree model
ChangeableTableModel x2;  // one table model

// constructor assigns component models to entry fields
public VM1( String y0, DefaultTreeModel y1, ChangeableTableModel y2 )
    {
    x0 = y0;
    x1 = y1;
    x2 = y2;
    }

// get array of components models
@Override public Object[] getValue()
    {
    Object[] value = new Object[3];
    value[0] = x0;
    value[1] = x1;
    value[2] = x2;
    return value;    
    }
}
