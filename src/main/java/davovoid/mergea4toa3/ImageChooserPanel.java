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

import javax.swing.JPanel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.JTextField;
import javax.swing.TransferHandler;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.border.EtchedBorder;

/**
 * Component used to browse, drag and drop an image.
 * The image can be previewed and the name is displayed.
 * 
 * @author David
 *
 */
public class ImageChooserPanel extends JPanel {
	
	private BufferedImage img, defaultImg;
	
	private JImagePanel imgPanel;
	private JLabel lblName;
	private JButton btnBrowse;

	private DropTarget imgDropTarget = new DropTarget() {
		public synchronized void drop(DropTargetDropEvent evt) {

			// Drop image event
			evt.acceptDrop(DnDConstants.ACTION_COPY);
			dropImage(evt.getTransferable());

		}

	};
	private JPanel panel;

	/**
	 * Loads image from file and updates the UI.
	 * 
	 * @param image The image file
	 * @throws IOException If the image could not be read properly
	 */
	public void loadImage(File image) throws IOException {

		img = ImageIO.read(image);
		updateControls(image);

	}

	/**
	 * Returns current selected image as BufferedImage. The image had already been
	 * loaded using method {@code loadImage(image)}, so this function would not
	 * throw any IOException.
	 * 
	 * @return The current selected (and loaded) image.
	 */
	public BufferedImage getImg() {
		return img;
	}

	/**
	 * Sets the image from this control using an already existing BufferedImage
	 * object.
	 * 
	 * @param img The image to use as loaded image.
	 */
	public void setImg(BufferedImage img) {
		this.img = img;
		updateControls(null);
	}

	/**
	 * Gets the default image used, that is, the image displayed while not img is
	 * loaded ({@code getImg()} is null).
	 * 
	 * @return The default image.
	 */
	public BufferedImage getDefaultImg() {
		return defaultImg;
	}

	/**
	 * Sets the default image, that is, the image displayed while no img is
	 * loaded({@code getImg()} is null).
	 * 
	 * @param defaultImg The image to use as default image.
	 */
	public void setDefaultImg(BufferedImage defaultImg) {
		this.defaultImg = defaultImg;
		updateControls(null);
	}

	/**
	 * Updates the controls inside this control with the file information. This
	 * method will use the file name to update the file name displayed, and refresh
	 * the image displayed depending on whether there is a image set (not null), or
	 * otherwise use the default image (if available/not null).
	 * 
	 * @param file
	 */
	public void updateControls(File file) {

		imgPanel.setImage(img == null ? defaultImg : img);
		lblName.setText(file == null ? "" : file.getName());
		imgPanel.updateUI();

	}

	@Override
	public void setEnabled(boolean enabled) {
		
		super.setEnabled(enabled);
		btnBrowse.setEnabled(enabled);
		
	}

	/**
	 * Create the panel.
	 */
	public ImageChooserPanel() {
		setOpaque(false);
		setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.GLUE_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.GLUE_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		imgPanel = new JImagePanel();
		imgPanel.setOpaque(true);
		imgPanel.setBackground(Color.white);
		imgPanel.setBorder(new LineBorder(Color.LIGHT_GRAY));
		imgPanel.setTransferHandler(new TransferHandler() {
			
			@Override
	        public boolean canImport(TransferHandler.TransferSupport support) {
	            return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
	        }

	        @Override
	        public boolean importData(TransferHandler.TransferSupport support) {
	        	
	        	// Drop event but in the image panel
	        	
	            boolean accept = false;
	            if (canImport(support)) {
	                try {
	                    Transferable t = support.getTransferable();

	                    dropImage(t);
	                    
	                } catch (Exception exp) {
	                    exp.printStackTrace();
	                }
	            }
	            return accept;
	        }

		});
		add(imgPanel, "2, 2, fill, fill");
		
		panel = new JPanel();
		panel.setOpaque(false);
		add(panel, "2, 4, fill, fill");
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.GLUE_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		lblName = new JLabel();
		panel.add(lblName, "1, 1, left, center");
		lblName.setDropTarget(imgDropTarget);
		
		btnBrowse = new JButton("...");
		panel.add(btnBrowse, "3, 1, left, top");
		btnBrowse.addActionListener(e -> {
			
			// Browse an image to use it as image
			
			JFileChooser chooser = new JFileChooser();
			
			if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				
				File file = chooser.getSelectedFile();
				
				guiLoadImage(file);
				
			}
			
		});

	}

	/**
	 * Same as {@code loadImage(file)}, but noticing the user
	 * about the existence of an error. 
	 * @param file The image file to load.
	 */
	private void guiLoadImage(File file) {
		try {
			
			loadImage(file);
			
		} catch (IOException e1) {
			
			JOptionPane.showMessageDialog(null, "Error: file could not be loaded as image.");
			
			e1.printStackTrace();
		}
	}

	/**
	 * Common image dropping method, used by drag&drop or transfer handling.
	 * @param transferable
	 */
	private void dropImage(Transferable transferable) {
		if(!isEnabled()) return;
		
		try {
			
			List<File> droppedFiles = (List<File>) transferable
					.getTransferData(DataFlavor.javaFileListFlavor);

			for(File file : droppedFiles) {
				
				guiLoadImage(file);
				
			}
			
		} catch (UnsupportedFlavorException | IOException e) {
			e.printStackTrace();
		}
	}

}
