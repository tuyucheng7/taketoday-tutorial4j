package cn.tuyucheng.taketoday.caching.ttl.controller;

import cn.tuyucheng.taketoday.caching.ttl.model.Hotel;
import cn.tuyucheng.taketoday.caching.ttl.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/hotel")
public class HotelController {
	private final HotelService hotelService;

	@Autowired
	public HotelController(HotelService hotelService) {
		this.hotelService = hotelService;
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<Hotel> getAllHotels() {
		return hotelService.getAllHotels();
	}
}