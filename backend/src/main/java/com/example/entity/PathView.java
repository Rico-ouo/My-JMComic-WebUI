package com.example.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
@RegisterForReflection
public class PathView extends PanacheEntity{
    @Column(name="path", unique = true)
    private String path;
    private String viewData;
}
