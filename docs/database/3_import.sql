SET search_path TO liftertrans_project, public;
BEGIN;

-- Clear existing demo/application data so explicit IDs do not collide.
TRUNCATE TABLE
    job_status_history,
    transport_document,
    job_document,
    cargo,
    job,
    crane_capacity,
    "user",
    subcontractor,
    driver,
    vehicle,
    customer,
    role
    CASCADE;

-- =========================================================
-- ROLES
-- =========================================================

INSERT INTO role (id, name) VALUES
                                (1, 'ADMIN'),
                                (2, 'DRIVER');


-- =========================================================
-- CUSTOMERS
-- =========================================================

INSERT INTO customer (
    id,
    name,
    company_name,
    email,
    phone,
    created_at,
    company_registration_number,
    vat_number,
    invoice_email
) VALUES
      (
          1,
          'Ants Asi',
          'Mida Vaja OÜ',
          'ants@midavaja.ee',
          '+3725559876',
          CURRENT_TIMESTAMP,
          '12345678',
          'EE123456789',
          'arved@midavaja.ee'
      ),
      (
          2,
          'Mari Mets',
          'Ehitus AS',
          'mari@ehitus.ee',
          '+3725551234',
          CURRENT_TIMESTAMP,
          '87654321',
          'EE987654321',
          'arved@ehitus.ee'
      ),
      (
          3,
          'Jaan Kuusk',
          'Logistika OÜ',
          'jaan@logistika.ee',
          '+3725554321',
          CURRENT_TIMESTAMP,
          '45612378',
          NULL,
          'arved@logistika.ee'
      );


-- =========================================================
-- VEHICLES
-- =========================================================

INSERT INTO vehicle (
    id,
    registration_number,
    name,
    max_cargo_weight_kg,
    crane_capacity_kg,
    crane_reach_m,
    platform_length_m,
    platform_width_m,
    subcontractor_id,
    status,
    vehicle_length_m,
    vehicle_width_m,
    vehicle_height_m
) VALUES
      (
          1,
          '876HGF',
          'MAN TGS 35.480',
          18000.00,
          NULL,
          NULL,
          8.50,
          2.50,
          NULL,
          'ACTIVE',
          9.60,
          2.55,
          3.90
      ),
      (
          2,
          '123YTR',
          'Volvo FM',
          15000.00,
          NULL,
          NULL,
          7.50,
          2.50,
          NULL,
          'ACTIVE',
          9.10,
          2.55,
          3.80
      ),
      (
          3,
          '555ABC',
          'Scania P',
          12000.00,
          NULL,
          NULL,
          7.00,
          2.45,
          NULL,
          'IN_SERVICE',
          8.80,
          2.50,
          3.70
      );


-- =========================================================
-- CRANE CAPACITY
-- =========================================================

INSERT INTO crane_capacity (
    id,
    reach_m,
    max_weight_kg,
    vehicle_id
) VALUES
      (1, 5.00, 8000.00, 1),
      (2, 10.00, 5000.00, 1),
      (3, 15.00, 2500.00, 1),
      (4, 18.00, 1500.00, 1),
      (5, 5.00, 6500.00, 2),
      (6, 10.00, 4000.00, 2),
      (7, 15.00, 2000.00, 2);


-- =========================================================
-- DRIVERS
-- =========================================================

INSERT INTO driver (
    id,
   name,
    phone,
    email,
    active
) VALUES
      (
          1,
          'Mart Tamm',
          '+3725551111',
          'mart.tamm@liftertrans.ee',
          TRUE
      ),
      (
          2,
          'Jaan Kask',
          '+3725552222',
          'jaan.kask@liftertrans.ee',
          TRUE
      ),
      (
          3,
          'Peeter Sild',
          '+3725553333',
          'peeter.sild@liftertrans.ee',
          FALSE
      );


-- =========================================================
-- USERS
-- =========================================================
-- BCrypt hashes:
-- admin123
-- driver123

