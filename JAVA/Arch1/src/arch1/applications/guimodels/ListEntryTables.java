//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// List entry for tree GUI element, 
// with tables for replace panel when node selection.

package arch1.applications.guimodels;

import javax.swing.table.AbstractTableModel;

public class ListEntryTables extends ListEntry
    {
    // private final String name1, name2, path;
    // private final boolean handled, leaf, failed;
    private final AbstractTableModel atm1, atm2;
    
    public ListEntryTables
            ( String s1, String s2, String s3, boolean b1, boolean b2,
              AbstractTableModel a1, AbstractTableModel a2  )  
        { 
        // name1 = s1; 
        // name2 = s2; 
        // path = s3; 
        // handled = b1; 
        // leaf = b2; 
        // failed = false;
        super( s1, s2, s3, b1, b2 );
        atm1 = a1;
        atm2 = a2;
        }
    
    // public String getName1()            { return name1;   }
    // public String getName2()            { return name2;   }
    // public String getPath()             { return path;    }
    // public boolean getHandled()         { return handled; }
    // public boolean getLeaf()            { return leaf;    }
    // public boolean getFailed()          { return failed;  }
    public AbstractTableModel getAtm1() { return atm1;    }
    public AbstractTableModel getAtm2() { return atm2;    }
            
    // @Override public String toString()
    //     { 
    //     if (leaf==false) 
    //         { return name1; }
    //     else if (failed==false) 
    //         { return "<html>" + name1 + " = <b><font color=blue>" + name2; }
    //     else
    //         { return "<html>" + name1 + " = <b><font color=gray>" + name2; }
    //     }
    }
