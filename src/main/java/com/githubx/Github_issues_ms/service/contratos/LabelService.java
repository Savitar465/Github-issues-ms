package com.githubx.Github_issues_ms.service.contratos;

import com.githubx.Github_issues_ms.generated.model.CreateLabelBody;
import com.githubx.Github_issues_ms.generated.model.LabelDTO;
import com.githubx.Github_issues_ms.generated.model.ListLabelsBody;

public interface LabelService {

    ListLabelsBody listLabels(String owner, String repo);

    LabelDTO createLabel(String owner, String repo, CreateLabelBody request);
}
