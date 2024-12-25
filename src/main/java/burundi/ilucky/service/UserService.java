package burundi.ilucky.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import burundi.ilucky.model.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import burundi.ilucky.model.User;
import burundi.ilucky.payload.Response;
import burundi.ilucky.repository.UserRepository;

@Service
public class UserService {
	@Autowired
    private UserRepository userRepository;


	public User findByUserName(String username) {
		try {
			return userRepository.findByUsername(username);
		} catch (Exception e) {
			return null;
		}
	}

	
	public User saveUser(User user) {
		return userRepository.save(user);
	}
	public List<User> findAll() {
		return userRepository.findAll();
	}
	public List<UserDTO> findTop5(){
		 List<User> users = userRepository.findTop5();
		 return users.stream()
				 .map(UserDTO::new)
				 .collect(Collectors.toList());
	}


}
