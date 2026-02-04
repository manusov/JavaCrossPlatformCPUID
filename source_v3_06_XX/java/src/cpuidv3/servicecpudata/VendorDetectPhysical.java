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

Vendor detector for physical procesors. Plus, data base management methods.

Note. Early processor vendor detection required for:
1) Re-load dump with vendor-specific functions try, note unconditional
   use of vendor-specific functions can cause hardware failures.
2) Select or control vendor-specific leafs of CPUID instruction, note
   vendor type argument required for some parsings and must be valid
   before paesings.
pattern = processor vendor signature.

*/

package cpuidv3.servicecpudata;

import static cpuidv3.servicecpudata.ServiceCpudata.P_SIGN;
import cpuidv3.servicecpudata.ServiceCpudata.VENDOR_T;

class VendorDetectPhysical 
{
    final static int STANDARD_KEY = 0;
    final static int VIRTUAL_KEY  = 0x40000000;
    
//  EntryCpuidSubfunction[][] cpuidDump = null;
    EntryCpuidSubfunction[] oneCpuid = null;
    
    VendorStash stash = null;
    
    private VENDOR_T pVendor = null;
    private String   pSign = null;
    private String   pName = null;
    private Phandler pHandler = null;

    VENDOR_T getPvendor()  { return pVendor;  }
    Phandler getPhandler() { return pHandler; }

//  void setCpuidDump( EntryCpuidSubfunction[][] cpuidDump )
//  {
//      this.cpuidDump = cpuidDump;

    void setCpuidDump( EntryCpuidSubfunction[] oneCpuid )
    {
        this.oneCpuid = oneCpuid;
        stash = null;
        pVendor = null;
        pSign = null;
        pName = null;
        pHandler = null;
    }

    void setStash( VendorStash stash )
    {
        this.stash = stash;
    }

/*
    VENDOR_T earlyCPU( int index )
    {
        VENDOR_T result = null;
        if (( cpuidDump != null )&&( cpuidDump.length > index ))
        {
            VENDOR_T[] pv = VENDOR_T.values();
            String signature = 
                helperSignature( STANDARD_KEY, cpuidDump[index] );
*/            
    VENDOR_T earlyCPU()
    {
        VENDOR_T result = null;
        if ( oneCpuid != null )
        {
            VENDOR_T[] pv = VENDOR_T.values();
            String signature = helperSignature( STANDARD_KEY, oneCpuid );
            for ( VENDOR_T value : pv ) 
            {
                String pattern = P_SIGN[ value.ordinal()][0];
                if ( pattern == null )
                {
                    if ( signature == null )
                    {
                        result = value;
                        break;
                    }
                }
                else if ( pattern.equals( signature ) ) 
                {
                    result = value;
                    break;
                }
            }
        }
        return result;
    }

/*    
    String detectCPU( int index )
    {
        String result = null;
        if (( cpuidDump != null )&&( cpuidDump.length > index ))
        {
            String signature = 
                helperSignature( STANDARD_KEY, cpuidDump[index] );
*/            

    String detectCPU()
    {
        String result = null;
        if ( oneCpuid != null )
        {
            String signature = helperSignature( STANDARD_KEY, oneCpuid );

            VENDOR_T[] pv = VENDOR_T.values();
            for ( int i=0; ( i < pv.length )&&( pSign == null ); i++ )
            {
            switch ( pv[i] )
            {
                case VENDOR_UNKNOWN:
                    pVendor  = pv[i];
                    pSign    = P_SIGN[i][0];
                    pName    = P_SIGN[i][1];
                    pHandler = P_HAND[i];
                    break;
                case VENDOR_INTEL:
                case VENDOR_AMD:
                case VENDOR_CYRIX:
                case VENDOR_VIA:
                case VENDOR_TRANSMETA:
                case VENDOR_UMC:
                case VENDOR_NEXGEN:
                case VENDOR_RISE:
                case VENDOR_SIS:
                case VENDOR_NSC:
                case VENDOR_VORTEX:
                case VENDOR_RDC:
                case VENDOR_HYGON:
                case VENDOR_ZHAOXIN:
                    if ( P_SIGN[i][0].equals( signature ) )
                    {
                        pVendor   = pv[i];
                        pSign     = P_SIGN[i][0];
                        pName     = P_SIGN[i][1];
                        pHandler  = P_HAND[i];
                    }
                    break;
                }
            }
            result = pName;
        }
        return result;
    }
    
