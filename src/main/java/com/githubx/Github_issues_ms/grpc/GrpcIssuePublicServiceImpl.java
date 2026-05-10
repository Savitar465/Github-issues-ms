package com.githubx.Github_issues_ms.grpc;

import com.githubx.Github_issues_ms.generated.model.ListIssuesBody;
import com.githubx.Github_issues_ms.service.contratos.IssueCommentService;
import com.githubx.Github_issues_ms.service.contratos.IssueService;
import com.githubx.Github_issues_ms.service.contratos.LabelService;
import com.githubx.Github_issues_ms.util.errorhandling.EntityNotFoundException;
import com.githubx.grpc.proto.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class GrpcIssuePublicServiceImpl extends IssuePublicServiceGrpc.IssuePublicServiceImplBase {

    private final IssueService issueService;
    private final IssueCommentService issueCommentService;
    private final LabelService labelService;
    private final GrpcProtoMapper mapper;

    @Override
    public void getIssue(GetIssueRequest req, StreamObserver<GetIssueResponse> obs) {
        try {
            obs.onNext(GetIssueResponse.newBuilder()
                    .setIssue(mapper.toProtoIssue(issueService.getIssue(req.getOwner(), req.getRepo(), req.getIssueNumber())))
                    .build());
            obs.onCompleted();
        } catch (EntityNotFoundException e) {
            obs.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            obs.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void listIssues(ListIssuesRequest req, StreamObserver<ListIssuesResponse> obs) {
        try {
            int page    = req.getPagination().getPage() > 0 ? req.getPagination().getPage() : 1;
            int perPage = req.getPagination().getPerPage() > 0 ? req.getPagination().getPerPage() : 20;

            String state    = req.getState() == IssueState.ISSUE_STATE_UNSPECIFIED ? null : req.getState().name().toLowerCase().replace("issue_state_", "");
            String label    = req.getLabel().isBlank()    ? null : req.getLabel();
            String assignee = req.getAssignee().isBlank() ? null : req.getAssignee();

            ListIssuesBody result = issueService.listIssues(req.getOwner(), req.getRepo(), state, label, assignee, page, perPage);

            obs.onNext(ListIssuesResponse.newBuilder()
                    .addAllIssues(result.getIssues().stream().map(mapper::toProtoIssue).toList())
                    .setPagination(mapper.toProtoPagination(result.getPagination()))
                    .build());
            obs.onCompleted();
        } catch (Exception e) {
            obs.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void listIssueComments(ListIssueCommentsRequest req, StreamObserver<ListIssueCommentsResponse> obs) {
        try {
            var result = issueCommentService.listIssueComments(req.getOwner(), req.getRepo(), req.getIssueNumber());
            obs.onNext(ListIssueCommentsResponse.newBuilder()
                    .addAllComments(result.getComments().stream().map(mapper::toProtoComment).toList())
                    .build());
            obs.onCompleted();
        } catch (EntityNotFoundException e) {
            obs.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            obs.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void listLabels(ListLabelsRequest req, StreamObserver<ListLabelsResponse> obs) {
        try {
            var result = labelService.listLabels(req.getOwner(), req.getRepo());
            obs.onNext(ListLabelsResponse.newBuilder()
                    .addAllLabels(result.getLabels().stream().map(mapper::toProtoLabel).toList())
                    .build());
            obs.onCompleted();
        } catch (Exception e) {
            obs.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }
}
