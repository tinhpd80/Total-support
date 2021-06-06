__kernel void ComputeSums(__global const int* inDirectedAdjacentList,
                          __global const int* unDirectedAdjacentList,
                          __global float* loyalPoint,
                          __global float* sums,
                          int leader,
                          int againstLeader,
                          int targetNode,
                          int n)
{
    int currentNode = get_global_id(0);
    
    if(currentNode >= n || currentNode == leader || currentNode == againstLeader)
    {
        return;
    }
    
    float sum = 0.0f;

    int nInDirectedList = inDirectedAdjacentList[0] == -1 ? 0 : inDirectedAdjacentList[currentNode + 1] - inDirectedAdjacentList[currentNode];
    for (int index = 0; index < nInDirectedList; index++) {
        int neighbourNode = inDirectedAdjacentList[inDirectedAdjacentList[currentNode] + index];
        float weight = 1.0f;

        sum += weight * (loyalPoint[neighbourNode] - loyalPoint[currentNode]);
    }

    int nUnDirectedList = unDirectedAdjacentList[0] == -1 ? 0 : unDirectedAdjacentList[currentNode + 1] - unDirectedAdjacentList[currentNode];
    for (int index = 0; index < nUnDirectedList; index++) {
        int neighbourNode = unDirectedAdjacentList[unDirectedAdjacentList[currentNode] + index];
        float weight = 1.0f;

        sum += weight * (loyalPoint[neighbourNode] - loyalPoint[currentNode]);
    }
    
    if (currentNode == targetNode) {
        int neighbourNode = againstLeader;
        float weight = 1.0f;
        
        sum += weight * (loyalPoint[neighbourNode] - loyalPoint[currentNode]);
    }
    
    sums[currentNode] = sum;
}

__kernel void ComputeLoyalPoint(__global float* loyalPoint,
                                __global float* sums,
                                __global float* errors,
                                int leader,
                                int againstLeader,
                                float E,
                                int n)
{
    int currentNode = get_global_id(0);
    
    if(currentNode >= n || currentNode == leader || currentNode == againstLeader)
    {
        return;
    }
    
    float oldLoyalPoint = loyalPoint[currentNode];
    float newLoyalPoint = oldLoyalPoint + E * sums[currentNode];
    
    errors[currentNode] = fabs(newLoyalPoint - oldLoyalPoint) > errors[currentNode] ? fabs(newLoyalPoint - oldLoyalPoint) : errors[currentNode];
    loyalPoint[currentNode] = newLoyalPoint;
}
