package com.revature.daoimpl;
import static com.revature.utils.HibernateUtil.runHibernateTransaction;
import static com.revature.utils.HibernateUtil.saveToDB;
import java.util.List;

import javax.persistence.EntityManager;
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

/**Data Access Object implementation to access the associate entity from the Database*/
public class AssociateDaoImpl implements AssociateDao
{
	/** Gets a single associate with an id
	 * @param id associateId */
	@Override
	public TfAssociate getAssociate(Integer id) {
		return HibernateUtil.runHibernate((Session session, Object ... args) ->
			session.createQuery("from TfAssociate a where a.id = :id", TfAssociate.class)
			.setParameter("id", id).getSingleResult());
	}
	
	/** Gets list of associates matching criteria. Used by updated angular front end to perform 
	 * pagnation of results and improve performance.
	 * @author Joshua-Pressley-1807
	 * @param startIdx starting index
	 * @param numRes the number of resuts to return
	 * @param mktStatus the marketing ID
	 * @param clientId the client ID
	 * @return list of associates matching criteria */
	public List<TfAssociate> getNAssociateMatchingCriteria(int startIdx, int numRes, int mktStatus, int clientId)
	{
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<TfAssociate> criteria = builder.createQuery(TfAssociate.class);
			Root<TfAssociate> root = criteria.from(TfAssociate.class);
			criteria.where(builder.equal(root.get("TfAssociate_.marketingStatus"), mktStatus));
			criteria.where(builder.equal(root.get("TfAssociate_.client"), clientId));
			List<TfAssociate> results = session.createQuery(criteria).getResultList();
			
			System.out.println("RESULTS FINAL > " + results);
			//filter results
			if (startIdx + numRes > results.size()) {
				results = results.subList(startIdx, results.size());
			} else {
				results = results.subList(startIdx, startIdx+numRes);
			}
			System.out.println("RESULTS FINAL > " + results);
			return results;
	}

	/** Gets an associate by an associated user id
	 * @param id userId */
	@Override
	public TfAssociate getAssociateByUserId(int id) {
		return HibernateUtil.runHibernate((Session session, Object ... args) ->
				session.createQuery("from TfAssociate where user.id = :id", TfAssociate.class)
				.setParameter("id", id).getSingleResult());
	}

	/** Gets all associates */
	@Override
	public List<TfAssociate> getAllAssociates() {
		return HibernateUtil.runHibernate((Session session, Object... args) -> session
				.createQuery("from TfAssociate", TfAssociate.class).getResultList());
	}
	
	@Override
	public List<TfAssociate> getNAssociates() {
		return HibernateUtil.runHibernate((Session session, Object ...args) -> session
				.createQuery("from TfAssociate", TfAssociate.class)
				.setMaxResults(60).getResultList());
	}

	//---------------------------------------------------

