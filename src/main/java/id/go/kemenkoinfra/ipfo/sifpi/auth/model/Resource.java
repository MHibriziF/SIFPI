package id.go.kemenkoinfra.ipfo.sifpi.auth.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "resources")
public class Resource {

    @Id
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 200)
    private String description;
}
