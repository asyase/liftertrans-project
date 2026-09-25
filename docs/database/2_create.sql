-- tables
-- Table: cargo
CREATE TABLE cargo (
                       id int  NOT NULL,
                       job_id int  NOT NULL,
                       description varchar(255)  NOT NULL,
                       weight_kg decimal(10,2)  NULL,
                       length_m decimal(8,2)  NULL,
                       width_m decimal(8,2)  NULL,
                       height_m decimal(8,2)  NULL,
                       quantity integer  NULL,
                       cargo_photo_url varchar(500)  NULL,
                       notes text  NULL,
                       CONSTRAINT CARGO_pk PRIMARY KEY (id)
);

-- Table: crane_capacity
CREATE TABLE crane_capacity (
                                id int  NOT NULL,
                                reach_m decimal(8,2)  NOT NULL,
                                max_weight_kg decimal(10,2)  NOT NULL,
                                vehicle_id int  NOT NULL,
                                CONSTRAINT CRANE_CAPACITY_ak_1 UNIQUE (vehicle_id, reach_m) NOT DEFERRABLE  INITIALLY IMMEDIATE,
                                CONSTRAINT CRANE_CAPACITY_reach_m_positive CHECK (( reach_m > 0 )) NOT DEFERRABLE INITIALLY IMMEDIATE,
                                CONSTRAINT CRANE_CAPACITY_max_weight_kg_positive CHECK (( max_weight_kg > 0 )) NOT DEFERRABLE INITIALLY IMMEDIATE,
                                CONSTRAINT CRANE_CAPACITY_pk PRIMARY KEY (id)
);

-- Table: customer
CREATE TABLE customer (
                          id int  NOT NULL,
                          name varchar(150)  NOT NULL,
                          company_name varchar(150)  NULL,
                          email varchar(150)  NULL,
                          phone varchar(30)  NOT NULL,
                          created_at timestamp  NULL,
                          company_registration_number varchar(20)  NULL,
                          vat_number varchar(30)  NULL,
                          invoice_email varchar(150)  NULL,
                          CONSTRAINT CUSTOMER_pk PRIMARY KEY (id)
);

-- Table: driver
CREATE TABLE driver (
                        id int  NOT NULL,
                        name varchar(150)  NOT NULL,
                        phone varchar(30)  NOT NULL,
                        email varchar(150)  NULL,
                        active boolean  NOT NULL,
                        CONSTRAINT DRIVER_pk PRIMARY KEY (id)
);

-- Table: job
CREATE TABLE job (
                     id int  NOT NULL,
                     customer_id int  NOT NULL,
                     vehicle_id int  NULL,
                     driver_id int  NULL,
                     subcontractor_id int  NULL,
                     job_type varchar(30)  NOT NULL,
                     execution_type varchar(20)  NOT NULL,
                     pickup_address varchar(255)  NULL,
                     delivery_address varchar(255)  NULL,
                     service_address varchar(255)  NULL,
                     receiver_name varchar(150)  NULL,
                     receiver_phone varchar(30)  NULL,
                     planned_start_time timestamp  NOT NULL,
                     planned_end_time timestamp  NULL,
                     actual_start_time timestamp  NULL,
                     actual_finish_time timestamp  NULL,
                     estimated_km decimal(10,2)  NULL,
                     actual_km decimal(10,2)  NULL,
                     estimated_hours decimal(6,2)  NULL,
                     actual_hours decimal(6,2)  NULL,
                     status varchar(20)  NOT NULL,
                     notes text  NULL,
                     created_at timestamp  NULL,
                     updated_at timestamp  NULL,
                     CONSTRAINT JOB_status_ck CHECK (( status IN ( 'DRAFT' , 'PLANNED' , 'IN_PROGRESS' , 'COMPLETED' , 'CANCELLED' ) )) NOT DEFERRABLE INITIALLY IMMEDIATE,
                     CONSTRAINT JOB_assignment_ck CHECK (( ( ( execution_type = 'INTERNAL' AND subcontractor_id IS NULL ) OR ( execution_type = 'SUBCONTRACTED' AND subcontractor_id IS NOT NULL AND driver_id IS NULL ) ) )) NOT DEFERRABLE INITIALLY IMMEDIATE,
                     CONSTRAINT JOB_job_type_ck CHECK (( job_type IN ( 'TRANSPORT_AND_CRANE' , 'CRANE_ONLY' ) )) NOT DEFERRABLE INITIALLY IMMEDIATE,
                     CONSTRAINT JOB_execution_type_ck CHECK (( execution_type IN ( 'INTERNAL' , 'SUBCONTRACTED' ) )) NOT DEFERRABLE INITIALLY IMMEDIATE,
                     CONSTRAINT JOB_address_ck CHECK (( ( ( job_type = 'CRANE_ONLY' AND service_address IS NOT NULL ) OR ( job_type = 'TRANSPORT_AND_CRANE' AND pickup_address IS NOT NULL AND delivery_address IS NOT NULL ) ) )) NOT DEFERRABLE INITIALLY IMMEDIATE,
                     CONSTRAINT JOB_pk PRIMARY KEY (id)
);

