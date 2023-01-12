package cn.tuyucheng.taketoday.common.web;

import cn.tuyucheng.taketoday.persistence.model.Foo;
import cn.tuyucheng.taketoday.web.util.HTTPLinkHeaderUtil;
import com.google.common.net.HttpHeaders;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.core.AnyOf;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.io.Serializable;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public abstract class AbstractDiscoverabilityLiveTest<T extends Serializable> extends AbstractLiveTest<T> {

	public AbstractDiscoverabilityLiveTest(final Class<T> clazzToSet) {
		super(clazzToSet);
	}

	// tests

	// discoverability

	@Test
	public void whenInvalidPOSTIsSentToValidURIOfResource_thenAllowHeaderListsTheAllowedActions() {
		// Given
		final String uriOfExistingResource = createAsUri();

		// When
		final Response res = RestAssured.post(uriOfExistingResource);

		// Then
		final String allowHeader = res.getHeader(HttpHeaders.ALLOW);
		assertThat(allowHeader, AnyOf.anyOf(containsString("GET"), containsString("PUT"), containsString("DELETE")));
	}

	@Test
	public void whenResourceIsCreated_thenUriOfTheNewlyCreatedResourceIsDiscoverable() {
		// When
		final Foo newResource = new Foo(randomAlphabetic(6));
		final Response createResp = RestAssured.given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(newResource)
			.post(getURL());
		final String uriOfNewResource = createResp.getHeader(HttpHeaders.LOCATION);

		// Then
		final Response response = RestAssured.given()
			.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
			.get(uriOfNewResource);

		final Foo resourceFromServer = response.body().as(Foo.class);
		assertThat(newResource, equalTo(resourceFromServer));
	}

	@Test
	public void whenResourceIsRetrieved_thenUriToGetAllResourcesIsDiscoverable() {
		// Given
		final String uriOfExistingResource = createAsUri();

		// When
		final Response getResponse = RestAssured.get(uriOfExistingResource);

		// Then
		final String uriToAllResources = HTTPLinkHeaderUtil.extractURIByRel(getResponse.getHeader("Link"), "collection");

		final Response getAllResponse = RestAssured.get(uriToAllResources);
		assertThat(getAllResponse.getStatusCode(), is(200));
	}

	// template method

}
