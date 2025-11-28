# So sánh API Documentation vs Code thực tế

> **Ngày tạo:** 27/11/2025
> 
> **File được so sánh:**
> - `API_ENDPOINTS_DOCUMENTATION.md` (Tài liệu cũ)
> - `ACTUAL_API_ENDPOINTS.md` (Code thực tế từ Controllers)

---

## Tổng quan

| Tiêu chí | API_ENDPOINTS_DOCUMENTATION.md | Code thực tế |
|----------|-------------------------------|--------------|
| **Tổng số endpoints** | ~43 endpoints | 37 endpoints |
| **Số Controllers** | 10 controllers | 10 controllers |
| **Có pagination** | Có | Có (chuẩn hơn) |
| **Response format** | BaseResponse | BaseResponse |

---

## Chi tiết từng Controller

### 1. AuthenticationController (`/auth`)

#### ✅ Giống nhau:
- `POST /auth/login` - Đăng nhập

#### ⚠️ Khác biệt:
- **Documentation:** Không có mô tả chi tiết
- **Code thực tế:** Có đầy đủ request/response structure

#### ❌ Thiếu trong code:
- Không có endpoint nào bị thiếu

---

### 2. AdminController (`/admin`)

#### ✅ Giống nhau:
- `DELETE /admin/users/{userId}` - Xóa user
- `GET /admin/users` - Lấy danh sách users
- `GET /admin/sessions/pending` - Lấy sessions đang chờ duyệt
- `PUT /admin/sessions/{sessionId}` - Duyệt/từ chối session
- `GET /admin/tutor/pending` - Lấy tutors đang chờ duyệt
- `PATCH /admin/{userId}/approve` - Duyệt tutor
- `PATCH /admin/{userId}/reject` - Từ chối tutor

#### ⚠️ Khác biệt:

**1. `DELETE /admin/users/{userId}`**
- **Documentation:** 
  - Có 2 endpoints riêng: `/admin/students/{studentId}` và `/admin/tutors/{tutorId}`
  - Cần chỉ định rõ student hay tutor
- **Code thực tế:** 
  - Chỉ có 1 endpoint chung: `/admin/users/{userId}`
  - Tự động xác định student/tutor dựa trên role trong database
  - **Đơn giản hơn và linh hoạt hơn**

**2. `PUT /admin/sessions/{sessionId}`**
- **Documentation:** 
  - Query param: `action=approve` hoặc `action=reject`
- **Code thực tế:**
  - Query param: `setStatus=SCHEDULED` hoặc `setStatus=CANCELLED`
  - **Rõ ràng hơn về trạng thái**

**3. `GET /admin/tutor/pending`**
- **Documentation:** 
  - Response: Array of TutorDTO
- **Code thực tế:**
  - Response: Page<TutorProfileResponse> (có pagination)
  - **Chuẩn hơn với pagination**

#### ❌ Thiếu trong code:
- Không có endpoint nào trong documentation bị thiếu

---

### 3. DepartmentController (`/departments`)

#### ✅ Giống nhau:
- `GET /departments` - Lấy danh sách departments

#### ⚠️ Khác biệt:
- **Documentation:** Không có chi tiết
- **Code thực tế:** Có đầy đủ response structure với DepartmentDTO

---

### 4. MajorController (`/majors`)

#### ✅ Giống nhau:
- `GET /majors` - Lấy tất cả majors
- `GET /majors/by-department/{departmentId}` - Lấy majors theo department

#### ⚠️ Khác biệt:
- **Documentation:** Response không rõ structure
- **Code thực tế:** Response đầy đủ với MajorDTO (có departmentId, departmentName, majorCode, programCode, note)

---

### 5. SessionController (`/sessions`)

#### ✅ Giống nhau:
- `GET /sessions` - Lấy danh sách sessions
- `POST /sessions` - Tạo session mới
- `PUT /sessions/{id}` - Cập nhật session
- `DELETE /sessions/{id}` - Xóa session

#### ⚠️ Khác biệt:

**1. `GET /sessions`**
- **Documentation:** 
  - Có filter: `status`, `tutorId`, `subjectId`
- **Code thực tế:**
  - Không có filter parameters
  - Chỉ có pagination
  - **⚠️ CẦN BỔ SUNG filter trong code**

**2. `PUT /sessions/{id}` và `DELETE /sessions/{id}`**
- **Documentation:** 
  - Không đề cập ownership check
- **Code thực tế:**
  - Có kiểm tra ownership: chỉ tutor tạo session mới được update/delete
  - Response 403 nếu không phải owner
  - **Bảo mật tốt hơn**

---

### 6. SessionStatusController (`/session-statuses`)

#### ✅ Giống nhau:
- `GET /session-statuses` - Lấy danh sách session statuses

#### ⚠️ Khác biệt:
- **Documentation:** Không có chi tiết
- **Code thực tế:** Response đầy đủ với SessionStatusDTO

---

### 7. StudentController (`/students`)

