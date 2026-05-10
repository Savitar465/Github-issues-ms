package com.githubx.Github_issues_ms.dao;

import com.githubx.Github_issues_ms.model.Issue;
import com.githubx.Github_issues_ms.model.IssueState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface IssueDao extends JpaRepository<Issue, UUID> {

    Optional<Issue> findByRepoIdAndNumber(String repoId, Integer number);

    Page<Issue> findAllByRepoId(String repoId, Pageable pageable);

    Page<Issue> findAllByRepoIdAndState(String repoId, IssueState state, Pageable pageable);

    @Query("""
            SELECT i FROM Issue i
            JOIN i.labels l
            WHERE i.repoId = :repoId AND l.name = :labelName
            """)
    Page<Issue> findAllByRepoIdAndLabelName(
            @Param("repoId") String repoId,
            @Param("labelName") String labelName,
            Pageable pageable);

    @Query("""
            SELECT i FROM Issue i
            WHERE i.repoId = :repoId AND i.assigneeUsername = :assignee
            """)
    Page<Issue> findAllByRepoIdAndAssignee(
            @Param("repoId") String repoId,
            @Param("assignee") String assignee,
            Pageable pageable);

    @Query("SELECT COALESCE(MAX(i.number), 0) FROM Issue i WHERE i.repoId = :repoId")
    Integer findMaxNumberByRepoId(@Param("repoId") String repoId);
}
