package cn.tuyucheng.taketoday.rxjava.service;

import cn.tuyucheng.taketoday.model.Movie;

import rx.Observable;

public interface MovieObservableService {

	Observable<Movie> getMovies();

	Observable<Movie> getMovie();

}
