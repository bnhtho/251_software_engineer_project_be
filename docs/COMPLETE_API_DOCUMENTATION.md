# TỔNG HỢP ĐẦY ĐỦ CÁC API CỦA HỆ THỐNG TUTOR SYSTEM

> **LƯU Ý**: File này được tổng hợp từ CODE THỰC TẾ của dự án, không đoán mò, không dựa vào các file .md khác.

## MỤC LỤC
1. [Authentication APIs](#1-authentication-apis)
2. [Admin APIs](#2-admin-apis)
3. [Tutor APIs](#3-tutor-apis)
4. [Student APIs](#4-student-apis)
5. [Session APIs](#5-session-apis)
6. [Department APIs](#6-department-apis)
7. [Major APIs](#7-major-apis)
8. [Subject APIs](#8-subject-apis)
9. [Session Status APIs](#9-session-status-apis)
10. [Student Session Status APIs](#10-student-session-status-apis)

---

## 1. AUTHENTICATION APIs

### 1.1. Login
**Endpoint:** `POST /auth/login`

**Mô tả:** Đăng nhập bằng HCMUT SSO

**Request Body:**
```json
{
  "email": "string",
  "password": "string"
}
```

**Response:**
```json
{
  "statusCode": 200,
  "message": "Login successful",
  "data": "JWT_TOKEN_STRING"
}
```

**Response (Failed):**
```json
{
  "statusCode": 401,
  "message": "Invalid credentials",
  "data": null
}
```

---

## 2. ADMIN APIs

### 2.1. Get Admin Profile
**Endpoint:** `GET /admin/profile`

**Mô tả:** Lấy thông tin profile của admin hiện tại (từ token authentication)

**Headers:** `Authorization: Bearer {token}`

**Response:**
```json
{
  "statusCode": 200,
  "message": "Admin profile retrieved successfully",
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
    "role": "ADMIN",
    "majorId": 1,
    "majorName": "string",
    "department": "string",
    "statusId": 1,
    "statusName": "string",
    "createdDate": "2024-01-01T00:00:00Z",
    "updateDate": "2024-01-01T00:00:00Z",
    "lastLogin": "2024-01-01T00:00:00Z"
  }
}
```

### 2.2. Get All Users (with Pagination)
**Endpoint:** `GET /admin/users?page={page}`

**Mô tả:** Lấy danh sách tất cả users (students + tutors + admins)

**Headers:** `Authorization: Bearer {token}`

**Query Parameters:**
- `page` (optional, default=0): Số trang (bắt đầu từ 0)

**Response:**
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
        "createdDate": "2024-01-01T00:00:00Z",
        "updateDate": "2024-01-01T00:00:00Z",
        "lastLogin": "2024-01-01T00:00:00Z"
      }
    ],
    "currentPage": 0,
    "totalPages": 5,
    "totalItems": 50,
    "pageSize": 10
  }
}
```

### 2.3. Get User Profile by ID
**Endpoint:** `GET /admin/users/{userId}`

**Mô tả:** Lấy thông tin chi tiết của 1 user theo userId

**Headers:** `Authorization: Bearer {token}`

**Path Parameters:**
- `userId`: ID của user cần lấy thông tin

**Response:**
```json
{
  "statusCode": 200,
  "message": "User profile retrieved successfully",
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
    "role": "STUDENT|TUTOR|ADMIN",
    "majorId": 1,
    "majorName": "string",
    "department": "string",
    "statusId": 1,
    "statusName": "string",
    "createdDate": "2024-01-01T00:00:00Z",
    "updateDate": "2024-01-01T00:00:00Z",
    "lastLogin": "2024-01-01T00:00:00Z"
  }
}
```

### 2.4. Delete User Profile (Soft Delete)
**Endpoint:** `DELETE /admin/users/{userId}`

**Mô tả:** Soft delete user (set status thành INACTIVE). Tự động xác định user là student hay tutor dựa trên role.

**Headers:** `Authorization: Bearer {token}`

**Path Parameters:**
- `userId`: ID của user cần xóa

**Response:**
```json
{
  "statusCode": 200,
  "message": "User profile deactivated successfully by admin",
  "data": null
}
```

### 2.5. Get Pending Sessions (with Pagination)
**Endpoint:** `GET /admin/sessions/pending?page={page}`

**Mô tả:** Lấy danh sách các session đang chờ duyệt (status = PENDING)

**Headers:** `Authorization: Bearer {token}`

**Query Parameters:**
- `page` (optional, default=0): Số trang (bắt đầu từ 0)

**Response:**
```json
{
  "statusCode": 200,
  "message": "Pending sessions retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "tutorName": "string",
        "studentNames": ["student1", "student2"],
        "subjectName": "string",
        "startTime": "2024-01-01T08:00:00Z",
        "endTime": "2024-01-01T10:00:00Z",
        "format": "ONLINE|OFFLINE",
        "location": "string",
        "maxQuantity": 10,
        "currentQuantity": 5,
        "updatedDate": "2024-01-01T00:00:00Z"
      }
    ],
    "currentPage": 0,
    "totalPages": 3,
    "totalItems": 30,
    "pageSize": 10
  }
}
```

### 2.6. Update Session Status (Approve/Reject)
**Endpoint:** `PUT /admin/sessions/{sessionId}?setStatus={status}`

**Mô tả:** Admin duyệt hoặc từ chối session

**Headers:** `Authorization: Bearer {token}`

**Path Parameters:**
- `sessionId`: ID của session cần duyệt/từ chối

**Query Parameters:**
- `setStatus`: Trạng thái mới ("SCHEDULED" để approve, "CANCELLED" để reject)

**Response (Approved):**
```json
{
  "statusCode": 200,
  "message": "Session approved successfully",
  "data": {
    "id": 1,
    "tutorName": "string",
    "studentNames": ["student1", "student2"],
    "subjectName": "string",
    "startTime": "2024-01-01T08:00:00Z",
    "endTime": "2024-01-01T10:00:00Z",
    "format": "ONLINE|OFFLINE",
    "location": "string",
    "maxQuantity": 10,
    "currentQuantity": 5,
    "updatedDate": "2024-01-01T00:00:00Z"
  }
}
```

**Response (Rejected):**
```json
{
  "statusCode": 200,
  "message": "Session rejected",
  "data": {
    "id": 1,
    "tutorName": "string",
    "studentNames": ["student1", "student2"],
    "subjectName": "string",
    "startTime": "2024-01-01T08:00:00Z",
    "endTime": "2024-01-01T10:00:00Z",
    "format": "ONLINE|OFFLINE",
    "location": "string",
    "maxQuantity": 10,
    "currentQuantity": 5,
    "updatedDate": "2024-01-01T00:00:00Z"
  }
}
```

### 2.7. Get Pending Tutors (with Pagination)
**Endpoint:** `GET /admin/tutor/pending?page={page}`

**Mô tả:** Lấy danh sách các tutor profile đang chờ duyệt (status = PENDING)

**Headers:** `Authorization: Bearer {token}`

**Query Parameters:**
- `page` (optional, default=0): Số trang (bắt đầu từ 0)

**Response:**
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
        "status": "PENDING",
        "subjects": [
          {
            "id": 1,
            "name": "Toán cao cấp"
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
    "currentPage": 0,
    "totalPages": 2,
    "totalItems": 15,
    "pageSize": 10
  }
}
```

### 2.8. Approve Tutor by User ID
**Endpoint:** `PATCH /admin/{userId}/approve`

**Mô tả:** Admin duyệt tutor profile (set status = APPROVED)

**Headers:** `Authorization: Bearer {token}`

**Path Parameters:**
- `userId`: ID của user (tutor) cần duyệt

**Response:**
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
        "name": "Toán cao cấp"
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

### 2.9. Reject Tutor by User ID
**Endpoint:** `PATCH /admin/{userId}/reject`

**Mô tả:** Admin từ chối tutor profile (set status = REJECTED)

**Headers:** `Authorization: Bearer {token}`

**Path Parameters:**
- `userId`: ID của user (tutor) cần từ chối

**Response:**
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
        "name": "Toán cao cấp"
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

## 3. TUTOR APIs

### 3.1. Get All Tutors (with Pagination)
**Endpoint:** `GET /tutors?page={page}`

**Mô tả:** Lấy danh sách tất cả tutors (Public endpoint - không cần authentication)

**Query Parameters:**
- `page` (optional, default=0): Số trang (bắt đầu từ 0)

**Response:**
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
        "createdDate": "2024-01-01T00:00:00Z",
        "updateDate": "2024-01-01T00:00:00Z",
        "lastLogin": "2024-01-01T00:00:00Z",
        "title": "string",
        "majorId": 1,
        "majorName": "string",
        "department": "string",
        "description": "string",
        "specializations": ["Toán", "Lý"],
        "rating": 4.5,
        "reviewCount": 10,
        "studentCount": 50,
        "experienceYears": 3,
        "isAvailable": true,
        "status": "APPROVED"
      }
    ],
    "currentPage": 0,
    "totalPages": 5,
    "totalItems": 50,
    "pageSize": 10
  }
}
```

### 3.2. Get Tutor Profile (Detail)
**Endpoint:** `GET /tutors/profile`

**Mô tả:** Lấy thông tin chi tiết của tutor hiện tại (từ token). Bao gồm: teaching schedule, subjects, experience, etc.

**Headers:** `Authorization: Bearer {token}`

**Response:**
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
    "createdDate": "2024-01-01T00:00:00Z",
    "updateDate": "2024-01-01T00:00:00Z",
    "lastLogin": "2024-01-01T00:00:00Z",
    "tutorProfileId": 1,
    "majorId": 1,
    "majorName": "string",
    "department": "string",
    "bio": "string",
    "subjects": [
      {
        "id": 1,
        "name": "Toán cao cấp"
      },
      {
        "id": 2,
        "name": "Vật lý đại cương"
      }
    ],
    "rating": 4.5,
    "experienceYears": 3,
    "totalSessionsCompleted": 100,
    "isAvailable": true,
    "status": "APPROVED",
    "schedules": [
      {
        "id": 1,
        "sessionId": 1,
        "dayOfWeek": "MONDAY",
        "startTime": "2024-01-01T08:00:00Z",
        "endTime": "2024-01-01T10:00:00Z",
        "subjectName": "Toán cao cấp",
        "tutorName": "string",
        "format": "ONLINE",
        "location": "Google Meet",
        "createdDate": "2024-01-01T00:00:00Z",
        "updateDate": "2024-01-01T00:00:00Z"
      }
    ]
  }
}
```

