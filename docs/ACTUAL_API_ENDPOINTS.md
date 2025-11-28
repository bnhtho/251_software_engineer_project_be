# ACTUAL API ENDPOINTS - Tổng hợp từ Code thực tế

> **Tài liệu này được tạo tự động từ code thực tế trong các Controller**
> 
> **Ngày tạo:** 27/11/2025
> 
> **Lưu ý:** Tất cả response đều wrap trong `BaseResponse` với cấu trúc:
> ```json
> {
>   "statusCode": 200,
>   "message": "Success message",
>   "data": <actual_data>
> }
> ```

---

## 1. Authentication API (`/auth`)

### 1.1. Login
- **Endpoint:** `POST /auth/login`
- **Mô tả:** Đăng nhập vào hệ thống sử dụng HCMUT SSO
- **Authentication:** Không cần (public)
- **Request Body:**
```json
{
  "email": "string",
  "password": "string"
}
```
- **Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Login successful",
  "data": "JWT_TOKEN_STRING"
}
```
- **Response Failed (401):**
```json
{
  "statusCode": 401,
  "message": "Invalid credentials",
  "data": null
}
```

---

## 2. Admin API (`/admin`)

### 2.1. Soft Delete User Profile
- **Endpoint:** `DELETE /admin/users/{userId}`
- **Mô tả:** Admin soft delete user profile (set status to INACTIVE). Tự động xác định user là student hay tutor dựa trên role trong database
- **Authentication:** Required (ADMIN)
- **Path Parameters:**
  - `userId` (Integer) - ID của user cần xóa
- **Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "User profile deactivated successfully by admin",
  "data": null
}
```

### 2.2. Get All Users
- **Endpoint:** `GET /admin/users`
- **Mô tả:** Admin lấy danh sách tất cả users với pagination
- **Authentication:** Required (ADMIN)
- **Query Parameters:**
  - `page` (int, optional, default=0) - Số trang (bắt đầu từ 0)
- **Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Users retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "hcmutId": "string",
        "firstName": "string",
        "lastName": "string",
        "profileImage": "string",
        "academicStatus": "string",
        "dob": "2000-01-01",
        "phone": "string",
        "otherMethodContact": "string",
        "role": "STUDENT|TUTOR|ADMIN",
        "majorId": 1,
        "majorName": "string",
        "department": "string",
        "statusId": 1,
        "statusName": "string",
        "createdDate": "2025-01-01T00:00:00Z",
        "updateDate": "2025-01-01T00:00:00Z",
        "lastLogin": "2025-01-01T00:00:00Z"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 100,
    "totalPages": 10
  }
}
```

### 2.3. Get Pending Sessions
- **Endpoint:** `GET /admin/sessions/pending`
- **Mô tả:** Admin lấy danh sách các session đang chờ duyệt (status = PENDING)
- **Authentication:** Required (ADMIN)
- **Query Parameters:**
  - `page` (int, optional, default=0) - Số trang (bắt đầu từ 0)
- **Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Pending sessions retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "tutorName": "string",
        "studentNames": ["string"],
        "subjectName": "string",
        "startTime": "2025-01-01T08:00:00Z",
        "endTime": "2025-01-01T10:00:00Z",
        "format": "ONLINE|OFFLINE",
        "location": "string",
        "maxQuantity": 10,
        "currentQuantity": 5,
        "updatedDate": "2025-01-01T00:00:00Z"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 50,
    "totalPages": 5
  }
}
```

### 2.4. Update Session Status (Approve/Reject)
- **Endpoint:** `PUT /admin/sessions/{sessionId}`
- **Mô tả:** Admin approve hoặc reject session
- **Authentication:** Required (ADMIN)
- **Path Parameters:**
  - `sessionId` (Integer) - ID của session cần duyệt/từ chối
- **Query Parameters:**
  - `setStatus` (String, required) - "SCHEDULED" (approve) hoặc "CANCELLED" (reject)
