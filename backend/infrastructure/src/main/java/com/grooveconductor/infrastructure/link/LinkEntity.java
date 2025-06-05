package com.grooveconductor.infrastructure.link;

import com.grooveconductor.domain.link.LinkDTO;
import com.grooveconductor.infrastructure.common.DomainEntity;
import com.grooveconductor.infrastructure.linkpage.LinkPageEntity;

import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "links")
public class LinkEntity implements DomainEntity<LinkDTO> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(nullable = false)
    private String title;

    @Column(length = 5000)
    private String url;

    @Column(length = 5000)
    private String iconUrl;

    @Column
    private String description;

    @ManyToOne
    @JoinColumn
    private LinkPageEntity linkPage;

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column
    private Date updatedAt;

    @Override
    public LinkDTO toDTO() {
        return new LinkDTO(
                slug,
                title,
                description,
                url,
                iconUrl);
    }
}
