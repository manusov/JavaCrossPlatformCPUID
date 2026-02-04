/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Parent class for elements of trees.

*/

package cpuidv3.sal;

import cpuidv3.servicecpuid.ServiceCpuid.HYBRID_CPU;

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

public class TreeEntry
{ 
    private final int index;
    private final String name1, name2;
    private final boolean cpu, leaf;
    private final HYBRID_CPU hybridType;
    
    public TreeEntry( int i, String s1, String s2, 
        boolean b1, boolean b2, HYBRID_CPU hc )
    {
        index = i;
        name1 = s1; 
        name2 = s2; 
        cpu = b1; 
        leaf = b2; 
        hybridType = hc;
    }
    
    public TreeEntry( String s1, String s2, boolean b1, boolean b2, 
                      HYBRID_CPU hc )
    {
        this( -1, s1, s2, b1, b2, hc );
    }
    
    public TreeEntry( int i, String s1, String s2, boolean b1, boolean b2 )
    {
        this( i, s1, s2, b1, b2, null );
    }

    public TreeEntry( String s1, String s2, boolean b1, boolean b2 )
    {
        this( -1, s1, s2, b1, b2, null );
    }
    
    public int getIndex()     { return index;   }
    public String getName1()  { return name1;   }
    public String getName2()  { return name2;   }
    
    @Override public String toString()
    { 
        if (( cpu )&&( hybridType == HYBRID_CPU.P_CORE ))
        {
            return "<html><b><font color=black>" + name1 + 
                   " <b><font color=blue>" + name2; 
        }
        else if (( cpu )&&( hybridType == HYBRID_CPU.E_CORE ))
        {
            return "<html><b><font color=black>" + name1 + 
                   " <b><font color=green>" + name2; 
        }
        else if (( cpu )&&( hybridType == HYBRID_CPU.LP_E_CORE ))
        {
            return "<html><b><font color=black>" + name1 + 
                   " <b><font color=fuchsia>" + name2; 
        }
        else if ( cpu )
        { 
            return "<html><b><font color=black>" + name1 + " " + name2;
            // return "<html>" + name1 + " " + name2; 
        }
        else if ( leaf )
        { 
            return "<html><b><font color=black>" + name1 + 
                   " = <b><font color=teal>" + name2; 
        }
        else 
        { 
            return name1; 
        }
    }
}
