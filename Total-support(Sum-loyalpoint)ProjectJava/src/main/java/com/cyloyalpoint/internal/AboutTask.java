package com.cyloyalpoint.internal;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

import com.cyloyalpoint.util.ImageUtil;

public class AboutTask extends AbstractTask {

	private JFrame frame;
	private JLabel authorName;
	private JLabel authorEmail;
	private JLabel authorAvatar;

	public AboutTask() {
		frame = new JFrame();
		frame.setUndecorated(true);
		frame.setSize(600, 300);
		frame.setLayout(null);

		frame.setLocationRelativeTo(null);
		frame.setAlwaysOnTop(true);
		frame.getContentPane().setBackground(Color.WHITE);

		authorName = new JLabel("Author: Dong Kien Tran");
		authorName.setBounds(50, 165, 200, 30);
		authorName.setFont(new Font("Tahoma", 1, 13));
		frame.add(authorName);

		authorEmail = new JLabel("Email: trandongkien.sine@gmail.com");
		authorEmail.setBounds(25, 185, 250, 30);
		authorEmail.setFont(new Font("Tahoma", 0, 13));
		frame.add(authorEmail);

		authorAvatar = new JLabel();
		authorAvatar.setBounds(50, 20, 150, 150);
		BufferedImage bi = ImageUtil.cropImage(getClass().getResource("/images/kientran.jpg"), 300, 400, 300, 10);
		Image image = bi.getScaledInstance(authorAvatar.getWidth(), authorAvatar.getHeight(), Image.SCALE_SMOOTH);
		ImageIcon imageIcon = new ImageIcon(image);
		authorAvatar.setIcon(imageIcon);
		frame.add(authorAvatar);

		frame.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				frame.dispose();
			}
		});
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		 frame.setVisible(true);
	}
}
