package org.example.Dao;

import org.example.HibernateUtil;
import org.example.entities.CurrentWeather;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class CurrentWeatherDao {

    public void saveCurrentWeather(CurrentWeather currentWeather) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(currentWeather);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }




}
