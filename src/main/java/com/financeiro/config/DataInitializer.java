package com.financeiro.config;

import com.financeiro.model.Categoria;
import com.financeiro.model.Usuario;
import com.financeiro.repository.CategoriaRepository;
import com.financeiro.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.HashSet;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UsuarioRepository usuarioRepository, 
                                      CategoriaRepository categoriaRepository, 
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            if (usuarioRepository.count() == 0) {
                Usuario admin = new Usuario();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setNome("Administrador");
                admin.setRoles(new HashSet<>(Arrays.asList("ROLE_ADMIN")));
                usuarioRepository.save(admin);
                System.out.println("Usuário 'admin' criado com senha 'admin123'");
            }

            if (categoriaRepository.count() == 0) {
                Arrays.asList("Alimentação", "Transporte", "Saúde", "Educação", "Lazer", "Outros")
                        .forEach(nome -> {
                            Categoria cat = new Categoria();
                            cat.setNome(nome);
                            categoriaRepository.save(cat);
                        });
                System.out.println("Categorias iniciais criadas.");
            }
        };
    }
}
