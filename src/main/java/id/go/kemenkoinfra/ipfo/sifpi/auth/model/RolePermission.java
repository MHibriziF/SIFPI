package id.go.kemenkoinfra.ipfo.sifpi.auth.model;

import java.util.UUID;

import id.go.kemenkoinfra.ipfo.sifpi.common.enums.Action;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"role", "resource"})
@Entity
@Table(
    name = "role_permissions",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_role_resource_action",
        columnNames = {"role_id", "resource_name", "action"}
    ),
    indexes = {
        @Index(name = "idx_role_permission_role_id", columnList = "role_id"),
        @Index(name = "idx_role_permission_resource_name", columnList = "resource_name")
    }
)
public class RolePermission {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_name", nullable = false)
    private Resource resource;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Action action;

    public String getResourceName() {
        return resource.getName();
    }

    public String getActionName() {
        return action.name();
    }
}
