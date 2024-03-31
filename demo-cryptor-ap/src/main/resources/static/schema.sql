CREATE TABLE IF NOT EXISTS keystore
(
    id            SERIAL PRIMARY KEY,
    data          bytea,
    created       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON COLUMN keystore.id IS 'The primary key of the keystore table, automatically generated as a serial number.';
COMMENT ON COLUMN keystore.data IS 'The binary data stored in the keystore.';
COMMENT ON COLUMN keystore.created IS 'The timestamp when the record was created.';
