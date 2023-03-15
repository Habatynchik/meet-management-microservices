package ua.habatynchik.userservice.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.habatynchik.userservice.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByRoleEnum(Role.RoleEnum roleEnum);

}