### 3.3. Register Tutor Profile
**Endpoint:** `POST /tutors`

**Mô tả:** Đăng ký trở thành tutor (tạo tutor profile mới). userId lấy từ token authentication.

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "title": "string",
  "majorId": 1,
  "description": "string",
  "subjects": [1, 2, 3],
  "experienceYears": 3
}
```

**Response:**
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
    "createdDate": "2024-01-01T00:00:00Z",
    "updateDate": "2024-01-01T00:00:00Z",
    "lastLogin": "2024-01-01T00:00:00Z",
    "title": "string",
    "majorId": 1,
    "majorName": "string",
    "department": "string",
    "description": "string",
    "specializations": ["Toán", "Lý"],
    "rating": 0.0,
    "reviewCount": 0,
    "studentCount": 0,
    "experienceYears": 3,
    "isAvailable": true,
    "status": "PENDING"
  }
}
```

### 3.4. Update Tutor Profile
**Endpoint:** `PUT /tutors/profile`

**Mô tả:** Cập nhật thông tin tutor profile. Tutor chỉ có thể cập nhật profile của chính mình (userId từ token).

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
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

**Response:**
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
    "createdDate": "2024-01-01T00:00:00Z",
    "updateDate": "2024-01-01T00:00:00Z",
    "lastLogin": "2024-01-01T00:00:00Z",
    "tutorProfileId": 1,
    "majorId": 1,
    "majorName": "string",
    "department": "string",
    "bio": "string",
    "subjects": [
      {
        "id": 1,
        "name": "Toán cao cấp"
      }
    ],
    "rating": 4.5,
    "experienceYears": 3,
    "totalSessionsCompleted": 100,
    "isAvailable": true,
    "status": "APPROVED",
    "schedules": []
  }
}
```

### 3.5. Get Pending Student Registrations (with Pagination)
**Endpoint:** `GET /tutors/pending-registrations?page={page}`

**Mô tả:** Lấy danh sách yêu cầu đăng ký đang chờ duyệt. Tutor chỉ xem được yêu cầu của các session mình tạo.

**Headers:** `Authorization: Bearer {token}`

**Query Parameters:**
- `page` (optional, default=0): Số trang (bắt đầu từ 0)

**Response:**
```json
{
  "statusCode": 200,
  "message": "Pending student sessions retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "studentId": 10,
        "studentName": "Nguyen Van A",
        "sessionId": 5,
        "sessionSubject": "Toán cao cấp",
        "sessionStartTime": "2024-01-01T08:00:00Z",
        "sessionEndTime": "2024-01-01T10:00:00Z",
        "sessionFormat": "ONLINE",
        "sessionDayOfWeek": "MONDAY",
        "status": "PENDING",
        "registeredDate": "2024-01-01T00:00:00Z",
        "confirmedDate": null,
        "updatedDate": "2024-01-01T00:00:00Z",
        "sessionLocation": "Google Meet"
      }
    ],
    "currentPage": 0,
    "totalPages": 2,
    "totalItems": 15,
    "pageSize": 10
  }
}
```

### 3.6. Approve Student Registrations
**Endpoint:** `PUT /tutors/student-sessions/approve`

**Mô tả:** Duyệt yêu cầu đăng ký của student (một hoặc nhiều). Nếu duyệt 1 người thì gửi list có 1 phần tử.

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
[1, 2, 3]
```
(Danh sách các studentSessionId cần duyệt)

