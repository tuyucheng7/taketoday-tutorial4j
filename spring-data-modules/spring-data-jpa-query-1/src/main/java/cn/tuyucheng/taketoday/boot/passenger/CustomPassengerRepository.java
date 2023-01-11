package cn.tuyucheng.taketoday.boot.passenger;

import java.util.List;

interface CustomPassengerRepository {

	List<Passenger> findOrderedBySeatNumberLimitedTo(int limit);
}