-- Table: job_document
CREATE TABLE job_document (
                              id int  NOT NULL,
                              job_id int  NOT NULL,
                              document_type varchar(30)  NOT NULL,
                              file_name varchar(255)  NULL,
                              file_url varchar(500)  NOT NULL,
                              uploaded_at timestamp  NULL,
                              CONSTRAINT JOB_DOCUMENT_type_ck CHECK (( ( document_type IN ( 'DELIVERY_PHOTO' , 'WAYBILL_PHOTO' , 'CARGO_PHOTO' , 'OTHER' ) ) )) NOT DEFERRABLE INITIALLY IMMEDIATE,
                              CONSTRAINT JOB_DOCUMENT_pk PRIMARY KEY (id)
);

-- Table: job_status_history
CREATE TABLE job_status_history (
                                    id int  NOT NULL,
                                    job_id int  NOT NULL,
                                    old_status varchar(20)  NULL,
                                    new_status varchar(20)  NOT NULL,
                                    changed_at timestamp  NOT NULL,
                                    changed_by varchar(150)  NULL,
                                    comment text  NULL,
                                    CONSTRAINT JOB_STATUS_HISTORY_pk PRIMARY KEY (id)
);

-- Table: role
CREATE TABLE role (
                      id int  NOT NULL,
                      name varchar(30)  NOT NULL,
                      CONSTRAINT ROLE_name_uq UNIQUE (name) NOT DEFERRABLE  INITIALLY IMMEDIATE,
                      CONSTRAINT ROLE_pk PRIMARY KEY (id)
);

-- Table: subcontractor
CREATE TABLE subcontractor (
                               id int  NOT NULL,
                               company_name varchar(150)  NOT NULL,
                               contact_name varchar(150)  NULL,
                               phone varchar(30)  NULL,
                               email varchar(150)  NULL,
                               notes text  NULL,
                               active boolean  NULL,
                               company_registration_number varchar(20)  NULL,
                               vat_number varchar(30)  NULL,
                               CONSTRAINT SUBCONTRACTOR_pk PRIMARY KEY (id)
);

-- Table: transport_document
CREATE TABLE transport_document (
                                    id int  NOT NULL,
                                    job_id int  NOT NULL,
                                    document_number varchar(100)  NULL,
                                    sender_name varchar(150)  NULL,
                                    sender_address varchar(255)  NULL,
                                    receiver_name varchar(150)  NULL,
                                    receiver_address varchar(255)  NULL,
                                    carrier_name varchar(150)  NULL,
                                    cargo_description text  NULL,
                                    cargo_weight_kg decimal(10,2)  NULL,
                                    loading_date timestamp  NULL,
                                    delivery_date timestamp  NULL,
                                    generated_file_url varchar(500)  NULL,
                                    created_at timestamp  NULL,
                                    CONSTRAINT TRANSPORT_DOCUMENT_job_id_uq UNIQUE (job_id) NOT DEFERRABLE  INITIALLY IMMEDIATE,
                                    CONSTRAINT TRANSPORT_DOCUMENT_pk PRIMARY KEY (id)
);

-- Table: user
CREATE TABLE "user" (
                        id int  NOT NULL,
                        email varchar(150)  NOT NULL,
                        password_hash varchar(255)  NOT NULL,
                        role_id int  NOT NULL,
                        driver_id int  NULL,
                        status varchar(1)  NOT NULL,
                        CONSTRAINT USER_email_uq UNIQUE (email) NOT DEFERRABLE  INITIALLY IMMEDIATE,
                        CONSTRAINT USER_pk PRIMARY KEY (id)
);

