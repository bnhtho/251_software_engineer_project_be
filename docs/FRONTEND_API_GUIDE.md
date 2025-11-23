# Hướng Dẫn Sử Dụng API Hệ Thống Tutor - Frontend Developer Guide

## 📋 Mục Lục
1. [Giới Thiệu](#giới-thiệu)
2. [Cấu Trúc Response Chung](#cấu-trúc-response-chung)
3. [Luồng 1: Student Đăng Ký Và Tham Gia Session](#luồng-1-student-đăng-ký-và-tham-gia-session)
4. [Luồng 2: Tutor Tạo Session Và Duyệt Student](#luồng-2-tutor-tạo-session-và-duyệt-student)
5. [Luồng 3: Quản Lý Hồ Sơ Cá Nhân](#luồng-3-quản-lý-hồ-sơ-cá-nhân)
6. [Luồng 4: Admin Quản Lý Session](#luồng-4-admin-quản-lý-session)
7. [Các API Hỗ Trợ Khác](#các-api-hỗ-trợ-khác)

---

## Giới Thiệu

Tài liệu này hướng dẫn Frontend Developer sử dụng các API của hệ thống Tutor HCMUT theo từng luồng nghiệp vụ thực tế, dựa trên code đã implement.

### Authentication
- Tất cả API đều yêu cầu JWT token trong header: `Authorization: Bearer {token}`
- User ID được lấy tự động từ token (không cần truyền trong request)
- Hệ thống tự động kiểm tra quyền truy cập

---

## Cấu Trúc Response Chung

Tất cả API đều trả về format chuẩn:

```json
{
  "statusCode": 200,
  "message": "Success message",
  "data": {
    // Dữ liệu thực tế
  }
}
```

**Status Codes:**
- `200`: Thành công
- `403`: Không có quyền truy cập
- `404`: Không tìm thấy dữ liệu
- `400`: Dữ liệu không hợp lệ
- `500`: Lỗi server

---

## Luồng 1: Student Đăng Ký Và Tham Gia Session

### 📖 Mô Tả Luồng

1. **Student xem danh sách session khả dụng**
2. **Student chọn và đăng ký session**
3. **Yêu cầu đăng ký chuyển sang trạng thái PENDING, chờ Tutor duyệt**
4. **Student có thể xem lịch sử đăng ký của mình**

---

### 🔹 Bước 1: Xem Danh Sách Session Khả Dụng

**Endpoint:** `GET /students/available-sessions`

**Mô tả:** Lấy danh sách các session còn chỗ trống, chưa bắt đầu và đang ở trạng thái SCHEDULED.

**Request:**
```http
GET /students/available-sessions
Authorization: Bearer {token}
```

**Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Available sessions retrieved successfully",
  "data": [
    {
      "id": 1,
      "tutorName": "Nguyễn Văn A",
      "studentNames": ["Trần B", "Lê C"],
      "subjectName": "Lập Trình C++",
      "startTime": "2025-11-25T14:00:00Z",
      "endTime": "2025-11-25T16:00:00Z",
      "format": "ONLINE",
      "location": "https://meet.google.com/xxx-yyyy-zzz",
      "maxQuantity": 50,
      "currentQuantity": 2,
      "updatedDate": "2025-11-23T10:30:00Z"
    },
    {
      "id": 2,
      "tutorName": "Phạm Thị D",
      "studentNames": [],
      "subjectName": "Cấu Trúc Dữ Liệu",
      "startTime": "2025-11-26T09:00:00Z",
      "endTime": "2025-11-26T11:00:00Z",
      "format": "OFFLINE",
      "location": "Phòng A1.101, BK TP.HCM",
      "maxQuantity": 30,
      "currentQuantity": 0,
      "updatedDate": "2025-11-23T09:00:00Z"
    }
  ]
}
```

**Cách Hiển Thị Trên UI:**
- Hiển thị **số chỗ còn lại** = `maxQuantity - currentQuantity`
- Nếu `currentQuantity >= maxQuantity`: Ẩn nút "Đăng ký" hoặc hiển thị "Đã đầy"
- Nếu `startTime < hiện tại`: Hiển thị "Đã bắt đầu" (không cho đăng ký)

---

### 🔹 Bước 2: Đăng Ký Session

**Endpoint:** `POST /students/register-session?sessionId={id}`

**Mô tả:** 
- Student đăng ký tham gia một session
- Yêu cầu sẽ ở trạng thái **PENDING** chờ Tutor duyệt
- Hệ thống tự động kiểm tra:
  - **Session phải ở trạng thái SCHEDULED** (đã được Admin duyệt) - Nếu session còn PENDING hoặc CANCELLED, không cho đăng ký
  - **Session chưa bắt đầu** (`startTime > hiện tại`)
  - **Student chưa đăng ký session này** (không cho đăng ký trùng)
  - **Không trùng lịch** (dựa trên `dayOfWeek`, `startTime`, `endTime` trong `StudentSchedule`)
- **Sau khi đăng ký thành công**:
  - Tạo bản ghi `StudentSession` với status = PENDING
  - **Tự động thêm vào `StudentSchedule`** để ngăn đăng ký session khác trùng giờ

**Request:**
```http
POST /students/register-session?sessionId=1
Authorization: Bearer {token}
```

**Request Body:** KHÔNG CẦN (studentId lấy tự động từ token)

**Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Yêu cầu đăng ký đã gửi, đang chờ tutor duyệt",
  "data": {
    "id": 101,
    "studentId": 5,
    "studentName": "Nguyễn Văn B",
    "sessionId": 1,
    "sessionSubject": "Lập Trình C++",
    "sessionStartTime": "2025-11-25T14:00:00Z",
    "sessionEndTime": "2025-11-25T16:00:00Z",
    "sessionFormat": "ONLINE",
    "sessionDayOfWeek": "MONDAY",
    "sessionLocation": "https://meet.google.com/xxx-yyyy-zzz",
    "status": "PENDING",
    "registeredDate": "2025-11-23T10:45:00Z",
    "confirmedDate": null,
    "updatedDate": "2025-11-23T10:45:00Z"
  }
}
```

**Response Error - Đã đăng ký rồi (400):**
```json
{
  "statusCode": 400,
  "message": "Student has already registered for this session",
  "data": null
}
```

**Response Error - Trùng lịch (400):**
```json
{
  "statusCode": 400,
  "message": "Schedule conflict: Student already has a session at this time",
  "data": null
}
```

**Response Error - Session không khả dụng (400):**
```json
{
  "statusCode": 400,
  "message": "Session is not available for registration",
  "data": null
}
```

**Response Error - Session đã bắt đầu (400):**
```json
{
  "statusCode": 400,
  "message": "Session has already started or passed",
  "data": null
}
```

**Hiển Thị Trên UI:**
- Sau khi đăng ký thành công, hiển thị: "✅ Đã gửi yêu cầu đăng ký, đang chờ tutor duyệt"
- Chuyển student đến trang "Lịch học của tôi" để xem trạng thái

---

### 🔹 Bước 3: Xem Lịch Sử Đăng Ký

**Endpoint:** `GET /students/history/{userId}`

**Mô tả:** Student xem tất cả các session đã đăng ký (bao gồm PENDING, CONFIRMED, REJECTED).

**Request:**
```http
GET /students/history/5
Authorization: Bearer {token}
```

**Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Student session history retrieved successfully",
  "data": [
    {
      "studentSessionId": 101,
      "sessionId": 1,
      "tutorName": "Nguyễn Văn A",
      "subjectName": "Lập Trình C++",
      "startTime": "2025-11-25T14:00:00Z",
      "endTime": "2025-11-25T16:00:00Z",
      "format": "ONLINE",
      "location": "https://meet.google.com/xxx-yyyy-zzz",
      "dayOfWeek": "MONDAY",
      "sessionStatus": null,
      "registrationStatus": "PENDING",
      "registeredDate": "2025-11-23T10:45:00Z",
      "updatedDate": "2025-11-23T10:45:00Z"
    },
    {
      "studentSessionId": 98,
      "sessionId": 15,
      "tutorName": "Phạm Thị D",
      "subjectName": "Cơ Sở Dữ Liệu",
      "startTime": "2025-11-20T09:00:00Z",
      "endTime": "2025-11-20T11:00:00Z",
      "format": "OFFLINE",
      "location": "Phòng B2.201",
      "dayOfWeek": "WEDNESDAY",
      "sessionStatus": null,
      "registrationStatus": "CONFIRMED",
      "registeredDate": "2025-11-15T08:00:00Z",
      "updatedDate": "2025-11-16T14:30:00Z"
    },
    {
      "studentSessionId": 85,
      "sessionId": 10,
      "tutorName": "Lê Văn E",
      "subjectName": "Toán Rời Rạc",
      "startTime": "2025-11-18T14:00:00Z",
      "endTime": "2025-11-18T16:00:00Z",
      "format": "ONLINE",
      "location": "https://meet.google.com/aaa-bbbb-ccc",
      "dayOfWeek": "TUESDAY",
      "sessionStatus": null,
      "registrationStatus": "REJECTED",
      "registeredDate": "2025-11-10T12:00:00Z",
      "updatedDate": "2025-11-11T09:20:00Z"
    }
  ]
}
```

**Hiển Thị Trên UI:**

Phân loại theo `registrationStatus`:
- **PENDING**: 
  - Badge màu vàng: "⏳ Đang chờ duyệt"
  - Không hiển thị link tham gia
  
- **CONFIRMED**: 
  - Badge màu xanh: "✅ Đã được duyệt"
  - Nếu `startTime > hiện tại`: Hiển thị nút "Tham gia buổi học" (với link trong `location`)
  - Nếu `startTime < hiện tại`: Hiển thị "Đã diễn ra"

- **REJECTED**: 
  - Badge màu đỏ: "❌ Bị từ chối"
  - Có thể hiển thị tooltip: "Buổi học đã đầy hoặc không phù hợp"

---

### 🔹 Bước 4: Xem Hồ Sơ Cá Nhân

**Endpoint:** `GET /students/profile/{userId}`

**Request:**
```http
GET /students/profile/5
Authorization: Bearer {token}
```

**Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Student profile retrieved successfully",
  "data": {
    "id": 5,
    "hcmutId": "2012345",
    "firstName": "Nguyễn Văn",
    "lastName": "B",
    "profileImage": "https://example.com/avatar.jpg",
    "academicStatus": "Sinh viên năm 3",
    "dob": "2003-05-15T00:00:00Z",
    "phone": "0901234567",
    "otherMethodContact": "facebook.com/nguyenvanb",
    "role": "STUDENT",
    "createdDate": "2024-09-01T00:00:00Z",
    "updateDate": "2025-11-20T10:30:00Z",
    "lastLogin": "2025-11-23T09:00:00Z",
    "majorId": 3,
    "majorName": "Khoa Học Máy Tính",
    "department": "Khoa Khoa Học và Kỹ Thuật Máy Tính"
  }
}
```

---

## Luồng 2: Tutor Tạo Session Và Duyệt Student

### 📖 Mô Tả Luồng

1. **Tutor tạo session mới**
2. **Tutor xem danh sách yêu cầu đăng ký đang PENDING**
3. **Tutor duyệt (approve) hoặc từ chối (reject) từng yêu cầu**
4. **Tutor có thể duyệt hàng loạt nhiều yêu cầu cùng lúc**

---

### 🔹 Bước 1: Tutor Tạo Session Mới

**Endpoint:** `POST /sessions`

**Mô tả:** 
- Tutor tạo buổi học mới
- **Session mới sẽ ở trạng thái PENDING (ID = 1), cần Admin duyệt trước khi student có thể đăng ký**
- Hệ thống tự động:
  - Set `currentQuantity = 0`
  - Set `status = PENDING` (ID = 1) - Chờ Admin duyệt
  - Validate: `startTime < endTime`, `startTime > hiện tại`

**Request:**
```http
POST /sessions
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "tutorId": 3,
  "subjectId": 5,
  "startTime": "2025-11-28T14:00:00Z",
  "endTime": "2025-11-28T16:00:00Z",
  "format": "ONLINE",
  "location": "https://meet.google.com/new-link",
  "maxQuantity": 40
}
```

**Lưu ý:** 
- Nếu không truyền `maxQuantity`, hệ thống tự động set = 50

**Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Session created successfully",
  "data": {
    "id": 25,
    "tutorName": "Nguyễn Văn A",
    "studentNames": [],
    "subjectName": "Lập Trình Java",
    "startTime": "2025-11-28T14:00:00Z",
    "endTime": "2025-11-28T16:00:00Z",
    "format": "ONLINE",
    "location": "https://meet.google.com/new-link",
    "maxQuantity": 40,
    "currentQuantity": 0,
    "updatedDate": "2025-11-23T11:00:00Z"
  }
}
```

**Response Error - Thời gian không hợp lệ (400):**
```json
{
  "statusCode": 400,
  "message": "Start time must be before end time",
  "data": null
}
```

**Response Error - Thời gian trong quá khứ (400):**
```json
{
  "statusCode": 400,
  "message": "Session start time must be in the future",
  "data": null
}
```

---

### 🔹 Bước 2: Xem Danh Sách Yêu Cầu Đăng Ký PENDING

**Endpoint:** `GET /tutors/pending-registrations`

**Mô tả:** Lấy danh sách tất cả yêu cầu đăng ký đang chờ duyệt của các session mà tutor sở hữu.

**Request:**
```http
GET /tutors/pending-registrations
Authorization: Bearer {token}
```

**Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Pending student sessions retrieved successfully",
  "data": [
    {
      "id": 105,
      "studentId": 8,
      "studentName": "Trần Thị C",
      "sessionId": 25,
      "sessionSubject": "Lập Trình Java",
      "sessionStartTime": "2025-11-28T14:00:00Z",
      "sessionEndTime": "2025-11-28T16:00:00Z",
      "sessionFormat": "ONLINE",
      "sessionDayOfWeek": "THURSDAY",
      "sessionLocation": "https://meet.google.com/new-link",
      "status": "PENDING",
      "registeredDate": "2025-11-23T11:15:00Z",
      "confirmedDate": null,
      "updatedDate": "2025-11-23T11:15:00Z"
    },
    {
      "id": 106,
      "studentId": 12,
      "studentName": "Lê Văn D",
      "sessionId": 25,
      "sessionSubject": "Lập Trình Java",
      "sessionStartTime": "2025-11-28T14:00:00Z",
      "sessionEndTime": "2025-11-28T16:00:00Z",
      "sessionFormat": "ONLINE",
      "sessionDayOfWeek": "THURSDAY",
      "sessionLocation": "https://meet.google.com/new-link",
      "status": "PENDING",
      "registeredDate": "2025-11-23T11:20:00Z",
      "confirmedDate": null,
      "updatedDate": "2025-11-23T11:20:00Z"
    }
  ]
}
```

**Hiển Thị Trên UI:**
- Nhóm theo `sessionId` để dễ quản lý
- Hiển thị thông tin student: tên, MSSV (nếu có), thời gian đăng ký
- Cung cấp 2 nút: "✅ Duyệt" và "❌ Từ chối" cho mỗi yêu cầu
- Có thể có checkbox để chọn nhiều yêu cầu và nút "Duyệt hàng loạt"

---

### 🔹 Bước 3A: Duyệt Một Yêu Cầu (Approve)

**Endpoint:** `PUT /tutors/student-sessions/{studentSessionId}/approve`

**Mô tả:**
- Tutor duyệt một yêu cầu đăng ký
- Hệ thống tự động:
  - Kiểm tra session còn chỗ không (`currentQuantity < maxQuantity`)
  - Nếu còn chỗ: Chuyển status → `CONFIRMED`, tăng `currentQuantity`, set `confirmedDate`
  - Nếu đã đầy: Trả lỗi
  - Nếu là người cuối cùng được approve (session đầy): Tự động reject các yêu cầu PENDING còn lại

**Request:**
```http
PUT /tutors/student-sessions/105/approve
Authorization: Bearer {token}
```

**Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Đã duyệt đăng ký cho sinh viên",
  "data": {
    "id": 105,
    "studentId": 8,
    "studentName": "Trần Thị C",
    "sessionId": 25,
    "sessionSubject": "Lập Trình Java",
    "sessionStartTime": "2025-11-28T14:00:00Z",
    "sessionEndTime": "2025-11-28T16:00:00Z",
    "sessionFormat": "ONLINE",
    "sessionDayOfWeek": "THURSDAY",
    "sessionLocation": "https://meet.google.com/new-link",
    "status": "CONFIRMED",
    "registeredDate": "2025-11-23T11:15:00Z",
    "confirmedDate": "2025-11-23T12:00:00Z",
    "updatedDate": "2025-11-23T12:00:00Z"
  }
}
```

**Response Error - Session đã đầy (400):**
```json
{
  "statusCode": 400,
  "message": "Buổi học đã đủ số lượng, không thể duyệt thêm",
  "data": null
}
```

**Response Error - Không phải tutor sở hữu (403):**
```json
{
  "statusCode": 403,
  "message": "Bạn không có quyền duyệt yêu cầu này (session không thuộc về bạn)",
  "data": null
}
```

**Response Error - Không phải trạng thái PENDING (400):**
```json
{
  "statusCode": 400,
  "message": "Yêu cầu đăng ký không ở trạng thái chờ duyệt. Trạng thái hiện tại: CONFIRMED",
  "data": null
}
```

---

### 🔹 Bước 3B: Từ Chối Yêu Cầu (Reject)

**Endpoint:** `PUT /tutors/student-sessions/{studentSessionId}/reject`

**Mô tả:**
- Tutor từ chối một yêu cầu đăng ký
- Hệ thống tự động:
  - Chuyển status → `REJECTED`
  - KHÔNG tăng `currentQuantity`
  - Xóa lịch đã thêm vào `StudentSchedule` (nếu có)

**Request:**
```http
PUT /tutors/student-sessions/106/reject
Authorization: Bearer {token}
```

**Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Đã từ chối yêu cầu đăng ký",
  "data": {
    "id": 106,
    "studentId": 12,
    "studentName": "Lê Văn D",
    "sessionId": 25,
    "sessionSubject": "Lập Trình Java",
    "sessionStartTime": "2025-11-28T14:00:00Z",
    "sessionEndTime": "2025-11-28T16:00:00Z",
    "sessionFormat": "ONLINE",
    "sessionDayOfWeek": "THURSDAY",
    "sessionLocation": "https://meet.google.com/new-link",
    "status": "REJECTED",
    "registeredDate": "2025-11-23T11:20:00Z",
    "confirmedDate": null,
    "updatedDate": "2025-11-23T12:05:00Z"
  }
}
```

---

### 🔹 Bước 3C: Duyệt Hàng Loạt (Batch Approve)

**Endpoint:** `PUT /tutors/student-sessions/batch-approve`

**Mô tả:**
- Tutor duyệt nhiều yêu cầu cùng lúc
- Hệ thống xử lý tuần tự từng yêu cầu
- Nếu session đầy giữa chừng, các yêu cầu còn lại sẽ tự động bị reject

**Request:**
```http
PUT /tutors/student-sessions/batch-approve
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
[105, 106, 107, 108]
```

**Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Đã xử lý các yêu cầu đăng ký",
  "data": [
    {
      "id": 105,
      "studentName": "Trần Thị C",
      "status": "CONFIRMED",
      "confirmedDate": "2025-11-23T12:10:00Z"
    },
    {
      "id": 106,
      "studentName": "Lê Văn D",
      "status": "CONFIRMED",
      "confirmedDate": "2025-11-23T12:10:00Z"
    },
    {
      "id": 107,
      "studentName": "Phạm Thị E",
      "status": "CONFIRMED",
      "confirmedDate": "2025-11-23T12:10:00Z"
    },
    {
      "id": 108,
      "studentName": "Hoàng Văn F",
      "status": "REJECTED",
      "confirmedDate": null
    }
  ]
}
```

**Lưu ý:** Trong ví dụ trên, yêu cầu 108 bị reject vì session đã đầy sau khi approve 107.

---

## Luồng 3: Quản Lý Hồ Sơ Cá Nhân

### 🔹 Student Cập Nhật Hồ Sơ

**Endpoint:** `PUT /students/profile/{userId}`

**Request:**
```http
PUT /students/profile/5
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "firstName": "Nguyễn Văn",
  "lastName": "B Updated",
  "otherMethodContact": "zalo: 0901234567",
  "majorId": 5
}
```

**Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Student profile updated successfully",
  "data": {
    "id": 5,
    "hcmutId": "2012345",
    "firstName": "Nguyễn Văn",
    "lastName": "B Updated",
    "profileImage": "https://example.com/avatar.jpg",
    "academicStatus": "Sinh viên năm 3",
    "dob": "2003-05-15T00:00:00Z",
    "phone": "0901234567",
    "otherMethodContact": "zalo: 0901234567",
    "role": "STUDENT",
    "majorId": 5,
    "majorName": "Kỹ Thuật Phần Mềm",
    "department": "Khoa Khoa Học và Kỹ Thuật Máy Tính"
  }
}
```

---

### 🔹 Tutor Xem Hồ Sơ Chi Tiết

**Endpoint:** `GET /tutors/profile/{userId}`

**Request:**
```http
GET /tutors/profile/3
Authorization: Bearer {token}
```

**Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Tutor detail retrieved successfully",
  "data": {
    "id": 3,
    "hcmutId": "1912345",
    "firstName": "Nguyễn Văn",
    "lastName": "A",
    "profileImage": "https://example.com/tutor-avatar.jpg",
    "academicStatus": "Giảng viên",
    "dob": "1990-03-20T00:00:00Z",
    "phone": "0912345678",
    "otherMethodContact": "email: tutor@hcmut.edu.vn",
    "role": "TUTOR",
    "majorId": 3,
    "majorName": "Khoa Học Máy Tính",
    "department": "Khoa Khoa Học và Kỹ Thuật Máy Tính",
    "tutorProfileId": 10,
    "bio": "5 năm kinh nghiệm giảng dạy lập trình",
    "rating": 4.8,
    "experienceYears": 5,
    "totalSessionsCompleted": 120,
    "isAvailable": true,
    "subjects": [
      {
        "id": 5,
        "name": "Lập Trình Java"
      },
      {
        "id": 8,
        "name": "Cấu Trúc Dữ Liệu"
      }
    ],
    "schedules": [
      {
        "id": 1,
        "dayOfWeek": "MONDAY",
        "startTime": "14:00:00",
        "endTime": "16:00:00",
        "createdDate": "2025-09-01T00:00:00Z",
        "updateDate": "2025-11-01T00:00:00Z"
      },
      {
        "id": 2,
        "dayOfWeek": "WEDNESDAY",
        "startTime": "09:00:00",
        "endTime": "11:00:00",
        "createdDate": "2025-09-01T00:00:00Z",
        "updateDate": "2025-11-01T00:00:00Z"
      }
    ]
  }
}
```

---

### 🔹 Tutor Cập Nhật Hồ Sơ

**Endpoint:** `PUT /tutors/profile/{userId}`

**Request:**
```http
PUT /tutors/profile/3
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "bio": "7 năm kinh nghiệm giảng dạy lập trình và CSDL",
  "experienceYears": 7,
  "subjectIds": [5, 8, 12],
  "isAvailable": true
}
```

**Response Success (200):** (Tương tự response của GET /tutors/profile/{userId})

---

## Luồng 4: Admin Quản Lý Session

### 📖 Mô Tả Luồng

1. **Admin xem danh sách session đang chờ duyệt (status = PENDING)**
2. **Admin duyệt (approve) hoặc từ chối (reject) session**
3. **Chỉ session được duyệt (status = SCHEDULED) mới cho phép student đăng ký**

---

### 🔹 Bước 1: Admin Duyệt Session (Approve)

**Endpoint:** `PUT /admin/sessions/{sessionId}?setStatus=SCHEDULED`

**Mô tả:**
- Admin duyệt session do Tutor tạo
- Session chuyển từ PENDING → SCHEDULED
- **Chỉ session SCHEDULED mới xuất hiện trong danh sách available sessions cho student**
- Hệ thống tự động:
  - Kiểm tra session phải ở trạng thái PENDING
  - Chuyển status → SCHEDULED
  - Cập nhật `updatedDate`

**Request:**
```http
PUT /admin/sessions/25?setStatus=SCHEDULED
Authorization: Bearer {token}
```

**Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Session approved successfully",
  "data": {
    "id": 25,
    "tutorName": "Nguyễn Văn A",
    "studentNames": [],
    "subjectName": "Lập Trình Java",
    "startTime": "2025-11-28T14:00:00Z",
    "endTime": "2025-11-28T16:00:00Z",
    "format": "ONLINE",
    "location": "https://meet.google.com/new-link",
    "maxQuantity": 40,
    "currentQuantity": 0,
    "updatedDate": "2025-11-23T13:00:00Z"
  }
}
```

