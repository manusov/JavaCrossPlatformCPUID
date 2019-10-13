/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Sub-application class for show CPU Clock ( TSC, time stamp counter )
information, it read by native layer.
Based on MVC ( Model, View, Controller ) pattern.
Contains Controller, supports interface with data model and GUI view.
*/

package cpuidrefactoring.applications;

import cpuidrefactoring.About;
import cpuidrefactoring.CpuidRefactoring;
import cpuidrefactoring.rootmenu.*;
import cpuidrefactoring.system.Registry;

public class ApplicationCpuClk extends ApplicationController
{
public ApplicationCpuClk()
    {
    BuildModel model = new BuildModel();
    BuildView view = new BuildView
        ( model, About.getX2size(), About.getY2size() );
    setModel( model );
    setView( view );
    }

private class BuildModel extends ApplicationModel
    {
    private final static String NAME = "CPU Clocks";
    private final int OPB_SIZE = 4096;  // IPB not used for this functionality
    private final static int FUNCTION_CLOCK = 1;
    private BuildModel()
        {
        // initializing arrays for communication with native layer
        opb = new long[ OPB_SIZE ];  // no clear, use fact: all elements = 0
        // get registry
        Registry r = CpuidRefactoring.getRegistry();
        // Call function 1 = Measure CPU (yet TSC only) frequency
        // IPB = null, this means third parameter = function code = 2
        if ( ( r.binaryGate( null, opb, FUNCTION_CLOCK, OPB_SIZE ) ) == 0 )
            {
            opb[0]=0;  // clear result if invalid
            opb[1]=0;
            }
        // Load driver
        device = r.loadDriver( Registry.CPR.DRIVER_CPUCLK );
        // Send binary data (measured frequency) from hardware to data decoder
        device.setBinary( opb );
        // Get model strings for build table
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
        { "Save processor clock information in the text report", null, null };
    private final int[] BUTTONS_KEYS = { 'R', 'A', 'C' }; 
    private BuildView( BuildModel model , int x, int y )
        {
        helperSimpleBuildView
            ( model, x, y, BUTTONS_NAMES, BUTTONS_TIPS, BUTTONS_KEYS, true );
        }
    }
}
