package app.daos;


import app.entities.Skill;
import app.entities.Candidate;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.hibernate.exception.ConstraintViolationException;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SkillDAO {
    private static SkillDAO instance;
    private static EntityManagerFactory emf;

    private SkillDAO() {}

    public static SkillDAO getInstance(EntityManagerFactory _emf) {

        if (instance == null) {
            instance = new SkillDAO();
            emf = _emf;
        }
        return instance;
    }

    public Skill createSkill(Skill skill) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(skill);
            em.getTransaction().commit();
        }
        catch (ConstraintViolationException cve){
            throw new ApiException(400, "Skill already exists");
        }
        catch (PersistenceException pe) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new ApiException(500,"Persistence error");
        }
        catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new ApiException(500, "unknown error");
        }
        finally {
            em.close();
        }
        return skill;
    }

    public List<Skill> getAllSkills() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Skill> query = em.createQuery(("SELECT s FROM Skill s"), Skill.class);
            return query.getResultList();
        }
        catch (PersistenceException pe) {
            throw new ApiException(500, "Persistence error");
        }
        catch (Exception e) {
            throw new ApiException(500, "unknown error");
        }

    }

    public Skill getSkillById(int id) {

        try (EntityManager em = emf.createEntityManager()) {
            Skill skill =  em.find(Skill.class, id);
            if(skill == null) {
                throw new ApiException(404, "Skill with" + id + "Does not exist");
            }
            return skill;
        }
        catch (Exception e) {
            throw new ApiException(500, "unexpected error");
        }
    }

    public Skill updateSkill(Integer integer, Skill skill) {
        Skill target;
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            target = em.find(Skill.class, integer);
            if(target == null) {
                throw new ApiException(404, "Skill with" + skill.getId() + "Does not exist");
            }
            if(skill.getName() != null) {
                target.setName(skill.getName());
            }
            if(skill.getCategory() != null) {
                target.setCategory(skill.getCategory());
            }
            if (skill.getDescription() != null){
                target.setDescription(skill.getDescription());
            }
            em.merge(target);
            em.getTransaction().commit();
            return target;
        }
        catch (ApiException ae) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
                throw ae;
            }
        }
        catch (PersistenceException pe) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();}
            throw new ApiException(500, "Persistence error");
        }
        catch (Exception e) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();}
            throw new ApiException(500, "unknown error");
        }
        finally {
            em.close();
        }
        return null;
    }

    public void deleteSkill(Integer integer) {
        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();

            Skill delete = em.find(Skill.class, integer);
            if(delete == null) {
                if (em.getTransaction().isActive()){
                    em.getTransaction().rollback();}
                throw new ApiException(404, "Skill with" + integer + "Does not exist");
            }
            for(Candidate candidate : delete.getCandidates()) {
                candidate.getSkills().remove(delete);
            }
            delete.getCandidates().clear();

            em.remove(delete);
            em.getTransaction().commit();
        }
        catch (PersistenceException pe) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();}
            throw new ApiException(500, "Persistence error");
        }
        catch (Exception e) {
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();}
            throw new ApiException(500, "unknown error");
        }
        finally {
            em.close();
        }
    }
}
