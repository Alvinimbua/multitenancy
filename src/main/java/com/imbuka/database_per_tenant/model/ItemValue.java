package com.imbuka.database_per_tenant.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.imbuka.database_per_tenant.entity.Item;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemValue {

    @JsonProperty("itemId")
    private Long itemId;

    @NotNull
    @Size(max = 255)
    @JsonProperty("name")
    private String name;

    public static ItemValue fromEntity(Item item) {
        return ItemValue.builder()
                .itemId(item.getId())
                .name(item.getName())
                .build();
    }

}
