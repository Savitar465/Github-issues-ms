package com.githubx.Github_issues_ms.service.implementacion;

import com.githubx.Github_issues_ms.config.security.AuthenticatedUserResolver;
import com.githubx.Github_issues_ms.dao.IssueDao;
import com.githubx.Github_issues_ms.dao.LabelDao;
import com.githubx.Github_issues_ms.generated.model.CreateIssueBody;
import com.githubx.Github_issues_ms.generated.model.IssueDTO;
import com.githubx.Github_issues_ms.generated.model.ListIssuesBody;
import com.githubx.Github_issues_ms.generated.model.PaginationMeta;
import com.githubx.Github_issues_ms.generated.model.UpdateIssueBody;
import com.githubx.Github_issues_ms.mapper.IssueMapper;
import com.githubx.Github_issues_ms.model.Issue;
import com.githubx.Github_issues_ms.model.IssueState;
import com.githubx.Github_issues_ms.model.Label;
import com.githubx.Github_issues_ms.service.contratos.IssueService;
import com.githubx.Github_issues_ms.util.errorhandling.EntityNotFoundException;
import com.githubx.Github_issues_ms.util.errorhandling.ForbiddenOperationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class IssueServiceImpl implements IssueService {

    private final IssueDao issueDao;
    private final LabelDao labelDao;
    private final IssueMapper issueMapper;
    private final AuthenticatedUserResolver userResolver;

    @Override
    @Transactional(readOnly = true)
    public ListIssuesBody listIssues(String owner, String repo, String state,
                                     String label, String assignee, int page, int perPage) {
        String repoId = buildRepoId(owner, repo);
        PageRequest pageRequest = PageRequest.of(page - 1, perPage);

        Page<Issue> issuePage;

        if (state != null) {
            IssueState issueState = IssueState.valueOf(state.toUpperCase());
            issuePage = issueDao.findAllByRepoIdAndState(repoId, issueState, pageRequest);
        } else if (label != null) {
            issuePage = issueDao.findAllByRepoIdAndLabelName(repoId, label, pageRequest);
        } else if (assignee != null) {
            issuePage = issueDao.findAllByRepoIdAndAssignee(repoId, assignee, pageRequest);
        } else {
            issuePage = issueDao.findAllByRepoId(repoId, pageRequest);
        }

        List<IssueDTO> issues = issuePage.getContent().stream()
                .map(issueMapper::toDto)
                .toList();

        PaginationMeta pagination = new PaginationMeta()
                .page(page)
                .perPage(perPage)
                .total((int) issuePage.getTotalElements())
                .totalPages(issuePage.getTotalPages());

        return new ListIssuesBody()
                .issues(issues)
                .pagination(pagination);
    }

    @Override
    @Transactional
    public IssueDTO createIssue(String owner, String repo, CreateIssueBody request) {
        UUID currentUserId = userResolver.getCurrentUserId();
        String currentUsername = userResolver.getCurrentUsername();
        String repoId = buildRepoId(owner, repo);

        Integer nextNumber = issueDao.findMaxNumberByRepoId(repoId) + 1;

        List<Label> labels = new ArrayList<>();
        if (request.getLabels() != null) {
            for (String labelName : request.getLabels()) {
                labelDao.findByRepoIdAndName(repoId, labelName).ifPresent(labels::add);
            }
        }

        UUID assigneeId = null;
        String assigneeUsername = null;
        if (request.getAssignee() != null && !request.getAssignee().isBlank()) {
            assigneeUsername = request.getAssignee();
        }

        Issue issue = Issue.builder()
                .repoId(repoId)
                .number(nextNumber)
                .title(request.getTitle())
                .body(request.getBody())
                .state(IssueState.OPEN)
                .authorId(currentUserId)
                .authorUsername(currentUsername)
                .assigneeId(assigneeId)
                .assigneeUsername(assigneeUsername)
                .labels(labels)
                .build();

        issue = issueDao.save(issue);
        log.info("Issue #{} creado en repo: {} por usuario: {}", nextNumber, repoId, currentUsername);
        return issueMapper.toDto(issue);
    }

    @Override
    @Transactional(readOnly = true)
    public IssueDTO getIssue(String owner, String repo, Integer issueNumber) {
        String repoId = buildRepoId(owner, repo);
        Issue issue = findIssueOrThrow(repoId, issueNumber);
        return issueMapper.toDto(issue);
    }

    @Override
    @Transactional
    public IssueDTO updateIssue(String owner, String repo, Integer issueNumber, UpdateIssueBody request) {
        UUID currentUserId = userResolver.getCurrentUserId();
        String repoId = buildRepoId(owner, repo);
        Issue issue = findIssueOrThrow(repoId, issueNumber);

        if (!issue.getAuthorId().equals(currentUserId)) {
            throw ForbiddenOperationException.notAuthor();
        }

        if (request.getTitle() != null) issue.setTitle(request.getTitle());
        if (request.getBody() != null) issue.setBody(request.getBody());
        if (request.getState() != null) {
            IssueState newState = IssueState.valueOf(request.getState().name().toUpperCase());
            if (newState == IssueState.CLOSED && issue.getState() == IssueState.OPEN) {
                issue.setClosedAt(Instant.now());
            } else if (newState == IssueState.OPEN) {
                issue.setClosedAt(null);
            }
            issue.setState(newState);
        }
        if (request.getAssignee() != null) {
            issue.setAssigneeUsername(request.getAssignee().isBlank() ? null : request.getAssignee());
        }
        if (request.getLabels() != null) {
            List<Label> labels = new ArrayList<>();
            for (String labelName : request.getLabels()) {
                labelDao.findByRepoIdAndName(repoId, labelName).ifPresent(labels::add);
            }
            issue.setLabels(labels);
        }

        issue = issueDao.save(issue);
        log.info("Issue #{} actualizado en repo: {}", issueNumber, repoId);
        return issueMapper.toDto(issue);
    }

    // ===== Helpers =====

    private Issue findIssueOrThrow(String repoId, Integer issueNumber) {
        return issueDao.findByRepoIdAndNumber(repoId, issueNumber)
                .orElseThrow(() -> EntityNotFoundException.issue(issueNumber));
    }

    private String buildRepoId(String owner, String repo) {
        return owner + "/" + repo;
    }
}
