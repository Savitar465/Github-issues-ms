package com.githubx.Github_issues_ms.controller;

import com.githubx.Github_issues_ms.generated.model.CommentDTO;
import com.githubx.Github_issues_ms.generated.model.CreateIssueCommentBody;
import com.githubx.Github_issues_ms.generated.model.ListIssueCommentsBody;
import com.githubx.Github_issues_ms.generated.model.UpdateIssueCommentBody;
import com.githubx.Github_issues_ms.service.contratos.IssueCommentService;
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
@Tag(name = "Issue Comments", description = "Gestión de comentarios de issues")
public class IssueCommentController {

    private final IssueCommentService issueCommentService;

    @GetMapping("/v1/repos/{owner}/{repo}/issues/{issueNumber}/comments")
    @Operation(summary = "Lista los comentarios de un issue",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ListIssueCommentsBody> listIssueComments(
            @PathVariable String owner,
            @PathVariable String repo,
            @PathVariable Integer issueNumber) {

        return ResponseEntity.ok(issueCommentService.listIssueComments(owner, repo, issueNumber));
    }

    @PostMapping("/v1/repos/{owner}/{repo}/issues/{issueNumber}/comments")
    @Operation(summary = "Agrega un comentario a un issue",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CommentDTO> createIssueComment(
            @PathVariable String owner,
            @PathVariable String repo,
            @PathVariable Integer issueNumber,
            @Valid @RequestBody CreateIssueCommentBody request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(issueCommentService.createIssueComment(owner, repo, issueNumber, request));
    }

    @GetMapping("/v1/repos/{owner}/{repo}/issues/comments")
    @Operation(summary = "Lista comentarios de issues a nivel repositorio",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ListIssueCommentsBody> listRepositoryIssueComments(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int perPage) {

        return ResponseEntity.ok(issueCommentService.listRepositoryIssueComments(owner, repo, page, perPage));
    }

    @GetMapping("/v1/repos/{owner}/{repo}/issues/comments/{commentId}")
    @Operation(summary = "Obtiene un comentario de issue por su ID",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CommentDTO> getIssueComment(
            @PathVariable String owner,
            @PathVariable String repo,
            @PathVariable String commentId) {

        return ResponseEntity.ok(issueCommentService.getIssueComment(owner, repo, commentId));
    }

    @PatchMapping("/v1/repos/{owner}/{repo}/issues/comments/{commentId}")
    @Operation(summary = "Actualiza el cuerpo de un comentario de issue",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CommentDTO> updateIssueComment(
            @PathVariable String owner,
            @PathVariable String repo,
            @PathVariable String commentId,
            @Valid @RequestBody UpdateIssueCommentBody request) {

        return ResponseEntity.ok(issueCommentService.updateIssueComment(owner, repo, commentId, request));
    }

    @DeleteMapping("/v1/repos/{owner}/{repo}/issues/comments/{commentId}")
    @Operation(summary = "Elimina un comentario de issue por su ID",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteIssueComment(
            @PathVariable String owner,
            @PathVariable String repo,
            @PathVariable String commentId) {

        issueCommentService.deleteIssueComment(owner, repo, commentId);
        return ResponseEntity.noContent().build();
    }
}
