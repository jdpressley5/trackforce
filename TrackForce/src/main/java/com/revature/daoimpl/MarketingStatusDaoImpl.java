package com.revature.daoimpl;
import java.util.List;
import org.hibernate.Session;
import com.revature.dao.MarketingStatusDao;
import com.revature.entity.TfMarketingStatus;
import com.revature.utils.HibernateUtil;

public class MarketingStatusDaoImpl implements MarketingStatusDao {

	/** Gets all statuses. */
	@Override
	public List<TfMarketingStatus> getAllMarketingStatuses() {
		return HibernateUtil.runHibernate((Session session, Object ... args) ->
		session.createQuery("from TfMarketingStatus", TfMarketingStatus.class).getResultList());
	}
	
	/** Gets a status by its ID. */
	@Override
	public TfMarketingStatus getMarketingStatusById(int id) {
		return HibernateUtil.runHibernate((Session session, Object... args) ->
				session.createQuery("from TfMarketingStatus c where c.id like :id", TfMarketingStatus.class).setParameter("id", id).getSingleResult());
	}
}