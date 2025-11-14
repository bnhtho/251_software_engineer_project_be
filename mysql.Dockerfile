FROM mysql:latest
COPY mysql-conf/ /etc/mysql/conf.d/
RUN chmod 644 /etc/mysql/conf.d/*