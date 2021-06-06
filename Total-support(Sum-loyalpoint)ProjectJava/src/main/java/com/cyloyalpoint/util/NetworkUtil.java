package com.cyloyalpoint.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

public class NetworkUtil {
	public static int[][] extractNetworkEdgeList(List<CyNode> nodes, List<CyEdge> edges) {
		Map<CyNode, Integer> nodeIndexes = new IdentityHashMap<CyNode, Integer>();
		int nodeIndex = 0;
		for (CyNode node : nodes) {
			nodeIndexes.put(node, nodeIndex++);
		}

		int[][] result = new int[edges.size()][];
		int edgeIndex = 0;
		for (CyEdge edge : edges) {
			result[edgeIndex++] = new int[] { nodeIndexes.get(edge.getSource()), nodeIndexes.get(edge.getTarget()),
					edge.isDirected() ? 1 : 0 };
		}
		return result;
	}

	public static int[] extractNetworkInDirectedAdjacentList(CyNetwork network) {
		List<CyNode> nodes = network.getNodeList();
		List<CyEdge> edges = network.getEdgeList();

		Map<CyNode, Integer> nodeIndexes = new HashMap<CyNode, Integer>();
		int nodeIndex = 0;
		for (CyNode node : nodes) {
			nodeIndexes.put(node, nodeIndex++);
		}

		Map<Integer, ArrayList<Integer>> inDirectedAdjacentList = new HashMap<Integer, ArrayList<Integer>>();
		
		for (CyNode node : nodes) {
			inDirectedAdjacentList.put(nodeIndexes.get(node), new ArrayList<Integer>());
		}

		int nAdjNode = 0;
		for (CyEdge edge : edges) {
			if (network.getRow(edge).isSet("interaction")
					&& network.getRow(edge).get("interaction", String.class).equalsIgnoreCase("directed")) {
				inDirectedAdjacentList.get(nodeIndexes.get(edge.getTarget())).add(nodeIndexes.get(edge.getSource()));
				nAdjNode++;
			}
		}

		Integer[] adjKey = inDirectedAdjacentList.keySet().toArray(new Integer[inDirectedAdjacentList.keySet().size()]);

		if (adjKey.length == 0)
			return new int[] { -1 };

		int nNode = nodes.size();
		int arraySize = nNode + 1 + nAdjNode;

		int[] result = new int[arraySize];

		int adjacentNodeBeginIndex, adjacentNodeValueIndex;
		adjacentNodeBeginIndex = 0;
		adjacentNodeValueIndex = nNode + 1;
		result[adjacentNodeBeginIndex] = adjacentNodeValueIndex;

		for (adjacentNodeBeginIndex = 1; adjacentNodeBeginIndex < nNode + 1; adjacentNodeBeginIndex++) {
			ArrayList<Integer> currentNodes = inDirectedAdjacentList.get(adjKey[adjacentNodeBeginIndex - 1]);

			// fill adjacent nodes
			for (int index = 0; index < currentNodes.size(); index++) {
				result[adjacentNodeValueIndex + index] = currentNodes.get(index);
			}

			adjacentNodeValueIndex += currentNodes.size();

			result[adjacentNodeBeginIndex] = adjacentNodeValueIndex;
		}

		return result;
	}

