package cn.tuyucheng.taketoday.enummapping.controllers;

import cn.tuyucheng.taketoday.enummapping.editors.LevelEditor;
import cn.tuyucheng.taketoday.enummapping.enums.Level;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("enummapping")
public class EnumMappingController {

   @InitBinder
   public void initBinder(WebDataBinder dataBinder) {
      dataBinder.registerCustomEditor(Level.class, new LevelEditor());
   }

   @GetMapping("/get")
   public String getByLevel(@RequestParam(required = false) Level level) {
      if (level != null) {
         return level.name();
      }
      return "undefined";
   }
}