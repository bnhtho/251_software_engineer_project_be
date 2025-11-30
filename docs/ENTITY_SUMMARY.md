# TỔNG HỢP CÁC ENTITY VÀ QUAN HỆ

## 1. DATACORE
**Bảng:** `datacore`

### Thuộc tính:
- `- id: Integer`
- `- hcmutId: String`
- `- email: String`
- `- firstName: String`
- `- lastName: String`
- `- profileImage: String`
- `- academicStatus: String`
- `- dob: LocalDate`
- `- phone: String`
- `- otherMethodContact: String`

### Quan hệ:
- **ManyToOne** với `Role`
- **OneToOne** với `HcmutSso`
- **ManyToOne** với `Major`

---

## 2. DEPARTMENT
**Bảng:** `department`

### Thuộc tính:
- `- id: Integer`
- `- name: String`

### Quan hệ:
- **OneToMany** với `Major`

---

## 3. FEEDBACKSTUDENT
**Bảng:** `feedback_student`

### Thuộc tính:
- `- id: Integer`
- `- rating: BigDecimal`
- `- comment: String`
- `- wouldRecommend: Boolean`
- `- createdDate: Instant`

### Quan hệ:
- **ManyToOne** với `Session`
- **ManyToOne** với `User`

---

## 4. HCMUTSSO
**Bảng:** `hcmut_sso`

### Thuộc tính:
- `- id: Integer`
- `- email: String`
- `- password: String`
- `- hcmutId: String`

### Quan hệ:
- **OneToOne** với `Datacore`

---

## 5. LIBRARY
**Bảng:** `library`

### Thuộc tính:
- `- id: Integer`
- `- name: String`
- `- catagory: String`
- `- author: String`
- `- url: String`
- `- uploadedDate: Instant`
- `- uploadedBy: Integer`

### Quan hệ:
- **ManyToOne** với `Subject`
- **OneToMany** với `ReportMaterial`

---

## 6. MAJOR
**Bảng:** `major`

### Thuộc tính:
- `- id: Integer`
- `- name: String`
- `- majorCode: String`
- `- programCode: String`
- `- note: String`

### Quan hệ:
- **ManyToOne** với `Department`
- **OneToMany** với `User`
- **OneToMany** với `Datacore`

---

## 7. NOTIFICATION
**Bảng:** `notification`

### Thuộc tính:
- `- id: Integer`
- `- type: String`
- `- title: String`
- `- message: String`
- `- isRead: Boolean`
- `- sentAt: Instant`
- `- createdDate: Instant`

### Quan hệ:
- **ManyToOne** với `User`
- **ManyToOne** với `Session`

---

## 8. REPORTMATERIAL
**Bảng:** `report_material`

### Thuộc tính:
- `- id: Integer`
- `- title: String`
- `- url: String`

### Quan hệ:
- **ManyToOne** với `ReportofTutor`
- **ManyToOne** với `Library`

---

## 9. REPORTOFTUTOR
**Bảng:** `reportof_tutor`

### Thuộc tính:
- `- id: Integer`
- `- tutorComment: String`
- `- studentPerformance: Integer`
- `- createdAt: Instant`
- `- updatedAt: Instant`

### Quan hệ:
- **OneToOne** với `Session`
- **OneToMany** với `ReportMaterial`

---

## 10. ROLE
**Bảng:** `role`

### Thuộc tính:
- `- id: Integer`
- `- name: String`
- `- description: String`

### Quan hệ:
- **OneToMany** với `Datacore`

---

## 11. SCHEDULE
**Bảng:** `schedule`

### Thuộc tính:
- `- id: Integer`
- `- dayOfWeek: DayOfWeek`
- `- createdDate: Instant`
- `- updateDate: Instant`

### Quan hệ:
- **ManyToOne** với `User`
- **ManyToOne** với `Session`

---

## 12. SESSION
**Bảng:** `session`

### Thuộc tính:
- `- id: Integer`
- `- startTime: Instant`
- `- endTime: Instant`
- `- dayOfWeek: DayOfWeek`
- `- format: String`
- `- location: String`
- `- maxQuantity: Integer`
- `- currentQuantity: Integer`
- `- createdDate: Instant`
- `- updatedDate: Instant`

### Quan hệ:
- **ManyToOne** với `User` (tutor)
- **ManyToOne** với `Subject`
- **ManyToOne** với `SessionStatus`
- **OneToMany** với `StudentSession`
- **OneToMany** với `FeedbackStudent`
- **OneToMany** với `Notification`
- **OneToOne** với `ReportofTutor`