**Response Error - Session không ở trạng thái PENDING (400):**
```json
{
  "statusCode": 400,
  "message": "Session is not in PENDING status. Current status: SCHEDULED",
  "data": null
}
```

**Response Error - Không phải Admin (403):**
```json
{
  "statusCode": 403,
  "message": "User does not have admin privileges",
  "data": null
}
```

---

### 🔹 Bước 2: Admin Từ Chối Session (Reject)

**Endpoint:** `PUT /admin/sessions/{sessionId}?setStatus=CANCELLED`

**Mô tả:**
- Admin từ chối session do Tutor tạo
- Session chuyển từ PENDING → CANCELLED
- Session bị hủy sẽ không xuất hiện trong danh sách available sessions
- Hệ thống tự động:
  - Kiểm tra session phải ở trạng thái PENDING
  - Chuyển status → CANCELLED
  - Cập nhật `updatedDate`

**Request:**
```http
PUT /admin/sessions/26?setStatus=CANCELLED
Authorization: Bearer {token}
```

**Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Session rejected",
  "data": {
    "id": 26,
    "tutorName": "Phạm Thị D",
    "studentNames": [],
    "subjectName": "Cơ Sở Dữ Liệu",
    "startTime": "2025-11-29T09:00:00Z",
    "endTime": "2025-11-29T11:00:00Z",
    "format": "OFFLINE",
    "location": "Phòng A1.201",
    "maxQuantity": 30,
    "currentQuantity": 0,
    "updatedDate": "2025-11-23T13:10:00Z"
  }
}
```

**Response Error - Parameter không hợp lệ (400):**
```json
{
  "statusCode": 400,
  "message": "Invalid setStatus parameter. Must be 'SCHEDULED' or 'CANCELLED'",
  "data": null
}
```

---

### 🔹 Bước 3: Admin Xem Tất Cả User

**Endpoint:** `GET /admin/users`

**Mô tả:** Lấy danh sách tất cả user trong hệ thống (Student, Tutor, Admin).

**Request:**
```http
GET /admin/users
Authorization: Bearer {token}
```

**Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Users retrieved successfully",
  "data": [
    {
      "id": 1,
      "hcmutId": "2012345",
      "firstName": "Nguyễn Văn",
      "lastName": "A",
      "role": "STUDENT",
      "majorId": 3,
      "majorName": "Khoa Học Máy Tính",
      "department": "Khoa Khoa Học và Kỹ Thuật Máy Tính",
      "statusId": 1,
      "statusName": "ACTIVE"
    },
    {
      "id": 3,
      "hcmutId": "1912345",
      "firstName": "Phạm Thị",
      "lastName": "B",
      "role": "TUTOR",
      "majorId": 5,
      "majorName": "Kỹ Thuật Phần Mềm",
      "department": "Khoa Khoa Học và Kỹ Thuật Máy Tính",
      "statusId": 1,
      "statusName": "ACTIVE"
    }
  ]
}
```

