# Backend Integration Request - Photo Field

## 📝 Overview

Request untuk menambahkan field `photo` di user profile untuk menyimpan URL foto dari Google
Account.

## 🎯 Endpoint Yang Perlu Diupdate

### GET `/api/auth/profile`

**Current Response:**

```json
{
  "status": "SUCCESS",
  "message": "Profil berhasil diambil",
  "data": {
    "user": {
      "id": "6bdcd606-2e7d-4019-a82c-4a6b0ff4c3c8",
      "first_name": "Alvaro",
      "last_name": "Lutfi",
      "email": "coba@gmail.com",
      "created_at": "2025-11-08T11:37:19.314Z",
      "updated_at": "2025-11-14T12:18:04.168Z"
    }
  }
}
```

**Expected Response (with photo field):**

```json
{
  "status": "SUCCESS",
  "message": "Profil berhasil diambil",
  "data": {
    "user": {
      "id": "6bdcd606-2e7d-4019-a82c-4a6b0ff4c3c8",
      "first_name": "Alvaro",
      "last_name": "Lutfi",
      "email": "coba@gmail.com",
      "photo": "https://lh3.googleusercontent.com/a/ACg8ocKxxx...",
      "created_at": "2025-11-08T11:37:19.314Z",
      "updated_at": "2025-11-14T12:18:04.168Z"
    }
  }
}
```

## 📋 Database Schema Update

### Table: `users`

**Add Column:**

```sql
ALTER TABLE users 
ADD COLUMN photo VARCHAR(500) NULL;
```

**Field Details:**

- **Name**: `photo`
- **Type**: `VARCHAR(500)` or `TEXT`
- **Nullable**: `YES` (user might not have profile photo)
- **Default**: `NULL`
- **Description**: URL to user's profile photo from Google

## 🔧 Implementation Details

### 1. Google OAuth Callback

Saat user login dengan Google, Google OAuth akan return user info termasuk foto:

**Google OAuth Response:**

```json
{
  "id": "xxx",
  "email": "user@gmail.com",
  "verified_email": true,
  "name": "Alvaro Lutfi",
  "given_name": "Alvaro",
  "family_name": "Lutfi",
  "picture": "https://lh3.googleusercontent.com/a/ACg8ocKxxx...",
  "locale": "en"
}
```

### 2. Save Photo URL

**Backend harus:**

1. Extract `picture` field dari Google OAuth response
2. Save ke database saat user pertama kali register
3. Update jika foto berubah (optional, bisa di endpoint update profile)

### 3. Return Photo in Profile Endpoint

**GET `/api/auth/profile`** harus include field `photo` dari database.

## 🎨 Frontend Usage

### Display di Dashboard:

```kotlin
// Load from SharedPreferences
val photoUrl = preferenceManager.getUserPhoto()

// Display with Glide (circular crop)
Glide.with(context)
    .load(photoUrl)
    .circleCrop()
    .placeholder(R.drawable.default_avatar)
    .error(R.drawable.default_avatar)
    .into(imageView)
```

### Display di Profile:

- Same implementation
- Circular ImageView (96dp x 96dp)
- Clickable untuk change photo (future feature)

## 🔒 Security Considerations

1. **URL Validation**: Validate URL format sebelum save
2. **HTTPS Only**: Only accept https:// URLs
3. **Google CDN**: URL dari Google CDN (`lh3.googleusercontent.com`)
4. **Nullable**: Field harus nullable, tidak semua user punya foto
5. **No Direct Upload Yet**: Untuk MVP, hanya simpan URL dari Google

## 📊 Data Flow

```
1. User login dengan Google
   ↓
2. Backend receive Google OAuth token
   ↓
3. Backend exchange token untuk user info
   ↓
4. Extract: email, first_name, last_name, PHOTO
   ↓
5. Save/Update user di database (include photo URL)
   ↓
6. Return JWT token ke frontend
   ↓
7. Frontend call GET /api/auth/profile
   ↓
8. Backend return user data (include photo URL)
   ↓
9. Frontend save to SharedPreferences
   ↓
10. Display photo di Dashboard & Profile
```

## ✅ Testing

### Test Cases:

1. **User dengan foto**:
    - Response harus include `photo` URL
    - Frontend harus display foto circular

2. **User tanpa foto**:
    - Response: `photo: null`
    - Frontend harus display default avatar

3. **Invalid URL**:
    - Backend validate URL format
    - Return error jika invalid

### Example Test Request:

```bash
curl -X GET https://api.lutfialvarop.cloud/api/auth/profile \
  -H "Authorization: Bearer {token}"
```

### Expected Response:

```json
{
  "status": "SUCCESS",
  "message": "Profil berhasil diambil",
  "data": {
    "user": {
      "id": "xxx",
      "first_name": "John",
      "last_name": "Doe",
      "email": "john@gmail.com",
      "photo": "https://lh3.googleusercontent.com/a/...",
      "created_at": "2025-11-08T11:37:19.314Z",
      "updated_at": "2025-11-14T12:18:04.168Z"
    }
  }
}
```

## 🚀 Priority: HIGH

Frontend sudah ready untuk handle field `photo`.
Setelah backend add field ini, feature akan langsung berfungsi tanpa perlu update frontend lagi.

## 📞 Contact

Jika ada pertanyaan tentang implementasi, silakan hubungi frontend developer.