INSERT INTO "user" (
    id,
    email,
    password_hash,
    role_id,
    driver_id
) VALUES
      (
          1,
          'admin@liftertrans.ee',
          '$2y$10$CudlGKATzPrzH0VZbQig0.2xwD4yZrEJVVckoa0NGXBNuDv3d7ZVK',
          1,
          NULL
      ),
      (
          2,
          'mart.tamm@liftertrans.ee',
          '$2y$10$uJBIDdSsHp85irrMhfcFGuZ3K3oNZhfHKSEurYUARodvirUeHHxgq',
          2,
          1
      );


-- =========================================================
-- SUBCONTRACTORS
-- =========================================================

INSERT INTO subcontractor (
    id,
    company_name,
    contact_name,
    phone,
    email,
    notes,
    active,
    company_registration_number,
    vat_number
) VALUES
    (
        1,
        'Partner Transport OÜ',
        'Karl Saar',
        '+3725557777',
        'info@partnertransport.ee',
        'Kasutada vajadusel alltöövõtjana.',
        TRUE,
        '11223344',
        'EE112233445'
    );


-- =========================================================
-- SUBCONTRACTOR VEHICLES
-- =========================================================

INSERT INTO vehicle (
    id,
    registration_number,
    name,
    max_cargo_weight_kg,
    crane_capacity_kg,
    crane_reach_m,
    platform_length_m,
    platform_width_m,
    subcontractor_id,
    status,
    vehicle_length_m,
    vehicle_width_m,
    vehicle_height_m
) VALUES
    (
        4,
        '777XYZ',
        'Mercedes-Benz Arocs',
        16000.00,
        NULL,
        NULL,
        8.00,
        2.50,
        1,
        'ACTIVE',
        9.30,
        2.55,
        3.85
    );

INSERT INTO crane_capacity (
    id,
    reach_m,
    max_weight_kg,
    vehicle_id
) VALUES
      (8, 5.00, 7000.00, 4),
      (9, 10.00, 4500.00, 4),
      (10, 15.00, 2200.00, 4);


-- =========================================================
-- JOBS
-- Current model allows CRANE_ONLY and TRANSPORT_AND_CRANE.
-- =========================================================

-- DRAFT: salvestatud, kuid veel kinnitamata
INSERT INTO job (
    id,
    customer_id,
    vehicle_id,
    driver_id,
    subcontractor_id,
    job_type,
    execution_type,
    pickup_address,
    delivery_address,
    service_address,
    receiver_name,
    receiver_phone,
    planned_start_time,
    planned_end_time,
    actual_start_time,
    actual_finish_time,
    estimated_km,
    actual_km,
    estimated_hours,
    actual_hours,
    status,
    notes,
    created_at,
    updated_at
) VALUES
      (
          1,
          1,
          NULL,
          NULL,
          NULL,
          'TRANSPORT_AND_CRANE',
          'INTERNAL',
          'Lasnamäe tee 12, Tallinn',
          'Kesklinna 4, Tallinn',
          NULL,
          'Peeter Kask',
          '+3725554444',
          CURRENT_DATE + INTERVAL '1 day 09:00',
          CURRENT_DATE + INTERVAL '1 day 13:00',
          NULL,
          NULL,
          65.00,
          NULL,
          4.00,
          NULL,
          'DRAFT',
          'Auto ja juht tuleb veel määrata.',
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),

-- PLANNED: own resource
      (
          2,
          2,
          1,
          1,
          NULL,
          'TRANSPORT_AND_CRANE',
          'INTERNAL',
          'Pärnu mnt 145, Tallinn',
          'Mustamäe tee 5, Tallinn',
          NULL,
          'Mari Mets',
          '+3725551234',
          CURRENT_DATE + INTERVAL '2 day 09:00',
          CURRENT_DATE + INTERVAL '2 day 12:00',
          NULL,
          NULL,
          42.00,
          NULL,
          3.00,
          NULL,
          'PLANNED',
          'Planeeritud töö oma ressursiga.',
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),

