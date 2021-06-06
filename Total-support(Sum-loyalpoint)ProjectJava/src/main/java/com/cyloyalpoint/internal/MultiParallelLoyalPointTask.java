package com.cyloyalpoint.internal;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.opencl.cycl.CyCLDevice;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskMonitor;

import com.cyloyalpoint.algorithm.BenchmarkKernel;
import com.cyloyalpoint.algorithm.MultiParallelLoyalPoint;
import com.cyloyalpoint.util.MapUtil;
import com.cyloyalpoint.util.StringUtil;

public class MultiParallelLoyalPointTask extends AbstractTask implements ObservableTask {

	private CyNetworkFactory networkFactory;
	private CyNetwork network;
	private List<CyCLDevice> devices;
	private File file;

	private boolean interrupted = false;

	private long executeTime;

	public MultiParallelLoyalPointTask(CyNetworkFactory networkFactory, CyNetwork network, List<CyCLDevice> devices, String folderName,
			String fileName) {
		this.networkFactory = networkFactory;
		this.network = network;
		this.devices = devices;

		if (folderName != null && fileName != null) {
			file = new File(new File(folderName), fileName);
		}
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		taskMonitor.setTitle("Computing loyal point...");

		taskMonitor.setStatusMessage("Running benchmark...");
		
		List<Long> benchMarks = new ArrayList<>();

		long tempMax = 0;
		for (CyCLDevice device : devices) {
			long benchMarkTime = new BenchmarkKernel(device).benchmarkComputeTime();
			taskMonitor.setStatusMessage("Running benchmark...\n" + device.name + " : " + benchMarkTime);
			benchMarks.add(benchMarkTime);

			if (benchMarkTime > tempMax) {
				tempMax = benchMarkTime;
			}
		}

		final long maxBenchMarkTime = tempMax;

		benchMarks = benchMarks.stream().map(time -> (maxBenchMarkTime / time) + (maxBenchMarkTime % time == 0 ? 0 : 1))
				.collect(Collectors.toList());

		taskMonitor.setStatusMessage("Initializing...");
		taskMonitor.setProgress(0.0);

		List<CyNode> nodes = network.getNodeList();
		CyTable nodeTable = network.getDefaultNodeTable();

		MultiParallelLoyalPoint plp = new MultiParallelLoyalPoint(network, devices, benchMarks);
		Map<CyNode, Integer> nodeIndexes = new HashMap<>();
		Map<Integer, CyNode> indexNodes = new HashMap<>();
		int nodeIndex = 0;
		for (CyNode node : nodes) {
			indexNodes.put(nodeIndex, node);
			nodeIndexes.put(node, nodeIndex++);
		}

		String columnName = "Sum Loyal Point";
		if (nodeTable.getColumn(columnName) == null) {
			nodeTable.createColumn(columnName, Double.class, false);
		}

		long startTime = System.currentTimeMillis();

		List<String> lines = new ArrayList<>();
		int count = 0;
		double percent;
		for (CyNode node : nodes) {
			if (interrupted) {
				break;
			}

			count++;
			if (count == 1) {
				percent = 1.0 * count / nodes.size();
				taskMonitor.setStatusMessage("Processing... " + count + "/" + nodes.size() + " ("
						+ BigDecimal.valueOf(percent * 100).setScale(2, RoundingMode.HALF_UP).doubleValue() + "%)"
						+ "\n" + "Remaining Time: estimating...");
				taskMonitor.setProgress(percent);
			}

			long startTimeNode = System.currentTimeMillis();

			lines.add(network.getRow(node).get("name", String.class) + "'s supporter:");

			Map<Integer, Float> result = plp.compute(nodeIndexes.get(node));
			Map<Integer, Float> sortedResult = MapUtil.sortIntegerFloatMapByValue(result, false);

			double sum = 0.0;
			for (Map.Entry<Integer, Float> entry : sortedResult.entrySet()) {
				lines.add(network.getRow(indexNodes.get(entry.getKey())).get("name", String.class) + "\t"
						+ sortedResult.get(entry.getKey()));
				sum += sortedResult.get(entry.getKey());
			}
			network.getRow(node).set(columnName, sum);

			if (file != null) {
				FileUtils.writeLines(file, lines, true);
			}

			lines.clear();

			long elapsedTime = System.currentTimeMillis() - startTimeNode;
			long allTimeForDownloading = (elapsedTime * nodes.size()) - (elapsedTime * count);
			long remainingTime = allTimeForDownloading - elapsedTime;

			percent = 1.0 * count / nodes.size();
			taskMonitor.setStatusMessage("Processing... " + (count + 1) + "/" + nodes.size() + " ("
					+ BigDecimal.valueOf(percent * 100).setScale(2, RoundingMode.HALF_UP).doubleValue() + "%)" + "\n"
					+ "Remaining Time: " + StringUtil.getDurationBreakdown(remainingTime));
			taskMonitor.setProgress(percent);

//			 break;
		}

		long endTime = System.currentTimeMillis();

		executeTime = endTime - startTime;

		taskMonitor.setStatusMessage("Done.");
		taskMonitor.setProgress(1.0);

		Thread.sleep(1000);
	}

	@Override
	public void cancel() {
		super.cancel();
		this.interrupted = true;
	}

	@Override
	public <R> R getResults(Class<? extends R> type) {
		if (Long.class.equals(type)) {
			return type.cast(executeTime);
		}
		return null;
	}
}
