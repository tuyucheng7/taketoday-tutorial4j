package cn.tuyucheng.taketoday.features.records;

public record Location(String name, GPSPoint gpsPoint) implements ILocation {
}