-- IN_PROGRESS
      (
          3,
          3,
          2,
          2,
          NULL,
          'CRANE_ONLY',
          'INTERNAL',
          NULL,
          NULL,
          'Tartu mnt 80, Tallinn',
          'Jaan Kuusk',
          '+3725554321',
          CURRENT_TIMESTAMP - INTERVAL '1 hour',
          CURRENT_TIMESTAMP + INTERVAL '2 hours',
          CURRENT_TIMESTAMP - INTERVAL '45 minutes',
          NULL,
          0.00,
          NULL,
          3.00,
          NULL,
          'IN_PROGRESS',
          'Kraanatöö on hetkel töös.',
          CURRENT_TIMESTAMP - INTERVAL '1 day',
          CURRENT_TIMESTAMP
      ),

-- COMPLETED
      (
          4,
          1,
          1,
          1,
          NULL,
          'TRANSPORT_AND_CRANE',
          'INTERNAL',
          'Peterburi tee 47, Tallinn',
          'Paldiski mnt 96, Tallinn',
          NULL,
          'Ants Asi',
          '+3725559876',
          CURRENT_TIMESTAMP - INTERVAL '2 days 5 hours',
          CURRENT_TIMESTAMP - INTERVAL '2 days 1 hour',
          CURRENT_TIMESTAMP - INTERVAL '2 days 5 hours',
          CURRENT_TIMESTAMP - INTERVAL '2 days 45 minutes',
          58.00,
          62.00,
          4.00,
          4.25,
          'COMPLETED',
          'Töö lõpetatud edukalt.',
          CURRENT_TIMESTAMP - INTERVAL '3 days',
          CURRENT_TIMESTAMP - INTERVAL '2 days 45 minutes'
      ),

-- PLANNED: subcontracted
      (
          5,
          2,
          4,
          NULL,
          1,
          'TRANSPORT_AND_CRANE',
          'SUBCONTRACTED',
          'Sõpruse pst 10, Tallinn',
          'Kadaka tee 42, Tallinn',
          NULL,
          'Mari Mets',
          '+3725551234',
          CURRENT_DATE + INTERVAL '3 day 10:00',
          CURRENT_DATE + INTERVAL '3 day 14:00',
          NULL,
          NULL,
          35.00,
          NULL,
          4.00,
          NULL,
          'PLANNED',
          'Töö teostab alltöövõtja Partner Transport OÜ oma autoga.',
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),

-- CANCELLED
      (
          6,
          3,
          NULL,
          NULL,
          NULL,
          'CRANE_ONLY',
          'INTERNAL',
          NULL,
          NULL,
          'Narva mnt 20, Tallinn',
          'Jaan Kuusk',
          '+3725554321',
          CURRENT_DATE + INTERVAL '4 day 11:00',
          CURRENT_DATE + INTERVAL '4 day 13:00',
          NULL,
          NULL,
          0.00,
          NULL,
          2.00,
          NULL,
          'CANCELLED',
          'Klient tühistas töö.',
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      );


-- =========================================================
-- CARGO
-- =========================================================

INSERT INTO cargo (
    id,
    job_id,
    description,
    weight_kg,
    length_m,
    width_m,
    height_m,
    quantity,
    cargo_photo_url,
    notes
) VALUES
      (
          1,
          1,
          'Betoonplaadid',
          4200.00,
          3.00,
          1.20,
          0.80,
          6,
          '/demo/cargo/betoonplaadid.jpg',
          NULL
      ),
      (
          2,
          2,
          'Ehitusmaterjalid',
          2500.00,
          2.50,
          1.50,
          1.20,
          4,
          '/demo/cargo/ehitusmaterjalid.jpg',
          'Hoida kuivana.'
      ),
      (
          3,
          4,
          'Metallkonstruktsioonid',
          5600.00,
          4.50,
          2.00,
          1.50,
          2,
          '/demo/cargo/metall.jpg',
          NULL
      ),
      (
          4,
          5,
          'Puitmaterjal',
          3100.00,
          5.00,
          1.80,
          1.50,
          3,
          NULL,
          NULL
      );


-- =========================================================
-- JOB DOCUMENTS
-- =========================================================

