package cn.tuyucheng.taketoday.sampleapp.web.dto;


public class TuyuchengItemV2 {
    private final String itemName;

    public TuyuchengItemV2(String itemName) {
        this.itemName = itemName;
    }

    public String getItemName() {
        return itemName;
    }
}