**Response:**
```json
{
  "statusCode": 200,
  "message": "Đã duyệt 3 yêu cầu đăng ký",
  "data": [
    {
      "id": 1,
      "studentId": 10,
      "studentName": "Nguyen Van A",
      "sessionId": 5,
      "sessionSubject": "Toán cao cấp",
      "sessionStartTime": "2024-01-01T08:00:00Z",
      "sessionEndTime": "2024-01-01T10:00:00Z",
      "sessionFormat": "ONLINE",
      "sessionDayOfWeek": "MONDAY",
      "status": "CONFIRMED",
      "registeredDate": "2024-01-01T00:00:00Z",
      "confirmedDate": "2024-01-01T10:00:00Z",
      "updatedDate": "2024-01-01T10:00:00Z",
      "sessionLocation": "Google Meet"
    }
  ]
}
```

### 3.7. Reject Student Registrations
**Endpoint:** `PUT /tutors/student-sessions/reject`

**Mô tả:** Từ chối yêu cầu đăng ký của student (một hoặc nhiều). Nếu từ chối 1 người thì gửi list có 1 phần tử.

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
[1, 2, 3]
```
(Danh sách các studentSessionId cần từ chối)

**Response:**
```json
{
  "statusCode": 200,
  "message": "Đã từ chối 3 yêu cầu đăng ký",
  "data": [
    {
      "id": 1,
      "studentId": 10,
      "studentName": "Nguyen Van A",
      "sessionId": 5,
      "sessionSubject": "Toán cao cấp",
      "sessionStartTime": "2024-01-01T08:00:00Z",
      "sessionEndTime": "2024-01-01T10:00:00Z",
      "sessionFormat": "ONLINE",
      "sessionDayOfWeek": "MONDAY",
      "status": "REJECTED",
      "registeredDate": "2024-01-01T00:00:00Z",
      "confirmedDate": null,
      "updatedDate": "2024-01-01T10:00:00Z",
      "sessionLocation": "Google Meet"
    }
  ]
}
```

### 3.8. Get Tutor Week Schedule
**Endpoint:** `GET /tutors/schedule/{weekOffset}`

**Mô tả:** Lấy lịch giảng dạy trong tuần của tutor. Tutor chỉ có thể xem lịch của chính mình (từ token).

**Headers:** `Authorization: Bearer {token}`

**Path Parameters:**
- `weekOffset`: Offset tuần (0 = tuần hiện tại, 1 = tuần sau, -1 = tuần trước)

**Response:**
```json
{
  "statusCode": 200,
  "message": "Lịch giảng dạy trong tuần lấy thành công",
  "data": [
    {
      "id": 1,
      "tutorName": "string",
      "studentNames": ["student1", "student2"],
      "subjectName": "Toán cao cấp",
      "startTime": "2024-01-01T08:00:00Z",
      "endTime": "2024-01-01T10:00:00Z",
      "format": "ONLINE",
      "location": "Google Meet",
      "maxQuantity": 10,
      "currentQuantity": 5,
      "updatedDate": "2024-01-01T00:00:00Z"
    }
  ]
}
```

---

## 4. STUDENT APIs

### 4.1. Get Student Profile
**Endpoint:** `GET /students/profile`

**Mô tả:** Lấy thông tin profile của student hiện tại (từ token). Student chỉ có thể xem profile của chính mình.

**Headers:** `Authorization: Bearer {token}`

**Response:**
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
    "createdDate": "2024-01-01T00:00:00Z",
    "updateDate": "2024-01-01T00:00:00Z",
    "lastLogin": "2024-01-01T00:00:00Z"
  }
}
```

