package com.sabormineiro.api.dto;

import lombok.Data;
import java.util.Set;

@Data
public class SignupRequest {
  private String name;
  private String email;
  private String password;
  private String cpf;
  private String celular;
  private Set<String> role;
}