---

### 🔹 Bước 4: Admin Cập Nhật Hồ Sơ Student

**Endpoint:** `PUT /admin/students/{userId}`

**Mô tả:** Admin có thể cập nhật hồ sơ của bất kỳ student nào.

**Request:**
```http
PUT /admin/students/5
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "firstName": "Nguyễn Văn",
  "lastName": "B Admin Updated",
  "otherMethodContact": "admin@hcmut.edu.vn",
  "majorId": 3
}
```

**Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Student profile updated successfully by admin",
  "data": {
    "id": 5,
    "hcmutId": "2012345",
    "firstName": "Nguyễn Văn",
    "lastName": "B Admin Updated",
    "role": "STUDENT",
    "majorId": 3,
    "majorName": "Khoa Học Máy Tính"
  }
}
```

---

### 🔹 Bước 5: Admin Xóa (Deactivate) User

**Endpoint (Student):** `DELETE /admin/students/{userId}`  
**Endpoint (Tutor):** `DELETE /admin/tutors/{userId}`

**Mô tả:**
- Admin soft delete user (set status = INACTIVE)
- User bị deactivate sẽ không thể đăng nhập hoặc sử dụng hệ thống

**Request:**
```http
DELETE /admin/students/5
Authorization: Bearer {token}
```

**Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Student profile deactivated successfully by admin",
  "data": null
}
```

