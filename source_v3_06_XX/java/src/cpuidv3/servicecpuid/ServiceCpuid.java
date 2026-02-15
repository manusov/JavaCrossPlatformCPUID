/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Special thanks to Todd Allen CPUID project
https://etallen.com/cpuid.html
http://www.etallen.com/

This file contains Processors and Hypervisors data exported from
Todd Allen CPUID project. Some variables and functions names not compliant
with java naming conventions, this fields using original C/C++ naming.

Special thanks to:
https://refactoring.guru/design-patterns/singleton/java/example#example-2
https://refactoring.guru/java-dcl-issue
about Singleton pattern.

Decoder class for interpreting CPUID functions information.
Text arrays = F ( CPUID binary dump ).

*/

package cpuidv3.servicecpuid;

import cpuidv3.servicecpudata.EntryCpuidSubfunction;
import cpuidv3.servicecpudata.ServiceCpudata.HYPERVISOR_T;
import static cpuidv3.servicecpudata.ServiceCpudata.HYPERVISOR_T.HYPERVISOR_XEN;
import cpuidv3.servicecpudata.ServiceCpudata.VENDOR_T;
import java.util.ArrayList;

public final class ServiceCpuid 
{
    public enum HYBRID_CPU 
        { DEFAULT, P_CORE, E_CORE, LP_E_CORE, RESERVED, UNKNOWN };
    
    // Thread-safe singleton pattern for ServiceCpuid class.
    private static volatile ServiceCpuid instance;
    public static ServiceCpuid getInstance()
    {
        ServiceCpuid result = instance;
        if ( result != null )
        {
            return result;
        }
        // Some redundant operations required for thread-safe, see links above.
        synchronized( ServiceCpuid.class ) 
        {
            if ( instance == null ) 
            {
                instance = new ServiceCpuid();
            }
            return instance;
        }
    }
    // For singleton, constructor must be private.
    // Dump not set in the constructor, for easy reload.
    private ServiceCpuid() { /* Reserved. */ }

    // CPUID functions classes.
    
    private final static ReservedFunctionCpuid[] STANDARD_FUNCTIONS =
    {   new Cpuid00000000(),
        new Cpuid00000001(),
        new Cpuid00000002(),
        new Cpuid00000003(),
        new Cpuid00000004(),
        new Cpuid00000005(),
        new Cpuid00000006(),
        new Cpuid00000007(),
        new Cpuid00000008(),
        new Cpuid00000009(),
        new Cpuid0000000A(),
        new Cpuid0000000B(),
        new Cpuid0000000C(),
        new Cpuid0000000D(),
        new Cpuid0000000E(),
        new Cpuid0000000F(),
        new Cpuid00000010(),
        new Cpuid00000011(),
        new Cpuid00000012(),
        new Cpuid00000013(),
        new Cpuid00000014(),
        new Cpuid00000015(),
        new Cpuid00000016(),
        new Cpuid00000017(),
        new Cpuid00000018(),
        new Cpuid00000019(),
        new Cpuid0000001A(),
        new Cpuid0000001B(),
        new Cpuid0000001C(),
        new Cpuid0000001D(),
        new Cpuid0000001E(),
        new Cpuid0000001F(),
        new Cpuid00000020(),
        new Cpuid00000021(),
        new Cpuid00000022(),
        new Cpuid00000023(),
        new Cpuid00000024(),
        new Cpuid00000025(),
        new Cpuid00000026(),
        new Cpuid00000027(),
        new Cpuid00000028(),
        new Cpuid00000029()  };
    
