package com.grooveconductor.infrastructure.post;

import com.grooveconductor.domain.post.PostDTO;
import com.grooveconductor.infrastructure.common.DomainEntity;
import com.grooveconductor.infrastructure.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

import static java.util.UUID.randomUUID;

@Getter
@Setter
@Entity(name = "posts")
@NoArgsConstructor
@AllArgsConstructor
public class PostEntity implements DomainEntity<PostDTO> {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id = randomUUID();

  @Column(unique = true)
  private String slug;

  @Column
  private String title;

  @Column(length = 5000)
  private String content;

  @ManyToOne
  @JoinColumn
  private UserEntity user;

  @CreationTimestamp
  @Column(updatable = false)
  private Date createdAt;

  @UpdateTimestamp
  @Column
  private Date updatedAt;

  @Override
  public PostDTO toDTO() {
    return new PostDTO(
        slug,
        title,
        content,
        user.toDTO());
  }

}
