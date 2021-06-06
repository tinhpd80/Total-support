package com.cyloyalpoint.algorithm;

import java.util.Random;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.opencl.cycl.CyCLBuffer;
import org.cytoscape.opencl.cycl.CyCLDevice;
import org.cytoscape.opencl.cycl.CyCLException;
import org.cytoscape.opencl.cycl.CyCLProgram;

public class BenchmarkKernel {

	private final CyCLDevice device;
	private final CyCLProgram program;

	public BenchmarkKernel(CyCLDevice device) {
		try {
			this.device = device;
		} catch (Exception e) {
			throw new RuntimeException("No OpenCL devices found, cannot run program.");
		}

		String[] kernelNames = new String[] { "BenchmarkKernel" };

		CyCLProgram tryProgram;
		try {
			tryProgram = device.forceAddProgram("BenchmarkKernel", getClass().getResource("/Benchmark.cl"), kernelNames,
					null, false);
		} catch (Exception exc) {
			throw new RuntimeException("Could not load and compile OpenCL program.");
		}
		this.program = tryProgram;
	}

	public long benchmarkComputeTime() {
		int n = 8192;
		int[] a = new int[n];
		int[] b = new int[n];
		int[] c = new int[n];

		int index = 0;
		while (index < n) {
			a[index] = index;
			b[index] = index++;
		}

		CyCLBuffer bufferA = device.createBuffer(a);
		CyCLBuffer bufferB = device.createBuffer(b);
		CyCLBuffer bufferC = device.createBuffer(c);

		long startTime = System.currentTimeMillis();

		try {
			program.getKernel("BenchmarkKernel").execute(new long[] { n }, null, bufferA, bufferB, bufferC, n);
			bufferC.getFromDevice(c);

			bufferA.free();
			bufferB.free();
			bufferC.free();
		} catch (Exception e) {
			try {
				bufferA.free();
				bufferB.free();
				bufferC.free();
				throw e;
			} catch (Exception ex) {
				throw new CyCLException("Error running benchmark", ex);
			}
		}

		long endTime = System.currentTimeMillis();

		return endTime - startTime;
	}
	
	public long benchmarkComputeTime(CyNetwork network) {
		long startTime = System.currentTimeMillis();
		
		new ParallelLoyalPoint2(network).compute(new Random().nextInt(network.getNodeCount()));
		
		long endTime = System.currentTimeMillis();

		return endTime - startTime;
	}
}