- **Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Session approved successfully",
  "data": {
    "id": 1,
    "tutorName": "string",
    "studentNames": ["string"],
    "subjectName": "string",
    "startTime": "2025-01-01T08:00:00Z",
    "endTime": "2025-01-01T10:00:00Z",
    "format": "ONLINE|OFFLINE",
    "location": "string",
    "maxQuantity": 10,
    "currentQuantity": 5,
    "updatedDate": "2025-01-01T00:00:00Z"
  }
}
```

### 2.5. Get Pending Tutors
- **Endpoint:** `GET /admin/tutor/pending`
- **Mô tả:** Admin lấy danh sách tutor profiles đang chờ duyệt
- **Authentication:** Required (ADMIN)
- **Query Parameters:**
  - `page` (int, optional, default=0) - Số trang (bắt đầu từ 0)
- **Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Fetched pending tutor profiles successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "bio": "string",
        "experienceYears": 3,
        "rating": 4.5,
        "totalSessionsCompleted": 10,
        "isAvailable": true,
        "status": "PENDING|APPROVED|REJECTED",
        "subjects": [
          {
            "id": 1,
            "name": "string"
          }
        ],
        "user": {
          "id": 1,
          "firstName": "string",
          "lastName": "string",
          "role": "TUTOR",
          "hcmutId": "string",
          "academicStatus": "string"
        }
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 20,
    "totalPages": 2
  }
}
```

### 2.6. Approve Tutor Profile
- **Endpoint:** `PATCH /admin/{userId}/approve`
- **Mô tả:** Admin duyệt tutor profile
- **Authentication:** Required (ADMIN)
- **Path Parameters:**
  - `userId` (Integer) - ID của user
- **Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Tutor profile approved successfully",
  "data": {
    "id": 1,
    "bio": "string",
    "experienceYears": 3,
    "rating": 4.5,
    "totalSessionsCompleted": 10,
    "isAvailable": true,
    "status": "APPROVED",
    "subjects": [
      {
        "id": 1,
        "name": "string"
      }
    ],
    "user": {
      "id": 1,
      "firstName": "string",
      "lastName": "string",
      "role": "TUTOR",
      "hcmutId": "string",
      "academicStatus": "string"
    }
  }
}
```

### 2.7. Reject Tutor Profile
- **Endpoint:** `PATCH /admin/{userId}/reject`
- **Mô tả:** Admin từ chối tutor profile
- **Authentication:** Required (ADMIN)
- **Path Parameters:**
  - `userId` (Integer) - ID của user
- **Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Tutor profile rejected successfully",
  "data": {
    "id": 1,
    "bio": "string",
    "experienceYears": 3,
    "rating": 4.5,
    "totalSessionsCompleted": 10,
    "isAvailable": true,
    "status": "REJECTED",
    "subjects": [
      {
        "id": 1,
        "name": "string"
      }
    ],
    "user": {
      "id": 1,
      "firstName": "string",
      "lastName": "string",
      "role": "TUTOR",
      "hcmutId": "string",
      "academicStatus": "string"
    }
  }
}
```

---

## 3. Department API (`/departments`)

### 3.1. Get All Departments
- **Endpoint:** `GET /departments`
- **Mô tả:** Lấy danh sách tất cả khoa
- **Authentication:** Không rõ (có thể là public)
- **Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Departments retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "string"
    }
  ]
}
```

---

## 4. Major API (`/majors`)

### 4.1. Get All Majors
- **Endpoint:** `GET /majors`
- **Mô tả:** Lấy danh sách tất cả chuyên ngành
- **Authentication:** Không rõ (có thể là public)
- **Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Majors retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "string",
      "majorCode": "string",
      "programCode": "string",
      "note": "string",
      "departmentId": 1,
      "departmentName": "string"
    }
  ]
}
```

### 4.2. Get Majors By Department
- **Endpoint:** `GET /majors/by-department/{departmentId}`
- **Mô tả:** Lấy danh sách chuyên ngành theo khoa
- **Authentication:** Không rõ (có thể là public)
- **Path Parameters:**
  - `departmentId` (Integer) - ID của khoa
- **Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Majors retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "string",
      "majorCode": "string",
      "programCode": "string",
      "note": "string",
      "departmentId": 1,
      "departmentName": "string"
    }
  ]
}
```

---

## 5. Session API (`/sessions`)

### 5.1. Get All Sessions
- **Endpoint:** `GET /sessions`
- **Mô tả:** Lấy danh sách tất cả sessions với pagination
- **Authentication:** Không rõ
- **Query Parameters:**
  - `page` (int, optional, default=0) - Số trang (bắt đầu từ 0)
- **Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Sessions retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "tutorName": "string",
        "studentNames": ["string"],
        "subjectName": "string",
        "startTime": "2025-01-01T08:00:00Z",
        "endTime": "2025-01-01T10:00:00Z",
        "format": "ONLINE|OFFLINE",
        "location": "string",
        "maxQuantity": 10,
        "currentQuantity": 5,
        "updatedDate": "2025-01-01T00:00:00Z"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 100,
    "totalPages": 10
  }
}
```

