/* 
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Set of GUI objects: tree for selections.
*/

package cpuidrefactoring.rootmenu;

import javax.swing.tree.DefaultTreeModel;

public class ViewSetTree extends ViewSet
{
final String x0;                // name string
final DefaultTreeModel x1;      // one tree model
// constructor assigns component models to entry fields
public ViewSetTree( String y0, DefaultTreeModel y1 )
    {
    x0 = y0;
    x1 = y1;
    }
// get array of components models
@Override public Object[] getGuiObjects()
    {
    return new Object[] { x0, x1, null, null };
    }
}
