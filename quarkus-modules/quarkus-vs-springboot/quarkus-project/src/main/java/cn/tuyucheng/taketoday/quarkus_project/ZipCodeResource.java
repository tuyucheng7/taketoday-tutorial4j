package cn.tuyucheng.taketoday.quarkus_project;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import javax.persistence.PersistenceException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/zipcode")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ZipCodeResource {

	private ZipCodeRepo zipRepo;

	public ZipCodeResource(ZipCodeRepo zipRepo) {
		this.zipRepo = zipRepo;
	}

	@GET
	@Path("/{zipcode}")
	public Uni<ZipCode> findById(@PathParam("zipcode") String zipcode) {
		return getById(zipcode);
	}

	@GET
	@Path("/by_city")
	public Multi<ZipCode> postZipCode(@QueryParam("city") String city) {
		return zipRepo.findByCity(city);
	}

	@POST
	public Uni<ZipCode> create(ZipCode zipCode) {
		return getById(zipCode.getZip())
			.onItem()
			.ifNull()
			.switchTo(createZipCode(zipCode))
			.onFailure(PersistenceException.class)
			.recoverWithUni(() -> getById(zipCode.getZip()));
	}

	private Uni<ZipCode> getById(String zipCode) {
		return zipRepo.findById(zipCode);
	}

	private Uni<ZipCode> createZipCode(ZipCode zipCode) {
		return Uni.createFrom().deferred(() -> zipRepo.save(zipCode));
	}
}