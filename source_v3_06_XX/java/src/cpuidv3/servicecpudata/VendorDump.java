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

Extract selected information from CPUID dump.
Used CPUID (sub)functions entries array.

*/

package cpuidv3.servicecpudata;

import java.util.ArrayList;

class VendorDump 
{
    private final static int BASE_STANDARD_CPUID  = 0x00000000;
    private final static int BASE_EXTENDED_CPUID  = 0x80000000;

    private final static int BASE_VENDOR_CPUID_PHI       = 0x20000000;
    private final static int BASE_VENDOR_CPUID_TRANSMETA = 0x80860000;
    private final static int BASE_VENDOR_CPUID_VIA       = 0xC0000000;

    private final static int BASE_VIRTUAL_CPUID   = 0x40000000;
    private final static int NAME_STRING_CPUID    = 0x80000002;

    private final static int TFMS_AND_BRAND_CPUID = 0x00000001;
    private final static int EXTENDED_TFMS        = 0x80000001;
    private final static int CORE_PHYSICAL        = 0x80000008;

    private final static int CACHE_DESCRIPTORS    = 0x00000002;
    private final static int CACHE_DETERMINISTIC  = 0x00000004;
    private final static int CACHE_AMD            = 0x80000006;
    private final static int EXTENDED_TOPOLOGY    = 0x0000000B;
    private final static int V2_EXTENDED_TOPOLOGY = 0x0000001F;
    private final static int AMD_MP_TOPOLOGY      = 0x8000001E;

    private final static int TRANSMETA_INFO       = 0x80860001;

    private final static int INTEL_HYBRID_CHECK   = 0x00000007;
    private final static int INTEL_HYBRID_BIT     = 15;

    private final static int INTEL_HYBRID_TYPE    = 0x0000001A;
    private final static int HYBRID_BIG           = 0x40;
    private final static int HYBRID_SMALL         = 0x20;
    private final static int HYBRID_ZERO          = 0;

/*    
    void extractFromDump( VendorStash stash, 
            EntryCpuidSubfunction[][] multiEntries, int index )
    {
        if (( stash != null )&&( multiEntries != null )&&
            ( multiEntries.length > index ))
        {
            EntryCpuidSubfunction[] entries = multiEntries[index];
*/
    void extractFromDump( VendorStash stash, 
            EntryCpuidSubfunction[] singleEntries )
    {
    // Index yet removed.

        if (( stash != null )&&( singleEntries != null ))
        {
            EntryCpuidSubfunction[] entries = singleEntries;
            // Index yet removed.

            // Load from dump to stash: base standard CPUID.
            EntryCpuidSubfunction[] e = 
                buildEntries( entries, BASE_STANDARD_CPUID );
            if ( ( e != null )&&( e.length > 0 ) )
            {
                stash.val_0_eax = e[0].eax;
            }
            
            // Load from dump to stash:
            // type, family, model, stepping, brand index.
            e = buildEntries( entries, TFMS_AND_BRAND_CPUID );
            if ( ( e != null )&&( e.length >= 1 ) )
            {
                stash.val_1_eax = e[0].eax;
                stash.val_1_ebx = e[0].ebx;
                stash.val_1_ecx = e[0].ecx;
                stash.val_1_edx = e[0].edx;
            }
        
            // Load from dump to stash: extended TFMS.
            e = buildEntries( entries, EXTENDED_TFMS );
            if ( ( e != null )&&( e.length >= 1 ) )
            {
                stash.val_80000001_eax = e[0].eax;
                stash.val_80000001_ebx = e[0].ebx;
                stash.val_80000001_ecx = e[0].ecx;
                stash.val_80000001_edx = e[0].edx;
            }
        
            // Load from dump to stash: core physical parameters.
            e = buildEntries( entries, CORE_PHYSICAL );
            if ( ( e != null )&&( e.length >= 1 ) )
            {
                stash.val_80000008_ecx = e[0].ecx;
            }
        
            // Load from dump to stash: cache descriptors data by function 02h.
            e = buildEntries( entries, CACHE_DESCRIPTORS );
            if ( ( e != null )&&( e.length >= 1 ) )
            {
                stash.val_2_eax = e[0].eax;
                stash.val_2_ebx = e[0].ebx;
                stash.val_2_ecx = e[0].ecx;
                stash.val_2_edx = e[0].edx;
            }
        
            // Load from dump to stash: cache and MP topology by function 04h.
            e = buildEntries( entries, CACHE_DETERMINISTIC );
            if ( ( e != null )&&( e.length >= 1 ) )
            {
                if ( e[0].eax != 0 )
                {
                    stash.val_4_eax = e[0].eax;
                    stash.saw_4 = true;
                }
            }
        
            // Load from dump to stash: L2 cache by function 80000006h.
            e = buildEntries( entries, CACHE_AMD );
            if ( ( e != null )&&( e.length >= 1 ) )
            {
                stash.val_80000006_ecx = e[0].ecx;
            }

            // Load from dump to stash: cache and MP topology by function 0Bh
            e = buildEntries( entries, EXTENDED_TOPOLOGY );
            if ( ( e != null )&&( e.length >= 1 ) )
            {
            for( int i=0; i<e.length; i++ )
                {
                    if ( i < stash.val_b_eax.length )
                    {
                        stash.val_b_eax[i] = e[i].eax;
                        stash.saw_b = true;
                    }
                    if ( i < stash.val_b_ebx.length )
                    {
                        stash.val_b_ebx[i] = e[i].ebx;
                        stash.saw_b = true;
                    }
                }
            }
            
            // Load from dump to stash: cache and MP topology by function 1Fh.
            e = buildEntries( entries, V2_EXTENDED_TOPOLOGY );
            if ( ( e != null )&&( e.length >= 1 ) )
            {
                for( int i=0; i<e.length; i++ )
                {
                    if ( i < stash.val_1f_eax.length )
                    {
                        stash.val_1f_eax[i] = e[i].eax;
                        stash.saw_1f = true;
                    }
                    if ( i < stash.val_1f_ebx.length )
                    {
                        stash.val_1f_ebx[i] = e[i].ebx;
                        stash.saw_1f = true;
                    }
                    if ( i < stash.val_1f_ecx.length )
                    {
                        stash.val_1f_ecx[i] = e[i].ecx;
                        stash.saw_1f = true;
                    }
                }
            }

            // Load from dump to stash: AMD MP topology.
            e = buildEntries( entries, AMD_MP_TOPOLOGY );
            if ( ( e != null )&&( e.length >= 1 ) )
            {
                stash.val_8000001e_ebx = e[0].ebx;
            }

            // Load from dump to stash: Transmeta processor info.
            e = buildEntries( entries, TRANSMETA_INFO );
            if ( ( e != null )&&( e.length >= 1 ) )
            {
                stash.transmeta_proc_rev = e[0].ebx;
            }
        
            // Intel Hybrid CPU support: check hybrid technology flag.
            e = buildEntries( entries, INTEL_HYBRID_CHECK );
            if ( ( e != null )&&( e.length >= 1 ) )
            {
                stash.hybridCheck = 
                    ( ( e[0].edx & ( 1 << INTEL_HYBRID_BIT ) ) != 0);
            }        
        
            // Intel Hybrid CPU support: get core type, even if check = false.
            e = buildEntries( entries, INTEL_HYBRID_TYPE );
            if ( ( e != null )&&( e.length >= 1 ) )
            {
                int hybridId = e[0].eax >> 24;
                switch ( hybridId ) 
                {
                    case HYBRID_BIG:
                        stash.bigCore = true;
                        break;
                    case HYBRID_SMALL:
                        stash.smallCore = true;
                        break;
                    case HYBRID_ZERO:
                        stash.zeroCore = true;
                        break;
                    default:
                        break;
                }
            }
            
            // Set model string at stash, 
            // used for parsing and keywords detection.
            stash.brand = getCpuName( entries );
        }
    }
    
