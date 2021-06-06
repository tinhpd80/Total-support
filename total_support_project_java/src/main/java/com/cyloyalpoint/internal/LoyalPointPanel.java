package com.cyloyalpoint.internal;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.opencl.cycl.CyCL;
import org.cytoscape.opencl.cycl.CyCLDevice;
import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskObserver;

import com.cyloyalpoint.util.StringUtil;
import com.cyloyalpoint.view.ButtonGroupAtLeastOne;

public class LoyalPointPanel extends JPanel implements CytoPanelComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final CyApplicationManager cyApplicationManager;
	private final TaskManager<?, ?> taskManager;
	private final CyNetworkFactory networkFactory;
	private final JFrame parentFrame;

	private List<CyCLDevice> selectedDevices;
	private Map<CyCLDevice, JCheckBox> checkBoxDevices;

	private JButton btnExecute;
	private ButtonGroup btnGroup;
	private JRadioButton radSequence;
	private JRadioButton radParallel;
	private JRadioButton radMultiParallel;
	private JPanel panelCompute;
	private JTextField tfSaveFile;
	private JButton btnSaveFile;
	private JLabel lbStatus;

	private String folderName;
	private String fileName;

	public LoyalPointPanel(CyApplicationManager cyApplicationManager, CySwingApplication cySwingApplication,
			TaskManager<?, ?> taskManager, CyNetworkFactory networkFactory) {
		this.cyApplicationManager = cyApplicationManager;
		this.taskManager = taskManager;
		this.networkFactory = networkFactory;
		this.parentFrame = cySwingApplication.getJFrame();

		initComponents();
		initCyCLDevices();
	}

	public Component getComponent() {
		return this;
	}

	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.SOUTH;
	}

	public String getTitle() {
		return "Loyal Point";
	}

	public Icon getIcon() {
		return null;
	}

	private void initComponents() {
		this.setLayout(new GridBagLayout());
		this.setBorder(BorderFactory.createTitledBorder("Loyal Point"));
		this.setVisible(true);

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;

		panelCompute = new JPanel();
		panelCompute.setBorder(BorderFactory.createTitledBorder("Compute"));
		this.add(panelCompute, gbc);

		radSequence = new JRadioButton("Sequence");
		radSequence.setSelected(true);
		radSequence.addActionListener(new RadioButtonSequenceListener());
		panelCompute.add(radSequence);

		radParallel = new JRadioButton("Parallel");
		radParallel.addActionListener(new RadioButtonParallelListener());
		panelCompute.add(radParallel);

		radMultiParallel = new JRadioButton("Multi-Parallel");
		radMultiParallel.addActionListener(new RadioButtonMultiParallelListener());
		panelCompute.add(radMultiParallel);

		btnGroup = new ButtonGroup();
		btnGroup.add(radSequence);
		btnGroup.add(radParallel);
		btnGroup.add(radMultiParallel);

		gbc.gridx = 0;
		gbc.gridy = 1;

		tfSaveFile = new JTextField();
		tfSaveFile.setToolTipText("Save file location");
		this.add(tfSaveFile, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;

		btnSaveFile = new JButton("Browse...");
		btnSaveFile.addActionListener(new SaveFile());
		this.add(btnSaveFile, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;

		btnExecute = new JButton("Execute");
		btnExecute.addActionListener(new ButtonExecuteListener());
		this.add(btnExecute, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;

		lbStatus = new JLabel();
		this.add(lbStatus, gbc);
	}

	private void initCyCLDevices() {
		this.selectedDevices = new ArrayList<>();
		this.checkBoxDevices = new HashMap<>();

		List<CyCLDevice> deviceList = null;

		try {
			deviceList = CyCL.getDevices();
		} catch (Exception e) {
		}

		ButtonGroupAtLeastOne buttonGroupAtLeastOne = new ButtonGroupAtLeastOne();

		for (CyCLDevice device : deviceList) {
			JCheckBox cb = new JCheckBox(device.name);
			checkBoxDevices.put(device, cb);
			buttonGroupAtLeastOne.add(cb);
		}
	}

	private class ButtonExecuteListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			CyNetwork network = cyApplicationManager.getCurrentNetwork();

			if (network == null) {
				JOptionPane.showMessageDialog(parentFrame,
						"Network is not imported or selected.\n(Go to Menu: File->Import->Network->File...)",
						"Information", 1);
				btnExecute.setEnabled(true);
				return;
			}

			try {
				if (radParallel.isSelected()) {
					ParallelLoyalPointTask task = new ParallelLoyalPointTask(network, folderName, fileName);
					LoyalPointTaskObserver observer = new LoyalPointTaskObserver();

					taskManager.execute(new TaskIterator(task), observer);
				} else if (radMultiParallel.isSelected()) {
					MultiParallelLoyalPointTask task = new MultiParallelLoyalPointTask(networkFactory, network, selectedDevices,
							folderName, fileName);
					LoyalPointTaskObserver observer = new LoyalPointTaskObserver();

					taskManager.execute(new TaskIterator(task), observer);
				} else {
					LoyalPointTask task = new LoyalPointTask(network, folderName, fileName);
					LoyalPointTaskObserver observer = new LoyalPointTaskObserver();

					taskManager.execute(new TaskIterator(task), observer);
				}

			} catch (Exception ex) {

			}
		}
	}

	private class RadioButtonSequenceListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
		}
	}

	private class RadioButtonParallelListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				CyCLDevice device = CyCL.getDevices().get(0);

				JOptionPane.showMessageDialog(parentFrame,
						"Device: " + device.name
								+ "\nYou can change OpenCL device by go to menu: Edit->Preferences->OpenCL Settings...",
						"Information", 1);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(parentFrame, "No OpenCL devices found, cannot run program in parallel.",
						"Error", 0);
				radSequence.setSelected(true);
			}
		}
	}

	private class RadioButtonMultiParallelListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				JDialog jDialog = new JDialog(parentFrame, "OpenCL Devices", true);
				jDialog.setLayout(new GridBagLayout());
				jDialog.getContentPane().setBackground(new Color(240, 240, 240));
				
				GridBagConstraints gbc = new GridBagConstraints();

				gbc.fill = GridBagConstraints.HORIZONTAL;
				gbc.gridx = 0;
				gbc.gridy = 0;

				for (Entry<CyCLDevice, JCheckBox> entry : checkBoxDevices.entrySet()) {
					jDialog.add(entry.getValue(), gbc);
					gbc.gridy++;
				}

				selectedDevices.clear();

				JButton jButton = new JButton("OK");
				jButton.setBackground(Color.MAGENTA);
				jButton.setForeground(Color.BLUE);
				jButton.addActionListener((ActionEvent event) -> {
					for (Entry<CyCLDevice, JCheckBox> entry : checkBoxDevices.entrySet()) {
						if (entry.getValue().isSelected()) {
							selectedDevices.add(entry.getKey());
						}
					}

					jDialog.dispose();
				});
				
				gbc.gridy++;
				jDialog.add(jButton, gbc);

				jDialog.setSize(500, 300);
				jDialog.setLocationRelativeTo(null);
				jDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
				jDialog.setVisible(true);

			} catch (Exception ex) {
				JOptionPane.showMessageDialog(parentFrame, "No OpenCL devices found, cannot run program in parallel.",
						"Error", 0);
				radSequence.setSelected(true);
			}
		}
	}

	private class SaveFile implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JFileChooser c = new JFileChooser();
			int rVal = c.showSaveDialog(parentFrame);
			if (rVal == JFileChooser.APPROVE_OPTION) {
				folderName = c.getCurrentDirectory().toString();
				fileName = c.getSelectedFile().getName();
				tfSaveFile.setText(new File(new File(folderName), fileName).getAbsolutePath());
			}
			if (rVal == JFileChooser.CANCEL_OPTION) {
				folderName = null;
				fileName = null;
				tfSaveFile.setText("");
			}
		}
	}

	private class LoyalPointTaskObserver implements TaskObserver {
		private long executeTime;

		@Override
		public void taskFinished(ObservableTask task) {
			executeTime = task.getResults(Long.class).longValue();
		}

		@Override
		public void allFinished(FinishStatus finishStatus) {
			lbStatus.setText("Elapsed Time: " + StringUtil.getDurationBreakdown(executeTime));
		}
	}
}
