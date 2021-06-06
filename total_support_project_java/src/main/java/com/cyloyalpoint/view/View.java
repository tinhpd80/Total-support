package com.cyloyalpoint.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;

import com.cyloyalpoint.algorithm.LoyalPoint;
import com.cyloyalpoint.algorithm.ParallelLoyalPoint2;

public class View extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public View(CyNetwork network) {

		JButton b = new JButton("Compute");
		b.setBounds(150, 400, 100, 40);

		JButton bTest = new JButton("TEST");
		bTest.setBounds(300, 400, 100, 40);

		this.add(b);
		this.add(bTest);

		JLabel l1, l2;
		l1 = new JLabel("Nodes: " + network.getNodeCount());
		l1.setBounds(50, 50, 100, 30);
		l2 = new JLabel("Edges: " + network.getEdgeCount());
		l2.setBounds(50, 100, 100, 30);
		this.add(l1);
		this.add(l2);

		JTextArea area = new JTextArea();
		area.setEditable(false);
		JScrollPane scroll = new JScrollPane(area);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setBounds(10, 150, 380, 250);

		getContentPane().add(scroll);
		this.setSize(400, 500);
		this.setLayout(null);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		this.setAlwaysOnTop(false);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		List<CyNode> nodes = network.getNodeList();
		CyTable nodeTable = network.getDefaultNodeTable();

		LoyalPoint lp = new LoyalPoint(network);

		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				b.setText("Computing...");
				b.setEnabled(false);

				String columnName = "Sum Loyal Point";
				if (nodeTable.getColumn(columnName) == null) {
					nodeTable.createColumn(columnName, Double.class, false);
				}

				String s = "";
				for (CyNode node : nodes) {
					Map<String, Float> result = lp
							.computeLoyalNodesOfLeader(network.getRow(node).get("name", String.class));
					s += (network.getRow(node).get("name", String.class) + "'s supporter:\n");

					double sum = 0.0;
					for (String loyalNode : result.keySet()) {
						s += (loyalNode + "\t" + result.get(loyalNode) + "\n");
						sum += result.get(loyalNode);
					}
					network.getRow(node).set(columnName, sum);

					area.setText(s);
					
					break;
				}

				b.setText("Compute");
				b.setEnabled(true);
			}
		});

		//
		// ParallelLoyalPoint2 pp = new ParallelLoyalPoint2(network);
		// int[] xx = pp.run();
		// String ss = "";
		// area.setText(ss);
		// for (int i = 0; i < xx.length; i++) {
		// ss += xx[i] + " - \n";
		// area.setText(ss);
		// }

		ParallelLoyalPoint2 plp = new ParallelLoyalPoint2(network);
		Map<CyNode, Integer> nodeIndexes = new HashMap<>();
		Map<Integer, CyNode> indexNodes = new HashMap<>();
		int nodeIndex = 0;
		for (CyNode node : nodes) {
			indexNodes.put(nodeIndex, node);
			nodeIndexes.put(node, nodeIndex++);
		}
		
		bTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bTest.setText("Computing...");
				bTest.setEnabled(false);

				String columnName = "Sum Loyal Point";
				if (nodeTable.getColumn(columnName) == null) {
					nodeTable.createColumn(columnName, Double.class, false);
				}

				String s = "";
				for (CyNode node : nodes) {
					Map<Integer, Float> result = plp.compute(nodeIndexes.get(node));
					s += (network.getRow(node).get("name", String.class) + "'s supporter:\n");

					double sum = 0.0;
					for (Integer loyalNode : result.keySet()) {
						s += (network.getRow(indexNodes.get(loyalNode)).get("name", String.class) + "\t"
								+ result.get(loyalNode) + "\n");
						sum += result.get(loyalNode);
					}
					network.getRow(node).set(columnName, sum);

					area.setText(s);
					
					break;
				}

				bTest.setText("TEST");
				bTest.setEnabled(true);
			}
		});
	}
}
