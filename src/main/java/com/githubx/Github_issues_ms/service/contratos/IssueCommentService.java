package com.githubx.Github_issues_ms.service.contratos;

import com.githubx.Github_issues_ms.generated.model.CommentDTO;
import com.githubx.Github_issues_ms.generated.model.CreateIssueCommentBody;
import com.githubx.Github_issues_ms.generated.model.ListIssueCommentsBody;
import com.githubx.Github_issues_ms.generated.model.ListIssueCommentsBody;
import com.githubx.Github_issues_ms.generated.model.UpdateIssueCommentBody;

public interface IssueCommentService {

    ListIssueCommentsBody listIssueComments(String owner, String repo, Integer issueNumber);

    CommentDTO createIssueComment(String owner, String repo, Integer issueNumber, CreateIssueCommentBody request);

    ListIssueCommentsBody listRepositoryIssueComments(String owner, String repo, int page, int perPage);

    CommentDTO getIssueComment(String owner, String repo, String commentId);

    CommentDTO updateIssueComment(String owner, String repo, String commentId, UpdateIssueCommentBody request);

    void deleteIssueComment(String owner, String repo, String commentId);
}
