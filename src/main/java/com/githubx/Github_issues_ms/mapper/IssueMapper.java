package com.githubx.Github_issues_ms.mapper;

import com.githubx.Github_issues_ms.generated.model.AuthorSummary;
import com.githubx.Github_issues_ms.generated.model.IssueDTO;
import com.githubx.Github_issues_ms.generated.model.LabelDTO;
import com.githubx.Github_issues_ms.model.Issue;
import com.githubx.Github_issues_ms.model.Label;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        imports = com.githubx.Github_issues_ms.generated.model.IssueState.class)
public interface IssueMapper {

    @Mapping(target = "id", expression = "java(entity.getId().toString())")
    @Mapping(target = "repoId", source = "repoId")
    @Mapping(target = "number", source = "number")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "body", source = "body")
    @Mapping(target = "state", expression = "java(IssueState.fromValue(entity.getState().name()))")
    @Mapping(target = "author", expression = "java(toAuthorSummary(entity.getAuthorId().toString(), entity.getAuthorUsername()))")
    @Mapping(target = "assignee", expression = "java(entity.getAssigneeId() != null ? toAuthorSummary(entity.getAssigneeId().toString(), entity.getAssigneeUsername()) : null)")
    @Mapping(target = "labels", source = "labels")
    @Mapping(target = "commentsCount", source = "commentsCount")
    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAt().toString())")
    @Mapping(target = "updatedAt", expression = "java(entity.getUpdatedAt().toString())")
    @Mapping(target = "closedAt", expression = "java(entity.getClosedAt() != null ? entity.getClosedAt().toString() : null)")
    IssueDTO toDto(Issue entity);

    List<IssueDTO> toDtoList(List<Issue> entities);

    default AuthorSummary toAuthorSummary(String id, String username) {
        return new AuthorSummary()
                .id(id)
                .username(username);
    }

    @Mapping(target = "id", expression = "java(label.getId().toString())")
    @Mapping(target = "repoId", source = "repoId")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "color", source = "color")
    @Mapping(target = "description", source = "description")
    LabelDTO toLabelDto(Label label);

    List<LabelDTO> toLabelDtoList(List<Label> labels);
}
