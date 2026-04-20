CREATE DATABASE "argo-dev";
CREATE DATABASE "argo-prod";
CREATE DATABASE "keycloak";

CREATE TABLE IF NOT EXISTS category_sequences (
    category  VARCHAR(50) PRIMARY KEY,
    last_val  BIGINT      NOT NULL DEFAULT 0
);

INSERT INTO category_sequences (category, last_val) VALUES
    ('ENGINE_SPARES',     0),
    ('DECK_STORES',       0),
    ('SAFETY_EQUIPMENT',  0),
    ('LUBRICANTS',        0),
    ('CHEMICALS',         0),
    ('PROVISIONS',        0),
    ('MEDICAL',           0),
    ('ELECTRICAL',        0),
    ('NAVIGATION',        0),
    ('CABIN_STORES',      0),
    ('STATIONERY',        0),
    ('OTHER',             0)
ON CONFLICT (category) DO NOTHING;