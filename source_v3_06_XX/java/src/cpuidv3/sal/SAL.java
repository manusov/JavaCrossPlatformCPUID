/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

SAL = Service Abstraction Layer. Parent abstract class.

This class inherited by SALHW.java (HW=Hardware) for
"Physical platform with dump loader" application build.

This class inherited by SALDL.java (DL=Dump loader) for 
"Dump loader only" application build.

*/

package cpuidv3.sal;

import cpuidv3.pal.PAL;
import cpuidv3.servicemp.ServiceMp;
import java.util.ArrayList;

public class SAL 
{
    public enum SERVICE_ID
    {
        CPUID_PHYSICAL,
        CPUID_FILE,
        TOPOLOGY,
        CLOCKS,
        CONTEXT,
        OS_INFO,
        JVM_INFO,
        UNKNOWN
    }

    public boolean getDumpLoaderBuild()  { return true; }
    
    PAL getPal()                         { return null; }
    public PAL.PAL_STATUS getPalStatus() { return null; }
    public String getRuntimeName()       { return "";   }
    
    Service getService( SERVICE_ID id )  { return null; }

    // Public methods called from GUI depend on user actions.
    // Methods for support tools - dialogue boxes.

    boolean overrideByFile = false;
    public void restartOverride( boolean overrideMode )
    {
        overrideByFile = overrideMode;
    }

    public boolean internalLoadAllBinaryData()             { return false; }
    public boolean internalLoadNonAffinizedCpuid()         { return false; }
    public void clearAllBinaryData()                       {               }
    public long[][] getCpuidBinaryData()                   { return null;  }
    public boolean setCpuidBinaryData( long[][] data )     { return false; }
    public boolean setCpuidBinaryData( long[] loadedData ) { return false; }

    public boolean setCpuidBinaryData( int[] loadedData )
    {
        boolean status = false;
        if ( ( loadedData != null )&&( loadedData.length > 0 )&&
                ( ( loadedData.length % 5 ) == 0) )
        {
            ArrayList<Long[]> a = new ArrayList<>();
            ArrayList<Long> b = new ArrayList<>();
            int subfunction = 0;
            int previous = -1;
            for( int i=0; i<loadedData.length; i+=5 )
            {
                int function = loadedData[i + 0];
                if(( function != previous )||( function == 0 ))
                {
                    previous = function;
                    subfunction = 0;
                }
                long f = function;
                long s = subfunction++;
                long eax = loadedData[i + 1];
                long ebx = loadedData[i + 2];
                long ecx = loadedData[i + 3];
                long edx = loadedData[i + 4];
                b.add( f << 32 );
                b.add( s & 0xFFFFFFFFL );
                b.add( ( ebx << 32 ) + ( eax & 0xFFFFFFFFL ) );
                b.add( ( edx << 32 ) + ( ecx & 0xFFFFFFFFL ) );
                if (( i == loadedData.length - 5 )||( loadedData[i + 5] == 0 ))
                {
                    Long[] longs = b.toArray( new Long[ b.size() ] );
                    a.add( longs );
                    b.clear();
                }
            }
            
            if ( a.size() > 0 )
            {
                status = true;
                long[][] data = new long[ a.size() ][];
                for( int i=0; i<a.size(); i++ )
                {
                    Long[] longs = a.get( i );
                    data[i] = new long[ longs.length ];
                    if ( longs.length == 0 )
                    {
                        status = false;
                        break;
                    }
                    for( int j=0; j<longs.length; j++ )
                    {
                        data[i][j] = longs[j];
                    }
                }
                if ( status )
                {
                    Service service = getService( SERVICE_ID.CPUID_FILE );
                    if ( service != null )
                    {
                        service.setBinaryData( data );
                    }
                    else
                    {
                        status = false;
                    }
                }
            }
        }
        return status;
    }

    private final HelperSystemInfo helperSystemInfo = 
            new HelperSystemInfo( this );
    // This 3 fields required for SAL.java class childs,
    // can't be moved to HelperSystemInfo class. This 3 fields
    // updated by HelperSystemInfo class methods, by sal reference.
    ServiceMp serviceMp = null;
    String serviceMpName = null;
    boolean serviceMpStatus = false;
    
    public ChangeableTableModel getSummaryTable()
    {
        return helperSystemInfo.getSummaryTable();
    }
    
    public TreeScreenModel getDetailsTree()
    {
        return helperSystemInfo.getDetailsTree();
    }
    
    public TreeScreenModel getSmpTree() { return null; }
    
    public ChangeableTableModel getDumpTable()            
    {
        SERVICE_ID id;
        if ( overrideByFile )
        {
            id = SERVICE_ID.CPUID_FILE;
        }
        else
        {
            id = SERVICE_ID.CPUID_PHYSICAL;
        }
        Service service = getService( id );
        return new ChangeableTableModel
            ( service.getTableUp(), service.getTableData() );
    }
    
    public ChangeableTableModel getClockTable()            { return null;  }
    public ChangeableTableModel getContextTable()          { return null;  }
    public ChangeableTableModel getOsTable()               { return null;  }
    public ChangeableTableModel getJvmTable()              { return null;  }
    public void consoleSummary()                           {               }
}
