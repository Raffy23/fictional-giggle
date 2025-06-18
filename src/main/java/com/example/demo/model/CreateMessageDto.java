package com.example.demo.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode
@ToString
public class CreateMessageDto {
    @NotEmpty(message = "Title is a required attribute")
    @NotBlank(message = "Title must contain at least one character")
    @Max(value = 32, message = "Title should not be greater than 32 characters") 
    String title;

    @NotEmpty(message = "Summary is a required attribute")
    @NotBlank(message = "Summary must contain at least one character")
    @Max(value = 256, message = "Summary should not be greater than 256 characters") 
    String summary;

    @NotEmpty(message = "Title is a required attribute")
    @NotBlank(message = "Title must contain at least one character")
    @Max(value = 2048, message = "Title should not be greater than 2048 characters") 
    String message;
}
