package cn.tuyucheng.taketoday.optionalreturntype;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Optional;

public class PersistOptionalTypeExample {
	static String persistenceUnit = "cn.tuyucheng.taketoday.optionalreturntype";
	static EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnit);

	static EntityManager entityManager = emf.createEntityManager();

	// to run this app, uncomment the follow line in META-INF/persistence.xml
	// <class>cn.tuyucheng.taketoday.optionalreturntype.UserOptionalField</class>
	public static void main(String[] args) {
		UserOptionalField user1 = new UserOptionalField();
		user1.setUserId(1L);
		user1.setFirstName(Optional.of("Tu Yucheng"));
		entityManager.persist(user1);

		UserOptional user2 = entityManager.find(UserOptional.class, 1L);
		System.out.print("User2.firstName:" + user2.getFirstName());
	}
}
