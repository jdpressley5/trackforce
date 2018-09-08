package com.revature.daoimpl;
import static com.revature.utils.HibernateUtil.runHibernateTransaction;
import java.util.List;
import com.revature.entity.TfRole;
import org.hibernate.Session;
import com.revature.dao.UserDao;
import com.revature.entity.TfUser;
import com.revature.utils.HibernateUtil;

public class UserDaoImpl implements UserDao {

	/** Gets a user by name */
	@Override
	public TfUser getUser(String username) {
		return HibernateUtil.runHibernate((Session session, Object ... args) ->
		session.createQuery("from TfUser u where u.username like :username", TfUser.class)
		.setParameter("username", username).getSingleResult());
	}
    
	/** Gets all users */
    @Override
	public List<TfUser> getAllUsers() {
		return HibernateUtil.runHibernate((Session session, Object ... args) ->
				session.createQuery("from TfUser ", TfUser.class).getResultList());
	}
    
    /** Inserts a user */
	@Override
	public boolean insertUser(TfUser newUser) {
		return HibernateUtil.saveToDB(newUser);
	}

	/** Gets role based on ID */
	@Override
	public TfRole getRole(int roleId) {
		return HibernateUtil.runHibernate((Session session, Object ... args) ->
				session.createQuery("from TfRole u where u.id = :id", TfRole.class)
				.setParameter("id", roleId).getSingleResult());
	}

	/** Updates a user */
	@Override
	public boolean updateUser(TfUser user) {
		return runHibernateTransaction((Session session, Object ... args)->{
			session.update(user);
			return true;
		});
	}
}