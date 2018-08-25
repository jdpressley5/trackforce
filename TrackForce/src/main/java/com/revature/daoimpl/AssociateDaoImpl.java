package com.revature.daoimpl;
import static com.revature.utils.HibernateUtil.runHibernateTransaction;
import static com.revature.utils.HibernateUtil.saveToDB;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.openqa.selenium.InvalidArgumentException;
import com.revature.criteria.GraphedCriteriaResult;
import com.revature.dao.AssociateDao;
import com.revature.entity.TfAssociate;
import com.revature.entity.TfBatch;
import com.revature.entity.TfClient;
import com.revature.entity.TfCurriculum;
import com.revature.entity.TfMarketingStatus;
import com.revature.entity.TfUser;
import com.revature.utils.HibernateUtil;
import com.revature.utils.Sessional;

/** Data Access Object implementation to access the associate entity from the Database */
public class AssociateDaoImpl implements AssociateDao 
{
	private static final String countIdSQL =
			"select count(tf_associate_id) from admin.tf_associate where tf_marketing_status_id = ";

	/** Gets a single associate with an id
	 * @param Integer associateId */
	@Override
	public TfAssociate getAssociate(Integer id) {
		return HibernateUtil.runHibernate((Session session, Object ... args) ->
		session.createQuery("from TfAssociate a where a.id = :id", TfAssociate.class)
				.setParameter("id", id).getSingleResult());
	}

	/** Gets an associate by an associated user id
	 * @param int userId */
	@Override
	public TfAssociate getAssociateByUserId(int id) {
		return HibernateUtil.runHibernate((Session session, Object ... args) ->
				session.createQuery("from TfAssociate where user.id = :id", TfAssociate.class)
						.setParameter("id", id).getSingleResult());
	}

	/**  Gets all associates */
	@Override
	public List<TfAssociate> getAllAssociates() {
		return HibernateUtil.runHibernate((Session session, Object... args) -> session
				.createQuery("from TfAssociate", TfAssociate.class).getResultList());
	}
	
	@Override
	public List<TfAssociate> getNAssociates() {
		return HibernateUtil.runHibernate((Session session, Object ...args) -> session
				.createQuery("from TfAssociate", TfAssociate.class).setMaxResults(60).getResultList());
	}

