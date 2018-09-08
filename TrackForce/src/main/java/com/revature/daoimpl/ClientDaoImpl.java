package com.revature.daoimpl;
import java.util.List;
import com.revature.entity.TfEndClient;
import org.hibernate.Session;
import com.revature.dao.ClientDao;
import com.revature.entity.TfClient;
import com.revature.utils.HibernateUtil;

public class ClientDaoImpl implements ClientDao {

	/** Gets all clients ordered by client name.*/
	@Override
	public List<TfClient> getAllTfClients() {
		return HibernateUtil.runHibernate((Session session, Object ...args) -> session
				.createQuery("from TfClient order by tf_client_name ", TfClient.class)
				.getResultList());
	}
	
	/** Gets the first 50 clients. */
	public List<TfClient> getFirstFiftyTfClients() {
		return HibernateUtil.runHibernate((Session session, Object ...args) -> session
				.createQuery("from TfClient order by tf_client_name ", TfClient.class)
				.setMaxResults(50).getResultList());
	}

	/** Gets clients with mapped associates. */
	@Override
	public List<TfClient> getAllClientsWithMappedAssociates(){
		return HibernateUtil.runHibernate((Session session, Object ...args) -> 
			session.createQuery(
					  "FROM TfClient c WHERE "
					+ "(SELECT COUNT(tf_associate_id) FROM TfAssociate a "
					+ "WHERE a.client.id = c.id AND a.marketingStatus.id < 5) > 0", 
			TfClient.class).getResultList()
		);
	}
	
	/** Gets all clients. */
	@Override
	public List<TfClient> getAllTfClients(String[] columns) {
		return HibernateUtil.runHibernate((Session session, Object ...args) -> session
				.createQuery("SELECT " + String.join(" ", (String[]) args[0]) + "FROM TfClient ", TfClient.class)
				.getResultList());
	}

	/** Gets client matching name. */
	@Override
	public TfClient getClient(String name) {
		return HibernateUtil.runHibernate((Session session, Object... args) -> session
				.createQuery("from TfClient c where c.name like :name", TfClient.class)
				.setParameter("name", name).getSingleResult());
	}

	/** Gets client by ID */
	@Override
	public TfClient getClient(Integer id) {
		return HibernateUtil.runHibernate((Session session, Object... args) -> session
				.createQuery("from TfClient c where c.id like :id", TfClient.class)
				.setParameter("id", id).getSingleResult());
	}

	/** Gets end client */
	@Override
	public TfEndClient getEndClient(int id) {
		return HibernateUtil.runHibernate((Session session, Object... args) -> session
				.createQuery("from TfEndClient c where c.id like :id", TfEndClient.class)
				.setParameter("id", id).getSingleResult());
	}
}