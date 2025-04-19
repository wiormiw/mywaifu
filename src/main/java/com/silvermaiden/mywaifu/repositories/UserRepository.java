package com.silvermaiden.mywaifu.repositories;

import com.silvermaiden.mywaifu.models.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(value = "User.withRoles")
    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username")
    boolean existsByUsername(@Param("username") String username);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email")
    boolean existsByEmail(@Param("email") String email);

    @EntityGraph(value = "User.withRoles")
    @Override
    @NonNull
    Optional<User> findById(@NonNull Long id);

    @Override
    boolean existsById(@NonNull Long id);

    @Override
    void deleteById(@NonNull Long id);

    @EntityGraph(value = "User.withRoles")
    @Override
    @NonNull
    List<User> findAll();

    @EntityGraph(value = "User.withRoles")
    @Override
    @NonNull
    Page<User> findAll(@NonNull Pageable pageable);
}