---

## Các API Hỗ Trợ Khác

### 🔹 Lấy Danh Sách Môn Học

**Endpoint:** `GET /subjects`

**Response:**
```json
{
  "statusCode": 200,
  "message": "Subjects retrieved successfully",
  "data": [
    {
      "id": 5,
      "name": "Lập Trình Java",
      "code": "CO2001"
    },
    {
      "id": 8,
      "name": "Cấu Trúc Dữ Liệu",
      "code": "CO2003"
    }
  ]
}
```

---

### 🔹 Lấy Danh Sách Khoa

**Endpoint:** `GET /departments`

**Response:**
```json
{
  "statusCode": 200,
  "message": "Departments retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "Khoa Khoa Học và Kỹ Thuật Máy Tính",
      "code": "CSE"
    },
    {
      "id": 2,
      "name": "Khoa Điện - Điện Tử",
      "code": "EEE"
    }
  ]
}
```

---

### 🔹 Lấy Danh Sách Ngành Theo Khoa

**Endpoint:** `GET /majors/department/{departmentId}`

**Response:**
```json
{
  "statusCode": 200,
  "message": "Majors retrieved successfully",
  "data": [
    {
      "id": 3,
      "name": "Khoa Học Máy Tính",
      "departmentId": 1,
      "departmentName": "Khoa Khoa Học và Kỹ Thuật Máy Tính"
    },
    {
      "id": 5,
      "name": "Kỹ Thuật Phần Mềm",
      "departmentId": 1,
      "departmentName": "Khoa Khoa Học và Kỹ Thuật Máy Tính"
    }
  ]
}
```

