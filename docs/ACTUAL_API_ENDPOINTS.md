# Tài liệu API Endpoints Thực tế - AdminController

## Tổng quan
Tài liệu này được tạo dựa trên **code thực tế** từ `AdminController.java`, không có phần nào được tự đoán.

Tất cả các response đều có cấu trúc theo `BaseResponse`:
```json
{
  "statusCode": 200,
  "message": "Success message",
  "data": { ... }
}
```

---

## ADMIN ENDPOINTS (`/admin`)

### 1. Xóa user (soft delete)
- **Endpoint**: `DELETE /admin/users/{userId}`
- **Quyền**: ROLE_ADMIN
- **Path Variable**: 
  - `userId` (Integer) - ID của user cần xóa
- **Request Body**: Không có
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

---

### 2. Lấy danh sách tất cả users (có phân trang)
- **Endpoint**: `GET /admin/users`
- **Quyền**: ROLE_ADMIN
- **Query Params**: 
  - `page` (int, default = 0): Số trang (bắt đầu từ 0)
- **Request Body**: Không có
- **Response Success** (200):
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
- **Mô tả**: Lấy danh sách tất cả users với phân trang, mặc định 10 items per page

---

### 3. Lấy thông tin chi tiết user theo userId
- **Endpoint**: `GET /admin/users/{userId}`
- **Quyền**: ROLE_ADMIN
- **Path Variable**: 
  - `userId` (Integer) - ID của user cần lấy thông tin
- **Request Body**: Không có
- **Response Success** (200):
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
- **Mô tả**: Lấy thông tin chi tiết của 1 user theo userId

---

### 4. Lấy danh sách sessions đang chờ duyệt (có phân trang)
- **Endpoint**: `GET /admin/sessions/pending`
- **Quyền**: ROLE_ADMIN
- **Query Params**: 
  - `page` (int, default = 0): Số trang (bắt đầu từ 0)
- **Request Body**: Không có
- **Response Success** (200):
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
- **Mô tả**: Lấy danh sách các session có status = PENDING (chờ admin duyệt), mặc định 10 items per page

---

### 5. Duyệt hoặc từ chối Session
- **Endpoint**: `PUT /admin/sessions/{sessionId}`
- **Quyền**: ROLE_ADMIN
- **Path Variable**: 
  - `sessionId` (Integer) - ID của session cần duyệt/từ chối
- **Query Params**: 
  - `setStatus` (string, required): "SCHEDULED" (approve) hoặc "CANCELLED" (reject)
- **Request Body**: Không có
- **Response Success** (200) - Approve:
```json
{
  "statusCode": 200,
  "message": "Session approved successfully",
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
- **Response Success** (200) - Reject:
```json
{
  "statusCode": 200,
  "message": "Session rejected",
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
- **Mô tả**: 
  - URL approve: `/admin/sessions/{sessionId}?setStatus=SCHEDULED`
  - URL reject: `/admin/sessions/{sessionId}?setStatus=CANCELLED`
  - AdminId được lấy từ token authentication

---

### 6. Lấy danh sách tutor pending (chờ duyệt)
- **Endpoint**: `GET /admin/tutor/pending`
- **Quyền**: ROLE_ADMIN
- **Query Params**: 
  - `page` (int, default = 0): Số trang (bắt đầu từ 0)
- **Request Body**: Không có
- **Response Success** (200):
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
- **Mô tả**: Lấy danh sách các tutor profile có status = PENDING, mặc định 10 items per page. Response trả về dạng Spring Data Page format

---

### 7. Duyệt tutor profile
- **Endpoint**: `PATCH /admin/{userId}/approve`
- **Quyền**: ROLE_ADMIN
- **Path Variable**: 
  - `userId` (Integer) - ID của user (tutor) cần duyệt
- **Request Body**: Không có
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

---

### 8. Từ chối tutor profile
- **Endpoint**: `PATCH /admin/{userId}/reject`
- **Quyền**: ROLE_ADMIN
- **Path Variable**: 
  - `userId` (Integer) - ID của user (tutor) cần từ chối
- **Request Body**: Không có
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
- **Mô tả**: Từ chối profile của tutor, set status = REJECTED

---

## Tổng kết

### Danh sách endpoints
1. `DELETE /admin/users/{userId}` - Xóa user (soft delete)
2. `GET /admin/users` - Lấy danh sách tất cả users (có phân trang)
3. `GET /admin/users/{userId}` - Lấy thông tin chi tiết user theo userId
4. `GET /admin/sessions/pending` - Lấy danh sách sessions đang chờ duyệt (có phân trang)
5. `PUT /admin/sessions/{sessionId}` - Duyệt hoặc từ chối Session
6. `GET /admin/tutor/pending` - Lấy danh sách tutor pending (chờ duyệt)
7. `PATCH /admin/{userId}/approve` - Duyệt tutor profile
8. `PATCH /admin/{userId}/reject` - Từ chối tutor profile

### Quy tắc chung
- Tất cả endpoints đều yêu cầu quyền **ROLE_ADMIN**
- Tất cả response đều theo cấu trúc `BaseResponse` với `statusCode`, `message`, `data`
- Pagination mặc định: 10 items per page, bắt đầu từ page = 0
- Admin ID được lấy tự động từ JWT token trong header Authorization

---

**Version**: 1.0  
**Last Updated**: November 28, 2025  
**Source**: AdminController.java (code thực tế)

