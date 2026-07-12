DO $$
BEGIN
  IF (SELECT COUNT(*) FROM books) = 0 THEN
    INSERT INTO books (title, author, price)
    SELECT
      'Book ' || i AS title,
      'Author ' || ((i - 1) % 20 + 1) AS author,
      ROUND((9.99 + (i % 40))::numeric, 2) AS price
    FROM generate_series(1, 100) AS s(i);
  END IF;
END $$;
