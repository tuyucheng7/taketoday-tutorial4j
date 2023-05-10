package cn.tuyucheng.taketoday.swagger.basepath.service;

import cn.tuyucheng.taketoday.swagger.basepath.model.Hotel;

import java.util.List;

public interface HotelService {
   List<Hotel> getAllHotels();

   List<Hotel> getHotelsByCity(Long cityId);

   Hotel createNewHotel(Hotel hotel);
}