package com.prospect.crm.security;

import com.prospect.crm.model.Users;
import com.prospect.crm.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
        return createUserDetails(user);
    }
    
    /**
     * ID ile kullanıcı yükler
     */
    public UserDetails loadUserById(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));
        
        return createUserDetails(user);
    }
    
    /**
     * UserDetails oluşturur
     */
    private UserDetails createUserDetails(Users user) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        
        // Temel USER yetkisi
        authorities.add(new SimpleGrantedAuthority("USER"));
        
        // Kullanıcının rolüne göre yetki ekle
        if (user.getRoleId() != null && user.getRoleId().getName() != null) {
            String roleName = user.getRoleId().getName().toUpperCase();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));
            
            // Admin rolü için ek yetkiler
            if ("ADMIN".equals(roleName)) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }
        }
        
        // User ID'yi username'e ekle (format: email:userId)
        String usernameWithId = user.getEmail() + ":" + user.getId();
        
        return User.builder()
                .username(usernameWithId)
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(!user.getIsActive())
                .credentialsExpired(false)
                .disabled(!user.getIsActive())
                .build();
    }
} 