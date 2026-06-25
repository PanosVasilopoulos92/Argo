-- =============================================================================
-- ARGO — DEVELOPMENT SEED DATA
-- =============================================================================
-- Generated from the JPA entities under src/main. Run against the argo-dev DB.
--
-- Notes / assumptions:
--   * Every table extends BaseEntity → columns:
--       id, public_id, created_at, updated_at, created_by, updated_by, version, status
--     `status` and `gender` are mapped ORDINAL (integer): status 0 = ACTIVE,
--     gender 0 = MALE, 1 = FEMALE, 2 = OTHER.
--   * JOINED inheritance:
--       persons (root)      -> seafarers / office_employees (child share persons.id)
--       certificates (root) -> person_certificates / vessel_certifications
--     Base columns live ONLY on the root table; child tables hold id + own cols.
--   * A few enum columns are ORDINAL (integer), NOT string — see inline comments:
--       quotations.state, requisition_approvals_history.action / level,
--       *.snapshot_unit_of_measurement
--   * IDs are assigned explicitly so foreign keys are easy to follow. Identity
--     sequences are re-synced at the end so the app can keep inserting.
--   * category_sequences is already seeded by init-db.sql, so it is omitted here.
--
-- IMPORTANT: application-dev.yml uses `ddl-auto: create`, which DROPS and
-- recreates the schema on every app startup — wiping this data. Run this AFTER
-- the app has started at least once, and switch ddl-auto to `update`/`none` if
-- you want the data to survive restarts.
-- =============================================================================

BEGIN;

-- =============================================================================
-- PERSONS (root) — 12 seafarers (ids 1-12) + 4 office employees (ids 13-16)
-- gender: 0=MALE, 1=FEMALE, 2=OTHER ; status: 0=ACTIVE
-- =============================================================================
INSERT INTO persons
    (id, public_id, created_at, updated_at, created_by, updated_by, version, status,
     first_name, last_name, father_name, mother_name, nationality, birth_date, birth_place,
     gender, passport_number, passport_expiry_date, passport_issued, remarks,
     bank_name, bank_account, person_type)
