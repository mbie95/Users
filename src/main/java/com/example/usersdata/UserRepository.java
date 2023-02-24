package com.example.usersdata;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 *
 * @author marcin
 */
@RepositoryRestResource
public interface UserRepository extends JpaRepository<User, Integer> {
    public User save(User user);
    public boolean existsByPhoneNumber(Integer phoneNumber);
    public long count();
    public List<User> findAll();
    public Page<User> findAll(Pageable page);
    public Optional<User> findByLastName(String lastName);
    public void deleteAll();
    
    @Modifying
    @Query("delete from User user where user.phoneNumber=:phoneNumber")
    public void deleteByPhoneNumber(@Param("phoneNumber") Integer phoneNumber);
}
