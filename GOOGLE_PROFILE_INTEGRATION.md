# Google Profile Integration

## 🎯 Overview

Implementasi untuk mengambil data profil user (nama dan foto) dari Google Account setelah login,
kemudian menampilkannya di Dashboard dan Profile Activity.

## ✅ Summary - Apa yang Sudah Dikerjakan

### Backend API Required:

- **GET `/api/user/profile`** - Endpoint untuk mengambil data user (name, email, photo URL)
- Header: `Authorization: Bearer {token}`
- Response: `{ status, message, data: { user_id, name, email, photo } }`

### Frontend Changes:

1. ✅ **PreferenceManager** - Tambah field untuk simpan photo URL
2. ✅ **UserProfileResponse** - Model untuk response API profile
3. ✅ **ApiService & Repository** - Endpoint getUserProfile()
4. ✅ **GoogleAuthCallbackActivity** - Fetch profile setelah login
5. ✅ **DashboardActivity** - Display nama & foto di header
6. ✅ **ProfileActivity** - Display foto profile
7. ✅ **Glide Library** - Load image dari URL dengan circular crop

### User Experience:

- Setelah login dengan Google → Auto fetch & display profile
- Dashboard menampilkan: "Halo, {Nama User}!" + foto profile
- Profile Activity menampilkan foto yang sama
- Fallback ke default image jika tidak ada foto

## Flow Implementasi

```
1. User klik "Sign in with Google" di WelcomeUserActivity
   ↓
2. Buka browser untuk Google OAuth
   ↓
3. Setelah berhasil, redirect ke GoogleAuthCallbackActivity dengan token
   ↓
4. Simpan token ke SharedPreferences
   ↓
5. Fetch user profile dari API: GET /api/user/profile
   ↓
6. Simpan user data (name, email, photo URL) ke SharedPreferences
   ↓
7. Navigate ke DashboardActivity
   ↓
8. Dashboard dan Profile Activity load data dari SharedPreferences
```

## Files Created/Modified

### 1. **PreferenceManager.kt** - Updated

- ✅ Tambah field `KEY_USER_PHOTO` untuk menyimpan URL foto
- ✅ Method `saveUserPhoto(photoUrl: String)`
- ✅ Method `getUserPhoto(): String?`
- ✅ Update `clearUserData()` untuk hapus foto saat logout

### 2. **UserProfileResponse.kt** - Created

Response model untuk endpoint `/api/user/profile`:

```kotlin
data class UserProfileResponse(
    val status: String,
    val message: String,
    val data: UserProfileData
)

data class UserProfileData(
    val userId: String,
    val name: String,
    val email: String,
    val photo: String?  // Nullable karena bisa tidak ada foto
)
```

### 3. **ApiService.kt** - Updated

Tambah endpoint:

```kotlin
@GET("api/user/profile")
suspend fun getUserProfile(
    @Header("Authorization") token: String
): UserProfileResponse
```

### 4. **ApiRepository.kt** - Updated

Tambah method:

```kotlin
suspend fun getUserProfile(token: String): Result<UserProfileResponse>
```

### 5. **GoogleAuthCallbackActivity.kt** - Updated

- ✅ Setelah terima token dari deeplink, panggil `fetchUserProfile()`
- ✅ Fetch user profile dari API
- ✅ Simpan user data (id, name, email, photo) ke SharedPreferences
- ✅ Navigate ke Dashboard
- ✅ Handle error gracefully (tetap navigate ke Dashboard jika fetch profile gagal)

### 6. **DashboardActivity.kt** - Updated

- ✅ Import Glide untuk load image
- ✅ Method `loadUserProfile()` untuk load nama dan foto
- ✅ Display nama di greeting: "Halo, {userName}!"
- ✅ Load foto profile dengan Glide (circular crop)
- ✅ Fallback ke default image jika tidak ada foto

### 7. **ProfileActivity.kt** - Updated

- ✅ Import Glide untuk load image
- ✅ Update `loadUserProfile()` untuk load foto
- ✅ Display foto profile dengan Glide (circular crop)
- ✅ Fallback ke default image jika tidak ada foto

