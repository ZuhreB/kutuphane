package com.kutuphane.Service;

import com.kutuphane.Entity.User;
import com.kutuphane.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository ){
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User saveUser(User user) {
        if(userRepository.existsByUsername(user.getUsername()) || userRepository.existsByEmail(user.getEmail()))
        {
            throw new RuntimeException("Bu hesap zaten var");
        }
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> findByUsername(String username) {

        return userRepository.findByUsername(username);
    }

    public User loginUser(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            System.out.println("-----------------------------------------");
            System.out.println("[DEBUG] Formdan Gelen Şifre: '" + password + "'");
            System.out.println("[DEBUG] Veritabanındaki Şifre: '" + user.getPassword() + "'");
            System.out.println("[DEBUG] Şifreler Eşleşiyor mu? " + password.equals(user.getPassword()));
            System.out.println("-----------------------------------------");

            if (password.equals(user.getPassword())) {
                return user;
            }
        }
        return null;
    }
}