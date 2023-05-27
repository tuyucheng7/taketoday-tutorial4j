package cn.tuyucheng.taketoday.resource.web.controller;

import cn.tuyucheng.taketoday.resource.persistence.model.Foo;
import cn.tuyucheng.taketoday.resource.service.IFooService;
import cn.tuyucheng.taketoday.resource.web.dto.FooDto;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping(value = "/api/foos")
public class FooController {

   private IFooService fooService;

   public FooController(IFooService fooService) {
      this.fooService = fooService;
   }

   @CrossOrigin(origins = "http://localhost:8089")
   @GetMapping(value = "/{id}")
   public FooDto findOne(@PathVariable Long id) {
      Foo entity = fooService.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
      return convertToDto(entity);
   }

   @ResponseStatus(HttpStatus.CREATED)
   @PostMapping
   public void create(@RequestBody FooDto newFoo) {
      Foo entity = convertToEntity(newFoo);
      this.fooService.save(entity);
   }

   @GetMapping
   public Collection<FooDto> findAll() {
      Iterable<Foo> foos = this.fooService.findAll();
      List<FooDto> fooDtos = new ArrayList<>();
      foos.forEach(p -> fooDtos.add(convertToDto(p)));
      return fooDtos;
   }

   @PutMapping("/{id}")
   public FooDto updateFoo(@PathVariable("id") Long id, @RequestBody FooDto updatedFoo) {
      Foo fooEntity = convertToEntity(updatedFoo);
      return this.convertToDto(this.fooService.save(fooEntity));
   }

   protected FooDto convertToDto(Foo entity) {
      FooDto dto = new FooDto(entity.getId(), entity.getName());

      return dto;
   }

   protected Foo convertToEntity(FooDto dto) {
      Foo foo = new Foo(dto.getName());
      if (!StringUtils.isEmpty(dto.getId())) {
         foo.setId(dto.getId());
      }
      return foo;
   }
}