package cn.tuyucheng.taketoday.api;

public interface CabBookingService {
	Booking bookRide(String pickUpLocation) throws BookingException;
}