INSERT INTO job_document (
    id,
    job_id,
    document_type,
    file_name,
    file_url,
    uploaded_at
) VALUES
      (
          1,
          4,
          'DELIVERY_PHOTO',
          'delivery-job-4.jpg',
          '/demo/documents/delivery-job-4.jpg',
          CURRENT_TIMESTAMP - INTERVAL '2 days'
      ),
      (
          2,
          4,
          'WAYBILL_PHOTO',
          'waybill-job-4.jpg',
          '/demo/documents/waybill-job-4.jpg',
          CURRENT_TIMESTAMP - INTERVAL '2 days'
      ),
      (
          3,
          2,
          'CARGO_PHOTO',
          'cargo-job-2.jpg',
          '/demo/documents/cargo-job-2.jpg',
          CURRENT_TIMESTAMP
      );


-- =========================================================
-- TRANSPORT DOCUMENT
-- =========================================================

INSERT INTO transport_document (
    id,
    job_id,
    document_number,
    sender_name,
    sender_address,
    receiver_name,
    receiver_address,
    carrier_name,
    cargo_description,
    cargo_weight_kg,
    loading_date,
    delivery_date,
    generated_file_url,
    created_at
) VALUES
    (
        1,
        4,
        'ST-2026-0001',
        'Mida Vaja OÜ',
        'Peterburi tee 47, Tallinn',
        'Ants Asi',
        'Paldiski mnt 96, Tallinn',
        'LIFTERTRANS',
        'Metallkonstruktsioonid',
        5600.00,
        CURRENT_TIMESTAMP - INTERVAL '2 days 5 hours',
        CURRENT_TIMESTAMP - INTERVAL '2 days 45 minutes',
        '/demo/waybills/ST-2026-0001.pdf',
        CURRENT_TIMESTAMP - INTERVAL '2 days'
    );


-- =========================================================
-- JOB STATUS HISTORY
-- =========================================================

INSERT INTO job_status_history (
    id,
    job_id,
    old_status,
    new_status,
    changed_at,
    changed_by,
    comment
) VALUES
      (1, 1, NULL, 'DRAFT', CURRENT_TIMESTAMP, 'admin@liftertrans.ee', 'Mustand salvestatud'),
      (2, 2, NULL, 'DRAFT', CURRENT_TIMESTAMP - INTERVAL '1 day', 'admin@liftertrans.ee', 'Mustand salvestatud'),
      (3, 2, 'DRAFT', 'PLANNED', CURRENT_TIMESTAMP, 'admin@liftertrans.ee', 'Tellimus kinnitatud ja planeeritud'),
      (4, 3, 'DRAFT', 'PLANNED', CURRENT_TIMESTAMP - INTERVAL '2 hours', 'admin@liftertrans.ee', 'Tellimus kinnitatud ja planeeritud'),
      (5, 3, 'PLANNED', 'IN_PROGRESS', CURRENT_TIMESTAMP - INTERVAL '45 minutes', 'jaan.kask@liftertrans.ee', 'Töö alustatud'),
      (6, 4, 'DRAFT', 'PLANNED', CURRENT_TIMESTAMP - INTERVAL '3 days', 'admin@liftertrans.ee', 'Tellimus kinnitatud ja planeeritud'),
      (7, 4, 'PLANNED', 'IN_PROGRESS', CURRENT_TIMESTAMP - INTERVAL '2 days 5 hours', 'mart.tamm@liftertrans.ee', 'Töö alustatud'),
      (8, 4, 'IN_PROGRESS', 'COMPLETED', CURRENT_TIMESTAMP - INTERVAL '2 days 45 minutes', 'mart.tamm@liftertrans.ee', 'Töö lõpetatud'),
      (9, 5, NULL, 'DRAFT', CURRENT_TIMESTAMP - INTERVAL '1 hour', 'admin@liftertrans.ee', 'Mustand salvestatud'),
      (10, 5, 'DRAFT', 'PLANNED', CURRENT_TIMESTAMP, 'admin@liftertrans.ee', 'Tellimus kinnitatud ja alltöövõtja määratud'),
      (11, 6, 'DRAFT', 'CANCELLED', CURRENT_TIMESTAMP, 'admin@liftertrans.ee', 'Klient tühistas töö');

COMMIT;
