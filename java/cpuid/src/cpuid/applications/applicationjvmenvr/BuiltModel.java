//---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
// MVC "Model" module for ApplicationJVMinfo,
// Note MVC is Model, View, Controller paradigm.
// Application: Java Virtual Machine (JVM) info.
// Model provide data model, use PAL and local builder.

package cpuid.applications.applicationjvmenvr;

import cpuid.CpuId;
import cpuid.applications.guimodels.ChangeableTableModel;
import cpuid.applications.guimodels.ModelBuilder;
import cpuid.applications.guimodels.ViewableModel;
import cpuid.applications.guimodels.VM3;
import cpuid.applications.mvc.BMA;
import cpuid.drivers.cpr.Device;
import cpuid.kernel.Registry;

public class BuiltModel extends BMA   //  implements BM 
{
    
public BuiltModel()
    {
    // Initialize JVM info model builder
    Registry r = CpuId.getRegistry();
    pal = r.getPAL();
    cmb = new JVMenvironmentModelBuilder();
    }

// Model builder for JVM environment data source declaration

protected class JVMenvironmentModelBuilder implements ModelBuilder 
    {
    private final static String NAME = "JVM information";
    private final ViewableModel vm;
    private final Device device;
    
    public JVMenvironmentModelBuilder()
        {
        // Load driver
        Registry r = CpuId.getRegistry();
        device = r.loadDriver(Registry.CPR.driverJVMENVR);
        String[] sa1 = device.getSummaryUp();
        String[][] sa2 = device.getSummaryText();
        ChangeableTableModel m1 = new ChangeableTableModel(sa1, sa2);
        vm = new VM3(NAME, m1);
        }

    @Override public int getCount()
        { return 1; }

    @Override public ViewableModel getValue(int i)
        { return vm; }

    @Override public long[] getBinary()
        { return null; }   // not used for this model

    @Override public boolean setBinary( long[] x )
        { return false; }   // not used for this model
            
    }

}