---

## 📝 Lưu Ý Quan Trọng Cho Frontend

### 1. Authentication & Authorization
- **Tất cả API** đều cần JWT token
- Token được gửi qua header: `Authorization: Bearer {token}`
- User ID được extract tự động từ token, **KHÔNG GỬI** userId trong request body

### 2. Xử Lý Trạng Thái Session (SessionStatus)
- **PENDING (ID = 1)**: Session mới tạo, đang chờ Admin duyệt
- **SCHEDULED (ID = 2)**: Session đã được Admin duyệt, cho phép student đăng ký
- **CANCELLED (ID = 3)**: Session bị Admin từ chối hoặc bị hủy
- **COMPLETED (ID = 4)**: Session đã diễn ra xong

### 3. Xử Lý Trạng Thái StudentSession
- **PENDING**: Đang chờ tutor duyệt (màu vàng ⏳)
- **CONFIRMED**: Đã được duyệt (màu xanh ✅)
- **REJECTED**: Bị từ chối (màu đỏ ❌)

### 4. Race Condition & Capacity Check
- Hệ thống đã xử lý race condition khi nhiều tutor approve cùng lúc
- Frontend **CHỈ CẦN** hiển thị thông báo lỗi khi API trả về error
- **KHÔNG CẦN** kiểm tra `currentQuantity` trước khi approve (BE đã handle)

