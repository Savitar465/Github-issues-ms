package com.githubx.Github_issues_ms.service.implementacion;

import com.githubx.Github_issues_ms.config.security.AuthenticatedUserResolver;
import com.githubx.Github_issues_ms.dao.IssueCommentDao;
import com.githubx.Github_issues_ms.dao.IssueDao;
import com.githubx.Github_issues_ms.generated.model.CommentDTO;
import com.githubx.Github_issues_ms.generated.model.CreateIssueCommentBody;
import com.githubx.Github_issues_ms.generated.model.ListIssueCommentsBody;
import com.githubx.Github_issues_ms.generated.model.UpdateIssueCommentBody;
import com.githubx.Github_issues_ms.mapper.IssueCommentMapper;
import com.githubx.Github_issues_ms.model.Issue;
import com.githubx.Github_issues_ms.model.IssueComment;
import com.githubx.Github_issues_ms.service.contratos.IssueCommentService;
import com.githubx.Github_issues_ms.util.errorhandling.EntityNotFoundException;
import com.githubx.Github_issues_ms.util.errorhandling.ForbiddenOperationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class IssueCommentServiceImpl implements IssueCommentService {

    private final IssueCommentDao issueCommentDao;
    private final IssueDao issueDao;
    private final IssueCommentMapper commentMapper;
    private final AuthenticatedUserResolver userResolver;

    @Override
    @Transactional(readOnly = true)
    public ListIssueCommentsBody listIssueComments(String owner, String repo, Integer issueNumber) {
        String repoId = buildRepoId(owner, repo);
        Issue issue = findIssueOrThrow(repoId, issueNumber);
        List<IssueComment> comments = issueCommentDao.findAllByIssueId(issue.getId());
        return new ListIssueCommentsBody().comments(commentMapper.toDtoList(comments));
    }

    @Override
    @Transactional
    public CommentDTO createIssueComment(String owner, String repo, Integer issueNumber,
                                          CreateIssueCommentBody request) {
        UUID currentUserId = userResolver.getCurrentUserId();
        String currentUsername = userResolver.getCurrentUsername();
        String repoId = buildRepoId(owner, repo);
        Issue issue = findIssueOrThrow(repoId, issueNumber);

        IssueComment comment = IssueComment.builder()
                .issue(issue)
                .body(request.getBody())
                .authorId(currentUserId)
                .authorUsername(currentUsername)
                .build();

        comment = issueCommentDao.save(comment);
        log.info("Comentario creado en issue #{} del repo: {} por: {}", issueNumber, repoId, currentUsername);
        return commentMapper.toDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public ListIssueCommentsBody listRepositoryIssueComments(String owner, String repo, int page, int perPage) {
        String repoId = buildRepoId(owner, repo);
        PageRequest pageRequest = PageRequest.of(page - 1, perPage);
        Page<IssueComment> commentPage = issueCommentDao.findAllByIssueRepoId(repoId, pageRequest);
        return new ListIssueCommentsBody().comments(commentMapper.toDtoList(commentPage.getContent()));
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDTO getIssueComment(String owner, String repo, String commentId) {
        IssueComment comment = findCommentOrThrow(commentId);
        return commentMapper.toDto(comment);
    }

    @Override
    @Transactional
    public CommentDTO updateIssueComment(String owner, String repo, String commentId,
                                          UpdateIssueCommentBody request) {
        UUID currentUserId = userResolver.getCurrentUserId();
        IssueComment comment = findCommentOrThrow(commentId);

        if (!comment.getAuthorId().equals(currentUserId)) {
            throw ForbiddenOperationException.notAuthor();
        }

        comment.setBody(request.getBody());
        comment = issueCommentDao.save(comment);
        log.info("Comentario {} actualizado", commentId);
        return commentMapper.toDto(comment);
    }

    @Override
    @Transactional
    public void deleteIssueComment(String owner, String repo, String commentId) {
        UUID currentUserId = userResolver.getCurrentUserId();
        IssueComment comment = findCommentOrThrow(commentId);

        if (!comment.getAuthorId().equals(currentUserId)) {
            throw ForbiddenOperationException.notAuthor();
        }

        issueCommentDao.delete(comment);
        log.info("Comentario {} eliminado", commentId);
    }

    // ===== Helpers =====

    private Issue findIssueOrThrow(String repoId, Integer issueNumber) {
        return issueDao.findByRepoIdAndNumber(repoId, issueNumber)
                .orElseThrow(() -> EntityNotFoundException.issue(issueNumber));
    }

    private IssueComment findCommentOrThrow(String commentId) {
        return issueCommentDao.findById(UUID.fromString(commentId))
                .orElseThrow(() -> EntityNotFoundException.comment(commentId));
    }

    private String buildRepoId(String owner, String repo) {
        return owner + "/" + repo;
    }
}
