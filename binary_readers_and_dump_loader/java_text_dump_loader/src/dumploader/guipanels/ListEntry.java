/* 

This code is fragment of Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Parent class for elements of trees.

*/

package dumploader.guipanels;

import dumploader.cpuid.IHybrid.HYBRID_CPU;

/*
Strings notes:
name1 = item name
name2 = item value, can be visualized as bold or colored
path = application-specific path of node, example: file path.

Boolean flags notes:
handled = if true, means this entry executed, for example opened node
leaf = true for leafs (files) , false for openable nodes (directories)
       for some applications redundant with setAllowsChildren()
failed = true if error detected.
*/

class ListEntry
{ 
    private final String name1, name2, path;
    private final boolean cpu, leaf;
    private final int index;
    private final HYBRID_CPU hybridType;
    
    public ListEntry( String s1, String s2, String s3, boolean b1, boolean b2 )
    { 
        name1 = s1; 
        name2 = s2; 
        path = s3; 
        cpu = b1; 
        leaf = b2; 
        index = -1;
        hybridType = null;
    }

    public ListEntry( String s1, String s2, String s3, 
                      boolean b1, boolean b2, int i )
    {
        name1 = s1; 
        name2 = s2; 
        path = s3; 
        cpu = b1; 
        leaf = b2; 
        index = i;
        hybridType = null;
    }

    public ListEntry( String s1, String s2, String s3, 
                      boolean b1, boolean b2, int i, HYBRID_CPU hc )
    {
        name1 = s1; 
        name2 = s2; 
        path = s3; 
        cpu = b1; 
        leaf = b2; 
        index = i;
        hybridType = hc;
    }

    public String getPath()     { return path;    }
    public int getIndex()       { return index;   }
    
    @Override public String toString()
    { 
        if (( cpu )&&( hybridType == HYBRID_CPU.P_CORE ))
        {
            return "<html><b><font color=blue>" + name1 + 
                   " <b><font color=black>" + name2; 
        }
        else if (( cpu )&&( hybridType == HYBRID_CPU.E_CORE ))
        {
            return "<html><b><font color=blue>" + name1 + 
                   " <b><font color=green>" + name2; 
        }
        else if (( cpu )&&( hybridType == HYBRID_CPU.LP_E_CORE ))
        {
            return "<html><b><font color=blue>" + name1 + 
                   " <b><font color=fuchsia>" + name2; 
        }
        else if ( cpu )
        { 
            return "<html><b><font color=blue>" + name1 + " " + name2; 
        }
        else if ( leaf )
        { 
            return "<html>" + name1 + " = <b><font color=teal>" + name2; 
        }
        else 
        { 
            return name1; 
        }
    }
}
