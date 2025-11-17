# Hướng Dẫn API Profile cho Frontend

## Cấu Trúc Response Chung

Tất cả API trả về theo format:
```
{
  statusCode: number,
  message: string,
  data: object | array | null
}
```

---

## 1. STUDENT APIs

### 1.1. Lấy Thông Tin Profile Student
**GET** `/students/profile/{userId}`

**Headers:** `Authorization: Bearer {token}`

**Quyền:** Student chỉ xem được profile của chính mình

**Response data (StudentDTO):**
- `id`: Long
- `hcmutId`: String
- `firstName`: String
- `lastName`: String
- `profileImage`: String
- `academicStatus`: String
- `dob`: String (format: "YYYY-MM-DD")
- `phone`: String
- `otherMethodContact`: String
- `role`: String
- `majorId`: Long
- `majorName`: String
- `department`: String
- `createdDate`: String (ISO 8601)
- `updateDate`: String (ISO 8601)
- `lastLogin`: String (ISO 8601)

---

### 1.2. Lấy Lịch Sử Học Của Student
**GET** `/students/history/{userId}`

**Headers:** `Authorization: Bearer {token}`

**Quyền:** Student chỉ xem được lịch sử của chính mình

**Response data:** Array of StudentSessionHistoryDTO
- `studentSessionId`: Long
- `sessionId`: Long
- `tutorName`: String
- `subjectName`: String
- `subjectCode`: String
- `startTime`: String (ISO 8601)
- `endTime`: String (ISO 8601)
- `format`: String (online/offline)
- `location`: String
- `sessionStatus`: String (trạng thái của session)
- `registrationStatus`: String (trạng thái đăng ký của student)
- `registeredDate`: String (ISO 8601)
- `updatedDate`: String (ISO 8601)

---

### 1.3. Cập Nhật Profile Student
**PUT** `/students/profile/{userId}`

**Headers:** 
- `Authorization: Bearer {token}`
- `Content-Type: application/json`

**Quyền:** Student chỉ update được profile của chính mình

**Request body (StudentProfileUpdateRequest):**
- `firstName`: String (optional)
- `lastName`: String (optional)
- `profileImage`: String (optional)
- `academicStatus`: String (optional)
- `dob`: String - format "YYYY-MM-DD" (optional)
- `phone`: String (optional)
- `otherMethodContact`: String (optional)
- `majorId`: Long (optional)

**Lưu ý:** Tất cả fields đều optional, chỉ gửi những field cần update

**Response data:** StudentDTO (giống 1.1)

---

## 2. TUTOR APIs

### 2.1. Lấy Danh Sách Tutors (Public)
**GET** `/tutors?page={page}&size={size}`

**Headers:** Không cần authentication

**Query params:**
- `page`: number (default: 0)
- `size`: number (default: 10)

**Response data (PageDTO):**
- `content`: Array of TutorDTO
  - `id`: Long
  - `hcmutId`: String
  - `firstName`: String
  - `lastName`: String
  - `profileImage`: String
  - `academicStatus`: String
  - `dob`: String (format: "YYYY-MM-DD")
  - `phone`: String
  - `otherMethodContact`: String
  - `role`: String
  - `createdDate`: String (ISO 8601)
  - `updateDate`: String (ISO 8601)
  - `lastLogin`: String (ISO 8601)
  - `title`: String
  - `majorId`: Long
  - `majorName`: String
  - `department`: String
  - `description`: String
  - `specializations`: Array of String (tên môn học)
  - `rating`: Number
  - `reviewCount`: Number
  - `studentCount`: Number
  - `experienceYears`: Number
  - `isAvailable`: Boolean
- `pageNumber`: number
- `pageSize`: number
- `totalElements`: number
- `totalPages`: number
- `first`: boolean
- `last`: boolean
- `empty`: boolean

---

### 2.2. Lấy Profile Chi Tiết Của Tutor
**GET** `/tutors/profile/{userId}`

**Headers:** `Authorization: Bearer {token}`

**Quyền:** Tutor chỉ xem được profile chi tiết của chính mình

**Response data (TutorDetailDTO):**
- `id`: Long
- `hcmutId`: String
- `firstName`: String
- `lastName`: String
- `profileImage`: String
- `academicStatus`: String (bằng cấp/chứng chỉ)
- `dob`: String (format: "YYYY-MM-DD")
- `phone`: String
- `otherMethodContact`: String
- `role`: String
- `createdDate`: String (ISO 8601)
- `updateDate`: String (ISO 8601)
- `lastLogin`: String (ISO 8601)
- `tutorProfileId`: Long
- `majorId`: Long
- `majorName`: String
- `department`: String
- `bio`: String
- `subjects`: Array of SubjectDTO
  - `id`: Long
  - `name`: String
