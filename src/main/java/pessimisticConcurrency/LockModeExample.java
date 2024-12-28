package pessimisticConcurrency;

import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.SessionFactory;

public class LockModeExample {

    public static void main(String[] args) {
    	 Configuration configuration = HibernateConfig.getConfig();
         configuration.addAnnotatedClass(BankAccount.class);
         ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                 .applySettings(configuration.getProperties())
                 .build();
         SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        LockModeExample example = new LockModeExample();
        
        // Call methods for different lock modes
        example.readLockExample(sessionFactory); // LockMode.READ
        example.writeLockExample(sessionFactory); // LockMode.WRITE
        example.pessimisticReadLockExample(sessionFactory); // LockMode.PESSIMISTIC_READ
        example.pessimisticWriteLockExample(sessionFactory); // LockMode.PESSIMISTIC_WRITE
        example.optimisticLockExample(sessionFactory); // LockMode.OPTIMISTIC
        example.optimisticForceIncrementExample(sessionFactory); // LockMode.OPTIMISTIC_FORCE_INCREMENT
    }

    // LockMode.READ example
    public void readLockExample(SessionFactory sessionFactory) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        try {
            // LockMode.READ allows multiple readers but prevents updates by other transactions
            BankAccount account = session.get(BankAccount.class, 1L, LockMode.READ);
            account.setBalance(account.getBalance() + 500); // This will be blocked
            session.update(account);

            tx.commit();
        } catch (Exception e) {
            System.out.println("READ Lock Exception: " + e.getMessage());
            tx.rollback();
        } finally {
            session.close();
        }
    }

    // LockMode.WRITE example
    public void writeLockExample(SessionFactory sessionFactory) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        try {
            // LockMode.WRITE ensures that no other transaction can read or write
            BankAccount account = session.get(BankAccount.class, 1L, LockMode.WRITE);
            account.setBalance(account.getBalance() + 1000); // Update balance
            session.update(account);

            tx.commit();
        } catch (Exception e) {
            System.out.println("WRITE Lock Exception: " + e.getMessage());
            tx.rollback();
        } finally {
            session.close();
        }
    }

    // LockMode.PESSIMISTIC_READ example
    public void pessimisticReadLockExample(SessionFactory sessionFactory) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        try {
            // LockMode.PESSIMISTIC_READ prevents other transactions from modifying the entity
            BankAccount account = session.get(BankAccount.class, 1L, LockMode.PESSIMISTIC_READ);
            account.setBalance(account.getBalance() + 2000); // This will be blocked by other write operations
            session.update(account);

            tx.commit();
        } catch (Exception e) {
            System.out.println("PESSIMISTIC_READ Lock Exception: " + e.getMessage());
            tx.rollback();
        } finally {
            session.close();
        }
    }

    // LockMode.PESSIMISTIC_WRITE example
    public void pessimisticWriteLockExample(SessionFactory sessionFactory) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        try {
            // LockMode.PESSIMISTIC_WRITE locks the row exclusively for write operations
            BankAccount account = session.get(BankAccount.class, 1L, LockMode.PESSIMISTIC_WRITE);
            account.setBalance(account.getBalance() + 1500);
            session.update(account);

            tx.commit();
        } catch (Exception e) {
            System.out.println("PESSIMISTIC_WRITE Lock Exception: " + e.getMessage());
            tx.rollback();
        } finally {
            session.close();
        }
    }

    // LockMode.OPTIMISTIC example
    public void optimisticLockExample(SessionFactory sessionFactory) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        try {
            // LockMode.OPTIMISTIC relies on version checking during commit
            BankAccount account = session.get(BankAccount.class, 1L, LockMode.OPTIMISTIC);
            account.setBalance(account.getBalance() + 3000);
            session.update(account);

            tx.commit();
        } catch (Exception e) {
            System.out.println("OPTIMISTIC Lock Exception: " + e.getMessage());
            tx.rollback();
        } finally {
            session.close();
        }
    }

    // LockMode.OPTIMISTIC_FORCE_INCREMENT example
    public void optimisticForceIncrementExample(SessionFactory sessionFactory) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        try {
            // LockMode.OPTIMISTIC_FORCE_INCREMENT increments the version even without a modification
            BankAccount account = session.get(BankAccount.class, 1L, LockMode.OPTIMISTIC_FORCE_INCREMENT);
            account.setBalance(account.getBalance() + 500); // Forces version increment
            session.update(account);

            tx.commit();
        } catch (Exception e) {
            System.out.println("OPTIMISTIC_FORCE_INCREMENT Lock Exception: " + e.getMessage());
            tx.rollback();
        } finally {
            session.close();
        }
    }
}
