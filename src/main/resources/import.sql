-- Roles
INSERT INTO roles(id, name) VALUES(1, 'ADMIN') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles(id, name) VALUES(2, 'COZINHEIRO') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles(id, name) VALUES(3, 'ATENDENTE') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles(id, name) VALUES(4, 'AUXILIAR_COZINHA') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles(id, name) VALUES(5, 'ENTREGADOR') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles(id, name) VALUES(6, 'CLIENTE') ON CONFLICT (name) DO NOTHING;

-- Default Admin (Password is 'admin123')
INSERT INTO users(name, email, password) 
VALUES('Admin', 'admin@sabormineiro.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.7uqqQ8a') 
ON CONFLICT (email) DO NOTHING;

INSERT INTO user_roles(user_id, role_id) 
SELECT u.id, r.id FROM users u, roles r 
WHERE u.email = 'admin@sabormineiro.com' AND r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

-- Products with local images
INSERT INTO products (nome, descricao, preco, url_imagem, qtd_disp, precisa_produzir, categoria) VALUES ('Tutu à Mineira', 'Feijão refogado e engrossado com farinha de mandioca, servido com torresmo crocante, couve refogada, ovo frito e arroz branco.', 32.23, '/images/tutu.webp', 30, true, 'PRATOS_PRINCIPAIS');
INSERT INTO products (nome, descricao, preco, url_imagem, qtd_disp, precisa_produzir, categoria) VALUES ('Frango com Quiabo', 'Coxas e sobrecoxas de frango cozidas em molho caseiro, acompanhadas de quiabo refogado, angu cremoso e arroz soltinho.', 30.32, '/images/frango-com-quiabo.webp', 30, true, 'PRATOS_PRINCIPAIS');
INSERT INTO products (nome, descricao, preco, url_imagem, qtd_disp, precisa_produzir, categoria) VALUES ('Feijoada Mineira', 'Feijoada completa com carnes suínas, linguiça, paio, costelinha, servida com couve, farofa, laranja e arroz branco.', 45.00, '/images/feijoada.webp', 30, true, 'PRATOS_PRINCIPAIS');
INSERT INTO products (nome, descricao, preco, url_imagem, qtd_disp, precisa_produzir, categoria) VALUES ('Feijão Tropeiro', 'Feijão tropeiro com linguiça, bacon, torresmo, ovo, farofa e couve crocante, servido com arroz branco.', 34.00, '/images/feijao-tropeiro.png.webp', 30, true, 'PRATOS_PRINCIPAIS');
INSERT INTO products (nome, descricao, preco, url_imagem, qtd_disp, precisa_produzir, categoria) VALUES ('Suco de Laranja Natural', 'Suco fresco de laranjas selecionadas, adoçado ou natural.', 10.00, '/images/suco-de-laranja.webp', 50, false, 'BEBIDAS');
INSERT INTO products (nome, descricao, preco, url_imagem, qtd_disp, precisa_produzir, categoria) VALUES ('Pão de Queijo', 'Porção com 6 unidades do autêntico pão de queijo mineiro, crocante por fora e macio por dentro.', 15.00, '/images/pao-de-queijo.webp', 100, true, 'ENTRADAS');
INSERT INTO products (nome, descricao, preco, url_imagem, qtd_disp, precisa_produzir, categoria) VALUES ('Bolinho de Mandioca', 'Porção com 8 bolinhos de mandioca recheados com queijo canastra derretido.', 26.00, '/images/bolinho-de-mandioca.webp', 40, true, 'ENTRADAS');
INSERT INTO products (nome, descricao, preco, url_imagem, qtd_disp, precisa_produzir, categoria) VALUES ('Pastel de Angu', 'Tradicional pastel mineiro feito com massa de milho, recheado com carne moída temperada ou queijo.', 22.00, '/images/pastel-de-angu.webp', 50, true, 'ENTRADAS');
INSERT INTO products (nome, descricao, preco, url_imagem, qtd_disp, precisa_produzir, categoria) VALUES ('Galinhada Mineira', 'Arroz cozido com frango caipira, açafrão, milho verde e temperos típicos. Um clássico das fazendas mineiras.', 42.00, '/images/galinhada.webp', 25, true, 'PRATOS_PRINCIPAIS');
INSERT INTO products (nome, descricao, preco, url_imagem, qtd_disp, precisa_produzir, categoria) VALUES ('Cerveja Artesanal', 'Cerveja artesanal produzida nas montanhas de Minas (600ml). Consulte os estilos disponíveis.', 18.00, '/images/cerveja-artesanal.webp', 60, false, 'BEBIDAS');
INSERT INTO products (nome, descricao, preco, url_imagem, qtd_disp, precisa_produzir, categoria) VALUES ('Goiabada Cascão com Queijo', 'A clássica sobremesa Romeu e Julieta: goiabada cascão artesanal servida com queijo minas frescal.', 16.00, '/images/goiabada-queijo.webp', 35, false, 'SOBREMESAS');
INSERT INTO products (nome, descricao, preco, url_imagem, qtd_disp, precisa_produzir, categoria) VALUES ('Pudim de Leite', 'Pudim de leite condensado cremoso, sem furinhos, com uma deliciosa calda de caramelo.', 14.00, '/images/pudim.webp', 20, true, 'SOBREMESAS');
INSERT INTO products (nome, descricao, preco, url_imagem, qtd_disp, precisa_produzir, categoria) VALUES ('Refrigerante Lata', 'Refrigerante em lata 350ml (Cola, Guaraná, Laranja ou Limão).', 7.00, '/images/refrigerante.webp', 150, false, 'BEBIDAS');
INSERT INTO products (nome, descricao, preco, url_imagem, qtd_disp, precisa_produzir, categoria) VALUES ('Isca de Tilápia', 'Porção de iscas de tilápia empanadas e fritas, acompanhadas de molho tártaro caseiro.', 42.00, '/images/isca-de-tilapia.webp', 25, true, 'ENTRADAS');