    private final static ReservedFunctionCpuid[] EXTENDED_FUNCTIONS =
    {   new Cpuid80000000(),
        new Cpuid80000001(),
        new Cpuid80000002(),
        new Cpuid80000003(),
        new Cpuid80000004(),
        new Cpuid80000005(),
        new Cpuid80000006(),
        new Cpuid80000007(),
        new Cpuid80000008(),
        new Cpuid80000009(),
        new Cpuid8000000A(),
        new Cpuid8000000B(),
        new Cpuid8000000C(),
        new Cpuid8000000D(),
        new Cpuid8000000E(),
        new Cpuid8000000F(),
        new Cpuid80000010(),
        new Cpuid80000011(),
        new Cpuid80000012(),
        new Cpuid80000013(),
        new Cpuid80000014(),
        new Cpuid80000015(),
        new Cpuid80000016(),
        new Cpuid80000017(),
        new Cpuid80000018(),
        new Cpuid80000019(),
        new Cpuid8000001A(),
        new Cpuid8000001B(),
        new Cpuid8000001C(),
        new Cpuid8000001D(),
        new Cpuid8000001E(),
        new Cpuid8000001F(),
        new Cpuid80000020(),
        new Cpuid80000021(),
        new Cpuid80000022(),
        new Cpuid80000023(),
        new Cpuid80000024(),
        new Cpuid80000025(),
        new Cpuid80000026(),
        new Cpuid80000027(),
        new Cpuid80000028(),
        new Cpuid8FFFFFFF()  };

    private final static ReservedFunctionCpuid[] VENDOR_FUNCTIONS =
    {   new Cpuid20000000(),
        new Cpuid20000001(),
        new Cpuid80860000(),
        new Cpuid80860001(),
        new Cpuid80860002(),
        new Cpuid80860003(),
        new Cpuid80860004(),
        new Cpuid80860005(),
        new Cpuid80860006(),
        new Cpuid80860007(),
        new CpuidC0000000(),
        new CpuidC0000001(),
        new CpuidC0000002(),
        new CpuidC0000003(),
        new CpuidC0000004(),
        new CpuidC0000005(),
        new CpuidC0000006()  };

    private final static int REPLACE_BY_XEN = 1;
    private final static ReservedFunctionCpuid[] VIRTUAL_FUNCTIONS =
    {   new Cpuid40000000(),
        new Cpuid40000001(),   // Start range replaced for Xen.
        new Cpuid40000002(),
        new Cpuid40000003(),
        new Cpuid40000004(),
        new Cpuid40000005(),   // End range replaced for Xen.
        new Cpuid40000006(),
        new Cpuid40000007(),
        new Cpuid40000008(),
        new Cpuid40000009(),
        new Cpuid4000000A(),
        new Cpuid4000000B(),
        new Cpuid4000000C(),
        new Cpuid4000000D(),
        new Cpuid4000000E(),
        new Cpuid4000000F(),
        new Cpuid40000010()  };  // Generic function.
    
    private void restoreDefault()
    {
        ReservedFunctionCpuid[] defaultFunctions =
        {   new Cpuid40000001(),
            new Cpuid40000002(),
            new Cpuid40000003(),
            new Cpuid40000004(),
            new Cpuid40000005()  };
        int n = defaultFunctions.length;
        System.arraycopy
            ( defaultFunctions, 0, VIRTUAL_FUNCTIONS, REPLACE_BY_XEN, n );
    }

    private void replaceByXen( int index )
    {
        ReservedFunctionCpuid[] xenFunctions =
        {   new Cpuid40000X01( index ),
            new Cpuid40000X02( index ),
            new Cpuid40000X03( index ),
            new Cpuid40000X04( index ),
            new Cpuid40000X05( index )  };
        int n = xenFunctions.length;
        System.arraycopy
            ( xenFunctions, 0, VIRTUAL_FUNCTIONS, REPLACE_BY_XEN, n );
    }

    private ReservedFunctionCpuid[] detectedFunctions;
    
    private ReservedFunctionCpuid findFunction( int x )
    {
        ReservedFunctionCpuid f = null;
        if ( detectedFunctions != null )
        {
            int n = detectedFunctions.length;
            for( int i=0; i<n; i++ )
            {
                if ( detectedFunctions[i].getFunction() == x )
                {
                    f = detectedFunctions[i];
                    break;
                }
            }
        }
    return f;    
    }
    
