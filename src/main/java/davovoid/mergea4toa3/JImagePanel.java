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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * JPanel modified to display an image. The image is dynamically resized using
 * anti-aliasing sizing procedures. The sizing occurs only if the panel changes
 * size, and after certain time, so it is resized asynchronously once the size
 * is stable, not affecting the parent component painting speed.
 * 
 * This panel has also the possibility of showing a loading animation (i.e. if
 * requiring to show that the image is being processed).
 * 
 * This panel can also show a magnifying glass while the cursor goes through.
 * 
 * @author David
 *
 */
public class JImagePanel extends JPanel {

	private static final long serialVersionUID = 7260513590679309946L;
	private JImagePanel thisObject = this;

	private double scaling = 1d; // Value updated later in paintComponent routine

	// Threads used for scaling image per panel size and for the loading animation
	private Thread scalingThread = null;
	private Thread loadingThread = null;
	
	// Loading animation
	private int loadingAngle = 0;
	private double loadingPercent=0d;

	// Images (incl. scaled image)
	private BufferedImage originalImage;
	private Image scaledImage;
	
	private boolean mousefullScale = false;
	private int mouseX=-1, mouseY=-1;

	/**
	 * Gets the set image.
	 * @return
	 */
	public BufferedImage getImage() {
		return originalImage;
	}

	/**
	 * Sets the image and updates the UI.
	 * @param originalImage
	 */
	public void setImage(BufferedImage originalImage) {
		this.originalImage = originalImage;
		reloadScalingImage();
		repaint();
	}

	/**
	 * Gets the mouse magnifying glass status.
	 * @return True if activated.
	 */
	public boolean isMousefullScale() {
		return mousefullScale;
	}

	/**
	 * Sets the usage of a magnifying glass when the mouse
	 * goes through the panel.
	 * @param mousefullScale True to activate.
	 */
	public void setMousefullScale(boolean mousefullScale) {
		this.mousefullScale = mousefullScale;
		if(!mousefullScale) {
			
			mouseX=-1;
			mouseY=-1;
			repaint();
			
		}
	}

	/**
	 * Gets the actual panel width according to the OS scaling.
	 * @return
	 */
	public int getWidthCorr() {

		return (int) (getWidth() * scaling);

	}

	/**
	 * Gets the actual panel height according to the OS scaling.
	 * @return
	 */
	public int getHeightCorr() {

		return (int) (getHeight() * scaling);

	}
	
	/**
	 * Activates the loading animation.
	 */
	public void activateLoading() {
		
		loadingThread = new Thread() {
			
			@Override
			public void run() {
				
				try {
					
					while(!isInterrupted()) {
						
						loadingAngle += 10;
						loadingAngle %=360;

						//System.out.println(loadingAngle);
						
						repaint();
						
						Thread.sleep(100);
						
					}
					
				} catch (InterruptedException e) {
				}
				
			}
			
		};
		
		loadingThread.start();
		
	}
	
	/**
	 * Deactivates the loading animation, if existent.
	 */
	public void deactivateLoading() {
		
		try {
			
			if(loadingThread!=null)
				loadingThread.interrupt();
			
			loadingThread = null;
			
		} catch (Exception e) {
		}
		
	}

	/**
	 * Gets the loading percent set to the panel for loading animation.
	 * @return
	 */
	public double getLoadingPercent() {
		return loadingPercent;
	}

	/**
	 * Sets the loading percent for the loading animation.
	 * @param loadingPercent Double value from 0 to 1 (both included).
	 * Values lower than 0 or greater than 1 would be approximated
	 * to the nearest value.
	 */
	public void setLoadingPercent(double loadingPercent) {
		
		this.loadingPercent = loadingPercent>1d ? 1d : loadingPercent<0d ? 0d : loadingPercent;
	}

	/**
	 * Reloads the scaling image as a thread. It waits some time and, if not
	 * interrupted, it gets scaled.
	 */
	private void reloadScalingImage() {
		if (scalingThread != null)
			scalingThread.interrupt(); // If recalled, interrupt the previous thread

		scalingThread = new Thread() {

			@Override
			public void run() {

				try {

					// Waits 1 second
					if (scaledImage != null)
						Thread.sleep(1000);

					// If not interrupted means the panel has not been resized
					// So it can be painted now using EventQueue
					EventQueue.invokeLater(new Runnable() {

						@Override
						public void run() {

							if (originalImage == null)
								return;

							//System.out.println("Painting...");

							int width = Math.min(getWidthCorr(),
									getHeightCorr() * originalImage.getWidth() / originalImage.getHeight());

							int height = Math.min(getHeightCorr(),
									getWidthCorr() * originalImage.getHeight() / originalImage.getWidth());

							//System.out.format("Resize: %d x %d\r\n", width, height);

							scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING);

							thisObject.repaint();

						}

					});

				} catch (InterruptedException e) {
				}

			}

		};

