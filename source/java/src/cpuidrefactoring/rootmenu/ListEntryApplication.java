/* 
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Element of tree: sub-application.
*/

package cpuidrefactoring.rootmenu;

public class ListEntryApplication extends ListEntry 
{
private final int id;

public ListEntryApplication( String s1, boolean b1, int n )
    {
    super( s1, "", "", true, b1 );     // No right part, no path, handled
    id = n;
    }

public int getID() { return id; }
@Override public String toString() { return "<html><b>" + name1; }
}
