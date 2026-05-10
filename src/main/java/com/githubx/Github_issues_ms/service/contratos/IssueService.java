package com.githubx.Github_issues_ms.service.contratos;

import com.githubx.Github_issues_ms.generated.model.CreateIssueBody;
import com.githubx.Github_issues_ms.generated.model.IssueDTO;
import com.githubx.Github_issues_ms.generated.model.ListIssuesBody;
import com.githubx.Github_issues_ms.generated.model.UpdateIssueBody;

public interface IssueService {

    ListIssuesBody listIssues(String owner, String repo, String state, String label, String assignee, int page, int perPage);

    IssueDTO createIssue(String owner, String repo, CreateIssueBody request);

    IssueDTO getIssue(String owner, String repo, Integer issueNumber);

    IssueDTO updateIssue(String owner, String repo, Integer issueNumber, UpdateIssueBody request);
}
