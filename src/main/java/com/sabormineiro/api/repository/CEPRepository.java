package com.sabormineiro.api.repository;

import com.sabormineiro.api.entity.CEP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CEPRepository extends JpaRepository<CEP, Long> {
  Optional<CEP> findByCep(String cep);
}
