/* 
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
MODEL template class for build sub-applications
with MVC ( Model, View, Controller ) structure.
*/

package cpuidrefactoring.rootmenu;

import cpuidrefactoring.system.Device;
import cpuidrefactoring.system.PAL;

public class ApplicationModel 
{
private PAL pal;

protected ViewSet viewSet;
protected Device device;
protected long[] ipb, opb;

public int getCount()                    { return 0;     }
public ViewSet getSelectedModel( int i ) { return null;  }

public long[] getBinary()                { return null;  }
public boolean setBinary( long[] x )     { return false; }
public boolean redetectPlatform( )       { return false; }

public final PAL getPal()                { return pal;   }
public final void setPal( PAL x )        { pal = x;      }
}
