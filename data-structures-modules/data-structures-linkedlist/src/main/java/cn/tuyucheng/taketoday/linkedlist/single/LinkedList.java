package cn.tuyucheng.taketoday.linkedlist.single;

public class LinkedList {
    static int frequency = 0;
    public Node head;

    public static int countOccursTimeUsingRecursive(Node head, int key) {
        if (head == null)
            return frequency;
        if (head.data == key)
            frequency++;
        return countOccursTimeUsingRecursive(head.next, key);
    }

    public void push(int newData) {
        Node newNode = new Node(newData);
        newNode.next = head;
        head = newNode;
    }

    public void printList(Node head) {
        Node current = head;
        while (current != null) {
            System.out.print(current.data + " ");
            current = current.next;
        }
    }

    public void insertAfter(Node preNode, int newData) {
        if (preNode == null)
            return;
        Node newNode = new Node(newData);
        newNode.next = preNode.next;
        preNode.next = newNode;
    }

    public void append(int newData) {
        Node newNode = new Node(newData);
        if (head == null) {
            head = new Node(newData);
            return;
        }
        newNode.next = null;
        Node last = head;
        while (last.next != null)
            last = last.next;
        last.next = newNode;
    }

    public void delete(int key) {
        Node temp = head, previous = null;
        if (temp != null && temp.data == key) {
            head = temp.next;
            return;
        }
        while (temp != null && temp.data != key) {
            previous = temp;
            temp = temp.next;
        }
        if (temp == null)
            return;
        previous.next = temp.next;
    }

    public void deleteNode(int position) {
        if (head == null)
            return;
        Node temp = head;
        if (position == 0) {
            head = temp.next;
            return;
        }
        for (int i = 0; temp != null && i < position - 1; i++)
            temp = temp.next;
        if (temp == null || temp.next == null)
            return;
        temp.next = temp.next.next;
    }

    public int getCountUsingIterative() {
        int count = 0;
        Node current = head;
        while (current != null) {
            count++;
            current = current.next;
        }
        return count;
    }

    public int getCountUsingRecursive(Node head) {
        if (head == null)
            return 0;
        return 1 + getCountUsingRecursive(head.next);
    }

    public boolean searchUsingIterative(Node head, int key) {
        Node current = head;
        while (current != null) {
            if (current.data == key)
                return true;
            current = current.next;
        }
        return false;
    }

    public boolean searchUsingRecursive(Node head, int key) {
        if (head == null)
            return false;
        if (head.data == key)
            return true;
        return searchUsingRecursive(head.next, key);
    }

    public int GetNth(int index) {
        Node current = head;
        int count = 0;
        while (current != null) {
            if (count == index)
                return current.data;
            count++;
            current = current.next;
        }
        assert (false);
        return 0;
    }

    public int getNthUsingRecursive(Node head, int n) {
        int count = 0;
        if (head == null)
            return -1;
        if (count == n)
            return head.data;
        else return getNthUsingRecursive(head.next, n - 1);
    }

    public int getNthFromLast(int n) {
        int len = 0;
        Node temp = head;
        while (temp != null) {
            len++;
            temp = temp.next;
        }
        if (n > len)
            return -1;
        temp = head;
        for (int i = 1; i < len - n + 1; i++)
            temp = temp.next;
        return temp.data;
    }

    public int getNthFromLastUsingTwoPointer(int n) {
        Node refPointer = head;
        Node mainPointer = head;
        int count = 0;
        if (head != null) {
            while (count < n) {
                if (refPointer == null)
                    return -1;
                refPointer = refPointer.next;
                count++;
            }
            while (refPointer != null) {
                mainPointer = mainPointer.next;
                refPointer = refPointer.next;
            }
            return mainPointer.data;
        }
        return -1;
    }

    public int getMiddleNodeUsingRecursive(Node head) {
        int nodeCount = getCountUsingIterative();
        Node current = head;
        int count = 0;
        while (count < nodeCount / 2) {
            current = current.next;
            count++;
        }
        return current.data;
    }

    public int getMiddleNodeUsingTwoPointer(Node head) {
        if (head == null)
            return -1;
        Node slowPointer = head;
        Node fastPointer = head;
        while (fastPointer != null && fastPointer.next != null) {
            fastPointer = fastPointer.next.next;
            slowPointer = slowPointer.next;
        }
        return slowPointer.data;
    }

    public int getMiddleNodeUsingOtherMethod(Node head) {
        if (head == null)
            return -1;
        Node mid = head;
        int count = 0;
        while (head != null) {
            if (count % 2 == 1)
                mid = mid.next;
            count++;
            head = head.next;
        }
        return mid.data;
    }

    public int countOccursTime(int key) {
        Node current = head;
        int count = 0;
        while (current != null) {
            if (key == current.data)
                count++;
            current = current.next;
        }
        return count;
    }

    public int countOccursTimeUsingRecursiveWithGloabl(Node head, int key) {
        if (head == null)
            return 0;
        if (head.data == key)
            return 1 + countOccursTimeUsingRecursiveWithGloabl(head.next, key);
        return countOccursTimeUsingRecursiveWithGloabl(head.next, key);
    }
}