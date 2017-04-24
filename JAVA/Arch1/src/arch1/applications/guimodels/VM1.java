//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// Data element, argument for viewer built, contain:
// name string, one tree model, one table model.

package arch1.applications.guimodels;

import javax.swing.tree.DefaultTreeModel;

public class VM1 extends ViewableModel 
{
String x0;
DefaultTreeModel x1;
ChangeableTableModel x2;
public VM1( String y0, DefaultTreeModel y1, ChangeableTableModel y2 )
    {
    x0 = y0;
    x1 = y1;
    x2 = y2;
    }
@Override public Object[] getValue()
    {
    Object[] value = new Object[3];
    value[0] = x0;
    value[1] = x1;
    value[2] = x2;
    return value;    
    }
}
