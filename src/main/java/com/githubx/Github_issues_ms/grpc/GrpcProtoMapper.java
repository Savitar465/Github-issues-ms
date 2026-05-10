package com.githubx.Github_issues_ms.grpc;

import com.githubx.Github_issues_ms.generated.model.IssueState;
import com.githubx.grpc.proto.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GrpcProtoMapper {

    // ─── IssueState ───────────────────────────────────────────

    public com.githubx.grpc.proto.IssueState toProtoState(IssueState state) {
        if (state == null) return com.githubx.grpc.proto.IssueState.ISSUE_STATE_UNSPECIFIED;
        return switch (state) {
            case OPEN   -> com.githubx.grpc.proto.IssueState.ISSUE_STATE_OPEN;
            case CLOSED -> com.githubx.grpc.proto.IssueState.ISSUE_STATE_CLOSED;
        };
    }

    public IssueState fromProtoState(com.githubx.grpc.proto.IssueState state) {
        return switch (state) {
            case ISSUE_STATE_CLOSED -> IssueState.CLOSED;
            default                 -> IssueState.OPEN;
        };
    }

    // ─── AuthorSummary ────────────────────────────────────────

    public com.githubx.grpc.proto.AuthorSummary toProtoAuthor(
            com.githubx.Github_issues_ms.generated.model.AuthorSummary author) {
        if (author == null) return com.githubx.grpc.proto.AuthorSummary.getDefaultInstance();
        return com.githubx.grpc.proto.AuthorSummary.newBuilder()
                .setId(safe(author.getId()))
                .setUsername(safe(author.getUsername()))
                .build();
    }

    // ─── LabelDTO ─────────────────────────────────────────────

    public com.githubx.grpc.proto.LabelDTO toProtoLabel(
            com.githubx.Github_issues_ms.generated.model.LabelDTO dto) {
        if (dto == null) return com.githubx.grpc.proto.LabelDTO.getDefaultInstance();
        return com.githubx.grpc.proto.LabelDTO.newBuilder()
                .setId(safe(dto.getId()))
                .setRepoId(safe(dto.getRepoId()))
                .setName(safe(dto.getName()))
                .setColor(safe(dto.getColor()))
                .setDescription(safe(dto.getDescription()))
                .build();
    }

    // ─── IssueDTO ─────────────────────────────────────────────

    public com.githubx.grpc.proto.IssueDTO toProtoIssue(
            com.githubx.Github_issues_ms.generated.model.IssueDTO dto) {

        List<com.githubx.grpc.proto.LabelDTO> protoLabels = dto.getLabels() != null
                ? dto.getLabels().stream().map(this::toProtoLabel).toList()
                : List.of();

        var builder = com.githubx.grpc.proto.IssueDTO.newBuilder()
                .setId(safe(dto.getId()))
                .setRepoId(safe(dto.getRepoId()))
                .setNumber(safeInt(dto.getNumber()))
                .setTitle(safe(dto.getTitle()))
                .setBody(safe(dto.getBody()))
                .setState(toProtoState(dto.getState()))
                .setAuthor(toProtoAuthor(dto.getAuthor()))
                .addAllLabels(protoLabels)
                .setCommentsCount(safeInt(dto.getCommentsCount()))
                .setCreatedAt(safe(dto.getCreatedAt()))
                .setUpdatedAt(safe(dto.getUpdatedAt()))
                .setClosedAt(safe(dto.getClosedAt()));

        if (dto.getAssignee() != null) {
            builder.setAssignee(toProtoAuthor(dto.getAssignee()));
        }

        return builder.build();
    }

    // ─── CommentDTO ───────────────────────────────────────────

    public com.githubx.grpc.proto.CommentDTO toProtoComment(
            com.githubx.Github_issues_ms.generated.model.CommentDTO dto) {
        return com.githubx.grpc.proto.CommentDTO.newBuilder()
                .setId(safe(dto.getId()))
                .setIssueId(safe(dto.getIssueId()))
                .setBody(safe(dto.getBody()))
                .setAuthor(toProtoAuthor(dto.getAuthor()))
                .setCreatedAt(safe(dto.getCreatedAt()))
                .setUpdatedAt(safe(dto.getUpdatedAt()))
                .build();
    }

    // ─── PaginationMeta ───────────────────────────────────────

    public com.githubx.grpc.proto.PaginationMeta toProtoPagination(
            com.githubx.Github_issues_ms.generated.model.PaginationMeta m) {
        return com.githubx.grpc.proto.PaginationMeta.newBuilder()
                .setPage(safeInt(m.getPage()))
                .setPerPage(safeInt(m.getPerPage()))
                .setTotal(safeInt(m.getTotal()))
                .setTotalPages(safeInt(m.getTotalPages()))
                .build();
    }

    // ─── Helpers ──────────────────────────────────────────────

    private String safe(String s) { return s != null ? s : ""; }
    private int safeInt(Integer i) { return i != null ? i : 0; }
}
