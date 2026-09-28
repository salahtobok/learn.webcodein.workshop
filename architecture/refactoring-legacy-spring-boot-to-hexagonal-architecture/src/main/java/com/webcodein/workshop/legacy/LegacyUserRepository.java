package com.webcodein.workshop.legacy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LegacyUserRepository extends JpaRepository<User, Long> {
}
