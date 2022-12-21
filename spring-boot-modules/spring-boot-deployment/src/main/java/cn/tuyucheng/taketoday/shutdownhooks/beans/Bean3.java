package cn.tuyucheng.taketoday.shutdownhooks.beans;

public class Bean3 {

    public void destroy() {
        System.out.println("Shutdown triggered using bean destroy method.");
    }
}