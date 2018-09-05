package com.revature.utils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import com.revature.entity.TfAssociate;
import com.revature.entity.TfBatch;
import com.revature.entity.TfBatchLocation;
import com.revature.entity.TfClient;
import com.revature.entity.TfCurriculum;
import com.revature.entity.TfEndClient;
import com.revature.entity.TfInterview;
import com.revature.entity.TfInterviewType;
import com.revature.entity.TfMarketingStatus;
import com.revature.entity.TfPlacement;
import com.revature.entity.TfRole;
import com.revature.entity.TfTrainer;
import com.revature.entity.TfUser;
import com.revature.entity.TfUserAndCreatorRoleContainer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import static com.revature.utils.LogUtil.logger;

/** @author Curtis H. & Adam L. & Josh P. & Chris S.
 * <p> The abstracted methods for making Hibernate calls to the database </p>
 * @version v6.18.06.13 */
public class HibernateUtil {
	private static ThreadUtil threadUtil = new ThreadUtil();

	private static SessionFactory sessionFactory = buildSessionFactory();
	
	private static StandardServiceRegistry registry;

	private static Sessional<Boolean> detachedUpdate = (Session session, Object... args) -> {
		session.update(args[0]);
		return true;
	};

	private static Sessional<Boolean> dbSave = (Session session, Object... args) -> {
		session.save(args[0]);
		return true;
	};
	
	private HibernateUtil() {}

	private static void addShutdown() {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown()));
	}

	private static SessionFactory buildSessionFactory() {
		try {
			StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();
			Map<String, Object> cfg = new HashMap<>();
			
			// connection configuration
            cfg.put(Environment.URL, System.getenv("TRACKFORCE_DB_URL"));
            cfg.put(Environment.USER, System.getenv("TRACKFORCE_DB_USERNAME"));
            cfg.put(Environment.PASS, System.getenv("HBM_PW_ENV"));
            cfg.put(Environment.DRIVER, "oracle.jdbc.OracleDriver");
            cfg.put(Environment.HBM2DDL_AUTO, "validate");
            // c3p0 configuration
            cfg.put(Environment.C3P0_MIN_SIZE, 5);         //Minimum size of pool
            cfg.put(Environment.C3P0_MAX_SIZE, 20);        //Maximum size of pool
            cfg.put(Environment.C3P0_ACQUIRE_INCREMENT, 1);//Number of connections acquired at a time when pool is exhausted 
            cfg.put(Environment.C3P0_TIMEOUT, 1800);       //Connection idle time
            cfg.put(Environment.C3P0_MAX_STATEMENTS, 150); //PreparedStatement cache size
            cfg.put(Environment.C3P0_CONFIG_PREFIX+".initialPoolSize", 5);

            registryBuilder.applySettings(cfg);    
            registry = registryBuilder.build();
            MetadataSources sources = new MetadataSources(registry)
            	.addAnnotatedClass(TfAssociate.class).addAnnotatedClass(TfBatch.class)
            	.addAnnotatedClass(TfBatchLocation.class).addAnnotatedClass(TfClient.class)
            	.addAnnotatedClass(TfCurriculum.class).addAnnotatedClass(TfEndClient.class)
            	.addAnnotatedClass(TfInterview.class).addAnnotatedClass(TfInterviewType.class)
            	.addAnnotatedClass(TfMarketingStatus.class)
            	.addAnnotatedClass(TfPlacement.class).addAnnotatedClass(TfRole.class)
            	.addAnnotatedClass(TfTrainer.class).addAnnotatedClass(TfUser.class)
            	.addAnnotatedClass(TfUserAndCreatorRoleContainer.class);
            Metadata metadata = sources.getMetadataBuilder().build();
            sessionFactory = metadata.getSessionFactoryBuilder().build(); 
		}catch (Exception e) {
             if (registry != null)
            	 StandardServiceRegistryBuilder.destroy(registry);
             e.printStackTrace();
		} finally { addShutdown(); }
		return sessionFactory;
	}

	public static SessionFactory getSessionFactory()
	{ return sessionFactory; }

	public static void shutdown() {
		logger.info("Shutting down SessionFactory");
		getSessionFactory().close();
		logger.info("SessionFactory closed");
	}

	public static void closeSession(Session session) {
		if (session != null) {
			session.close();
			logger.info("Session is" + (session.isOpen() ? " open" : " closed"));
		}
	}

	private static void rollbackTransaction(Transaction transaction) {
		if (transaction != null) {
			transaction.rollback();
			logger.warn("Transaction rolled back");
		}
	}

	// The code above this line to the top of the package is basically an exact copy
	// of stuff William did in class
	// Now we abstract further...
	public static boolean runHibernateTransaction(Sessional<Boolean> sessional, Object... args) {
		Callable<Boolean> caller = () -> {
			Session session = null;
			Transaction transaction = null;
			try {
				session = HibernateUtil.getSessionFactory().openSession();
				transaction = session.beginTransaction();

				if (sessional.operate(session, args)) { logger.debug("Committing..."); }
				else { throw new HibernateException("Transaction Operation Failed!"); }

				transaction.commit();
				logger.info("Transaction committed!");
				return true;
			} catch (HibernateException | ThrownInHibernate e) {
				HibernateUtil.rollbackTransaction(transaction);
				logger.error(e.getMessage(), e);
			} finally {
				if (session != null) session.close();
			}
			return false;
		};
		return threadUtil.submitCallable(caller);
	}

	public static <T> boolean multiTransaction(Sessional<Boolean> sessional, List<T> items) {
		//Be careful using this method as it can create extreme strain by creating multiple threads
		//This should be refactored along with a refactor of runHibernateTransaction to both call
		//on another method that does the work that runs x amount of given times. Or implement a
		//cache the ensures that flush is not called on a hibernate transaction
		return HibernateUtil.runHibernateTransaction((Session session, Object... args) -> {
			for (T a : items) {
				if (!sessional.operate(session, a)) { return false; }
			}
			return true;
		});
	}

	public static <T> T runHibernate(Sessional<T> ss, Object... args) {
		Callable<T> caller = () -> {
			Session session = null;
			Throwable t;
			try {
				session = HibernateUtil.getSessionFactory().openSession();
				return ss.operate(session, args);
			} catch (ThrownInHibernate | HibernateException e) {
				logger.error(e.getMessage(), e);
				t = e;
			} finally {
				if (session != null) session.close();
			}
			throw new HibernateException(t);
		};
		return threadUtil.submitCallable(caller);
	}

	public static <T> List<T> runHibernate(ListOp<T> ss, Object... args) {
		Callable<List<T>> caller = () -> {
			Session session = null;
			try {
				session = HibernateUtil.getSessionFactory().openSession();
				return ss.operate(session, args);
			} catch (ThrownInHibernate | HibernateException e) {
				logger.error(e.getMessage(), e);
			} finally {
				if (session != null) session.close();
			}
			return new ArrayList<>();
		};
		return threadUtil.submitCallable(caller);
	}

	public static boolean saveToDB(Object o) {
		return runHibernateTransaction(dbSave, o);
	}

	public static <T> boolean saveToDB(List<T> o) {
		return multiTransaction(dbSave, o);
	}

	//UNUSED
	public static <T> boolean updateDetached(T det) {
		return runHibernateTransaction(detachedUpdate, det);
	}

	//UNUSED
	public static <T> boolean updateDetached(List<T> det) {
		return multiTransaction(detachedUpdate, det);
	}
}