---

## 13. SESSIONSTATUS
**Bảng:** `session_status`

### Thuộc tính:
- `- id: Byte`
- `- name: String`
- `- description: String`
- `- createdAt: Instant`

### Quan hệ:
- **OneToMany** với `Session`

---

## 14. STATUS
**Bảng:** `user_status`

### Thuộc tính:
- `- id: Integer`
- `- name: String`
- `- description: String`

### Quan hệ:
- **OneToMany** với `User`

---

## 15. STUDENTSESSION
**Bảng:** `student_session`

### Thuộc tính:
- `- id: Integer`
- `- registeredDate: Instant`
- `- confirmedDate: Instant`
- `- updatedDate: Instant`

### Quan hệ:
- **ManyToOne** với `User` (student)
- **ManyToOne** với `Session`
- **ManyToOne** với `StudentSessionStatus`

---

## 16. STUDENTSESSIONSTATUS
**Bảng:** `student_session_status`

### Thuộc tính:
- `- id: Byte`
- `- name: String`
- `- description: String`
- `- createdAt: Instant`

### Quan hệ:
- **OneToMany** với `StudentSession`

---

## 17. SUBJECT
**Bảng:** `subject`

### Thuộc tính:
- `- id: Integer`
- `- name: String`

### Quan hệ:
- **OneToMany** với `Session`
- **OneToMany** với `Library`
- **ManyToMany** với `TutorProfile`

---

## 18. TUTORPROFILE
**Bảng:** `tutor_profile`

### Thuộc tính:
- `- id: Integer`
- `- experienceYears: Short`
- `- bio: String`
- `- rating: BigDecimal`
- `- priority: Integer`
- `- totalSessionsCompleted: Integer`
- `- isAvailable: Boolean`
- `- status: TutorStatus`

### Quan hệ:
- **OneToOne** với `User`
- **ManyToMany** với `Subject`

---

## 19. USER
**Bảng:** `user`

### Thuộc tính:
- `- id: Integer`
- `- createdDate: Instant`
- `- updateDate: Instant`
- `- lastLogin: Instant`
- `- role: String`
- `- hcmutId: String`
- `- firstName: String`
- `- lastName: String`
- `- profileImage: String`
- `- academicStatus: String`
- `- dob: LocalDate`
- `- phone: String`
- `- otherMethodContact: String`

### Quan hệ:
- **ManyToOne** với `Status`
- **ManyToOne** với `Major`
- **OneToMany** với `FeedbackStudent` (as student)
- **OneToMany** với `Notification`
- **OneToMany** với `Session` (as tutor)
- **OneToMany** với `Schedule`
- **OneToMany** với `StudentSession` (as student)
- **OneToOne** với `TutorProfile`

---

## QUAN HỆ CHO UML CLASS DIAGRAM

### Chú thích ký hiệu UML:
- **Association (----)**: Liên kết thông thường
- **Aggregation (◇----)**: Quan hệ "has-a", phần có thể tồn tại độc lập
- **Composition (◆----)**: Quan hệ "contains", phần không thể tồn tại độc lập
- **1..1**: Đúng 1
- **0..1**: 0 hoặc 1
- **1..***: 1 hoặc nhiều
- **0..***: 0 hoặc nhiều (hay viết là *)

### Chú thích Access Modifier:
- **- (Private)**: Chỉ class này có thể truy cập
- **+ (Public)**: Mọi đối tượng đều có thể truy cập
- **# (Protected)**: Chỉ class này và class kế thừa có thể truy cập
- **~ (Package/Default)**: Chỉ các class trong cùng package có thể truy cập

### Multiplicity trong Class Diagram:
**Multiplicity** thể hiện quan hệ về số lượng giữa các đối tượng được tạo từ các class:

| Ký hiệu | Ý nghĩa | Ví dụ |
|---------|---------|-------|
| **1** | Bắt buộc có đúng 1 | User phải có 1 Status |
| **0..1** | 0 hoặc 1 (optional) | User có thể có 0 hoặc 1 Major |
| **n** | Bắt buộc có đúng n | Không dùng nhiều trong thực tế |
| **0..*** | 0 hoặc nhiều | User có thể tạo 0 hoặc nhiều Session |
| **1..*** | 1 hoặc nhiều | Department phải có ít nhất 1 Major |
| **m..n** | Tối thiểu m, tối đa n | Ví dụ: 1..10 (từ 1 đến 10) |