### 5.2. Create Session
- **Endpoint:** `POST /sessions`
- **Mô tả:** Tạo session mới
- **Authentication:** Required (TUTOR)
- **Request Body:**
```json
{
  "tutorId": 1,
  "subjectId": 1,
  "startTime": "2025-01-01T08:00:00Z",
  "endTime": "2025-01-01T10:00:00Z",
  "format": "ONLINE|OFFLINE",
  "location": "string",
  "maxQuantity": 10,
  "dayOfWeek": 1,
  "sessionStatusId": 1
}
```
- **Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Session created successfully",
  "data": {
    "id": 1,
    "tutorName": "string",
    "studentNames": [],
    "subjectName": "string",
    "startTime": "2025-01-01T08:00:00Z",
    "endTime": "2025-01-01T10:00:00Z",
    "format": "ONLINE|OFFLINE",
    "location": "string",
    "maxQuantity": 10,
    "currentQuantity": 0,
    "updatedDate": "2025-01-01T00:00:00Z"
  }
}
```

### 5.3. Update Session
- **Endpoint:** `PUT /sessions/{id}`
- **Mô tả:** Cập nhật session (chỉ tutor tạo session mới có quyền)
- **Authentication:** Required (TUTOR - owner only)
- **Path Parameters:**
  - `id` (Integer) - ID của session
- **Request Body:**
```json
{
  "tutorId": 1,
  "subjectId": 1,
  "startTime": "2025-01-01T08:00:00Z",
  "endTime": "2025-01-01T10:00:00Z",
  "format": "ONLINE|OFFLINE",
  "location": "string",
  "maxQuantity": 10,
  "dayOfWeek": 1,
  "sessionStatusId": 1
}
```
- **Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Session updated successfully",
  "data": {
    "id": 1,
    "tutorName": "string",
    "studentNames": ["string"],
    "subjectName": "string",
    "startTime": "2025-01-01T08:00:00Z",
    "endTime": "2025-01-01T10:00:00Z",
    "format": "ONLINE|OFFLINE",
    "location": "string",
    "maxQuantity": 10,
    "currentQuantity": 5,
    "updatedDate": "2025-01-01T00:00:00Z"
  }
}
```
- **Response Forbidden (403):**
```json
{
  "statusCode": 403,
  "message": "Access denied: You can only update your own sessions",
  "data": null
}
```

### 5.4. Delete Session
- **Endpoint:** `DELETE /sessions/{id}`
- **Mô tả:** Xóa session (chỉ tutor tạo session mới có quyền)
- **Authentication:** Required (TUTOR - owner only)
- **Path Parameters:**
  - `id` (Integer) - ID của session
- **Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Session deleted successfully",
  "data": null
}
```
- **Response Forbidden (403):**
```json
{
  "statusCode": 403,
  "message": "Access denied: You can only delete your own sessions",
  "data": null
}
```

---

## 6. Session Status API (`/session-statuses`)

### 6.1. Get All Session Statuses
- **Endpoint:** `GET /session-statuses`
- **Mô tả:** Lấy danh sách tất cả trạng thái session
- **Authentication:** Không rõ
- **Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Session statuses retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "string",
      "description": "string"
    }
  ]
}
```

---

## 7. Student API (`/students`)

