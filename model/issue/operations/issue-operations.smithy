$version: "2"

namespace com.githubx.issue

use aws.protocols#restJson1
use com.githubx.common#BadRequestError
use com.githubx.common#ConflictError
use com.githubx.common#ForbiddenError
use com.githubx.common#HexColor
use com.githubx.common#InternalServerError
use com.githubx.common#IssueState
use com.githubx.common#NotFoundError
use com.githubx.common#PaginationMeta
use com.githubx.common#RepoName
use com.githubx.common#RepoScopedInputMixin
use com.githubx.common#Title
use com.githubx.common#UnauthorizedError
use com.githubx.common#Username
use com.githubx.common#Uuid

// ─── Tipos de soporte ─────────────────────────────────────────
structure AuthorSummary {
    @required
    id: Uuid

    @required
    username: Username
}

// ─── Labels ───────────────────────────────────────────────────
structure LabelDTO {
    @required
    id: Uuid

    @required
    repoId: String

    @required
    @length(min: 1, max: 50)
    name: String

    @required
    color: HexColor

    description: String
}

list LabelList {
    member: LabelDTO
}

list LabelNameList {
    member: String
}

// ─── Issues ───────────────────────────────────────────────────
structure IssueDTO {
    @required
    id: Uuid

    @required
    repoId: String

    @required
    number: Integer

    @required
    title: Title

    body: String

    @required
    state: IssueState

    @required
    author: AuthorSummary

    assignee: AuthorSummary

    @required
    labels: LabelList

    @required
    commentsCount: Integer

    @required
    createdAt: String

    @required
    updatedAt: String

    closedAt: String
}

list IssueList {
    member: IssueDTO
}

// ─── Listar issues ────────────────────────────────────────────
@http(method: "GET", uri: "/v1/repos/{owner}/{repo}/issues", code: 200)
@readonly
@documentation("Lista los issues de un repositorio con filtros opcionales.")
operation ListIssues {
    input: ListIssuesInput
    output: ListIssuesOutput
    errors: [
        UnauthorizedError
        ForbiddenError
        NotFoundError
        InternalServerError
    ]
}

structure ListIssuesInput {
    @required
    @httpLabel
    owner: Username

    @required
    @httpLabel
    repo: RepoName

    @httpQuery("state")
    state: IssueState

    @httpQuery("label")
    label: String

    @httpQuery("assignee")
    assignee: String

    @httpQuery("page")
    @range(min: 1)
    page: Integer

    @httpQuery("perPage")
    @range(min: 1, max: 100)
    perPage: Integer
}

structure ListIssuesOutput {
    @required
    @httpPayload
    body: ListIssuesBody
}

structure ListIssuesBody {
    @required
    issues: IssueList

    @required
    pagination: PaginationMeta
}

// ─── Crear issue ─────────────────────────────────────────────
@http(method: "POST", uri: "/v1/repos/{owner}/{repo}/issues", code: 201)
@documentation("Crea un issue en el repositorio.")
operation CreateIssue {
    input: CreateIssueInput
    output: CreateIssueOutput
    errors: [
        BadRequestError
        UnauthorizedError
        ForbiddenError
        NotFoundError
        InternalServerError
    ]
}

structure CreateIssueInput {
    @required
    @httpLabel
    owner: Username

    @required
    @httpLabel
    repo: RepoName

    @required
    @httpPayload
    body: CreateIssueBody
}

structure CreateIssueBody {
    @required
    title: Title

    body: String

    labels: LabelNameList

    assignee: String
}

structure CreateIssueOutput {
    @required
    @httpPayload
    body: IssueDTO
}

// ─── Obtener issue ────────────────────────────────────────────
@http(method: "GET", uri: "/v1/repos/{owner}/{repo}/issues/{issueNumber}", code: 200)
@readonly
@documentation("Obtiene un issue por su número secuencial.")
operation GetIssue {
    input: GetIssueInput
    output: GetIssueOutput
    errors: [
        UnauthorizedError
        ForbiddenError
        NotFoundError
        InternalServerError
    ]
}

