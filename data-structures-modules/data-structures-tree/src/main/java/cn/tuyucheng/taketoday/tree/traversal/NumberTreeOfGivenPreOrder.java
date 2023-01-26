package cn.tuyucheng.taketoday.tree.traversal;

public class NumberTreeOfGivenPreOrder {

    public int countTrees(int n) {
        int[] BT = new int[n + 1];
        for (int i = 0; i <= n; i++)
            BT[i] = 0;
        BT[0] = BT[1] = 1;
        for (int i = 2; i <= n; i++)
            for (int j = 0; j < i; j++)
                BT[i] += BT[j] * BT[i - j - 1];
        return BT[n];
    }
}