package wildfly.beans;

import model.User;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Session Bean implementation class UserBean
 */
@Stateless
public class UserBean implements UserBeanRemote, UserBeanLocal {
	@PersistenceContext(unitName = "wildfly-jpa")
	private EntityManager em;

	@Override
	public List<User> getUsers() {
		return em.createNamedQuery("User.findAll")
			.getResultList();
	}
}
