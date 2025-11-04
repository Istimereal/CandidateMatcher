package app;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.service.Populator;
import jakarta.persistence.EntityManagerFactory;

public class Main {

    public static void main(String[] args) {

    //     EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("candidate_matcher");
        EntityManagerFactory emfTest = HibernateConfig.getEntityManagerFactoryForTest();
    //    Populator populator = new Populator(emf);
      //  Populator populator = new Populator(emfTest);
     //     populator.createAdminAndRolesForProdDB();
     //   populator.createUsersAndRolesTest();
   //    populator.poppulateDBTestSecurity();
      //  populator.poppulateDBTest();

       ApplicationConfig.startServer(7090, emfTest);
    }
}
