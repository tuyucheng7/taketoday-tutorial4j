package cn.tuyucheng.taketoday.features;

import jdk.incubator.concurrent.StructuredTaskScope;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class JEP428StructuredConcurrencyUnitTest {

	private static final String ERROR_MESSAGE = "Failed to get the result";

	@Test
	void givenStructuredConcurrency_whenThrowingException_thenCorrect() {
		assertThatThrownBy(() -> {
			try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
				Future<Shelter> shelter = scope.fork(this::getShelter);
				Future<List<Dog>> dogs = scope.fork(this::getDogsWithException);
				scope.throwIfFailed(e -> new RuntimeException(ERROR_MESSAGE));
				scope.join();
				Response response = new Response(shelter.resultNow(), dogs.resultNow());

				assertThat(response).isNotNull();
				assertThat(response.shelter()).isNotNull();
				assertThat(response.dogs()).isNotNull();
				assertThat(response.dogs()).hasSize(2);
			}
		}).isInstanceOf(RuntimeException.class)
			.hasMessage(ERROR_MESSAGE);
	}

	@Test
	void givenStructuredConcurrency_whenSlowTasksReachesDeadline_thenCorrect() {
		assertThatThrownBy(() -> {
			try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
				Future<Shelter> shelter = scope.fork(this::getShelter);
				Future<List<Dog>> dogs = scope.fork(this::getDogsSlowly);
				scope.throwIfFailed(e -> new RuntimeException(ERROR_MESSAGE));
				scope.join();
				scope.joinUntil(Instant.now().plusMillis(50));
				Response response = new Response(shelter.resultNow(), dogs.resultNow());

				assertThat(response).isNotNull();
				assertThat(response.shelter()).isNotNull();
				assertThat(response.dogs()).isNotNull();
				assertThat(response.dogs()).hasSize(2);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}).isInstanceOf(IllegalStateException.class);
	}

	@Test
	void givenStructuredConcurrency_whenResultNow_thenCorrect() {
		try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
			Future<Shelter> shelter = scope.fork(this::getShelter);
			Future<List<Dog>> dogs = scope.fork(this::getDogs);
			scope.join();

			Response response = new Response(shelter.resultNow(), dogs.resultNow());

			assertThat(response).isNotNull();
			assertThat(response.shelter()).isNotNull();
			assertThat(response.dogs()).isNotNull();
			assertThat(response.dogs()).hasSize(2);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	void givenUnstructuredConcurrency_whenGet_thenCorrect() {
		Future<Shelter> shelter;
		Future<List<Dog>> dogs;
		try (ExecutorService executorService = Executors.newFixedThreadPool(3)) {
			shelter = executorService.submit(this::getShelter);
			dogs = executorService.submit(this::getDogs);
			Shelter theShelter = shelter.get();   // Join the shelter
			List<Dog> theDogs = dogs.get();  // Join the dogs
			Response response = new Response(theShelter, theDogs);

			assertThat(response).isNotNull();
			assertThat(response.shelter()).isNotNull();
			assertThat(response.dogs()).isNotNull();
			assertThat(response.dogs()).hasSize(2);
		} catch (ExecutionException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	void givenCustomStructuredConcurrency_whenFirstTaskCompleted_thenOtherTasksCanceled() throws InterruptedException {
		try (var scope = new CriteriaScope()) {
			scope.fork(this::getProduct50);
			scope.fork(this::getProduct200);
			scope.fork(this::getProduct150);

			scope.join();

			assertThat(scope.getResult().price()).isEqualTo(50);
		}
	}

	private Shelter getShelter() {
		return new Shelter("Shelter");
	}

	private List<Dog> getDogs() {
		return List.of(new Dog("Buddy"), new Dog("Simba"));
	}

	private List<Dog> getDogsWithException() {
		throw new RuntimeException(ERROR_MESSAGE);
	}

	private List<Dog> getDogsSlowly() throws InterruptedException {
		Thread.sleep(1500);
		throw new RuntimeException(ERROR_MESSAGE);
	}

	record Shelter(String name) {
	}

	record Dog(String name) {
	}

	record Response(Shelter shelter, List<Dog> dogs) {
	}

	private Product getProduct50() {
		return new Product(50);
	}

	private Product getProduct150() {
		return new Product(150);
	}

	private Product getProduct200() {
		return new Product(200);
	}

	record Product(int price) {
	}

	static class CriteriaScope extends StructuredTaskScope<Product> {

		private volatile Product product;

		@Override
		protected void handleComplete(Future<Product> future) {
			if (future.state() == Future.State.SUCCESS && future.resultNow().price() < 100) {
				this.product = future.resultNow();
				shutdown();
			}
		}

		public Product getResult() {
			return product;
		}
	}
}