### 7.1. Get Student Profile
- **Endpoint:** `GET /students/profile`
- **Mô tả:** Student xem profile của chính mình (userId lấy từ token)
- **Authentication:** Required (STUDENT)
- **Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Student profile retrieved successfully",
  "data": {
    "id": 1,
    "hcmutId": "string",
    "firstName": "string",
    "lastName": "string",
    "profileImage": "string",
    "academicStatus": "string",
    "dob": "2000-01-01",
    "phone": "string",
    "otherMethodContact": "string",
    "role": "STUDENT",
    "majorId": 1,
    "majorName": "string",
    "department": "string",
    "createdDate": "2025-01-01T00:00:00Z",
    "updateDate": "2025-01-01T00:00:00Z",
    "lastLogin": "2025-01-01T00:00:00Z"
  }
}
```

### 7.2. Get Student Session History
- **Endpoint:** `GET /students/history`
- **Mô tả:** Student xem lịch sử các session đã đăng ký (userId lấy từ token)
- **Authentication:** Required (STUDENT)
- **Query Parameters:**
  - `page` (int, optional, default=0) - Số trang (bắt đầu từ 0)
- **Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Student session history retrieved successfully",
  "data": {
    "content": [
      {
        "studentSessionId": 1,
        "sessionId": 1,
        "tutorName": "string",
        "subjectName": "string",
        "startTime": "2025-01-01T08:00:00Z",
        "endTime": "2025-01-01T10:00:00Z",
        "format": "ONLINE|OFFLINE",
        "location": "string",
        "dayOfWeek": "MONDAY",
        "sessionStatus": "string",
        "registrationStatus": "PENDING|CONFIRMED|REJECTED",
        "registeredDate": "2025-01-01T00:00:00Z",
        "updatedDate": "2025-01-01T00:00:00Z"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 50,
    "totalPages": 5
  }
}
```

### 7.3. Update Student Profile
- **Endpoint:** `PUT /students/profile`
- **Mô tả:** Student cập nhật profile của chính mình (userId lấy từ token)
- **Authentication:** Required (STUDENT)
- **Request Body:**
```json
{
  "firstName": "string",
  "lastName": "string",
  "profileImage": "string",
  "academicStatus": "string",
  "dob": "2000-01-01",
  "phone": "string",
  "otherMethodContact": "string",
  "majorId": 1
}
```
- **Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Student profile updated successfully",
  "data": {
    "id": 1,
    "hcmutId": "string",
    "firstName": "string",
    "lastName": "string",
    "profileImage": "string",
    "academicStatus": "string",
    "dob": "2000-01-01",
    "phone": "string",
    "otherMethodContact": "string",
    "role": "STUDENT",
    "majorId": 1,
    "majorName": "string",
    "department": "string",
    "createdDate": "2025-01-01T00:00:00Z",
    "updateDate": "2025-01-01T00:00:00Z",
    "lastLogin": "2025-01-01T00:00:00Z"
  }
}
```

### 7.4. Get Available Sessions
- **Endpoint:** `GET /students/available-sessions`
- **Mô tả:** Lấy danh sách sessions khả dụng để đăng ký (public cho tất cả students)
- **Authentication:** Required (STUDENT)
- **Query Parameters:**
  - `page` (int, optional, default=0) - Số trang (bắt đầu từ 0)
- **Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Available sessions retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "tutorName": "string",
        "studentNames": ["string"],
        "subjectName": "string",
        "startTime": "2025-01-01T08:00:00Z",
        "endTime": "2025-01-01T10:00:00Z",
        "format": "ONLINE|OFFLINE",
        "location": "string",
        "maxQuantity": 10,
        "currentQuantity": 5,
        "updatedDate": "2025-01-01T00:00:00Z"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 100,
    "totalPages": 10
  }
}
```

### 7.5. Register Session
- **Endpoint:** `POST /students/register-session`
- **Mô tả:** Student đăng ký session (userId lấy từ token)
- **Authentication:** Required (STUDENT)
- **Query Parameters:**
  - `sessionId` (Integer, required) - ID của session muốn đăng ký
