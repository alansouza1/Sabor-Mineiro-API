package com.sabormineiro.api.repository;

import com.sabormineiro.api.entity.Client;
import com.sabormineiro.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
  Optional<Client> findByUser(User user);
  Optional<Client> findByCpf(String cpf);
  Boolean existsByCpf(String cpf);
}
