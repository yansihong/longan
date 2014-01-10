package com.rhcloud.numbercharacter.analysisofverificationcode.image.window;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rhcloud.numbercharacter.analysisofverificationcode.image.ImageUtils;

public class ImageWindow extends JFrame {

	private static final Logger LOG = LoggerFactory
			.getLogger(ImageWindow.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JMenuBar createMenuBar() {
		JMenuBar menuBar;
		JMenu menu;
		JMenuItem menuItem;
		JRadioButtonMenuItem rbMenuItem;
		JCheckBoxMenuItem cbMenuItem;

		// Create the menu bar.
		menuBar = new JMenuBar();

		// Build the first menu.
		menu = new JMenu("文件");
		menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription(
				"The only menu in this program that has menu items");
		menuBar.add(menu);

		// a group of JMenuItems
		menuItem = new JMenuItem("Open Image File", KeyEvent.VK_T);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(
				"This doesn't really do anything");
		menuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setAcceptAllFileFilterUsed(true);
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.setApproveButtonText("确定");
				fc.setFileFilter(new FileFilter() {

					@Override
					public String getDescription() {
						return "jpg;jpeg;png";
					}

					@Override
					public boolean accept(File f) {
						if (f.isDirectory())
							return true;
						String ext = null;
						String s = f.getName();
						int i = s.lastIndexOf('.');
						if (i > 0 && i < s.length() - 1) {
							ext = s.substring(i + 1).toLowerCase();
						}
						if (ext != null)
							if (ext.equals("jpg") || ext.equals("jpeg")
									|| ext.equals("png")) {
								return true;
							} else {
								return false;
							}

						return false;

					}
				});
				int returnVal = fc.showDialog(null, "Attach");
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					try {
						ImageApp.openImage(file);
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				fc.setSelectedFile(null);
				ImageWindow.this.repaint();
			}
		});
		menu.add(menuItem);

		menuItem = new JMenuItem("Save Image File", KeyEvent.VK_S);
		// menuItem.setMnemonic(KeyEvent.VK_T); //used constructor instead
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2,
				ActionEvent.CTRL_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(
				"This doesn't really do anything");
		menuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
		});
		menu.add(menuItem);

		// ImageIcon icon = createImageIcon("images/middle.gif");
		// menuItem = new JMenuItem("Both text and icon", icon);
		// menuItem.setMnemonic(KeyEvent.VK_B);
		// menuItem.addActionListener(this);
		// menu.add(menuItem);

		// menuItem = new JMenuItem(icon);
		// menuItem.setMnemonic(KeyEvent.VK_D);
		// menuItem.addActionListener(this);
		// menu.add(menuItem);

		// a group of radio button menu items
		menu.addSeparator();
		menuItem = new JMenuItem("Exit", KeyEvent.VK_Q);
		// menuItem.setMnemonic(KeyEvent.VK_T); //used constructor instead
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2,
				ActionEvent.CTRL_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(
				"This doesn't really do anything");
		menuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				System.exit(ABORT);
			}
		});
		menu.add(menuItem);
		
		ButtonGroup group = new ButtonGroup();

		rbMenuItem = new JRadioButtonMenuItem("A radio button menu item");
		rbMenuItem.setSelected(true);
		rbMenuItem.setMnemonic(KeyEvent.VK_R);
		group.add(rbMenuItem);
		rbMenuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
		});
		menu.add(rbMenuItem);

		rbMenuItem = new JRadioButtonMenuItem("Another one");
		rbMenuItem.setMnemonic(KeyEvent.VK_O);
		group.add(rbMenuItem);
		rbMenuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
		});
		menu.add(rbMenuItem);

		// a group of check box menu items
		menu.addSeparator();
		cbMenuItem = new JCheckBoxMenuItem("A check box menu item");
		cbMenuItem.setMnemonic(KeyEvent.VK_C);
		cbMenuItem.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub

			}
		});
		menu.add(cbMenuItem);

		cbMenuItem = new JCheckBoxMenuItem("Another one");
		cbMenuItem.setMnemonic(KeyEvent.VK_H);
		cbMenuItem.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub

			}
		});
		menu.add(cbMenuItem);

		// a submenu
		menu.addSeparator();
		

		// Build second menu in the menu bar.
		menu = new JMenu("图片处理");
		menu.setMnemonic(KeyEvent.VK_N);
		menu.getAccessibleContext().setAccessibleDescription(
				"This menu does nothing");
		
		menuItem = new JMenuItem("中值滤波");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ImageApp.filter(ImageUtils.MEDIAN_FILTER);
			}
		});
		menu.add(menuItem);
		
		menuItem = new JMenuItem("均值滤波");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ImageApp.filter(ImageUtils.MEAN_FILTER);
			}
		});
		menu.add(menuItem);
		
		menuItem = new JMenuItem("灰度");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ImageApp.filter(ImageUtils.GRAY_FILTER);
			}
		});
		menu.add(menuItem);
		
		
		JMenu submenu = new JMenu("二值化");
		menu.add(submenu);
		menuItem = new JMenuItem("MeanThreshold");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ImageApp.filter(ImageUtils.MeanThreshold);
			}
		});
		submenu.add(menuItem);
		menuItem = new JMenuItem("PTileThreshold");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		submenu.add(menuItem);
		menuItem = new JMenuItem("MinimumThreshold");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		submenu.add(menuItem);
		menuItem = new JMenuItem("IntermodesThreshold");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		submenu.add(menuItem);
		menuBar.add(menu);

		return menuBar;
	}

	public static void main(String[] args) {
		final ImageWindow iw = new ImageWindow();
		ImageApp.initImageApp(iw);
		javax.swing.SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				iw.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				iw.setJMenuBar(iw.createMenuBar());
				LayoutManager layout = new FlowLayout(FlowLayout.CENTER);
				//iw.setLayout(layout);
				LOG.info(iw.getLayout().toString());
				iw.setSize(800, 640);
				iw.setLocationRelativeTo(null);
				iw.setVisible(true);
			}
		});
	}
}
