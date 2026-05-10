$version: "2"

namespace com.githubx.issue

use aws.protocols#restJson1
use aws.apigateway#integration

@title("GitHub Issue API")
@restJson1
@httpBearerAuth
@integration(
    type: "http_proxy"
    uri: "https://example.com/issue"
    httpMethod: "POST"
)
@documentation("Servicio para issues, labels y comentarios.")
service IssueApi {
    version: "1.0.0"
    operations: [
        ListIssues
        CreateIssue
        GetIssue
        UpdateIssue
        ListIssueComments
        CreateIssueComment
        ListLabels
        CreateLabel
    ]
}

@title("GitHub Issue Comments API")
@restJson1
@httpBearerAuth
@integration(
    type: "http_proxy"
    uri: "https://example.com/issue/comments"
    httpMethod: "POST"
)
@documentation("Servicio separado para comentarios de issue a nivel repositorio por compatibilidad de rutas.")
service IssueCommentsApi {
    version: "1.0.0"
    operations: [
        ListRepositoryIssueComments
        GetIssueComment
        UpdateIssueComment
        DeleteIssueComment
    ]
}

