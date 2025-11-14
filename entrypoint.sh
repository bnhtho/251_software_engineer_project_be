#!/bin/bash
set -e
echo "Waiting for MySQL to be ready..."
# Chờ MySQL chạy và chấp nhận kết nối
until mysql -h "$MYSQL_HOST" -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" -e "SELECT 1" >/dev/null 2>&1; do
  echo "MySQL not ready yet... retrying in 2s"
  sleep 2
done
echo "MySQL is ready!"

echo "Starting Spring Boot API..."
exec java -jar /app/app.jar