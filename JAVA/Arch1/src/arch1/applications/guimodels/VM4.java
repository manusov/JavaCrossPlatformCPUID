//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// Data element, argument for viewer built, contain:
// name string, two table models.

package arch1.applications.guimodels;

public class VM4 extends ViewableModel 
{
String x0;
ChangeableTableModel x1, x2;
public VM4( String y0, ChangeableTableModel y1, ChangeableTableModel y2 )
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
