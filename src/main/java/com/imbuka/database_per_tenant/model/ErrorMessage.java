package com.imbuka.database_per_tenant.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorMessage {

    @JsonProperty("timestamp")
    @NotNull
    private OffsetDateTime timestamp;

    @JsonProperty("status")
    @NotNull
    private Integer status;

    @JsonProperty("error")
    @NotNull
    private String error;

    @JsonProperty("message")
    @NotNull
    @Size(max = 255)
    private String message;

    @JsonProperty("path")
    private String path;
}
