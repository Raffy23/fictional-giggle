package com.example.demo;

import java.sql.Blob;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString(exclude = {"data"})
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@RequiredArgsConstructor(staticName = "of")
@EqualsAndHashCode(exclude = "data")
@Entity
@Table(name = "Child")
public class Child {
    @Id
    @GeneratedValue
    private Long id;

    @NotNull @lombok.NonNull
    @Column(nullable = false)
    private String type;

    @NotNull @lombok.NonNull
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private Blob data;
}
