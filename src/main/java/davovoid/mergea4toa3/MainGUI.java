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
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicSplitPaneDivider;

import org.apache.commons.io.FilenameUtils;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import davovoid.libmergea4toa3.A3Merger;
import davovoid.libmergea4toa3.A3MergerStudyEvent;

import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JProgressBar;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Dimension;
import javax.swing.border.MatteBorder;
import javax.swing.SwingConstants;
import java.awt.SystemColor;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.FlowLayout;

public class MainGUI extends JFrame {
	private ImageChooserPanel leftImagePanel;
	private JButton btnMergeImages;
	private JProgressBar pbStatus;
	private JButton btnSaveImage;
	private JLabel lblStatus;
	private JImagePanel imgMerged;
	
	BufferedImage result = null;
	Thread mergingThread = null;
	private ImageChooserPanel centerImagePanel;
	private ImageChooserPanel rightImagePanel;
	private JLabel lblNewLabel_4;
	private JPanel cardPanel;
	private JPanel panel_2;
	
	private int numberOfMergedImages = 0;
	private JPanel panel_3;
	private JButton btnGoBack;
	private JLabel lblProcessing;
	private JPanel panel_4;
	private JImagePanel panel_5;
	private JLabel lblNewLabel_3;
	private JLabel lblNewLabel_5;
	private JLabel lblGnuGplV;
	private JLabel lblDavovoidGithub;
	private JLabel lblNewLabel_7;
	private JLabel label_1;

	private void setDefaultImages() throws IOException {
		
		leftImagePanel.setDefaultImg(ImageIO.read(MainGUI.class.getResourceAsStream("/davovoid/mergea4toa3/icons/paper-icon-left.png")));
		centerImagePanel.setDefaultImg(ImageIO.read(MainGUI.class.getResourceAsStream("/davovoid/mergea4toa3/icons/paper-icon-center.png")));
		rightImagePanel.setDefaultImg(ImageIO.read(MainGUI.class.getResourceAsStream("/davovoid/mergea4toa3/icons/paper-icon-right.png")));

		panel_5.setImage(ImageIO.read(MainGUI.class.getResourceAsStream("/davovoid/mergea4toa3/icons/app-icon.png")));
	}
	