### 8. **build.gradle.kts & libs.versions.toml** - Updated

- ✅ Tambah Glide dependency (versi 4.16.0)

## API Endpoint Required

### GET `/api/auth/profile`

**Headers:**

```
Authorization: Bearer {token}
```

**Response Success:**

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
      "photo": "https://lh3.googleusercontent.com/a/...",
      "created_at": "2025-11-08T11:37:19.314Z",
      "updated_at": "2025-11-14T12:18:04.168Z"
    }
  }
}
```

**Note**: Field `photo` perlu ditambahkan di backend untuk menyimpan URL foto profile dari Google.

**Response Error:**

```json
{
  "status": "FAILED",
  "message": "Unauthorized",
  "data": null
}
```

## Data Flow

### SharedPreferences Keys:

- `auth_token` - JWT token dari Google OAuth
- `user_id` - User ID dari backend
- `user_name` - Nama user dari Google
- `user_email` - Email user dari Google
- `user_photo` - URL foto profile dari Google

### Image Loading (Glide):

```kotlin
Glide.with(context)
    .load(photoUrl)           // Load dari URL
    .circleCrop()             // Crop jadi lingkaran
    .placeholder(R.drawable.bg_welcome_screen)  // Placeholder saat loading
    .error(R.drawable.bg_welcome_screen)        // Fallback jika error
    .into(imageView)
```

## UI Display

### Dashboard:

- **Greeting**: "Halo, {userName}!"
- **Profile Image**: Circular photo di pojok kanan atas (56dp x 56dp)

### Profile Activity:

- **Name Field**: Auto-filled dengan nama dari Google
- **Profile Image**: Circular photo di atas form (dapat diklik untuk ganti foto)

## Troubleshooting

### ⚠️ IMPORTANT: Glide Unresolved Reference

Jika muncul error `Unresolved reference 'bumptech'`, ikuti langkah berikut **secara berurutan**:

1. **Sync Gradle** (WAJIB dilakukan pertama kali):
    - Android Studio: File → Sync Project with Gradle Files
    - Tunggu hingga proses sync selesai (cek progress bar di bawah)
    - Jika ada error saat sync, cek koneksi internet

2. **Clean & Rebuild Project**:
    - Build → Clean Project
    - Tunggu selesai
    - Build → Rebuild Project

3. **Invalidate Caches & Restart** (jika masih error):
    - File → Invalidate Caches...
    - Centang semua opsi
    - Klik "Invalidate and Restart"
    - Tunggu Android Studio restart

4. **Verifikasi Dependency** (pastikan sudah ditambahkan):
    - Cek `gradle/libs.versions.toml`:
      ```toml
      [versions]
      glide = "4.16.0"
      
      [libraries]
      glide = { group = "com.github.bumptech.glide", name = "glide", version.ref = "glide" }
      ```
    - Cek `app/build.gradle.kts`:
      ```kotlin
      dependencies {
          // ...existing dependencies...
          implementation(libs.glide)
      }
      ```

5. **Re-sync Gradle** setelah edit file di atas

### Photo Not Loading

1. Cek internet permission di AndroidManifest.xml
2. Cek URL foto valid (https://...)
3. Cek Glide logs di Logcat dengan tag "Glide"

### Profile Fetch Failed

- App tetap akan navigate ke Dashboard
- User dapat manual update nama di Profile Activity
- Toast akan menampilkan "Login berhasil!" tanpa emoji jika fetch gagal

## Security Notes

- Token disimpan di SharedPreferences (MODE_PRIVATE)
- Token dikirim sebagai Bearer token di Authorization header
- Photo URL dari Google CDN (secure https)
- Data di-clear saat logout

## Future Improvements

- [ ] Add image picker untuk ganti foto profile
- [ ] Upload foto ke backend storage
- [ ] Cache foto dengan Glide untuk offline access
- [ ] Add loading state saat fetch profile
- [ ] Refresh profile data secara berkala
- [ ] Add location field di profile

