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
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user login and registration")
public class AuthController {
  
  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;
  private final ClientRepository clientRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder encoder;
  private final JwtUtils jwtUtils;

  @PostMapping("/login")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    log.info("Authenticating user: {}", loginRequest.getEmail());

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);
    
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();    
    List<String> roles = userDetails.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .collect(Collectors.toList());

    log.info("User {} authenticated successfully with roles: {}", loginRequest.getEmail(), roles);

    return ResponseEntity.ok(new JwtResponse(jwt, 
                         userDetails.getId(), 
                         userDetails.getUsername(), 
                         userDetails.getEmail(), 
                         roles));
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    log.info("Registering new user: {}", signUpRequest.getEmail());

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
        switch (role.toLowerCase()) {
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
        case "demo":
          Role demoRole = roleRepository.findByName(ERole.DEMO)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(demoRole);
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
        .phone(signUpRequest.getCelular())
        .cpf(signUpRequest.getCpf())
        .build();
    
    clientRepository.save(client);

    log.info("User {} registered successfully", signUpRequest.getEmail());
    return ResponseEntity.ok("User registered successfully!");
  }
}
