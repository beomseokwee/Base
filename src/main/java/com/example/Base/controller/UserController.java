package com.example.Base.controller;

import com.example.Base.domain.entity.UserEntity;
import com.example.Base.domain.dto.ResponseDTO;
import com.example.Base.domain.dto.UserDTO;
import com.example.Base.service.TokenServiceImpl;
import com.example.Base.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.*;

@Log4j2
@RestController // @Controller + @ResponseBody
@RequiredArgsConstructor //생성자 주입
@RequestMapping("/api")//아래에 있는 모든 mapping은 문자열/api를 포함해야한다.
public class UserController {
    private final UserService userService;
    private final TokenServiceImpl tokenService;

    @GetMapping("/users") //모든 유저 불러온다
    //ResponseEntity는  httpentity를 상속받는 결과 데이터와 HTTP 상태 코드를 직접 제어할 수 있는 클래스이고, 응답으로 변환될 정보를 모두 담은 요소들을 객체로 사용 된다.
    public ResponseEntity<List<UserEntity>>getUsers(){
        return ResponseEntity.ok().body(userService.getUsers()); //ResponseEntity.ok() => 200 OK status 코드를 반환하는 빌더 메서드
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody  UserDTO userDTO, HttpServletResponse response){
        try {
             tokenService.loginMethod(userDTO, response);

            return ResponseEntity.ok().body("로그인 성공!");

        } catch (Exception e) {
            log.info("error");
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity
                    .badRequest()
                    .body(responseDTO);
        }
    }

    @PostMapping("/user/save")
    public ResponseEntity saveUser(@RequestBody UserDTO userDTO) {
        try {
            userDTO.setRole("ROLE_USER");

            URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/save").toUriString());// = localhost8080:/api/user/save
            return ResponseEntity.created(uri).body(userService.saveUser(userDTO)); //201 Created => HTTP 201 Created는 요청이 성공적으로 처리되었으며, 자원이 생성되었음을 나타내는 성공 상태 응답 코드(URI 필요)

        } catch (Exception e) {
            log.info("error");
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity
                    .badRequest()
                    .body(responseDTO);
        }
    }

    @PostMapping("/helper/save")
    public ResponseEntity saveHelper(@RequestBody UserDTO userDTO) {
        try {
            userDTO.setRole("ROLE_HELPER");

            URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/helper/save").toUriString());// = localhost8080:/api/user/save
            return ResponseEntity.created(uri).body(userService.saveUser(userDTO)); //201 Created => HTTP 201 Created는 요청이 성공적으로 처리되었으며, 자원이 생성되었음을 나타내는 성공 상태 응답 코드(URI 필요)

        } catch (Exception e) {
            log.info("error");
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity
                    .badRequest()
                    .body(responseDTO);
        }
    }
 /*   @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refresh_token = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refresh_token);
                String email = decodedJWT.getSubject();
                UserEntity user = userService.getUser(email);//유저를 찾는다
                String access_token = JWT.create()
                        .withSubject(user.getName())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 *1000))
                        .withIssuer(request.getRequestURI().toString())
                        .withClaim("roles", user.getRoles().stream().map(RoleEntity::getName).collect(Collectors.toList()))
                        .sign(algorithm);
                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", access_token);
                tokens.put("refresh_token", refresh_token);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens); //토큰 전송

                TokenEntity tokenEntity = TokenEntity.builder()
                        .email(user.getEmail())
                        .refreshtoken(refresh_token)
                        .build();
                tokenRepository.save(tokenEntity);

            }catch (Exception exception) {
                response.setHeader("error", exception.getMessage());
                response.setStatus(FORBIDDEN.value());
                //response.sendError(FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("error_message", exception.getMessage());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
    }*/
}

@Data
class RoleToUserForm {
    private String username;
    private String roleName;
}