    private void writeMaxLevel( int x, ArrayList<String[]> a )
    {
        ReservedFunctionCpuid f = findFunction( x );
        if ( f != null )
        {
            String[][] s = f.getParametersList();
            if ( ( s != null )&&( s.length >= 1 ) )
            {
                a.add( s[0] );  // Write Maximum CPUID level.
            }
        }
   }

    // Field, initialized by setter (not constructor) for easy reload.
    // private EntryCpuidSubfunction[][] cpuidDump;
    
    // Values, accumulated after parsing entries dump.
    // Note parsing per one logical CPU, its entries dump set before parsing.
    private String accumVendorName = "n/a";
    private String accumCpuName = "n/a";
    private String accumApicId = "n/a";
    private String accumX2ApicId = "n/a";
    private String accumHybrid = "n/a";
    private HybridReturn accumHybridReturn = 
        new HybridReturn( HYBRID_CPU.DEFAULT, "n/a", 1 );
    
    public void earlyVendors( VENDOR_T cpuVendor, HYPERVISOR_T vmmVendor )
    {
        ReservedFunctionCpuid.setCpuVendor( cpuVendor );
        ReservedFunctionCpuid.setVmmVendor( vmmVendor );
    }
    
    public int setCpuidDump( EntryCpuidSubfunction[] e )
    {
        accumVendorName = "n/a";
        accumCpuName = "n/a";
        accumApicId = "n/a";
        accumX2ApicId = "n/a";
        accumHybrid = "n/a";
        accumHybridReturn = 
            new HybridReturn( HYBRID_CPU.DEFAULT, "n/a", 1 );
/*
    Vendor-specific initialization for functions, 
    can replace functions numbers 40000001h ... 40000005h
    or co-exist with other hypervisor, 
    if co-exist, Xen functions starts from 40000X01h, X=index.
    Yet supported only replace mode with index = 0.
*/
        if ( ReservedFunctionCpuid.getVmmVendor() == HYPERVISOR_XEN )
        {   // Yet supported only replace mode with index = 0
            replaceByXen( 0 );
        }
        else
        {   // Restore required after Xen support
            restoreDefault();
        }
/*
    End of vendor-specific initialization
*/
        ArrayList<ReservedFunctionCpuid> allFnc = new ArrayList<>();
        for( ReservedFunctionCpuid item : STANDARD_FUNCTIONS )
        {
            helperCpuidFunction( e, item, allFnc );
        }

        for( ReservedFunctionCpuid item : VIRTUAL_FUNCTIONS )
        {
            helperCpuidFunction( e, item, allFnc );
        }
            
        for( ReservedFunctionCpuid item : EXTENDED_FUNCTIONS )
        {
            helperCpuidFunction( e, item, allFnc );
        }
            
        for( ReservedFunctionCpuid item : VENDOR_FUNCTIONS )
        {
            helperCpuidFunction( e, item, allFnc );
        }
        
        detectedFunctions = allFnc.isEmpty() ? null :
            allFnc.toArray( new ReservedFunctionCpuid[allFnc.size()] );
        return detectedFunctions.length;
    }
    
/*
    Private and default visible helpers.
*/    

    private void helperCpuidFunction( 
            EntryCpuidSubfunction[] e, 
            ReservedFunctionCpuid function,
            ArrayList<ReservedFunctionCpuid> accumFunctions )
    {
        int functionNumber = function.getFunction();
        EntryCpuidSubfunction[] oneEntries = buildEntries( e, functionNumber );
        function.initData( oneEntries, e );
        if ( function.isShow() )
        {
            accumFunctions.add( function );
        }

        if ( function instanceof IVendorName )
        {
            accumVendorName = ((IVendorName) function).getVendorName();
        }

        if ( function instanceof ICpuName )
        {
            accumCpuName = ((ICpuName) function).getCpuName();
        }
        
        if ( function instanceof IApicId )
        {
            accumApicId = ((IApicId) function).getApicId();
        }
        
        if ( function instanceof IX2ApicId )
        {
            accumX2ApicId = ((IX2ApicId) function).getX2ApicId();
        }
        
        if (( function instanceof IHybrid )&&
            ( accumHybridReturn.hybridCpu == HYBRID_CPU.DEFAULT ))
        {
            HybridReturn hybridReturn = ((IHybrid) function).getHybrid();
            accumHybrid = hybridReturn.hybridName;
            
            HYBRID_CPU hc = hybridReturn.hybridCpu;
            String hn = "?";
            if ( null != hc ) switch (hc) 
            {
                case P_CORE:
                    hn = "[P]";
                    break;
                case E_CORE:
                    hn = "[E]";
                    break;
                case LP_E_CORE:
                    hn = "[LP E]";
                    break;
                default:
                    hn = "";
                    break;
            }
            accumHybridReturn = 
                    new HybridReturn( hc, hn, hybridReturn.hybridSmt );
        }
    }

