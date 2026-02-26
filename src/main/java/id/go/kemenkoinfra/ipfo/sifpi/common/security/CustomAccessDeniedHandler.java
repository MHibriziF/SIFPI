package id.go.kemenkoinfra.ipfo.sifpi.common.security;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
        String timestamp = sdf.format(new Date());

        String message;
        int status;

        if (accessDeniedException instanceof CsrfException) {
            status = HttpStatus.FORBIDDEN.value();
            message = "CSRF token tidak valid atau tidak ditemukan.";
        } else {
            status = HttpStatus.FORBIDDEN.value();
            message = "Anda tidak memiliki izin untuk mengakses sumber daya ini.";
        }

        response.setStatus(status);
        String body = """
                {"status":%d,"message":"%s","timestamp":"%s","data":null}"""
                .formatted(status, message, timestamp);

        response.getWriter().write(body);
        response.getWriter().flush();
    }
}
