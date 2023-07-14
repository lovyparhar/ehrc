package com.demographic.demographic.repository;

import com.demographic.demographic.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByAadhar(String aadhar);
    Boolean existsByAadhar(String aadhar);
}