### 5. Schedule Conflict Detection
- Backend tự động kiểm tra trùng lịch khi student đăng ký
- Dựa trên `dayOfWeek`, `startTime`, `endTime`
- **StudentSchedule được tự động tạo khi đăng ký** để ngăn đăng ký session khác trùng giờ
- Frontend chỉ cần hiển thị thông báo lỗi

### 6. Hiển Thị Thời Gian
- Tất cả thời gian đều là **ISO 8601 format** (UTC): `2025-11-25T14:00:00Z`
- Frontend cần convert sang timezone local trước khi hiển thị
- Ví dụ: `2025-11-25T14:00:00Z` → `25/11/2025 21:00 (GMT+7)`

### 7. Error Handling
Luôn kiểm tra `statusCode` trong response:
```javascript
if (response.statusCode === 200) {
  // Success
  showSuccess(response.message);
  updateUI(response.data);
} else {
  // Error
  showError(response.message);
}
```

### 8. Pagination (Chưa Implement)
- Hiện tại các API trả về **toàn bộ dữ liệu** (không phân trang)
- Nếu cần pagination, sẽ bổ sung params: `?page=1&size=20`

---

## 🎯 Checklist Cho Frontend Developer

### Student Flow
- [ ] Hiển thị danh sách session khả dụng (chỉ session SCHEDULED)
- [ ] Tính số chỗ còn lại (maxQuantity - currentQuantity)
- [ ] Disable nút đăng ký nếu session đầy hoặc đã bắt đầu
- [ ] Gọi API đăng ký session
- [ ] Hiển thị thông báo "Đang chờ duyệt"
- [ ] Hiển thị lịch sử đăng ký với các trạng thái khác nhau
- [ ] Phân biệt màu sắc cho từng trạng thái (PENDING/CONFIRMED/REJECTED)

