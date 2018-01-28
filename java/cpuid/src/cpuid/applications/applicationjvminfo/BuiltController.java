//---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
// MVC "Controller" module for ApplicationJVMinfo,
// Note MVC is Model, View, Controller paradigm.
// Application: Java Virtual Machine (JVM) info.
// Controller interconnects model and view.

package cpuid.applications.applicationjvminfo;

import cpuid.applications.mvc.BCA;
import cpuid.About;

public class BuiltController extends BCA   //  implements BC 
{

public BuiltController()
    {
    // MVC initialization
    bm = new BuiltModel();
    bv = new BuiltView( bm, About.getX2size(), About.getY2size() );
    }

}
