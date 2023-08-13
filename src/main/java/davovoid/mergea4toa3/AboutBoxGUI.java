/*
 * mergea4toa3 - A GUI program for merging A4 scanned fragments to obtain
 * a single A3 image. Copyright (C) 2023  David R. Araújo Piñeiro (Davovoid)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package davovoid.mergea4toa3;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.apache.commons.io.IOUtils;

import javax.swing.JEditorPane;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.awt.Font;
import java.awt.Color;
import java.awt.Toolkit;

/**
 * Frame used to show text documents related to current application. The
 * documents are taken from classpath /davovoid/mergea4toa3/text (resources).
 * The document name is taken as a parameter when constructing the JFrame. Read
 * as UTF-8 and shown on screen using monospaced font.
 * 
 * @author David
 *
 */
public class AboutBoxGUI extends JFrame {
	private JEditorPane txtAbout;
	private JScrollPane scp;

	/**
	 * Create the frame.
	 * 
	 * @param docname the document name.
	 */
	public AboutBoxGUI(String docname) {
		setIconImage(Toolkit.getDefaultToolkit().getImage(AboutBoxGUI.class.getResource("/davovoid/mergea4toa3/icons/app-icon-64.png")));
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				
				String content;
				
				// Read document content as UTF-8
				try {
					content = IOUtils.resourceToString("/davovoid/mergea4toa3/text/" + docname, StandardCharsets.UTF_8);
				
				} catch (IOException e1) {
					
					// In case of error reading the document show warning
					e1.printStackTrace();
					content = "< Error retrieving internal about.txt content.\r\n"
							+ "Please check the program integrity >";
					
				}
				
				// Set text content and move scroll up to the beginning
				txtAbout.setText(content);
				txtAbout.setCaretPosition(0);
				
				
			}
		});
		setBounds(100, 100, 700, 480);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		txtAbout = new JEditorPane();
		txtAbout.setFont(new Font("Monospaced", txtAbout.getFont().getStyle(), txtAbout.getFont().getSize()));
		txtAbout.setEditable(false);
		
		scp = new JScrollPane();
		scp.setBorder(null);
		scp.setViewportBorder(null);
		scp.setViewportView(txtAbout);
		
		getContentPane().add(scp, BorderLayout.CENTER);
		

	}

}
