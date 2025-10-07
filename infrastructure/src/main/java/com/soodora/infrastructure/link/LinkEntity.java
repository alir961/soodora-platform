package com.soodora.infrastructure.link;

import com.soodora.domain.link.LinkDTO;
import com.soodora.infrastructure.common.DomainEntityAdapter;
import com.soodora.infrastructure.linkpage.LinkPageEntity;

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
public class LinkEntity implements DomainEntityAdapter<LinkDTO> {

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
    public LinkDTO aggregate() {
        return new LinkDTO(
                slug,
                title,
                description,
                url,
                iconUrl);
    }
}