	/** @author Joshua Pressley-1807
	 * Removes duplicated code for the count action.
	 * @param sql the sql string to use in the native query
	 * @return the count of what was requested */
	private Object getCountOf(String sql) {
		Session session = null;
		Object counting = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			counting = session.createNativeQuery(sql).getSingleResult();
		} catch(HibernateException e) {
			e.printStackTrace();
		}
		finally {
			if ( session != null )
				session.close();
		}
		return counting;
	}//end getCountOf()

	/** @author Joshua Pressley-1807
	 * Contains switch statement for al existing sql queries in use by methods below.
	 * Used with new method above to remove duplicated code. */
	private Object getCountStringMapper(int sqlID) {
		final String BEGINNING = "select count(tf_associate_id) from admin.tf_associate ";
		final String BASE = BEGINNING + "where tf_marketing_status_id = ";
		String sql;
		switch (sqlID) {
			case 1: // getCountUndeployedMapped()
				sql =  BEGINNING + "where (tf_marketing_status_id = 1 or tf_marketing_status_id = 2 " +
						"or tf_marketing_status_id = 3 or tf_marketing_status_id = 4)";
				break;
			case 2: // getCountUndeployedUnmapped()
				sql = BEGINNING + "where (tf_marketing_status_id = 6 or tf_marketing_status_id = 7 " +
						"or tf_marketing_status_id = 8 or tf_marketing_status_id = 9)";
				break;
			case 3: // getCountDeployedMapped()
				sql=  BASE + 5; break;
			case 4: // getCountDeployedUnmapped()
				sql = BASE + 10; break;
			case 5: // getCountUnmappedTraining()
				sql=  BASE + 6; break;
			case 6: // getCountUnmappedOpen()
				sql=  BASE + 7; break;
			case 7: // getCountUnmappedSelected()
				sql=  BASE + 8; break;
			case 8: // getCountUnmappedConfirmed()
				sql = BASE + 9; break;
			case 9: // getCountMappedTraining()
				sql = BASE + 1; break;
			case 10: // getCountMappedReserved()
				sql = BASE + 2; break;
			case 11: // getCountMappedSelected()
				sql = BASE + 3; break;
			case 12: // getCountMappedConfirmed()
				sql = BASE + 4; break;
			default: 
				sql = "";  break;
		}//end switch
		return getCountOf(sql);
	}//end getCountStringMapper()

	//---------------------------------------------------
	@Override
	public Object getCountUndeployedMapped()
	{ return getCountStringMapper(1); }
	
	@Override
	public Object getCountUndeployedUnmapped()
	{ return getCountStringMapper(2); }
	
	@Override
	public Object getCountDeployedMapped()
	{ return getCountStringMapper(3); }
	
	@Override
	public Object getCountDeployedUnmapped()
	{ return getCountStringMapper(4); }

	@Override
	public Object getCountUnmappedTraining()
	{ return getCountStringMapper(5); }
	
	@Override
	public Object getCountUnmappedOpen()
	{ return getCountStringMapper(6); }
	
	@Override
	public Object getCountUnmappedSelected()
	{ return getCountStringMapper(7); }
	
	@Override
	public Object getCountUnmappedConfirmed()
	{ return getCountStringMapper(8); }
	
	@Override
	public Object getCountMappedTraining()
	{ return getCountStringMapper(9); }
	
	@Override
	public Object getCountMappedReserved()
	{ return getCountStringMapper(10); }
	
	@Override
	public Object getCountMappedSelected()
	{ return getCountStringMapper(11); }

	@Override
	public Object getCountMappedConfirmed()
	{ return getCountStringMapper(12); }

	@Override
	public boolean updateAssociatePartial(TfAssociate associate) {
		return runHibernateTransaction((Session session, Object ... args)->
		{
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
	 * @param associateId */
	@Override
	public boolean approveAssociate(int associateId)
	{ return runHibernateTransaction(approveAssociate, associateId);  }

	/** approves many given associates
	 * @param associateIds contains associate ids */
	@Override
	public boolean approveAssociates(List<Integer> associateIds)
	{ return HibernateUtil.multiTransaction(approveAssociate, associateIds); }

	/** Creates new associate with a given associate object.
	 * @param newassociate the new associate you wish to persist */
	@Override
	public boolean createAssociate(TfAssociate newassociate)
	{ return saveToDB(newassociate); }

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
	public List<GraphedCriteriaResult> getUndeployed(String which) {
		if (which.equals("mapped")) {
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
		} else if (which.equals("unmapped")) {
			return HibernateUtil.runHibernate((Session session, Object... args) -> {
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
	public <T> T countMappedAssociatesByValue(String column, T value, Integer mappedStatus) {
		Sessional<T> ss = (Session session, Object... args) -> {
			String condition;
			if (Integer.valueOf(value.toString()) != -1) {
				condition = column + " = " + args[0] + " AND ";
			} else {
				condition = "";
			}

			String hql = "SELECT COUNT(TF_ASSOCIATE_ID) FROM TfAssociate WHERE " +
					condition + "TF_MARKETING_STATUS_ID = :status";
			Query query = session.createQuery(hql);
			return (T) query.setParameter("status", args[1]).getSingleResult();
		};
		return HibernateUtil.runHibernate(ss, value, mappedStatus);
	}
}