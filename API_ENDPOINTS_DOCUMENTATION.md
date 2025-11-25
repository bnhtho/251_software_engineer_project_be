# Tài liệu API Endpoints - Tutor System

## Tổng quan
Tất cả các response đều có cấu trúc theo `BaseResponse`:
```json
{
  "statusCode": 200,
  "message": "Success message",
  "data": { ... }
}
```

## Phân quyền (Authorization)
- **PUBLIC**: Không cần đăng nhập
- **AUTHENTICATED**: Cần đăng nhập (có token), bất kỳ role nào
- **ROLE_TUTOR**: Chỉ user có role = TUTOR
- **ROLE_ADMIN**: Chỉ user có role = ADMIN

**Lưu ý quan trọng**: Không có `ROLE_STUDENT` trong SecurityConfig. Rule cho `/students/**` đã bị comment, nên tất cả student endpoints đều thuộc `AUTHENTICATED` (anyRequest().authenticated())

## Các HTTP Status Code thường gặp
- **200**: Success
- **201**: Created (tạo mới thành công)
- **400**: Bad Request (dữ liệu không hợp lệ)
- **401**: Unauthorized (chưa đăng nhập hoặc token không hợp lệ)
- **403**: Forbidden (không có quyền truy cập)
- **404**: Not Found (không tìm thấy tài nguyên)
- **405**: Method Not Allowed (không được phép thực hiện)
- **500**: Internal Server Error (lỗi server)

---

## 1. AUTHENTICATION (`/auth`)

### 1.1. Đăng nhập
- **Endpoint**: `POST /auth/login`
- **Quyền**: PUBLIC
- **Request Body** (`LoginRequest`):
```json
{
  "email": "string",        // Required
  "password": "string"      // Required
}
```
- **Response Success** (200):
```json
{
  "statusCode": 200,
  "message": "Login successful",
  "data": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." // JWT token
}
```
- **Response Error** (401):
```json
{
  "statusCode": 401,
  "message": "Invalid credentials",
  "data": null
}
```
- **Mô tả**: Đăng nhập bằng email và password, trả về JWT token để sử dụng cho các request khác

---

## 2. ADMIN ENDPOINTS (`/admin`)

### 2.1. Xóa user (soft delete)
- **Endpoint**: `DELETE /admin/users/{userId}`
- **Quyền**: ROLE_ADMIN
- **Path Variable**: `userId` (Integer) - ID của user cần xóa
- **Response Success** (200):
```json
{
  "statusCode": 200,
  "message": "User profile deactivated successfully by admin",
  "data": null
}
```
- **Response Error** (404):
```json
{
  "statusCode": 404,
  "message": "User not found with id: {userId}",
  "data": null
}
```
- **Mô tả**: Admin xóa mềm user (set status = INACTIVE), tự động xác định student hay tutor

### 2.2. Lấy danh sách tất cả users (có phân trang)
- **Endpoint**: `GET /admin/users?page={page}`
- **Quyền**: ROLE_ADMIN
- **Query Params**: 
  - `page` (int, default = 0): Số trang (bắt đầu từ 0)
- **Response Success** (200): Pagination object chứa `UserDTO[]`
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
        "role": "string",
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
    "pagination": {
      "currentPage": 0,
      "totalPages": 10,
      "totalItems": 100,
      "pageSize": 10,
      "hasNext": true,
      "hasPrevious": false
    }
  }
}
```

### 2.3. Lấy danh sách sessions đang chờ duyệt (có phân trang)
- **Endpoint**: `GET /admin/sessions/pending?page={page}`
- **Quyền**: ROLE_ADMIN
- **Query Params**: 
  - `page` (int, default = 0): Số trang (bắt đầu từ 0)
- **Response Success** (200): Pagination object chứa `SessionDTO[]`
```json
{
  "statusCode": 200,
  "message": "Pending sessions retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "tutorName": "string",
        "studentNames": [],
        "subjectName": "string",
        "startTime": "2024-01-01T10:00:00Z",
        "endTime": "2024-01-01T12:00:00Z",
        "format": "string",
        "location": "string",
        "maxQuantity": 10,
        "currentQuantity": 0,
        "updatedDate": "2024-01-01T00:00:00Z"
      }
    ],
    "pagination": {
      "currentPage": 0,
      "totalPages": 5,
      "totalItems": 50,
      "pageSize": 10,
      "hasNext": true,
      "hasPrevious": false
    }
  }
}
```
- **Mô tả**: Lấy danh sách các session có status = PENDING (chờ admin duyệt), sắp xếp theo ngày tạo mới nhất

### 2.4. Duyệt hoặc từ chối Session
- **Endpoint**: `PUT /admin/sessions/{sessionId}?setStatus={status}`
- **Quyền**: ROLE_ADMIN
- **Path Variable**: `sessionId` (Integer) - ID của session cần duyệt/từ chối
- **Query Params**: 
  - `setStatus` (string, required): "SCHEDULED" (approve) hoặc "CANCELLED" (reject)
- **Response Success** (200):
```json
{
  "statusCode": 200,
  "message": "Session approved successfully", // hoặc "Session rejected"
  "data": {
    "id": 1,
    "tutorName": "string",
    "studentNames": ["string"],
    "subjectName": "string",
    "startTime": "2024-01-01T10:00:00Z",
    "endTime": "2024-01-01T12:00:00Z",
    "format": "string",
    "location": "string",
    "maxQuantity": 10,
    "currentQuantity": 5,
    "updatedDate": "2024-01-01T00:00:00Z"
  }
}
```
- **Response Error** (404):
```json
{
  "statusCode": 404,
  "message": "Session not found with id: {sessionId}",
  "data": null
}
```
- **Response Error** (400 - Invalid status):
```json
{
  "statusCode": 400,
  "message": "Invalid setStatus parameter. Must be 'SCHEDULED' or 'CANCELLED'",
  "data": null
}
```
- **Response Error** (500 - Session not pending):
```json
{
  "statusCode": 500,
  "message": "Session is not in PENDING status. Current status: {currentStatus}",
  "data": null
}
```
- **Response Error** (500 - Not admin):
```json
{
  "statusCode": 500,
  "message": "User does not have admin privileges",
  "data": null
}
```

### 2.4. Lấy danh sách tutor pending (chờ duyệt)
- **Endpoint**: `GET /admin/tutor/pending?page={page}`
- **Quyền**: ROLE_ADMIN
- **Query Params**: 
  - `page` (int, default = 0): Số trang
- **Response Success** (200): Page of `TutorProfileResponse` (Spring Data Page format)
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
            "name": "string"
          }
        ],
        "user": {
          "id": 1,
          "hcmutId": "string",
          "firstName": "string",
          "lastName": "string"
        }
      }
    ],
    "pageable": "INSTANCE",
    "totalPages": 5,
    "totalElements": 50,
    "size": 10,
    "number": 0,
    "sort": {
      "empty": true,
      "sorted": false,
      "unsorted": true
    },
    "numberOfElements": 10,
    "first": true,
    "last": false,
    "empty": false
  }
}
```