**Cách đọc Multiplicity:**
```
ClassA "multiplicity_A" ---- "multiplicity_B" ClassB
```
- **multiplicity_A**: Số lượng đối tượng ClassA liên quan đến 1 đối tượng ClassB
- **multiplicity_B**: Số lượng đối tượng ClassB liên quan đến 1 đối tượng ClassA

**Ví dụ:**
```
User "1" ---- "0..*" Session
```
Nghĩa là:
- 1 Session được tạo bởi đúng 1 User (tutor)
- 1 User có thể tạo 0 hoặc nhiều Session

---

### QUAN HỆ CHI TIẾT:

#### 1. DEPARTMENT
```
Department "1" ◆----> "0..*" Major
```

#### 2. MAJOR
```
Major "1" ◇----> "0..*" User
Major "1" ◇----> "0..*" Datacore
Major "0..1" <---- Department
```

#### 3. ROLE
```
Role "0..1" ◇----> "0..*" Datacore
```

#### 4. STATUS
```
Status "1" ◇----> "0..*" User
```

#### 5. DATACORE
```
Datacore "0..1" <---- Role
Datacore "0..1" <---- Major
Datacore "1" ◆---- "1" HcmutSso
```

#### 6. HCMUTSSO
```
HcmutSso "1" ---- "1" Datacore
```

#### 7. USER
```
User "1" <---- Status
User "0..1" <---- Major
User "1" ◆---- "0..1" TutorProfile
User "1" (as tutor) ◇----> "0..*" Session
User "1" (as student) ◇----> "0..*" FeedbackStudent
User "1" ◇----> "0..*" Notification
User "1" ◇----> "0..*" Schedule
User "1" (as student) ◇----> "0..*" StudentSession
```

#### 8. TUTORPROFILE
```
TutorProfile "0..1" ---- "1" User
TutorProfile "0..*" <----> "0..*" Subject
```

#### 9. SUBJECT
```
Subject "1" ◇----> "0..*" Session
Subject "1" ◇----> "0..*" Library
Subject "0..*" <----> "0..*" TutorProfile
```

#### 10. SESSIONSTATUS
```
SessionStatus "1" ◇----> "0..*" Session
```

#### 11. SESSION
```
Session "1" <---- User (as tutor)
Session "1" <---- Subject
Session "1" <---- SessionStatus
Session "1" ◆----> "0..*" StudentSession
Session "1" ◆----> "0..*" FeedbackStudent
Session "0..1" ◇----> "0..*" Notification
Session "1" ◆----> "0..*" Schedule
Session "1" ◆---- "0..1" ReportofTutor
```

#### 12. STUDENTSESSIONSTATUS
```
StudentSessionStatus "1" ◇----> "0..*" StudentSession
```

#### 13. STUDENTSESSION
```
StudentSession "0..*" ----> "1" User (as student)
StudentSession "0..*" ----> "1" Session
StudentSession "0..*" ----> "1" StudentSessionStatus
```

#### 14. FEEDBACKSTUDENT
```
FeedbackStudent "0..*" ----> "1" Session
FeedbackStudent "0..*" ----> "1" User (as student)
```

#### 15. NOTIFICATION
```
Notification "0..*" ----> "1" User
Notification "0..*" ----> "0..1" Session
```

#### 16. SCHEDULE
```
Schedule "0..*" ----> "1" User
Schedule "0..*" ----> "1" Session
```

#### 17. REPORTOFTUTOR
```
ReportofTutor "0..1" ---- "1" Session
ReportofTutor "1" ◆----> "0..*" ReportMaterial
```

#### 18. LIBRARY
```
Library "0..*" ----> "0..1" Subject
Library "1" ◇----> "0..*" ReportMaterial
```

#### 19. REPORTMATERIAL
```
ReportMaterial "0..*" ----> "1" ReportofTutor
ReportMaterial "0..*" ----> "0..1" Library
```

---

## BẢNG TỔNG HỢP QUAN HỆ (ĐỂ VẼ CLASS DIAGRAM)

