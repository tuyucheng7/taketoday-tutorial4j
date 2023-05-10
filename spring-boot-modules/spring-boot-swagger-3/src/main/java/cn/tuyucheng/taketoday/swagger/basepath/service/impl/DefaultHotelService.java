package cn.tuyucheng.taketoday.swagger.basepath.service.impl;

import cn.tuyucheng.taketoday.swagger.basepath.exception.BadRequestException;
import cn.tuyucheng.taketoday.swagger.basepath.model.Hotel;
import cn.tuyucheng.taketoday.swagger.basepath.repository.HotelRepository;
import cn.tuyucheng.taketoday.swagger.basepath.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
class DefaultHotelService implements HotelService {
   private final HotelRepository hotelRepository;

   @Autowired
   DefaultHotelService(HotelRepository hotelRepository) {
      this.hotelRepository = hotelRepository;
   }

   @Override
   public List<Hotel> getAllHotels() {
      return hotelRepository.findAll();
   }

   @Override
   public List<Hotel> getHotelsByCity(Long cityId) {
      return hotelRepository.findAll().stream()
            .filter((hotel) -> cityId.equals(hotel.getCity().getId()))
            .collect(Collectors.toList());
   }

   @Override
   public Hotel createNewHotel(Hotel hotel) {
      if (hotel.getId() != null)
         throw new BadRequestException("The ID must not be provided when creating a new Hotel");
      return hotelRepository.save(hotel);
   }
}