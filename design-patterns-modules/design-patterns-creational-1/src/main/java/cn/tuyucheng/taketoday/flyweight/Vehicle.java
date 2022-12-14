package cn.tuyucheng.taketoday.flyweight;

import java.awt.Color;

/**
 * Interface for a vehicle.
 *
 * @author tuyucheng
 */
public interface Vehicle {

	void start();

	void stop();

	Color getColor();
}