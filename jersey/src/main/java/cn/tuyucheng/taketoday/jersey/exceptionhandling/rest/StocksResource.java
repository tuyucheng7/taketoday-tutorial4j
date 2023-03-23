package cn.tuyucheng.taketoday.jersey.exceptionhandling.rest;

import java.util.Optional;

import cn.tuyucheng.taketoday.jersey.exceptionhandling.data.Stock;
import cn.tuyucheng.taketoday.jersey.exceptionhandling.repo.Db;
import cn.tuyucheng.taketoday.jersey.exceptionhandling.service.Repository;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/stocks")
public class StocksResource {
	private static final Db<Stock> stocks = Repository.STOCKS_DB;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response post(Stock stock) {
		stocks.save(stock);

		return Response.ok(stock)
			.build();
	}

	@GET
	@Path("/{ticker}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("ticker") String id) {
		Optional<Stock> stock = stocks.findById(id);
		stock.orElseThrow(() -> new IllegalArgumentException("ticker"));

		return Response.ok(stock.get())
			.build();
	}
}