### 4.2. Update Student Profile
**Endpoint:** `PUT /students/profile`

**Mô tả:** Cập nhật thông tin student profile. Student chỉ có thể cập nhật profile của chính mình (từ token).

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
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

**Response:**
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
    "createdDate": "2024-01-01T00:00:00Z",
    "updateDate": "2024-01-01T00:00:00Z",
    "lastLogin": "2024-01-01T00:00:00Z"
  }
}
```

### 4.3. Get Student Session History (with Pagination)
**Endpoint:** `GET /students/history?page={page}`

**Mô tả:** Lấy lịch sử tất cả các session student đã đăng ký. Student chỉ có thể xem history của chính mình (từ token).

**Headers:** `Authorization: Bearer {token}`

**Query Parameters:**
- `page` (optional, default=0): Số trang (bắt đầu từ 0)

**Response:**
```json
{
  "statusCode": 200,
  "message": "Student session history retrieved successfully",
  "data": {
    "content": [
      {
        "studentSessionId": 1,
        "sessionId": 5,
        "tutorName": "Nguyen Van B",
        "subjectName": "Toán cao cấp",
        "startTime": "2024-01-01T08:00:00Z",
        "endTime": "2024-01-01T10:00:00Z",
        "format": "ONLINE",
        "location": "Google Meet",
        "dayOfWeek": "MONDAY",
        "sessionStatus": "SCHEDULED",
        "registrationStatus": "CONFIRMED",
        "registeredDate": "2024-01-01T00:00:00Z",
        "updatedDate": "2024-01-01T00:00:00Z"
      }
    ],
    "currentPage": 0,
    "totalPages": 3,
    "totalItems": 30,
    "pageSize": 10
  }
}
```

### 4.4. Get Available Sessions (with Pagination)
**Endpoint:** `GET /students/available-sessions?page={page}`

**Mô tả:** Lấy danh sách session khả dụng để đăng ký. Public endpoint - tất cả student có thể xem.

**Headers:** `Authorization: Bearer {token}`

**Query Parameters:**
- `page` (optional, default=0): Số trang (bắt đầu từ 0)

**Response:**
```json
{
  "statusCode": 200,
  "message": "Available sessions retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "tutorName": "string",
        "studentNames": ["student1"],
        "subjectName": "Toán cao cấp",
        "startTime": "2024-01-01T08:00:00Z",
        "endTime": "2024-01-01T10:00:00Z",
        "format": "ONLINE",
        "location": "Google Meet",
        "maxQuantity": 10,
        "currentQuantity": 5,
        "updatedDate": "2024-01-01T00:00:00Z"
      }
    ],
    "currentPage": 0,
    "totalPages": 5,
    "totalItems": 50,
    "pageSize": 10
  }
}
```

### 4.5. Register Session
**Endpoint:** `POST /students/register-session?sessionId={sessionId}`

**Mô tả:** Đăng ký tham gia session. Student chỉ có thể đăng ký cho chính mình (từ token). Sau khi đăng ký, yêu cầu sẽ ở trạng thái PENDING, đợi tutor duyệt.

**Headers:** `Authorization: Bearer {token}`

**Query Parameters:**
- `sessionId`: ID của session muốn đăng ký

**Response:**
```json
{
  "statusCode": 200,
  "message": "Yêu cầu đăng ký đã gửi, đang chờ tutor duyệt",
  "data": {
    "id": 1,
    "studentId": 10,
    "studentName": "Nguyen Van A",
    "sessionId": 5,
    "sessionSubject": "Toán cao cấp",
    "sessionStartTime": "2024-01-01T08:00:00Z",
    "sessionEndTime": "2024-01-01T10:00:00Z",
    "sessionFormat": "ONLINE",
    "sessionDayOfWeek": "MONDAY",
    "status": "PENDING",
    "registeredDate": "2024-01-01T00:00:00Z",
    "confirmedDate": null,
    "updatedDate": "2024-01-01T00:00:00Z",
    "sessionLocation": "Google Meet"
  }
}
```

### 4.6. Get Student Week Schedule
**Endpoint:** `GET /students/schedule/{weekOffset}`

**Mô tả:** Lấy lịch học trong tuần của student. Student chỉ có thể xem lịch của chính mình (từ token).

**Headers:** `Authorization: Bearer {token}`

**Path Parameters:**
- `weekOffset`: Offset tuần (0 = tuần hiện tại, 1 = tuần sau, -1 = tuần trước)

**Response:**
```json
{
  "statusCode": 200,
  "message": "Lịch học trong tuần lấy thành công",
  "data": [
    {
      "id": 1,
      "studentId": 10,
      "studentName": "Nguyen Van A",
      "sessionId": 5,
      "sessionSubject": "Toán cao cấp",
      "sessionStartTime": "2024-01-01T08:00:00Z",
      "sessionEndTime": "2024-01-01T10:00:00Z",
      "sessionFormat": "ONLINE",
      "sessionDayOfWeek": "MONDAY",
      "status": "CONFIRMED",
      "registeredDate": "2024-01-01T00:00:00Z",
      "confirmedDate": "2024-01-01T05:00:00Z",
      "updatedDate": "2024-01-01T05:00:00Z",
      "sessionLocation": "Google Meet"
    }
  ]
}
```

---

## 5. SESSION APIs

### 5.1. Get All Sessions (with Pagination)
**Endpoint:** `GET /sessions?page={page}`

**Mô tả:** Lấy danh sách tất cả sessions

**Query Parameters:**
- `page` (optional, default=0): Số trang (bắt đầu từ 0)

**Response:**
```json
{
  "statusCode": 200,
  "message": "Sessions retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "tutorName": "string",
        "studentNames": ["student1", "student2"],
        "subjectName": "Toán cao cấp",
        "startTime": "2024-01-01T08:00:00Z",
        "endTime": "2024-01-01T10:00:00Z",
        "format": "ONLINE",
        "location": "Google Meet",
        "maxQuantity": 10,
        "currentQuantity": 5,
        "updatedDate": "2024-01-01T00:00:00Z"
      }
    ],
    "currentPage": 0,
    "totalPages": 5,
    "totalItems": 50,
    "pageSize": 10
  }
}
```

### 5.2. Create Session
**Endpoint:** `POST /sessions`

**Mô tả:** Tạo session mới (chỉ tutor mới có thể tạo)

**Headers:** `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "tutorId": 1,
  "subjectId": 1,
  "startTime": "2024-01-01T08:00:00Z",
  "endTime": "2024-01-01T10:00:00Z",
  "format": "ONLINE|OFFLINE",
  "location": "string",
  "maxQuantity": 10,
  "dayOfWeek": 1,
  "sessionStatusId": 1
}
```

**Response:**
```json
{
  "statusCode": 200,
  "message": "Session created successfully",
  "data": {
    "id": 1,
    "tutorName": "string",
    "studentNames": [],
    "subjectName": "Toán cao cấp",
    "startTime": "2024-01-01T08:00:00Z",
    "endTime": "2024-01-01T10:00:00Z",
    "format": "ONLINE",
    "location": "Google Meet",
    "maxQuantity": 10,
    "currentQuantity": 0,
    "updatedDate": "2024-01-01T00:00:00Z"
  }
}
```

### 5.3. Update Session
**Endpoint:** `PUT /sessions/{id}`

**Mô tả:** Cập nhật session. Chỉ tutor tạo ra session mới có thể cập nhật.

**Headers:** `Authorization: Bearer {token}`

**Path Parameters:**
- `id`: ID của session cần cập nhật

**Request Body:**
```json
{
  "tutorId": 1,
  "subjectId": 1,
  "startTime": "2024-01-01T08:00:00Z",
  "endTime": "2024-01-01T10:00:00Z",
  "format": "ONLINE|OFFLINE",
  "location": "string",
  "maxQuantity": 10,
  "dayOfWeek": 1,
  "sessionStatusId": 1
}
```

**Response:**
```json
{
  "statusCode": 200,
  "message": "Session updated successfully",
  "data": {
    "id": 1,
    "tutorName": "string",
    "studentNames": ["student1"],
    "subjectName": "Toán cao cấp",
    "startTime": "2024-01-01T08:00:00Z",
    "endTime": "2024-01-01T10:00:00Z",
    "format": "ONLINE",
    "location": "Google Meet",
    "maxQuantity": 10,
    "currentQuantity": 5,
    "updatedDate": "2024-01-01T00:00:00Z"
  }
}
```

**Response (Forbidden - không phải tutor của session):**
```json
{
  "statusCode": 403,
  "message": "Access denied: You can only update your own sessions",
  "data": null
}
```

### 5.4. Delete Session
**Endpoint:** `DELETE /sessions/{id}`

**Mô tả:** Xóa session. Chỉ tutor tạo ra session mới có thể xóa.

**Headers:** `Authorization: Bearer {token}`

**Path Parameters:**
- `id`: ID của session cần xóa

**Response:**
```json
{
  "statusCode": 200,
  "message": "Session deleted successfully",
  "data": null
}
```

**Response (Forbidden - không phải tutor của session):**
```json
{
  "statusCode": 403,
  "message": "Access denied: You can only delete your own sessions",
  "data": null
}
```

---

## 6. DEPARTMENT APIs

### 6.1. Get All Departments
**Endpoint:** `GET /departments`

**Mô tả:** Lấy danh sách tất cả departments (khoa)

**Response:**
```json
{
  "statusCode": 200,
  "message": "Departments retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "Khoa Khoa học và Kỹ thuật Máy tính"
    },
    {
      "id": 2,
      "name": "Khoa Cơ khí"
    }
  ]
}
```

---

## 7. MAJOR APIs

### 7.1. Get All Majors
**Endpoint:** `GET /majors`

**Mô tả:** Lấy danh sách tất cả majors (ngành học)

**Response:**
```json
{
  "statusCode": 200,
  "message": "Majors retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "Khoa học máy tính",
      "majorCode": "CS",
      "programCode": "CS01",
      "note": "string",
      "departmentId": 1,
      "departmentName": "Khoa Khoa học và Kỹ thuật Máy tính"
    }
  ]
}
```

### 7.2. Get Majors by Department
**Endpoint:** `GET /majors/by-department/{departmentId}`

**Mô tả:** Lấy danh sách majors theo department (khoa)

**Path Parameters:**
- `departmentId`: ID của department

**Response:**
```json
{
  "statusCode": 200,
  "message": "Majors retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "Khoa học máy tính",
      "majorCode": "CS",
      "programCode": "CS01",
      "note": "string",
      "departmentId": 1,
      "departmentName": "Khoa Khoa học và Kỹ thuật Máy tính"
    }
  ]
}
```

---

## 8. SUBJECT APIs

### 8.1. Get All Subjects
**Endpoint:** `GET /subjects`

**Mô tả:** Lấy danh sách tất cả subjects (môn học)

**Response:**
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
      "name": "Vật lý đại cương"
    }
  ]
}
```