#### ✅ Giống nhau:
- `GET /students/profile` - Xem profile
- `PUT /students/profile` - Cập nhật profile
- `GET /students/history` - Xem lịch sử sessions
- `GET /students/available-sessions` - Xem sessions khả dụng
- `POST /students/register-session` - Đăng ký session
- `GET /students/schedule/{weekOffset}` - Xem lịch học tuần

#### ⚠️ Khác biệt:

**1. `GET /students/profile`**
- **Documentation:** 
  - Endpoint: `GET /students/{studentId}/profile`
  - StudentId trong URL
- **Code thực tế:**
  - Endpoint: `GET /students/profile`
  - StudentId lấy từ token authentication
  - **Bảo mật hơn, student chỉ xem được profile của mình**

**2. `PUT /students/profile`**
- **Documentation:** 
  - Endpoint: `PUT /students/{studentId}/profile`
- **Code thực tế:**
  - Endpoint: `PUT /students/profile`
  - StudentId lấy từ token
  - **Bảo mật hơn**

**3. `GET /students/history`**
- **Documentation:** 
  - Endpoint: `GET /students/{studentId}/history`
- **Code thực tế:**
  - Endpoint: `GET /students/history`
  - StudentId lấy từ token
  - **Bảo mật hơn**

**4. `POST /students/register-session`**
- **Documentation:** 
  - Request body có `studentId` và `sessionId`
- **Code thực tế:**
  - Chỉ cần `sessionId` trong query param
  - StudentId lấy từ token
  - **Đơn giản hơn và bảo mật hơn**

**5. `GET /students/schedule/{weekOffset}`**
- **Documentation:** 
  - Endpoint: `GET /students/{studentId}/schedule/{weekOffset}`
- **Code thực tế:**
  - Endpoint: `GET /students/schedule/{weekOffset}`
  - StudentId lấy từ token
  - **Bảo mật hơn**

#### ❌ Thiếu trong code:
- Không có endpoint nào bị thiếu

---

### 8. StudentSessionStatusController (`/student-session-statuses`)

#### ✅ Giống nhau:
- `GET /student-session-statuses` - Lấy danh sách student session statuses

#### ⚠️ Khác biệt:
- **Documentation:** Không có chi tiết
- **Code thực tế:** Response đầy đủ với StudentSessionStatusDTO

---

### 9. SubjectController (`/subjects`)

#### ✅ Giống nhau:
- `GET /subjects` - Lấy danh sách subjects

#### ⚠️ Khác biệt:
- **Documentation:** Không có chi tiết
- **Code thực tế:** Response đầy đủ với SubjectDTO

---

### 10. TutorController (`/tutors`)

#### ✅ Giống nhau:
- `GET /tutors` - Lấy danh sách tutors
- `GET /tutors/profile` - Xem profile chi tiết
- `PUT /tutors/profile` - Cập nhật profile
- `GET /tutors/pending-registrations` - Xem yêu cầu đăng ký chờ duyệt
- `PUT /tutors/student-sessions/approve` - Duyệt yêu cầu đăng ký
- `PUT /tutors/student-sessions/reject` - Từ chối yêu cầu đăng ký
- `GET /tutors/schedule/{weekOffset}` - Xem lịch giảng dạy
- `POST /tutors` - Đăng ký làm tutor

#### ⚠️ Khác biệt:

**1. `GET /tutors`**
- **Documentation:** 
  - Có filter: `subjectId`, `minRating`, `maxPrice`
- **Code thực tế:**
  - Không có filter parameters
  - Chỉ có pagination
  - **⚠️ CẦN BỔ SUNG filter trong code**

**2. `GET /tutors/profile`**
- **Documentation:** 
  - Endpoint: `GET /tutors/{tutorId}`
  - TutorId trong URL
  - Public endpoint (ai cũng xem được)
- **Code thực tế:**
  - Endpoint: `GET /tutors/profile`
  - TutorId lấy từ token
  - Chỉ tutor xem được profile của chính mình
  - **Khác hoàn toàn về mục đích sử dụng**

**3. `PUT /tutors/profile`**
- **Documentation:** 
  - Endpoint: `PUT /tutors/{tutorId}`
- **Code thực tế:**
  - Endpoint: `PUT /tutors/profile`
  - TutorId lấy từ token
  - **Bảo mật hơn**

**4. `GET /tutors/pending-registrations`**
- **Documentation:** 
  - Endpoint: `GET /tutors/{tutorId}/pending-students`
- **Code thực tế:**
  - Endpoint: `GET /tutors/pending-registrations`
  - TutorId lấy từ token
  - **Bảo mật hơn**

**5. `PUT /tutors/student-sessions/approve`**
- **Documentation:** 
  - Endpoint: `PUT /tutors/{tutorId}/students/approve`
  - Request body có `studentIds`
- **Code thực tế:**
  - Endpoint: `PUT /tutors/student-sessions/approve`
  - Request body: array of `studentSessionIds` (không phải studentIds)
  - TutorId lấy từ token
  - **Chính xác hơn vì approve StudentSession, không phải Student**

**6. `PUT /tutors/student-sessions/reject`**
- **Documentation:** 
  - Endpoint: `PUT /tutors/{tutorId}/students/reject`
  - Request body có `studentIds`
