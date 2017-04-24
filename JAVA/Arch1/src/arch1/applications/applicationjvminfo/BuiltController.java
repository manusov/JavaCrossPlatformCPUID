//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// MVC "Controller" module for ApplicationJVMinfo,
// Note MVC is Model, View, Controller paradigm.
// Application: Java Virtual Machine (JVM) info.
// Controller interconnects model and view.

package arch1.applications.applicationjvminfo;

import arch1.About;
import arch1.applications.mvc.*;

public class BuiltController extends BCA   //  implements BC 
{
// private final BM bm;
// private final BV bv;

public BuiltController()
    {
    //--- MVC initialization ---    
    bm = new BuiltModel();
    bv = new BuiltView( bm, About.getX2size(), About.getY2size() );
    }

// ApplicationCpuid is simple, uses model and view direct communication,
// without controller (this class), because THIN controller.
// controller required for complex class, example = drawigs

// public BM getModel()      { return bm;}
// public BV getView()       { return bv; }

}
