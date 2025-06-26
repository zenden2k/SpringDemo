package dev.svistunov.springdemo.model;

import dev.svistunov.springdemo.util.PhoneNumberUtils;
import dev.svistunov.springdemo.validation.annotations.ValidBirthDate;
import dev.svistunov.springdemo.validation.annotations.ValidName;
import dev.svistunov.springdemo.validation.annotations.ValidPhoneNumber;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SoftDelete
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "first_name", length = 100)
    @NotNull
    @Size(max = 100, message = "Имя должно быть не длиннее 100 символов")
    @ValidName
    private String firstName;

    @Column(name = "last_name", length = 100)
    @Size(max = 100, message = "Фамилия должна быть не длиннее 100 символов")
    @ValidName
    private String lastName;

    @Column(name = "middle_name")
    @Size(max = 100, message = "Отчество должно быть не длиннее 100 символов")
    @ValidName
    private String middleName;

    @Column(name = "birth_date")
    @Past(message = "Дата рождения должна быть в прошлом")
    @ValidBirthDate
    private LocalDate birthDate;

    @Column(name = "email", unique = true)
    @Email
    private String email;

    @Column(name = "phone_number")
    @ValidPhoneNumber
    private String phoneNumber;

    @Column(name = "photo")
    private String photo;

    @Column(name = "created_at", updatable = false)
    @NotNull
    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    private void normalizePhoneNumber() {
        if (this.phoneNumber != null && !this.phoneNumber.isBlank()) {
            this.phoneNumber = PhoneNumberUtils.normalizePhoneNumber(this.phoneNumber, "RU");
        }
    }
}