	/**
	 * Create the frame.
	 */
	public MainGUI() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainGUI.class.getResource("/davovoid/mergea4toa3/icons/app-icon-64.png")));
		setTitle("A4 to A3 image merger");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 850, 600);
		setLocationRelativeTo(null);
		
		cardPanel = new JPanel();
		cardPanel.setBackground(Color.WHITE);
		cardPanel.setLayout(new CardLayout());
		cardPanel.setBorder(null);
		
		getContentPane().add(cardPanel);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		cardPanel.add(panel, "optionsPanel");
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.GLUE_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("0px:grow(12)"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.GLUE_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		panel_4 = new JPanel();
		panel_4.setOpaque(false);
		panel.add(panel_4, "1, 4, 3, 1, fill, fill");
		panel_4.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.GLUE_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.UNRELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.GLUE_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		panel_5 = new JImagePanel();
		panel_5.setPreferredSize(new Dimension(100, 100));
		panel_5.setOpaque(false);
		panel_4.add(panel_5, "4, 2, fill, fill");
		
		lblNewLabel_3 = new JLabel("A4 to A3 Merger");
		lblNewLabel_3.setFont(new Font(lblNewLabel_3.getFont().getName(), lblNewLabel_3.getFont().getStyle(), lblNewLabel_3.getFont().getSize() + 18));
		panel_4.add(lblNewLabel_3, "6, 2");
		
		lblNewLabel_4 = new JLabel("Drop or select images to merge:");
		lblNewLabel_4.setFont(new Font(lblNewLabel_4.getFont().getName(), lblNewLabel_4.getFont().getStyle(), lblNewLabel_4.getFont().getSize() + 8));
		panel.add(lblNewLabel_4, "2, 8");
		
		panel_2 = new JPanel();
		panel_2.setOpaque(false);
		panel.add(panel_2, "2, 10, fill, fill");
		panel_2.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.GLUE_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.GLUE_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.GLUE_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.GLUE_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		JLabel lblNewLabel = new JLabel("Left image:");
		lblNewLabel.setFont(lblNewLabel.getFont().deriveFont(lblNewLabel.getFont().getSize() + 2f));
		panel_2.add(lblNewLabel, "2, 2");
		
		JLabel lblNewLabel_1 = new JLabel("Center image:");
		lblNewLabel_1.setFont(lblNewLabel_1.getFont().deriveFont(lblNewLabel_1.getFont().getSize() + 2f));
		panel_2.add(lblNewLabel_1, "4, 2");
		
		JLabel lblNewLabel_2 = new JLabel("Right image:");
		lblNewLabel_2.setFont(lblNewLabel_2.getFont().deriveFont(lblNewLabel_2.getFont().getSize() + 2f));
		panel_2.add(lblNewLabel_2, "6, 2");
		
		leftImagePanel = new ImageChooserPanel();
		panel_2.add(leftImagePanel, "2, 4, default, fill");
		
		centerImagePanel = new ImageChooserPanel();
		panel_2.add(centerImagePanel, "4, 4, default, fill");
		
		rightImagePanel = new ImageChooserPanel();
		panel_2.add(rightImagePanel, "6, 4, default, fill");
		
		btnMergeImages = new JButton("Merge images now >");
		btnMergeImages.setIconTextGap(10);
		btnMergeImages.setIcon(new ImageIcon(MainGUI.class.getResource("/davovoid/mergea4toa3/icons/app-icon-32.png")));
		btnMergeImages.setFont(new Font(btnMergeImages.getFont().getName(), btnMergeImages.getFont().getStyle(), btnMergeImages.getFont().getSize() + 6));
		btnMergeImages.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if(leftImagePanel.getImg()==null
						|| centerImagePanel.getImg()==null
						|| rightImagePanel.getImg()==null) {
					
					JOptionPane.showMessageDialog(null, "Please select all required images on the left.");
					
					return;
					
				}
				
				BufferedImage imgLeft = leftImagePanel.getImg();
				BufferedImage imgCenter = centerImagePanel.getImg();
				BufferedImage imgRight = rightImagePanel.getImg();
				
				imgMerged.setImage(imgLeft);
				imgMerged.updateUI();
				
				((CardLayout) cardPanel.getLayout()).show(cardPanel, "previewPanel");
				numberOfMergedImages = 0;
				
				mergingThread = new Thread() {
					
					public void run(){
						
						A3Merger merger = new A3Merger(imgLeft);
						merger.setScannerLeftCorrection(true);
						
						merger.setMergerStudyEvent(new A3MergerStudyEvent() {

							@Override
							public void updateStudyProgress(double progress, int firstScaleRed, int scaleRed, int xMinFindRange,
									int xMaxFindRange, int yMinFindRange, int yMaxFindRange, int currentxpos, int currentypos,
									double currentangle, int bestxpos, int bestypos, double bestangle,
									double smallestdeviation) {

								EventQueue.invokeLater(new Runnable() {
									
									public void run() {

										double progressBarNum = progress * 50d + (double)(50*numberOfMergedImages);
										
										imgMerged.setLoadingPercent(progressBarNum/100d);
										
										String whatisbeingmerged = numberOfMergedImages==0 ? "center" : "right";
										
										if(scaleRed==1) {

											lblStatus.setText(String.format("(%.1f%%) Merging %s... Final processing, lowest index: %.5f.",
													progressBarNum, whatisbeingmerged, smallestdeviation));
											
										}
										
										else if(firstScaleRed==scaleRed) {

											lblStatus.setText(String.format("(%.1f%%) Merging %s... First exploring (scale 1/%d), lowest index: %.5f.",
													progressBarNum, whatisbeingmerged, scaleRed, smallestdeviation));
											
										} else {

											lblStatus.setText(String.format("(%.1f%%) Merging %s... Improving precision (scale 1/%d), lowest index: %.5f.",
													progressBarNum, whatisbeingmerged, scaleRed, smallestdeviation));
											
										}
										
										lblStatus.setForeground(smallestdeviation<100 ? Color.green.darker() : Color.red.darker());
										
										pbStatus.setValue((int) (progressBarNum * 10d));
										
									}
									
								});
								
								
							}
							
						});
						
						merger.mergeImageOnRight(imgCenter, true);
						result = merger.getWorkingImg();

						EventQueue.invokeLater(new Runnable() {
							
							public void run() {

								numberOfMergedImages = 1;
								imgMerged.setImage(result);
								imgMerged.updateUI();
								
							}
							
						});
						
						merger.mergeImageOnRight(imgRight, true);
						
						result = merger.getWorkingImg();

						EventQueue.invokeLater(new Runnable() {
							
							public void run() {

								imgMerged.setImage(result);
								imgMerged.updateUI();
								
								lblStatus.setText("Finished processing merge.");
								
								pbStatus.setValue(pbStatus.getMinimum());

								setUIEnable(true);
								
							}
							
						});
						
					}
					
				};

				setUIEnable(false);
				
				mergingThread.start();
				
			}
		});
		panel.add(btnMergeImages, "2, 12, right, default");
		
		lblNewLabel_5 = new JLabel("About software and libraries");
		lblNewLabel_5.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				AboutBoxGUI about = new AboutBoxGUI("about.txt");
				about.setTitle("About software and libraries");
				about.setVisible(true);
				
			}
		});
		lblNewLabel_5.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblNewLabel_5.setForeground(Color.ORANGE);
		
		JPanel panelAbouts = new JPanel();
		panelAbouts.setOpaque(false);
		FlowLayout flowLayout = (FlowLayout) panelAbouts.getLayout();
		flowLayout.setVgap(0);
		
		lblGnuGplV = new JLabel("Under GNU GPL v3 license");
		lblGnuGplV.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblGnuGplV.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				AboutBoxGUI about = new AboutBoxGUI("gpl-3.0.txt");
				about.setTitle("GNU GPLv3 License from MergeA4toA3 application");
				about.setVisible(true);
				
			}
		});
		
		lblDavovoidGithub = new JLabel("Copyright (C) 2023 David R. Araújo Piñeiro (Davovoid)");
		lblDavovoidGithub.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				try {
					
					Desktop.getDesktop().browse(new URI("https://github.com/davovoid"));
					
				} catch (IOException | URISyntaxException e1) {
					e1.printStackTrace();
				}
				
			}
		});
		lblDavovoidGithub.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		panelAbouts.add(lblDavovoidGithub);
		
		label_1 = new JLabel("·");
		panelAbouts.add(label_1);
		lblGnuGplV.setForeground(Color.ORANGE);
		panelAbouts.add(lblGnuGplV);
		
		lblNewLabel_7 = new JLabel("·");
		panelAbouts.add(lblNewLabel_7);
		panelAbouts.add(lblNewLabel_5);
		
		panel.add(panelAbouts, "2, 16");
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.WHITE);
		cardPanel.add(panel_1,"previewPanel");
		panel_1.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.GLUE_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(15dlu;default)"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		lblProcessing = new JLabel("Merging status");
		lblProcessing.setIconTextGap(10);
		lblProcessing.setIcon(new ImageIcon(MainGUI.class.getResource("/davovoid/mergea4toa3/icons/app-icon-32.png")));
		lblProcessing.setFont(new Font(lblProcessing.getFont().getName(), lblProcessing.getFont().getStyle(), lblProcessing.getFont().getSize() + 8));
		
		panel_1.add(lblProcessing, "2, 2");
		
		imgMerged = new JImagePanel();
		imgMerged.setOpaque(true);
		imgMerged.setBackground(Color.white);
		imgMerged.setMousefullScale(true);
		panel_1.add(imgMerged, "2, 4, fill, fill");
		
		lblStatus = new JLabel("Ready.");
		panel_1.add(lblStatus, "2, 6");
		
		pbStatus = new JProgressBar();
		pbStatus.setMaximum(1000);
		panel_1.add(pbStatus, "2, 8, default, fill");
		
		panel_3 = new JPanel();
		panel_3.setOpaque(false);
		panel_1.add(panel_3, "2, 10, fill, fill");
		panel_3.setLayout(new GridLayout(0, 2, 5, 0));
		
		btnGoBack = new JButton("< Go back");
		btnGoBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(mergingThread!=null) {
					
					try {
						
						mergingThread.stop();
						
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					
				}
				
				setUIEnable(true);
				
				((CardLayout)cardPanel.getLayout()).show(cardPanel, "optionsPanel");
				
			}
		});
		panel_3.add(btnGoBack);
		
		btnSaveImage = new JButton("Save image");
		panel_3.add(btnSaveImage);
		btnSaveImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				JFileChooser fileChooser = new JFileChooser();
				
				if(fileChooser.showSaveDialog(null)== JFileChooser.APPROVE_OPTION) {
					
					try {
						
						ImageIO.write(result,
								FilenameUtils.getExtension(fileChooser.getSelectedFile().getName()),
								fileChooser.getSelectedFile());

						JOptionPane.showMessageDialog(null, "Done successfully.");
						
					} catch (Exception e1) {

						JOptionPane.showMessageDialog(null, "Error processing file.");
						
						e1.printStackTrace();
					}
							
					
				}
						
				
			}
		});
		btnSaveImage.setEnabled(false);
		
		afterGUIConstruction();
		
	}

	private void afterGUIConstruction() {

		try {
			
			setDefaultImages();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void setUIEnable(boolean enable) {
		
		leftImagePanel.setEnabled(enable);
		centerImagePanel.setEnabled(enable);
		rightImagePanel.setEnabled(enable);
		btnMergeImages.setEnabled(enable);
		btnSaveImage.setEnabled(enable);
		btnGoBack.setText(enable ? "< Go back" : "< Stop and go back");
		lblProcessing.setText(enable ? "Merged. Now you can save the image." : "Merging in progress...");
		
		if(enable)
			imgMerged.deactivateLoading();
		else
			imgMerged.activateLoading();
		
	}

}
