package com.githubx.Github_issues_ms.controller;

import com.githubx.Github_issues_ms.generated.model.IssueDTO;
import com.githubx.Github_issues_ms.generated.model.ListIssuesBody;
import com.githubx.Github_issues_ms.generated.model.PaginationMeta;
import com.githubx.Github_issues_ms.service.contratos.IssueService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = IssueController.class)
class IssueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IssueService issueService;

    @Test
    @WithMockUser
    void debeListarIssues() throws Exception {
        // Arrange
        ListIssuesBody response = new ListIssuesBody()
                .issues(List.of())
                .pagination(new PaginationMeta().page(1).perPage(20).total(0).totalPages(0));

        Mockito.when(issueService.listIssues(eq("octocat"), eq("hello-world"),
                any(), any(), any(), eq(1), eq(20)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/v1/repos/octocat/hello-world/issues")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.issues").isArray())
                .andExpect(jsonPath("$.pagination.page").value(1));
    }

    @Test
    @WithMockUser
    void debeCrearIssue() throws Exception {
        // Arrange
        IssueDTO issueDTO = new IssueDTO();
        issueDTO.setId("some-uuid");
        issueDTO.setNumber(1);
        issueDTO.setTitle("Bug encontrado");

        Mockito.when(issueService.createIssue(eq("octocat"), eq("hello-world"), any()))
                .thenReturn(issueDTO);

        String requestBody = """
                {
                    "title": "Bug encontrado",
                    "body": "Descripción del bug"
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/v1/repos/octocat/hello-world/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Bug encontrado"));
    }
@WithMockUser
    
    @Test
    void debeRetornar404CuandoIssueNoExiste() throws Exception {
        // Arrange
        Mockito.when(issueService.getIssue(eq("octocat"), eq("hello-world"), eq(999)))
                .thenThrow(new com.githubx.Github_issues_ms.util.errorhandling.EntityNotFoundException(
                        "Issue no encontrado con número: 999"));

        // Act & Assert
        mockMvc.perform(get("/v1/repos/octocat/hello-world/issues/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
