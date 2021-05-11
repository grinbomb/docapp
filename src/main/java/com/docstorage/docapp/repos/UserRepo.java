package com.docstorage.docapp.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.docstorage.docapp.domain.User;


public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByActivationCode(String activationCode);
}
