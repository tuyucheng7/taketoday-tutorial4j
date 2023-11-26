package cn.tuyucheng.taketoday.testcontainers.support;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("characters")
public class MiddleEarthCharactersController {
   private final MiddleEarthCharactersRepository repository;

   public MiddleEarthCharactersController(MiddleEarthCharactersRepository repository) {
      this.repository = repository;
   }

   @GetMapping
   public List<MiddleEarthCharacter> findByRace(@RequestParam String race) {
      return repository.findAllByRace(race);
   }

   @PostMapping
   public MiddleEarthCharacter save(@RequestBody MiddleEarthCharacter character) {
      return repository.save(character);
   }
}