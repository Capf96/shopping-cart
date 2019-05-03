package shoppingcart.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import shoppingcart.models.AppUser;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

@Repository
@Transactional
public class AppUserDAO {
    @Autowired
    private EntityManager entityManager;

    public AppUser findByUsername(String username) {
        try {
            String sql = "Select e from " + AppUser.class.getName() + " e where e.username = :username";

            Query query = entityManager.createQuery(sql, AppUser.class);
            query.setParameter("username", username);

            return (AppUser) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

//    public static void main(String[] args) {
//        System.out.println(AppUser.class.getName());
//    }
}
