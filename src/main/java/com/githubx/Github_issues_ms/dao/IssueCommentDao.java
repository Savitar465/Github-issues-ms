package com.githubx.Github_issues_ms.dao;

import com.githubx.Github_issues_ms.model.IssueComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IssueCommentDao extends JpaRepository<IssueComment, UUID> {

    List<IssueComment> findAllByIssueId(UUID issueId);

    Page<IssueComment> findAllByIssueRepoId(String repoId, Pageable pageable);
}