structure GetIssueInput {
    @required
    @httpLabel
    owner: Username

    @required
    @httpLabel
    repo: RepoName

    @required
    @httpLabel
    issueNumber: Integer
}

structure GetIssueOutput {
    @required
    @httpPayload
    body: IssueDTO
}

// ─── Actualizar issue ─────────────────────────────────────────
@http(method: "PATCH", uri: "/v1/repos/{owner}/{repo}/issues/{issueNumber}", code: 200)
@documentation("Actualiza título, body, estado, assignee o labels.")
operation UpdateIssue {
    input: UpdateIssueInput
    output: UpdateIssueOutput
    errors: [
        BadRequestError
        UnauthorizedError
        ForbiddenError
        NotFoundError
        InternalServerError
    ]
}

structure UpdateIssueInput {
    @required
    @httpLabel
    owner: Username

    @required
    @httpLabel
    repo: RepoName

    @required
    @httpLabel
    issueNumber: Integer

    @required
    @httpPayload
    body: UpdateIssueBody
}

structure UpdateIssueBody {
    title: Title
    body: String
    state: IssueState
    assignee: String
    labels: LabelNameList
}

structure UpdateIssueOutput {
    @required
    @httpPayload
    body: IssueDTO
}

// ─── Comentarios ──────────────────────────────────────────────
structure CommentDTO {
    @required
    id: Uuid

    @required
    issueId: Uuid

    @required
    @length(min: 1)
    body: String

    @required
    author: AuthorSummary

    @required
    createdAt: String

    @required
    updatedAt: String
}

list CommentList {
    member: CommentDTO
}

@http(method: "GET", uri: "/v1/repos/{owner}/{repo}/issues/{issueNumber}/comments", code: 200)
@readonly
@documentation("Lista los comentarios de un issue.")
operation ListIssueComments {
    input: IssueCommentsInput
    output: ListIssueCommentsOutput
    errors: [
        UnauthorizedError
        ForbiddenError
        NotFoundError
        InternalServerError
    ]
}

structure IssueCommentsInput {
    @required
    @httpLabel
    owner: Username

    @required
    @httpLabel
    repo: RepoName

    @required
    @httpLabel
    issueNumber: Integer
}

structure ListIssueCommentsOutput {
    @required
    @httpPayload
    body: ListIssueCommentsBody
}

structure ListIssueCommentsBody {
    @required
    comments: CommentList
}

@http(method: "POST", uri: "/v1/repos/{owner}/{repo}/issues/{issueNumber}/comments", code: 201)
@documentation("Agrega un comentario a un issue.")
operation CreateIssueComment {
    input: CreateIssueCommentInput
    output: CreateIssueCommentOutput
    errors: [
        BadRequestError
        UnauthorizedError
        ForbiddenError
        NotFoundError
        InternalServerError
    ]
}

structure CreateIssueCommentInput {
    @required
    @httpLabel
    owner: Username

    @required
    @httpLabel
    repo: RepoName

    @required
    @httpLabel
    issueNumber: Integer

    @required
    @httpPayload
    body: CreateIssueCommentBody
}

structure CreateIssueCommentBody {
    @required
    @length(min: 1)
    body: String
}

structure CreateIssueCommentOutput {
    @required
    @httpPayload
    body: CommentDTO
}

@http(method: "GET", uri: "/v1/repos/{owner}/{repo}/issues/comments", code: 200)
@readonly
@documentation("Lista comentarios de issues a nivel repositorio.")
operation ListRepositoryIssueComments {
    input: ListRepositoryIssueCommentsInput
    output: ListIssueCommentsOutput
    errors: [
        UnauthorizedError
        ForbiddenError
        NotFoundError
        InternalServerError
    ]
}

