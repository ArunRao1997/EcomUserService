package dev.arun.ecomuserservice.service;//package dev.arun.ecomuserservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
//import dev.arun.ecomuserservice.clients.KafkaProducerClient;
import dev.arun.ecomuserservice.clients.KafkaProducerClient;
import dev.arun.ecomuserservice.dto.SendEmailMessageDto;
import dev.arun.ecomuserservice.dto.UserDto;
import dev.arun.ecomuserservice.exception.InvalidCredentialException;
import dev.arun.ecomuserservice.exception.InvalidSessionException;
import dev.arun.ecomuserservice.exception.InvalidTokenException;
import dev.arun.ecomuserservice.exception.UserNotFoundException;
import dev.arun.ecomuserservice.mapper.UserEntityDTOMapper;
import dev.arun.ecomuserservice.models.Session;
import dev.arun.ecomuserservice.models.SessionStatus;
import dev.arun.ecomuserservice.models.User;
import dev.arun.ecomuserservice.repository.SessionRepository;
import dev.arun.ecomuserservice.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.MacAlgorithm;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.util.*;

@Service
public class AuthService {
    private UserRepository userRepository;
    private SessionRepository sessionRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    //private SecretKey secretKey;
    private KafkaProducerClient kafkaProducerClient;

    private EmailService emailService;
    private ObjectMapper objectMapper;

        public AuthService(UserRepository userRepository, SessionRepository sessionRepository, BCryptPasswordEncoder bCryptPasswordEncoder, KafkaProducerClient kafkaProducerClient, EmailService emailService, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.kafkaProducerClient = kafkaProducerClient;
        this.emailService = emailService;
        this.objectMapper = objectMapper;
    }



    public ResponseEntity<List<Session>> getAllSession() {
        List<Session> sessions = sessionRepository.findAll();
        return ResponseEntity.ok(sessions);
    }

    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    public ResponseEntity<UserDto> login(String email, String password) {
        //Get user details from DB
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User for the given email id does not exist");
        }
        User user = userOptional.get();
        //Verify the user password given at the time of login
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialException("Invalid Credentials");
        }
        //token generation
      //  String token = RandomStringUtils.randomAlphanumeric(30);
        MacAlgorithm alg = Jwts.SIG.HS256; // HS256 algo added for JWT
        SecretKey key = alg.key().build(); // generating the secret key

        //start adding the claims
        Map<String, Object> jsonForJWT = new HashMap<>();
        jsonForJWT.put("userId", user.getId());
        jsonForJWT.put("email", user.getEmail());
        jsonForJWT.put("roles", user.getRoles());
        jsonForJWT.put("createdAt", new Date());
        jsonForJWT.put("expiryAt", new Date(LocalDate.now().plusDays(3).toEpochDay()));

        String token = Jwts.builder()
                .claims(jsonForJWT) // added the claims
                .signWith(key, alg) // added the algo and key
                .compact(); //building the token


        //session creation
        Session session = new Session();
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setToken(token);
        session.setUser(user);
        session.setLoginAt(new Date());
        sessionRepository.save(session);
        //generating the response
        UserDto userDto = UserEntityDTOMapper.getUserDTOFromUserEntity(user);
        //setting up the headers
        MultiValueMapAdapter<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add(HttpHeaders.SET_COOKIE, token);
        return new ResponseEntity<>(userDto, headers, HttpStatus.OK);
    }

    public ResponseEntity<Void> logout(String token, Long userId) {
        // validations -> token exists, token is not expired, user exists else throw an exception
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);
        if (sessionOptional.isEmpty()) {
            throw new InvalidSessionException("Session not found");
        }
        Session session = sessionOptional.get();
        session.setSessionStatus(SessionStatus.ENDED);
        sessionRepository.save(session);
        return ResponseEntity.ok().build();
    }

    public UserDto signUp(String email, String password) throws JsonProcessingException {
        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));

        User savedUser = userRepository.save(user);

        UserDto userDto = UserDto.from(savedUser);

//        kafkaProducerClient.sendMessage("userSignUp", objectMapper.writeValueAsString(userDto));
//        SendEmailMessageDto emailMessage = new SendEmailMessageDto();
//        emailMessage.setTo(userDto.getEmail());
//        emailMessage.setFrom("rao.nayinenii@gmail.com");
//        emailMessage.setSubject("Welcome to EcomUserService");
//        emailMessage.setBody("Thanks for creating an account. We look forward to you growing. Team Ecom");
//        kafkaProducerClient.sendMessage("sendEmail",objectMapper.writeValueAsString(emailMessage));
        return userDto;
    }

    /*public UserDto signUp(String email, String password) throws JsonProcessingException {
        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));

        User savedUser = userRepository.save(user);

        UserDto userDto = UserDto.from(savedUser);
        // Send the email directly
        SendEmailMessageDto emailMessage = new SendEmailMessageDto();
        emailMessage.setTo(userDto.getEmail());
        emailMessage.setFrom("rao.nayinenii@gmail.com");
        emailMessage.setSubject("Welcome to EcomUserService");
        emailMessage.setBody("Thanks for creating an account. We look forward to you growing. Team Ecom");
        emailService.sendEmail(emailMessage); // Use your injected emailService

        return userDto;
    }*/

    public SessionStatus validate(String token, Long userId) {
        //TODO check expiry // Jwts Parser -> parse the encoded JWT token to read the claims
//        try {
//            JwtParser parser = (JwtParser) Jwts.parser()
//                    .setSigningKey(secretKey);
//
//            Claims claims = parser.parseClaimsJws(token).getBody();
//            claims.get("userId", String.class);
//
//            Date expirationDate = claims.getExpiration();
//            if (expirationDate.before(new Date())) {
//                throw new InvalidTokenException("Token has expired");
//            }
//
//        } catch (ExpiredJwtException e) {
//            throw new InvalidTokenException("Token has expired");
//        } catch (JwtException e) { // Covers broader issues like malformed JWT
//            throw new InvalidTokenException("Token is invalid");
//        }
        //verifying from DB if session exists
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);
        if (sessionOptional.isEmpty() || sessionOptional.get().getSessionStatus().equals(SessionStatus.ENDED)) {
            throw new InvalidTokenException("token is invalid");
        }
        return SessionStatus.ACTIVE;
    }

}
