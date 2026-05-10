package com.githubx.Github_issues_ms.controller;

import com.githubx.Github_issues_ms.generated.model.CreateIssueBody;
import com.githubx.Github_issues_ms.generated.model.IssueDTO;
import com.githubx.Github_issues_ms.generated.model.ListIssuesBody;
import com.githubx.Github_issues_ms.generated.model.UpdateIssueBody;
import com.githubx.Github_issues_ms.service.contratos.IssueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Issues", description = "Gestión de issues de repositorio")
public class IssueController {

    private final IssueService issueService;

    @GetMapping("/v1/repos/{owner}/{repo}/issues")
    @Operation(summary = "Lista los issues de un repositorio con filtros opcionales",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ListIssuesBody> listIssues(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String label,
            @RequestParam(required = false) String assignee,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int perPage) {

        return ResponseEntity.ok(issueService.listIssues(owner, repo, state, label, assignee, page, perPage));
    }

    @PostMapping("/v1/repos/{owner}/{repo}/issues")
    @Operation(summary = "Crea un issue en el repositorio",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<IssueDTO> createIssue(
            @PathVariable String owner,
            @PathVariable String repo,
            @Valid @RequestBody CreateIssueBody request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(issueService.createIssue(owner, repo, request));
    }

    @GetMapping("/v1/repos/{owner}/{repo}/issues/{issueNumber}")
    @Operation(summary = "Obtiene un issue por su número secuencial",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<IssueDTO> getIssue(
            @PathVariable String owner,
            @PathVariable String repo,
            @PathVariable Integer issueNumber) {

        return ResponseEntity.ok(issueService.getIssue(owner, repo, issueNumber));
    }

    @PatchMapping("/v1/repos/{owner}/{repo}/issues/{issueNumber}")
    @Operation(summary = "Actualiza título, body, estado, assignee o labels",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<IssueDTO> updateIssue(
            @PathVariable String owner,
            @PathVariable String repo,
            @PathVariable Integer issueNumber,
            @Valid @RequestBody UpdateIssueBody request) {

        return ResponseEntity.ok(issueService.updateIssue(owner, repo, issueNumber, request));
    }
}
