package com.wuli.badminton.service;

import com.wuli.badminton.pojo.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService {
    User findByUsername(String username);
    User findByEmail(String email);
    void save(User user);
    User getCurrentUser();
    
    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
} 