package com.example.demo.entitiy;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.NonNull;

@Setter
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@RequiredArgsConstructor(staticName = "of")
@EqualsAndHashCode
@Entity
@Table(name = "Message")
public class Message {
    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    @Column(nullable = false, length = 32)
    private String title;

    @NonNull
    @Column(nullable = false, length = 256)
    private String summary;

    @NonNull
    @Column(nullable = false, length = 2048)
    private String message;

    @NonNull
    @Column(nullable = false)
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images;
}
