package com.backend.repos;

import com.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Override
    List<User> findAll();


    @Query("select u from User u where u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("select u from User u where u.token = :token")
    Optional<User> findByToken(@Param("token") String token);

    @Query("select u from User u where u.email = :email and u.password = :password")
    Optional<User> findByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    @Query("select u from User u where u.username = :username and u.password = :password")
    Optional<User> findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);


    @Override
    <S extends User> S save(S entity);

    @Query("select u from User u where u.username like %:usernameOrEmail% or u.email like %:usernameOrEmail%")
    List<User> findAllLike(String usernameOrEmail);
}