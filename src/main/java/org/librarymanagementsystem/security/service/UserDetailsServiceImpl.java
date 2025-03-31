package org.librarymanagementsystem.security.service;

import lombok.extern.slf4j.Slf4j;
import org.librarymanagementsystem.model.User;
import org.librarymanagementsystem.repository.UserRepository;
import org.librarymanagementsystem.repository.UserRoleMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleMappingRepository userRoleMappingRepository;


    public UserDetails loadUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));
        // Fetch roles as a list of Strings
        List<String> roles = userRoleMappingRepository.findRolesByUserId(userId);

        return UserDetailsImpl.build(user, roles);
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return UserDetailsImpl.build(user);
    }

    /*@Override
    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), new ArrayList<>()
        );
    }*/

   /* private UserDetailsImpl buildUserDetails(User user) {
        List<GrantedAuthority> authorities = userRoleMappingRepository.findByUserId(user.getId())
                .stream()
                .map(userRoleMapping -> new SimpleGrantedAuthority(userRoleMapping.getRole().getRoleName().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.getPassword(),
                user.getAccountNonExpired(),
                user.getAccountNonLocked(),
                user.getCredentialsNonExpired(),
                user.getEnabled(),
                authorities
        );
    }*/
}
