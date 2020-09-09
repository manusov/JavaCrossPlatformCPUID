/* 
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
CONTROLLER template class for build sub-applications
with MVC ( Model, View, Controller ) structure.
*/

package cpuidrefactoring.rootmenu;

public class ApplicationController 
{
private ApplicationModel model;
private ApplicationView view;

public final ApplicationModel getModel() { return model; }
public final ApplicationView getView()   { return view;  }

public final void setModel( ApplicationModel m ) { model = m; }
public final void setView ( ApplicationView v  ) { view = v;  }
}
