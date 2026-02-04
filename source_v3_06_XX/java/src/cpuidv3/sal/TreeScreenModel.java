/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Model for screen with tree and dependent tables.
Model used for visualization by GUI and generating text reports.

*/

package cpuidv3.sal;

import javax.swing.tree.DefaultTreeModel;

public class TreeScreenModel 
{
    public final DefaultTreeModel treeModel;
    public final String[] tablesPairsNames;
    public final ChangeableTableModel[] upTables;
    public final ChangeableTableModel[] downTables;
    
    public TreeScreenModel( DefaultTreeModel m, String[] tpn,
            ChangeableTableModel[] ut, ChangeableTableModel[] dt )
    {
        treeModel = m;
        tablesPairsNames = tpn;
        upTables = ut;
        downTables = dt;
    }
}
