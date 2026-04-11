package com.sabormineiro.api.controller;

import com.sabormineiro.api.config.JwtUtils;
import com.sabormineiro.api.dto.JwtResponse;
import com.sabormineiro.api.dto.LoginRequest;
import com.sabormineiro.api.dto.SignupRequest;
import com.sabormineiro.api.entity.Client;
import com.sabormineiro.api.entity.ERole;
import com.sabormineiro.api.entity.Role;
import com.sabormineiro.api.entity.User;
import com.sabormineiro.api.repository.ClientRepository;
import com.sabormineiro.api.repository.RoleRepository;
import com.sabormineiro.api.repository.UserRepository;
import com.sabormineiro.api.service.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  ClientRepository clientRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @PostMapping("/login")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);
    
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();    
    List<String> roles = userDetails.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .collect(Collectors.toList());

    return ResponseEntity.ok(new JwtResponse(jwt, 
                         userDetails.getId(), 
                         userDetails.getUsername(), 
                         userDetails.getEmail(), 
                         roles));
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity
          .badRequest()
          .body("Error: Email is already in use!");
    }

    if (clientRepository.existsByCpf(signUpRequest.getCpf())) {
        return ResponseEntity
            .badRequest()
            .body("Error: CPF is already in use!");
    }

    // Create new user's account
    User user = User.builder()
        .name(signUpRequest.getName())
        .email(signUpRequest.getEmail())
        .password(encoder.encode(signUpRequest.getPassword()))
        .build();

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = roleRepository.findByName(ERole.CLIENTE)
          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        switch (role) {
        case "admin":
          Role adminRole = roleRepository.findByName(ERole.ADMIN)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(adminRole);
          break;
        case "cozinheiro":
          Role cozinheiroRole = roleRepository.findByName(ERole.COZINHEIRO)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(cozinheiroRole);
          break;
        default:
          Role userRole = roleRepository.findByName(ERole.CLIENTE)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(userRole);
        }
      });
    }

    user.setRoles(roles);
    userRepository.save(user);

    Client client = Client.builder()
        .user(user)
        .celular(signUpRequest.getCelular())
        .cpf(signUpRequest.getCpf())
        .build();
    
    clientRepository.save(client);

    return ResponseEntity.ok("User registered successfully!");
  }
}
