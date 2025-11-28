# So sánh API Endpoints: ACTUAL vs DOCUMENTATION

## Tổng quan
File này so sánh sự khác biệt giữa:
- **ACTUAL_API_ENDPOINTS.md**: API thực tế từ code AdminController.java
- **API_ENDPOINTS_DOCUMENTATION.md**: Tài liệu API đã có trước đó

---

## 1. Endpoint: Xóa user

### ✅ GIỐNG NHAU
- **HTTP Method**: `DELETE`
- **Path**: `/admin/users/{userId}`
- **Quyền**: ROLE_ADMIN
- **Response structure**: Giống nhau

### ⚠️ KHÁC BIỆT
**Không có khác biệt**

---

## 2. Endpoint: Lấy danh sách users

### ✅ GIỐNG NHAU
- **HTTP Method**: `GET`
- **Path**: `/admin/users`
- **Query param**: `page` (default = 0)
- **Quyền**: ROLE_ADMIN
- **Response structure**: Giống nhau (sử dụng PaginationUtil)
- **Page size**: 10 items

### ⚠️ KHÁC BIỆT
**Không có khác biệt**

---

## 3. Endpoint: Lấy thông tin chi tiết user theo userId

### ✅ MỚI THÊM (KHÔNG CÓ TRONG API_ENDPOINTS_DOCUMENTATION.md)

- **HTTP Method**: `GET`
- **Path**: `/admin/users/{userId}`
- **Quyền**: ROLE_ADMIN
- **Path Variable**: `userId` (Integer)
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

**📝 GHI CHÚ**: 
- Endpoint này **MỚI ĐƯỢC THÊM** vào code thực tế
- File `API_ENDPOINTS_DOCUMENTATION.md` **KHÔNG CÓ** endpoint này
- Endpoint này được tạo theo yêu cầu "thêm endpoint get profile cho admin"

---

## 4. Endpoint: Lấy danh sách sessions pending

### ✅ GIỐNG NHAU
- **HTTP Method**: `GET`
- **Path**: `/admin/sessions/pending`
- **Query param**: `page` (default = 0)
- **Quyền**: ROLE_ADMIN
- **Response structure**: Giống nhau (sử dụng PaginationUtil)
- **Page size**: 10 items

### ⚠️ KHÁC BIỆT
**Không có khác biệt**

---

## 5. Endpoint: Duyệt/từ chối Session

### ✅ GIỐNG NHAU
- **HTTP Method**: `PUT`
- **Path**: `/admin/sessions/{sessionId}`
- **Query param**: `setStatus` (required)
- **Quyền**: ROLE_ADMIN
- **Response structure**: Giống nhau
- **Logic**: 
  - `setStatus=SCHEDULED` → approve
  - `setStatus=CANCELLED` → reject

### ⚠️ KHÁC BIỆT
**Không có khác biệt**

---

## 6. Endpoint: Lấy danh sách tutor pending

### ✅ GIỐNG NHAU
- **HTTP Method**: `GET`
- **Path**: `/admin/tutor/pending`
- **Query param**: `page` (default = 0)
- **Quyền**: ROLE_ADMIN
- **Page size**: 10 items

### ⚠️ KHÁC BIỆT

#### Trong API_ENDPOINTS_DOCUMENTATION.md (Section 2.4):
```markdown
### 2.4. Lấy danh sách tutor pending (chờ duyệt)
```

#### Trong ACTUAL_API_ENDPOINTS.md (Section 6):
```markdown
### 6. Lấy danh sách tutor pending (chờ duyệt)
```

**📝 GHI CHÚ**: 
- Chỉ khác số thứ tự section (2.4 vs 6)
- **Cấu trúc response khác nhau**:
  - DOCUMENTATION: Mô tả là "Page of TutorProfileResponse (Spring Data Page format)"
  - ACTUAL: Xác nhận trả về Spring Data Page format với đầy đủ các field: `content`, `pageable`, `totalPages`, `totalElements`, `size`, `number`, `sort`, `numberOfElements`, `first`, `last`, `empty`

---

## 7. Endpoint: Duyệt tutor profile

### ✅ GIỐNG NHAU
- **HTTP Method**: `PATCH`
- **Path**: `/admin/{userId}/approve`
- **Quyền**: ROLE_ADMIN
- **Path Variable**: `userId` (Integer)
- **Response structure**: Giống nhau
- **Logic**: Set status = APPROVED

### ⚠️ KHÁC BIỆT

#### Trong API_ENDPOINTS_DOCUMENTATION.md (Section 2.6):
```markdown
### 2.6. Duyệt tutor profile
```

#### Trong ACTUAL_API_ENDPOINTS.md (Section 7):
```markdown
### 7. Duyệt tutor profile
```

**📝 GHI CHÚ**: Chỉ khác số thứ tự section (2.6 vs 7)

---

## 8. Endpoint: Từ chối tutor profile

### ✅ GIỐNG NHAU
- **HTTP Method**: `PATCH`
- **Path**: `/admin/{userId}/reject`
- **Quyền**: ROLE_ADMIN
- **Path Variable**: `userId` (Integer)
- **Response structure**: Giống nhau
- **Logic**: Set status = REJECTED

