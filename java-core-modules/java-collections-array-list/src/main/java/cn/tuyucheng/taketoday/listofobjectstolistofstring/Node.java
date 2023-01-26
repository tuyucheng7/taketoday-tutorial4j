package cn.tuyucheng.taketoday.listofobjectstolistofstring;

public class Node {

    private final int x;
    private final int y;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Node (" + "x=" + x + ", y=" + y + ')';
    }
}
