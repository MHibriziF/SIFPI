package id.go.kemenkoinfra.ipfo.sifpi.auth.controller;

import id.go.kemenkoinfra.ipfo.sifpi.common.enums.BudgetRange;
import id.go.kemenkoinfra.ipfo.sifpi.common.enums.Sector;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth/register")
@RequiredArgsConstructor
public class RegistrationMetadataController {

    @GetMapping("/registration-options")
    public ResponseEntity<Map<String, Object>> getRegistrationOptions() {
        List<Map<String, String>> budgetOptions = Arrays.stream(BudgetRange.values())
                .map(b -> Map.of("value", b.getValue(), "label", b.getLabel()))
                .collect(Collectors.toList());

        List<Map<String, String>> sectorOptions = Arrays.stream(Sector.values())
                .map(s -> Map.of("value", s.getValue(), "label", s.getLabel()))
                .collect(Collectors.toList());

        Map<String, Object> body = Map.of(
                "budgetOptions", budgetOptions,
                "sectorOptions", sectorOptions
        );

        return ResponseEntity.ok(body);
    }
}
