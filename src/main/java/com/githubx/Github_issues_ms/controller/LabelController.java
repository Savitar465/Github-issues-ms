package com.githubx.Github_issues_ms.controller;

import com.githubx.Github_issues_ms.generated.model.CreateLabelBody;
import com.githubx.Github_issues_ms.generated.model.LabelDTO;
import com.githubx.Github_issues_ms.generated.model.ListLabelsBody;
import com.githubx.Github_issues_ms.service.contratos.LabelService;
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
@Tag(name = "Labels", description = "Gestión de labels de repositorio")
public class LabelController {

    private final LabelService labelService;

    @GetMapping("/v1/repos/{owner}/{repo}/labels")
    @Operation(summary = "Lista los labels disponibles en el repositorio",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ListLabelsBody> listLabels(
            @PathVariable String owner,
            @PathVariable String repo) {

        return ResponseEntity.ok(labelService.listLabels(owner, repo));
    }

    @PostMapping("/v1/repos/{owner}/{repo}/labels")
    @Operation(summary = "Crea un label nuevo en el repositorio",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<LabelDTO> createLabel(
            @PathVariable String owner,
            @PathVariable String repo,
            @Valid @RequestBody CreateLabelBody request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(labelService.createLabel(owner, repo, request));
    }
}
