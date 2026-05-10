-- ============================================================
-- Github-issues-ms | Schema PostgreSQL
-- ============================================================

-- ===== TIPOS ENUM =====

CREATE TYPE issue_state AS ENUM ('OPEN', 'CLOSED');

-- ===== TABLA: issues =====

CREATE TABLE issues (
    id              UUID         NOT NULL DEFAULT gen_random_uuid(),
    repo_id         VARCHAR(255) NOT NULL,
    number          INTEGER      NOT NULL,
    title           VARCHAR(255) NOT NULL,
    body            TEXT,
    state           issue_state  NOT NULL DEFAULT 'OPEN',
    author_id       UUID         NOT NULL,
    author_username VARCHAR(50)  NOT NULL,
    assignee_id     UUID,
    assignee_username VARCHAR(50),
    comments_count  INTEGER      NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    closed_at       TIMESTAMPTZ,

    CONSTRAINT pk_issues PRIMARY KEY (id),
    CONSTRAINT uq_issue_repo_number UNIQUE (repo_id, number)
);

CREATE INDEX idx_issues_repo_id    ON issues (repo_id);
CREATE INDEX idx_issues_author_id  ON issues (author_id);
CREATE INDEX idx_issues_state      ON issues (repo_id, state);

-- ===== TABLA: labels =====

CREATE TABLE labels (
    id          UUID         NOT NULL DEFAULT gen_random_uuid(),
    repo_id     VARCHAR(255) NOT NULL,
    name        VARCHAR(50)  NOT NULL,
    color       VARCHAR(7)   NOT NULL,
    description VARCHAR(255),

    CONSTRAINT pk_labels PRIMARY KEY (id),
    CONSTRAINT uq_label_repo_name UNIQUE (repo_id, name)
);

CREATE INDEX idx_labels_repo_id ON labels (repo_id);

-- ===== TABLA: issue_labels (relacion N:M) =====

CREATE TABLE issue_labels (
    issue_id UUID NOT NULL,
    label_id UUID NOT NULL,

    CONSTRAINT pk_issue_labels PRIMARY KEY (issue_id, label_id),
    CONSTRAINT fk_issue_labels_issue
        FOREIGN KEY (issue_id)
        REFERENCES issues (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_issue_labels_label
        FOREIGN KEY (label_id)
        REFERENCES labels (id)
        ON DELETE CASCADE
);

-- ===== TABLA: issue_comments =====

CREATE TABLE issue_comments (
    id         UUID        NOT NULL DEFAULT gen_random_uuid(),
    issue_id   UUID        NOT NULL,
    body       TEXT        NOT NULL,
    author_id  UUID        NOT NULL,
    author_username VARCHAR(50) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_issue_comments PRIMARY KEY (id),
    CONSTRAINT fk_issue_comments_issue
        FOREIGN KEY (issue_id)
        REFERENCES issues (id)
        ON DELETE CASCADE
);

CREATE INDEX idx_issue_comments_issue_id ON issue_comments (issue_id);
CREATE INDEX idx_issue_comments_author   ON issue_comments (author_id);

-- ===== TRIGGER: actualiza updated_at automaticamente =====

CREATE OR REPLACE FUNCTION fn_set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_issues_updated_at
    BEFORE UPDATE ON issues
    FOR EACH ROW
    EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_issue_comments_updated_at
    BEFORE UPDATE ON issue_comments
    FOR EACH ROW
    EXECUTE FUNCTION fn_set_updated_at();

-- ===== TRIGGER: incrementa/decrementa comments_count =====

CREATE OR REPLACE FUNCTION fn_update_comments_count()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE issues SET comments_count = comments_count + 1 WHERE id = NEW.issue_id;
    ELSIF TG_OP = 'DELETE' THEN
        UPDATE issues SET comments_count = comments_count - 1 WHERE id = OLD.issue_id;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_comments_count_insert
    AFTER INSERT ON issue_comments
    FOR EACH ROW EXECUTE FUNCTION fn_update_comments_count();

CREATE TRIGGER trg_comments_count_delete
    AFTER DELETE ON issue_comments
    FOR EACH ROW EXECUTE FUNCTION fn_update_comments_count();
