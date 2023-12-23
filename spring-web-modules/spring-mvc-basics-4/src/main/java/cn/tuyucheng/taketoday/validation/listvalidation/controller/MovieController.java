package cn.tuyucheng.taketoday.validation.listvalidation.controller;

import cn.tuyucheng.taketoday.validation.listvalidation.constraint.MaxSizeConstraint;
import cn.tuyucheng.taketoday.validation.listvalidation.model.Movie;
import cn.tuyucheng.taketoday.validation.listvalidation.service.MovieService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/movies")
public class MovieController {

   @Autowired
   private MovieService movieService;

   @PostMapping
   public void addAll(@RequestBody @NotEmpty(message = "Input movie list cannot be empty.") @MaxSizeConstraint List<@Valid Movie> movies) {
      movieService.addAll(movies);
   }
}