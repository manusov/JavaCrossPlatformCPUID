/* 
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Set of GUI objects: single table.
*/

package cpuidrefactoring.rootmenu;

public class ViewSetSingleTable extends ViewSet
{
final String x0;                  // name string
final ChangeableTableModel x1;    // one table model
// constructor assigns component models to entry fields
public ViewSetSingleTable ( String y0, ChangeableTableModel y1 )
    {
    x0 = y0;
    x1 = y1;
    }
// get array of components models
@Override public Object[] getGuiObjects()
    {
    return new Object[] { x0, x1 };
    }
}
