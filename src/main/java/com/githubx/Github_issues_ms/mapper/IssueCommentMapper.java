package com.githubx.Github_issues_ms.mapper;

import com.githubx.Github_issues_ms.generated.model.AuthorSummary;
import com.githubx.Github_issues_ms.generated.model.CommentDTO;
import com.githubx.Github_issues_ms.model.IssueComment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IssueCommentMapper {

    @Mapping(target = "id", expression = "java(entity.getId().toString())")
    @Mapping(target = "issueId", expression = "java(entity.getIssue().getId().toString())")
    @Mapping(target = "body", source = "body")
    @Mapping(target = "author", expression = "java(toAuthorSummary(entity.getAuthorId().toString(), entity.getAuthorUsername()))")
    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAt().toString())")
    @Mapping(target = "updatedAt", expression = "java(entity.getUpdatedAt().toString())")
    CommentDTO toDto(IssueComment entity);

    List<CommentDTO> toDtoList(List<IssueComment> entities);

    default AuthorSummary toAuthorSummary(String id, String username) {
        return new AuthorSummary()
                .id(id)
                .username(username);
    }
}
