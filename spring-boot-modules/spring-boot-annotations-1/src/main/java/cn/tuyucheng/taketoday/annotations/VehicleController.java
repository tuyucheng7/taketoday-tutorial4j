package cn.tuyucheng.taketoday.annotations;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/vehicles", method = RequestMethod.GET)
public class VehicleController {

   @CrossOrigin
   @ResponseBody
   @RequestMapping("/hello")
   public String hello() {
      return "Hello World!";
   }

   @RequestMapping("/home")
   public String home() {
      return "home";
   }

   @PostMapping("/save")
   public void saveVehicle(@RequestBody Vehicle vehicle) {
   }

   @RequestMapping("/{id}")
   public Vehicle getVehicle(@PathVariable("id") long id) {
      return null;
   }

   @RequestMapping
   public Vehicle getVehicleByParam(@RequestParam("id") long id) {
      return null;
   }

   @RequestMapping("/buy")
   public Car buyCar(@RequestParam(defaultValue = "5") int seatCount) {
      return null;
   }

   @ExceptionHandler(IllegalArgumentException.class)
   @ResponseStatus(HttpStatus.BAD_REQUEST)
   public void onIllegalArgumentException(IllegalArgumentException exception) {
   }

   @PostMapping("/assemble")
   public void assembleVehicle(@ModelAttribute("vehicle") Vehicle vehicle) {
   }

   @ModelAttribute("vehicle")
   public Vehicle getVehicle() {
      return null;
   }
}