- **Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Yêu cầu đăng ký đã gửi, đang chờ tutor duyệt",
  "data": {
    "id": 1,
    "studentId": 1,
    "studentName": "string",
    "sessionId": 1,
    "sessionSubject": "string",
    "sessionStartTime": "2025-01-01T08:00:00Z",
    "sessionEndTime": "2025-01-01T10:00:00Z",
    "sessionFormat": "ONLINE|OFFLINE",
    "sessionDayOfWeek": "MONDAY",
    "status": "PENDING",
    "registeredDate": "2025-01-01T00:00:00Z",
    "confirmedDate": null,
    "updatedDate": "2025-01-01T00:00:00Z",
    "sessionLocation": "string"
  }
}
```

### 7.6. Get Week Schedule
- **Endpoint:** `GET /students/schedule/{weekOffset}`
- **Mô tả:** Student xem lịch học trong tuần (userId lấy từ token)
- **Authentication:** Required (STUDENT)
- **Path Parameters:**
  - `weekOffset` (Integer) - Offset tuần (0 = tuần hiện tại, 1 = tuần sau, -1 = tuần trước)
- **Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Lịch học trong tuần lấy thành công",
  "data": [
    {
      "id": 1,
      "studentId": 1,
      "studentName": "string",
      "sessionId": 1,
      "sessionSubject": "string",
      "sessionStartTime": "2025-01-01T08:00:00Z",
      "sessionEndTime": "2025-01-01T10:00:00Z",
      "sessionFormat": "ONLINE|OFFLINE",
      "sessionDayOfWeek": "MONDAY",
      "status": "CONFIRMED",
      "registeredDate": "2025-01-01T00:00:00Z",
      "confirmedDate": "2025-01-01T00:00:00Z",
      "updatedDate": "2025-01-01T00:00:00Z",
      "sessionLocation": "string"
    }
  ]
}
```

---

## 8. Student Session Status API (`/student-session-statuses`)

### 8.1. Get All Student Session Statuses
- **Endpoint:** `GET /student-session-statuses`
- **Mô tả:** Lấy danh sách tất cả trạng thái student-session
- **Authentication:** Không rõ
- **Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Student session statuses retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "string",
      "description": "string"
    }
  ]
}
```

---

## 9. Subject API (`/subjects`)

### 9.1. Get All Subjects
- **Endpoint:** `GET /subjects`
- **Mô tả:** Lấy danh sách tất cả môn học
- **Authentication:** Không rõ (có thể là public)
- **Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Subjects retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "string"
    }
  ]
}
```

---

## 10. Tutor API (`/tutors`)

### 10.1. Get All Tutors
- **Endpoint:** `GET /tutors`
- **Mô tả:** Lấy danh sách tất cả tutors (public endpoint)
- **Authentication:** Không rõ (có thể là public)
- **Query Parameters:**
  - `page` (int, optional, default=0) - Số trang (bắt đầu từ 0)
- **Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Tutors retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "hcmutId": "string",
        "firstName": "string",
        "lastName": "string",
        "profileImage": "string",
        "academicStatus": "string",
        "dob": "2000-01-01",
        "phone": "string",
        "otherMethodContact": "string",
        "role": "TUTOR",
        "createdDate": "2025-01-01T00:00:00Z",
        "updateDate": "2025-01-01T00:00:00Z",
        "lastLogin": "2025-01-01T00:00:00Z",
        "title": "string",
        "majorId": 1,
        "majorName": "string",
        "department": "string",
        "description": "string",
        "specializations": ["string"],
        "rating": 4.5,
        "reviewCount": 10,
        "studentCount": 50,
        "experienceYears": 3,
        "isAvailable": true,
        "status": "PENDING|APPROVED|REJECTED"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 100,
    "totalPages": 10
  }
}
```

### 10.2. Get Tutor Detail Profile
- **Endpoint:** `GET /tutors/profile`
- **Mô tả:** Tutor xem detailed profile của chính mình (userId lấy từ token)
- **Authentication:** Required (TUTOR)
- **Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Tutor detail retrieved successfully",
  "data": {
    "id": 1,
    "hcmutId": "string",
    "firstName": "string",
    "lastName": "string",
    "profileImage": "string",
    "academicStatus": "string",
    "dob": "2000-01-01",
    "phone": "string",
    "otherMethodContact": "string",
    "role": "TUTOR",
    "createdDate": "2025-01-01T00:00:00Z",
    "updateDate": "2025-01-01T00:00:00Z",
    "lastLogin": "2025-01-01T00:00:00Z",
    "tutorProfileId": 1,
    "majorId": 1,
    "majorName": "string",
    "department": "string",
    "bio": "string",
    "subjects": [
      {
        "id": 1,
        "name": "string"
      }
    ],
    "rating": 4.5,
    "experienceYears": 3,
    "totalSessionsCompleted": 10,
    "isAvailable": true,
    "status": "PENDING|APPROVED|REJECTED",
    "schedules": [
      {
        "id": 1,
        "sessionId": 1,
        "dayOfWeek": "MONDAY",
        "startTime": "2025-01-01T08:00:00Z",
        "endTime": "2025-01-01T10:00:00Z",
        "subjectName": "string",
        "tutorName": "string",
        "format": "ONLINE|OFFLINE",
        "location": "string",
        "createdDate": "2025-01-01T00:00:00Z",
        "updateDate": "2025-01-01T00:00:00Z"
      }
    ]
  }
}
```

