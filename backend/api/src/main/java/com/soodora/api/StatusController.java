package com.soodora.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/status")
@Tag(name = "Status Controller")
public class StatusController {

    @GetMapping
    public String status() {
        return "OK";
    }

}
