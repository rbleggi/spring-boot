package br.com.croa.api.resource.v1;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MainController {

    @PreAuthorize("isAuthenticated()")
    @Timed("protectedPage")
    @Counted("protectedPage")
    @GetMapping(path = "/protected")
    public void protectedPage(final Principal principal) {
        log.info("protected " + Map.of("principal", principal));
    }

    @Timed("unprotectedPage")
    @Counted("unprotectedPage")
    @GetMapping(path = "/unprotected")
    public void unprotectedPage() {
        log.info("unprotected");
    }

}