### ⚠️ KHÁC BIỆT

#### Trong API_ENDPOINTS_DOCUMENTATION.md (Section 2.7):
```markdown
### 2.7. Từ chối tutor profile
```

#### Trong ACTUAL_API_ENDPOINTS.md (Section 8):
```markdown
### 8. Từ chối tutor profile
```

**📝 GHI CHÚ**: Chỉ khác số thứ tự section (2.7 vs 8)

---

## Tổng kết so sánh

### 📊 Thống kê
| Loại | Số lượng | Endpoints |
|------|----------|-----------|
| **Giống nhau hoàn toàn** | 7 | DELETE /admin/users/{userId}<br>GET /admin/users<br>GET /admin/sessions/pending<br>PUT /admin/sessions/{sessionId}<br>GET /admin/tutor/pending<br>PATCH /admin/{userId}/approve<br>PATCH /admin/{userId}/reject |
| **Mới thêm** | 1 | GET /admin/users/{userId} |
| **Khác biệt** | 0 | - |
| **Bị xóa** | 0 | - |

### ✨ Điểm khác biệt chính

#### 1. **Endpoint mới: GET /admin/users/{userId}**
- **Trạng thái**: ✅ MỚI THÊM
- **Mục đích**: Lấy thông tin chi tiết của 1 user theo userId
- **Lý do**: Đáp ứng yêu cầu "thêm endpoint get profile cho admin"
- **Impact**: Frontend cần implement thêm logic để gọi endpoint này khi admin muốn xem chi tiết 1 user cụ thể

#### 2. **Số thứ tự section**
- File DOCUMENTATION đánh số từ 2.1 đến 2.7 (trong section ADMIN ENDPOINTS)
- File ACTUAL đánh số từ 1 đến 8 (độc lập)
- **Impact**: Không ảnh hưởng đến functionality

#### 3. **Response format của GET /admin/tutor/pending**
- DOCUMENTATION: Mô tả Spring Data Page format nhưng không chi tiết
- ACTUAL: Liệt kê đầy đủ tất cả các field của Spring Data Page
- **Impact**: Frontend cần chú ý parse đúng các field như `number` (thay vì `currentPage`), `totalElements` (thay vì `totalItems`)

### 🔍 Phân tích chi tiết

#### API_ENDPOINTS_DOCUMENTATION.md
- **Phạm vi**: Bao gồm TẤT CẢ các module (Auth, Admin, Tutor, Student, Session, Lookup)
- **Số lượng endpoints**: 32 endpoints
- **Admin endpoints**: 7 endpoints (sections 2.1 - 2.7)
- **Format**: Theo cấu trúc tài liệu hoàn chỉnh với best practices, business flow, security notes

#### ACTUAL_API_ENDPOINTS.md
- **Phạm vi**: CHỈ Admin endpoints từ AdminController.java
- **Số lượng endpoints**: 8 endpoints
- **Format**: Tài liệu chi tiết từ code thực tế, không có phần giải thích về business flow

### ⚠️ Lưu ý quan trọng

1. **Endpoint mới GET /admin/users/{userId}**:
   - Cần cập nhật vào `API_ENDPOINTS_DOCUMENTATION.md` section 2
   - Cần thông báo cho team Frontend để implement
   - Cần test kỹ response structure và error handling

2. **Response format của tutor pending**:
   - Frontend cần chú ý parse đúng Spring Data Page format
   - Không dùng `PaginationUtil` như các endpoint khác
   - Field mapping: `number` = currentPage, `totalElements` = totalItems

3. **Consistency**:
   - 7/7 endpoints cũ giữ nguyên structure và logic
   - Không có breaking changes
   - Backward compatible

---

## Khuyến nghị

### 📝 Cập nhật tài liệu
1. **Thêm endpoint mới vào API_ENDPOINTS_DOCUMENTATION.md**:
   - Thêm section 2.3: "Lấy thông tin chi tiết user theo userId"
   - Đánh lại số thứ tự cho các section sau (2.3 → 2.4, 2.4 → 2.5, ...)

2. **Chi tiết hóa response format**:
   - Section 2.5 (cũ 2.4): Làm rõ Spring Data Page format cho GET /admin/tutor/pending
   - Thêm mapping table giữa Spring Data Page fields và custom pagination fields

### 🧪 Testing
1. Test endpoint mới: `GET /admin/users/{userId}`
   - Test với userId hợp lệ
   - Test với userId không tồn tại (404)
   - Test với userId của các role khác nhau (STUDENT, TUTOR, ADMIN)

2. Verify backward compatibility
   - Tất cả 7 endpoints cũ vẫn hoạt động bình thường
   - Response structure không đổi

### 👥 Communication
1. Thông báo team Frontend về endpoint mới
2. Cập nhật Postman collection (nếu có)
3. Cập nhật API documentation tool (Swagger/OpenAPI)

---

**Version**: 1.0  
**Last Updated**: November 28, 2025  
**Compared Files**: 
- ACTUAL_API_ENDPOINTS.md (from AdminController.java)
- API_ENDPOINTS_DOCUMENTATION.md (existing documentation)

