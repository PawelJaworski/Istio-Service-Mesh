package pl.javorex.poc.istio.cashloans.user.adapter.micronaut.rest;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.context.ServerRequestContext;

import java.util.Objects;

@Controller
class UserRestController {

    @Get("/user/fullName")
    public String getUserFullName() {
        final HttpHeaders headers = ServerRequestContext
                .currentRequest()
                .orElseThrow(() -> new IllegalStateException("No servlet request exists"))
                .getHeaders();

        final String fullName = headers.get("x-user-desc");
        Objects.requireNonNull(fullName, "x-user-desc header not found.");

        return fullName;
    }
}