### 10.3. Update Tutor Profile
- **Endpoint:** `PUT /tutors/profile`
- **Mô tả:** Tutor cập nhật detailed profile của chính mình (userId lấy từ token)
- **Authentication:** Required (TUTOR)
- **Request Body:**
```json
{
  "firstName": "string",
  "lastName": "string",
  "profileImage": "string",
  "academicStatus": "string",
  "dob": "2000-01-01",
  "phone": "string",
  "otherMethodContact": "string",
  "majorId": 1,
  "bio": "string",
  "subjectIds": [1, 2, 3],
  "experienceYears": 3,
  "isAvailable": true
}
```
- **Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Tutor profile updated successfully",
  "data": {
    "id": 1,
    "hcmutId": "string",
    "firstName": "string",
    "lastName": "string",
    "profileImage": "string",
    "academicStatus": "string",
    "dob": "2000-01-01",
    "phone": "string",
    "otherMethodContact": "string",
    "role": "TUTOR",
    "createdDate": "2025-01-01T00:00:00Z",
    "updateDate": "2025-01-01T00:00:00Z",
    "lastLogin": "2025-01-01T00:00:00Z",
    "tutorProfileId": 1,
    "majorId": 1,
    "majorName": "string",
    "department": "string",
    "bio": "string",
    "subjects": [
      {
        "id": 1,
        "name": "string"
      }
    ],
    "rating": 4.5,
    "experienceYears": 3,
    "totalSessionsCompleted": 10,
    "isAvailable": true,
    "status": "APPROVED",
    "schedules": []
  }
}
```

### 10.4. Get Pending Student Registrations
- **Endpoint:** `GET /tutors/pending-registrations`
- **Mô tả:** Tutor xem danh sách yêu cầu đăng ký đang chờ duyệt (userId lấy từ token)
- **Authentication:** Required (TUTOR)
- **Query Parameters:**
  - `page` (int, optional, default=0) - Số trang (bắt đầu từ 0)
- **Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Pending student sessions retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "studentId": 1,
        "studentName": "string",
        "sessionId": 1,
        "sessionSubject": "string",
        "sessionStartTime": "2025-01-01T08:00:00Z",
        "sessionEndTime": "2025-01-01T10:00:00Z",
        "sessionFormat": "ONLINE|OFFLINE",
        "sessionDayOfWeek": "MONDAY",
        "status": "PENDING",
        "registeredDate": "2025-01-01T00:00:00Z",
        "confirmedDate": null,
        "updatedDate": "2025-01-01T00:00:00Z",
        "sessionLocation": "string"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 20,
    "totalPages": 2
  }
}
```

### 10.5. Approve Student Sessions
- **Endpoint:** `PUT /tutors/student-sessions/approve`
- **Mô tả:** Tutor duyệt một hoặc nhiều yêu cầu đăng ký (userId lấy từ token)
- **Authentication:** Required (TUTOR)
- **Request Body:**
```json
[1, 2, 3]
```
(Array of studentSessionIds)
- **Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Đã duyệt 3 yêu cầu đăng ký",
  "data": [
    {
      "id": 1,
      "studentId": 1,
      "studentName": "string",
      "sessionId": 1,
      "sessionSubject": "string",
      "sessionStartTime": "2025-01-01T08:00:00Z",
      "sessionEndTime": "2025-01-01T10:00:00Z",
      "sessionFormat": "ONLINE|OFFLINE",
      "sessionDayOfWeek": "MONDAY",
      "status": "CONFIRMED",
      "registeredDate": "2025-01-01T00:00:00Z",
      "confirmedDate": "2025-01-01T00:00:00Z",
      "updatedDate": "2025-01-01T00:00:00Z",
      "sessionLocation": "string"
    }
  ]
}
```

### 10.6. Reject Student Sessions
- **Endpoint:** `PUT /tutors/student-sessions/reject`
- **Mô tả:** Tutor từ chối một hoặc nhiều yêu cầu đăng ký (userId lấy từ token)
- **Authentication:** Required (TUTOR)
- **Request Body:**
```json
[1, 2, 3]
```
(Array of studentSessionIds)
- **Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Đã từ chối 3 yêu cầu đăng ký",
  "data": [
    {
      "id": 1,
      "studentId": 1,
      "studentName": "string",
      "sessionId": 1,
      "sessionSubject": "string",
      "sessionStartTime": "2025-01-01T08:00:00Z",
      "sessionEndTime": "2025-01-01T10:00:00Z",
      "sessionFormat": "ONLINE|OFFLINE",
      "sessionDayOfWeek": "MONDAY",
      "status": "REJECTED",
      "registeredDate": "2025-01-01T00:00:00Z",
      "confirmedDate": null,
      "updatedDate": "2025-01-01T00:00:00Z",
      "sessionLocation": "string"
    }
  ]
}
```

