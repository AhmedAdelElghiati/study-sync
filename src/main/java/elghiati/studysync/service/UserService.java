package elghiati.studysync.service;

import elghiati.studysync.entity.User;
import org.springframework.stereotype.Service;

import elghiati.studysync.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {
    final private UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean existsByUserName(String userName) {
        return userRepository.existsByUserName(userName);
    }
    public Optional<User> findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }


}