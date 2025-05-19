package com.grooveconductor.infrastructure.linkpage;

import com.grooveconductor.domain.linkpage.LinkPageDTO;
import com.grooveconductor.infrastructure.common.DomainEntity;
import com.grooveconductor.infrastructure.link.LinkEntity;
import com.grooveconductor.infrastructure.user.UserEntity;

import java.util.*;
import java.util.stream.Collectors;

import jakarta.persistence.*;
import lombok.*;

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
