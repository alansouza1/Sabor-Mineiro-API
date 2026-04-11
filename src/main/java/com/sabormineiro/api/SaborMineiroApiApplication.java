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

@SpringBootApplication
public class SaborMineiroApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaborMineiroApiApplication.class, args);
	}

	@Bean
	CommandLineRunner init(RoleRepository roleRepository, UserRepository userRepository, 
						  ProductRepository productRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			// Roles
			for (ERole eRole : ERole.values()) {
				if (roleRepository.findByName(eRole).isEmpty()) {
					Role role = new Role();
					role.setName(eRole);
					roleRepository.save(role);
				}
			}

			// Admin User
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

			// Products
			if (productRepository.count() == 0) {
				List<Product> products = List.of(
					Product.builder().nome("Tutu à Mineira").descricao("Feijão refogado e engrossado com farinha de mandioca.").preco(new BigDecimal("32.23")).urlImagem("/images/tutu.webp").qtdDisp(30).precisaProduzir(true).categoria(Category.PRATOS_PRINCIPAIS).build(),
					Product.builder().nome("Frango com Quiabo").descricao("Coxas e sobrecoxas de frango cozidas em molho caseiro.").preco(new BigDecimal("30.32")).urlImagem("/images/frango-com-quiabo.webp").qtdDisp(30).precisaProduzir(true).categoria(Category.PRATOS_PRINCIPAIS).build(),
					Product.builder().nome("Feijoada Mineira").descricao("Feijoada completa com carnes suínas.").preco(new BigDecimal("45.00")).urlImagem("/images/feijoada.webp").qtdDisp(30).precisaProduzir(true).categoria(Category.PRATOS_PRINCIPAIS).build(),
					Product.builder().nome("Feijão Tropeiro").descricao("Feijão tropeiro com linguiça, bacon, torresmo.").preco(new BigDecimal("34.00")).urlImagem("/images/feijao-tropeiro.png.webp").qtdDisp(30).precisaProduzir(true).categoria(Category.PRATOS_PRINCIPAIS).build(),
					Product.builder().nome("Suco de Laranja Natural").descricao("Suco fresco de laranjas selecionadas.").preco(new BigDecimal("10.00")).urlImagem("/images/suco-de-laranja.webp").qtdDisp(50).precisaProduzir(false).categoria(Category.BEBIDAS).build(),
					Product.builder().nome("Pão de Queijo").descricao("Porção com 6 unidades do autêntico pão de queijo mineiro.").preco(new BigDecimal("15.00")).urlImagem("/images/pao-de-queijo.webp").qtdDisp(100).precisaProduzir(true).categoria(Category.ENTRADAS).build(),
					Product.builder().nome("Bolinho de Mandioca").descricao("Porção com 8 bolinhos de mandioca recheados.").preco(new BigDecimal("26.00")).urlImagem("/images/bolinho-de-mandioca.webp").qtdDisp(40).precisaProduzir(true).categoria(Category.ENTRADAS).build(),
					Product.builder().nome("Pastel de Angu").descricao("Tradicional pastel mineiro feito com massa de milho.").preco(new BigDecimal("22.00")).urlImagem("/images/pastel-de-angu.webp").qtdDisp(50).precisaProduzir(true).categoria(Category.ENTRADAS).build(),
					Product.builder().nome("Galinhada Mineira").descricao("Arroz cozido com frango caipira, açafrão.").preco(new BigDecimal("42.00")).urlImagem("/images/galinhada.webp").qtdDisp(25).precisaProduzir(true).categoria(Category.PRATOS_PRINCIPAIS).build(),
					Product.builder().nome("Cerveja Artesanal").descricao("Cerveja artesanal produzida nas montanhas de Minas.").preco(new BigDecimal("18.00")).urlImagem("/images/cerveja-artesanal.webp").qtdDisp(60).precisaProduzir(false).categoria(Category.BEBIDAS).build(),
					Product.builder().nome("Goiabada Cascão com Queijo").descricao("A clássica sobremesa Romeu e Julieta.").preco(new BigDecimal("16.00")).urlImagem("/images/goiabada-queijo.webp").qtdDisp(35).precisaProduzir(false).categoria(Category.SOBREMESAS).build(),
					Product.builder().nome("Pudim de Leite").descricao("Pudim de leite condensado cremoso.").preco(new BigDecimal("14.00")).urlImagem("/images/pudim.webp").qtdDisp(20).precisaProduzir(true).categoria(Category.SOBREMESAS).build(),
					Product.builder().nome("Refrigerante Lata").descricao("Refrigerante em lata 350ml.").preco(new BigDecimal("7.00")).urlImagem("/images/refrigerante.webp").qtdDisp(150).precisaProduzir(false).categoria(Category.BEBIDAS).build(),
					Product.builder().nome("Isca de Tilápia").descricao("Porção de iscas de tilápia empanadas e fritas.").preco(new BigDecimal("42.00")).urlImagem("/images/isca-de-tilapia.webp").qtdDisp(25).precisaProduzir(true).categoria(Category.ENTRADAS).build()
				);
				productRepository.saveAll(products);
			}
		};
	}
}
