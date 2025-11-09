package app.service;

import app.config.HibernateConfig;
//import app.enums.Category;
import app.entities.Candidate;
import app.entities.Skill;
import app.enums.Category;
import app.security.Role;
import app.security.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

//import static app.enums.Category.*;

public class Populator {

    private final EntityManagerFactory emf;
    public Populator(EntityManagerFactory emf){ this.emf = emf; }

    public void createAdminAndRolesForProdDB(){

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Role adminRole = new Role("Admin");
            Role userRole = new Role("User");
            Role anyRole = new Role("Anyone");
            em.persist(adminRole);
            em.persist(userRole);
            em.persist(anyRole);

            User admin = new User("admin", "tpas2025");
            adminRole.addUser(admin);
            em.persist(admin);

            em.getTransaction().commit();

            System.out.println("Production database have a Admin");
        }
    }

    public void createUsersAndRolesTest() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Ryd relationstabel først (ellers FK-fejl)
            em.createNativeQuery("DELETE FROM role_user").executeUpdate();
            // Slet Users før Roles (ellers FK-fejl)
            em.createQuery("DELETE FROM Role").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            System.out.println("Pop 1");
            Role adminRole = new Role("ADMIN");
            Role userRole = new Role("USER");
            Role anyRole = new Role("ANYONE");
            em.persist(adminRole);
            System.out.println("Pop 2");
            em.persist(userRole);
            System.out.println("Pop 3");
            em.persist(anyRole);
            System.out.println("Pop 4");
            User admin = new User("admin", "admin123");
            User normalUser = new User("user1", "pass123");

            adminRole.addUser(admin);
            userRole.addUser(normalUser);

            em.persist(admin);
            System.out.println("Pop 5");
            em.persist(normalUser);
            System.out.println("Pop 6");
            em.getTransaction().commit();

            System.out.println("Users and roles created successfully!");
        }
    }

    public void populateTestDB() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Skill s1 = Skill.builder()
                    .name("Java")
                    .category(Category.PROG_LANG)
                    .description("General-purpose programming languages")
                    .build();

            Skill s2 = Skill.builder()
                    .name("Python")
                    .category(Category.PROG_LANG)
                    .description("General-purpose programming languages")
                    .build();

            Skill s3 = Skill.builder()
                    .name("JavaScript")
                    .category(Category.PROG_LANG)
                    .description("General-purpose programming languages")
                    .build();

            Skill s4 = Skill.builder()
                    .name("MySQL")
                    .category(Category.DB)
                    .description("Databases and data storage technologies")
                    .build();

            Candidate c1 = Candidate.builder()
                            .name("Cody")
                    .phoneNumber("12345678")
                    .educationBackground("Software engineer")
                    .skills(new HashSet<>())
                            .build();

            Candidate c2 = Candidate.builder()
                    .name("Bobby")
                    .phoneNumber("34567812")
                    .educationBackground("Datalog")
                    .skills(new HashSet<>())
                    .build();

            em.persist(s1);
            em.persist(s2);
            em.persist(s3);
            em.persist(s4);

            c1.addSkill(s1);

            c2.addSkill(s2);
            c2.addSkill(s3);
            c2.addSkill(s4);

            em.persist(c1);
            em.persist(c2);

            em.getTransaction().commit();
        }
        catch (Exception e){

            System.out.println("Exception in populate Test DB");
            System.out.println(e.getCause() + " Message: " + e.getMessage());
        }
    }

    public void poppulateDBTestSecurity(){
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Role adminRole = new Role("Admin");
            Role userRole = new Role("User");
            Role anyRole = new Role("Anyone");
            em.persist(adminRole);
            em.persist(userRole);
            em.persist(anyRole);
            em.getTransaction().commit();
            System.out.println("Roles created successfully!");
        }
        catch (Exception e) {
            System.out.println("Exception in populateDBTestSecurity: " + e.getMessage());
        }
    }
}