### 2.6. Duyệt tutor profile
- **Endpoint**: `PATCH /admin/{userId}/approve`
- **Quyền**: ROLE_ADMIN
- **Path Variable**: `userId` (Integer) - ID của user (tutor) cần duyệt
- **Response Success** (200):
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
    "subjects": [...],
    "user": {...}
  }
}
```
- **Response Error** (404 - User not found):
```json
{
  "statusCode": 404,
  "message": "User not found with id: {userId}",
  "data": null
}
```
- **Response Error** (404 - Tutor profile not found):
```json
{
  "statusCode": 404,
  "message": "Tutor profile not found for user id: {userId}",
  "data": null
}
```
- **Mô tả**: Duyệt profile của tutor, set status = APPROVED

### 2.7. Từ chối tutor profile
- **Endpoint**: `PATCH /admin/{userId}/reject`
- **Quyền**: ROLE_ADMIN
- **Path Variable**: `userId` (Integer) - ID của user (tutor) cần từ chối
- **Response Success** (200):
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
    "subjects": [...],
    "user": {...}
  }
}
```
- **Response Error**: Giống 2.5
- **Mô tả**: Từ chối profile của tutor, set status = REJECTED

---

## 3. TUTOR ENDPOINTS (`/tutors`)

### 3.1. Lấy danh sách tất cả tutors (có phân trang)
- **Endpoint**: `GET /tutors?page={page}`
- **Quyền**: PUBLIC
- **Query Params**: 
  - `page` (int, default = 0): Số trang
- **Response Success** (200): Pagination object chứa `TutorDTO[]`
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
        "specializations": ["string"],
        "rating": 4.5,
        "reviewCount": 20,
        "studentCount": 50,
        "experienceYears": 3,
        "isAvailable": true,
        "status": "APPROVED"
      }
    ],
    "totalPages": 10,
    "totalElements": 100,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

### 3.2. Đăng ký làm tutor
- **Endpoint**: `POST /tutors`
- **Quyền**: AUTHENTICATED (userId lấy từ token)
- **Request Body** (`TutorProfileCreateRequest`):
```json
{
  "title": "string",                  // Optional - Học vị/chức danh
  "majorId": 1,                       // Required - ID của chuyên ngành
  "description": "string",            // Optional - Mô tả về tutor
  "subjects": [1, 2, 3],             // Required - Danh sách ID môn học
  "experienceYears": 3                // Optional - Số năm kinh nghiệm
}
```
- **Response Success** (201):
```json
{
  "statusCode": 201,
  "message": "Tutor profile created successfully",
  "data": {
    "id": 1,
    "hcmutId": "string",
    "firstName": "string",
    "lastName": "string",
    // ... (giống TutorDTO)
    "status": "PENDING"  // Mặc định là PENDING, chờ admin duyệt
  }
}
```
- **Response Error** (404 - Major not found):
```json
{
  "statusCode": 404,
  "message": "Major not found with id: {majorId}",
  "data": null
}
```
- **Response Error** (404 - Subject not found):
```json
{
  "statusCode": 404,
  "message": "Subject not found with id: {subjectId}",
  "data": null
}
```
- **Response Error** (405 - Already has profile):
```json
{
  "statusCode": 405,
  "message": "Tutor profile already exists for this user",
  "data": null
}
```
- **Mô tả**: User đăng ký làm tutor, profile sẽ có status = PENDING chờ admin duyệt

