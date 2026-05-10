package com.githubx.Github_issues_ms.dao;

import com.githubx.Github_issues_ms.model.Label;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LabelDao extends JpaRepository<Label, UUID> {

    List<Label> findAllByRepoId(String repoId);

    Optional<Label> findByRepoIdAndName(String repoId, String name);

    boolean existsByRepoIdAndName(String repoId, String name);
}
