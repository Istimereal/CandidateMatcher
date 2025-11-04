package app.daos;

import app.entities.Skill;
import app.entities.Candidate;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

import java.util.HashSet;
import java.util.List;

public class CandidateDAO {

    private static  CandidateDAO instance;
    private static EntityManagerFactory emf;

    public CandidateDAO(){}

    public static CandidateDAO getInstance(EntityManagerFactory _emf) {

        if(instance==null){
            instance= new CandidateDAO();
            emf = _emf; }
        return instance;
    }

    public Candidate getCandidateById(int id) {
        try(EntityManager em=emf.createEntityManager()){
            Candidate found =em.find(Candidate.class, id);
            if(found == null)
                throw new ApiException(404,"Candidate not found");
            return found;
        }
        catch (Exception ex){
            throw new ApiException(500, "unexpected error");
        }
    }

    public List<Candidate> getAllCandidates(){
        try(EntityManager em=emf.createEntityManager()){

            TypedQuery<Candidate> query = em.createQuery(("SELECT c FROM Candidate c"),Candidate.class);
            return query.getResultList();
        }
        catch (PersistenceException pe){
            throw new ApiException(500,"Persistence error");
        }
        catch (Exception ex){
            throw new ApiException(500, "unexpected error");
        }
    }

    public Candidate createCandidate(Candidate Candidate) {
        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            em.persist(Candidate);
            em.getTransaction().commit();
        }
        catch (PersistenceException pe){
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();}
            throw new ApiException(500,"Persistence error");
        }
        catch (Exception ex){
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();}
            throw new ApiException(500, "unexpected error");
        }
        finally{
            em.close();
        }
        return Candidate;
    }

    public Candidate updateCandidate(Integer integer, Candidate Candidate){
        EntityManager em=emf.createEntityManager();

        try{
            em.getTransaction().begin();
            Candidate target = em.find(Candidate.class, integer);
            if(target == null){
                throw new ApiException(404,"Candidate with id "+integer+" not found");
            }
            if(Candidate.getName() !=null){
                target.setName(Candidate.getName());
            }
            if(Candidate.getPhoneNumber() !=null){
                target.setPhoneNumber(Candidate.getPhoneNumber());
            }
            em.merge(target);
            em.getTransaction().commit();
            return target;
        }
        catch (PersistenceException pe){
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();}
            throw new ApiException(500,"Persistence error");
        }
        catch (Exception ex){
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();}
            throw new ApiException(500, "unexpected error");
        }
        finally {
            em.close();
        }
    }

    public void deleteCandidate(Integer integer){
        EntityManager em=emf.createEntityManager();
        try{
            em.getTransaction().begin();
            Candidate target = em.find(Candidate.class, integer);
            if(target==null){
                throw new ApiException(404,"Candidate with id "+integer+" not found");
            }
            for (Skill s : new HashSet<>(target.getSkills())) {
                target.removeSkill(s);
            }
            em.remove(target);
            em.getTransaction().commit();
        }
        catch (PersistenceException pe){
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();}
            throw new ApiException(500,"Persistence error");
        }
        catch (Exception ex){
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();}
            throw new ApiException(500, "unexpected error");
        }
        finally {
            em.close();
        }
    }

    public void addSkillToCandidate(Integer CandidateId, Integer skillId ){
        EntityManager em=emf.createEntityManager();
        try{
            em.getTransaction().begin();
            Skill skill = em.find(Skill.class, skillId);
            if(skill == null){
                throw new ApiException(404,"Skill is not found: " + skillId);
            }
            Candidate Candidate = em.find(Candidate.class, CandidateId);
            if(Candidate == null){
                throw new ApiException(404,"Candidate is not found:  " + CandidateId);
            }
            Candidate.addSkill(skill);
            em.merge(Candidate);
            em.getTransaction().commit();
        }
        catch (PersistenceException pe){
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();}
            throw new ApiException(500,"Persistence error adding Skill to Candidate:");
        }
        catch (Exception ex){
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();}
            throw new ApiException(500, "unexpected error adding Skill to Candidate:");
        }
        finally {
            em.close();
        }
    }

}
