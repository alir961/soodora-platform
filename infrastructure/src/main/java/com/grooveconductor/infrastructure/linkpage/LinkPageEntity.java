package com.grooveconductor.infrastructure.linkpage;

import com.grooveconductor.domain.linkpage.LinkPageDTO;
import com.grooveconductor.infrastructure.common.DomainEntity;
import com.grooveconductor.infrastructure.link.LinkEntity;
import com.grooveconductor.infrastructure.user.UserEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
@Entity(name = "link_pages")
public class LinkPageEntity implements DomainEntity<LinkPageDTO> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(nullable = false)
    private String title;

    @Column
    private String description;

    @OneToOne
    @JoinColumn
    private UserEntity user;

    @OneToMany(mappedBy = "linkPage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LinkEntity> links = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column
    private Date updatedAt;

    @Override
    public LinkPageDTO toDTO() {
        return new LinkPageDTO(
                slug,
                title,
                description,
                links.stream().map(LinkEntity::toDTO).collect(Collectors.toList()),
                user.toDTO());
    }

}