---

## 9. SESSION STATUS APIs

### 9.1. Get All Session Statuses
**Endpoint:** `GET /session-statuses`

**Mô tả:** Lấy danh sách tất cả trạng thái của session (PENDING, SCHEDULED, CANCELLED, COMPLETED)

**Response:**
```json
{
  "statusCode": 200,
  "message": "Session statuses retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "PENDING",
      "description": "Đang chờ duyệt"
    },
    {
      "id": 2,
      "name": "SCHEDULED",
      "description": "Đã được phê duyệt"
    },
    {
      "id": 3,
      "name": "CANCELLED",
      "description": "Đã bị hủy"
    },
    {
      "id": 4,
      "name": "COMPLETED",
      "description": "Đã hoàn thành"
    }
  ]
}
```

---

## 10. STUDENT SESSION STATUS APIs

### 10.1. Get All Student Session Statuses
**Endpoint:** `GET /student-session-statuses`

**Mô tả:** Lấy danh sách tất cả trạng thái đăng ký của student (PENDING, CONFIRMED, REJECTED)

**Response:**
```json
{
  "statusCode": 200,
  "message": "Student session statuses retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "PENDING",
      "description": "Đang chờ tutor duyệt"
    },
    {
      "id": 2,
      "name": "CONFIRMED",
      "description": "Đã được tutor duyệt"
    },
    {
      "id": 3,
      "name": "REJECTED",
      "description": "Đã bị tutor từ chối"
    }
  ]
}
```

