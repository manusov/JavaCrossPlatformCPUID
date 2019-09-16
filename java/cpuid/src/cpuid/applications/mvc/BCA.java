/*---------- CPUID Utility. (C)2019 IC Book Labs -------------------------------
Built Controller Adapter.
Implements public methods: 
get GUI object(s) data model(s) and get GUI object(s) view components.
Note, constructor assigns variables values bm, bv located at child class, 
for flexibility.
*/

package cpuid.applications.mvc;

public class BCA implements BC
{
protected BM bm;
protected BV bv;

@Override public BM getModel()
    { 
    return bm;
    }

@Override public BV getView()
    {
    return bv; 
    }

}
