/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Handler for "Redetect affinized" item at root menu.
Returns JDialog GUI object for show "Select logical CPU" window.

*/

package cpuidv3.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

final class CpuSelector 
{
    private final static int BOX_X = 340;
    private final static int BOX_Y = 130;
    private final static int COMBO_X = 260;
    private final static int COMBO_Y = 24;
    private final static int BUTTONS_X = 85;
    private final static int BUTTONS_Y = 24;
    private final static int X1 = ( BOX_X - COMBO_X ) / 2;
    
    private final static String MENU_NAF = "Non affinized (%d-%d)";
    private final static String MENU_CPU = "Logical CPU # %d";
    private final String[] cpuNames;
    
    private final int cpuCount;
    private int cpuSelected;
    private boolean affEnabled;
    private boolean makeSelection;
    
    int getCpuSelected()       { return cpuSelected;   }
    boolean getAffEnabled()    { return affEnabled;    }
    boolean getMakeSelection() { return makeSelection; }
    
    CpuSelector( int count )
    {
        cpuCount = count;
        cpuSelected = 0;
        affEnabled = false;
        makeSelection = false;
        cpuNames = new String[count + 1];
        cpuNames[0] = String.format( MENU_NAF, 0, count - 1 );
        for( int i=0; i<count; i++ )
        {
            cpuNames[i + 1] = String.format( MENU_CPU, i );
        }
    }

    // This method returns JDialog window.
    JDialog createDialog( JFrame parentWin )
    {
        final JDialog dialog = 
            new JDialog( parentWin, "Select logical CPU", true );
        dialog.setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        SpringLayout sl = new SpringLayout();
        JPanel p = new JPanel( sl );
        
        // Create combo box and buttons with user actions.
        JComboBox c1 = new JComboBox();
        JButton b1 = new JButton( "OK" );
        JButton b2 = new JButton( "Cancel" );
        c1.setPreferredSize( new Dimension( COMBO_X   , COMBO_Y   ) );
        b1.setPreferredSize( new Dimension( BUTTONS_X , BUTTONS_Y ) );
        b2.setPreferredSize( new Dimension( BUTTONS_X , BUTTONS_Y ) );
        
        // Layout management for combo box and buttons.
        sl.putConstraint( SpringLayout.NORTH, c1, 12, SpringLayout.NORTH, p );
        sl.putConstraint( SpringLayout.WEST,  c1, X1, SpringLayout.WEST,  p );
        sl.putConstraint( SpringLayout.SOUTH, b1, -14, SpringLayout.SOUTH, p );
        sl.putConstraint( SpringLayout.SOUTH, b2, -14, SpringLayout.SOUTH, p );
        sl.putConstraint( SpringLayout.WEST, b1, X1, SpringLayout.WEST, p );
        sl.putConstraint( SpringLayout.WEST, b2, 12, SpringLayout.EAST, b1 );
        
        // Add combo box and buttons to panel.
        p.add( c1 );
        p.add( b1 );
        p.add( b2 );
        
        // Fill combo-box by platform-specific CPU list.
        for ( String item : cpuNames ) 
        {
            c1.addItem( item );
        }

        // Add action listener for "OK" button.
        b1.addActionListener( ( ActionEvent e ) -> 
        {
            int selectedIndex = c1.getSelectedIndex();
            if ( selectedIndex >= 0 )
            {
                if ( selectedIndex == 0 )
                {
                    affEnabled = false;
                }
                else
                {
                    affEnabled = true;
                    int cpuIndex = selectedIndex - 1;
                    if ( cpuIndex < cpuCount )
                    {
                        cpuSelected = cpuIndex;
                    }
                }
            }
            makeSelection = true;
            dialog.dispose(); 
        });
        
        // Add action listener for "Cancel" button.
        b2.addActionListener( ( ActionEvent e ) -> 
        { 
            dialog.dispose(); 
        });

        // Set dialogue window visual options and return dialogue window.
        dialog.setContentPane( p );
        dialog.setSize( BOX_X, BOX_Y );
        dialog.setResizable( false );
        return dialog;
    }
}
