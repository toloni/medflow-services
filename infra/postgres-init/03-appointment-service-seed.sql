-- appointment-service development seed (database: appointment_db)
-- Password for all users: 123456

INSERT INTO
    users (
        id,
        username,
        password_hash,
        role,
        full_name
    )
VALUES (
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1',
        'dr.house',
        '$2a$10$qktGwS79jHoYPVZHbaRG5OCzHcjJQDjQG2zwFfb71i5TB0KEYWIOC',
        'DOCTOR',
        'Dr. Gregory House'
    ),
    (
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2',
        'nurse.joy',
        '$2a$10$qktGwS79jHoYPVZHbaRG5OCzHcjJQDjQG2zwFfb71i5TB0KEYWIOC',
        'NURSE',
        'Joy Pokemon'
    ),
    (
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3',
        'john.doe',
        '$2a$10$qktGwS79jHoYPVZHbaRG5OCzHcjJQDjQG2zwFfb71i5TB0KEYWIOC',
        'PATIENT',
        'John Doe'
    )
ON CONFLICT (id) DO NOTHING;

INSERT INTO
    doctors (
        id,
        user_id,
        specialty,
        license_number
    )
VALUES (
        '22222222-2222-2222-2222-222222222222',
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1',
        'Diagnostic Medicine',
        'CRM-12345'
    )
ON CONFLICT (id) DO NOTHING;

INSERT INTO
    patients (
        id,
        user_id,
        birth_date,
        phone
    )
VALUES (
        '11111111-1111-1111-1111-111111111111',
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3',
        '1990-05-20',
        '+55 11 99999-0000'
    )
ON CONFLICT (id) DO NOTHING;