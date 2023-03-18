package ua.habatynchik.meetservice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "guest_status", schema = "meet_service")
public class GuestStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private GuestStatusEnum status;

    public enum GuestStatusEnum {
        ACCEPT, TENTATIVE, DECLINE;
    }

}