    // This function returns entries array for selected function f.
    static EntryCpuidSubfunction[]
        buildEntries( EntryCpuidSubfunction[] e, int f )
    {
        return helperEntries( e, f, false );
    }

    // Internal helper.
    private static EntryCpuidSubfunction[] 
        helperEntries( EntryCpuidSubfunction[] e, int f, boolean b )
    {
        ArrayList<EntryCpuidSubfunction> a = new ArrayList<>();
        if( e != null )
        {
            for ( EntryCpuidSubfunction entry : e ) 
            {
                if ( b || entry.function == f )
                {
                    a.add( entry );
                }
            }
        }
        return a.isEmpty() ? null :
                a.toArray( new EntryCpuidSubfunction[a.size()] );
    }
    
/*
Public services.
*/
        
    public String getEnumeratorName()
    {
        return " [ " + accumVendorName + " ] [ "  + accumCpuName + " ]";
    }
    
    public String[] getEnumeratorFirstTableUp()
    {
        return new String[] { "Parameter", "Value" };
    }
    
    public String[][] getEnumeratorFirstTable()
    {
        return new String[][] 
        {
            { "Processor vendor" , accumVendorName },
            { "Processor model"  , accumCpuName }
        };
    }
    
    public String[] getEnumeratorSecondTableUp()
    {
        return new String[] { "Parameter", "Value" };
    }
    
    public String[][] getEnumeratorSecondTable()
    {
        return new String[][] {{ "-", "-" }};
    }

    public String[] getProcessorFirstTableUp( int cpuIndex )
    {
        return new String[] { "Parameter", "Value" };
    }
    
    public String[][] getProcessorFirstTable( int cpuIndex )
    {
        return new String[][] 
        {
            { "CPU index at report" , "" + cpuIndex   },
            { "Local APIC ID"       , accumApicId     },
            { "Local x2APIC ID"     , accumX2ApicId   },
            { "Hybrid part"         , accumHybrid     }
        };
    }

    public HybridReturn getProcessorHybrid( int cpuIndex )
    {
        return accumHybridReturn;
    }

    // Support CPUID functions. Note Container.java transit class removed.
    
    public int getDetectedFunctionsCount()
    {
        return detectedFunctions.length;
    }
    
    public boolean getFunctionShow( int fncIndex )
    {
        boolean b = false;
        if( fncIndex < detectedFunctions.length )
        {
            b = detectedFunctions[fncIndex].isShow();
        }
        return b;
    }
    
    public String getFunctionShortName( int fncIndex )
    {
        String s = "?";
        if( fncIndex < detectedFunctions.length )
        {
            s = detectedFunctions[fncIndex].getShortName();
        }
        return s;
    }

    public String getFunctionLongName( int fncIndex )
    {
        String s = "?";
        if( fncIndex < detectedFunctions.length )
        {
            s = detectedFunctions[fncIndex].getLongName();
        }
        return s;
    }
    
    public String[] getFunctionFirstTableUp( int fncIndex )
    {
        String[] s = new String[]{ "?", "?" };
        if( fncIndex < detectedFunctions.length )
        {
            s = detectedFunctions[fncIndex].getParametersListUp();
        }
        return s;
    }
    
