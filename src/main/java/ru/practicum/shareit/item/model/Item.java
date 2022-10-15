package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items", schema = "public")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull
    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;
    @NotEmpty
    @Column(name = "description", nullable = false)
    private String description;
    @NotNull
    @Column(name = "is_available", nullable = false)
    private Boolean available;
    @NotNull
    @Column(name = "owner_id", nullable = false)
    private long ownerId;
    @Column(name = "request_id")
    private Long requestId;

}
