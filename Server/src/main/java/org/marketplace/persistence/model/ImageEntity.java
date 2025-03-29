package org.marketplace.persistence.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "\"ImageEntity\"", schema = "public")
public class ImageEntity implements PersistableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"productId\"")
    private org.marketplace.persistence.model.ProductEntity product;

    @Column(name = "bytes")
    private byte[] bytes;

    @Lob
    @Column(name = "extension")
    private String extension;

}