package com.sabormineiro.api;

import com.sabormineiro.api.entity.Category;
import com.sabormineiro.api.entity.ERole;
import com.sabormineiro.api.entity.Product;
import com.sabormineiro.api.entity.Role;
import com.sabormineiro.api.entity.User;
import com.sabormineiro.api.repository.ProductRepository;
import com.sabormineiro.api.repository.RoleRepository;
import com.sabormineiro.api.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication
public class SaborMineiroApiApplication {

	@Value("${sabormineiro.admin.email}")
	private String adminEmail;

	@Value("${sabormineiro.admin.password}")
	private String adminPassword;

	@Value("${sabormineiro.demo.email}")
	private String demoEmail;

	@Value("${sabormineiro.demo.password}")
	private String demoPassword;

	public static void main(String[] args) {
		SpringApplication.run(SaborMineiroApiApplication.class, args);
	}

	@Bean
	CommandLineRunner init(RoleRepository roleRepository, UserRepository userRepository, 
						  ProductRepository productRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			// Roles - Seed safely without ID conflicts
			for (ERole eRole : ERole.values()) {
				if (roleRepository.findByName(eRole).isEmpty()) {
					try {
						Role role = new Role();
						role.setName(eRole);
						roleRepository.save(role);
					} catch (Exception e) {
						// Role might have been created by concurrent process or manual script
						System.out.println("Role already exists or error saving: " + eRole);
					}
				}
			}

			// Admin User
			userRepository.findByEmail(adminEmail).ifPresentOrElse(
				user -> {
					user.setPassword(passwordEncoder.encode(adminPassword));
					userRepository.save(user);
				},
				() -> {
					Role adminRole = roleRepository.findByName(ERole.ADMIN).get();
					User admin = User.builder()
							.name("Admin")
							.email(adminEmail)
							.password(passwordEncoder.encode(adminPassword))
							.roles(Set.of(adminRole))
							.build();
					userRepository.save(admin);
				}
			);

			// Demo User
			userRepository.findByEmail(demoEmail).ifPresentOrElse(
				user -> {
					user.setName("Demo");
					user.setPassword(passwordEncoder.encode(demoPassword));
					
					// Force assign DEMO role if not present
					Role demoRole = roleRepository.findByName(ERole.DEMO).get();
					if (!user.getRoles().contains(demoRole)) {
						user.getRoles().add(demoRole);
					}
					
					userRepository.save(user);
				},
				() -> {
					Role demoRole = roleRepository.findByName(ERole.DEMO).get();
					User demo = User.builder()
							.name("Demo")
							.email(demoEmail)
							.password(passwordEncoder.encode(demoPassword))
							.roles(new java.util.HashSet<>(java.util.List.of(demoRole)))
							.build();
					userRepository.save(demo);
				}
			);


			// Products
			if (productRepository.count() == 0) {
				List<Product> products = List.of(
					Product.builder().name("Tutu à Mineira").description("Feijão refogado e engrossado com farinha de mandioca.").price(new BigDecimal("32.23")).imageUrl("/images/tutu.webp").availableQuantity(30).needsProduction(true).category(Category.PRATOS_PRINCIPAIS).build(),
					Product.builder().name("Frango com Quiabo").description("Coxas e sobrecoxas de frango cozidas em molho caseiro.").price(new BigDecimal("30.32")).imageUrl("/images/frango-com-quiabo.webp").availableQuantity(30).needsProduction(true).category(Category.PRATOS_PRINCIPAIS).build(),
					Product.builder().name("Feijoada Mineira").description("Feijoada completa com carnes suínas.").price(new BigDecimal("45.00")).imageUrl("/images/feijoada.webp").availableQuantity(30).needsProduction(true).category(Category.PRATOS_PRINCIPAIS).build(),
					Product.builder().name("Feijão Tropeiro").description("Feijão tropeiro com linguiça, bacon, torresmo.").price(new BigDecimal("34.00")).imageUrl("/images/feijao-tropeiro.png.webp").availableQuantity(30).needsProduction(true).category(Category.PRATOS_PRINCIPAIS).build(),
					Product.builder().name("Suco de Laranja Natural").description("Suco fresco de laranjas selecionadas.").price(new BigDecimal("10.00")).imageUrl("/images/suco-de-laranja.webp").availableQuantity(50).needsProduction(false).category(Category.BEBIDAS).build(),
					Product.builder().name("Pão de Queijo").description("Porção com 6 unidades do autêntico pão de queijo mineiro.").price(new BigDecimal("15.00")).imageUrl("/images/pao-de-queijo.webp").availableQuantity(100).needsProduction(true).category(Category.ENTRADAS).build(),
					Product.builder().name("Bolinho de Mandioca").description("Porção com 8 bolinhos de mandioca recheados.").price(new BigDecimal("26.00")).imageUrl("/images/bolinho-de-mandioca.webp").availableQuantity(40).needsProduction(true).category(Category.ENTRADAS).build(),
					Product.builder().name("Pastel de Angu").description("Tradicional pastel mineiro feito com massa de milho.").price(new BigDecimal("22.00")).imageUrl("/images/pastel-de-angu.webp").availableQuantity(50).needsProduction(true).category(Category.ENTRADAS).build(),
					Product.builder().name("Galinhada Mineira").description("Arroz cozido com frango caipira, açafrão.").price(new BigDecimal("42.00")).imageUrl("/images/galinhada.webp").availableQuantity(25).needsProduction(true).category(Category.PRATOS_PRINCIPAIS).build(),
					Product.builder().name("Cerveja Artesanal").description("Cerveja artesanal produzida nas montanhas de Minas.").price(new BigDecimal("18.00")).imageUrl("/images/cerveja-artesanal.webp").availableQuantity(60).needsProduction(false).category(Category.BEBIDAS).build(),
					Product.builder().name("Goiabada Cascão com Queijo").description("A clássica sobremesa Romeu e Julieta.").price(new BigDecimal("16.00")).imageUrl("/images/goiabada-queijo.webp").availableQuantity(35).needsProduction(false).category(Category.SOBREMESAS).build(),
					Product.builder().name("Pudim de Leite").description("Pudim de leite condensado cremoso.").price(new BigDecimal("14.00")).imageUrl("/images/pudim.webp").availableQuantity(20).needsProduction(true).category(Category.SOBREMESAS).build(),
					Product.builder().name("Refrigerante Lata").description("Refrigerante em lata 350ml.").price(new BigDecimal("7.00")).imageUrl("/images/refrigerante.webp").availableQuantity(150).needsProduction(false).category(Category.BEBIDAS).build(),
					Product.builder().name("Isca de Tilápia").description("Porção de iscas de tilápia empanadas e fritas.").price(new BigDecimal("42.00")).imageUrl("/images/isca-de-tilapia.webp").availableQuantity(25).needsProduction(true).category(Category.ENTRADAS).build()
				);
				productRepository.saveAll(products);
			}
		};
	}
}
