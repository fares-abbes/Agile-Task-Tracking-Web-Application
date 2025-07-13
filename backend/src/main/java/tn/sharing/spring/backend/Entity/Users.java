package tn.sharing.spring.backend.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level= AccessLevel.PRIVATE)
@Entity


public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String username;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String email;
    private String password;

    private LocalDate createdAt;

    @OneToMany(mappedBy="user")
    @JsonIgnore
    private List<Product> products;

}
