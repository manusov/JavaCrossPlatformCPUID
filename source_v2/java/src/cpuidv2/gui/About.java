/* 
Java cross-platform CPUID Utility.
This source (Java CPUID v2.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source_v2
Previous source (Java CPUID v1.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source
All repositories: 
https://github.com/manusov?tab=repositories
(C) Manusov I.V. Refactoring at 2024.
-------------------------------------------------------------------------------
Handler for "About" item at root menu.
Returns JDialog GUI object for show "About" window.
*/

package cpuidv2.gui;

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
import cpuidv2.CPUIDv2;

public final class About
{
// This method returns JDialog window.
public JDialog createDialog
    ( JFrame parentWin , String longName , String vendorVersion )
    {
    final JDialog dialog = new JDialog( parentWin, "Program info", true );
    dialog.setDefaultCloseOperation( DISPOSE_ON_CLOSE );
    SpringLayout sl = new SpringLayout();
    JPanel p = new JPanel( sl );
    // Start create GUI components, create label with application icon.
    JLabel l1 = new JLabel ();
    try { 
        l1.setIcon(new javax.swing.ImageIcon(getClass().
           getResource( CPUIDv2.getAppLogo())));
        } 
    catch ( Exception e ) { }
    // Create labels with application information strings.
    JLabel l2 = new JLabel  ( CPUIDv2.getLongName() );
    JLabel l3 = new JLabel  ( CPUIDv2.getVendorName() );
    Color logoColor = new Color(40, 40, 80);
    l2.setForeground( logoColor );
    l3.setForeground( logoColor );
    Font font1 = new Font ( "Verdana", Font.ITALIC, 13 );  // Font for strings.
    l2.setFont( font1 );
    l3.setFont( font1 );
    // Create buttons with user actions.
    JButton b1 = new JButton( "GitHub" );
    JButton b2 = new JButton( "All projects" );
    JButton b3 = new JButton( "Cancel" );
    b1.setPreferredSize( new Dimension ( 140, 24 ) );
    b2.setPreferredSize( new Dimension ( 140, 24 ) );
    b3.setPreferredSize( new Dimension ( 100, 24 ) );
    Font font2 = new Font ( "Verdana", Font.PLAIN, 12 );  // Font for buttons.
    b1.setFont( font2 );
    b2.setFont( font2 );
    b3.setFont( font2 );
    // Layout management for panels and labels, application icon label.
    sl.putConstraint ( SpringLayout.NORTH, l1, 10, SpringLayout.NORTH, p );
    sl.putConstraint ( SpringLayout.WEST,  l1, 98, SpringLayout.WEST,  p );
    // Layout management for panels and information strings labels.
    sl.putConstraint ( SpringLayout.NORTH, l2,  2, SpringLayout.SOUTH, l1 );
    sl.putConstraint ( SpringLayout.WEST,  l2, 44, SpringLayout.WEST,  p  );
    sl.putConstraint ( SpringLayout.NORTH, l3,  2, SpringLayout.SOUTH, l2 );
    sl.putConstraint ( SpringLayout.WEST,  l3, 42, SpringLayout.WEST,  p  );
    // Layout management for panels and buttons.
    sl.putConstraint ( SpringLayout.SOUTH, b3, -12, SpringLayout.SOUTH, p );
    sl.putConstraint ( SpringLayout.WEST,  b3,  64, SpringLayout.WEST,  p );
    sl.putConstraint ( SpringLayout.SOUTH, b2,  -4, SpringLayout.NORTH, b3 );
    sl.putConstraint ( SpringLayout.WEST,  b2,  44, SpringLayout.WEST,  p );
    sl.putConstraint ( SpringLayout.SOUTH, b1,  -4, SpringLayout.NORTH, b2 );
    sl.putConstraint ( SpringLayout.WEST,  b1,  44, SpringLayout.WEST,  p );
    // Add labels and buttons to panel
    p.add( l1 );
    p.add( l2 );
    p.add( l3 );
    p.add( b1 );
    p.add( b2 );
    p.add( b3 );
    // Action listener for "GitHib" button
    b1.addActionListener( ( ActionEvent e ) -> 
        {
        if(Desktop.isDesktopSupported())
            { // web access
            try { Desktop.getDesktop().
                        browse(new URI( CPUIDv2.getProjectWeb() )); }
            catch ( IOException | URISyntaxException ex ) { } 
            }
        });
    // Action listener for "All projects" button
    b2.addActionListener( ( ActionEvent ae1 ) -> 
        {
        if(Desktop.isDesktopSupported())
            { // web access
            try { Desktop.getDesktop().
                        browse(new URI( CPUIDv2.getAllWeb() )); }
            catch ( IOException | URISyntaxException ex ) { } 
            }
        });
    // Action listener for cancel button.
    b3.addActionListener( ( ActionEvent e ) -> 
        { 
        dialog.dispose(); 
        });
    // Visual window and return    
    dialog.setContentPane( p );
    dialog.setSize( 240, 230 );
    dialog.setResizable( false );
    return dialog;
    }
}
