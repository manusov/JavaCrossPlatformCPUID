/*---------- CPUID Utility. (C)2019 IC Book Labs -------------------------------
MVC "Controller" module for ApplicationJVMinfo,
note MVC is Model, View, Controller paradigm.
Application: Java Virtual Machine (JVM) info.
Controller interconnects model and view.
Note, variables bm(BuildModel), bv(BuiltView) located at parent class.
*/

package cpuid.applications.applicationjvmenvr;

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
