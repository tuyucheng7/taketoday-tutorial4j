package cn.tuyucheng.taketoday.rxjava.service;

import cn.tuyucheng.taketoday.model.Movie;
import ratpack.exec.Promise;

import java.util.List;

public interface MoviePromiseService {

   Promise<List<Movie>> getMovies();

   Promise<Movie> getMovie();

}
