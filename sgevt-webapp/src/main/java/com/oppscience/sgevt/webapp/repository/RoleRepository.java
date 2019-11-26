package com.oppscience.sgevt.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.oppscience.sgevt.webapp.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{
}
