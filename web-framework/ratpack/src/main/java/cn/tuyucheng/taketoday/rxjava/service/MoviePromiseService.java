package cn.tuyucheng.taketoday.rxjava.service;

import java.util.List;

import cn.tuyucheng.taketoday.model.Movie;

import ratpack.exec.Promise;

public interface MoviePromiseService {

	Promise<List<Movie>> getMovies();

	Promise<Movie> getMovie();

}
