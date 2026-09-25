package ee.liftertrans.persistence.repository;

import ee.liftertrans.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

//Repository = koht, kust Service küsib andmebaasi andmeid.
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("select u from User u where u.email = :email and u.passwordHash = :passwordHash and u.status = :status")
    Optional<User> findUserBy(String email, String passwordHash, String status);

}