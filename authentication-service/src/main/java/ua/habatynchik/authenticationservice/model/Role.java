package ua.habatynchik.authenticationservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @Enumerated(EnumType.STRING)
    private RoleEnum roleEnum;

    @Column(name = "description")
    private String description;

    public enum RoleEnum {
        CLIENT, ADMIN, GUEST;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id) && roleEnum == role.roleEnum && Objects.equals(description, role.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, roleEnum, description);
    }
}
