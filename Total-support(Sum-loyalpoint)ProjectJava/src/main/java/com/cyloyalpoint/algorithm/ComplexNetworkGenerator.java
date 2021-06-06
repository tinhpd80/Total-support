package com.cyloyalpoint.algorithm;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNode;

public class ComplexNetworkGenerator {

	private final CyNetworkFactory networkFactory;

	public ComplexNetworkGenerator(CyNetworkFactory networkFactory) {
		this.networkFactory = networkFactory;
	}

	public CyNetwork generateScaleFreeUndirectedNetwork(int nNode, int nEdge) throws Exception {
		// nLink is the number of extra links after adding the primary links connecting
		// all nodes
		nEdge -= nMinSFLink(nNode);

		if (nEdge < 0 || nEdge > nExtraSFULink(nNode)) {
			throw new Exception(String.format("The number of links should be in the range [%d .. %d]",
					nMinSFLink(nNode), nMaxSFULink(nNode)));
		}

		// Generate a network with the minimum links connecting to all nodes by
		// preffered attachment mechanism
		CyNetwork net = generateSimpleScaleFreeDzung(nNode);

		return addScaleFreeLink(net, nEdge);
	}

	public CyNetwork generateSimpleScaleFreeDzung(int nNode) {
		CyNetwork net = networkFactory.createNetwork();

		// Intitialize the network with a node inside
		CyNode node = net.addNode();

		int sum = 1;// Total of node degrees on the network

		Random random = new Random();

		for (int i = 1; i < nNode; i++) {
			node = net.addNode();

			// Randomly choose a node that's preferred by hub
			// Begin
			double r = 0, p = 1;
			r = random.nextDouble();
			int k = 0;
			for (; k < net.getNodeCount() && r <= p; k++)
				p -= (double) (net.getAdjacentEdgeList(net.getNodeList().get(k), CyEdge.Type.ANY).size() + 1) / sum;
			k = Math.max(k - 1, 0);
			// End

			net.addEdge(node, net.getNodeList().get(k), false);

			sum += 4;
		}
		return net;
	}

	public CyNetwork addScaleFreeLink(CyNetwork net, int nEdge) {

		// Randomly add more links between nodes
		for (; nEdge-- > 0;) {
			// avaNodes: available node, which is not fully connecting to all nodes
			List<CyNode> avaNodes = net.getNodeList().stream()
					.filter(node -> net.getAdjacentEdgeList(node, CyEdge.Type.ANY).size() < net.getNodeCount() - 1)
					.collect(Collectors.toList());

			if (avaNodes.isEmpty())
				break;

			Random random = new Random();

			// randomly select a node in the available nodes
			CyNode selectedNode = avaNodes.get(random.nextInt(avaNodes.size()));

			// toNodes: Contains nodes have no link to the selected node
			List<CyNode> toNodes = avaNodes.stream()
					.filter(node -> !net.getNeighborList(node, CyEdge.Type.ANY).contains(selectedNode))
					.collect(Collectors.toList());

			if (toNodes.isEmpty())
				break;

			int sum = 0;// Total of node degrees on the network
			for (int j = 0; j < toNodes.size(); j++)
				sum += net.getAdjacentEdgeList(toNodes.get(j), CyEdge.Type.ANY).size() + 1;

			// Randomly choose a node that's preferred by hub
			// Begin
			double r = 0, p = 1;
			r = random.nextDouble();
			int k = 0;
			for (; k < toNodes.size() && r <= p; k++)
				p -= (double) (net.getAdjacentEdgeList(toNodes.get(k), CyEdge.Type.ANY).size() + 1) / sum;
			k = Math.max(k - 1, 0);
			// End

			net.addEdge(selectedNode, toNodes.get(k), false);
		}
		return net;
	}

	public int nMinSFLink(int nNode) {
		return (nNode - 1);
	}

	public int nExtraSFULink(int nNode) {
		return nMaxSFULink(nNode) - nMinSFLink(nNode);
	}

	public int nMaxSFULink(int nNode) {
		return (nNode * nNode - nNode) / 2;
	}
}
