package cn.tuyucheng.taketoday.sampleapp.web.dto;

public class TuyuchengItem {
    private final String itemId;

    public TuyuchengItem(String itemId) {
        this.itemId = itemId;
    }

    public String getItemId() {
        return itemId;
    }
}