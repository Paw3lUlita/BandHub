-- Sprint 16: parametryzowane wydruki DOCX (szablony edytowalne przez użytkownika)

CREATE TABLE docx_templates
(
    id              UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    name            VARCHAR(255) NOT NULL,
    module_code     VARCHAR(80)  NOT NULL,
    template_version INT         NOT NULL DEFAULT 1,
    active          BOOLEAN      NOT NULL DEFAULT FALSE,
    file_path       VARCHAR(500) NOT NULL,
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_docx_templates_module ON docx_templates (module_code);
CREATE INDEX idx_docx_templates_created_at ON docx_templates (created_at DESC);

-- Co najwyżej jeden aktywny szablon na moduł (MVP)
CREATE UNIQUE INDEX uq_docx_templates_one_active_per_module
    ON docx_templates (module_code)
    WHERE active = TRUE;
