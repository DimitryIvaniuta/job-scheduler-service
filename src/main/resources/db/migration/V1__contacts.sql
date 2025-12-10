CREATE EXTENSION IF NOT EXISTS citext;

CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE TABLE contacts
(
    id                  uuid PRIMARY KEY,
    email               citext      NOT NULL,
    secondary_email     citext,
    first_name          varchar(100),
    middle_name         varchar(100),
    last_name           varchar(100),
    mobile_phone        varchar(50),
    work_phone          varchar(50),
    home_phone          varchar(50),
    company_name        varchar(200),
    job_title           varchar(150),
    address_line1       varchar(200),
    address_line2       varchar(200),
    city                varchar(100),
    state_region        varchar(100),
    postal_code         varchar(20),
    country_code        char(2),
    time_zone           varchar(64),
    locale              varchar(10),
    preferred_channel   varchar(32),
    tags                varchar(512),
    birth_date          date,
    gender              varchar(20),
    is_active           boolean     NOT NULL DEFAULT true,
    marketing_opt_in    boolean     NOT NULL DEFAULT false,
    marketing_opt_in_at timestamptz,
    unsubscribed        boolean     NOT NULL DEFAULT false,
    unsubscribed_at     timestamptz,
    bounce_count        integer     NOT NULL DEFAULT 0,
    last_emailed_at     timestamptz,
    last_opened_at      timestamptz,
    last_clicked_at     timestamptz,
    last_activity_at    timestamptz,
    created_at          timestamptz NOT NULL,
    updated_at          timestamptz NOT NULL
);

-- Case-insensitive unique email
CREATE UNIQUE INDEX ux_contacts_email_ci
    ON contacts (email);

CREATE INDEX idx_contacts_secondary_email_ci
    ON contacts (secondary_email);

-- Common name search/sort
CREATE INDEX idx_contacts_last_first
    ON contacts (lower(last_name), lower(first_name));

-- Company filter/sort
CREATE INDEX idx_contacts_company
    ON contacts (lower(company_name));

-- Geo filters
CREATE INDEX idx_contacts_country_city
    ON contacts (country_code, lower(city));

-- Consent/active filters
CREATE INDEX idx_contacts_active_marketing
    ON contacts (is_active, marketing_opt_in, unsubscribed);

-- Time-based filters
CREATE INDEX idx_contacts_created_at
    ON contacts (created_at);

CREATE INDEX idx_contacts_last_activity_at
    ON contacts (last_activity_at);

CREATE INDEX idx_contacts_last_emailed_at
    ON contacts (last_emailed_at);

-- Fast LIKE / free-text via trigram
CREATE INDEX gin_contacts_email_trgm
    ON contacts USING gin (email gin_trgm_ops);

CREATE INDEX gin_contacts_name_trgm
    ON contacts USING gin ((first_name || ' ' || last_name) gin_trgm_ops);

CREATE INDEX gin_contacts_company_trgm
    ON contacts USING gin (company_name gin_trgm_ops);

CREATE INDEX gin_contacts_tags_trgm
    ON contacts USING gin (tags gin_trgm_ops);
