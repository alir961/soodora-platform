package com.grooveconductor.rest.controller;

import com.grooveconductor.domain.dto.UserDTO;
import com.grooveconductor.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.grooveconductor.domain.factory.IdentifierFactory.createFromSlug;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService service;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getUsers() {
        List<UserDTO> users = service.retrieveAllUsers();

        if (users.isEmpty())
            return new ResponseEntity<>(NO_CONTENT);
        return new ResponseEntity<>(users, OK);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String slug) {
        UserDTO user = service.retrieveUser(createFromSlug(slug));

        return new ResponseEntity<>(user, OK);
    }

    @DeleteMapping("/{slug}")
    public void deleteUser(@PathVariable("slug") String slug) {
        service.deleteUser(createFromSlug(slug));
    }

}
