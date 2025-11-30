# TỔNG HỢP CÁC CONTROLLER VÀ QUAN HỆ

## MỤC LỤC
1. [Tổng quan các Controller](#tổng-quan-các-controller)
2. [Quan hệ giữa các Controller](#quan-hệ-giữa-các-controller)
3. [Chi tiết từng Controller](#chi-tiết-từng-controller)
4. [Sơ đồ quan hệ Controller - Service - Entity](#sơ-đồ-quan-hệ-controller---service---entity)

---

## TỔNG QUAN CÁC CONTROLLER

| Controller | Base URL | Mô tả | Role |
|------------|----------|-------|------|
| AdminController | `/admin` | Quản lý hệ thống, duyệt session, quản lý user | ADMIN |
| AuthenticationController | `/auth` | Đăng nhập, xác thực | PUBLIC |
| DepartmentController | `/departments` | Quản lý khoa | PUBLIC |
| MajorController | `/majors` | Quản lý ngành học | PUBLIC |
| SessionController | `/sessions` | Quản lý session (CRUD) | TUTOR |
| SessionStatusController | `/session-statuses` | Lấy danh sách trạng thái session | PUBLIC |
| StudentController | `/students` | Quản lý sinh viên, đăng ký session | STUDENT |
| StudentSessionStatusController | `/student-session-statuses` | Lấy danh sách trạng thái đăng ký | PUBLIC |
| SubjectController | `/subjects` | Quản lý môn học | PUBLIC |
| TutorController | `/tutors` | Quản lý gia sư, duyệt đăng ký | TUTOR |

---

## QUAN HỆ GIỮA CÁC CONTROLLER

### 1. **Quan hệ theo Role/Permission:**

```
AuthenticationController (Public)
    └─> Login → Trả về token với role
        ├─> ADMIN → AdminController
        ├─> STUDENT → StudentController
        └─> TUTOR → TutorController
```

### 2. **Quan hệ theo Business Flow:**

#### **Flow 1: Đăng ký làm Tutor**
```
TutorController.registerTutorProfile()
    └─> SubjectController.getAllSubjects() (chọn môn dạy)
    └─> AdminController.getPendingTutors() (admin xem danh sách chờ duyệt)
        ├─> AdminController.approveTutorByUserId() (duyệt)
        └─> AdminController.rejectTutorByUserId() (từ chối)
```

#### **Flow 2: Tạo Session (Tutor)**
```
SessionController.createSession()
    └─> SubjectController.getAllSubjects() (chọn môn)
    └─> AdminController.getPendingSessions() (admin xem session chờ duyệt)
        └─> AdminController.updateSessionStatus() (duyệt/từ chối)
```

#### **Flow 3: Đăng ký Session (Student)**
```
StudentController.getAvailableSessions() (xem session khả dụng)
    └─> StudentController.registerSession()
        └─> TutorController.getPendingStudentSessions() (tutor xem đăng ký chờ duyệt)
            ├─> TutorController.approveStudentSessions() (duyệt)
            └─> TutorController.rejectStudentSessions() (từ chối)
```

#### **Flow 4: Quản lý User (Admin)**
```
AdminController.getAllUsers()
    └─> AdminController.getUserProfile()
        └─> AdminController.deleteUserProfile() (soft delete)
```

### 3. **Quan hệ với Entity:**

| Controller | Sử dụng Entity chính | Entity phụ |
|------------|---------------------|------------|
| AdminController | User, Session, TutorProfile | Status, SessionStatus |
| AuthenticationController | HcmutSso, Datacore | User |
| DepartmentController | Department | - |
| MajorController | Major | Department |
| SessionController | Session | User, Subject, SessionStatus |
| SessionStatusController | SessionStatus | - |
| StudentController | User, StudentSession | Session, StudentSessionStatus |
| StudentSessionStatusController | StudentSessionStatus | - |
| SubjectController | Subject | - |
| TutorController | TutorProfile, Session | User, Subject, StudentSession |

---

## CHI TIẾT TỪNG CONTROLLER

### 1. ADMINCONTROLLER
**Base URL:** `/admin`  
**Role:** ADMIN

#### Service Dependencies:
- `AdminService`
- `TutorProfileService`

#### Các hàm:

```
+ getAdminProfile(): ResponseEntity<BaseResponse>
+ deleteUserProfile(userId: Integer): ResponseEntity<BaseResponse>
+ getAllUsers(page: int): ResponseEntity<BaseResponse>
+ getUserProfile(userId: Integer): ResponseEntity<BaseResponse>
+ getPendingSessions(page: int): ResponseEntity<BaseResponse>
+ updateSessionStatus(sessionId: Integer, setStatus: String): ResponseEntity<BaseResponse>
+ getPendingTutors(page: int): ResponseEntity<BaseResponse>
+ approveTutorByUserId(userId: Integer): ResponseEntity<BaseResponse>
+ rejectTutorByUserId(userId: Integer): ResponseEntity<BaseResponse>
- getCurrentUserId(authentication: Authentication): Integer
```

#### Quan hệ với Entity:
- **User** (0..* ---- 1 Admin) - Quản lý nhiều user
- **Session** (0..* ---- 1 Admin) - Duyệt nhiều session
- **TutorProfile** (0..* ---- 1 Admin) - Duyệt nhiều tutor

---

### 2. AUTHENTICATIONCONTROLLER
**Base URL:** `/auth`  
**Role:** PUBLIC

#### Service Dependencies:
- `HcmutSsoService`

#### Các hàm:

```
+ login(loginRequest: LoginRequest): ResponseEntity<?>
```

#### Quan hệ với Entity:
- **HcmutSso** (1 ---- 1 Datacore) - Xác thực user

---

### 3. DEPARTMENTCONTROLLER
**Base URL:** `/departments`  
**Role:** PUBLIC

#### Repository Dependencies:
- `DepartmentRepository`

#### Các hàm:

```
+ getAllDepartments(): ResponseEntity<BaseResponse>
```

#### Quan hệ với Entity:
- **Department** (1 ---- 0..* Major) - Có nhiều ngành

---

### 4. MAJORCONTROLLER
**Base URL:** `/majors`  
**Role:** PUBLIC

#### Repository Dependencies:
- `MajorRepository`

#### Các hàm:

```
+ getAllMajors(): ResponseEntity<BaseResponse>
+ getMajorsByDepartment(departmentId: Integer): ResponseEntity<BaseResponse>
- convertToDTO(major: Major): MajorDTO
```

#### Quan hệ với Entity:
- **Major** (0..* ---- 1 Department) - Thuộc về 1 khoa

---

### 5. SESSIONCONTROLLER
**Base URL:** `/sessions`  
**Role:** TUTOR

#### Service Dependencies:
- `SessionService`

#### Các hàm:

```
+ getAllSessions(page: int): ResponseEntity<BaseResponse>
+ createSession(sessionRequest: SessionRequest): ResponseEntity<BaseResponse>
+ updateSession(id: Integer, sessionRequest: SessionRequest): ResponseEntity<BaseResponse>
+ deleteSession(id: Integer): ResponseEntity<BaseResponse>
- getCurrentUserId(authentication: Authentication): Integer
```

#### Quan hệ với Entity:
- **Session** (0..* ---- 1 User/Tutor) - Tutor tạo nhiều session
- **Session** (0..* ---- 1 Subject) - Session về 1 môn học
- **Session** (0..* ---- 1 SessionStatus) - Session có 1 trạng thái

---

### 6. SESSIONSTATUSCONTROLLER
**Base URL:** `/session-statuses`  
**Role:** PUBLIC

#### Repository Dependencies:
- `SessionStatusRepository`

#### Các hàm:

```
+ getAllSessionStatuses(): ResponseEntity<BaseResponse>
```

#### Quan hệ với Entity:
- **SessionStatus** (1 ---- 0..* Session) - Một status có nhiều session

---

### 7. STUDENTCONTROLLER
**Base URL:** `/students`  
**Role:** STUDENT

#### Service Dependencies:
- `StudentService`

#### Các hàm:

```
+ getStudentProfile(): ResponseEntity<BaseResponse>
+ getStudentSessionHistory(page: int): ResponseEntity<BaseResponse>
+ updateStudentProfile(request: StudentProfileUpdateRequest): ResponseEntity<BaseResponse>
+ getAvailableSessions(page: int): ResponseEntity<BaseResponse>
+ registerSession(sessionId: Integer): ResponseEntity<BaseResponse>
+ getWeekSchedule(weekOffset: Integer): ResponseEntity<BaseResponse>
- getCurrentUserId(authentication: Authentication): Integer
```

#### Quan hệ với Entity:
- **User/Student** (1 ---- 0..* StudentSession) - Student đăng ký nhiều session
- **User/Student** (1 ---- 0..* FeedbackStudent) - Student gửi nhiều feedback
- **StudentSession** (0..* ---- 1 Session) - Đăng ký vào session

---

### 8. STUDENTSESSIONSTATUSCONTROLLER
**Base URL:** `/student-session-statuses`  
**Role:** PUBLIC

#### Repository Dependencies:
- `StudentSessionStatusRepository`

#### Các hàm:

```
+ getAllStudentSessionStatuses(): ResponseEntity<BaseResponse>
```

#### Quan hệ với Entity:
- **StudentSessionStatus** (1 ---- 0..* StudentSession) - Một status có nhiều đăng ký

---

### 9. SUBJECTCONTROLLER
**Base URL:** `/subjects`  
**Role:** PUBLIC

#### Repository Dependencies:
- `SubjectRepository`

#### Các hàm:

```
+ getAllSubjects(): ResponseEntity<BaseResponse>
```

#### Quan hệ với Entity:
- **Subject** (1 ---- 0..* Session) - Môn học có nhiều session
- **Subject** (0..* ---- 0..* TutorProfile) - ManyToMany với tutor

---

### 10. TUTORCONTROLLER
**Base URL:** `/tutors`  
**Role:** TUTOR

#### Service Dependencies:
- `TutorService`
- `TutorProfileService`

#### Các hàm:

```
+ getAllTutors(page: int): ResponseEntity<BaseResponse>
+ getTutorDetail(): ResponseEntity<BaseResponse>
+ updateTutorProfile(request: TutorProfileUpdateRequest): ResponseEntity<BaseResponse>
+ getPendingStudentSessions(page: int): ResponseEntity<BaseResponse>
+ approveStudentSessions(studentSessionIds: List<Integer>): ResponseEntity<BaseResponse>
+ rejectStudentSessions(studentSessionIds: List<Integer>): ResponseEntity<BaseResponse>
+ getWeekSchedule(weekOffset: Integer): ResponseEntity<BaseResponse>
+ registerTutorProfile(request: TutorProfileCreateRequest): ResponseEntity<BaseResponse>
- getCurrentUserId(authentication: Authentication): Integer
```

#### Quan hệ với Entity:
- **TutorProfile** (0..1 ---- 1 User) - Profile của 1 user
- **TutorProfile** (0..* ---- 0..* Subject) - Tutor dạy nhiều môn
- **User/Tutor** (1 ---- 0..* Session) - Tutor tạo nhiều session
- **Session** (1 ---- 0..* StudentSession) - Session có nhiều đăng ký

---

## SƠ ĐỒ QUAN HỆ CONTROLLER - SERVICE - ENTITY

### Kiến trúc phân tầng:

```
┌─────────────────────────────────────────────────────────┐
│                    CONTROLLER LAYER                      │
│  (Nhận request, validate, gọi service, trả response)    │
└─────────────────┬───────────────────────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────────────────────┐
│                     SERVICE LAYER                        │
│        (Business logic, validation, transaction)        │
└─────────────────┬───────────────────────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────────────────────┐
│                  REPOSITORY LAYER                        │
│             (Data access, JPA queries)                   │
└─────────────────┬───────────────────────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────────────────────┐
│                     ENTITY LAYER                         │
│                  (Database tables)                       │
└─────────────────────────────────────────────────────────┘
```

### Chi tiết mapping:

#### **AdminController:**
```
AdminController
    ├─> AdminService
    │   ├─> UserRepository → User
    │   ├─> SessionRepository → Session
    │   └─> StatusRepository → Status
    │
    └─> TutorProfileService
        ├─> TutorProfileRepository → TutorProfile
        └─> UserRepository → User
```

#### **SessionController:**
```
SessionController
    └─> SessionService
        ├─> SessionRepository → Session
        ├─> UserRepository → User
        ├─> SubjectRepository → Subject
        └─> SessionStatusRepository → SessionStatus
```

#### **StudentController:**
```
StudentController
    └─> StudentService
        ├─> UserRepository → User
        ├─> StudentSessionRepository → StudentSession
        ├─> SessionRepository → Session
        ├─> StudentSessionStatusRepository → StudentSessionStatus
        └─> StatusRepository → Status
```

#### **TutorController:**
```
TutorController
    ├─> TutorService
    │   ├─> TutorProfileRepository → TutorProfile
    │   ├─> SessionRepository → Session
    │   ├─> StudentSessionRepository → StudentSession
    │   └─> UserRepository → User
    │
    └─> TutorProfileService
        ├─> TutorProfileRepository → TutorProfile
        ├─> SubjectRepository → Subject
        └─> UserRepository → User
```

#### **Lookup Controllers (Đơn giản, chỉ có Repository):**
```
DepartmentController → DepartmentRepository → Department
MajorController → MajorRepository → Major
SubjectController → SubjectRepository → Subject
SessionStatusController → SessionStatusRepository → SessionStatus
StudentSessionStatusController → StudentSessionStatusRepository → StudentSessionStatus
```

---

## PATTERN & BEST PRACTICES

### 1. **Controller Pattern:**
- Mỗi controller phụ trách 1 domain/resource
- Sử dụng `@RestController` + `@RequestMapping`
- Trả về `ResponseEntity<BaseResponse>` thống nhất
- Phân trang mặc định: 10 items/page

### 2. **Authentication Pattern:**
```java
Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
Integer userId = getCurrentUserId(authentication);
```
- Lấy userId từ JWT token
- Kiểm tra ownership trước khi CRUD

### 3. **Authorization Pattern:**
- **AdminController**: Chỉ ADMIN
- **TutorController**: Chỉ TUTOR
- **StudentController**: Chỉ STUDENT
- **Lookup Controllers**: PUBLIC (Department, Major, Subject, Status)

### 4. **Service Dependencies:**
- Controller → Service (Business logic)
- Service → Repository (Data access)
- Không bao giờ Controller → Repository trực tiếp (trừ lookup đơn giản)

### 5. **DTO Pattern:**
- Không trả về Entity trực tiếp
- Convert Entity → DTO trước khi trả về client
- Giảm thiểu data exposure, tránh circular reference

---

## TỔNG HỢP QUAN HỆ GIỮA CÁC CONTROLLER

### Biểu đồ quan hệ chức năng:

```
                    AuthenticationController
                              │
                    ┌─────────┼─────────┐
                    │         │         │
                  ADMIN    STUDENT    TUTOR
                    │         │         │
                    ▼         ▼         ▼
          AdminController  StudentController  TutorController
                │               │                   │
                │               │                   │
                ├───────────────┴───────────────────┤
                │                                   │
                │         SessionController         │
                │                 │                 │
                │                 │                 │
      ┌─────────┴────────┬────────┴────────┬───────┴──────┐
      │                  │                 │              │
DepartmentController  MajorController  SubjectController  │
      │                  │                 │              │
      └──────────────────┴─────────────────┴──────────────┘
                                │
                    ┌───────────┴───────────┐
                    │                       │
        SessionStatusController  StudentSessionStatusController
```

### Multiplicity giữa Controller và Entity:

| Controller | Entity | Multiplicity |
|------------|--------|--------------|
| AdminController | User | 1 ---- 0..* |
| AdminController | Session | 1 ---- 0..* |
| AdminController | TutorProfile | 1 ---- 0..* |
| SessionController | Session | 1 ---- 0..* |
| SessionController | User (tutor) | 1 ---- 0..* |
| StudentController | User (student) | 1 ---- 1 |
| StudentController | StudentSession | 1 ---- 0..* |
| TutorController | TutorProfile | 1 ---- 0..1 |
| TutorController | Session | 1 ---- 0..* |
| TutorController | StudentSession | 1 ---- 0..* |
| DepartmentController | Department | 1 ---- 0..* |
| MajorController | Major | 1 ---- 0..* |
| SubjectController | Subject | 1 ---- 0..* |