### Tutor Flow
- [ ] Form tạo session mới (validate startTime < endTime)
- [ ] Hiển thị thông báo "Session đang chờ Admin duyệt" sau khi tạo
- [ ] Hiển thị danh sách yêu cầu đăng ký PENDING
- [ ] Nhóm yêu cầu theo sessionId
- [ ] Nút duyệt/từ chối từng yêu cầu
- [ ] Checkbox và nút "Duyệt hàng loạt"
- [ ] Xử lý trường hợp session đầy (hiển thị thông báo phù hợp)
- [ ] Real-time update UI sau khi approve/reject

### Admin Flow
- [ ] Hiển thị danh sách session PENDING chờ duyệt
- [ ] Nút duyệt/từ chối session
- [ ] Hiển thị chi tiết session trước khi duyệt
- [ ] Xem danh sách tất cả user
- [ ] Cập nhật hồ sơ student/tutor
- [ ] Deactivate user (soft delete)

### Profile Management
- [ ] Hiển thị hồ sơ student/tutor
- [ ] Form chỉnh sửa hồ sơ
- [ ] Upload avatar (nếu có feature)
- [ ] Dropdown chọn khoa → Tự động load danh sách ngành

---

## 🆘 Hỗ Trợ

Nếu có thắc mắc về API hoặc gặp lỗi không nằm trong tài liệu này, vui lòng liên hệ Backend Team.

**Version:** 1.0  
**Last Updated:** 23/11/2025