	public static int[] extractNetworkOutDirectedAdjacentList(CyNetwork network) {
		List<CyNode> nodes = network.getNodeList();
		List<CyEdge> edges = network.getEdgeList();

		Map<CyNode, Integer> nodeIndexes = new HashMap<CyNode, Integer>();
		int nodeIndex = 0;
		for (CyNode node : nodes) {
			nodeIndexes.put(node, nodeIndex++);
		}

		Map<Integer, ArrayList<Integer>> outDirectedAdjacentList = new HashMap<Integer, ArrayList<Integer>>();
		
		for (CyNode node : nodes) {
			outDirectedAdjacentList.put(nodeIndexes.get(node), new ArrayList<Integer>());
		}
		
		int nAdjNode = 0;
		for (CyEdge edge : edges) {
			if (network.getRow(edge).isSet("interaction")
					&& network.getRow(edge).get("interaction", String.class).equalsIgnoreCase("directed")) {
				outDirectedAdjacentList.get(nodeIndexes.get(edge.getSource())).add(nodeIndexes.get(edge.getTarget()));
				nAdjNode++;
			}
		}

		Integer[] adjKey = outDirectedAdjacentList.keySet()
				.toArray(new Integer[outDirectedAdjacentList.keySet().size()]);
		if (adjKey.length == 0)
			return new int[] { -1 };

		int nNode = nodes.size();
		int arraySize = nNode + 1 + nAdjNode;

		int[] result = new int[arraySize];

		int adjacentNodeBeginIndex, adjacentNodeValueIndex;
		adjacentNodeBeginIndex = 0;
		adjacentNodeValueIndex = nNode + 1;
		result[adjacentNodeBeginIndex] = adjacentNodeValueIndex;

		for (adjacentNodeBeginIndex = 1; adjacentNodeBeginIndex < nNode + 1; adjacentNodeBeginIndex++) {
			ArrayList<Integer> currentNodes = outDirectedAdjacentList.get(adjKey[adjacentNodeBeginIndex - 1]);

			// fill adjacent nodes
			for (int index = 0; index < currentNodes.size(); index++) {
				result[adjacentNodeValueIndex + index] = currentNodes.get(index);
			}

			adjacentNodeValueIndex += currentNodes.size();

			result[adjacentNodeBeginIndex] = adjacentNodeValueIndex;
		}

		return result;
	}

	public static int[] extractNetworkUnDirectedAdjacentList(CyNetwork network) {
		List<CyNode> nodes = network.getNodeList();
		List<CyEdge> edges = network.getEdgeList();

		Map<CyNode, Integer> nodeIndexes = new HashMap<CyNode, Integer>();
		int nodeIndex = 0;
		for (CyNode node : nodes) {
			nodeIndexes.put(node, nodeIndex++);
		}

		Map<Integer, ArrayList<Integer>> unDirectedAdjacentList = new HashMap<Integer, ArrayList<Integer>>();
		
		for (CyNode node : nodes) {
			unDirectedAdjacentList.put(nodeIndexes.get(node), new ArrayList<Integer>());
		}
		
		int nAdjNode = 0;
		for (CyEdge edge : edges) {
			if (!network.getRow(edge).isSet("interaction")
					|| !network.getRow(edge).get("interaction", String.class).equalsIgnoreCase("directed")) {
				unDirectedAdjacentList.get(nodeIndexes.get(edge.getSource())).add(nodeIndexes.get(edge.getTarget()));
				unDirectedAdjacentList.get(nodeIndexes.get(edge.getTarget())).add(nodeIndexes.get(edge.getSource()));

				nAdjNode += 2;
			}
		}

		Integer[] adjKey = unDirectedAdjacentList.keySet().toArray(new Integer[unDirectedAdjacentList.keySet().size()]);
		if (adjKey.length == 0)
			return new int[] { -1 };

		int nNode = nodes.size();
		int arraySize = nNode + 1 + nAdjNode;

		int[] result = new int[arraySize];

		int adjacentNodeBeginIndex, adjacentNodeValueIndex;
		adjacentNodeBeginIndex = 0;
		adjacentNodeValueIndex = nNode + 1;
		result[adjacentNodeBeginIndex] = adjacentNodeValueIndex;

		for (adjacentNodeBeginIndex = 1; adjacentNodeBeginIndex < nNode + 1; adjacentNodeBeginIndex++) {
			ArrayList<Integer> currentNodes = unDirectedAdjacentList.get(adjKey[adjacentNodeBeginIndex - 1]);

			// fill adjacent nodes
			for (int index = 0; index < currentNodes.size(); index++) {
				result[adjacentNodeValueIndex + index] = currentNodes.get(index);
			}

			adjacentNodeValueIndex += currentNodes.size();

			result[adjacentNodeBeginIndex] = adjacentNodeValueIndex;
		}

		return result;
	}
}
