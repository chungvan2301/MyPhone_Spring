package com.example.demo.repository;

import com.example.demo.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepo extends JpaRepository <Address, Long> {
    List<Address> findByUserIdOrderByAddressDefaultDesc (Long userId);

    Optional<Address> findByUserIdAndAddressDefault (Long userId, Boolean addressDefault);
}