VALUES
    (1,  'per-0001', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'James',   'Carter',       'Robert',  'Mary',    'GBR', '1975-03-12', 'London',      0, 'P1234567', '2030-01-01', '2020-01-01', NULL, 'HSBC',           'GB29NWBK001',  'SEAFARER'),
    (2,  'per-0002', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Mikael',  'Johansson',    'Erik',    'Anna',    'SWE', '1980-07-22', 'Gothenburg',  0, 'P2234567', '2029-06-01', '2019-06-01', NULL, 'SEB',            'SE35500000002','SEAFARER'),
    (3,  'per-0003', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Dimitris','Papadopoulos', 'Nikos',   'Elena',   'GRC', '1985-11-05', 'Piraeus',     0, 'P3234567', '2031-03-01', '2021-03-01', NULL, 'Piraeus Bank',   'GR1601100003', 'SEAFARER'),
    (4,  'per-0004', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Rajesh',  'Kumar',        'Suresh',  'Lakshmi', 'IND', '1978-02-18', 'Mumbai',      0, 'P4234567', '2028-12-01', '2018-12-01', NULL, 'SBI',            'IN0000000004', 'SEAFARER'),
    (5,  'per-0005', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Wei',     'Chen',         'Jun',     'Li',      'CHN', '1982-09-30', 'Shanghai',    0, 'P5234567', '2030-09-01', '2020-09-01', NULL, 'Bank of China',  'CN0000000005', 'SEAFARER'),
    (6,  'per-0006', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Carlos',  'Mendez',       'Juan',    'Maria',   'PHL', '1988-05-14', 'Manila',      0, 'P6234567', '2029-05-01', '2019-05-01', NULL, 'BDO',            'PH0000000006', 'SEAFARER'),
    (7,  'per-0007', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Ivan',    'Petrov',       'Sergei',  'Olga',    'RUS', '1979-12-01', 'Vladivostok', 0, 'P7234567', '2028-01-01', '2018-01-01', NULL, 'Sberbank',       'RU0000000007', 'SEAFARER'),
    (8,  'per-0008', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'John',    'Smith',        'David',   'Susan',   'USA', '1990-06-25', 'Houston',     0, 'P8234567', '2031-06-01', '2021-06-01', NULL, 'Chase',          'US0000000008', 'SEAFARER'),
    (9,  'per-0009', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Andre',   'Silva',        'Paulo',   'Ana',     'PRT', '1986-04-17', 'Lisbon',      0, 'P9234567', '2030-04-01', '2020-04-01', NULL, 'Millennium BCP', 'PT0000000009', 'SEAFARER'),
    (10, 'per-0010', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Mario',   'Rossi',        'Giuseppe','Lucia',   'ITA', '1983-08-08', 'Naples',      0, 'P1034567', '2029-08-01', '2019-08-01', NULL, 'UniCredit',      'IT0000000010', 'SEAFARER'),
    (11, 'per-0011', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Ahmed',   'Hassan',       'Omar',    'Fatima',  'EGY', '1987-10-10', 'Alexandria',  0, 'P1134567', '2030-10-01', '2020-10-01', NULL, 'NBE',            'EG0000000011', 'SEAFARER'),
    (12, 'per-0012', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Nguyen',  'Van An',       'Minh',    'Hoa',     'VNM', '1991-01-20', 'Haiphong',    0, 'P1234560', '2031-01-01', '2021-01-01', NULL, 'Vietcombank',    'VN0000000012', 'SEAFARER'),
    (13, 'per-0013', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Maria',   'Gonzalez',     'Antonio', 'Carmen',  'ESP', '1981-03-03', 'Madrid',      1, NULL, NULL, NULL, 'Procurement manager', NULL, NULL, 'OFFICE_EMPLOYEE'),
    (14, 'per-0014', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Peter',   'Schmidt',      'Hans',    'Greta',   'DEU', '1984-07-19', 'Hamburg',     0, NULL, NULL, NULL, 'Procurement clerk',   NULL, NULL, 'OFFICE_EMPLOYEE'),
    (15, 'per-0015', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Sarah',   'Johnson',      'Michael', 'Linda',   'USA', '1979-11-28', 'New York',    1, NULL, NULL, NULL, 'Procurement approver',NULL, NULL, 'OFFICE_EMPLOYEE'),
    (16, 'per-0016', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Admin',   'User',         'System',  'System',  'GBR', '1985-01-01', 'London',      0, NULL, NULL, NULL, 'System administrator',NULL, NULL, 'OFFICE_EMPLOYEE');

-- SEAFARERS (child) — rank stored as STRING
INSERT INTO seafarers (id, rank, sb_number, sb_issued, sb_expiry) VALUES
    (1,  'CAPTAIN',         'SB100001', '2020-01-01', '2030-01-01'),
    (2,  'CHIEF_OFFICER',   'SB100002', '2019-06-01', '2029-06-01'),
    (3,  'SECOND_OFFICER',  'SB100003', '2021-03-01', '2031-03-01'),
    (4,  'CHIEF_ENGINEER',  'SB100004', '2018-12-01', '2028-12-01'),
    (5,  'SECOND_ENGINEER', 'SB100005', '2020-09-01', '2030-09-01'),
    (6,  'THIRD_ENGINEER',  'SB100006', '2019-05-01', '2029-05-01'),
    (7,  'BOSUN',           'SB100007', '2018-01-01', '2028-01-01'),
    (8,  'AB_SEAMAN',       'SB100008', '2021-06-01', '2031-06-01'),
    (9,  'ELECTRICIAN',     'SB100009', '2020-04-01', '2030-04-01'),
    (10, 'COOK',            'SB100010', '2019-08-01', '2029-08-01'),
    (11, 'THIRD_OFFICER',   'SB100011', '2020-10-01', '2030-10-01'),
    (12, 'OILER',           'SB100012', '2021-01-01', '2031-01-01');

-- OFFICE EMPLOYEES (child)
INSERT INTO office_employees (id, test_field) VALUES
    (13, NULL),
    (14, NULL),
    (15, NULL),
    (16, NULL);

-- =============================================================================
-- USERS — role & level stored as STRING
-- =============================================================================
INSERT INTO users
    (id, public_id, created_at, updated_at, created_by, updated_by, version, status,
     username, email, keycloak_id, first_name, last_name, date_of_birth, role, level, person_id)
VALUES
    (1,  'usr-0001', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'admin',         'admin@argo.com',         'kc-0000000001', 'Admin',    'User',         '1985-01-01', 'ICT_ADMIN',            'LEVEL_5', 16),
    (2,  'usr-0002', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'mgonzalez',     'mgonzalez@argo.com',     'kc-0000000002', 'Maria',    'Gonzalez',     '1981-03-03', 'PROCUREMENT_MANAGER',  'LEVEL_4', 13),
    (3,  'usr-0003', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'pschmidt',      'pschmidt@argo.com',      'kc-0000000003', 'Peter',    'Schmidt',      '1984-07-19', 'PROCUREMENT_CLERK',    'LEVEL_2', 14),
    (4,  'usr-0004', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'sjohnson',      'sjohnson@argo.com',      'kc-0000000004', 'Sarah',    'Johnson',      '1979-11-28', 'PROCUREMENT_APPROVER', 'LEVEL_5', 15),
    (5,  'usr-0005', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'cmendez',       'cmendez@argo.com',       'kc-0000000005', 'Carlos',   'Mendez',       '1988-05-14', 'FOM',                  'LEVEL_3', 6),
    (6,  'usr-0006', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'jcarter',       'jcarter@argo.com',       'kc-0000000006', 'James',    'Carter',       '1975-03-12', 'USER',                 'LEVEL_1', 1),
    (7,  'usr-0007', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'mjohansson',    'mjohansson@argo.com',    'kc-0000000007', 'Mikael',   'Johansson',    '1980-07-22', 'USER',                 'LEVEL_1', 2),
    (8,  'usr-0008', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'dpapadopoulos', 'dpapadopoulos@argo.com', 'kc-0000000008', 'Dimitris', 'Papadopoulos', '1985-11-05', 'USER',                 'LEVEL_1', 3),
    (9,  'usr-0009', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'rkumar',        'rkumar@argo.com',        'kc-0000000009', 'Rajesh',   'Kumar',        '1978-02-18', 'USER',                 'LEVEL_1', 4),
    (10, 'usr-0010', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'wchen',         'wchen@argo.com',         'kc-0000000010', 'Wei',      'Chen',         '1982-09-30', 'USER',                 'LEVEL_1', 5);

-- =============================================================================
-- VESSELS — vessel_type & classification_society stored as STRING
-- =============================================================================
INSERT INTO vessels
    (id, public_id, created_at, updated_at, created_by, updated_by, version, status,
     name, imo_number, mmsi_number, call_sign, flag_state, vessel_type,
     gross_tonnage, net_tonnage, dead_weight_tonnage, year_build, builder,
     classification_society, port_of_registry)
VALUES
    (1,  'ves-0001', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'MV Atlantic Star',     'IMO9123456', '235012345', 'ZCAS1', 'PAN', 'BULK_CARRIER',  35000, 21000, 58000, 2010, 'Hyundai Heavy Industries', 'DNV',      'Panama City'),
    (2,  'ves-0002', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'MV Pacific Dawn',      'IMO9234567', '235023456', 'ZCPD2', 'LBR', 'TANKER',        82000, 49000, 115000,2012, 'Daewoo Shipbuilding',      'ABS',      'Monrovia'),
    (3,  'ves-0003', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'MV Nordic Wind',       'IMO9345678', '235034567', 'ZCNW3', 'MLT', 'CONTAINER',     65000, 39000, 72000, 2015, 'Samsung Heavy Industries', 'LR',       'Valletta'),
    (4,  'ves-0004', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'MV Aegean Trader',     'IMO9456789', '235045678', 'ZCAT4', 'GRC', 'GENERAL_CARGO', 18000, 10000, 25000, 2008, 'Hanjin Heavy Industries',  'BV',       'Piraeus'),
    (5,  'ves-0005', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'MV Orient Express',    'IMO9567890', '235056789', 'ZCOE5', 'SGP', 'CONTAINER',     91000, 55000, 98000, 2017, 'Imabari Shipbuilding',     'CLASS_NK', 'Singapore'),
    (6,  'ves-0006', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'MV Baltic Pride',      'IMO9678901', '235067890', 'ZCBP6', 'CYP', 'BULK_CARRIER',  42000, 25000, 70000, 2011, 'Tsuneishi Shipbuilding',   'BV',       'Limassol'),
    (7,  'ves-0007', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'MV Mediterranean Sun', 'IMO9789012', '235078901', 'ZCMS7', 'MLT', 'RO_RO',         28000, 16000, 12000, 2013, 'Fincantieri',              'RINA',     'Valletta'),
    (8,  'ves-0008', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'MV Arabian Pearl',     'IMO9890123', '235089012', 'ZCAP8', 'MHL', 'TANKER',        75000, 45000, 105000,2014, 'Hyundai Heavy Industries', 'ABS',      'Majuro'),
    (9,  'ves-0009', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'MV Caribbean Queen',   'IMO9901234', '235090123', 'ZCCQ9', 'PAN', 'PASSENGER',     90000, 54000, 9000,  2016, 'Meyer Werft',              'LR',       'Panama City'),
    (10, 'ves-0010', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'MV Indian Ocean',      'IMO9012345', '235001234', 'ZCIO0', 'IND', 'BULK_CARRIER',  38000, 22000, 63000, 2009, 'Cochin Shipyard',          'IRS',      'Mumbai');

-- =============================================================================
-- ASSIGNMENTS — assignment_rank & assignment_state stored as STRING
-- =============================================================================
INSERT INTO assignments
    (id, public_id, created_at, updated_at, created_by, updated_by, version, status,
     assignment_rank, sign_on_date, expected_sign_off_date, actual_signed_off_date,
     sign_on_port, sign_off_port, remarks, sign_off_remarks, assignment_state,
     vessel_id, seafarer_id)
VALUES
    (1,  'asg-0001', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'CAPTAIN',         '2024-01-10', '2024-07-10', NULL,         'Rotterdam',  NULL,        'Master on board',        NULL,                 'ACTIVE',    1, 1),
    (2,  'asg-0002', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'CHIEF_OFFICER',   '2024-02-01', '2024-08-01', NULL,         'Rotterdam',  NULL,        NULL,                     NULL,                 'ACTIVE',    1, 2),
    (3,  'asg-0003', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'CHIEF_ENGINEER',  '2024-01-15', '2024-07-15', NULL,         'Singapore',  NULL,        NULL,                     NULL,                 'ACTIVE',    2, 4),
    (4,  'asg-0004', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'SECOND_ENGINEER', '2024-03-01', '2024-09-01', NULL,         'Singapore',  NULL,        NULL,                     NULL,                 'ACTIVE',    2, 5),
    (5,  'asg-0005', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'SECOND_OFFICER',  '2023-06-01', '2023-12-01', '2023-12-05', 'Singapore',  'Hamburg',   'Completed tour',         'Disembarked on time','COMPLETED', 3, 3),
    (6,  'asg-0006', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'THIRD_ENGINEER',  '2024-04-01', '2024-10-01', NULL,         'Piraeus',    NULL,        NULL,                     NULL,                 'ACTIVE',    4, 6),
    (7,  'asg-0007', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'BOSUN',           '2024-02-15', '2024-08-15', NULL,         'Singapore',  NULL,        NULL,                     NULL,                 'ACTIVE',    5, 7),
    (8,  'asg-0008', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'AB_SEAMAN',       '2023-09-01', '2024-03-01', '2024-03-02', 'Limassol',   'Limassol',  'Completed tour',         'Signed off as planned','COMPLETED',6, 8),
    (9,  'asg-0009', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'ELECTRICIAN',     '2024-05-01', '2024-11-01', NULL,         'Valletta',   NULL,        NULL,                     NULL,                 'ACTIVE',    7, 9),
    (10, 'asg-0010', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'COOK',            '2024-01-20', '2024-07-20', NULL,         'Majuro',     NULL,        NULL,                     NULL,                 'ACTIVE',    8, 10),
    (11, 'asg-0011', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'THIRD_OFFICER',   '2024-03-10', '2024-09-10', NULL,         'Panama City',NULL,        NULL,                     NULL,                 'ACTIVE',    9, 11),
    (12, 'asg-0012', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'OILER',           '2023-11-01', '2024-05-01', NULL,         'Mumbai',     NULL,        'Cancelled before joining','Visa issues',      'CANCELLED', 10, 12);

-- =============================================================================
-- CERTIFICATES (root) — ids 1-12 person certs, 13-22 vessel certs
-- =============================================================================
INSERT INTO certificates
    (id, public_id, created_at, updated_at, created_by, updated_by, version, status,
     certificate_number, issuing_authority, issue_date, expiry_date, remarks)
VALUES
    (1,  'crt-0001', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'PCERT-001', 'UK MCA',           '2020-02-01', '2030-02-01', NULL),
    (2,  'crt-0002', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'PCERT-002', 'Swedish TA',       '2019-07-01', '2029-07-01', NULL),
    (3,  'crt-0003', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'PCERT-003', 'Hellenic CG',      '2021-04-01', '2026-04-01', NULL),
    (4,  'crt-0004', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'PCERT-004', 'DG Shipping India','2019-01-01', '2029-01-01', NULL),
    (5,  'crt-0005', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'PCERT-005', 'China MSA',        '2020-10-01', '2025-10-01', NULL),
    (6,  'crt-0006', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'PCERT-006', 'Philippine MARINA','2019-06-01', '2024-06-01', NULL),
    (7,  'crt-0007', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'PCERT-007', 'Maritime Clinic',  '2023-01-01', '2025-01-01', 'Annual medical'),
    (8,  'crt-0008', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'PCERT-008', 'US Coast Guard',   '2021-07-01', '2026-07-01', NULL),
    (9,  'crt-0009', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'PCERT-009', 'Portuguese DGRM',  '2020-05-01', '2030-05-01', NULL),
    (10, 'crt-0010', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'PCERT-010', 'Italian Coast Gd', '2019-09-01', '2024-09-01', NULL),
    (11, 'crt-0011', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'PCERT-011', 'Egyptian MMA',     '2020-11-01', '2025-11-01', NULL),
    (12, 'crt-0012', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'PCERT-012', 'Vietnam VMA',      '2021-02-01', '2026-02-01', NULL),
    (13, 'crt-0013', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'VCERT-001', 'DNV',              '2022-01-01', '2027-01-01', NULL),
    (14, 'crt-0014', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'VCERT-002', 'ABS',              '2022-03-01', '2027-03-01', NULL),
    (15, 'crt-0015', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'VCERT-003', 'Lloyd''s Register','2021-06-01', '2026-06-01', NULL),
    (16, 'crt-0016', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'VCERT-004', 'Bureau Veritas',   '2021-08-01', '2026-08-01', NULL),
    (17, 'crt-0017', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'VCERT-005', 'ClassNK',          '2023-02-01', '2028-02-01', NULL),
    (18, 'crt-0018', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'VCERT-006', 'Bureau Veritas',   '2022-05-01', '2027-05-01', NULL),
    (19, 'crt-0019', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'VCERT-007', 'RINA',             '2022-07-01', '2027-07-01', NULL),
    (20, 'crt-0020', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'VCERT-008', 'ABS',              '2021-09-01', '2026-09-01', NULL),
    (21, 'crt-0021', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'VCERT-009', 'Lloyd''s Register','2022-10-01', '2027-10-01', NULL),
    (22, 'crt-0022', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'VCERT-010', 'IRS',              '2021-11-01', '2026-11-01', NULL);

-- PERSON CERTIFICATES (child) — certificate_type stored as STRING
INSERT INTO person_certificates (id, certificate_type, person_id) VALUES
    (1,  'COC',                   1),
    (2,  'STCW_ENDORSEMENT',      2),
    (3,  'GMDSS',                 3),
    (4,  'COC',                   4),
    (5,  'ADVANCED_FIREFIGHTING', 5),
    (6,  'BST',                   6),
    (7,  'MEDICAL_FITNESS',       7),
    (8,  'SURVIVAL_CRAFT',        8),
    (9,  'COC',                   9),
    (10, 'MEDICAL_FIRST_AID',     10),
    (11, 'SHIP_SECURITY_OFFICER', 11),
    (12, 'BST',                   12);

-- VESSEL CERTIFICATES (child) — certificate_type stored as STRING
INSERT INTO vessel_certifications (id, certificate_type, vessel_id) VALUES
    (13, 'SMC',                 1),
    (14, 'ISSC',                2),
    (15, 'IOPP',                3),
    (16, 'LOAD_LINE',           4),
    (17, 'SAFETY_EQUIPMENT',    5),
    (18, 'SAFETY_RADIO',        6),
    (19, 'TONNAGE',             7),
    (20, 'CERTIFICATE_OF_CLASS',8),
    (21, 'DMLC',                9),
    (22, 'CLC',                 10);

-- =============================================================================
-- ITEMS — category & unit_of_measurement stored as STRING
-- =============================================================================
INSERT INTO items
    (id, public_id, created_at, updated_at, created_by, updated_by, version, status,
     name, description, item_code, category, unit_of_measurement, part_number, manufacturer)
VALUES
    (1,  'itm-0001', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Marine Diesel Engine Oil Filter', 'Spin-on lube oil filter for main engine', 'ENG-0001', 'ENGINE_SPARES',    'PIECE', 'FLT-2200', 'Mann+Hummel'),
    (2,  'itm-0002', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Mooring Rope 24mm',               'Polypropylene mooring rope, 220m coil',   'DCK-0001', 'DECK_STORES',      'ROLL',  'MR-24',    'Bridon'),
    (3,  'itm-0003', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Life Jacket SOLAS',               'SOLAS-approved adult life jacket',        'SAF-0001', 'SAFETY_EQUIPMENT', 'PIECE', 'LJ-100',   'Viking'),
    (4,  'itm-0004', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Hydraulic Oil ISO 68',            'Anti-wear hydraulic oil, 208L drum',      'LUB-0001', 'LUBRICANTS',       'DRUM',  'HO-68',    'Shell'),
    (5,  'itm-0005', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Rust Remover Chemical',           'Acid-based rust remover, 5L',             'CHM-0001', 'CHEMICALS',        'BOTTLE','RR-5',     'Drew Marine'),
    (6,  'itm-0006', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Canned Beef 400g',                'Corned beef, case of 24 cans',            'PRV-0001', 'PROVISIONS',       'BOX',   'CB-400',   'Maling'),
    (7,  'itm-0007', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'First Aid Kit',                   'SOLAS medical first aid kit',             'MED-0001', 'MEDICAL',          'SET',   'FAK-1',    'Cederroth'),
    (8,  'itm-0008', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'LED Navigation Bulb',             '24V LED navigation light bulb',           'ELC-0001', 'ELECTRICAL',       'PIECE', 'NB-24V',   'Philips'),
    (9,  'itm-0009', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Radar Magnetron',                 'Replacement magnetron for X-band radar',  'NAV-0001', 'NAVIGATION',       'PIECE', 'MG-5000',  'Furuno'),
    (10, 'itm-0010', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Bed Sheets Cotton',               'Cotton bed sheet set for cabins',         'CBN-0001', 'CABIN_STORES',     'SET',   'BS-200',   'Generic'),
    (11, 'itm-0011', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Printer Paper A4',                'A4 copy paper, 500 sheets per ream',      'STN-0001', 'STATIONERY',       'PACK',  'PA4-500',  'Generic'),
    (12, 'itm-0012', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Generic Spare Part',              'Uncategorised spare part',                'OTH-0001', 'OTHER',            'PIECE', 'GSP-1',    'Generic');

-- =============================================================================
-- SUPPLIERS
-- =============================================================================
INSERT INTO suppliers
    (id, public_id, created_at, updated_at, created_by, updated_by, version, status,
     company_name, contact_person, email, phone, address, vat_number)
VALUES
    (1,  'sup-0001', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Maritime Supplies International', 'John Doe',        'sales@maritimesupplies.com', '+30 210 1234567', '15 Akti Miaouli, Piraeus, Greece', 'EL123456001'),
    (2,  'sup-0002', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'OceanTech Marine Ltd',           'Emma Wilson',     'sales@oceantech.com',        '+44 20 79460001', '10 Dock Road, Liverpool, UK',      'GB123456002'),
    (3,  'sup-0003', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Nordic Ship Chandlers AS',       'Lars Olsen',      'post@nordicchandlers.no',    '+47 22 334455',   '5 Havnegata, Oslo, Norway',        'NO123456003'),
    (4,  'sup-0004', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Asia Marine Trading Pte',        'Lim Wei Long',    'enquiry@asiamarine.sg',      '+65 6123 4567',   '8 Maritime Square, Singapore',     'SG123456004'),
    (5,  'sup-0005', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Gulf Marine Equipment LLC',      'Ahmed Al Maktoum','sales@gulfmarine.ae',        '+971 4 1234567',  '20 Port Rashid, Dubai, UAE',       'AE123456005'),
    (6,  'sup-0006', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Atlantic Provisions Co',         'Maria Santos',    'orders@atlanticprov.pt',     '+351 21 1234567', '3 Cais do Sodre, Lisbon, Portugal','PT123456006'),
    (7,  'sup-0007', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Adriatic Ship Services Srl',     'Giulia Bianchi',  'info@adriaticship.it',       '+39 010 1234567', '12 Via del Porto, Genoa, Italy',   'IT123456007'),
    (8,  'sup-0008', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Hanseatic Marine GmbH',          'Klaus Weber',     'vertrieb@hanseaticmarine.de','+49 40 12345678', '7 Hafenstrasse, Hamburg, Germany', 'DE123456008'),
    (9,  'sup-0009', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Pacific Coast Supply Inc',       'Robert Brown',    'sales@pacificcoast.com',     '+1 713 1234567',  '99 Harbor Blvd, Houston, USA',     'US123456009'),
    (10, 'sup-0010', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Indus Marine Pvt Ltd',           'Priya Sharma',    'sales@indusmarine.in',       '+91 22 12345678', '4 Ballard Estate, Mumbai, India',  'IN123456010');

-- =============================================================================
-- REQUISITIONS — type/state/priority stored as STRING
-- =============================================================================
INSERT INTO requisitions
    (id, public_id, created_at, updated_at, created_by, updated_by, version, status,
     requisition_number, requisition_type, state, priority, remarks, required_by_date,
     submitted_at, submitted_by, approved_at, approved_by, approval_remarks,
     rejected_at, rejected_by, rejected_reason, cancelled_at, cancelled_by,
     vessel_id, person_id)
VALUES
    (1,  'req-0001', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'REQ-2024-0001', 'VESSEL', 'FINALIZED', 'HIGH',   'Engine room consumables', '2024-04-01', '2024-02-20 09:00:00', 'jcarter',  '2024-02-25 14:00:00', 'sjohnson', 'Approved', NULL, NULL, NULL, NULL, NULL, 1,    1),
    (2,  'req-0002', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'REQ-2024-0002', 'VESSEL', 'FINALIZED', 'NORMAL', 'Lubricants and chemicals','2024-04-15', '2024-02-22 09:00:00', 'rkumar',   '2024-02-27 14:00:00', 'sjohnson', 'Approved', NULL, NULL, NULL, NULL, NULL, 2,    4),
    (3,  'req-0003', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'REQ-2024-0003', 'VESSEL', 'FINALIZED', 'NORMAL', 'Safety equipment renewal','2024-05-01', '2024-03-01 09:00:00', 'dpapadopoulos','2024-03-06 14:00:00','sjohnson','Approved', NULL, NULL, NULL, NULL, NULL, 3, 3),
    (4,  'req-0004', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'REQ-2024-0004', 'OFFICE', 'FINALIZED', 'NORMAL', 'Office stationery and kits','2024-04-10','2024-03-05 09:00:00','pschmidt','2024-03-08 14:00:00','sjohnson','Approved', NULL, NULL, NULL, NULL, NULL, NULL, 13),
    (5,  'req-0005', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'REQ-2024-0005', 'VESSEL', 'FINALIZED', 'URGENT', 'Radar magnetron replacement','2024-03-25','2024-03-10 09:00:00','mjohansson','2024-03-12 14:00:00','sjohnson','Urgent - approved', NULL, NULL, NULL, NULL, NULL, 5, 7),
    (6,  'req-0006', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'REQ-2024-0006', 'VESSEL', 'FINALIZED', 'NORMAL', 'Provisions restock',      '2024-05-10', '2024-03-15 09:00:00', 'wchen',    '2024-03-18 14:00:00', 'sjohnson', 'Approved', NULL, NULL, NULL, NULL, NULL, 6,    8),
    (7,  'req-0007', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'REQ-2024-0007', 'VESSEL', 'FINALIZED', 'LOW',    'Navigation light spares', '2024-06-01', '2024-03-20 09:00:00', 'jcarter',  '2024-03-24 14:00:00', 'sjohnson', 'Approved', NULL, NULL, NULL, NULL, NULL, 7,    9),
    (8,  'req-0008', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'REQ-2024-0008', 'VESSEL', 'FINALIZED', 'NORMAL', 'Cabin stores and spares', '2024-05-20', '2024-03-22 09:00:00', 'mjohansson','2024-03-26 14:00:00','sjohnson', 'Approved', NULL, NULL, NULL, NULL, NULL, 8,    10),
    (9,  'req-0009', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'REQ-2024-0009', 'VESSEL', 'SUBMITTED', 'NORMAL', 'Awaiting approval',       '2024-06-15', '2024-04-01 09:00:00', 'dpapadopoulos', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 9,    11),
    (10, 'req-0010', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'REQ-2024-0010', 'VESSEL', 'DRAFT',     'LOW',    'Draft not yet submitted', '2024-07-01', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 10,   12),
    (11, 'req-0011', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'REQ-2024-0011', 'OFFICE', 'APPROVED',  'NORMAL', 'In approval saga',        '2024-06-20', '2024-04-05 09:00:00', 'pschmidt', '2024-04-08 14:00:00', 'mgonzalez','Pending final', NULL, NULL, NULL, NULL, NULL, NULL, 14),
    (12, 'req-0012', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'REQ-2024-0012', 'VESSEL', 'REJECTED',  'NORMAL', 'Rejected requisition',    '2024-06-25', '2024-04-06 09:00:00', 'mjohansson', NULL, NULL, NULL, '2024-04-09 14:00:00', 'sjohnson', 'Budget exceeded', NULL, NULL, 1, 2);

-- =============================================================================
-- REQUISITION LINES
--   unit_of_measurement        -> STRING
--   snapshot_category          -> STRING
--   snapshot_unit_of_measurement -> ORDINAL integer
--     PIECE=0, SET=1, BOX=2, PACK=3, DRUM=13, BOTTLE=14, ROLL=16
-- =============================================================================
INSERT INTO requisition_lines
    (id, public_id, created_at, updated_at, created_by, updated_by, version, status,
     quantity, unit_of_measurement, remarks, snapshot_item_code, snapshot_item_name,
     snapshot_category, snapshot_manufacturer, snapshot_unit_of_measurement,
     item_id, requisition_id)
VALUES
    (1,  'rql-0001', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 10,  'PIECE', NULL, 'ENG-0001', 'Marine Diesel Engine Oil Filter', 'ENGINE_SPARES',    'Mann+Hummel', 0,  1,  1),
    (2,  'rql-0002', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 5,   'ROLL',  NULL, 'DCK-0001', 'Mooring Rope 24mm',               'DECK_STORES',      'Bridon',      16, 2,  1),
    (3,  'rql-0003', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 4,   'DRUM',  NULL, 'LUB-0001', 'Hydraulic Oil ISO 68',            'LUBRICANTS',       'Shell',       13, 4,  2),
    (4,  'rql-0004', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 12,  'BOTTLE',NULL, 'CHM-0001', 'Rust Remover Chemical',           'CHEMICALS',        'Drew Marine', 14, 5,  2),
    (5,  'rql-0005', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 20,  'PIECE', NULL, 'SAF-0001', 'Life Jacket SOLAS',               'SAFETY_EQUIPMENT', 'Viking',      0,  3,  3),
    (6,  'rql-0006', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 50,  'PACK',  NULL, 'STN-0001', 'Printer Paper A4',                'STATIONERY',       'Generic',     3,  11, 4),
    (7,  'rql-0007', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 3,   'SET',   NULL, 'MED-0001', 'First Aid Kit',                   'MEDICAL',          'Cederroth',   1,  7,  4),
    (8,  'rql-0008', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 2,   'PIECE', NULL, 'NAV-0001', 'Radar Magnetron',                 'NAVIGATION',       'Furuno',      0,  9,  5),
    (9,  'rql-0009', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 100, 'BOX',   NULL, 'PRV-0001', 'Canned Beef 400g',                'PROVISIONS',       'Maling',      2,  6,  6),
    (10, 'rql-0010', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 30,  'PIECE', NULL, 'ELC-0001', 'LED Navigation Bulb',             'ELECTRICAL',       'Philips',     0,  8,  7),
    (11, 'rql-0011', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 40,  'SET',   NULL, 'CBN-0001', 'Bed Sheets Cotton',               'CABIN_STORES',     'Generic',     1,  10, 8),
    (12, 'rql-0012', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 15,  'PIECE', NULL, 'OTH-0001', 'Generic Spare Part',              'OTHER',            'Generic',     0,  12, 8);

-- =============================================================================
-- REQUISITION APPROVALS HISTORY
--   action (RequisitionStateEnum, ORDINAL): DRAFT=0, SUBMITTED=1, APPROVED=2,
--       FINALIZED=3, REJECTED=4, CANCELLED=5, FULFILLED=6
--   approver_level_at_action (UserLevelEnum, ORDINAL): LEVEL_1=0 ... LEVEL_5=4
-- =============================================================================
INSERT INTO requisition_approvals_history
    (id, public_id, created_at, updated_at, created_by, updated_by, version, status,
     requisition_id, approver_username, action, approver_level_at_action, remarks)
VALUES
    (1,  'rah-0001', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 1,  'pschmidt',  1, 1, 'Submitted to approval queue'),
    (2,  'rah-0002', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 1,  'sjohnson',  2, 4, 'Approved at level 5'),
    (3,  'rah-0003', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 1,  'sjohnson',  3, 4, 'Finalized'),
    (4,  'rah-0004', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 2,  'pschmidt',  1, 1, 'Submitted'),
    (5,  'rah-0005', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 2,  'sjohnson',  3, 4, 'Finalized'),
    (6,  'rah-0006', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 3,  'pschmidt',  1, 1, 'Submitted'),
    (7,  'rah-0007', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 3,  'sjohnson',  3, 4, 'Finalized'),
    (8,  'rah-0008', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 11, 'pschmidt',  1, 1, 'Submitted'),
    (9,  'rah-0009', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 11, 'mgonzalez', 2, 3, 'Approved at level 4'),
    (10, 'rah-0010', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 12, 'sjohnson',  4, 4, 'Rejected - budget exceeded');

-- =============================================================================
-- QUOTATIONS
--   currency -> STRING ; state (QuotationStateEnum, ORDINAL): RECEIVED=0, ACCEPTED=1, REJECTED=2
--   join columns default to req_line_id / supplier_id
-- =============================================================================
INSERT INTO quotations
    (id, public_id, created_at, updated_at, created_by, updated_by, version, status,
     price, currency, quoted_quantity, valid_until, state, notes,
     accepted_at, accepted_by, rejected_at, rejected_by, rejection_reason,
     req_line_id, supplier_id)
VALUES
    (1,  'quo-0001', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 12.50,   'EUR', 10,  '2024-05-01', 1, NULL, '2024-02-24 10:00:00', 'pschmidt', NULL, NULL, NULL, 1,  1),
    (2,  'quo-0002', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 85.00,   'EUR', 5,   '2024-05-01', 1, NULL, '2024-02-24 10:00:00', 'pschmidt', NULL, NULL, NULL, 2,  1),
    (3,  'quo-0003', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 220.00,  'USD', 4,   '2024-05-15', 1, NULL, '2024-02-26 10:00:00', 'pschmidt', NULL, NULL, NULL, 3,  2),
    (4,  'quo-0004', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 9.75,    'USD', 12,  '2024-05-15', 1, NULL, '2024-02-26 10:00:00', 'pschmidt', NULL, NULL, NULL, 4,  2),
    (5,  'quo-0005', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 45.00,   'EUR', 20,  '2024-06-01', 1, NULL, '2024-03-05 10:00:00', 'pschmidt', NULL, NULL, NULL, 5,  3),
    (6,  'quo-0006', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 4.20,    'EUR', 50,  '2024-06-01', 1, NULL, '2024-03-07 10:00:00', 'pschmidt', NULL, NULL, NULL, 6,  4),
    (7,  'quo-0007', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 65.00,   'EUR', 3,   '2024-06-01', 1, NULL, '2024-03-07 10:00:00', 'pschmidt', NULL, NULL, NULL, 7,  4),
    (8,  'quo-0008', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 3500.00, 'USD', 2,   '2024-04-15', 1, NULL, '2024-03-11 10:00:00', 'pschmidt', NULL, NULL, NULL, 8,  5),
    (9,  'quo-0009', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 2.10,    'EUR', 100, '2024-06-10', 1, NULL, '2024-03-17 10:00:00', 'pschmidt', NULL, NULL, NULL, 9,  6),
    (10, 'quo-0010', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 8.50,    'EUR', 30,  '2024-06-20', 1, NULL, '2024-03-23 10:00:00', 'pschmidt', NULL, NULL, NULL, 10, 7),
    (11, 'quo-0011', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 22.00,   'EUR', 40,  '2024-06-20', 1, NULL, '2024-03-25 10:00:00', 'pschmidt', NULL, NULL, NULL, 11, 8),
    (12, 'quo-0012', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 15.00,   'EUR', 15,  '2024-06-20', 1, NULL, '2024-03-25 10:00:00', 'pschmidt', NULL, NULL, NULL, 12, 8);

-- =============================================================================
-- PURCHASE ORDERS — order_type/order_state/currency stored as STRING
-- =============================================================================
INSERT INTO purchase_orders
    (id, public_id, created_at, updated_at, created_by, updated_by, version, status,
     purchase_order_number, order_type, justification_notes, order_state, currency,
     total_amount, sent_at, acknowledged_at, acknowledged_by, supplier_ack_reference,
     supplier_id, requisition_id)
VALUES
    (1,  'pol-0001', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'PO-2024-0001', 'STANDARD', NULL, 'ACKNOWLEDGED',       'EUR', 550.00,  '2024-02-26 09:00:00', '2024-02-27 11:00:00', 'John Doe',        'ACK-1001', 1, 1),
    (2,  'pol-0002', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'PO-2024-0002', 'STANDARD', NULL, 'ACKNOWLEDGED',       'USD', 997.00,  '2024-02-28 09:00:00', '2024-02-29 11:00:00', 'Emma Wilson',     'ACK-1002', 2, 2),
    (3,  'pol-0003', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'PO-2024-0003', 'STANDARD', NULL, 'SENT',               'EUR', 900.00,  '2024-03-07 09:00:00', NULL, NULL, NULL, 3, 3),
    (4,  'pol-0004', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'PO-2024-0004', 'STANDARD', NULL, 'FULLY_RECEIVED',     'EUR', 405.00,  '2024-03-09 09:00:00', '2024-03-10 11:00:00', 'Lars Olsen',      'ACK-1004', 4, 4),
    (5,  'pol-0005', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'PO-2024-0005', 'URGENT',   'Urgent radar repair', 'ACKNOWLEDGED','USD', 7000.00, '2024-03-12 09:00:00', '2024-03-12 15:00:00', 'Ahmed Al Maktoum','ACK-1005', 5, 5),
    (6,  'pol-0006', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'PO-2024-0006', 'STANDARD', NULL, 'SENT',               'EUR', 210.00,  '2024-03-18 09:00:00', NULL, NULL, NULL, 6, 6),
    (7,  'pol-0007', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'PO-2024-0007', 'STANDARD', NULL, 'ACKNOWLEDGED',       'EUR', 255.00,  '2024-03-24 09:00:00', '2024-03-25 11:00:00', 'Giulia Bianchi',  'ACK-1007', 7, 7),
    (8,  'pol-0008', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'PO-2024-0008', 'STANDARD', NULL, 'PARTIALLY_RECEIVED', 'EUR', 1105.00, '2024-03-26 09:00:00', '2024-03-27 11:00:00', 'Klaus Weber',     'ACK-1008', 8, 8);

-- =============================================================================
-- PURCHASE ORDER LINES
--   snapshot_category            -> STRING
--   snapshot_unit_of_measurement -> ORDINAL integer (same codes as above)
-- =============================================================================
INSERT INTO purchase_order_lines
    (id, public_id, created_at, updated_at, created_by, updated_by, version, status,
     quantity, unit_price, line_total, snapshot_item_code, snapshot_item_name,
     snapshot_item_description, snapshot_category, snapshot_unit_of_measurement,
     snapshot_part_number, snapshot_manufacturer, quotation_id, purchase_order_id, requisition_line_id)
VALUES
    (1,  'pll-0001', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 10,  12.50,   125.00,  'ENG-0001', 'Marine Diesel Engine Oil Filter', 'Spin-on lube oil filter',  'ENGINE_SPARES',    0,  'FLT-2200', 'Mann+Hummel', 1,  1, 1),
    (2,  'pll-0002', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 5,   85.00,   425.00,  'DCK-0001', 'Mooring Rope 24mm',               'Polypropylene mooring rope','DECK_STORES',      16, 'MR-24',    'Bridon',      2,  1, 2),
    (3,  'pll-0003', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 4,   220.00,  880.00,  'LUB-0001', 'Hydraulic Oil ISO 68',            'Anti-wear hydraulic oil',  'LUBRICANTS',       13, 'HO-68',    'Shell',       3,  2, 3),
    (4,  'pll-0004', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 12,  9.75,    117.00,  'CHM-0001', 'Rust Remover Chemical',           'Acid-based rust remover',  'CHEMICALS',        14, 'RR-5',     'Drew Marine', 4,  2, 4),
    (5,  'pll-0005', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 20,  45.00,   900.00,  'SAF-0001', 'Life Jacket SOLAS',               'SOLAS adult life jacket',  'SAFETY_EQUIPMENT', 0,  'LJ-100',   'Viking',      5,  3, 5),
    (6,  'pll-0006', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 50,  4.20,    210.00,  'STN-0001', 'Printer Paper A4',                'A4 copy paper',            'STATIONERY',       3,  'PA4-500',  'Generic',     6,  4, 6),
    (7,  'pll-0007', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 3,   65.00,   195.00,  'MED-0001', 'First Aid Kit',                   'SOLAS first aid kit',      'MEDICAL',          1,  'FAK-1',    'Cederroth',   7,  4, 7),
    (8,  'pll-0008', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 2,   3500.00, 7000.00, 'NAV-0001', 'Radar Magnetron',                 'X-band radar magnetron',   'NAVIGATION',       0,  'MG-5000',  'Furuno',      8,  5, 8),
    (9,  'pll-0009', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 100, 2.10,    210.00,  'PRV-0001', 'Canned Beef 400g',                'Corned beef cans',         'PROVISIONS',       2,  'CB-400',   'Maling',      9,  6, 9),
    (10, 'pll-0010', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 30,  8.50,    255.00,  'ELC-0001', 'LED Navigation Bulb',             '24V LED nav light bulb',   'ELECTRICAL',       0,  'NB-24V',   'Philips',     10, 7, 10),
    (11, 'pll-0011', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 40,  22.00,   880.00,  'CBN-0001', 'Bed Sheets Cotton',               'Cotton bed sheet set',     'CABIN_STORES',     1,  'BS-200',   'Generic',     11, 8, 11),
    (12, 'pll-0012', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 15,  15.00,   225.00,  'OTH-0001', 'Generic Spare Part',              'Uncategorised spare',      'OTHER',            0,  'GSP-1',    'Generic',     12, 8, 12);

-- =============================================================================
-- GOODS RECEIPTS — state stored as STRING (RECORDED / CANCELLED)
-- =============================================================================
INSERT INTO goods_receipts
    (id, public_id, created_at, updated_at, created_by, updated_by, version, status,
     goods_receipt_number, receipt_date, delivery_notes, state,
     cancelled_at, cancelled_by, cancellation_reason, purchase_order_id)
VALUES
    (1,  'grn-0001', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'GRN-2024-0001', '2024-03-01', 'Delivered alongside', 'RECORDED', NULL, NULL, NULL, 1),
    (2,  'grn-0002', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'GRN-2024-0002', '2024-03-05', 'Partial - one item damaged','RECORDED', NULL, NULL, NULL, 2),
    (3,  'grn-0003', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'GRN-2024-0003', '2024-03-10', NULL, 'RECORDED', NULL, NULL, NULL, 3),
    (4,  'grn-0004', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'GRN-2024-0004', '2024-03-12', 'Full delivery', 'RECORDED', NULL, NULL, NULL, 4),
    (5,  'grn-0005', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'GRN-2024-0005', '2024-03-15', 'Urgent item received', 'RECORDED', NULL, NULL, NULL, 5),
    (6,  'grn-0006', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'GRN-2024-0006', '2024-03-18', NULL, 'RECORDED', NULL, NULL, NULL, 6),
    (7,  'grn-0007', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'GRN-2024-0007', '2024-03-20', NULL, 'RECORDED', NULL, NULL, NULL, 7),
    (8,  'grn-0008', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'GRN-2024-0008', '2024-03-22', 'Partial receipt', 'RECORDED', NULL, NULL, NULL, 8),
    (9,  'grn-0009', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'GRN-2024-0009', '2024-03-25', 'Second delivery', 'RECORDED', NULL, NULL, NULL, 1),
    (10, 'grn-0010', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'GRN-2024-0010', '2024-03-28', 'Entered in error', 'CANCELLED', '2024-03-29 09:00:00', 'pschmidt', 'Duplicate receipt', 2);

-- =============================================================================
-- GOODS RECEIPT LINES
--   received_goods_condition -> STRING (OK / DAMAGED / WRONG_ITEM / OTHER)
--   line_flag                -> STRING (OVER_RECEIVED / WELL_RECEIVED / UNDER_RECEIVED)
-- =============================================================================
INSERT INTO goods_receipt_lines
    (id, public_id, created_at, updated_at, created_by, updated_by, version, status,
     received_quantity, received_goods_condition, line_flag, notes, receipt_id, po_line_id)
VALUES
    (1,  'grl-0001', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 10,  'OK',      'WELL_RECEIVED',  NULL, 1, 1),
    (2,  'grl-0002', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 5,   'OK',      'WELL_RECEIVED',  NULL, 1, 2),
    (3,  'grl-0003', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 4,   'OK',      'WELL_RECEIVED',  NULL, 2, 3),
    (4,  'grl-0004', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 10,  'DAMAGED', 'UNDER_RECEIVED', '2 bottles leaked in transit', 2, 4),
    (5,  'grl-0005', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 20,  'OK',      'WELL_RECEIVED',  NULL, 3, 5),
    (6,  'grl-0006', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 50,  'OK',      'WELL_RECEIVED',  NULL, 4, 6),
    (7,  'grl-0007', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 3,   'OK',      'WELL_RECEIVED',  NULL, 4, 7),
    (8,  'grl-0008', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 2,   'OK',      'WELL_RECEIVED',  NULL, 5, 8),
    (9,  'grl-0009', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 100, 'OK',      'WELL_RECEIVED',  NULL, 6, 9),
    (10, 'grl-0010', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 30,  'OK',      'WELL_RECEIVED',  NULL, 7, 10),
    (11, 'grl-0011', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 40,  'OK',      'WELL_RECEIVED',  NULL, 8, 11),
    (12, 'grl-0012', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 10,  'OK',      'UNDER_RECEIVED', 'Balance of 5 to follow', 8, 12);

-- =============================================================================
-- INVOICES — currency/state/payment_method stored as STRING
-- =============================================================================
INSERT INTO invoices
    (id, public_id, created_at, updated_at, created_by, updated_by, version, status,
     invoice_number, supplier_inv_ref, invoice_date, due_date, currency, total_amount, state,
     matched_at, matched_by, cancelled_at, cancelled_by, cancellation_reason, notes,
     approved_at, approved_by, rejected_at, rejected_by, rejection_reason,
     record_payment_at, paid_by, payment_reference, payment_date, payment_method,
     supplier_id, po_id)
VALUES
    (1,  'inv-0001', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'INV-2024-0001', 'SUP-INV-1001', '2024-03-05', '2024-04-04', 'EUR', 550.00,  'MATCHED',  '2024-03-06 10:00:00', 'pschmidt', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 1),
    (2,  'inv-0002', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'INV-2024-0002', 'SUP-INV-1002', '2024-03-08', '2024-04-07', 'USD', 997.00,  'APPROVED', '2024-03-09 10:00:00', 'pschmidt', NULL, NULL, NULL, NULL, '2024-03-10 10:00:00', 'mgonzalez', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 2, 2),
    (3,  'inv-0003', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'INV-2024-0003', 'SUP-INV-1003', '2024-03-15', '2024-04-14', 'EUR', 900.00,  'RECEIVED', NULL, NULL, NULL, NULL, NULL, 'Awaiting match', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 3, 3),
    (4,  'inv-0004', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'INV-2024-0004', 'SUP-INV-1004', '2024-03-16', '2024-04-15', 'EUR', 405.00,  'PAID',     '2024-03-17 10:00:00', 'pschmidt', NULL, NULL, NULL, NULL, '2024-03-18 10:00:00', 'mgonzalez', NULL, NULL, NULL, '2024-03-20 10:00:00', 'mgonzalez', 'PAYREF-4001', '2024-03-20', 'BANK_TRANSFER', 4, 4),
    (5,  'inv-0005', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'INV-2024-0005', 'SUP-INV-1005', '2024-03-18', '2024-04-17', 'USD', 7000.00, 'MATCHED',  '2024-03-19 10:00:00', 'pschmidt', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 5, 5),
    (6,  'inv-0006', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'INV-2024-0006', 'SUP-INV-1006', '2024-03-20', '2024-04-19', 'EUR', 230.00,  'DISPUTED', '2024-03-21 10:00:00', 'pschmidt', NULL, NULL, NULL, 'Price higher than PO', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 6, 6),
    (7,  'inv-0007', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'INV-2024-0007', 'SUP-INV-1007', '2024-03-22', '2024-04-21', 'EUR', 255.00,  'MATCHED',  '2024-03-23 10:00:00', 'pschmidt', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 7, 7),
    (8,  'inv-0008', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'INV-2024-0008', 'SUP-INV-1008', '2024-03-25', '2024-04-24', 'EUR', 1105.00, 'RECEIVED', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 8, 8),
    (9,  'inv-0009', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'INV-2024-0009', 'SUP-INV-1009', '2024-03-28', '2024-04-27', 'EUR', 550.00,  'REJECTED', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2024-03-29 10:00:00', 'mgonzalez', 'Duplicate of INV-2024-0001', NULL, NULL, NULL, NULL, NULL, 1, 1),
    (10, 'inv-0010', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'INV-2024-0010', 'SUP-INV-1010', '2024-03-30', '2024-04-29', 'EUR', 150.00,  'CANCELLED','2024-03-31 10:00:00', 'pschmidt', '2024-04-01 10:00:00', 'mgonzalez', 'Issued against wrong supplier', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 9, NULL);

-- =============================================================================
-- INVOICE LINES — match_status stored as STRING
-- =============================================================================
INSERT INTO invoice_lines
    (id, public_id, created_at, updated_at, created_by, updated_by, version, status,
     unit_price, quantity, line_total, description, match_status,
     price_variance_percent, quantity_variance_percent, invoice_id, po_line_id)
VALUES
    (1,  'ivl-0001', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 12.50,   10,  125.00,  'Engine oil filters',  'MATCHED',        0.00, 0.00, 1, 1),
    (2,  'ivl-0002', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 85.00,   5,   425.00,  'Mooring rope',        'MATCHED',        0.00, 0.00, 1, 2),
    (3,  'ivl-0003', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 220.00,  4,   880.00,  'Hydraulic oil drums', 'MATCHED',        0.00, 0.00, 2, 3),
    (4,  'ivl-0004', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 9.75,    12,  117.00,  'Rust remover',        'MATCHED',        0.00, 0.00, 2, 4),
    (5,  'ivl-0005', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 45.00,   20,  900.00,  'Life jackets',        'UNMATCHED',      NULL, NULL, 3, 5),
    (6,  'ivl-0006', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 4.20,    50,  210.00,  'Printer paper',       'MATCHED',        0.00, 0.00, 4, 6),
    (7,  'ivl-0007', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 65.00,   3,   195.00,  'First aid kits',      'MATCHED',        0.00, 0.00, 4, 7),
    (8,  'ivl-0008', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 3500.00, 2,   7000.00, 'Radar magnetron',     'MATCHED',        0.00, 0.00, 5, 8),
    (9,  'ivl-0009', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 2.30,    100, 230.00,  'Canned beef',         'PRICE_MISMATCH', 9.52, 0.00, 6, 9),
    (10, 'ivl-0010', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 8.50,    30,  255.00,  'LED nav bulbs',       'MATCHED',        0.00, 0.00, 7, 10),
    (11, 'ivl-0011', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 22.00,   40,  880.00,  'Bed sheets',          'MATCHED',        0.00, 0.00, 8, 11),
    (12, 'ivl-0012', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 15.00,   15,  225.00,  'Generic spare part',  'MATCHED',        0.00, 0.00, 8, 12);

-- =============================================================================
-- DOCUMENT CATEGORIES
-- =============================================================================
INSERT INTO doc_categories
    (id, public_id, created_at, updated_at, created_by, updated_by, version, status,
     name, description)
VALUES
    (1,  'dct-0001', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Safety Certificates',    'Vessel and crew safety certificates'),
    (2,  'dct-0002', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Vessel Documents',       'Registration and ownership documents'),
    (3,  'dct-0003', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Crew Documents',         'Seafarer contracts and records'),
    (4,  'dct-0004', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Procurement Contracts',  'Supplier contracts and agreements'),
    (5,  'dct-0005', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Invoices Archive',       'Archived supplier invoices'),
    (6,  'dct-0006', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Inspection Reports',     'Port state and class inspection reports'),
    (7,  'dct-0007', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Maintenance Records',    'Planned maintenance documentation'),
    (8,  'dct-0008', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Insurance Documents',    'P&I and hull insurance papers'),
    (9,  'dct-0009', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Compliance & ISM',       'ISM Code and compliance manuals'),
    (10, 'dct-0010', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'General Correspondence', 'Miscellaneous correspondence');

-- =============================================================================
-- DOCUMENT FILES
-- =============================================================================
INSERT INTO document_files
    (id, public_id, created_at, updated_at, created_by, updated_by, version, status,
     name, description, content_type, storage_key, original_filename, file_size, doc_category_id)
VALUES
    (1,  'dcf-0001', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'SMC Atlantic Star',   'Safety Management Certificate',  'application/pdf', 'doc-key-0001', 'smc_atlantic_star.pdf',     204800, 1),
    (2,  'dcf-0002', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Registry Pacific Dawn','Certificate of registry',       'application/pdf', 'doc-key-0002', 'registry_pacific_dawn.pdf', 153600, 2),
    (3,  'dcf-0003', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Contract Carter',     'Seafarer employment contract',   'application/pdf', 'doc-key-0003', 'contract_carter.pdf',       102400, 3),
    (4,  'dcf-0004', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Supplier Agreement 1','Master supply agreement',        'application/pdf', 'doc-key-0004', 'supply_agreement_msi.pdf',  256000, 4),
    (5,  'dcf-0005', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Invoice 2024-0001',   'Archived invoice copy',          'application/pdf', 'doc-key-0005', 'inv_2024_0001.pdf',         51200,  5),
    (6,  'dcf-0006', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'PSC Report Nordic',   'Port state control inspection',  'application/pdf', 'doc-key-0006', 'psc_nordic_wind.pdf',       307200, 6),
    (7,  'dcf-0007', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'PMS Aegean Trader',   'Planned maintenance schedule',   'application/pdf', 'doc-key-0007', 'pms_aegean_trader.pdf',     409600, 7),
    (8,  'dcf-0008', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'PandI Cover 2024',    'P&I insurance certificate',      'application/pdf', 'doc-key-0008', 'pandi_cover_2024.pdf',      128000, 8),
    (9,  'dcf-0009', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'ISM Manual',          'ISM Code safety manual',         'application/pdf', 'doc-key-0009', 'ism_manual.pdf',            512000, 9),
    (10, 'dcf-0010', '2025-06-01 10:00:00', '2025-06-01 10:00:00', 'seed', 'seed', 0, 0, 'Memo Fleet 2024',     'Fleet-wide circular',            'application/pdf', 'doc-key-0010', 'memo_fleet_2024.pdf',       76800,  10);

-- =============================================================================
-- SEQUENCE HELPER TABLES (numbering generators)
-- category_sequences is already seeded by init-db.sql and is intentionally omitted.
-- =============================================================================
INSERT INTO requisition_sequences (year, last_value, final_formated_value) VALUES
    (2023, 0,  'REQ-2023-0000'),
    (2024, 12, 'REQ-2024-0012');

INSERT INTO purchase_order_sequences (id, year, last_value, final_formatted_value) VALUES
    (1, 2024, 8, 'PO-2024-0008');

INSERT INTO goods_receipt_sequence (id, year, last_value, final_formatted_value) VALUES
    (1, 2024, 10, 'GRN-2024-0010');

INSERT INTO invoice_sequence (id, year, last_value, final_formatted_value) VALUES
    (1, 2024, 10, 'INV-2024-0010');

-- =============================================================================
-- RE-SYNC IDENTITY SEQUENCES
-- We inserted explicit ids above; advance each identity sequence past MAX(id)
-- so the application can keep inserting without primary-key collisions.
-- (Child tables seafarers/office_employees/person_certificates/vessel_certifications
--  share the parent identity and have no sequence of their own.)
-- =============================================================================
SELECT setval(pg_get_serial_sequence('persons',                       'id'), (SELECT MAX(id) FROM persons));
SELECT setval(pg_get_serial_sequence('users',                         'id'), (SELECT MAX(id) FROM users));
SELECT setval(pg_get_serial_sequence('vessels',                       'id'), (SELECT MAX(id) FROM vessels));
SELECT setval(pg_get_serial_sequence('assignments',                   'id'), (SELECT MAX(id) FROM assignments));
SELECT setval(pg_get_serial_sequence('certificates',                  'id'), (SELECT MAX(id) FROM certificates));
SELECT setval(pg_get_serial_sequence('items',                         'id'), (SELECT MAX(id) FROM items));
SELECT setval(pg_get_serial_sequence('suppliers',                     'id'), (SELECT MAX(id) FROM suppliers));
SELECT setval(pg_get_serial_sequence('requisitions',                  'id'), (SELECT MAX(id) FROM requisitions));
SELECT setval(pg_get_serial_sequence('requisition_lines',             'id'), (SELECT MAX(id) FROM requisition_lines));
SELECT setval(pg_get_serial_sequence('requisition_approvals_history', 'id'), (SELECT MAX(id) FROM requisition_approvals_history));
SELECT setval(pg_get_serial_sequence('quotations',                    'id'), (SELECT MAX(id) FROM quotations));
SELECT setval(pg_get_serial_sequence('purchase_orders',               'id'), (SELECT MAX(id) FROM purchase_orders));
SELECT setval(pg_get_serial_sequence('purchase_order_lines',          'id'), (SELECT MAX(id) FROM purchase_order_lines));
SELECT setval(pg_get_serial_sequence('goods_receipts',                'id'), (SELECT MAX(id) FROM goods_receipts));
SELECT setval(pg_get_serial_sequence('goods_receipt_lines',           'id'), (SELECT MAX(id) FROM goods_receipt_lines));
SELECT setval(pg_get_serial_sequence('invoices',                      'id'), (SELECT MAX(id) FROM invoices));
SELECT setval(pg_get_serial_sequence('invoice_lines',                 'id'), (SELECT MAX(id) FROM invoice_lines));
SELECT setval(pg_get_serial_sequence('doc_categories',                'id'), (SELECT MAX(id) FROM doc_categories));
SELECT setval(pg_get_serial_sequence('document_files',                'id'), (SELECT MAX(id) FROM document_files));
SELECT setval(pg_get_serial_sequence('purchase_order_sequences',      'id'), (SELECT MAX(id) FROM purchase_order_sequences));
SELECT setval(pg_get_serial_sequence('goods_receipt_sequence',        'id'), (SELECT MAX(id) FROM goods_receipt_sequence));
SELECT setval(pg_get_serial_sequence('invoice_sequence',              'id'), (SELECT MAX(id) FROM invoice_sequence));

COMMIT;
