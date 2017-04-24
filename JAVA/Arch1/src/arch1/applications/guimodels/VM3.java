//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// Data element, argument for viewer built, contain:
// name string, one table model.

package arch1.applications.guimodels;

public class VM3 extends ViewableModel 
{
String x0;
ChangeableTableModel x1;
public VM3 ( String y0, ChangeableTableModel y1 )
    {
    x0 = y0;
    x1 = y1;
    }
@Override public Object[] getValue()
    {
    Object[] value = new Object[2];
    value[0] = x0;
    value[1] = x1;
    return value;    
    }
}
