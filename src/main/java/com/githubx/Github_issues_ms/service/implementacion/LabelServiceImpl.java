package com.githubx.Github_issues_ms.service.implementacion;

import com.githubx.Github_issues_ms.dao.LabelDao;
import com.githubx.Github_issues_ms.generated.model.CreateLabelBody;
import com.githubx.Github_issues_ms.generated.model.LabelDTO;
import com.githubx.Github_issues_ms.generated.model.ListLabelsBody;
import com.githubx.Github_issues_ms.mapper.IssueMapper;
import com.githubx.Github_issues_ms.model.Label;
import com.githubx.Github_issues_ms.service.contratos.LabelService;
import com.githubx.Github_issues_ms.util.errorhandling.EntityConflictException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {

    private final LabelDao labelDao;
    private final IssueMapper issueMapper;

    @Override
    @Transactional(readOnly = true)
    public ListLabelsBody listLabels(String owner, String repo) {
        String repoId = buildRepoId(owner, repo);
        List<Label> labels = labelDao.findAllByRepoId(repoId);
        return new ListLabelsBody().labels(issueMapper.toLabelDtoList(labels));
    }

    @Override
    @Transactional
    public LabelDTO createLabel(String owner, String repo, CreateLabelBody request) {
        String repoId = buildRepoId(owner, repo);

        if (labelDao.existsByRepoIdAndName(repoId, request.getName())) {
            throw EntityConflictException.labelName(request.getName());
        }

        Label label = Label.builder()
                .repoId(repoId)
                .name(request.getName())
                .color(request.getColor())
                .description(request.getDescription())
                .build();

        label = labelDao.save(label);
        log.info("Label '{}' creado en repo: {}", label.getName(), repoId);
        return issueMapper.toLabelDto(label);
    }

    private String buildRepoId(String owner, String repo) {
        return owner + "/" + repo;
    }
}
