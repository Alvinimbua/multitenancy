package com.imbuka.database_per_tenant.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "item")
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Item {

    @Id
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    @SequenceGenerator(name = "item_seq", sequenceName = "item_seq", allocationSize = 50)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "item_seq")
    private Long id;

    @Column(name = "name", length = 255, nullable = false)
    @NotNull
    @Size(max = 255)
    private String name;

    @Version
    @Column(name = "version", nullable = false, columnDefinition = "int default 0")
    private Integer version;

}
