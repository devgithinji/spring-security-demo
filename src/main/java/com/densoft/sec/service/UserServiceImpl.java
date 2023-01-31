package com.densoft.sec.service;

import com.densoft.sec.errorhandling.APIException;
import com.densoft.sec.model.User;
import com.densoft.sec.repository.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepo userRepo;

    public UserServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getExistingUser(username);
    }
    @Override
    public User getUser(String email) {
        return getExistingUser(email);
    }

    private User getExistingUser(String username) {
        return userRepo.findUserByEmail(username).orElseThrow(() -> new APIException("no user found with email: " + username));
    }
}