| Entity A | Multiplicity A | Quan hệ | Multiplicity B | Entity B | Ghi chú |
|----------|---------------|---------|----------------|----------|---------|
| Department | 1 | OneToMany | 0..* | Major | 1 Department có nhiều Major |
| Major | 0..1 | OneToMany | 0..* | User | User có thể không có Major |
| Major | 0..1 | OneToMany | 0..* | Datacore | Datacore có thể không có Major |
| Role | 0..1 | OneToMany | 0..* | Datacore | Datacore có thể không có Role |
| Status | 1 | OneToMany | 0..* | User | User bắt buộc có Status |
| Datacore | 1 | OneToOne | 1 | HcmutSso | Quan hệ 1-1 bắt buộc |
| User | 1 | OneToOne | 0..1 | TutorProfile | User có thể có TutorProfile |
| User (tutor) | 1 | OneToMany | 0..* | Session | Tutor tạo Session |
| Subject | 1 | OneToMany | 0..* | Session | Session bắt buộc có Subject |
| SessionStatus | 1 | OneToMany | 0..* | Session | Session bắt buộc có Status |
| Session | 1 | OneToMany | 0..* | StudentSession | Session có nhiều đăng ký |
| Session | 1 | OneToMany | 0..* | FeedbackStudent | Session có nhiều feedback |
| Session | 0..1 | OneToMany | 0..* | Notification | Notification có thể không liên quan Session |
| Session | 1 | OneToMany | 0..* | Schedule | Session có nhiều lịch |
| Session | 1 | OneToOne | 0..1 | ReportofTutor | Session có thể có báo cáo |
| User (student) | 1 | OneToMany | 0..* | StudentSession | Student đăng ký Session |
| StudentSessionStatus | 1 | OneToMany | 0..* | StudentSession | StudentSession bắt buộc có Status |
| User (student) | 1 | OneToMany | 0..* | FeedbackStudent | Student gửi feedback |
| User | 1 | OneToMany | 0..* | Notification | User nhận thông báo |
| User | 1 | OneToMany | 0..* | Schedule | User có lịch cá nhân |
| ReportofTutor | 1 | OneToMany | 0..* | ReportMaterial | Báo cáo có tài liệu |
| Library | 1 | OneToMany | 0..* | ReportMaterial | Tài liệu được tham chiếu |
| Subject | 0..1 | OneToMany | 0..* | Library | Library có thể không thuộc Subject |
| **TutorProfile** | **0..*** | **ManyToMany** | **0..*** | **Subject** | **Tutor dạy nhiều Subject** |

---

## HƯỚNG DẪN VẼ CLASS DIAGRAM

### 1. Format Class trong UML:

```
┌─────────────────────────┐
│     ClassName           │
├─────────────────────────┤
│ - id: Integer           │
│ - name: String          │
│ ...                     │
└─────────────────────────┘
```

### 2. Loại quan hệ cần thể hiện:

#### **Association:**
- Dùng cho ManyToOne/OneToMany khi không có ownership mạnh
- Ví dụ: User -> Major, Session -> Subject

#### **Aggregation (◇):**
- Phần có thể tồn tại độc lập với tổng thể
- Ví dụ: Department ◇-> Major, User ◇-> Session

#### **Composition (◆):**
- Phần không thể tồn tại độc lập
- Ví dụ: Session ◆-> StudentSession, ReportofTutor ◆-> ReportMaterial

#### **OneToOne:**
- Vẽ đường thẳng với multiplicity "1" ở cả 2 đầu
- Ví dụ: User 1---1 TutorProfile, Datacore 1---1 HcmutSso

#### **ManyToMany:**
- Vẽ với multiplicity "*" hoặc "0..*" ở cả 2 đầu
- Ví dụ: TutorProfile *---* Subject

### 3. Nhóm các Class theo chức năng:
- **User Management**: User, Status, Role, Datacore, HcmutSso, Major, Department
- **Tutor System**: TutorProfile, Subject
- **Session Management**: Session, SessionStatus, Schedule
- **Student Enrollment**: StudentSession, StudentSessionStatus
- **Feedback & Report**: FeedbackStudent, ReportofTutor, ReportMaterial
- **Library**: Library, Subject
- **Notification**: Notification

---

## BẢNG TRUNG GIAN

### tutor_profile_subject
Bảng trung gian cho quan hệ ManyToMany giữa `TutorProfile` và `Subject`
- `tutor_profile_id`
- `subject_id`

---

## TÓM TẮT MULTIPLICITY CHO TỪNG ENTITY

### 1. DEPARTMENT
- **Department 1 ---- 0..* Major**: Một Department có thể có nhiều Major

### 2. MAJOR  
- **Major 1 ---- 0..* User**: Một Major có nhiều User
- **Major 1 ---- 0..* Datacore**: Một Major có nhiều Datacore
- **Major 0..* ---- 1 Department**: Major bắt buộc thuộc Department

### 3. ROLE
- **Role 0..1 ---- 0..* Datacore**: Datacore có thể có hoặc không có Role

### 4. STATUS
- **Status 1 ---- 0..* User**: User bắt buộc có Status

