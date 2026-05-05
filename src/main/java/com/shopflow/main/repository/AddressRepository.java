package com.shopflow.main.repository;

import com.shopflow.main.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserId(Long userId);

    @Modifying
    @Query("UPDATE Address a SET a.principal = false WHERE a.user.id = :userId")
    void clearPrincipal(@Param("userId") Long userId);
}