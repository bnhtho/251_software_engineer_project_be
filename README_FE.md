# 📘 Hướng dẫn chạy Backend cho team Frontend

## 📋 Checklist
- [x] Hướng dẫn cài đặt môi trường
- [x] Chạy database bằng Docker (khuyến nghị)
- [x] Chạy backend Spring Boot
- [x] Test API với Postman
- [x] Danh sách endpoint và cách xác thực
- [x] Định dạng request/response

---

## ⚙️ Bước 1: Chuẩn bị môi trường

### Yêu cầu cài đặt:
- ✅ **Java 21** (JDK) - [Download](https://www.oracle.com/java/technologies/downloads/#java21)
- ✅ **Docker Desktop** - [Download](https://www.docker.com/products/docker-desktop/)
- ✅ **Maven** (hoặc dùng wrapper có sẵn trong project)
- ✅ **Postman** - [Download](https://www.postman.com/downloads/)

### Kiểm tra đã cài đặt chưa:
```cmd
java -version
docker -v
```

**Kết quả mong đợi:**
```
java version "21.x.x"
Docker version 24.x.x
```

---

## 🐳 Bước 2: Chạy Database bằng Docker

### Step 2.1: Mở Terminal/CMD tại thư mục gốc project
```cmd
cd C:\Bach_Khoa\Nam_Bar_2025_2026\CNPM\TutorSytem
```

### Step 2.2: Chạy Docker Compose
```cmd
docker compose up -d
```

**Giải thích:**
- Lệnh này sẽ tạo container MySQL tên `tutor_system`
- **Port:** `3307` (không phải 3306 mặc định)
- **Database:** `tutor_system`
- **Username:** `root` (có thể thay đổi trong file `docker-compose.yml`)
- **Password:** `admin123` (có thể thay đổi trong file `docker-compose.yml`)
- **Timezone:** Asia/Ho_Chi_Minh

> **Lưu ý:** Nếu bạn thay đổi username/password trong `docker-compose.yml`, nhớ cập nhật lại trong file `src/main/resources/application.yml`

### Step 2.3: Kiểm tra container đã chạy chưa
```cmd
docker ps
```

**Kết quả mong đợi:** Bạn sẽ thấy container `tutor_system` với status `Up`

### Step 2.4 (Tùy chọn): Kiểm tra database bằng phần mềm quản trị CSDL
Bạn có thể sử dụng các công cụ như **DBeaver**, **MySQL Workbench**, **phpMyAdmin**, hoặc công cụ tích hợp trong IDE để kết nối:

**Thông tin kết nối:**
- Host: `localhost`
- Port: `3307` (⚠️ không phải 3306)
- Database: `tutor_system`
- Username: `root` (hoặc theo config của bạn)
- Password: `admin123` (hoặc theo config của bạn)

**Lưu ý với DBeaver:**
- Vào Driver properties → Tìm `allowPublicKeyRetrieval` → Đổi thành `true`

---

## 🚀 Bước 3: Chạy Backend Spring Boot

### ⚠️ QUAN TRỌNG: Chạy SQL Scripts trước
**Backend sẽ KHÔNG chạy được nếu thiếu bước này!**

Database cần chạy tất cả các file SQL từ **V1 đến V10** trong thư mục:
📁 **`C:\Bach_Khoa\Nam_Bar_2025_2026\CNPM\TutorSytem\docker\`**

Danh sách file theo thứ tự:
- `V1_update_hcmutSso.sql`
- `V2_create-table-status.sql`
- `V3_update_User_table.sql`
- `V4_create_department&major_table.sql`
- `V5_modify_subject_table.sql`
- `V6_update_report&feedback_table.sql`
- `V7_rename_Table_table_to_Library_table.sql`
- `V8__tutor_profile_subject_many_to_many.sql`
- `V9__create_student_session_table.sql`
- `V10_status_values.sql`

**Cách chạy:** 
1. Mở công cụ quản trị CSDL của bạn (DBeaver, MySQL Workbench, phpMyAdmin, v.v.)
2. Kết nối đến database `tutor_system` (xem Step 2.4)
3. Mở từng file SQL từ thư mục `docker/` ở trên
4. Execute theo thứ tự từ **V1 → V10** (rất quan trọng phải đúng thứ tự!)

### Step 3.1: Đảm bảo Docker MySQL đang chạy và đã chạy hết SQL scripts (V1-V10)

### Step 3.2: Chọn cách chạy backend

#### **Cách 1: Chạy bằng IntelliJ IDEA (Khuyến nghị cho dev)** 🎯

**Bước 1:** Tải và cài đặt IntelliJ IDEA
- Download: [IntelliJ IDEA Community (Free)](https://www.jetbrains.com/idea/download/)
- Hoặc dùng Ultimate nếu có license

**Bước 2:** Mở project
- File → Open → Chọn thư mục `TutorSytem`
- IntelliJ sẽ tự động nhận diện project Maven

**Bước 3:** Đợi IntelliJ tải dependencies
- Nhìn góc dưới bên phải màn hình, đợi "Indexing" và "Downloading" hoàn tất

**Bước 4:** Chạy Application
- Mở file: `src/main/java/HCMUT/TutorSytem/TutorSytemApplication.java`
- Click chuột phải vào file → **Run 'TutorSytemApplication'**
- Hoặc nhấn nút ▶️ Play màu xanh bên cạnh `public class TutorSytemApplication`

**Bước 5:** Kiểm tra log
- Backend sẽ chạy tại **http://localhost:8081**
- Xem log trong tab "Run" phía dưới IntelliJ

#### **Cách 2: Chạy bằng CMD**

**Step 3.2.1:** Mở Terminal/CMD mới tại thư mục gốc project

**Step 3.2.2:** Chạy backend bằng Maven Wrapper
```cmd
mvnw.cmd spring-boot:run
```

**Hoặc** nếu đã cài Maven:
```cmd
mvn spring-boot:run
```

### Step 3.3: Đợi backend khởi động
Backend sẽ chạy tại: **http://localhost:8081**

**Dấu hiệu backend đã sẵn sàng:**
```
Started TutorSytemApplication in X.XXX seconds (process running for X.XXX)
```

**Nếu gặp lỗi khi khởi động:**
1. Kiểm tra Docker container MySQL có đang chạy không: `docker ps`
2. **Kiểm tra đã chạy hết SQL scripts V1-V10 chưa** ⚠️
3. Xem log lỗi cụ thể trong terminal/IntelliJ

---

## 🔍 Bước 4: Test API với Postman

### Step 4.1: Mở Postman

### Step 4.2: Test endpoint đơn giản (không cần auth)
**GET** `http://localhost:8081/subjects`

**Response mong đợi:**
```json
{
  "statusCode": 200,
  "message": "Subjects retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "Toán cao cấp"
    },
    {
      "id": 2,
      "name": "Lập trình Java"
    }
  ]
}
```

### Step 4.3: Test đăng nhập (lấy token)
**POST** `http://localhost:8081/auth/login`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Response mong đợi:**
```json
{
  "statusCode": 200,
  "message": "Login successful",
  "data": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Lưu ý:** Copy token từ `data` để dùng cho các request cần xác thực

### Step 4.4: Test endpoint cần xác thực
Ví dụ với endpoint yêu cầu token:

**GET** `http://localhost:8081/tutors`

**Headers:**
```
Authorization: Bearer <paste_token_vào_đây>
Content-Type: application/json
```

---

## 📚 Bước 5: Danh sách API endpoints

### 🔓 Endpoints KHÔNG cần xác thực (Public)

#### 1. Lấy danh sách môn học
```
GET /subjects
```
**Response:** Danh sách `SubjectDTO[]`

#### 2. Lấy danh sách ngành học
```
GET /majors
```
**Response:** Danh sách `MajorDTO[]`

#### 3. Lấy ngành học theo khoa
```
GET /majors/by-department/{departmentId}
```
**Params:** `departmentId` (Long)
**Response:** Danh sách `MajorDTO[]`

#### 4. Lấy danh sách khoa
```
GET /departments
```
**Response:** Danh sách `DepartmentDTO[]`

#### 5. Lấy trạng thái session
```
GET /session-statuses
```
**Response:** Danh sách `SessionStatusDTO[]`

#### 6. Lấy trạng thái student-session
```
GET /student-session-statuses
```
**Response:** Danh sách `StudentSessionStatusDTO[]`

### 🔐 Endpoint Xác thực

#### Đăng nhập
```
POST /auth/login
```
**Body:**
```json
{
  "email": "string",
  "password": "string"
}
```
**Response:** Token trong `data`

Lưu ý: Password đã được mã hoá sẽ được lưu trong database dưới dạng mã Hash thông qua **BcCrypt** với cost là **12**.. 

Reminder: Convert password dạng chuỗi sang Hash tự động và lưu vào cột **password** của bảng **hcmut_sso**  . 

---

## 📦 Bước 6: Cấu trúc Response chung

**Tất cả API đều trả về format:**
```json
{
  "statusCode": 200,
  "message": "Success message",
  "data": <DTO_object hoặc array>
}
```

**Trong đó:**
- `statusCode`: 200 (success), 401 (unauthorized), 404 (not found), 500 (error)
- `message`: Mô tả kết quả
- `data`: Dữ liệu thực tế (DTO hoặc mảng DTO)

---

## 📝 Bước 7: DTOs chính (Data Transfer Objects)

### SubjectDTO
```typescript
{
  id: number,
  name: string
}
```

### MajorDTO
```typescript
{
  id: number,
  name: string,
  majorCode: string,
  programCode: string,
  note: string,
  departmentId: number,
  departmentName: string
}
```

### DepartmentDTO
```typescript
{
  id: number,
  name: string
}
```

### SessionStatusDTO / StudentSessionStatusDTO
```typescript
{
  id: number,  // byte trong Java
  name: string,
  description: string
}
```

### TutorDTO (tóm tắt)
```typescript
{
  id: number,
  hcmutId: string,
  firstName: string,
  lastName: string,
  profileImage: string,
  academicStatus: string,
  dob: string,  // LocalDate: "yyyy-MM-dd"
  phone: string,
  otherMethodContact: string,
  role: string,
  createdDate: string,  // Instant ISO-8601: "2023-10-01T12:34:56Z"
  updateDate: string,   // Instant
  lastLogin: string,    // Instant
  title: string,
  majorId: number,
  majorName: string,
  department: string,
  description: string,
  specializations: string[],  // Danh sách tên môn học
  rating: number,
  reviewCount: number,
  studentCount: number,
  experienceYears: number,
  isAvailable: boolean
}
```

---

## 📅 Bước 8: Định dạng ngày/giờ

### LocalDate (ví dụ: `dob`)
```
Format: yyyy-MM-dd
Example: "2000-05-15"
```

### Instant (ví dụ: `createdDate`, `updateDate`, `lastLogin`)
```
Format: ISO-8601 with timezone
Example: "2023-10-01T12:34:56Z"
```

**Parse trên FE:**
```javascript
// JavaScript
const date = new Date("2023-10-01T12:34:56Z");

// TypeScript với date-fns
import { parseISO } from 'date-fns';
const date = parseISO("2023-10-01T12:34:56Z");
```

---

## 🛠️ Bước 9: Troubleshooting (Xử lý lỗi thường gặp)

### Lỗi 1: "Cannot connect to database" hoặc "Table doesn't exist"
**Nguyên nhân:** 
- Docker MySQL chưa chạy
- **HOẶC chưa chạy SQL scripts V1-V10** ⚠️

**Giải pháp:**
```cmd
docker ps
docker compose up -d
```
- **Quan trọng:** Đảm bảo đã chạy tất cả file SQL từ V1 đến V10 trong thư mục `docker/`

### Lỗi 2: Port 8081 already in use
**Nguyên nhân:** Backend đã chạy rồi hoặc port bị chiếm
**Giải pháp:**
- Tắt backend cũ (Ctrl+C trong terminal)
- Hoặc đổi port trong `application.yml`

### Lỗi 3: 401 Unauthorized
**Nguyên nhân:** Token không hợp lệ hoặc chưa gửi header Authorization
**Giải pháp:**
- Login lại để lấy token mới
- Kiểm tra header: `Authorization: Bearer <token>`

### Lỗi 4: Docker không khởi động
**Giải pháp:**
```cmd
# Xóa container cũ
docker compose down

# Tạo lại
docker compose up -d
```

---

## ✅ Checklist hoàn thành

Sau khi làm xong các bước trên, bạn đã có thể:
- [x] Chạy được backend tại `http://localhost:8081`
- [x] Kết nối database MySQL thành công
- [x] **Đã chạy tất cả SQL scripts V1-V10** ⚠️
- [x] Test được API với Postman
- [x] Hiểu cấu trúc request/response
- [x] Biết cách xác thực với JWT token

---

## 💡 Tips cho Frontend

1. **Tạo Axios instance với baseURL:**
```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8081',
  headers: {
    'Content-Type': 'application/json'
  }
});

// Interceptor để tự động thêm token
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

2. **Xử lý response wrapper:**
```javascript
// Thay vì dùng response.data trực tiếp
// Phải dùng response.data.data (vì có BaseResponse wrapper)
const subjects = await api.get('/subjects')
  .then(res => res.data.data); // ← Lưu ý .data.data
```

3. **Lưu token sau khi login:**
```javascript
const login = async (username, password) => {
  const response = await api.post('/auth/login', { username, password });
  const token = response.data.data; // Token trong data
  localStorage.setItem('token', token);
  return token;
};
```

---

## 🆘 Cần hỗ trợ?

Nếu gặp vấn đề không có trong tài liệu này:
1. Check log backend trong terminal
2. Check log Docker: `docker logs tutor_system`
3. Liên hệ team Backend

---

**Chúc team Frontend code vui vẻ! 🚀**