    public String[][] getFunctionFirstTable( int fncIndex )
    {
        String[][] s = new String[][]{{ "?", "?" }};
        if( fncIndex < detectedFunctions.length )
        {
            s = detectedFunctions[fncIndex].getParametersList();
        }
        return s;
    }

    public String[] getFunctionSecondTableUp( int fncIndex )
    {
        String[] s = new String[]{ "?", "?" };
        if( fncIndex < detectedFunctions.length )
        {
            s = detectedFunctions[fncIndex].getRegistersDumpUp();
        }
        return s;
    }
    
    public String[][] getFunctionSecondTable( int fncIndex )
    {
        String[][] s = new String[][]{{ "?", "?" }};
        if( fncIndex < detectedFunctions.length )
        {
            s = detectedFunctions[fncIndex].getRegistersDump();
        }
        return s;
    }
    
    // This constants used by CPUID summary info method.
    private final static int BASE_STANDARD_CPUID  = 0x00000000;
    private final static int BASE_EXTENDED_CPUID  = 0x80000000;
    private final static int NAME_STRING_CPUID    = 0x80000002;
    private final static int BASE_VIRTUAL_CPUID   = 0x40000000;
    
    private final static int BASE_VENDOR_CPUID_PHI       = 0x20000000;
    private final static int BASE_VENDOR_CPUID_TRANSMETA = 0x80860000;
    private final static int BASE_VENDOR_CPUID_VIA       = 0xC0000000;
    
    public void appendSummaryCpuidInfo( ArrayList<String[]> a )
    {
        String[][] s; // Scratch pad.
        // Some of this parameters also used later for data base calls.
        String[] physicalVendor = null;
        String[] physicalModel  = null;
        String[] virtualVendor  = null;
        String[] virtualMax     = null;
        // Get and Write CPU model name string.    
        ReservedFunctionCpuid f = findFunction( NAME_STRING_CPUID );
        if ( f != null )
        {
            s = f.getParametersList();
            if ( ( s != null )&&( s.length >= 1 ) )
            {
                physicalModel = s[0];
                a.add( physicalModel );  // Write CPU model name string.
            }
        }
        // Get Virtual CPUID parameters,
        // used later by strings order and for data base calls.
        f = findFunction( BASE_VIRTUAL_CPUID );
        if ( f != null )
        {
            s = f.getParametersList();
            if ( ( s != null )&&( s.length >= 1 ) )
            {
                virtualVendor = s[1];
            }
            if ( ( s != null )&&( s.length >= 2 ) )
            {
                virtualMax = s[0];
            }
        }
        // Get and Write Standard CPUID parameters.
        f = findFunction( BASE_STANDARD_CPUID );
        if ( f != null )
        {
            s = f.getParametersList();
            if ( ( s != null )&&( s.length >= 2 ) )
            {
                physicalVendor = s[1];
                a.add( physicalVendor ); // Write Physical CPU vendor string.
                if ( virtualVendor != null )
                {
                    a.add( virtualVendor ); // Write Virtual CPU vendor string.
                }
                    a.add( s[0] ); // Write Maximum standard CPUID level.
                }
        }
        else
        {  // This for exotic variant: virtual vendor without physical vendor.
            if ( virtualVendor != null )
            {
                a.add( virtualVendor );
            }
        }
        // Get and Write Maximum extended CPUID level to summary report.
        writeMaxLevel( BASE_EXTENDED_CPUID, a );
        // Get and Write Maximum vendor CPUID level to summary report, 
        // for Xeon Phi.
        writeMaxLevel( BASE_VENDOR_CPUID_PHI, a );
        // Get and Write Maximum vendor CPUID level to summary report,
        // for Transmeta.
        writeMaxLevel( BASE_VENDOR_CPUID_TRANSMETA, a );
        // Get and Write Maximum vendor CPUID level to summary report,
        // for VIA.
        writeMaxLevel( BASE_VENDOR_CPUID_VIA, a );
        // Write Maximum virtual CPUID level to summary report.
        if ( virtualMax != null )
        {
            a.add( virtualMax );
        }
    }
}
