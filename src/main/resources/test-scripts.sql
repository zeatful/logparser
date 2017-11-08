SELECT
  ip_address,
  COUNT(*) AS count
FROM
  (SELECT ip_address
   FROM server_logs
   WHERE entry_time
   BETWEEN '2017-01-01.15:00:00.000'
   AND '2017-01-01.16:00:00.000'
  ) result
GROUP BY ip_address
HAVING count > 200
ORDER BY count DESC;

SELECT *
FROM server_logs
WHERE ip_address = '192.192.102.136'
      AND entry_time
      BETWEEN '2017-01-01.15:00:00.000'
      AND '2017-01-01.16:00:00.000';