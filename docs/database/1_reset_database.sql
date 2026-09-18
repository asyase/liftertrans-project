DROP SCHEMA IF EXISTS liftertrans_project CASCADE;

CREATE SCHEMA liftertrans_project;

GRANT ALL ON SCHEMA liftertrans_project TO postgres;
GRANT ALL ON SCHEMA liftertrans_project TO PUBLIC;

SET search_path TO liftertrans_project;