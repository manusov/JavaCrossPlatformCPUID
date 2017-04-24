//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// Static library: Spring Layout Helper methods

package arch1.applications.guipanels;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

public class SetupLayout 
{
public static void springCenter ( SpringLayout s, JComponent p, JComponent c, 
                                   int up, int down, int left, int right )
    {
    s.putConstraint ( SpringLayout.NORTH, c, up,    SpringLayout.NORTH, p );
    s.putConstraint ( SpringLayout.SOUTH, c, down,  SpringLayout.SOUTH, p );
    s.putConstraint ( SpringLayout.WEST,  c, left,  SpringLayout.WEST,  p );
    s.putConstraint ( SpringLayout.EAST,  c, right, SpringLayout.EAST,  p );
    }

// overloaded method
public static void springCenter ( SpringLayout s, JPanel p, JComponent c, 
                                   int up, int left )
    {
    s.putConstraint ( SpringLayout.NORTH, c, up,    SpringLayout.NORTH, p );
    s.putConstraint ( SpringLayout.WEST,  c, left,  SpringLayout.WEST,  p );
    }

// overloaded method
public static void springCenter ( SpringLayout s, 
                                   JComponent p1, JComponent p2, JComponent c, 
                                   int up, int down, int left, int right      )
    {
    s.putConstraint ( SpringLayout.NORTH, c, up,    SpringLayout.SOUTH, p2 );
    s.putConstraint ( SpringLayout.SOUTH, c, down,  SpringLayout.SOUTH, p1 );
    s.putConstraint ( SpringLayout.WEST,  c, left,  SpringLayout.WEST,  p1 );
    s.putConstraint ( SpringLayout.EAST,  c, right, SpringLayout.EAST,  p1 );
    }

public static void springSouth ( SpringLayout s, JPanel p, 
                                  JComponent c, int down )
    {
    s.putConstraint ( SpringLayout.SOUTH, c, down, SpringLayout.SOUTH, p );
    }

public static void springWest ( SpringLayout s, JPanel p, 
                                 JComponent c, int left )
    {
    s.putConstraint ( SpringLayout.WEST, c, left, SpringLayout.WEST, p );
    }

public static void springWestEast
    ( SpringLayout s, JComponent c1, JComponent c2, int inv )
    {
    s.putConstraint ( SpringLayout.WEST, c2, inv, SpringLayout.EAST, c1 );
    }

public static void springEast
    ( SpringLayout s, JComponent c1, JComponent c2, int inv )
    {
    s.putConstraint ( SpringLayout.EAST, c2, inv, SpringLayout.EAST, c1 );
    }

public static void springEastWest
    ( SpringLayout s, JComponent c1, JComponent c2, int inv )
    {
    s.putConstraint ( SpringLayout.EAST, c2, inv, SpringLayout.WEST, c1 );
    }

    
}