### 5. DATACORE
- **Datacore 0..* ---- 0..1 Role**: Có thể có hoặc không có Role
- **Datacore 0..* ---- 0..1 Major**: Có thể có hoặc không có Major
- **Datacore 1 ---- 1 HcmutSso**: Quan hệ 1-1 bắt buộc

### 6. HCMUTSSO
- **HcmutSso 1 ---- 1 Datacore**: Quan hệ 1-1 bắt buộc

### 7. USER
- **User 0..* ---- 1 Status**: Bắt buộc có Status
- **User 0..* ---- 0..1 Major**: Có thể có hoặc không có Major
- **User 1 ---- 0..1 TutorProfile**: Có thể có TutorProfile (nếu là tutor)
- **User (tutor) 1 ---- 0..* Session**: Tutor có thể tạo nhiều Session
- **User (student) 1 ---- 0..* FeedbackStudent**: Student có thể gửi nhiều feedback
- **User 1 ---- 0..* Notification**: Có thể nhận nhiều thông báo
- **User 1 ---- 0..* Schedule**: Có thể có nhiều lịch
- **User (student) 1 ---- 0..* StudentSession**: Student có thể đăng ký nhiều session

### 8. TUTORPROFILE
- **TutorProfile 0..1 ---- 1 User**: Bắt buộc thuộc về 1 User
- **TutorProfile 0..* ---- 0..* Subject**: Có thể dạy nhiều Subject (ManyToMany)

### 9. SUBJECT
- **Subject 1 ---- 0..* Session**: Có thể có nhiều Session
- **Subject 0..1 ---- 0..* Library**: Có thể có nhiều Library
- **Subject 0..* ---- 0..* TutorProfile**: Được dạy bởi nhiều Tutor (ManyToMany)

### 10. SESSIONSTATUS
- **SessionStatus 1 ---- 0..* Session**: Có thể có nhiều Session

### 11. SESSION
- **Session 0..* ---- 1 User (tutor)**: Bắt buộc có 1 tutor
- **Session 0..* ---- 1 Subject**: Bắt buộc có 1 Subject
- **Session 0..* ---- 1 SessionStatus**: Bắt buộc có 1 Status
- **Session 1 ---- 0..* StudentSession**: Có thể có nhiều đăng ký
- **Session 1 ---- 0..* FeedbackStudent**: Có thể có nhiều feedback
- **Session 0..1 ---- 0..* Notification**: Có thể có nhiều thông báo
- **Session 1 ---- 0..* Schedule**: Có thể có nhiều lịch
- **Session 1 ---- 0..1 ReportofTutor**: Có thể có 1 báo cáo

### 12. STUDENTSESSIONSTATUS
- **StudentSessionStatus 1 ---- 0..* StudentSession**: Có thể có nhiều StudentSession

### 13. STUDENTSESSION
- **StudentSession 0..* ---- 1 User (student)**: Bắt buộc có 1 student
- **StudentSession 0..* ---- 1 Session**: Bắt buộc thuộc 1 Session
- **StudentSession 0..* ---- 1 StudentSessionStatus**: Bắt buộc có 1 Status

### 14. FEEDBACKSTUDENT
- **FeedbackStudent 0..* ---- 1 Session**: Bắt buộc cho 1 Session
- **FeedbackStudent 0..* ---- 1 User (student)**: Bắt buộc được gửi bởi 1 student

### 15. NOTIFICATION
- **Notification 0..* ---- 1 User**: Bắt buộc gửi đến 1 User
- **Notification 0..* ---- 0..1 Session**: Có thể liên quan đến Session

### 16. SCHEDULE
- **Schedule 0..* ---- 1 User**: Bắt buộc thuộc về 1 User
- **Schedule 0..* ---- 1 Session**: Bắt buộc liên kết với 1 Session

### 17. REPORTOFTUTOR
- **ReportofTutor 0..1 ---- 1 Session**: Bắt buộc cho 1 Session
- **ReportofTutor 1 ---- 0..* ReportMaterial**: Có thể có nhiều tài liệu

### 18. LIBRARY
- **Library 0..* ---- 0..1 Subject**: Có thể thuộc về Subject
- **Library 1 ---- 0..* ReportMaterial**: Có thể được tham chiếu nhiều lần

### 19. REPORTMATERIAL
- **ReportMaterial 0..* ---- 1 ReportofTutor**: Bắt buộc thuộc 1 báo cáo
- **ReportMaterial 0..* ---- 0..1 Library**: Có thể tham chiếu đến Library
