/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for representation GUI panel data block, used for save text report.

*/

package cpuidv3.sal;

public class ReportData 
{
    public final String panelName;
    public final String[] tablesPairsNames;
    public final ChangeableTableModel[] upTables;
    public final ChangeableTableModel[] downTables;
    
    public ReportData( String pn, String[] tpn, 
        ChangeableTableModel[] ut, ChangeableTableModel[] dt )
    {
        panelName = pn;
        tablesPairsNames = tpn;
        upTables = ut;
        downTables = dt;
    }
}
