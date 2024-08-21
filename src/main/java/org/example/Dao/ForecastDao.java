package org.example.Dao;

import org.example.HibernateUtil;
import org.example.entities.Forecast;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class ForecastDao {

    public void saveForecast(Forecast forecast) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(forecast);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public Forecast getForecastById(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Forecast.class, id);
        }
    }

    public List<Forecast> getForecastsByLocationId(long locationId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Forecast> query = session.createQuery("from Forecast where location.id = :locationId", Forecast.class);
            query.setParameter("locationId", locationId);
            return query.list();
        }
    }

    public void updateForecast(Forecast forecast) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(forecast);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void deleteForecast(long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Forecast forecast = session.get(Forecast.class, id);
            if (forecast != null) {
                session.delete(forecast);
                transaction.commit();
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
}
