/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Sub-application class for show Operating System (OS) information.
Based on MVC ( Model, View, Controller ) pattern.
Contains Controller, supports interface with data model and GUI view.
*/

package cpuidrefactoring.applications;

import cpuidrefactoring.About;
import cpuidrefactoring.CpuidRefactoring;
import cpuidrefactoring.rootmenu.*;
import cpuidrefactoring.system.Registry;

public class ApplicationOsInfo extends ApplicationController
{
public ApplicationOsInfo()
    {
    BuildModel model = new BuildModel();
    BuildView view = new BuildView
        ( model, About.getX2size(), About.getY2size() );
    setModel( model );
    setView( view );
    }
    
private class BuildModel extends ApplicationModel
    {
    private final static String NAME = "OS environment";
    private BuildModel()
        {
        Registry r = CpuidRefactoring.getRegistry();
        device = r.loadDriver( Registry.CPR.DRIVER_OSINFO );
        String[][] s1 = device.getScreensListsUp();
        String[][][] s2 = device.getScreensLists();
        if ( ( s1 != null ) & ( s2 != null ) )
            {
            String[] s3 = s1[0];
            String[][] s4 = s2[0];
            ChangeableTableModel m1 = new ChangeableTableModel( s3, s4 );
            viewSet = new ViewSetSingleTable( NAME, m1 );
            }
        else
            {
            viewSet = null;
            }
        }
    @Override public int getCount() { return 1; }
    @Override public ViewSet getSelectedModel( int i ) { return viewSet; }
    }

private class BuildView extends ApplicationView
    {
    private final String[] BUTTONS_NAMES = { "Report this", "About", "Cancel" };
    private final String[] BUTTONS_TIPS =
        { "Save Operating System information in the text report", null, null };
    private final int[] BUTTONS_KEYS = { 'R', 'A', 'C' }; 
    private BuildView( BuildModel model , int x, int y )
        {
        helperSimpleBuildView
            ( model, x, y, BUTTONS_NAMES, BUTTONS_TIPS, BUTTONS_KEYS, false );
        }
    }
}
