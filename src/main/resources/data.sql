INSERT INTO users (login, password, name, email, is_in_ban, is_only_friends, hide_friends)
SELECT 'nikitos',
       '$2a$10$u7JP6SAu7jqlK3SrEI4o4eQnBkdXU8lw4usGH3/f72mOS12d8jguW',
       'nikitos',
       'nikitos@mail.ru',
       false,
       false,
       false
Where not exists(select * from users where id = 1)
ON CONFLICT DO NOTHING;
INSERT INTO users (login, password, name, email, is_in_ban, is_only_friends, hide_friends)
SELECT 'n',
       '$2a$10$u7JP6SAu7jqlK3SrEI4o4eQnBkdXU8lw4usGH3/f72mOS12d8jguW',
       'n',
       'nikitos123@mail.ru',
       false,
       false,
       false
Where not exists(select * from users where id = 2)
ON CONFLICT DO NOTHING;