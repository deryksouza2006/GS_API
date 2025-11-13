package com.taskmanager.service;

import com.taskmanager.model.User;
import com.taskmanager.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    @Transactional
    public User create(User user) {
        // Validar username único
        if (userRepository.findByUsername(user.username).isPresent()) {
            throw new IllegalArgumentException("Username já está em uso");
        }

        // Validar email único
        if (userRepository.findByEmail(user.email).isPresent()) {
            throw new IllegalArgumentException("Email já está em uso");
        }

        // Criptografar senha
        user.password = hashPassword(user.password);

        userRepository.persist(user);
        return user;
    }

    public Optional<User> findById(Long id) {
        return userRepository.findByIdOptional(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> findAll() {
        return userRepository.listAll();
    }

    @Transactional
    public User update(Long id, User updatedUser) {
        User user = userRepository.findByIdOptional(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        // Validar username único
        if (!user.username.equals(updatedUser.username)) {
            if (userRepository.findByUsername(updatedUser.username).isPresent()) {
                throw new IllegalArgumentException("Username já está em uso");
            }
            user.username = updatedUser.username;
        }

        // Validar email único
        if (!user.email.equals(updatedUser.email)) {
            if (userRepository.findByEmail(updatedUser.email).isPresent()) {
                throw new IllegalArgumentException("Email já está em uso");
            }
            user.email = updatedUser.email;
        }

        // Atualizar senha se fornecida
        if (updatedUser.password != null && !updatedUser.password.isEmpty()) {
            user.password = hashPassword(updatedUser.password);
        }

        return user;
    }

    @Transactional
    public boolean delete(Long id) {
        return userRepository.deleteById(id);
    }

    public Optional<User> authenticate(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent() && user.get().password.equals(hashPassword(password))) {
            return user;
        }
        return Optional.empty();
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao criptografar senha", e);
        }
    }
}