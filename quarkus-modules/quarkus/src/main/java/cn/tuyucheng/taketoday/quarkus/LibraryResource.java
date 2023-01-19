package cn.tuyucheng.taketoday.quarkus;

import cn.tuyucheng.taketoday.quarkus.model.Book;
import cn.tuyucheng.taketoday.quarkus.service.LibraryService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Set;

@Path("/library")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LibraryResource {

	@Inject
	LibraryService libraryService;

	@GET
	@Path("/book")
	public Set<Book> findBooks(@QueryParam("query") String query) {
		return libraryService.find(query);
	}
}