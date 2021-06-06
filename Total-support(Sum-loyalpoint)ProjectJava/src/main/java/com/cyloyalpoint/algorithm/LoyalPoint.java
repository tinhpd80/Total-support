package com.cyloyalpoint.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

import com.cyloyalpoint.util.MathUtil;

public class LoyalPoint {

	private float E;
	private final float EPS = 2 * 1e-7f;
	private List<String> nodeList;
	private Map<String, ArrayList<String>> unDirectedAdjacentList;
	private Map<String, ArrayList<String>> outDirectedAdjacentList;
	private Map<String, ArrayList<String>> inDirectedAdjacentList;

	public LoyalPoint(CyNetwork network) {
		this.nodeList = new ArrayList<>();
		this.unDirectedAdjacentList = new HashMap<>();
		this.outDirectedAdjacentList = new HashMap<>();
		this.inDirectedAdjacentList = new HashMap<>();

		Map<CyNode, String> nodeIndexes = new HashMap<>();
		for (CyNode node : network.getNodeList()) {
			String nodeName = network.getRow(node).get("name", String.class);
			nodeIndexes.put(node, nodeName);
			nodeList.add(nodeName);
			inDirectedAdjacentList.put(nodeName, new ArrayList<>());
			outDirectedAdjacentList.put(nodeName, new ArrayList<>());
			unDirectedAdjacentList.put(nodeName, new ArrayList<>());
		}

		for (CyEdge edge : network.getEdgeList()) {
			if (network.getRow(edge).get("interaction", String.class).equalsIgnoreCase("directed")) {
				inDirectedAdjacentList.get(nodeIndexes.get(edge.getTarget())).add(nodeIndexes.get(edge.getSource()));
				outDirectedAdjacentList.get(nodeIndexes.get(edge.getSource())).add(nodeIndexes.get(edge.getTarget()));
			} else {
				unDirectedAdjacentList.get(nodeIndexes.get(edge.getSource())).add(nodeIndexes.get(edge.getTarget()));
				unDirectedAdjacentList.get(nodeIndexes.get(edge.getTarget())).add(nodeIndexes.get(edge.getSource()));
			}
		}

		initialize();
	}

	public void initialize() {
		int maxOutDegMixing = -1;
		for (String node : nodeList) {
			int sum = outDirectedAdjacentList.get(node).size() + unDirectedAdjacentList.get(node).size();
			if (sum > maxOutDegMixing) {
				maxOutDegMixing = sum;
			}
		}
		E = 1.0f / maxOutDegMixing;
	}

	public Map<String, Float> computeLoyalNodesOfLeader(String leader) {
		Map<String, Float> loyalNodesOfLeader = new HashMap<>();
		String againstLeader = "###AgainstLeader###";

		nodeList.add(againstLeader);

		List<String> normalNodes = new ArrayList<>();
		for (String node : nodeList) {
			if (!node.equals(leader) && !node.equals(againstLeader)) {
				normalNodes.add(node);
			}
		}

		for (String node : normalNodes) {
			unDirectedAdjacentList.get(node).add(againstLeader);

			Map<String, Float> result = computeCompetitive(leader, againstLeader);
			loyalNodesOfLeader.put(node, MathUtil.zero(result.get(node), EPS));

			unDirectedAdjacentList.get(node).remove(againstLeader);
		}

		nodeList.remove(againstLeader);

		return loyalNodesOfLeader;
	}

	public Map<String, Float> computeCompetitive(String leader, String againstLeader) {
		int maxIterations = 200;
		Map<String, Float> loyalPoint = new HashMap<>();
		Map<String, Float> tempLoyalPoint = new HashMap<>();

		for (String node : nodeList) {
			//loyalPoint.put(node, (float) MathUtil.randomInRange(-1, 1)); // random value in {-1, 0, 1}
			loyalPoint.put(node, 0f);
		}

		loyalPoint.put(leader, 1f);
		tempLoyalPoint.put(leader, 1f);
		loyalPoint.put(againstLeader, -1f);
		tempLoyalPoint.put(againstLeader, -1f);

		float error = 0.0f;
		int iter = 0;

		do {
			error = 0.0f;

			for (String currentNode : loyalPoint.keySet()) {
				float currentLoyalPoint = loyalPoint.get(currentNode);

				if (currentNode.equals(leader) || currentNode.equals(againstLeader)) {
					continue;
				}

				List<String> inUnNodeList = new ArrayList<>();
				inUnNodeList.addAll(inDirectedAdjacentList.get(currentNode));
				inUnNodeList.addAll(unDirectedAdjacentList.get(currentNode));

				float sum = 0.0f;
				for (String neighbourNode : inUnNodeList) {
					float weight = 1.0f;

					sum += weight * (loyalPoint.get(neighbourNode) - currentLoyalPoint);
				}

				float newLoyalPoint = currentLoyalPoint + E * sum;
				tempLoyalPoint.put(currentNode, newLoyalPoint);
				error += Math.abs(newLoyalPoint - currentLoyalPoint);
			}

			Map<String, Float> tempMap = loyalPoint;
			loyalPoint = tempLoyalPoint;
			tempLoyalPoint = tempMap;

			iter++;
		} while (error > EPS && iter < maxIterations);

		return loyalPoint;
	}
}
