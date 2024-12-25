	package burundi.ilucky.controller;

import burundi.ilucky.model.User;
import burundi.ilucky.model.dto.CreateUserDTO;
import burundi.ilucky.model.dto.UserDTO;
import burundi.ilucky.payload.Response;
import burundi.ilucky.service.UserService;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

    @RestController
@CrossOrigin("*")
@Log4j2
@RequestMapping("/api/user")
public class UserController {

        @Autowired
        private UserService userService;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @GetMapping("/topPlayer")
        public ResponseEntity<?> getTopUser() {
            List<UserDTO> list = userService.findTop5();
            return new ResponseEntity<>(list, HttpStatus.OK);
        }

        @PostMapping("/info")
        public ResponseEntity<?> getUser(@AuthenticationPrincipal UserDetails userDetails) {
            try {
                User user = userService.findByUserName(userDetails.getUsername());
                UserDTO userDTO = new UserDTO(user);
                return ResponseEntity.ok(userDTO);
            } catch (Exception e) {
                log.warn(e);
                return ResponseEntity.internalServerError().build();
            }
        }


        @PostMapping("/create")
        public ResponseEntity<?> createUser(@RequestBody CreateUserDTO createUserDTO) {
            User user = User.builder()
                    .username(createUserDTO.getUsername())
                    .password(passwordEncoder.encode(createUserDTO.getPassword()))
                    .lastUpdate(new Date())
                    .addTime(new Date())
                    .totalPlay(5)
                    .totalVnd(0)
                    .totalStar(0)
                    .build();
            userService.saveUser(user);
            return ResponseEntity.ok(user);
        }


        @GetMapping("/")
        public ResponseEntity<?> getAllUsers() {
            List<User> users = userService.findAll();

            if (users.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(users);
        }

        @GetMapping("/find/{username}")
        public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
            User user = userService.findByUserName(username);
            Response response = new Response();
            if (user == null) {
                response.setStatus("404");
                response.setMessage("User not found");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
            response.setStatus("409");
            response.setMessage("User ready exist");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }



