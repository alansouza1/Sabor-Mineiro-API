package com.sabormineiro.api.repository;

import com.sabormineiro.api.entity.Address;
import com.sabormineiro.api.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
  List<Address> findByClient(Client client);
}
