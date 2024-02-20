INSERT INTO pass_local.t_pass (count, pass_id, user_group_id, expired_at, started_at, pass_status) VALUES (3, 1, 1, '2022-01-01 13:10:58.000000', '2023-01-01 13:10:58.000000', 'COMPLETED');
INSERT INTO pass_local.t_pass (count, pass_id, user_group_id, expired_at, started_at, pass_status) VALUES (3, 2, 1, '2024-01-01 13:10:58.000000', '2025-01-01 13:10:58.000000', 'READY');

INSERT INTO pass_local.t_user_group (user_group_id, created_at, modified_at, user_id, user_group_name) VALUES (1, '2022-02-16 13:17:04.000000', '2022-02-16 13:17:04.000000', 1, '테스트');
INSERT INTO pass_local.t_user_group (user_group_id, created_at, modified_at, user_id, user_group_name) VALUES (1, '2024-02-16 13:17:18.000000', '2024-02-16 13:17:18.000000', 2, '테스트');
INSERT INTO pass_local.t_user_group (user_group_id, created_at, modified_at, user_id, user_group_name) VALUES (1, '2024-02-16 13:17:18.000000', '2024-02-16 13:17:18.000000', 3, '테스트');