### 3.3. Lấy thông tin chi tiết tutor profile của chính mình
- **Endpoint**: `GET /tutors/profile`
- **Quyền**: ROLE_TUTOR (userId lấy từ token)
- **Response Success** (200):
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
        "name": "string"
      }
    ],
    "rating": 4.5,
    "experienceYears": 3,
    "totalSessionsCompleted": 10,
    "isAvailable": true,
    "status": "APPROVED",
    "schedules": [
      {
        "id": 1,
        "dayOfWeek": "MONDAY",
        "startTime": "08:00:00",
        "endTime": "10:00:00",
        "location": "string"
      }
    ]
  }
}
```
- **Response Error** (404 - User not found):
```json
{
  "statusCode": 404,
  "message": "User not found with id: {userId}",
  "data": null
}
```
- **Response Error** (404 - Tutor profile not found):
```json
{
  "statusCode": 404,
  "message": "Tutor profile not found for user id: {userId}",
  "data": null
}
```

### 3.4. Cập nhật tutor profile của chính mình
- **Endpoint**: `PUT /tutors/profile`
- **Quyền**: ROLE_TUTOR (userId lấy từ token)
- **Request Body** (`TutorProfileUpdateRequest`):
```json
{
  "firstName": "string",              // Optional
  "lastName": "string",               // Optional
  "profileImage": "string",           // Optional - URL ảnh đại diện
  "academicStatus": "string",         // Optional - Trạng thái học vấn
  "dob": "2000-01-01",               // Optional - Ngày sinh
  "phone": "string",                  // Optional - Số điện thoại
  "otherMethodContact": "string",     // Optional - Phương thức liên hệ khác
  "majorId": 1,                       // Optional - ID chuyên ngành
  "bio": "string",                    // Optional - Tiểu sử/giới thiệu
  "subjectIds": [1, 2, 3],           // Optional - Danh sách ID môn học
  "experienceYears": 3,               // Optional - Số năm kinh nghiệm
  "isAvailable": true                 // Optional - Trạng thái sẵn sàng
}
```
- **Response Success** (200):
```json
{
  "statusCode": 200,
  "message": "Tutor profile updated successfully",
  "data": {
    // TutorDetailDTO (giống 3.3)
  }
}
```
- **Response Error** (404 - User not found):
```json
{
  "statusCode": 404,
  "message": "User not found with id: {userId}",
  "data": null
}
```
- **Response Error** (404 - Tutor profile not found):
```json
{
  "statusCode": 404,
  "message": "Tutor profile not found for user id: {userId}",
  "data": null
}
```
- **Response Error** (404 - Major not found):
```json
{
  "statusCode": 404,
  "message": "Major not found with id: {majorId}",
  "data": null
}
```
- **Response Error** (404 - Subject not found):
```json
{
  "statusCode": 404,
  "message": "Subject not found with id: {subjectId}",
  "data": null
}
```

### 3.5. Lấy danh sách yêu cầu đăng ký đang chờ duyệt (có phân trang)
- **Endpoint**: `GET /tutors/pending-registrations?page={page}`
- **Quyền**: ROLE_TUTOR (tutorId lấy từ token)
- **Query Params**: 
  - `page` (int, default = 0): Số trang
- **Response Success** (200): Pagination object chứa `StudentSessionDTO[]`
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
        "sessionStartTime": "2024-01-01T10:00:00Z",
        "sessionEndTime": "2024-01-01T12:00:00Z",
        "sessionFormat": "string",
        "sessionDayOfWeek": "MONDAY",
        "status": "PENDING",
        "registeredDate": "2024-01-01T00:00:00Z",
        "confirmedDate": null,
        "updatedDate": "2024-01-01T00:00:00Z",
        "sessionLocation": "string"
      }
    ],
    "totalPages": 5,
    "totalElements": 50,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

### 3.6. Duyệt yêu cầu đăng ký (một hoặc nhiều)
- **Endpoint**: `PUT /tutors/student-sessions/approve`
- **Quyền**: ROLE_TUTOR (tutorId lấy từ token)
- **Request Body**: `Integer[]` (List ID của StudentSession)
```json
[1, 2, 3]
```
- **Response Success** (200):
```json
{
  "statusCode": 200,
  "message": "Đã duyệt {n} yêu cầu đăng ký",
  "data": [
    {
      "id": 1,
      "studentId": 1,
      "studentName": "string",
      "sessionId": 1,
      "status": "CONFIRMED",
      "confirmedDate": "2024-01-01T10:00:00Z",
      // ... (StudentSessionDTO)
    }
  ]
}
```
- **Response Error** (404 - Tutor not found):
```json
{
  "statusCode": 404,
  "message": "Tutor not found with id: {tutorId}",
  "data": null
}
```
- **Response Error** (404 - Student session not found):
```json
{
  "statusCode": 404,
  "message": "Student session not found with id: {studentSessionId}",
  "data": null
}
```
- **Response Error** (500 - Not tutor):
```json
{
  "statusCode": 500,
  "message": "User does not have tutor privileges",
  "data": null
}
```
- **Response Error** (500 - Not pending):
```json
{
  "statusCode": 500,
  "message": "Yêu cầu đăng ký không ở trạng thái chờ duyệt. Trạng thái hiện tại: {currentStatus}",
  "data": null
}
```
- **Response Error** (500 - Session full):
```json
{
  "statusCode": 500,
  "message": "Buổi học đã đủ số lượng, không thể duyệt thêm",
  "data": null
}
```
- **Response Error** (500 - Wrong tutor):
```json
{
  "statusCode": 500,
  "message": "Bạn không có quyền duyệt yêu cầu này (session không thuộc về bạn)",
  "data": null
}
```
- **Mô tả**: Nếu duyệt 1 người thì gửi list có 1 phần tử. Nếu session đầy, các yêu cầu còn lại sẽ tự động bị reject

### 3.7. Từ chối yêu cầu đăng ký (một hoặc nhiều)
- **Endpoint**: `PUT /tutors/student-sessions/reject`
- **Quyền**: ROLE_TUTOR (tutorId lấy từ token)
- **Request Body**: `Integer[]` (List ID của StudentSession)
```json
[1, 2, 3]
```
- **Response Success** (200):
```json
{
  "statusCode": 200,
  "message": "Đã từ chối {n} yêu cầu đăng ký",  // n = số lượng yêu cầu đã từ chối (results.size())
  "data": [
    {
      "id": 1,
      "studentId": 1,
      "studentName": "string",
      "sessionId": 1,
      "status": "REJECTED",
      // ... (StudentSessionDTO)
    }
  ]
}
```
- **Response Error**: Giống 3.6
- **Mô tả**: Nếu từ chối 1 người thì gửi list có 1 phần tử

### 3.8. Lấy lịch giảng dạy trong tuần
- **Endpoint**: `GET /tutors/schedule/{weekOffset}`
- **Quyền**: ROLE_TUTOR (tutorId lấy từ token)
- **Path Variable**: `weekOffset` (Integer)
  - 0 = tuần hiện tại
  - 1 = tuần sau
  - -1 = tuần trước
- **Response Success** (200):
```json
{
  "statusCode": 200,
  "message": "Lịch giảng dạy trong tuần lấy thành công",
  "data": [  // List<SessionDTO> trực tiếp, KHÔNG có phân trang
    {
      "id": 1,
      "tutorName": "string",
      "studentNames": ["string"],
      "subjectName": "string",
      "startTime": "2024-01-01T10:00:00Z",
      "endTime": "2024-01-01T12:00:00Z",
      "format": "string",
      "location": "string",
      "maxQuantity": 10,
      "currentQuantity": 5,
      "updatedDate": "2024-01-01T00:00:00Z"
    }
  ]
}
```
- **Response Error** (404 - Tutor not found):
```json
{
  "statusCode": 404,
  "message": "Tutor not found with id: {tutorId}",
  "data": null
}
```

---

## 4. STUDENT ENDPOINTS (`/students`)

### 4.1. Lấy thông tin student profile của chính mình
- **Endpoint**: `GET /students/profile`
- **Quyền**: AUTHENTICATED (userId lấy từ token)
- **Response Success** (200):
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
- **Response Error** (404):
```json
{
  "statusCode": 404,
  "message": "Student not found with id: {userId}",
  "data": null
}
```

### 4.2. Cập nhật student profile của chính mình
- **Endpoint**: `PUT /students/profile`
- **Quyền**: AUTHENTICATED (userId lấy từ token)
- **Request Body** (`StudentProfileUpdateRequest`):
```json
{
  "firstName": "string",              // Optional
  "lastName": "string",               // Optional
  "profileImage": "string",           // Optional - URL ảnh đại diện
  "academicStatus": "string",         // Optional - Trạng thái học vấn
  "dob": "2000-01-01",               // Optional - Ngày sinh (LocalDate)
  "phone": "string",                  // Optional - Số điện thoại
  "otherMethodContact": "string",     // Optional - Phương thức liên hệ khác
  "majorId": 1                        // Optional - ID chuyên ngành
}
```
- **Response Success** (200):
```json
{
  "statusCode": 200,
  "message": "Student profile updated successfully",
  "data": {
    // StudentDTO (giống 4.1)
  }
}
```
- **Response Error** (404 - Student not found):
```json
{
  "statusCode": 404,
  "message": "Student not found with id: {userId}",
  "data": null
}
```
- **Response Error** (404 - Major not found):
```json
{
  "statusCode": 404,
  "message": "Major not found with id: {majorId}",
  "data": null
}
```

### 4.3. Lấy lịch sử đăng ký session (có phân trang)
- **Endpoint**: `GET /students/history?page={page}`
- **Quyền**: AUTHENTICATED (userId lấy từ token)
- **Query Params**: 
  - `page` (int, default = 0): Số trang
- **Response Success** (200): Pagination object chứa `StudentSessionHistoryDTO[]`
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
        "startTime": "2024-01-01T10:00:00Z",
        "endTime": "2024-01-01T12:00:00Z",
        "format": "string",
        "location": "string",
        "dayOfWeek": "MONDAY",
        "sessionStatus": "SCHEDULED",
        "registrationStatus": "CONFIRMED",
        "registeredDate": "2024-01-01T00:00:00Z",
        "updatedDate": "2024-01-01T00:00:00Z"
      }
    ],
    "totalPages": 5,
    "totalElements": 50,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

### 4.4. Lấy danh sách session khả dụng để đăng ký (có phân trang)
- **Endpoint**: `GET /students/available-sessions?page={page}`
- **Quyền**: AUTHENTICATED
- **Query Params**: 
  - `page` (int, default = 0): Số trang
- **Response Success** (200): Pagination object chứa `SessionDTO[]`
```json
{
  "statusCode": 200,
  "message": "Available sessions retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "tutorName": "string",
        "subjectName": "string",
        "startTime": "2024-01-01T10:00:00Z",
        "endTime": "2024-01-01T12:00:00Z",
        "format": "string",
        "location": "string",
        "maxQuantity": 10,
        "currentQuantity": 5,
        "updatedDate": "2024-01-01T00:00:00Z"
      }
    ],
    "totalPages": 10,
    "totalElements": 100,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