- `rating`: Number (BigDecimal)
- `experienceYears`: Number
- `totalSessionsCompleted`: Long
- `isAvailable`: Boolean
- `schedules`: Array of TutorScheduleDTO (lịch dạy)
  - `id`: Long
  - `dayOfWeek`: Number (0=Chủ Nhật, 1=Thứ 2, ..., 6=Thứ 7)
  - `startTime`: String (format: "HH:mm:ss")
  - `endTime`: String (format: "HH:mm:ss")
  - `status`: String
  - `createdDate`: String (ISO 8601)
  - `updateDate`: String (ISO 8601)

---

### 2.3. Cập Nhật Profile Tutor
**PUT** `/tutors/profile/{userId}`

**Headers:** 
- `Authorization: Bearer {token}`
- `Content-Type: application/json`

**Quyền:** Tutor chỉ update được profile của chính mình

**Request body (TutorProfileUpdateRequest):**
- `firstName`: String (optional)
- `lastName`: String (optional)
- `profileImage`: String (optional)
- `academicStatus`: String (optional)
- `dob`: String - format "YYYY-MM-DD" (optional)
- `phone`: String (optional)
- `otherMethodContact`: String (optional)
- `majorId`: Long (optional)
- `bio`: String (optional)
- `subjectIds`: Array of Long (optional)
- `experienceYears`: Number (optional)
- `isAvailable`: Boolean (optional)

**Lưu ý:** Tất cả fields đều optional, chỉ gửi những field cần update

**Response data:** TutorDetailDTO (giống 2.2)

---

## 3. ADMIN APIs

### 3.1. Admin Update Student Profile
**PUT** `/admin/students/{userId}`

**Headers:** 
- `Authorization: Bearer {admin_token}`
- `Content-Type: application/json`

**Quyền:** Chỉ Admin

**Request body:** Giống StudentProfileUpdateRequest (xem 1.3)

**Response data:** StudentDTO

---

### 3.2. Admin Delete Student Profile
**DELETE** `/admin/students/{userId}`

**Headers:** `Authorization: Bearer {admin_token}`

**Quyền:** Chỉ Admin

**Response data:** `null`

**Lưu ý:** Xóa cascade tất cả dữ liệu liên quan

---

### 3.3. Admin Update Tutor Profile
**PUT** `/admin/tutors/{userId}`

**Headers:** 
- `Authorization: Bearer {admin_token}`
- `Content-Type: application/json`

**Quyền:** Chỉ Admin

**Request body:** Giống TutorProfileUpdateRequest (xem 2.3)

**Response data:** TutorDetailDTO

---

### 3.4. Admin Delete Tutor Profile
**DELETE** `/admin/tutors/{userId}`

**Headers:** `Authorization: Bearer {admin_token}`

**Quyền:** Chỉ Admin

**Response data:** `null`

**Lưu ý:** Xóa cascade tutor profile và dữ liệu liên quan

---

## Error Responses

### 403 Forbidden
Khi user cố gắng truy cập/sửa profile của người khác:
```
{
  statusCode: 403,
  message: "Access denied: You can only view/update your own profile",
  data: null
}
```

### 404 Not Found
Khi không tìm thấy user/profile:
```
{
  statusCode: 404,
  message: "User/Profile not found with id: {id}",
  data: null
}
```

---

## Lưu Ý Quan Trọng

1. **userId trong URL:** Là `user.id` (không phải `tutorProfile.id`)

2. **Authentication:** Tất cả endpoints (trừ GET /tutors) yêu cầu Bearer token

3. **Ownership Check:** Student/Tutor chỉ được access profile của chính mình (Admin không bị hạn chế)

4. **Partial Update:** PUT endpoints hỗ trợ partial update, chỉ gửi fields cần thay đổi

5. **Date Format:** 
   - `dob`: "YYYY-MM-DD" (LocalDate)
   - Timestamp fields: ISO 8601 format (Instant)

6. **Time Format:** `startTime`, `endTime` trong schedule: "HH:mm:ss" (LocalTime)

7. **Day of Week:** 0 = Chủ Nhật, 1 = Thứ 2, ..., 6 = Thứ 7

