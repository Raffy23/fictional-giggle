package com.example.demo.entitiy;

import java.sql.Blob;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.NonNull;

@Setter
@Getter
@ToString(exclude = {"data"})
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@RequiredArgsConstructor(staticName = "of")
@EqualsAndHashCode(exclude = "data")
@Entity
@Table(name = "Image")
public class Image {
    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String type;

    @NonNull
    @Column(nullable = false)
    private String name; 

    @NonNull
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private Blob data;
}
