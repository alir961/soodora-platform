package com.grooveconductor.rest.controller;

import com.grooveconductor.domain.dto.UserDTO;
import com.grooveconductor.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.grooveconductor.domain.factory.IdentifierFactory.createFromSlug;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping
    public List<UserDTO> getUsers() {
        return service.retrieveAllUsers();
    }

    @GetMapping("/{slug}")
    public UserDTO getUser(@PathVariable String slug) {
        return service.retrieveUser(createFromSlug(slug));
    }

    @DeleteMapping("/{slug}")
    public void deleteUser(@PathVariable("slug") String slug) {
        service.deleteUser(createFromSlug(slug));
    }

}
