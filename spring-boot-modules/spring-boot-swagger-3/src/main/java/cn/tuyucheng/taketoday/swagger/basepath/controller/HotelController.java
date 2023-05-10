package cn.tuyucheng.taketoday.swagger.basepath.controller;

import cn.tuyucheng.taketoday.swagger.basepath.model.Hotel;
import cn.tuyucheng.taketoday.swagger.basepath.service.HotelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/hotel")
@Api(tags = "Hotel")
public class HotelController {
   private final HotelService hotelService;

   @Autowired
   public HotelController(HotelService hotelService) {
      this.hotelService = hotelService;
   }

   @GetMapping
   @ResponseStatus(HttpStatus.OK)
   @ApiOperation(value = "Get Account Statement")
   public List<Hotel> getAllHotels() {
      return hotelService.getAllHotels();
   }
}