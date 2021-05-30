INSERT INTO public.user_role(id, name)
VALUES (0, 'ROLE_USER');
INSERT INTO public.user_role(id, name)
VALUES (1, 'ROLE_ADMIN');
INSERT INTO public.app_user(id, birth_date, email, first_name, gender, height, last_name, password, username, role_id)
VALUES (0, '1942-09-18', 'ktos353@wp.pl', '', 'male', 111, '',
        '$2a$10$A8sd/ZDJNUY2d8ZLVYQzvOdRF1uvgp294kb8cWOwq7kjbr6LhrsGK', 'admin', 1);
ALTER SEQUENCE app_user_id_seq RESTART WITH 1;
ALTER SEQUENCE user_role_id_seq RESTART WITH 2;