- **Code thực tế:**
  - Endpoint: `PUT /tutors/student-sessions/reject`
  - Request body: array of `studentSessionIds`
  - TutorId lấy từ token
  - **Chính xác hơn**

**7. `GET /tutors/schedule/{weekOffset}`**
- **Documentation:** 
  - Endpoint: `GET /tutors/{tutorId}/schedule/{weekOffset}`
- **Code thực tế:**
  - Endpoint: `GET /tutors/schedule/{weekOffset}`
  - TutorId lấy từ token
  - **Bảo mật hơn**

**8. `POST /tutors`**
- **Documentation:** 
  - Endpoint: `POST /tutors/{userId}/register` hoặc tương tự
- **Code thực tế:**
  - Endpoint: `POST /tutors`
  - UserId lấy từ token
  - Request body: TutorProfileCreateRequest
  - **Đơn giản và chuẩn RESTful hơn**

#### ❌ Thiếu trong code:
- **`GET /tutors/{tutorId}`** - Public endpoint để xem chi tiết 1 tutor cụ thể
  - **⚠️ CẦN BỔ SUNG: Endpoint public để student/guest xem chi tiết tutor**

---

## Tổng kết các điểm khác biệt chính

### 1. **Cách lấy userId/studentId/tutorId**

| API Documentation | Code thực tế |
|-------------------|--------------|
| Truyền trong URL path: `/students/{studentId}/profile` | Lấy từ token authentication: `/students/profile` |
| ❌ Có thể truy cập dữ liệu của người khác | ✅ Chỉ truy cập dữ liệu của chính mình |
| Kém bảo mật | **Bảo mật tốt hơn** |

### 2. **Pagination**

| API Documentation | Code thực tế |
|-------------------|--------------|
| Không rõ ràng về format | Chuẩn với cấu trúc: `{content, page, size, totalElements, totalPages}` |
| Một số endpoint không có pagination | Hầu hết list endpoint đều có pagination |

### 3. **Admin endpoints**

| API Documentation | Code thực tế |
|-------------------|--------------|
| Có endpoint riêng cho student và tutor: `/admin/students/{id}`, `/admin/tutors/{id}` | Chỉ 1 endpoint chung: `/admin/users/{userId}`, tự động xác định role |
| Phức tạp hơn | **Đơn giản và linh hoạt hơn** |

### 4. **Approve/Reject student sessions**

| API Documentation | Code thực tế |
|-------------------|--------------|
| Request body: `studentIds` | Request body: `studentSessionIds` |
| Không chính xác (approve student, không phải session) | **Chính xác (approve StudentSession entity)** |

### 5. **Query parameters**

| API Documentation | Code thực tế |
|-------------------|--------------|
| Admin session: `action=approve\|reject` | `setStatus=SCHEDULED\|CANCELLED` |
| Không rõ ràng | **Rõ ràng hơn về trạng thái** |

---

## Các điểm cần bổ sung trong code

### ⚠️ Thiếu hoàn toàn:

1. **`GET /tutors/{tutorId}`** - Public endpoint
   - Để student/guest xem chi tiết 1 tutor cụ thể
   - Response: TutorDetailDTO
   - **Quan trọng:** Cần có để student có thể xem thông tin tutor trước khi đăng ký

### ⚠️ Thiếu filter/search:

2. **`GET /sessions`** - Thiếu filter parameters:
   - `status` - Lọc theo trạng thái
   - `tutorId` - Lọc sessions của tutor
   - `subjectId` - Lọc theo môn học

3. **`GET /tutors`** - Thiếu filter parameters:
   - `subjectId` - Lọc tutor dạy môn cụ thể
   - `minRating` - Lọc tutor có rating tối thiểu
   - `experienceYears` - Lọc theo kinh nghiệm

---

## Kết luận

### ✅ Code thực tế tốt hơn Documentation ở:
1. **Bảo mật:** Lấy userId từ token thay vì URL
2. **Đơn giản:** Ít endpoint hơn nhưng linh hoạt hơn (vd: 1 endpoint `/admin/users/{userId}` thay vì 2)
3. **Chính xác:** Approve `studentSessionIds` thay vì `studentIds`
4. **Chuẩn hóa:** Pagination format rõ ràng, consistent
5. **Ownership check:** Kiểm tra quyền sở hữu khi update/delete

### ⚠️ Cần bổ sung trong code:
1. **Public endpoint:** `GET /tutors/{tutorId}` để xem chi tiết tutor
2. **Filter/Search:** Thêm query parameters để filter trong `GET /sessions` và `GET /tutors`
3. **Documentation:** Cập nhật API_ENDPOINTS_DOCUMENTATION.md để match với code thực tế

### 📝 Khuyến nghị:
- **Ưu tiên:** Implement `GET /tutors/{tutorId}` public endpoint
- **Nên có:** Thêm filter cho sessions và tutors
- **Update docs:** Cập nhật API_ENDPOINTS_DOCUMENTATION.md theo code thực tế

