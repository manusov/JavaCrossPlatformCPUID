/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Model for screen with tree and dependent tables.
Model used for visualization by GUI and generating text reports.

*/

package cpuidv3.services;

import javax.swing.tree.DefaultTreeModel;

public class TreeScreenModel 
{
    public final DefaultTreeModel treeModel;
    public final ChangeableTableModel[] upTables;
    public final ChangeableTableModel[] downTables;
    
    public TreeScreenModel( DefaultTreeModel m,
            ChangeableTableModel[] ut, ChangeableTableModel[] dt )
    {
        treeModel = m;
        upTables = ut;
        downTables = dt;
    }
}