		scalingThread.start();
	}

	public JImagePanel() {

		super();

		addComponentListener(new ComponentAdapter() {

			public void componentResized(ComponentEvent e) {

				// Reload scaling image once resized
				// No worries on calling it loads of times,
				// it is considered so the previous call gets
				// interrupted, so only if the component stops
				// getting resized the image gets rescaled.
				reloadScalingImage();

			}

		});
		
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseExited(MouseEvent e) {

				// -1 means no magnifying glass
				mouseX=-1;
				mouseY=-1;
				
				//System.out.println("Mouse left.");
				
			}
			
		});
		
		addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseMoved(MouseEvent e) {

				// Magnifying glass might be disabled
				if(!mousefullScale) {

					// Well, it is, so set all to -1
					
					mouseX=-1;
					mouseY=-1;
					
					return;
					
				}
				
				// Not disabled, set mouse position and repaint component
				
				mouseX = e.getX();
				mouseY = e.getY();
				
				//System.out.format("Mouse position: %d, %d.\r\n", mouseX, mouseY);
				
				repaint();
				
			}
			
		});

	}

	public JImagePanel(File imgFile) throws IOException {

		super();

		// Load the image and start scaling it
		
		if (imgFile == null)
			return;
		if (!imgFile.exists())
			return;
		originalImage = ImageIO.read(imgFile);
		reloadScalingImage();

	}

	@Override
	public void paintComponent(Graphics g) {

		Graphics2D g2d = (Graphics2D) g;

		super.paintComponent(g);

		// Scaling related functions
		
		final AffineTransform t = g2d.getTransform();
		scaling = t.getScaleX();

		// Antialiasing
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Image related rendering
		
		if (originalImage != null && scaledImage != null) {
				
			int width = Math.min(getWidth(), getHeight() * originalImage.getWidth() / originalImage.getHeight());
	
			int height = Math.min(getHeight(), getWidth() * originalImage.getHeight() / originalImage.getWidth());
	
			int x = (getWidth() - width) / 2;
			int y = (getHeight() - height) / 2;
	
			g2d.drawImage(scaledImage, x, y, width, height, null);

			// Mouse zoom function
			
			if(mouseX>=0 && mouseY>=0) {
				
				// Generate the zoom pan
				
				// Cursor position on the original image
				int posx = (mouseX-x)*originalImage.getWidth()/width;
				
				int posy = (mouseY-y)*originalImage.getHeight()/height;
	
				//System.out.format("Original img position: %d, %d.\r\n", posx, posy);
				
				// If positioning over a real pixel
				if(posx>=0 && posx<originalImage.getWidth()
						&& posy>=0 && posy<originalImage.getHeight()) {
					
					// Generate the visor, scaled
					
					int visorWidth = (int) (200*scaling);
					int visorHeight = (int) (200*scaling);
					
					BufferedImage visorImg = new BufferedImage(visorWidth,visorHeight,BufferedImage.TYPE_INT_RGB);
					
					// Iterate visor pixels and corresponding it the original image pixel
					
					for(int vx=0; vx<visorWidth; vx++) {
						
						int originalx = posx-visorWidth/2+vx;
		
						for(int vy=0; vy<visorHeight; vy++) {
							
							int originaly = posy-visorHeight/2+vy;
		
							visorImg.setRGB(vx, vy,
									originalx<0 || originalx>=originalImage.getWidth()
									|| originaly<0 || originaly>=originalImage.getHeight() ?
									Color.white.getRGB() :
									originalImage.getRGB(originalx, originaly));
							
						}
						
					}
					
					// Change scaling to keep proportions
					visorWidth /= scaling;
					visorHeight /= scaling;
					
					// Draw visor
					g2d.drawImage(visorImg, mouseX-visorWidth/2, mouseY-visorHeight/2, visorWidth, visorHeight, null);
					
					// Draw visor border
					g2d.setColor(Color.white);
					g2d.setStroke(new BasicStroke(1));
					g2d.drawRect(mouseX-visorWidth/2, mouseY-visorHeight/2, visorWidth, visorHeight);
					
				}
				
			}

		}
		
		// Loading animation if applies
		
		if(loadingThread!=null) {
			
			int width = 200;
			int height = 200;
	
			int x = (getWidth() - width) / 2;
			int y = (getHeight() - height) / 2;

			g2d.setColor(Color.DARK_GRAY);
			g2d.setStroke(new BasicStroke(14));
				
			g2d.drawArc(x, y, width, height, loadingAngle, (int) (180*loadingPercent));
			g2d.drawArc(x, y, width, height, (loadingAngle+180)%360, (int) (180*loadingPercent));

			g2d.setColor(Color.white);
			g2d.setStroke(new BasicStroke(10));
				
			g2d.drawArc(x, y, width, height, loadingAngle, (int) (180*loadingPercent));
			g2d.drawArc(x, y, width, height, (loadingAngle+180)%360, (int) (180*loadingPercent));
			
		}

	}

}
