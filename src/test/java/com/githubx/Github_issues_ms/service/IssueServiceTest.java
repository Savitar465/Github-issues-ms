package com.githubx.Github_issues_ms.service;

import com.githubx.Github_issues_ms.config.security.AuthenticatedUserResolver;
import com.githubx.Github_issues_ms.dao.IssueDao;
import com.githubx.Github_issues_ms.dao.LabelDao;
import com.githubx.Github_issues_ms.generated.model.CreateIssueBody;
import com.githubx.Github_issues_ms.generated.model.IssueDTO;
import com.githubx.Github_issues_ms.mapper.IssueMapper;
import com.githubx.Github_issues_ms.model.Issue;
import com.githubx.Github_issues_ms.model.IssueState;
import com.githubx.Github_issues_ms.service.implementacion.IssueServiceImpl;
import com.githubx.Github_issues_ms.util.errorhandling.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IssueServiceTest {

    @Mock
    private IssueDao issueDao;

    @Mock
    private LabelDao labelDao;

    @Mock
    private IssueMapper issueMapper;

    @Mock
    private AuthenticatedUserResolver userResolver;

    @InjectMocks
    private IssueServiceImpl issueService;

    @Test
    void debeCrearIssueCorrectamente() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userResolver.getCurrentUserId()).thenReturn(userId);
        when(userResolver.getCurrentUsername()).thenReturn("dev-user");
        when(issueDao.findMaxNumberByRepoId("octocat/hello-world")).thenReturn(0);

        Issue savedIssue = Issue.builder()
                .id(UUID.randomUUID())
                .repoId("octocat/hello-world")
                .number(1)
                .title("Nuevo issue")
                .state(IssueState.OPEN)
                .authorId(userId)
                .authorUsername("dev-user")
                .commentsCount(0)
                .labels(List.of())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(issueDao.save(any(Issue.class))).thenReturn(savedIssue);

        IssueDTO expectedDto = new IssueDTO();
        expectedDto.setNumber(1);
        expectedDto.setTitle("Nuevo issue");
        when(issueMapper.toDto(savedIssue)).thenReturn(expectedDto);

        CreateIssueBody request = new CreateIssueBody().title("Nuevo issue");

        // Act
        IssueDTO result = issueService.createIssue("octocat", "hello-world", request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getNumber()).isEqualTo(1);
        assertThat(result.getTitle()).isEqualTo("Nuevo issue");
    }

    @Test
    void debeLanzarExcepcionCuandoIssueNoExiste() {
        // Arrange
        when(issueDao.findByRepoIdAndNumber("octocat/hello-world", 999))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> issueService.getIssue("octocat", "hello-world", 999))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("999");
    }
}
