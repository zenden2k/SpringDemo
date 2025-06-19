package dev.svistunov.springdemo.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPhotoDto {
    @NotNull
    private Long id;

    private String photoUrl;
}
