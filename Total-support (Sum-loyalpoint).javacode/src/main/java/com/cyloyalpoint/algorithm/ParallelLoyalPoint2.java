package com.cyloyalpoint.algorithm;

import java.util.HashMap;
import java.util.Map;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.opencl.cycl.CyCL;
import org.cytoscape.opencl.cycl.CyCLBuffer;
import org.cytoscape.opencl.cycl.CyCLDevice;
import org.cytoscape.opencl.cycl.CyCLProgram;

import com.cyloyalpoint.util.ArrayUtil;
import com.cyloyalpoint.util.MathUtil;
import com.cyloyalpoint.util.NetworkUtil;

public class ParallelLoyalPoint2 {

	private final CyCLDevice device;
	private final CyCLProgram program;

	private CyNetwork network;

	private final long GLOBAL_WORK_SIZE;
	private final long LOCAL_WORK_SIZE;

	public ParallelLoyalPoint2(CyNetwork network) {
		try {
			this.device = CyCL.getDevices().get(0);
		} catch (Exception e) {
			throw new RuntimeException("No OpenCL devices found, cannot run program.");
		}

		String[] kernelNames = new String[] { "InitLoyalPoint", "InitResult", "ComputeLoyalNodesOfLeader" };

		CyCLProgram tryProgram;
		try {
			tryProgram = device.forceAddProgram("LoyalPoint2", getClass().getResource("/LoyalPoint2.cl"), kernelNames,
					null, false);
		} catch (Exception exc) {
			throw new RuntimeException("Could not load and compile OpenCL program.");
		}
		this.program = tryProgram;

		this.GLOBAL_WORK_SIZE = device.bestWarpSize * device.computeUnits;
		this.LOCAL_WORK_SIZE = device.bestWarpSize;

		this.network = network;
	}

	public String getDeviceInfo() {
		String info = "";
		info += "Address Bits: " + device.addressBits + "\n";
		info += "Best Block Size: " + device.bestBlockSize + "\n";
		info += "Best Warp Size: " + device.bestWarpSize + "\n";
		info += "Clock Frequency: " + device.clockFrequency + "\n";
		info += "Compute Units: " + device.computeUnits + "\n";
		info += "Global Mem Size: " + device.globalMemSize + "\n";
		info += "Local Mem Size: " + device.localMemSize + "\n";
		info += "Local Mem Type: " + device.localMemType + "\n";
		info += "Max Const Buffer Size: " + device.maxConstBufferSize + "\n";
		info += "Max Malloc Size: " + device.maxMallocSize + "\n";
		info += "Max Read Image Args: " + device.maxReadImageArgs + "\n";
		info += "Max Work Group Size: " + device.maxWorkGroupSize + "\n";
		info += "Max Write Image Args: " + device.maxWriteImageArgs + "\n";
		info += "Name: " + device.name + "\n";
		info += "Platform Name: " + device.platformName + "\n";
		info += "Pref Width Char: " + device.prefWidthChar + "\n";
		info += "Pref Width Double: " + device.prefWidthDouble + "\n";
		info += "Pref Width Float: " + device.prefWidthFloat + "\n";
		info += "Pref Width Int: " + device.prefWidthInt + "\n";
		info += "Pref Width Long: " + device.prefWidthLong + "\n";
		info += "Pref Width Short: " + device.prefWidthShort + "\n";
		info += "Vendor: " + device.vendor + "\n";
		info += "Version: " + device.version + "\n";
		info += "Work Item Dimensions: " + device.workItemDimensions + "\n";
		info += "Supports ECC: " + device.supportsECC + "\n";
		info += "Supports Images: " + device.supportsImages + "\n";
		return info;
	}

	public Map<Integer, Float> compute(int leader) {
		LoyalPoint lp = new LoyalPoint(network);
		return lp.compute(leader);
	}

	private class LoyalPoint {

		private final float EPS = 2 * 1e-7f;
		private final int MAX_ITERATION = 200;
		private int nodeCount;
		private float E;
		private int[] unDirectedAdjacentList;
		private int[] outDirectedAdjacentList;
		private int[] inDirectedAdjacentList;
		private int[] normalNode;
		private float[] loyalPoint;
		private int[] resultIndex;
		private float[] resultValue;

