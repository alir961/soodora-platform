package com.soodora.infrastructure.link;

import com.soodora.domain.common.value.Url;
import com.soodora.domain.link.Link;
import com.soodora.infrastructure.common.DomainEntityAdapter;
import com.soodora.infrastructure.linkhub.LinkHubEntity;

import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "links")
public class LinkEntity implements DomainEntityAdapter<Link> {

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
    private LinkHubEntity linkHub;

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column
    private Date updatedAt;

    @Override
    public Link aggregate() {
        return new Link(
                slug,
                title,
                description,
                new Url(url),
                iconUrl);
    }
}
