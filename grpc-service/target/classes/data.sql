INSERT INTO users (id, email, name, created_at)
SELECT i,
       'user' || i || '@example.com',
       'User ' || i,
       NOW() - (i || ' days')::interval
FROM generate_series(1, 1000) AS s(i)
ON CONFLICT DO NOTHING;