		// CyBuffers
		private CyCLBuffer bufferUnDirectedAdjacentList;
		private CyCLBuffer bufferInDirectedAdjacentList;
		private CyCLBuffer bufferOutDirectedAdjacentList;
		private CyCLBuffer bufferNormalNode;
		private CyCLBuffer bufferLoyalPoint;
		private CyCLBuffer bufferResultIndex;
		private CyCLBuffer bufferResultValue;

		private boolean buffersInitialized = false;

		public LoyalPoint(CyNetwork network) {
			this.nodeCount = network.getNodeCount();

			this.inDirectedAdjacentList = NetworkUtil.extractNetworkInDirectedAdjacentList(network);
			this.outDirectedAdjacentList = NetworkUtil.extractNetworkOutDirectedAdjacentList(network);
			this.unDirectedAdjacentList = NetworkUtil.extractNetworkUnDirectedAdjacentList(network);

			initialize();
		}

		private void initialize() {
			int maxOutDegMixing = Integer.MIN_VALUE;
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
			if (adjList[0] == -1) {
				return 0;
			}
			return adjList[node + 1] - adjList[node];
		}

		private void initializeBuffers() {

			bufferInDirectedAdjacentList = device.createBuffer(inDirectedAdjacentList);
			bufferOutDirectedAdjacentList = device.createBuffer(outDirectedAdjacentList);
			bufferUnDirectedAdjacentList = device.createBuffer(unDirectedAdjacentList);
			bufferNormalNode = device.createBuffer(normalNode);
			bufferLoyalPoint = device.createBuffer(loyalPoint);
			bufferResultIndex = device.createBuffer(resultIndex);
			bufferResultValue = device.createBuffer(resultValue);

			buffersInitialized = true;
		}

		private void freeBuffers() {
			if (!buffersInitialized) {
				return;
			}

			bufferInDirectedAdjacentList.free();
			bufferOutDirectedAdjacentList.free();
			bufferUnDirectedAdjacentList.free();
			bufferNormalNode.free();
			bufferLoyalPoint.free();
			bufferResultIndex.free();
			bufferResultValue.free();

			buffersInitialized = false;
		}

		public Map<Integer, Float> compute(int leader) {

			Map<Integer, Float> ans = new HashMap<>();

			int againstLeader = nodeCount;

			normalNode = new int[nodeCount - 1];
			int normalNodeIndex = 0;
			for (int node = 0; node < nodeCount; node++) {
				if (node != leader) {
					normalNode[normalNodeIndex++] = node;
				}
			}

			int[][] chunks = ArrayUtil.splitArrayIntoChunks(normalNode, (int) GLOBAL_WORK_SIZE);

			for (int[] chunk : chunks) {

				loyalPoint = new float[(nodeCount + 1) * chunk.length];
				resultIndex = new int[chunk.length];
				resultValue = new float[chunk.length];

				normalNode = chunk;

				initializeBuffers();

				program.getKernel("InitLoyalPoint").execute(new long[] { (nodeCount + 1) * chunk.length }, null,
						bufferLoyalPoint);

				program.getKernel("InitResult").execute(new long[] { chunk.length }, null, bufferResultIndex,
						bufferResultValue);

				program.getKernel("ComputeLoyalNodesOfLeader").execute(new long[] { GLOBAL_WORK_SIZE },
						new long[] { LOCAL_WORK_SIZE }, bufferInDirectedAdjacentList, bufferUnDirectedAdjacentList,
						bufferNormalNode, bufferLoyalPoint, bufferResultIndex, bufferResultValue, leader, againstLeader,
						nodeCount, E, EPS, MAX_ITERATION, chunk.length);

				bufferResultIndex.getFromDevice(resultIndex);
				bufferResultValue.getFromDevice(resultValue);

				for (int index = 0; index < chunk.length; index++) {
					if (resultValue[index] != -9999.0f) {
						ans.put(resultIndex[index], MathUtil.zero(resultValue[index], EPS));
					}
				}

				freeBuffers();
			}

			return ans;
		}
	}
}
