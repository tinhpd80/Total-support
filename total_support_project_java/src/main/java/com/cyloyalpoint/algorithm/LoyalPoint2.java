package com.cyloyalpoint.algorithm;

import java.util.HashMap;
import java.util.Map;

import org.cytoscape.model.CyNetwork;

import com.cyloyalpoint.util.ArrayUtil;
import com.cyloyalpoint.util.MathUtil;
import com.cyloyalpoint.util.NetworkUtil;

public class LoyalPoint2 {

	private int nodeCount;
	private float E;
	private final float EPS = 2 * 1e-7f;
	private int[] unDirectedAdjacentList;
	private int[] outDirectedAdjacentList;
	private int[] inDirectedAdjacentList;

	public LoyalPoint2(CyNetwork network) {
		this.nodeCount = network.getNodeCount();

		this.inDirectedAdjacentList = NetworkUtil.extractNetworkInDirectedAdjacentList(network);
		this.outDirectedAdjacentList = NetworkUtil.extractNetworkOutDirectedAdjacentList(network);
		this.unDirectedAdjacentList = NetworkUtil.extractNetworkUnDirectedAdjacentList(network);

		initialize();
	}

	private void initialize() {
		int maxOutDegMixing = 0;
		for (int node = 0; node < nodeCount; node++) {
			int sum = getNumberOfAdjacent(outDirectedAdjacentList, node)
					+ getNumberOfAdjacent(unDirectedAdjacentList, node);
			if (sum > maxOutDegMixing) {
				maxOutDegMixing = sum;
			}
		}

		E = 1.0f / maxOutDegMixing;
	}

	private int getNumberOfAdjacent(int[] adjList, int node) {
		if (adjList == null)
			return 0;
		return adjList[node + 1] - adjList[node];
	}

	private int getCurrentIndex(int[] adjList, int node, int index) {
		if (adjList == null)
			return 0;
		return adjList[adjList[node] + index];
	}

	public Map<Integer, Float> compute(int leader) {
		float[] result = new float[nodeCount + 1];

		// init
		for (int i = 0; i < result.length; i++) {
			result[i] = Float.MIN_VALUE;
		}

		int againstLeader = nodeCount;
		int[] normalNodes = new int[nodeCount - 1]; // except leader node
		int normalNodeIndex = 0;
		for (int node = 0; node < nodeCount; node++) {
			if (node != leader) {
				normalNodes[normalNodeIndex++] = node;
			}
		}

		computeLoyalNodesOfLeader(leader, againstLeader, normalNodes.length, normalNodes, unDirectedAdjacentList,
				inDirectedAdjacentList, result);

		Map<Integer, Float> ans = new HashMap<>();
		for (int i = 0; i < result.length; i++) {
			if (result[i] != Float.MIN_VALUE) {
				ans.put(i, result[i]);
			}
		}

		return ans;
	}

	public void computeLoyalNodesOfLeader(int leader, int againstLeader, int nNormalNode, int[] normalNodes,
			int[] unDirectedAdjacentList, int[] inDirectedAdjacentList, float[] result) {

		for (int node = 0; node < nNormalNode; node++) {

			float[] computeResult = new float[nodeCount + 1];
			computeCompetitive(leader, againstLeader, unDirectedAdjacentList, inDirectedAdjacentList, normalNodes[node],
					computeResult);
			result[normalNodes[node]] = computeResult[normalNodes[node]];

		}
	}

	public void computeCompetitive(int leader, int againstLeader, int[] unDirectedAdjacentList,
			int[] inDirectedAdjacentList, int targetNode, float[] result) {
		int maxIterations = 200;
		float[] tempLoyalPoint = new float[result.length];

		for (int node = 0; node < nodeCount; node++) {
			// result[node] = MathUtil.randomInRange(-1, 1); // random value in {-1, 0, 1}
			result[node] = 0;
		}

		result[leader] = tempLoyalPoint[leader] = 1;
		result[againstLeader] = tempLoyalPoint[againstLeader] = -1;

		float error = 0.0f;
		int iter = 0;

		do {
			error = 0.0f;
			for (int currentNode = 0; currentNode < result.length; currentNode++) {
				float currentLoyalPoint = result[currentNode];

				if (currentNode == leader || currentNode == againstLeader) {
					continue;
				}

				int[] inUnNodeList = new int[getNumberOfAdjacent(inDirectedAdjacentList, currentNode)
						+ getNumberOfAdjacent(unDirectedAdjacentList, currentNode)];

				int inUnNodeListIndex = 0;
				for (int index = 0; index < getNumberOfAdjacent(inDirectedAdjacentList, currentNode); index++) {
					inUnNodeList[inUnNodeListIndex++] = getCurrentIndex(inDirectedAdjacentList, currentNode, index);
				}
				for (int index = 0; index < getNumberOfAdjacent(unDirectedAdjacentList, currentNode); index++) {
					inUnNodeList[inUnNodeListIndex++] = getCurrentIndex(unDirectedAdjacentList, currentNode, index);
				}

				float sum = 0.0f;
				for (int index = 0; index < inUnNodeList.length; index++) {
					int neighbourNode = inUnNodeList[index];
					float weight = 1.0f;

					sum += weight * (result[neighbourNode] - currentLoyalPoint);
				}

				if (targetNode == currentNode) {
					int neighbourNode = againstLeader;
					float weight = 1.0f;

					sum += weight * (result[neighbourNode] - currentLoyalPoint);
				}

				float newLoyalPoint = currentLoyalPoint + E * sum;
				tempLoyalPoint[currentNode] = newLoyalPoint;
				error += Math.abs(newLoyalPoint - currentLoyalPoint);
			}

			ArrayUtil.swapArrays(result, tempLoyalPoint, result.length);

			iter++;
		} while (error > EPS && iter < maxIterations);
	}
}