-- Table: vehicle
CREATE TABLE vehicle (
                         id int  NOT NULL,
                         registration_number varchar(20)  NOT NULL,
                         name varchar(100)  NULL,
                         max_cargo_weight_kg decimal(10,2)  NULL,
                         crane_capacity_kg decimal(10,2)  NULL,
                         crane_reach_m decimal(8,2)  NULL,
                         platform_length_m decimal(8,2)  NULL,
                         platform_width_m decimal(8,2)  NULL,
                         subcontractor_id int  NULL,
                         status varchar(20)  NOT NULL,
                         vehicle_length_m decimal(8,2)  NULL,
                         vehicle_width_m decimal(8,2)  NULL,
                         vehicle_height_m decimal(8,2)  NULL,
                         CONSTRAINT VEHICLE_registration_number_uq UNIQUE (registration_number) NOT DEFERRABLE  INITIALLY IMMEDIATE,
                         CONSTRAINT VEHICLE_status_ck CHECK (( status IN ( 'ACTIVE' , 'IN_SERVICE' , 'UNAVAILABLE' , 'INACTIVE' ) )) NOT DEFERRABLE INITIALLY IMMEDIATE,
                         CONSTRAINT VEHICLE_pk PRIMARY KEY (id)
);

-- foreign keys
-- Reference: CARGO_JOB_fk (table: cargo)
ALTER TABLE cargo ADD CONSTRAINT CARGO_JOB_fk
    FOREIGN KEY (job_id)
        REFERENCES job (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: CRANE_CAPACITY_VEHICLE_fk (table: crane_capacity)
ALTER TABLE crane_capacity ADD CONSTRAINT CRANE_CAPACITY_VEHICLE_fk
    FOREIGN KEY (vehicle_id)
        REFERENCES vehicle (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: JOB_CUSTOMER_fk (table: job)
ALTER TABLE job ADD CONSTRAINT JOB_CUSTOMER_fk
    FOREIGN KEY (customer_id)
        REFERENCES customer (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: JOB_DOCUMENT_JOB_fk (table: job_document)
ALTER TABLE job_document ADD CONSTRAINT JOB_DOCUMENT_JOB_fk
    FOREIGN KEY (job_id)
        REFERENCES job (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: JOB_DRIVER_fk (table: job)
ALTER TABLE job ADD CONSTRAINT JOB_DRIVER_fk
    FOREIGN KEY (driver_id)
        REFERENCES driver (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: JOB_STATUS_HISTORY_JOB_fk (table: job_status_history)
ALTER TABLE job_status_history ADD CONSTRAINT JOB_STATUS_HISTORY_JOB_fk
    FOREIGN KEY (job_id)
        REFERENCES job (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: JOB_SUBCONTRACTOR_fk (table: job)
ALTER TABLE job ADD CONSTRAINT JOB_SUBCONTRACTOR_fk
    FOREIGN KEY (subcontractor_id)
        REFERENCES subcontractor (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: JOB_VEHICLE_fk (table: job)
ALTER TABLE job ADD CONSTRAINT JOB_VEHICLE_fk
    FOREIGN KEY (vehicle_id)
        REFERENCES vehicle (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: TRANSPORT_DOCUMENT_JOB_fk (table: transport_document)
ALTER TABLE transport_document ADD CONSTRAINT TRANSPORT_DOCUMENT_JOB_fk
    FOREIGN KEY (job_id)
        REFERENCES job (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: USER_DRIVER_fk (table: user)
ALTER TABLE "user" ADD CONSTRAINT USER_DRIVER_fk
    FOREIGN KEY (driver_id)
        REFERENCES driver (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: USER_ROLE_fk (table: user)
ALTER TABLE "user" ADD CONSTRAINT USER_ROLE_fk
    FOREIGN KEY (role_id)
        REFERENCES role (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- Reference: VEHICLE_SUBCONTRACTOR_fk (table: vehicle)
ALTER TABLE vehicle ADD CONSTRAINT VEHICLE_SUBCONTRACTOR_fk
    FOREIGN KEY (subcontractor_id)
        REFERENCES subcontractor (id)
        NOT DEFERRABLE
            INITIALLY IMMEDIATE
;

-- End of file.

