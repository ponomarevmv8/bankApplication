package bank.dev.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.function.Function;

@Component
public class TransactionHelper {

    private final SessionFactory sessionFactory;

    public TransactionHelper(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void executeInTransaction(Consumer<Session> action) {
        Session session = null;
        try {
            session = sessionFactory.getCurrentSession();
        } catch (Exception e) {
            System.out.println("Not found opened session");
        }

        if (session != null) {
            Transaction transaction = null;
            try {
                transaction = session.getTransaction();
                boolean isNewTransaction = !transaction.isActive();
                if (isNewTransaction) {
                    transaction.begin();
                }

                action.accept(session);

                if (isNewTransaction) {
                    transaction.commit();
                }
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            }
        } else {
            Transaction transaction = null;
            try (Session sessionNew = sessionFactory.openSession()) {
                transaction = sessionNew.beginTransaction();
                action.accept(sessionNew);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }

    public <T> T executeInTransaction(Function<Session, T> action) {

        Session session = null;

        try {
            session = sessionFactory.getCurrentSession();
        } catch (Exception e) {
            System.out.println("Not found opened session");
        }

        if (session != null) {
            Transaction transaction = null;
            try {
                transaction = session.getTransaction();
                boolean isNewTransaction = !transaction.isActive();
                if (isNewTransaction) {
                    transaction.begin();
                }

                var result = action.apply(session);

                if (isNewTransaction) {
                    transaction.commit();
                }
                return result;
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            }
        } else {
            Transaction transaction = null;
            try (Session sessionNew = sessionFactory.openSession()) {
                transaction = sessionNew.beginTransaction();
                var result = action.apply(sessionNew);
                transaction.commit();
                return result;
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }

}
