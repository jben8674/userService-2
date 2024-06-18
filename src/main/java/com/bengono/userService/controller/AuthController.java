package com.bengono.userService.controller;



import com.bengono.userService.configuration.JwtProvider;
import com.bengono.userService.model.User;
import com.bengono.userService.repository.UserRepository;
import com.bengono.userService.request.LogInRequest;
import com.bengono.userService.response.AuthResponse;
import com.bengono.userService.service.CustomUserServiceImplementation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private CustomUserServiceImplementation customerUserDetails;


    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(
            @RequestBody User user)throws Exception {
        String email =user.getEmail();
        String password =user.getPassword();
        String fullName=user.getFullName();
        //String mobile=user.getMobile();
        String role = user.getRole();

        User isEmailExist=userRepository.findByEmail(email);

        if (isEmailExist!=null) {
            throw new Exception("Email is Already used With Another Account");
        }

        User createdUser = new User();
        createdUser.setEmail(email);
        createdUser.setFullName(fullName);
        createdUser.setRole(role);
        createdUser.setPassword(passwordEncoder.encode(password));

        User savedUser = userRepository.save(createdUser);



        Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = JwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Register Success");
        authResponse.setStatus(true);

        return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.OK);

    }
    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signIn(@RequestBody LogInRequest loginRequest) {
        String username = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        System.out.println(username+"-----"+password);

        Authentication authentication= authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Générez le token JWT
        String token = JwtProvider.generateToken(authentication);

        // Créez une réponse d'authentification
        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Login Success");
        authResponse.setStatus(true);

        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    private Authentication authenticate(String username, String password) {
        UserDetails userDetails = customerUserDetails.loadUserByUsername(username);
        System.out.println("sign in userDetails -"+userDetails);

        if(userDetails==null){
            System.out.println("sign in userDetails - null "+userDetails);
            throw new BadCredentialsException("Invalid username or password");
        }
        if(!passwordEncoder.matches(password, userDetails.getPassword())){
            System.out.println("sign in userDetails - password not match "+userDetails);
            throw new BadCredentialsException("Invalid username or password");
        }
        return new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
    }

}