### 10.7. Get Week Schedule
- **Endpoint:** `GET /tutors/schedule/{weekOffset}`
- **Mô tả:** Tutor xem lịch giảng dạy trong tuần (userId lấy từ token)
- **Authentication:** Required (TUTOR)
- **Path Parameters:**
  - `weekOffset` (Integer) - Offset tuần (0 = tuần hiện tại, 1 = tuần sau, -1 = tuần trước)
- **Response Success (200):**
```json
{
  "statusCode": 200,
  "message": "Lịch giảng dạy trong tuần lấy thành công",
  "data": [
    {
      "id": 1,
      "tutorName": "string",
      "studentNames": ["string"],
      "subjectName": "string",
      "startTime": "2025-01-01T08:00:00Z",
      "endTime": "2025-01-01T10:00:00Z",
      "format": "ONLINE|OFFLINE",
      "location": "string",
      "maxQuantity": 10,
      "currentQuantity": 5,
      "updatedDate": "2025-01-01T00:00:00Z"
    }
  ]
}
```

### 10.8. Register Tutor Profile
- **Endpoint:** `POST /tutors`
- **Mô tả:** Đăng ký profile tutor mới (userId lấy từ token)
- **Authentication:** Required (User chưa có tutor profile)
- **Request Body:**
```json
{
  "title": "string",
  "majorId": 1,
  "description": "string",
  "subjects": [1, 2, 3],
  "experienceYears": 3
}
```
- **Response Success (201):**
```json
{
  "statusCode": 201,
  "message": "Tutor profile created successfully",
  "data": {
    "id": 1,
    "hcmutId": "string",
    "firstName": "string",
    "lastName": "string",
    "profileImage": "string",
    "academicStatus": "string",
    "dob": "2000-01-01",
    "phone": "string",
    "otherMethodContact": "string",
    "role": "TUTOR",
    "createdDate": "2025-01-01T00:00:00Z",
    "updateDate": "2025-01-01T00:00:00Z",
    "lastLogin": "2025-01-01T00:00:00Z",
    "title": "string",
    "majorId": 1,
    "majorName": "string",
    "department": "string",
    "description": "string",
    "specializations": ["string"],
    "rating": null,
    "reviewCount": 0,
    "studentCount": 0,
    "experienceYears": 3,
    "isAvailable": true,
    "status": "PENDING"
  }
}
```

---

## Tổng kết

**Tổng số endpoints: 37**

### Phân loại theo Controller:
1. **AuthenticationController** (1 endpoint)
2. **AdminController** (7 endpoints)
3. **DepartmentController** (1 endpoint)
4. **MajorController** (2 endpoints)
5. **SessionController** (4 endpoints)
6. **SessionStatusController** (1 endpoint)
7. **StudentController** (6 endpoints)
8. **StudentSessionStatusController** (1 endpoint)
9. **SubjectController** (1 endpoint)
10. **TutorController** (8 endpoints)

### Lưu ý quan trọng:
- Tất cả response đều được wrap trong `BaseResponse` với cấu trúc chuẩn
- Pagination sử dụng cấu trúc: `{content: [], page, size, totalElements, totalPages}`
- Authentication token chứa userId được extract bằng `(Integer) authentication.getPrincipal()`
- Các endpoint yêu cầu authentication sẽ lấy userId từ token, không cần client gửi userId trong request

