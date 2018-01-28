//---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
// Built Controller Adapter.

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
