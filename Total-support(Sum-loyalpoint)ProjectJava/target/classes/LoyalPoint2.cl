// OpenCL program
// Author: Kien Tran Dong

inline int GetIndex(int size, int threadId, int index);
inline float ComputeSum(__global const int* inDirectedAdjacentList,
		__global const int* unDirectedAdjacentList,
		__global float* loyalPoint,
		int currentNode,
		int againstLeader,
		int targetNode,
		int nodeCount,
		int currentThreadId);

__kernel void InitLoyalPoint(__global float* loyalPoint)
{
	int id = get_global_id(0);
	loyalPoint[id] = 0.0f;
}

__kernel void InitResult(__global int* resultIndex, __global float* resultValue)
{
	int id = get_global_id(0);
	resultIndex[id] = -1;
	resultValue[id] = -9999.0f;
}

__kernel void ComputeLoyalNodesOfLeader(__global const int* inDirectedAdjacentList,
		__global const int* unDirectedAdjacentList,
		__global const int* normalNode,
		__global float* loyalPoint,
		__global int* resultIndex,
		__global float* resultValue,
		int leader,
		int againstLeader,
		int nodeCount,
		float E,
		float EPS,
		int MAX_ITERATION,
		int n)
{
	int currentThreadId = get_global_id(0);

	if (currentThreadId >= n) {
		return;
	}

	int targetNode = normalNode[currentThreadId];

	loyalPoint[GetIndex(nodeCount + 1, currentThreadId, leader)] = 1;
	loyalPoint[GetIndex(nodeCount + 1, currentThreadId, againstLeader)] = -1;

	bool isConverged = true;
	int iter = 1;

	do {
		isConverged = true;
		for (int currentNode = 0; currentNode < nodeCount + 1; currentNode++)
		{
			float currentLoyalPoint = loyalPoint[GetIndex(nodeCount + 1, currentThreadId, currentNode)];

			if (currentNode == leader || currentNode == againstLeader) {
				continue;
			}

			float sum = ComputeSum(inDirectedAdjacentList, unDirectedAdjacentList, loyalPoint, currentNode, againstLeader, targetNode, nodeCount, currentThreadId);

			float newLoyalPoint = currentLoyalPoint + E * sum;

			loyalPoint[GetIndex(nodeCount + 1, currentThreadId, currentNode)] = newLoyalPoint;

			if (fabs(newLoyalPoint - currentLoyalPoint) > EPS)
			{
				isConverged = false;
			}
		}

		iter++;
	}while (iter <= MAX_ITERATION && !isConverged);

	resultIndex[currentThreadId] = targetNode;
	resultValue[currentThreadId] = loyalPoint[GetIndex(nodeCount + 1, currentThreadId, targetNode)];
}

inline int GetIndex(int size, int threadId, int index) {
	return size * threadId + index;
}

inline float ComputeSum(__global const int* inDirectedAdjacentList,
		__global const int* unDirectedAdjacentList,
		__global float* loyalPoint,
		int currentNode,
		int againstLeader,
		int targetNode,
		int nodeCount,
		int currentThreadId)
{
	float currentLoyalPoint = loyalPoint[GetIndex(nodeCount + 1, currentThreadId, currentNode)];

	float sum = 0.0f;

	int nInDirectedList = inDirectedAdjacentList[0] == -1 ? 0 : inDirectedAdjacentList[currentNode + 1] - inDirectedAdjacentList[currentNode];
	for (int index = 0; index < nInDirectedList; index++) {
		int neighbourNode = inDirectedAdjacentList[inDirectedAdjacentList[currentNode] + index];
		float weight = 1.0f;

		sum += weight * (loyalPoint[GetIndex(nodeCount + 1, currentThreadId, neighbourNode)] - currentLoyalPoint);
	}

	int nUnDirectedList = unDirectedAdjacentList[0] == -1 ? 0 : unDirectedAdjacentList[currentNode + 1] - unDirectedAdjacentList[currentNode];
	for (int index = 0; index < nUnDirectedList; index++) {
		int neighbourNode = unDirectedAdjacentList[unDirectedAdjacentList[currentNode] + index];
		float weight = 1.0f;

		sum += weight * (loyalPoint[GetIndex(nodeCount + 1, currentThreadId, neighbourNode)] - currentLoyalPoint);
	}

	if (currentNode == targetNode) {
		int neighbourNode = againstLeader;
		float weight = 1.0f;

		sum += weight * (loyalPoint[GetIndex(nodeCount + 1, currentThreadId, neighbourNode)] - currentLoyalPoint);
	}

	return sum;
}
