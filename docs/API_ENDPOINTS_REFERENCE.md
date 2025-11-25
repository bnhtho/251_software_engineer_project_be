# Complete API Endpoints Overview

## Base URL
```
http://localhost:8081
```

## All Endpoints

### 🎓 Student Endpoints

| Method | Endpoint | Description | Auth Required | Role |
|--------|----------|-------------|---------------|------|
| GET | `/students/profile/{userId}` | Get student profile | Yes | Student (self) |
| GET | `/students/history/{userId}` | Get learning history | Yes | Student (self) |
| POST | `/students/profile/{userId}` | Update student profile | Yes | Student (self) |
| POST | `/api/tutor-profiles` | Register as tutor (creates pending profile) | Yes | Student |

### 👨‍🏫 Tutor Endpoints

| Method | Endpoint | Description | Auth Required | Role |
|--------|----------|-------------|---------------|------|
| GET | `/tutors` | Get all tutors (paginated) | Optional | Public |
| POST | `/tutors` | Create tutor profile | Yes | Tutor |
| PUT | `/tutors/{tutorProfileId}` | Update tutor (by profile ID) | Yes | Tutor (self) |
| DELETE | `/tutors/{tutorProfileId}` | Delete tutor (by profile ID) | Yes | Tutor (self) |
| GET | `/tutors/profile/{userId}` | Get detailed tutor profile | Yes | Tutor (self) |
| POST | `/tutors/profile/{userId}` | Update tutor profile (by user ID) | Yes | Tutor (self) |

### 🔐 Admin Endpoints

| Method | Endpoint | Description | Auth Required | Role |
|--------|----------|-------------|---------------|------|
| POST | `/admin/students/{userId}` | Update student profile | Yes | Admin |
| DELETE | `/admin/students/{userId}` | Delete student profile | Yes | Admin |
| POST | `/admin/tutors/{userId}` | Update tutor profile | Yes | Admin |
| DELETE | `/admin/tutors/{userId}` | Delete tutor profile | Yes | Admin |
| GET | `/api/admin/tutor_profiles/pending?page={page}` | List pending tutor registrations (10 per page) | Yes | Admin |
| PATCH | `/api/admin/tutor_profiles/{userId}/approve` | Approve pending tutor for user | Yes | Admin |
| PATCH | `/api/admin/tutor_profiles/{userId}/reject` | Reject pending tutor for user | Yes | Admin |

### 📚 Other Existing Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/sessions` | Get all sessions |
| POST | `/sessions` | Create session |
| PUT | `/sessions/{id}` | Update session |
| DELETE | `/sessions/{id}` | Delete session |
| GET | `/subjects` | Get all subjects |
| GET | `/departments` | Get all departments |
| GET | `/majors` | Get all majors |

## Quick Reference

### Student Operations
```
# View my profile
GET /students/profile/123

# View my learning history
GET /students/history/123

# Update my profile
POST /students/profile/123
Body: { "firstName": "New Name", ... }

# Register as a tutor
POST /api/tutor-profiles
Body: {
  "title": "Senior Student",
  "majorId": 5,
  "description": "Có 2 năm kinh nghiệm dạy lập trình.",
  "subjects": [11, 20, 22],
  "experienceYears": 2
}
```

### Tutor Operations
```
# View my detailed profile (with schedule)
GET /tutors/profile/456

# Update my profile
POST /tutors/profile/456
Body: { "bio": "Updated bio", "experienceYears": 5, ... }
```

### Admin Operations
```
# Update any student
POST /admin/students/123
Body: { "firstName": "Admin Updated", ... }

# Delete any student
DELETE /admin/students/123

# Update any tutor
POST /admin/tutors/456
Body: { "bio": "Admin updated", ... }

# Delete any tutor
DELETE /admin/tutors/456

# View pending tutor registrations (page 0, 10 per page)
GET /api/admin/tutor_profiles/pending?page=0

# Approve/reject pending tutor (by userId)
PATCH /api/admin/tutor_profiles/42/approve
PATCH /api/admin/tutor_profiles/42/reject
```

## Response Format

All endpoints return responses in this format:

```json
{
  "statusCode": 200,
  "message": "Operation successful",
  "data": { /* Response data */ }
}
```

## Error Codes

| Status Code | Description |
|-------------|-------------|
| 200 | Success |
| 400 | Bad Request (invalid data) |
| 403 | Forbidden (unauthorized access) |
| 404 | Not Found (resource doesn't exist) |
| 500 | Internal Server Error |

## Authentication

All protected endpoints require a JWT token in the Authorization header:

```
Authorization: Bearer <jwt_token>
```

The token contains user information including:
- User ID
- Role (student/tutor/admin)
- Other claims

## Authorization Rules

### Student
- ✅ Can view own profile
- ✅ Can view own learning history
- ✅ Can update own profile
- ❌ Cannot view other students' data
- ❌ Cannot access admin functions

### Tutor
- ✅ Can view own detailed profile
- ✅ Can update own profile
- ✅ Can view own teaching schedule
- ❌ Cannot view other tutors' private data
- ❌ Cannot access admin functions

### Tutor Profile Status
- `PENDING`: Student has registered but awaits approval/review.
- `APPROVED`: Tutor profile is active and visible to others.
- `REJECTED`: Registration was denied; student must update info and resubmit.

### Admin
- ✅ Can view all profiles
- ✅ Can update any profile
- ✅ Can delete any profile
- ✅ Full system access

## Testing Examples

### Using cURL

```bash
# Get student profile
curl -X GET http://localhost:8080/students/profile/123 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Tutor registration (student role)
curl -X POST http://localhost:8080/api/tutor-profiles \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
        "title": "Senior Student",
        "majorId": 5,
        "description": "Mình có 2 năm kinh nghiệm dạy lập trình cho sinh viên năm 1, 2. Chuyên về Java, Python và cơ sở dữ liệu.",
        "subjects": [11, 20, 22],
        "experienceYears": 2
      }'

# Update tutor profile
curl -X POST http://localhost:8080/tutors/profile/456 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "bio": "Updated biography",
    "experienceYears": 10,
    "isAvailable": true
  }'

# Admin delete student
curl -X DELETE http://localhost:8080/admin/students/123 \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN"

# Admin list pending tutor profiles (page 0)
curl -X GET "http://localhost:8080/api/admin/tutor_profiles/pending?page=0" \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN"

# Admin approve pending tutor for user 42
curl -X PATCH http://localhost:8080/api/admin/tutor_profiles/42/approve \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN"

# Admin reject pending tutor for user 42
curl -X PATCH http://localhost:8080/api/admin/tutor_profiles/42/reject \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN"
```

### Using Postman

1. Set request type (GET/POST/PUT/DELETE)
2. Enter URL with path variables
3. Add Authorization header with JWT token
4. For POST/PUT: Add JSON body
5. Send request

## Data Validation

### Required Fields (Update Requests)
- All fields are optional (only provided fields will be updated)
- Empty strings are validated and rejected
- Invalid IDs (majorId, subjectIds) will return 404

### Field Constraints
- `phone`: Max 20 characters
- `firstName`, `lastName`: Max 100 characters
- `experienceYears`: Positive integer
- `dayOfWeek`: 0-6
- `majorId`: Must exist in database
- `subjectIds`: All must exist in database