structure ListRepositoryIssueCommentsInput {
    @required
    @httpLabel
    owner: Username

    @required
    @httpLabel
    repo: RepoName

    @httpQuery("page")
    @range(min: 1)
    page: Integer

    @httpQuery("perPage")
    @range(min: 1, max: 100)
    perPage: Integer
}

@http(method: "GET", uri: "/v1/repos/{owner}/{repo}/issues/comments/{commentId}", code: 200)
@readonly
@documentation("Obtiene un comentario de issue por su ID.")
operation GetIssueComment {
    input: IssueCommentByIdInput
    output: GetIssueCommentOutput
    errors: [
        UnauthorizedError
        ForbiddenError
        NotFoundError
        InternalServerError
    ]
}

structure GetIssueCommentOutput {
    @required
    @httpPayload
    body: CommentDTO
}

@http(method: "PATCH", uri: "/v1/repos/{owner}/{repo}/issues/comments/{commentId}", code: 200)
@documentation("Actualiza el cuerpo de un comentario de issue.")
operation UpdateIssueComment {
    input: UpdateIssueCommentInput
    output: UpdateIssueCommentOutput
    errors: [
        BadRequestError
        UnauthorizedError
        ForbiddenError
        NotFoundError
        InternalServerError
    ]
}

structure UpdateIssueCommentInput {
    @required
    @httpLabel
    owner: Username

    @required
    @httpLabel
    repo: RepoName

    @required
    @httpLabel
    commentId: Uuid

    @required
    @httpPayload
    body: UpdateIssueCommentBody
}

structure UpdateIssueCommentBody {
    @required
    @length(min: 1)
    body: String
}

structure UpdateIssueCommentOutput {
    @required
    @httpPayload
    body: CommentDTO
}

@http(method: "DELETE", uri: "/v1/repos/{owner}/{repo}/issues/comments/{commentId}", code: 204)
@idempotent
@documentation("Elimina un comentario de issue por su ID.")
operation DeleteIssueComment {
    input: IssueCommentByIdInput
    output: Unit
    errors: [
        UnauthorizedError
        ForbiddenError
        NotFoundError
        InternalServerError
    ]
}

structure IssueCommentByIdInput {
    @required
    @httpLabel
    owner: Username

    @required
    @httpLabel
    repo: RepoName

    @required
    @httpLabel
    commentId: Uuid
}

// ─── Labels de repositorio ────────────────────────────────────
@http(method: "GET", uri: "/v1/repos/{owner}/{repo}/labels", code: 200)
@readonly
@documentation("Lista los labels disponibles en el repositorio.")
operation ListLabels {
    input: RepoOnlyInput
    output: ListLabelsOutput
    errors: [
        UnauthorizedError
        ForbiddenError
        NotFoundError
        InternalServerError
    ]
}

structure RepoOnlyInput with [RepoScopedInputMixin] {
    @required
    @httpLabel
    owner: Username

    @required
    @httpLabel
    repo: RepoName
}

structure ListLabelsOutput {
    @required
    @httpPayload
    body: ListLabelsBody
}

structure ListLabelsBody {
    @required
    labels: LabelList
}

@http(method: "POST", uri: "/v1/repos/{owner}/{repo}/labels", code: 201)
@documentation("Crea un label nuevo en el repositorio.")
operation CreateLabel {
    input: CreateLabelInput
    output: CreateLabelOutput
    errors: [
        BadRequestError
        UnauthorizedError
        ForbiddenError
        NotFoundError
        ConflictError
        InternalServerError
    ]
}

structure CreateLabelInput {
    @required
    @httpLabel
    owner: Username

    @required
    @httpLabel
    repo: RepoName

    @required
    @httpPayload
    body: CreateLabelBody
}

structure CreateLabelBody {
    @required
    @length(min: 1, max: 50)
    name: String

    @required
    color: HexColor

    @length(max: 255)
    description: String
}

structure CreateLabelOutput {
    @required
    @httpPayload
    body: LabelDTO
}

