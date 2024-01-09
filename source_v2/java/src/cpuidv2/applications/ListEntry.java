/* 
Java cross-platform CPUID Utility.
This source (Java CPUID v2.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source_v2
Previous source (Java CPUID v1.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source
All repositories: 
https://github.com/manusov?tab=repositories
(C) Manusov I.V. Refactoring at 2024.
-------------------------------------------------------------------------------
Parent class for elements of trees.
*/

package cpuidv2.applications;

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

public class ListEntry
{ 
final String name1, name2, path;
final boolean handled, leaf, failed;
final int index;
    
public ListEntry( String s1, String s2, String s3, boolean b1, boolean b2 )
    { 
    name1 = s1; 
    name2 = s2; 
    path = s3; 
    handled = b1; 
    leaf = b2; 
    failed = false;
    index = -1;
    }

public ListEntry( String s1, String s2, String s3, boolean b1, boolean b2,
                  int index )
    {
    name1 = s1; 
    name2 = s2; 
    path = s3; 
    handled = b1; 
    leaf = b2; 
    failed = false;
    this.index = index;
    }

public String getName1()    { return name1;   }
public String getName2()    { return name2;   }
public String getPath()     { return path;    }
public boolean getHandled() { return handled; }
public boolean getLeaf()    { return leaf;    }
public boolean getFailed()  { return failed;  }
public int getIndex()       { return index;   }
    
@Override public String toString()
    { 
    if ( leaf == false )     // Select variants for nodes=brief, leafs=detail.
        { return name1; }
    else if ( failed == false ) 
        { return "<html>" + name1 + " = <b><font color=blue>" + name2; }
    else
        { return "<html>" + name1 + " = <b><font color=gray>" + name2; }
    }
}
