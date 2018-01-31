/*---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
List entry for tree GUI element, 
with two tables for replace panel when node selection.
*/

package cpuid.applications.guimodels;

import javax.swing.table.AbstractTableModel;

public class ListEntryTables extends ListEntry
    {
    private final AbstractTableModel atm1, atm2;

    // class constructor parameters:
    // s1 = item name
    // s2 = item value, can be visualized as bold or colored
    // s3 = application-specific path of node, example: file path 
    // b1 = if true, means this entry executed, for example opened node
    // b2 = true for leafs (files) , false for openable nodes (directories)
    //      for some applications redundant with setAllowsChildren()
    // a1 = first table model
    // a2 = second table model
    
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