    private final Phandler[] P_HAND =
    { 
    new Phandler( null, null, null, null ),
    new Phandler( () -> { return new IntelBrandId(); }, 
                  () -> { return new IntelMicroarchitecture( stash ); }, 
                  () -> { return new IntelSynth( stash ); },
                  null ),
    new Phandler( null, 
                  () -> { return new AmdMicroarchitecture( stash ); }, 
                  () -> { return new AmdSynth( stash ); },
                  () -> { return new AmdModel(); } ),
    new Phandler( null, 
                  () -> { return new CyrixMicroarchitecture( stash ); }, 
                  () -> { return new CyrixSynth( stash ); }, 
                  null ),
    new Phandler( null, 
                  () -> { return new ViaMicroarchitecture( stash ); }, 
                  () -> { return new ViaSynth( stash ); }, 
                  null ),
    new Phandler( null, 
                  () -> { return new TransmetaMicroarchitecture( stash ); }, 
                  () -> { return new TransmetaSynth( stash ); }, 
                  null ),
    new Phandler( null, 
                  () -> { return new UmcMicroarchitecture( stash ); }, 
                  () -> { return new UmcSynth( stash ); }, 
                  null ),
    new Phandler( null, 
                  () -> { return new NexgenMicroarchitecture( stash ); }, 
                  () -> { return new NexgenSynth( stash ); }, 
                  null ),
    new Phandler( null, 
                  () -> { return new RiseMicroarchitecture( stash ); }, 
                  () -> { return new RiseSynth( stash ); }, 
                  null ),
    new Phandler( null, 
                  () -> { return new SisMicroarchitecture( stash ); }, 
                  () -> { return new SisSynth( stash ); }, 
                  null ),
    new Phandler( null, 
                  null, 
                  () -> { return new NscSynth( stash ); }, 
                  null ),
    new Phandler( null, 
                  null, 
                  () -> { return new VortexSynth( stash ); }, 
                  null ),
    new Phandler( null, 
                  null, 
                  () -> { return new RdcSynth( stash ); }, 
                  null ),
    new Phandler( null, 
                  () -> { return new HygonMicroarchitecture( stash ); }, 
                  () -> { return new HygonSynth( stash ); }, 
                  null ),
    new Phandler( null, 
                  () -> { return new ZhaoxinMicroarchitecture( stash ); }, 
                  () -> { return new ZhaoxinSynth( stash ); }, 
                  null )
    };
    
    // Helper for extract CPU and VMM vendor strings from binary dump.
    // Possible optimization, see also ReservedFunctionCpuid.java,
    // same functionality duplication.
    final String helperSignature( int key, EntryCpuidSubfunction[] entries )
    {
        boolean b = false;
        StringBuilder sb = new StringBuilder( "" );
        if ( entries != null )
        {
            for ( EntryCpuidSubfunction entry : entries) 
            {
                if ( entry.function == key ) 
                {
                    int[] signature;
                    // For virtual function 40000000h order is EBX-ECX-EDX.
                    if ( key == VIRTUAL_KEY ) 
                    {
                        signature = 
                            new int[]{ entry.ebx, entry.ecx, entry.edx };
                    }
                    // For functions 00000000h, 80000000h order is EBX-EDX-ECX.
                    else 
                    {
                        signature = 
                            new int[]{ entry.ebx, entry.edx, entry.ecx };
                    }
                    // Cycle for convert 3 integer numbers to 12-char string.
                    for( int j=0; j<3; j++ )
                    {
                        int d = signature[j];
                        // Cycle convert int to 4 chars.
                        for( int k=0; k<4; k++ )
                        {
                            char c = (char)( d & 0xFF );
                            if ( c != 0 )
                            {
                                if ( ( c < ' ' )||( c > '}' ) )
                                {
                                    c = '.'; 
                                }
                                sb.append( c );  // Build string.
                                b = true;        // Set result validity flag.
                            }
                            d = d >>> 8;
                        }
                    }
                }
            }
        }
        return b ? sb.toString() : null;
    }
}
