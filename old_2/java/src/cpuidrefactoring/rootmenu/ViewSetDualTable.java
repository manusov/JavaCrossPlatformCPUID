/* 
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Set of GUI objects: dual table.
*/

package cpuidrefactoring.rootmenu;

public class ViewSetDualTable extends ViewSet 
{
final String x0;                      // name string
final ChangeableTableModel x1, x2;    // two table models, 
                                // for example cpuid function decode and dump
// constructor assigns component models to entry fields
public ViewSetDualTable 
    ( String y0, ChangeableTableModel y1, ChangeableTableModel y2 )
    {
    x0 = y0;
    x1 = y1;
    x2 = y2;
    }
// get array of components models
@Override public Object[] getGuiObjects()
    {
    return new Object[] { x0, x1, x2 };        
    }
}
