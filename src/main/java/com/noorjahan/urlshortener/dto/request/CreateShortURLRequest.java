package com.noorjahan.urlshortener.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateShortURLRequest {

    @NotBlank(message = "Original URL is required")
    @Pattern(
        regexp = "^(https?://).+",
        message = "Original URL must start with http:// or https://"
    )
    private String orignalUrl;
}
