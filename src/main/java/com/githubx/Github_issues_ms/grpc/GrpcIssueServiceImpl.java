package com.githubx.Github_issues_ms.grpc;

import com.githubx.Github_issues_ms.generated.model.CreateIssueBody;
import com.githubx.Github_issues_ms.generated.model.CreateIssueCommentBody;
import com.githubx.Github_issues_ms.generated.model.CreateLabelBody;
import com.githubx.Github_issues_ms.generated.model.UpdateIssueBody;
import com.githubx.Github_issues_ms.generated.model.UpdateIssueCommentBody;
import com.githubx.Github_issues_ms.service.contratos.IssueCommentService;
import com.githubx.Github_issues_ms.service.contratos.IssueService;
import com.githubx.Github_issues_ms.service.contratos.LabelService;
import com.githubx.Github_issues_ms.util.errorhandling.EntityConflictException;
import com.githubx.Github_issues_ms.util.errorhandling.EntityNotFoundException;
import com.githubx.Github_issues_ms.util.errorhandling.ForbiddenOperationException;
import com.githubx.grpc.proto.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class GrpcIssueServiceImpl extends IssueServiceGrpc.IssueServiceImplBase {

    private final IssueService issueService;
    private final IssueCommentService issueCommentService;
    private final LabelService labelService;
    private final GrpcProtoMapper mapper;

    // ─── Issues ───────────────────────────────────────────────

    @Override
    public void createIssue(CreateIssueRequest req, StreamObserver<CreateIssueResponse> obs) {
        try {
            var body = new CreateIssueBody()
                    .title(req.getTitle())
                    .body(req.getBody().isBlank() ? null : req.getBody())
                    .labels(req.getLabelsList().isEmpty() ? null : req.getLabelsList())
                    .assignee(req.getAssignee().isBlank() ? null : req.getAssignee());
            obs.onNext(CreateIssueResponse.newBuilder()
                    .setIssue(mapper.toProtoIssue(issueService.createIssue(req.getOwner(), req.getRepo(), body)))
                    .build());
            obs.onCompleted();
        } catch (Exception e) {
            obs.onError(toStatus(e).asRuntimeException());
        }
    }

    @Override
    public void updateIssue(UpdateIssueRequest req, StreamObserver<UpdateIssueResponse> obs) {
        try {
            var body = new UpdateIssueBody()
                    .title(req.getTitle().isBlank()    ? null : req.getTitle())
                    .body(req.getBody().isBlank()      ? null : req.getBody())
                    .assignee(req.getAssignee().isBlank() ? null : req.getAssignee())
                    .labels(req.getLabelsList().isEmpty()  ? null : req.getLabelsList());

            if (req.getState() != IssueState.ISSUE_STATE_UNSPECIFIED) {
                body.setState(mapper.fromProtoState(req.getState()));
            }

            obs.onNext(UpdateIssueResponse.newBuilder()
                    .setIssue(mapper.toProtoIssue(issueService.updateIssue(req.getOwner(), req.getRepo(), req.getIssueNumber(), body)))
                    .build());
            obs.onCompleted();
        } catch (Exception e) {
            obs.onError(toStatus(e).asRuntimeException());
        }
    }

    // ─── Comments ─────────────────────────────────────────────

    @Override
    public void getIssueComment(GetIssueCommentRequest req, StreamObserver<GetIssueCommentResponse> obs) {
        try {
            obs.onNext(GetIssueCommentResponse.newBuilder()
                    .setComment(mapper.toProtoComment(issueCommentService.getIssueComment(req.getOwner(), req.getRepo(), req.getCommentId())))
                    .build());
            obs.onCompleted();
        } catch (Exception e) {
            obs.onError(toStatus(e).asRuntimeException());
        }
    }

    @Override
    public void createIssueComment(CreateIssueCommentRequest req, StreamObserver<CreateIssueCommentResponse> obs) {
        try {
            var body = new CreateIssueCommentBody().body(req.getBody());
            obs.onNext(CreateIssueCommentResponse.newBuilder()
                    .setComment(mapper.toProtoComment(issueCommentService.createIssueComment(req.getOwner(), req.getRepo(), req.getIssueNumber(), body)))
                    .build());
            obs.onCompleted();
        } catch (Exception e) {
            obs.onError(toStatus(e).asRuntimeException());
        }
    }

    @Override
    public void updateIssueComment(UpdateIssueCommentRequest req, StreamObserver<UpdateIssueCommentResponse> obs) {
        try {
            var body = new UpdateIssueCommentBody().body(req.getBody());
            obs.onNext(UpdateIssueCommentResponse.newBuilder()
                    .setComment(mapper.toProtoComment(issueCommentService.updateIssueComment(req.getOwner(), req.getRepo(), req.getCommentId(), body)))
                    .build());
            obs.onCompleted();
        } catch (Exception e) {
            obs.onError(toStatus(e).asRuntimeException());
        }
    }

    @Override
    public void deleteIssueComment(DeleteIssueCommentRequest req, StreamObserver<DeleteIssueCommentResponse> obs) {
        try {
            issueCommentService.deleteIssueComment(req.getOwner(), req.getRepo(), req.getCommentId());
            obs.onNext(DeleteIssueCommentResponse.newBuilder().setSuccess(true).build());
            obs.onCompleted();
        } catch (Exception e) {
            obs.onError(toStatus(e).asRuntimeException());
        }
    }

    // ─── Labels ───────────────────────────────────────────────

    @Override
    public void createLabel(CreateLabelRequest req, StreamObserver<CreateLabelResponse> obs) {
        try {
            var body = new CreateLabelBody()
                    .name(req.getName())
                    .color(req.getColor())
                    .description(req.getDescription().isBlank() ? null : req.getDescription());
            obs.onNext(CreateLabelResponse.newBuilder()
                    .setLabel(mapper.toProtoLabel(labelService.createLabel(req.getOwner(), req.getRepo(), body)))
                    .build());
            obs.onCompleted();
        } catch (Exception e) {
            obs.onError(toStatus(e).asRuntimeException());
        }
    }

    // ─── Exception mapping ────────────────────────────────────

    private Status toStatus(Exception e) {
        if (e instanceof EntityNotFoundException)   return Status.NOT_FOUND.withDescription(e.getMessage());
        if (e instanceof EntityConflictException)   return Status.ALREADY_EXISTS.withDescription(e.getMessage());
        if (e instanceof ForbiddenOperationException) return Status.PERMISSION_DENIED.withDescription(e.getMessage());
        return Status.INTERNAL.withDescription(e.getMessage());
    }
}