---

## PHỤ LỤC: STRUCTURE CỦA RESPONSE

### BaseResponse Structure
Tất cả API response đều có cấu trúc chung:
```json
{
  "statusCode": 200,
  "message": "Success message",
  "data": {}
}
```

### Pagination Response Structure
Các endpoint có pagination sẽ có cấu trúc data như sau:
```json
{
  "content": [],
  "currentPage": 0,
  "totalPages": 5,
  "totalItems": 50,
  "pageSize": 10
}
```

### Error Response Structure
Khi có lỗi xảy ra, response sẽ có dạng:
```json
{
  "statusCode": 400|401|403|404|500,
  "message": "Error message",
  "data": null
}
```

---

## GHI CHÚ

1. **Authentication**: Hầu hết các API đều yêu cầu Bearer token trong header, trừ các public endpoints (danh sách tutors, departments, majors, subjects, session statuses).

2. **Authorization**: Các API profile và schedule đều check ownership - user chỉ có thể thao tác với dữ liệu của chính mình.

3. **Pagination**: Mặc định page size = 10. Page index bắt đầu từ 0.

4. **Date Format**: Tất cả date/time đều dùng ISO-8601 format (ví dụ: "2024-01-01T08:00:00Z").

5. **Role-based Access**:
   - ADMIN: Có quyền quản lý users, duyệt sessions, duyệt tutor profiles
   - TUTOR: Có quyền tạo/sửa/xóa sessions, duyệt student registrations
   - STUDENT: Có quyền đăng ký sessions, xem lịch học

---

**Ngày tạo**: 29/11/2025  
**Nguồn**: Code thực tế từ package controller  
**Phiên bản**: 1.0

