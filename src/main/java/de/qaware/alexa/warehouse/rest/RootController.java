package de.qaware.alexa.warehouse.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * Controller which responds to requests on /.
 */
@RequestMapping(path = "/")
@RestController
public class RootController {
    /**
     * Responds to requests on /.
     *
     * @return Response.
     */
    @GetMapping
    public String hello() {
        return "Hello from warehouse skill: " + LocalDateTime.now();
    }
}
