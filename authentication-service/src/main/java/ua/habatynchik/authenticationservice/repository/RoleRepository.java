package ua.habatynchik.authenticationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.habatynchik.authenticationservice.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByRoleEnum(Role.RoleEnum roleEnum);

}