	private Object getCount(String sqlcriteria)
	{
		Session session = null;
		Object countRes = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			countRes = session.createNativeQuery(sqlcriteria).getSingleResult();
		} catch(HibernateException e) { e.printStackTrace(); }
		finally { if ( session != null ) session.close(); }
		return countRes;
	}

	@Override
	public Object getCountUndeployedMapped() {
		String sql = "select count(tf_associate_id) from admin.tf_associate " +
				"where (tf_marketing_status_id = 1 or tf_marketing_status_id = 2 or tf_marketing_status_id = 3 " +
				"or tf_marketing_status_id = 4)";
		return getCount(sql);
	}
	
	@Override
	public Object getCountUndeployedUnmapped() {
		String sql = "select count(tf_associate_id) from admin.tf_associate " +
				"where (tf_marketing_status_id = 6 or tf_marketing_status_id = 7 " +
				"or tf_marketing_status_id = 8 or tf_marketing_status_id = 9)";
		return getCount(sql);
	}
	
	@Override
	public Object getCountDeployedMapped() { return getCount(countIdSQL + 5); }
	
	@Override
	public Object getCountDeployedUnmapped() { return getCount(countIdSQL + 10); }

	@Override
	public Object getCountUnmappedTraining() { return getCount(countIdSQL + 6); }
	
	@Override
	public Object getCountUnmappedOpen() { return getCount(countIdSQL + 7); }
	
	@Override
	public Object getCountUnmappedSelected() { return getCount(countIdSQL + 8); }
	
	@Override
	public Object getCountUnmappedConfirmed() { return getCount(countIdSQL + 9); }
	
	@Override
	public Object getCountMappedTraining() { return getCount(countIdSQL + 1); }
	
	@Override
	public Object getCountMappedReserved() { return getCount(countIdSQL + 2); }
	
	@Override
	public Object getCountMappedSelected() { return getCount(countIdSQL + 3); }
	
	@Override
	public Object getCountMappedConfirmed() { return getCount(countIdSQL + 4); }

	@Override
	public boolean updateAssociatePartial(TfAssociate associate) {
		return HibernateUtil.runHibernateTransaction((Session session, Object ... args)-> {
			TfAssociate temp = session.get(TfAssociate.class, associate.getId());
			temp.setFirstName(associate.getFirstName());
			temp.setLastName(associate.getLastName());
			session.update(temp);
			return true;
		});
	}

	/** Sessional with instructions on how to approve an associate */
	private Sessional<Boolean> approveAssociate = (Session session, Object... args) -> {
		TfAssociate temp = session.get(TfAssociate.class, (Integer) args[0]);
		temp.getUser().setIsApproved(TfUser.APPROVED);
		session.update(temp);
		return true;
	};

	/** approves given associate
	 * @param int associateId */
	@Override
	public boolean approveAssociate(int associateId) 
	{ return HibernateUtil.runHibernateTransaction(approveAssociate, associateId); }

	/** approves many given associates
	 * @param List<Integer> contains associate ids */
	@Override
	public boolean approveAssociates(List<Integer> associateIds) 
	{ return HibernateUtil.multiTransaction(approveAssociate, associateIds); }

	/** Creates new associate with a given associate object.
	 * @param TfAssociate the new associate you wish to persist */
	@Override
	public boolean createAssociate(TfAssociate newassociate) { return saveToDB(newassociate); }

	/** Does something */
	@Override
	public List<GraphedCriteriaResult> getMapped(int id) {
		return HibernateUtil.runHibernate((Session session, Object... args) -> 
		{
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<GraphedCriteriaResult> query = cb.createQuery(GraphedCriteriaResult.class);
			Root<TfAssociate> root = query.from(TfAssociate.class);
			Join<TfAssociate, TfClient> clientJoin = root.join("client");
			Join<TfAssociate, TfMarketingStatus> msJoin = root.join("marketingStatus");
			Path<?> clientId = clientJoin.get("id");
			Path<?> clientName = clientJoin.get("name");
			query.where(cb.equal(msJoin.get("id"), args[0]));
			query.groupBy(clientId, clientName);
			query.multiselect(cb.count(root), clientId, clientName);
			return session.createQuery(query).getResultList();
		}, id);
	}

	@Override
	public List<GraphedCriteriaResult> getUndeployed(String which) 
	{
		if (which.equals("mapped")) 
		{
			return HibernateUtil.runHibernate((Session session, Object... args) -> 
			{
				CriteriaBuilder cb = session.getCriteriaBuilder();
				CriteriaQuery<GraphedCriteriaResult> query = cb.createQuery(GraphedCriteriaResult.class);
				Root<TfAssociate> root = query.from(TfAssociate.class);
				Join<TfAssociate, TfClient> clientJoin = root.join("client");
				Join<TfAssociate, TfMarketingStatus> msJoin = root.join("marketingStatus");
				Path<?> clientId = clientJoin.get("id");
				Path<?> clientName = clientJoin.get("name");
				query.where(cb.lessThanOrEqualTo(msJoin.get("id"), 4));
				query.where(cb.greaterThanOrEqualTo(msJoin.get("id"), 1));
				query.groupBy(clientId, clientName);
				query.multiselect(cb.count(root), clientId, clientName);
				return session.createQuery(query).getResultList();
			});
		}
		else if (which.equals("unmapped")) 
		{
			return HibernateUtil.runHibernate((Session session, Object... args) -> 
			{
				CriteriaBuilder cb = session.getCriteriaBuilder();
				CriteriaQuery<GraphedCriteriaResult> query = cb.createQuery(GraphedCriteriaResult.class);
				Root<TfAssociate> root = query.from(TfAssociate.class);
				Join<TfAssociate, TfBatch> batchJoin = root.join("batch");
				Join<TfBatch, TfCurriculum> curriculumJoin = batchJoin.join("curriculumName");
				Join<TfAssociate, TfMarketingStatus> msJoin = root.join("marketingStatus");
				Path<?> curriculumid = curriculumJoin.get("id");
				Path<?> curriculumName = curriculumJoin.get("name");
				query.where(cb.lessThanOrEqualTo(msJoin.get("id"), 9));
				query.where(cb.greaterThanOrEqualTo(msJoin.get("id"), 6));
				query.groupBy(curriculumid, curriculumName);
				query.multiselect(cb.count(root), curriculumid, curriculumName);
				return session.createQuery(query).getResultList();
			});
		}
		throw new InvalidArgumentException("NOT MAPPED OR UNMAPPED YOU FOOOL");
	}

	@Override
	public boolean updateAssociate(TfAssociate associate) {
		return runHibernateTransaction((Session session, Object... args) -> {
			session.update(associate);
			return true;
		});
	}

	@Override
	public boolean updateAssociates(List<TfAssociate> associates) {
		associates.forEach(this::updateAssociate);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T countMappedAssociatesByValue(String column, T value, Integer mappedStatus) 
	{
		Sessional<T> ss = (Session session, Object... args) -> 
		{
			String condition = null;
			if (Integer.valueOf(value.toString()) != -1) condition = column + " = " + args[0] + " AND ";
			else condition = "";
		
			String hql = "SELECT COUNT(TF_ASSOCIATE_ID) FROM TfAssociate WHERE " + condition + "TF_MARKETING_STATUS_ID = :status";
			Query query = session.createQuery(hql);			
			return (T) query
					.setParameter("status", args[1])
					.getSingleResult();
		};
		return HibernateUtil.runHibernate(ss, value, mappedStatus);
	}
}