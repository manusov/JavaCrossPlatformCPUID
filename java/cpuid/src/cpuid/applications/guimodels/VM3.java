/*---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
Data element, argument for viewer built, contain:
name string, one table model.
*/

package cpuid.applications.guimodels;

public class VM3 extends ViewableModel 
{
String x0;                  // name string
ChangeableTableModel x1;    // one table model

// constructor assigns component models to entry fields
public VM3 ( String y0, ChangeableTableModel y1 )
    {
    x0 = y0;
    x1 = y1;
    }

// get array of components models
@Override public Object[] getValue()
    {
    Object[] value = new Object[2];
    value[0] = x0;
    value[1] = x1;
    return value;    
    }
}
