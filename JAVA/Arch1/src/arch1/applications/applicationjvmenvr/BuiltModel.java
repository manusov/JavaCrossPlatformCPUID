//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// MVC "Model" module for ApplicationJVMinfo,
// Note MVC is Model, View, Controller paradigm.
// Application: Java Virtual Machine (JVM) info.
// Model provide data model, use PAL and local builder.

package arch1.applications.applicationjvmenvr;

import arch1.Arch1;
import arch1.applications.guimodels.ChangeableTableModel;
import arch1.applications.guimodels.ModelBuilder;
import arch1.applications.guimodels.ViewableModel;
import arch1.applications.guimodels.VM3;
// import arch1.applications.mvc.BM;
import arch1.applications.mvc.BMA;
import arch1.drivers.cpr.Device;
// import arch1.kernel.PAL;
import arch1.kernel.Registry;

public class BuiltModel extends BMA   //  implements BM 
{
// private final PAL pal;
// private final ModelBuilder jmb;
    
public BuiltModel()
    {
    //--- Initialize JVM info model builder ---
    Registry r = Arch1.getRegistry();
    pal = r.getPAL();
    cmb = new JVMenvironmentModelBuilder();
    }

/*
@Override public PAL getPal() 
    {
    return pal; 
    }

@Override public ModelBuilder getModel() 
    {
    return jmb; 
    }
*/

//---------- Model builder for JVM environment data source declaration ---------

protected class JVMenvironmentModelBuilder implements ModelBuilder 
    {
    private final static String NAME = "JVM information";
    private final ViewableModel vm;
    private final Device device;
    
    public JVMenvironmentModelBuilder()
        {
        //--- Load driver ---
        Registry r = Arch1.getRegistry();
        device = r.loadDriver(Registry.CPR.driverJVMENVR);
        String[] sa1 = device.getSummaryUp();
        String[][] sa2 = device.getSummaryText();
        ChangeableTableModel m1 = new ChangeableTableModel(sa1, sa2);
        vm = new VM3(NAME, m1);
        }

    @Override public int getCount()
        {
        return 1;
        }

    @Override public ViewableModel getValue(int i)
        {
        return vm;
        }

    @Override public long[] getBinary()
        {
        return null;  // not used for this model
        }

    @Override public boolean setBinary( long[] x )
        {
        return false;  // not used for this model
        }
    
    }

}
