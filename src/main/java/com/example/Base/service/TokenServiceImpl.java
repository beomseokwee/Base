package com.example.Base.service;

import com.example.Base.domain.dto.UserDTO;
import com.example.Base.domain.entity.UserEntity;
import com.example.Base.repository.UserRepository;
import com.example.Base.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
@RequiredArgsConstructor
@Service
public class TokenServiceImpl implements TokenService{

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenProvider tokenProvider;

    @Override
    public void loginMethod(UserDTO userDTO, HttpServletResponse response) {

        UserEntity info = userRepository.findByEmail(userDTO.getEmail());

        if(info == null){
            throw new UsernameNotFoundException("User not found in the database");
        }

        else {
            String password = info.getPassword();
            boolean verify = passwordEncoder.matches(userDTO.getPassword(), password);

            if(verify){
                tokenProvider.createToken(userDTO, response);

            }

            else {
                throw new RuntimeException("WrongPassword");
            }
        }
    }
}
