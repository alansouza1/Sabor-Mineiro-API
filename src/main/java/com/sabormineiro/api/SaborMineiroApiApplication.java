package com.sabormineiro.api;

import com.sabormineiro.api.entity.ERole;
import com.sabormineiro.api.entity.Role;
import com.sabormineiro.api.entity.User;
import com.sabormineiro.api.repository.RoleRepository;
import com.sabormineiro.api.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootApplication
public class SaborMineiroApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaborMineiroApiApplication.class, args);
	}

	@Bean
	CommandLineRunner init(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			for (ERole eRole : ERole.values()) {
				if (roleRepository.findByName(eRole).isEmpty()) {
					Role role = new Role();
					role.setName(eRole);
					roleRepository.save(role);
				}
			}

			userRepository.findByEmail("admin@sabormineiro.com").ifPresentOrElse(
				user -> {
					user.setPassword(passwordEncoder.encode("admin123"));
					userRepository.save(user);
				},
				() -> {
					Role adminRole = roleRepository.findByName(ERole.ADMIN).get();
					User admin = User.builder()
							.name("Admin")
							.email("admin@sabormineiro.com")
							.password(passwordEncoder.encode("admin123"))
							.roles(Set.of(adminRole))
							.build();
					userRepository.save(admin);
				}
			);
		};
	}
}