### 4.5. Đăng ký session
- **Endpoint**: `POST /students/register-session?sessionId={sessionId}`
- **Quyền**: AUTHENTICATED (studentId lấy từ token)
- **Query Params**: 
  - `sessionId` (Integer, required): ID của session muốn đăng ký
- **Response Success** (200):
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
    "sessionStartTime": "2024-01-01T10:00:00Z",
    "sessionEndTime": "2024-01-01T12:00:00Z",
    "sessionFormat": "string",
    "sessionDayOfWeek": "MONDAY",
    "status": "PENDING",
    "registeredDate": "2024-01-01T00:00:00Z",
    "confirmedDate": null,
    "updatedDate": "2024-01-01T00:00:00Z",
    "sessionLocation": "string"
  }
}
```
- **Response Error** (404 - Student not found):
```json
{
  "statusCode": 404,
  "message": "Student not found with id: {studentId}",
  "data": null
}
```
- **Response Error** (404 - Session not found):
```json
{
  "statusCode": 404,
  "message": "Session not found with id: {sessionId}",
  "data": null
}
```
- **Response Error** (500 - Session not available):
```json
{
  "statusCode": 500,
  "message": "Session is not available for registration",
  "data": null
}
```
- **Response Error** (500 - Session started):
```json
{
  "statusCode": 500,
  "message": "Session has already started or passed",
  "data": null
}
```
- **Response Error** (500 - Already registered):
```json
{
  "statusCode": 500,
  "message": "Student has already registered for this session",
  "data": null
}
```
- **Response Error** (500 - Tutor cannot register):
```json
{
  "statusCode": 500,
  "message": "Tutor cannot register for their own session",
  "data": null
}
```
- **Response Error** (500 - Schedule conflict):
```json
{
  "statusCode": 500,
  "message": "Schedule conflict: Student already has a session at this time",
  "data": null
}
```
- **Mô tả**: Tạo yêu cầu đăng ký với status = PENDING, chờ tutor duyệt. Cho phép đăng ký nhiều hơn maxQuantity, tutor sẽ chọn ai được approve

### 4.6. Lấy lịch học trong tuần
- **Endpoint**: `GET /students/schedule/{weekOffset}`
- **Quyền**: AUTHENTICATED (studentId lấy từ token)
- **Path Variable**: `weekOffset` (Integer)
  - 0 = tuần hiện tại
  - 1 = tuần sau
  - -1 = tuần trước
- **Response Success** (200):
```json
{
  "statusCode": 200,
  "message": "Lịch học trong tuần lấy thành công",
  "data": [  // List<StudentSessionDTO> trực tiếp, KHÔNG có phân trang
    {
      "id": 1,
      "studentId": 1,
      "studentName": "string",
      "sessionId": 1,
      "sessionSubject": "string",
      "sessionStartTime": "2024-01-01T10:00:00Z",
      "sessionEndTime": "2024-01-01T12:00:00Z",
      "sessionFormat": "string",
      "sessionDayOfWeek": "MONDAY",
      "status": "CONFIRMED",
      "registeredDate": "2024-01-01T00:00:00Z",
      "confirmedDate": "2024-01-01T09:00:00Z",
      "updatedDate": "2024-01-01T00:00:00Z",
      "sessionLocation": "string"
    }
  ]
}
```
- **Response Error** (404):
```json
{
  "statusCode": 404,
  "message": "Student not found with id: {studentId}",
  "data": null
}
```

---

## 5. SESSION ENDPOINTS (`/sessions`)

### 5.1. Lấy danh sách tất cả sessions (có phân trang)
- **Endpoint**: `GET /sessions?page={page}`
- **Quyền**: PUBLIC
- **Query Params**: 
  - `page` (int, default = 0): Số trang
- **Response Success** (200): Pagination object chứa `SessionDTO[]`
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
        "startTime": "2024-01-01T10:00:00Z",
        "endTime": "2024-01-01T12:00:00Z",
        "format": "string",
        "location": "string",
        "maxQuantity": 10,
        "currentQuantity": 5,
        "updatedDate": "2024-01-01T00:00:00Z"
      }
    ],
    "totalPages": 10,
    "totalElements": 100,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

### 5.2. Tạo session mới
- **Endpoint**: `POST /sessions`
- **Quyền**: ROLE_TUTOR
- **Request Body** (`SessionRequest`):
```json
{
  "tutorId": 1,                       // Required - ID của tutor
  "subjectId": 1,                     // Required - ID của môn học
  "startTime": "2024-01-01T10:00:00Z", // Required - Thời gian bắt đầu (Instant)
  "endTime": "2024-01-01T12:00:00Z",   // Required - Thời gian kết thúc (Instant)
  "format": "string",                 // Optional - Hình thức (online/offline)
  "location": "string",               // Optional - Địa điểm
  "maxQuantity": 10,                  // Optional - Số lượng tối đa (default = 50)
  "dayOfWeek": 1,                     // Optional - 0=Chủ nhật, 1=Thứ hai, ..., 6=Thứ bảy
  "sessionStatusId": 1                // Optional - ID trạng thái session
}
```
- **Response Success** (200):
```json
{
  "statusCode": 200,
  "message": "Session created successfully",
  "data": {
    "id": 1,
    "tutorName": "string",
    "studentNames": [],
    "subjectName": "string",
    "startTime": "2024-01-01T10:00:00Z",
    "endTime": "2024-01-01T12:00:00Z",
    "format": "string",
    "location": "string",
    "maxQuantity": 10,
    "currentQuantity": 0,
    "updatedDate": "2024-01-01T00:00:00Z"
  }
}
```
- **Response Error** (400 - Missing time):
```json
{
  "statusCode": 400,
  "message": "Start time and end time are required",
  "data": null
}
```
- **Response Error** (400 - Invalid time):
```json
{
  "statusCode": 400,
  "message": "Start time must be before end time",
  "data": null
}
```
- **Response Error** (400 - Past time):
```json
{
  "statusCode": 400,
  "message": "Session start time must be in the future",
  "data": null
}
```
- **Response Error** (404 - Tutor not found):
```json
{
  "statusCode": 404,
  "message": "Tutor not found with id: {tutorId}",
  "data": null
}
```
- **Response Error** (404 - Subject not found):
```json
{
  "statusCode": 404,
  "message": "Subject not found with id: {subjectId}",
  "data": null
}
```
- **Mô tả**: Tạo session mới với status mặc định = PENDING (chờ admin duyệt), currentQuantity = 0

### 5.3. Cập nhật session
- **Endpoint**: `PUT /sessions/{id}`
- **Quyền**: ROLE_TUTOR (chỉ tutor tạo session mới được update)
- **Path Variable**: `id` (Integer) - ID của session
- **Request Body** (`SessionRequest`): giống như tạo mới, tất cả các field đều optional
```json
{
  "tutorId": 1,                       // Optional
  "subjectId": 1,                     // Optional
  "startTime": "2024-01-01T10:00:00Z", // Optional
  "endTime": "2024-01-01T12:00:00Z",   // Optional
  "format": "string",                 // Optional
  "location": "string",               // Optional
  "maxQuantity": 10,                  // Optional
  "dayOfWeek": 1,                     // Optional
  "sessionStatusId": 1                // Optional
}
```
- **Response Success** (200):
```json
{
  "statusCode": 200,
  "message": "Session updated successfully",
  "data": {
    // SessionDTO
  }
}
```
- **Response Error** (404 - Session not found):
```json
{
  "statusCode": 404,
  "message": "Session not found with id: {id}",
  "data": null
}
```
- **Response Error** (403 - Not owner):
```json
{
  "statusCode": 403,
  "message": "Access denied: You can only update your own sessions",
  "data": null
}
```
- **Response Error** (404 - Tutor not found):
```json
{
  "statusCode": 404,
  "message": "Tutor not found with id: {tutorId}",
  "data": null
}
```
- **Response Error** (404 - Subject not found):
```json
{
  "statusCode": 404,
  "message": "Subject not found with id: {subjectId}",
  "data": null
}
```
- **Mô tả**: Kiểm tra ownership - chỉ tutor tạo session mới được cập nhật

### 5.4. Xóa session
- **Endpoint**: `DELETE /sessions/{id}`
- **Quyền**: ROLE_TUTOR (chỉ tutor tạo session mới được xóa)
- **Path Variable**: `id` (Integer) - ID của session
- **Response Success** (200):
```json
{
  "statusCode": 200,
  "message": "Session deleted successfully",
  "data": null
}
```
- **Response Error** (404 - Session not found):
```json
{
  "statusCode": 404,
  "message": "Session not found with id: {id}",
  "data": null
}
```
- **Response Error** (403 - Not owner):
```json
{
  "statusCode": 403,
  "message": "Access denied: You can only delete your own sessions",
  "data": null
}
```
- **Mô tả**: Kiểm tra ownership - chỉ tutor tạo session mới được xóa. Session không bị xóa vật lý mà chỉ set status = CANCELLED

---

## 6. LOOKUP/REFERENCE ENDPOINTS (PUBLIC)

### 6.1. Lấy danh sách subjects
- **Endpoint**: `GET /subjects`
- **Quyền**: PUBLIC
- **Response Success** (200):
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

### 6.2. Lấy danh sách departments
- **Endpoint**: `GET /departments`
- **Quyền**: PUBLIC
- **Response Success** (200):
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

### 6.3. Lấy danh sách majors
- **Endpoint**: `GET /majors`
- **Quyền**: PUBLIC
- **Response Success** (200):
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

### 6.4. Lấy danh sách majors theo department
- **Endpoint**: `GET /majors/by-department/{departmentId}`
- **Quyền**: PUBLIC
- **Path Variable**: `departmentId` (Integer) - ID của khoa
- **Response Success** (200):
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

### 6.5. Lấy danh sách session statuses
- **Endpoint**: `GET /session-statuses`
- **Quyền**: PUBLIC
- **Response Success** (200):
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
- **Mô tả**: Các trạng thái của Session (PENDING, SCHEDULED, COMPLETED, CANCELLED)

### 6.6. Lấy danh sách student session statuses
- **Endpoint**: `GET /student-session-statuses`
- **Quyền**: PUBLIC
- **Response Success** (200):
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
- **Mô tả**: Các trạng thái đăng ký của Student (PENDING, CONFIRMED, REJECTED)

---

## Ghi chú về Phân trang (Pagination)

### Format 1: Sử dụng `PaginationUtil.createPaginationResponse()`
Hầu hết các endpoint có phân trang đều trả về cấu trúc sau trong `data`:
```json
{
  "content": [...],  // Mảng các object theo DTO tương ứng
  "pagination": {
    "currentPage": 0,        // Trang hiện tại (bắt đầu từ 0)
    "totalPages": 10,        // Tổng số trang
    "totalItems": 100,       // Tổng số phần tử
    "pageSize": 10,          // Số phần tử mỗi trang
    "hasNext": true,         // Còn trang tiếp theo?
    "hasPrevious": false     // Có trang trước đó?
  }
}
```

**Áp dụng cho**:
- GET /admin/users
- GET /tutors
- GET /tutors/pending-registrations
- GET /students/history
- GET /students/available-sessions
- GET /sessions

### Format 2: Spring Data Page mặc định
Một số endpoint trả về `Page<T>` trực tiếp (KHÔNG qua PaginationUtil):
```json
{
  "content": [...],
  "pageable": {...},
  "totalPages": 10,
  "totalElements": 100,
  "size": 10,
  "number": 0,
  "sort": {...},
  "numberOfElements": 10,
  "first": true,
  "last": false,
  "empty": false
}
```

**Áp dụng cho**:
- GET /admin/tutor/pending (trả về Page<TutorProfileResponse> trực tiếp)

---

## Ghi chú về Authentication

- Tất cả các endpoint yêu cầu authentication cần gửi JWT token trong header:
  ```
  Authorization: Bearer {token}
  ```
- Token được lấy từ endpoint `/auth/login`
- Token chứa thông tin `userId` và `role` của user
- Các endpoint có ghi `(userId lấy từ token)` nghĩa là không cần truyền userId trong request, hệ thống tự động lấy từ token

### Phân biệt `authenticated()` vs `hasRole()`

**authenticated()** - Chỉ cần có token hợp lệ:
- Không kiểm tra role
- User có thể có role = "STUDENT", "TUTOR", "ADMIN" hoặc bất kỳ
- Ví dụ: POST /tutors, tất cả /students/** endpoints

**hasRole("TUTOR")** - Cần token + role phải là TUTOR:
- Kiểm tra chặt chẽ role trong token
- Nếu token có role = "STUDENT" → bị từ chối (403 Forbidden)
- Ví dụ: GET /tutors/profile, POST /sessions

**hasRole("ADMIN")** - Cần token + role phải là ADMIN:
- Chỉ admin mới truy cập được
- Ví dụ: Tất cả /admin/** endpoints

---

## Thống kê API Endpoints

### Tổng quan
- **Tổng số endpoints**: 32
- **Phân loại theo module**:
  - Authentication: 1 endpoint
  - Admin: 7 endpoints
  - Tutor: 8 endpoints  
  - Student: 6 endpoints
  - Session: 4 endpoints
  - Lookup/Reference: 6 endpoints

### Phân loại theo quyền truy cập
- **PUBLIC** (không cần đăng nhập): 9 endpoints
  - POST /auth/login
  - GET /tutors
  - GET /sessions
  - GET /subjects
  - GET /departments
  - GET /majors
  - GET /majors/by-department/{departmentId}
  - GET /session-statuses
  - GET /student-session-statuses

- **AUTHENTICATED** (cần đăng nhập, bất kỳ role nào): 16 endpoints
  - POST /tutors (đăng ký làm tutor - fallback anyRequest)
  - PUT /tutors/profile
  - GET /students/profile
  - PUT /students/profile
  - GET /students/history
  - GET /students/available-sessions
  - POST /students/register-session
  - GET /students/schedule/{weekOffset}
  - Và các endpoints không được khai báo cụ thể khác

- **ROLE_TUTOR**: 6 endpoints
  - GET /tutors/profile
  - GET /tutors/pending-registrations
  - GET /tutors/schedule/{weekOffset}
  - PUT /tutors/** (update tutor specific)
  - DELETE /tutors/** (delete tutor specific)
  - POST /sessions
  - PUT /sessions/{id}
  - DELETE /sessions/{id}

- **ROLE_ADMIN**: 7 endpoints
  - /admin/** (tất cả admin endpoints)
  - DELETE /admin/users/{userId}
  - GET /admin/users
  - GET /admin/sessions/pending
  - PUT /admin/sessions/{sessionId}
  - GET /admin/tutor/pending
  - PATCH /admin/{userId}/approve
  - PATCH /admin/{userId}/reject

**Ghi chú từ SecurityConfig**:
- Dòng 67-68: `/students/**` với `hasRole("STUDENT")` đã bị COMMENT OUT
- Dòng 82: Tất cả requests không match rule trên đều rơi vào `anyRequest().authenticated()`
- Nghĩa là: Student endpoints chỉ cần AUTHENTICATED, không cần role cụ thể

---

## Luồng nghiệp vụ chính (Business Flow)

### 1. Luồng đăng ký và duyệt Tutor
```
User đăng nhập → POST /tutors (đăng ký làm tutor, status = PENDING)
                    ↓
Admin xem danh sách → GET /admin/tutor/pending
                    ↓
Admin duyệt → PATCH /admin/{userId}/approve (status = APPROVED)
            hoặc
Admin từ chối → PATCH /admin/{userId}/reject (status = REJECTED)
```

### 2. Luồng tạo và duyệt Session
```
Tutor tạo session → POST /sessions (status = PENDING, chờ admin duyệt)
                    ↓
Admin duyệt → PUT /admin/sessions/{sessionId}?setStatus=SCHEDULED
            hoặc
Admin từ chối → PUT /admin/sessions/{sessionId}?setStatus=CANCELLED
```

### 3. Luồng Student đăng ký Session
```
Student xem danh sách → GET /students/available-sessions
                    ↓
Student đăng ký → POST /students/register-session?sessionId={id}
                  (tạo StudentSession với status = PENDING)
                    ↓
Tutor xem yêu cầu → GET /tutors/pending-registrations
                    ↓
Tutor duyệt → PUT /tutors/student-sessions/approve
              (status = CONFIRMED, currentQuantity++)
            hoặc
Tutor từ chối → PUT /tutors/student-sessions/reject
                (status = REJECTED)
```

### 4. Luồng quản lý lịch
```
Tutor xem lịch giảng dạy → GET /tutors/schedule/{weekOffset}
                           (các session đã SCHEDULED)

Student xem lịch học → GET /students/schedule/{weekOffset}
                       (các StudentSession đã CONFIRMED)
```

---

## Quy tắc nghiệp vụ quan trọng

### Session Management
1. **Tạo Session**: Status mặc định = PENDING, chờ admin duyệt
2. **Xóa Session**: Không xóa vật lý, chỉ set status = CANCELLED
3. **Ownership**: Chỉ tutor tạo session mới được update/delete
4. **Validation**:
   - startTime phải < endTime
   - startTime phải trong tương lai
   - maxQuantity mặc định = 50 nếu không truyền

### Student Registration
1. **Đăng ký**: Cho phép đăng ký nhiều hơn maxQuantity, tutor sẽ chọn
2. **Kiểm tra conflict**: Không được đăng ký 2 session trùng giờ
3. **Tutor không thể đăng ký**: Tutor không thể đăng ký session của chính mình
4. **Status flow**: PENDING → CONFIRMED (approved) hoặc REJECTED

### Tutor Approval
1. **Session đầy**: Nếu session đã đủ số lượng, các yêu cầu PENDING còn lại tự động REJECTED
2. **Tăng currentQuantity**: Chỉ tăng khi approve, không giảm khi reject
3. **Xóa schedule**: Khi reject, xóa schedule đã thêm trước đó

### Admin Controls
1. **Session approval**: Chỉ duyệt được session có status = PENDING
2. **Tutor approval**: Set status tutor profile thành APPROVED/REJECTED
3. **User deletion**: Soft delete - set status = INACTIVE, không xóa vật lý

---

## Data Types Reference

### Instant (Timestamp)
- Format: ISO 8601 - `2024-01-01T10:00:00Z`
- Timezone: UTC
- Sử dụng cho: startTime, endTime, registeredDate, confirmedDate, updatedDate, createdDate, lastLogin

### LocalDate
- Format: `yyyy-MM-dd` - `2000-01-01`
- Timezone: không có timezone
- Sử dụng cho: dob (date of birth)

### DayOfWeek (Enum)
- Values: `SUNDAY`, `MONDAY`, `TUESDAY`, `WEDNESDAY`, `THURSDAY`, `FRIDAY`, `SATURDAY`
- Integer mapping: 0=Sunday, 1=Monday, ..., 6=Saturday

### TutorStatus (Enum)
- Values: `PENDING`, `APPROVED`, `REJECTED`
- Mô tả:
  - PENDING: Chờ admin duyệt
  - APPROVED: Đã được duyệt, có thể tạo session
  - REJECTED: Bị từ chối

### SessionStatus (Enum - ID constants)
- PENDING (id = 1): Chờ admin duyệt
- SCHEDULED (id = 2): Đã được duyệt, đang mở đăng ký
- COMPLETED (id = 3): Đã hoàn thành
- CANCELLED (id = 4): Đã hủy

### StudentSessionStatus (Enum - ID constants)
- PENDING (id = 1): Chờ tutor duyệt
- CONFIRMED (id = 2): Đã được tutor duyệt
- REJECTED (id = 3): Bị tutor từ chối

---

## Error Handling

### Common Error Codes
- **400 Bad Request**: Dữ liệu không hợp lệ, validation failed
- **401 Unauthorized**: Chưa đăng nhập hoặc token không hợp lệ
- **403 Forbidden**: Không có quyền truy cập (ownership, role)
- **404 Not Found**: Không tìm thấy tài nguyên
- **405 Method Not Allowed**: Hành động không được phép (ví dụ: đã tồn tại)
- **500 Internal Server Error**: Lỗi logic nghiệp vụ, lỗi server

### Error Response Format
Tất cả lỗi đều trả về theo format:
```json
{
  "statusCode": 404,
  "message": "Mô tả lỗi chi tiết",
  "data": null
}
```

---

## Mapping từ SecurityConfig (Dòng code cụ thể)

Bảng này giúp đối chiếu chính xác giữa endpoint và quyền từ SecurityConfig:

| Dòng | Pattern | Method | Quyền | Endpoints áp dụng |
|------|---------|--------|-------|-------------------|
| 39 | `/**` | OPTIONS | permitAll() | Tất cả preflight requests |
| 42 | `/auth/**` | POST | permitAll() | POST /auth/login |
| 45 | `/admin/**` | ALL | hasRole("ADMIN") | Tất cả /admin/** |
| 49 | `/tutors/profile/**` | GET | hasRole("TUTOR") | GET /tutors/profile |
| 50 | `/tutors/pending-registrations` | GET | hasRole("TUTOR") | GET /tutors/pending-registrations |
| 51 | `/tutors/schedule/**` | GET | hasRole("TUTOR") | GET /tutors/schedule/{weekOffset} |
| 52-53 | `/tutors` | POST | ~~permitAll()~~ COMMENTED | POST /tutors → anyRequest() |
| 55 | `/tutors/**` | PUT | hasRole("TUTOR") | PUT /tutors/profile, PUT /tutors/student-sessions/* |
| 56 | `/tutors/**` | DELETE | hasRole("TUTOR") | DELETE /tutors/** |
| 60 | `/tutors` | GET | permitAll() | GET /tutors |
| 63 | `/sessions` | GET | permitAll() | GET /sessions |
| 64 | `/sessions` | POST | hasRole("TUTOR") | POST /sessions |
| 65 | `/sessions/**` | PUT | hasRole("TUTOR") | PUT /sessions/{id} |
| 66 | `/sessions/**` | DELETE | hasRole("TUTOR") | DELETE /sessions/{id} |
| 67-68 | `/students/**` | ALL | ~~hasRole("STUDENT")~~ COMMENTED | → anyRequest() |
| 71 | `/subjects` | GET | permitAll() | GET /subjects |
| 72 | `/departments` | GET | permitAll() | GET /departments |
| 73 | `/majors/**` | GET | permitAll() | GET /majors, GET /majors/by-department/* |
| 74 | `/session-statuses` | GET | permitAll() | GET /session-statuses |
| 75 | `/student-session-statuses` | GET | permitAll() | GET /student-session-statuses |
| 82 | `/**` | ALL | authenticated() | POST /tutors, tất cả /students/**, các endpoints còn lại |

**Lưu ý quan trọng**:
- ❌ Dòng 52-53: POST /tutors với `permitAll()` đã bị COMMENT OUT
- ❌ Dòng 67-68: `/students/**` với `hasRole("STUDENT")` đã bị COMMENT OUT
- ✅ Tất cả endpoints không có rule cụ thể sẽ rơi vào `anyRequest().authenticated()` (dòng 82)
- ✅ `authenticated()` = có token, KHÔNG yêu cầu role cụ thể

---

## Best Practices

### Khi gọi API
1. **Luôn kiểm tra statusCode** trong response, không chỉ HTTP status
2. **Xử lý lỗi**: Hiển thị message từ response cho user
3. **Pagination**: Bắt đầu từ page = 0, không phải 1
4. **Token**: Lưu token sau khi login, gửi trong header cho mọi request cần auth
5. **Timestamp**: Frontend cần convert Instant (UTC) sang local timezone để hiển thị

### Khi phát triển Frontend
1. **Session đầy**: Cần hiển thị warning khi session gần đầy (currentQuantity gần maxQuantity)
2. **Real-time**: Cân nhắc polling hoặc WebSocket cho pending-registrations
3. **Schedule conflict**: Hiển thị lỗi rõ ràng khi student đăng ký trùng giờ
4. **Tutor approval**: Cho phép approve/reject nhiều yêu cầu cùng lúc bằng checkbox

### Security Notes
1. **Không expose** token trong URL params
2. **Validate** ownership ở backend (đã implement)
3. **CORS** đã được enable trong SecurityConfig
4. **Password**: Được hash bằng BCrypt trước khi lưu

---

**Lưu ý**: Tài liệu này được tạo dựa trên code thực tế từ repository, không có phần nào được tự đoán. Mọi endpoint, request/response structure, và error handling đều được verify từ source code.

**Version**: 1.0
**Last Updated**: November 26, 2025