    // This for selected function f.
    private EntryCpuidSubfunction[] 
        buildEntries( EntryCpuidSubfunction[] entries, int f )
    {
        return helperEntries( entries, f, false );
    }

    // This for all detected functions.
    private EntryCpuidSubfunction[] 
        buildEntries( EntryCpuidSubfunction[] entries )
    {
        return helperEntries( entries, 0, true );
    }
    
    private EntryCpuidSubfunction[] 
        helperEntries( EntryCpuidSubfunction[] entries, int f, boolean b )
    {
        ArrayList<EntryCpuidSubfunction> a = new ArrayList<>();
        if( entries != null )
        {
            for ( EntryCpuidSubfunction entry : entries ) 
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
        
    private String getCpuName( EntryCpuidSubfunction[] entries )
    {
        String result;
        EntryCpuidSubfunction[] e1 = 
                buildEntries( entries, NAME_STRING_CPUID );
        EntryCpuidSubfunction[] e2 = 
                buildEntries( entries, NAME_STRING_CPUID + 1 );
        EntryCpuidSubfunction[] e3 = 
                buildEntries( entries, NAME_STRING_CPUID + 2 );
        
        if ( ( e1 != null )&&( e2 != null )&&( e3 != null )&&
             ( e1.length == 1 )&&( e2.length == 1)&&( e3.length == 1 ) )
        {
            EntryCpuidSubfunction[] functions = 
                new EntryCpuidSubfunction[] { e1[0], e2[0], e3[0] };
            int[] data = new int[12];
            for( int i=0; i<3; i++ )
            {
                data[i*4]   = functions[i].eax;
                data[i*4+1] = functions[i].ebx;
                data[i*4+2] = functions[i].ecx;
                data[i*4+3] = functions[i].edx;
            }
            StringBuilder sb = new StringBuilder( "" );
            for( int i=0; i<12; i++ )
            {
                int d = data[i];
                for( int j=0; j<4; j++ )
                {
                    char c = (char)( d & 0xFF );
                    if ( c != 0 )
                    {
                        if ( ( c < ' ' )||( c > '}' ) ) c = '.';
                        sb.append( c );
                    }
                    d = d >>> 8;
                }
            }
            result = sb.toString().trim().replaceAll( "\\s+", " " );
        }
        else
        {
            result = "?";
        }
        return result;
    }
}
