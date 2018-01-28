//---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
// List entry for tree GUI element, extended with add identifier.

package cpuid.applications.guimodels;

public class ListEntryApplications extends ListEntry 
{
private final int id;

public ListEntryApplications 
        ( String s1, boolean b1, int n )
    {
    super( s1, "", "", true, b1 );     // No right part, no path, handled
    id = n;
    }

public int getID() 
    {
    return id; 
    }
        
@Override public String toString()
    {
    // return "<html><b><font color=black>" + name1;
    return "<html><b>" + name1;
    }
    
}
