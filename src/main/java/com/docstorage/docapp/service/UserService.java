package com.docstorage.docapp.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.docstorage.docapp.domain.Role;
import com.docstorage.docapp.domain.User;
import com.docstorage.docapp.repos.UserRepo;

@Service
@Transactional
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepo userRepo;
	
    @Autowired
    private MailSender mailSender;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
	
    @Value("${hostname}")
    private String hostname;
    
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return user;
	}
	
	public boolean addUser(User user) {
        User userFromDb = userRepo.findByUsername(user.getUsername());

        if (userFromDb != null) {
            return false;
        }

        user.setActive(false);
        user.setRoles(Collections.singleton(Role.USER));
        user.setActivationCode(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepo.save(user);

        sendMessage(user);

        return true;
    }
	
	private void sendMessage(User user) {
		if (!StringUtils.isEmpty(user.getEmail())) {
            String message = String.format(
                    "Hello, %s! \n" +
                            "Welcome to DocApp. Please, visit next link: http://%s/activate/%s",
                    user.getUsername(),
                    hostname,
                    user.getActivationCode()
            );

            mailSender.send(user.getEmail(), "Activation code", message);
        }
	}

	public boolean activateUser(String code) {
        User user = userRepo.findByActivationCode(code);

        if (user == null) {
            return false;
        }
        
        user.setActive(true);
        user.setActivationCode(null);

        userRepo.save(user);

        return true;
    }

	public List<User> findAll() {
		
		return userRepo.findAll();
	}

	public void saveUser(User user, String username, Map<String, String> form) {
		user.setUsername(username);

        Set<String> roles = Arrays.stream(Role.values())
                .map(Role::name)
                .collect(Collectors.toSet());

        user.getRoles().clear();

        for (String key : form.keySet()) {
            if (roles.contains(key)) {
                user.getRoles().add(Role.valueOf(key));
            }
        }

        userRepo.save(user);
		
	}

	public void deleteById(Long id) {
		userRepo.deleteById(id);
		
	}

	public Optional<User> findById(Long id) {
		return userRepo.findById(id);
	}
/*
	public void updateProfile(Long id, String password, String password2, String email) {
		User user = userRepo.findById(id).get();
		String userEmail = user.getEmail();
		String userPassword = user.getPassword();

        boolean isEmailChanged = (!email.isEmpty() && !email.equals(userEmail));

        if (isEmailChanged) {
            user.setEmail(email);

            if (!StringUtils.isEmpty(email)) {
                user.setActivationCode(UUID.randomUUID().toString());
                user.setActive(false);
            }
        }

        if (!StringUtils.isEmpty(password)) {
        	if(password.equals(password2)) {
        	user.setPassword(passwordEncoder.encode(password));
        	}
        }
        
        userRepo.save(user);
        
        if (isEmailChanged) {
            sendMessage(user);
        }
        
        if(!(userEmail.equals(userRepo.findById(id).get().getEmail()))||!(userPassword.equals(userRepo.findById(id).get().getPassword()))) {
        	deauthorization();
        }
        
    }
	
	public void deauthorization() {
		SecurityContext context = SecurityContextHolder.getContext();
    	Authentication authentication = context.getAuthentication();
    	authentication.setAuthenticated(false);
	}*/
}

