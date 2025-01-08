/* 

This code is fragment of Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Handler for "About" item at root menu.
Returns JDialog GUI object for show "About" window.

*/

package dumploader.gui;

import dumploader.DumpLoader;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JButton;

final class About
{
private final static int BOX_X = 290;
private final static int BOX_Y = 245;
private final static int BUTTONS12_X = 140;
private final static int BUTTON3_X = 100;
private final static int BUTTONS_Y = 24;

private final static int X1 = ( BOX_X - 32 ) / 2 - 5; // Depends on icon X = 32.
private final static int X2 = ( BOX_X - 240 ) / 2;    // Depends on string X.
private final static int X3 = ( BOX_X - 105 ) / 2;
private final static int X4 = ( BOX_X - 230 ) / 2;
private final static int X5 = ( BOX_X - BUTTONS12_X ) / 2 - 5;
private final static int X6 = ( BOX_X - BUTTONS12_X ) / 2 - 5;
private final static int X7 = ( BOX_X - BUTTON3_X ) / 2 - 5;
        
    // This method returns JDialog window.
    JDialog createDialog( JFrame parentWin )
    {
        final JDialog dialog = new JDialog( parentWin, "Program info", true );
        dialog.setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        SpringLayout sl = new SpringLayout();
        JPanel p = new JPanel( sl );
        
        // Start create GUI components, create label with application icon.
        JLabel l1 = new JLabel ();
        try 
        { 
            l1.setIcon(new javax.swing.ImageIcon(getClass().
                getResource( DumpLoader.getAppLogo())));
        } 
        catch ( Exception e ) { }
        
        // Create labels with application information strings.
        JLabel l2 = new JLabel( DumpLoader.getLongName() + "." );
        JLabel l3 = new JLabel( DumpLoader.getVendorName1() );
        JLabel l4 = new JLabel( DumpLoader.getVendorName2() );
        Color logoColor = new Color(40, 40, 80);
        l2.setForeground( logoColor );
        l3.setForeground( logoColor );
        l4.setForeground( logoColor );
        
        // Font for strings.
        Font labelsFont = new Font ( "Verdana", Font.ITALIC, 13 );
        l2.setFont( labelsFont );
        l3.setFont( labelsFont );
        l4.setFont( labelsFont );
        
        // Create buttons with user actions.
        JButton b1 = new JButton( "GitHub" );
        JButton b2 = new JButton( "All projects" );
        JButton b3 = new JButton( "Cancel" );
        b1.setPreferredSize( new Dimension( BUTTONS12_X , BUTTONS_Y ) );
        b2.setPreferredSize( new Dimension( BUTTONS12_X , BUTTONS_Y ) );
        b3.setPreferredSize( new Dimension( BUTTON3_X   , BUTTONS_Y ) );
        
        // Font for buttons.
        Font buttonsFont = new Font ( "Verdana", Font.PLAIN, 12 );
        b1.setFont( buttonsFont );
        b2.setFont( buttonsFont );
        b3.setFont( buttonsFont );
        
        // Layout management for panels and labels, application icon label.
        sl.putConstraint( SpringLayout.NORTH, l1, 10, SpringLayout.NORTH, p );
        sl.putConstraint( SpringLayout.WEST,  l1, X1, SpringLayout.WEST,  p );
        
        // Layout management for panels and information strings labels.
        sl.putConstraint( SpringLayout.NORTH, l2,  2, SpringLayout.SOUTH, l1 );
        sl.putConstraint( SpringLayout.WEST,  l2, X2, SpringLayout.WEST,  p  );
        sl.putConstraint( SpringLayout.NORTH, l3,  2, SpringLayout.SOUTH, l2 );
        sl.putConstraint( SpringLayout.WEST,  l3, X3, SpringLayout.WEST,  p  );
        sl.putConstraint( SpringLayout.NORTH, l4,  2, SpringLayout.SOUTH, l3 );
        sl.putConstraint( SpringLayout.WEST,  l4, X4, SpringLayout.WEST,  p  );
        
        // Layout management for panels and buttons.
        sl.putConstraint( SpringLayout.SOUTH, b3, -12, SpringLayout.SOUTH, p );
        sl.putConstraint( SpringLayout.WEST,  b3,  X7, SpringLayout.WEST,  p );
        sl.putConstraint( SpringLayout.SOUTH, b2,  -4, SpringLayout.NORTH, b3 );
        sl.putConstraint( SpringLayout.WEST,  b2,  X6, SpringLayout.WEST,  p );
        sl.putConstraint( SpringLayout.SOUTH, b1,  -4, SpringLayout.NORTH, b2 );
        sl.putConstraint( SpringLayout.WEST,  b1,  X5, SpringLayout.WEST,  p );

        // Add labels and buttons to panel.
        p.add( l1 );
        p.add( l2 );
        p.add( l3 );
        p.add( l4 );
        p.add( b1 );
        p.add( b2 );
        p.add( b3 );
        
        // Add action listener for "GitHib" button.
        b1.addActionListener( ( ActionEvent e ) -> 
        {
            if(Desktop.isDesktopSupported())
            {   // web access
                try 
                { 
                    Desktop.getDesktop().
                        browse(new URI( DumpLoader.getProjectWeb() )); 
                }
                catch ( IOException | URISyntaxException ex ) { } 
            }
        });
        
        // Add action listener for "All projects" button.
        b2.addActionListener( ( ActionEvent ae1 ) -> 
        {
            if(Desktop.isDesktopSupported())
            { // web access
                try 
                { 
                    Desktop.getDesktop().
                        browse(new URI( DumpLoader.getAllWeb() )); 
                }
                catch ( IOException | URISyntaxException ex ) { } 
            }
        });
        
        // Action listener for cancel button.
        b3.addActionListener( ( ActionEvent e ) -> 
        { 
            dialog.dispose(); 
        });
        
        // Visual window and return.
        dialog.setContentPane( p );
        dialog.setSize( BOX_X, BOX_Y );
        dialog.setResizable( false );
        return dialog;
    }
}
