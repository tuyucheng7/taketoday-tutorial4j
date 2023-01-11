package cn.tuyucheng.taketoday.joins;

import cn.tuyucheng.taketoday.joins.model.Department;
import cn.tuyucheng.taketoday.joins.model.Phone;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest(showSql = false)
@ActiveProfiles("joins")
class JpaJoinsIntegrationTest {

	@PersistenceContext
	private EntityManager entityManager;

	@Test
	void whenPathExpressionIsUsedForSingleValuedAssociation_thenCreatesImplicitInnerJoin() {
		TypedQuery<Department> query = entityManager.createQuery("SELECT e.department FROM Employee e", Department.class);

		List<Department> resultList = query.getResultList();

		assertThat(resultList).hasSize(3);
		assertThat(resultList).extracting("name")
				.containsOnly("Infra", "Accounting", "Accounting");
	}

	@Test
	void whenJoinKeywordIsUsed_thenCreatesExplicitInnerJoin() {
		TypedQuery<Department> query = entityManager.createQuery("SELECT d FROM Employee e JOIN e.department d", Department.class);

		List<Department> resultList = query.getResultList();

		assertThat(resultList).hasSize(3);
		assertThat(resultList).extracting("name")
				.containsOnly("Infra", "Accounting", "Accounting");
	}

	@Test
	void whenInnerJoinKeywordIsUsed_thenCreatesExplicitInnerJoin() {
		TypedQuery<Department> query = entityManager.createQuery("SELECT d FROM Employee e INNER JOIN e.department d", Department.class);

		List<Department> resultList = query.getResultList();

		assertThat(resultList).hasSize(3);
		assertThat(resultList).extracting("name")
				.containsOnly("Infra", "Accounting", "Accounting");
	}

	@Test
	void whenEntitiesAreListedInFromAndMatchedInWhere_ThenCreatesJoin() {
		TypedQuery<Department> query = entityManager.createQuery("SELECT d FROM Employee e, Department d WHERE e.department = d", Department.class);

		List<Department> resultList = query.getResultList();

		assertThat(resultList).hasSize(3);
		assertThat(resultList).extracting("name")
				.containsOnly("Infra", "Accounting", "Accounting");
	}

	@Test
	void whenEntitiesAreListedInFrom_ThenCreatesCartesianProduct() {
		TypedQuery<Department> query = entityManager.createQuery("SELECT d FROM Employee e, Department d", Department.class);

		List<Department> resultList = query.getResultList();

		assertThat(resultList).hasSize(9);
		assertThat(resultList).extracting("name")
				.containsOnly("Infra", "Accounting", "Management", "Infra", "Accounting", "Management", "Infra", "Accounting", "Management");
	}

	@Test
	void whenCollectionValuedAssociationIsJoined_ThenCanSelect() {
		TypedQuery<Phone> query = entityManager.createQuery("SELECT ph FROM Employee e JOIN e.phones ph WHERE ph LIKE '1%'", Phone.class);

		List<Phone> resultList = query.getResultList();

		assertThat(resultList).hasSize(1);
	}

	@Test
	void whenMultipleEntitiesAreListedWithJoin_ThenCreatesMultipleJoins() {
		TypedQuery<Phone> query = entityManager.createQuery("SELECT ph FROM Employee e JOIN e.department d JOIN e.phones ph WHERE d.name IS NOT NULL", Phone.class);

		List<Phone> resultList = query.getResultList();

		assertThat(resultList).hasSize(3);
		assertThat(resultList).extracting("number")
				.containsOnly("111", "222", "333");
	}

	@Test
	void whenLeftKeywordIsSpecified_thenCreatesOuterJoinAndIncludesNonMatched() {
		TypedQuery<Department> query = entityManager.createQuery("SELECT DISTINCT d FROM Department d LEFT JOIN d.employees e", Department.class);

		List<Department> resultList = query.getResultList();

		assertThat(resultList).hasSize(3);
		assertThat(resultList).extracting("name")
				.containsOnly("Infra", "Accounting", "Management");
	}

	@Test
	void whenFetchKeywordIsSpecified_ThenCreatesFetchJoin() {
		TypedQuery<Department> query = entityManager.createQuery("SELECT d FROM Department d JOIN FETCH d.employees", Department.class);

		List<Department> resultList = query.getResultList();

		assertThat(resultList).hasSize(3);
		assertThat(resultList).extracting("name")
				.containsOnly("Infra", "Accounting", "Accounting");
	}

	@Test
	void whenLeftAndFetchKeywordsAreSpecified_ThenCreatesOuterFetchJoin() {
		TypedQuery<Department> query = entityManager.createQuery("SELECT d FROM Department d LEFT JOIN FETCH d.employees", Department.class);

		List<Department> resultList = query.getResultList();

		assertThat(resultList).hasSize(4);
		assertThat(resultList).extracting("name")
				.containsOnly("Infra", "Accounting", "Accounting", "Management");
	}

	@Test
	void whenCollectionValuedAssociationIsSpecifiedInSelect_ThenReturnsCollections() {
		TypedQuery<Collection> query = entityManager.createQuery("SELECT e.phones FROM Employee e", Collection.class);

		List<Collection> resultList = query.getResultList();

		assertThat(resultList).extracting("number").containsOnly("111", "222", "333");
	}
}