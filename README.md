# TutorSystem Backend

## 📖 Tài liệu cho Frontend Developer
👉 **[Hướng dẫn chạy Backend cho team Frontend (Xem chi tiết)](./README_FE.md)**

## Changelog 14/11/2025 
- Cập nhật lại folder: Xoá `entrypoint.sh`
- Thêm `mysql.Dockerfile`
- Thêm `mysql-conf/charset.conf`
- Refractor `docker-compose.yml`

## Cài docker trên Ubuntu
Chạy từng dòng lệnh đề cài Ubuntu:
```bash

sudo apt-get update
sudo apt-get install ca-certificates curl
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc

echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "${UBUNTU_CODENAME:-$VERSION_CODENAME}") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update
# Setup docker
sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
```
## Lưu ý
> Vui lòng **drop database** cũ trước khi build để tránh xung đột như duplicate , hay table đã có sẵn từ phiên bản cũ

## Kết nối Database trong Mysql với DBeaver
-   Bước 1 1: Mở DBeaver -> New Connection --> Chọn MySQL
-   Bước 2: Thay port from 3306 sang 3307
-   Bước 3: Click vào **Driver properties**, tìm **allowPublicKeyRetrieval**, thay từ **false** sang **true** 

## Các lệnh cần nhớ

### Xem log cho container api và db
```bash
docker logs tutor_system_api
docker logs tutor_system_db
```
### Đăng nhập vào database mysql
```bash
sudo docker exec -it tutor_system_db mysql -u root -p
``` 
Pass: admin123

### Build docker
```bash
docker compose down -v && docker compose up -d --build
```

### Lưu ý với SQL
Để tiện việc xử lý, vui lòng thêm **chữ số** trước tên file nếu có cập nhật cho các file sql để docker có thể chạy tuần tự. 
Các file sql nằm trong `/docker` phải tuân theo quy tắc:
`<Số_Tên file>.sql`
Ví dụ
```bash
11_ABC.sql
```

