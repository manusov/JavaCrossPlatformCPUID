//---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
// List entry for tree GUI element, 
// with two tables for replace panel when node selection.

package cpuid.applications.guimodels;

import javax.swing.table.AbstractTableModel;

public class ListEntryTables extends ListEntry
    {
    private final AbstractTableModel atm1, atm2;
    
    public ListEntryTables
            ( String s1, String s2, String s3, boolean b1, boolean b2,
              AbstractTableModel a1, AbstractTableModel a2  )  
        { 
        super( s1, s2, s3, b1, b2 );
        atm1 = a1;
        atm2 = a2;
        }
    
    public AbstractTableModel getAtm1() { return atm1; }
    public AbstractTableModel getAtm2() { return atm2; }
    }
