package ua.habatynchik.authenticationservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

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
}
