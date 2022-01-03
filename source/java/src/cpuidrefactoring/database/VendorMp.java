/*
CPUID Utility. (C)2022 IC Book Labs
------------------------------------
This file contains Processors and Hypervisors
data exported from Todd Allen CPUID project.
Some variables and functions names not compliant with java
naming conventions, this fields using original C/C++ naming.
-----------------------------------------------
Processor multicore/multithread parameters detector.
*/

package cpuidrefactoring.database;

import static cpuidrefactoring.database.DefineArithmetic.*;
import static cpuidrefactoring.database.VendorDetectPhysical.VENDOR_T.*;

class VendorMp 
{

private int Synth_Family( int value )
    {
    return BIT_EXTRACT_LE( value, 20, 28 ) + 
           BIT_EXTRACT_LE( value, 8, 12 );
    }

private int Synth_Model( int value )
    {
    return ( BIT_EXTRACT_LE( value, 16, 20 ) << 4 ) + 
             BIT_EXTRACT_LE( value, 4, 8 );
    }
        
private int GET_ApicIdCoreIdSize( int val_80000008_ecx )
    {
    // return BIT_EXTRACT_LE( ( val_80000008_ecx ), 0, 4 );  // original bug ?
    return BIT_EXTRACT_LE( ( val_80000008_ecx ), 12, 16 );
    }

private int GET_LogicalProcessorCount( int val_1_ebx ) 
    {
    return BIT_EXTRACT_LE( ( val_1_ebx ), 16, 24 );
    }

private int IS_HTT( int val_1_edx )
    {
    return BIT_EXTRACT_LE( ( val_1_edx ), 28, 29 );
    }
   
private int IS_CmpLegacy( int val_80000001_ecx )
    {
    return BIT_EXTRACT_LE( ( val_80000001_ecx ), 1, 2 );
    }

private int GET_NC_INTEL( int val_4_eax )
    {
    return BIT_EXTRACT_LE( ( val_4_eax ), 26, 32 );
    }

private int GET_NC_AMD( int val_80000008_ecx )
    {
    return BIT_EXTRACT_LE( ( val_80000008_ecx ), 0, 8 );
    }
   
private int GET_X2APIC_PROCESSORS( int val_b_ebx )
    {
    return BIT_EXTRACT_LE( ( val_b_ebx ), 0, 16 );
    }
   
private int GET_V2_TOPO_LEVEL( int val_1f_ecx )
    {
    return BIT_EXTRACT_LE( ( val_1f_ecx ), 8, 16 );
    }
   
private int GET_V2_TOPO_PROCESSORS( int val_1f_ebx )
    {
    return BIT_EXTRACT_LE( ( val_1f_ebx ), 0, 16 );
    }
   
private int GET_CoresPerComputeUnit_AMD( int val_8000001e_ebx )
    {
    return BIT_EXTRACT_LE( ( val_8000001e_ebx ), 8, 16 );
    }

private final int V2_TOPO_SMT  = 1;
private final int V2_TOPO_CORE = 2;
    
void decodeMp( DatabaseStash stash )
    {
    switch ( stash.vendor ) 
        {

/*
** Logic derived from information in:
**    Detecting Multi-Core Processor Topology in an IA-32 Platform
**    by Khang Nguyen and Shihjong Kuo
** and:
**    Intel 64 Architecture Processor Topology Enumeration (Whitepaper)
**    by Shih Kuo
** Extension to the 0x1f leaf was obvious.
*/
        case VENDOR_INTEL:

            if ( stash.saw_1f )
                {
                stash.mp.method = "Intel leaf 01Fh";
                int  tryX, ht = 0, tc = 0;
                for ( tryX = 0; tryX < stash.val_1f_ecx.length; tryX++ )
                    {
                    if ( GET_V2_TOPO_LEVEL( stash.val_1f_ecx[tryX] ) ==
                         V2_TOPO_SMT) 
                        {
                        ht = GET_V2_TOPO_PROCESSORS( stash.val_1f_ebx[tryX] );
                        }
                    else if( GET_V2_TOPO_LEVEL( stash.val_1f_ecx[tryX]) ==
                             V2_TOPO_CORE ) 
                        {
                        tc = GET_V2_TOPO_PROCESSORS( stash.val_1f_ebx[tryX] );
                        }
                    }
                if ( ht == 0 )
                    {
                    ht = 1;
                    }
                stash.mp.cores = tc / ht;
                stash.mp.hyperthreads = ht;
                }
            else if ( stash.saw_b ) 
                {
                stash.mp.method = "Intel leaf 0Bh";
                int ht = GET_X2APIC_PROCESSORS( stash.val_b_ebx[0] );
                int tc = GET_X2APIC_PROCESSORS( stash.val_b_ebx[1] );
                if ( ht == 0 )
                    {
                    ht = 1;
                    }
                stash.mp.cores = tc / ht;
                stash.mp.hyperthreads = ht;
                }
            else if ( stash.saw_4 )
                {
                int tc = GET_LogicalProcessorCount( stash.val_1_ebx );
                int  c;
                if ( ( stash.val_4_eax & 0x1f ) != 0) 
                    {
                    c = GET_NC_INTEL( stash.val_4_eax ) + 1;
                    stash.mp.method = "Intel leaf 01h, 04h";
                    }
                else
                    {
/* Workaround for older 'cpuid -r' dumps with incomplete 4 data */
                    c = tc / 2;
                    stash.mp.method = "Intel leaf 01h, 04h (zero fallback)";
                    }
                stash.mp.cores = c;
                stash.mp.hyperthreads = tc / c;
                }
            else
                {
                stash.mp.method = "Intel leaf 01h";
                stash.mp.cores  = 1;
                if ( IS_HTT( stash.val_1_edx ) != 0 )
                    {
                    int tc = GET_LogicalProcessorCount( stash.val_1_ebx );
//                  stash.mp.hyperthreads = tc >= 2 ? tc : 2;
                    stash.mp.hyperthreads = ( tc > 1 ) ? 2 : 1;
                    }
                else
                    {
                    stash.mp.hyperthreads = 1;
                    }
                }
            break;
            
/*
** Logic deduced by analogy: As Intel's decode_mp_synth code is to AMD's
** decode_mp_synth code, so is Intel's APIC synth code to this.
**
** For Families 10h-16h, the CU (CMT "compute unit") logic was a
** logical extension.
**
** For Families 17h and later, terminology changed to reflect that
** the Family 10h-16h cores had been sharing resources significantly,
** similarly to (but less drastically than) SMT threads:
**    Family 10h-16h => Family 17h
**    ----------------------------
**    CU             => core   
**    core           => thread
** And leaf 0x8000001e/ebx is used for smt_count, because 1/ebx is
** unreliable.
*/
        case VENDOR_AMD:
        case VENDOR_HYGON:

            int htt = IS_HTT( stash.val_1_edx );
            int size = GET_ApicIdCoreIdSize( stash.val_80000008_ecx );
            int mask = ( 1 << size ) - 1;
            int core_count = 
                ( ( GET_NC_AMD( stash.val_80000008_ecx ) & mask ) + 1 );
            int smt_count =
                ( GET_LogicalProcessorCount( stash.val_1_ebx ) / core_count);
            int cu_count = 1;
            
            if ( ( GET_CoresPerComputeUnit_AMD( stash.val_8000001e_ebx ) != 0 ) 
                   && ( htt != 0 ) )
                {
                if ( Synth_Family( stash.val_80000001_eax ) > 0x16 ) 
                    {
                    int threads_per_core = GET_CoresPerComputeUnit_AMD
                        ( stash.val_8000001e_ebx ) + 1;
                    smt_count = threads_per_core;
                    core_count /= threads_per_core;
                    }
                else
                    {
                    int cores_per_cu = GET_CoresPerComputeUnit_AMD
                        ( stash.val_8000001e_ebx ) + 1;
                    cu_count   = core_count / cores_per_cu;
                    core_count = cores_per_cu;
                    }
                stash.mp.method = "AMD leafs 80000008h, 8000001Eh";
                stash.mp.cores = core_count;
                stash.mp.hyperthreads = smt_count;
                stash.mp.units = cu_count;
                }
            
/*
** Logic from:
**    AMD CPUID Specification (25481 Rev. 2.16),
**    3. LogicalProcessorCount, CmpLegacy, HTT, and NC
**    AMD CPUID Specification (25481 Rev. 2.28),
**    3. Multiple Core Calculation
*/
            else if ( htt != 0 )
                {
                int  tc = GET_LogicalProcessorCount( stash.val_1_ebx );
                int  c;
                if ( GET_ApicIdCoreIdSize( stash.val_80000008_ecx ) != 0 )
                    {
                    // int size = GET_ApicIdCoreIdSize( stash.val_80000008_ecx );
                    // int  mask = ( 1 << size ) - 1;
                    c = ( GET_NC_AMD( stash.val_80000008_ecx ) & mask ) + 1;
                    }
                else 
                    {
                    c = GET_NC_AMD( stash.val_80000008_ecx ) + 1;
                    }
                if ( ( tc == c ) == ( IS_CmpLegacy( stash.val_80000001_ecx ) != 0 ) )
                    {
                    stash.mp.method = ( stash.vendor == VENDOR_AMD ?
                        "AMD" : "Hygon");
                    if ( c > 1 )
                        {
                        stash.mp.cores        = c;
                        stash.mp.hyperthreads = tc / c;
                        }
                    else 
                        {
                        stash.mp.cores        = 1;
                        stash.mp.hyperthreads = ( tc >= 2 ? tc : 2 );
                        }
                    }
                else 
                    {
/* 
** Rev 2.28 leaves out mention that this case is nonsensical, but
** I'm leaving it in here as an "unknown" case.
*/
                    }
                }
            else 
                {
                stash.mp.method = 
                    ( stash.vendor == VENDOR_AMD ? "AMD" : "Hygon" );
                stash.mp.cores        = 1;
                stash.mp.hyperthreads = 1;
                }
            break;

/*
Branch without vendor-specific MP-topology support.
*/
        default:
            
            if ( IS_HTT( stash.val_1_edx ) == 0 ) 
                {
                stash.mp.method = "Generic leaf 1 no multi-threading";
                stash.mp.cores        = 1;
                stash.mp.hyperthreads = 1;
                }
            break;
        }
    }
}