# 🔐 Authentication Flow Documentation

## Overview

Aplikasi Potaful menggunakan Google OAuth untuk authentication dengan token-based authentication
yang disimpan di local storage (SharedPreferences).

## Authentication Flow

### 1️⃣ Login Process

```
WelcomeUserActivity
    ↓ (Click "Sign In with Google")
    ↓ fetchGoogleAuthUrl() via WelcomeViewModel
    ↓
Google Auth URL API
    ↓ (Returns auth_url)
    ↓
Browser/Chrome Custom Tab
    ↓ (User signs in with Google)
    ↓
Backend (Google OAuth Callback)
    ↓ (Redirects with token)
    ↓
Deep Link Handler
    ↓ potaful://auth/google/callback?token=JWT_TOKEN
    ↓ or https://api.lutfialvarop.cloud/api/auth/google/callback?token=JWT_TOKEN
    ↓
GoogleAuthCallbackActivity
    ↓ 
    ├─ Extract token from URI query parameter
    ├─ Save token to PreferenceManager
    ├─ Set isLoggedIn = true
    ├─ Set onboardingCompleted = true
    └─ Navigate to DashboardActivity (clear back stack)
```

### 2️⃣ Token Storage

**PreferenceManager Methods:**

```kotlin
// Save token after successful login
preferenceManager.saveAuthToken(token: String)

// Retrieve token for API calls
val token = preferenceManager.getAuthToken() // Returns String?

// Set login status
preferenceManager.setLoggedIn(true)

// Check login status
val isLoggedIn = preferenceManager.isLoggedIn() // Returns Boolean
```

**Storage Keys:**

- `auth_token` - JWT token dari backend
- `is_logged_in` - Boolean status login
- `is_onboarding_completed` - Boolean untuk skip onboarding
- `user_name` - Nama user (optional)
- `user_email` - Email user (optional)
- `user_id` - User ID (optional)

### 3️⃣ Using Token for API Calls

**Example in DashboardActivity:**

```kotlin
private fun loadMyPots() {
    val token = preferenceManager.getAuthToken().orEmpty()
    viewModel.loadMyPots(token)
}

private fun showAddPotDialog() {
    // ...
    val token = preferenceManager.getAuthToken().orEmpty()
    viewModel.addPot(token, potId)
}
```

**Example in ViewModel:**

```kotlin
fun loadMyPots(token: String) {
    viewModelScope.launch {
        _potsState.value = PotsState.Loading
        try {
            val response = repository.getMyPots("Bearer $token")
            if (response.status == "SUCCESS") {
                _potsState.value = PotsState.Success(
                    response.data.pots,
                    response.data.total
                )
            } else {
                _potsState.value = PotsState.Error(response.message)
            }
        } catch (e: Exception) {
            _potsState.value = PotsState.Error(e.message ?: "Unknown error")
        }
    }
}
```

### 4️⃣ Logout Process

```
ProfileActivity
    ↓ (Click "Logout" button)
    ↓ Show confirmation dialog
    ↓ (User confirms)
    ↓
performLogout()
    ↓
    ├─ preferenceManager.clearUserData()
    │   ├─ Remove auth_token
    │   ├─ Remove user_id, user_name, user_email
    │   └─ Set is_logged_in = false
    └─ Navigate to WelcomeUserActivity (clear back stack)
```

## API Configuration

### Base URL

```
https://api.lutfialvarop.cloud
```

### Authentication Endpoints

**1. Get Google Auth URL**

```
GET /api/auth/google

Response:
{
    "status": "SUCCESS",
    "message": "URL autentikasi Google berhasil dibuat",
    "data": {
        "auth_url": "https://accounts.google.com/o/oauth2/v2/auth?..."
    }
}
```

**2. Google OAuth Callback (Backend)**

```
GET /api/auth/google/callback?code=<GOOGLE_AUTH_CODE>

Redirects to:
- potaful://auth/google/callback?token=<JWT_TOKEN>
- or https://api.lutfialvarop.cloud/api/auth/google/callback?token=<JWT_TOKEN>
```

### Protected Endpoints (Require Token)

All protected endpoints require `Authorization` header:

```
Authorization: Bearer <JWT_TOKEN>
```

**Example Protected Endpoints:**

- `GET /api/mypot` - Get user's pots
- `POST /api/mypot` - Add new pot
- `DELETE /api/mypot/:id` - Delete pot
- etc.

## Deep Link Configuration

### AndroidManifest.xml

```xml

<activity android:name=".ui.auth.GoogleAuthCallbackActivity" android:exported="true"
    android:launchMode="singleTask">

    <!-- Custom scheme (recommended for development) -->
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data android:scheme="potaful" android:host="auth" android:pathPrefix="/google/callback" />
    </intent-filter>

    <!-- HTTPS App Links (for production) -->
    <intent-filter android:autoVerify="false">
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data android:scheme="https" android:host="api.lutfialvarop.cloud"
            android:pathPrefix="/api/auth/google/callback" />
    </intent-filter>
</activity>
```

## Security Best Practices

### ✅ Implemented

1. **Token Storage**: Token stored in SharedPreferences (private mode)
2. **Token Validation**: Backend validates JWT token
3. **HTTPS Only**: All API calls use HTTPS
4. **Clear on Logout**: Token cleared when user logs out

### 🔒 Recommendations

1. **Token Expiration**: Implement token refresh mechanism
2. **Token Encryption**: Consider encrypting token before storing (use EncryptedSharedPreferences)
3. **Biometric Auth**: Add biometric authentication option
4. **Session Timeout**: Auto logout after inactivity

### 🔐 Example: Encrypted Token Storage (Optional)

```kotlin
// In PreferenceManager.kt
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

private fun getEncryptedPreferences(context: Context): SharedPreferences {
    val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    return EncryptedSharedPreferences.create(
        context,
        "encrypted_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
}
```

## Error Handling

### Token Not Found in Callback

```kotlin
// In GoogleAuthCallbackActivity
if (!token.isNullOrEmpty()) {
    // Success: save and navigate to dashboard
} else {
    // Error: show message and return to welcome
    Toast.makeText(this, "Login gagal. Token tidak ditemukan.", Toast.LENGTH_LONG).show()
    val intent = Intent(this, WelcomeUserActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    startActivity(intent)
}
```

### API Call with Invalid Token

```kotlin
// In Repository/ViewModel
try {
    val response = apiService.getMyPots("Bearer $token")
    // Handle response
} catch (e: HttpException) {
    if (e.code() == 401) {
        // Token expired or invalid
        // Option 1: Refresh token
        // Option 2: Force logout
        // Option 3: Show reauth dialog
    }
}
```

## Testing

### Test Login Flow

```bash
# Test custom scheme deep link
adb shell am start -W -a android.intent.action.VIEW \
  -d "potaful://auth/google/callback?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test"

# Test HTTPS deep link
adb shell am start -W -a android.intent.action.VIEW \
  -d "https://api.lutfialvarop.cloud/api/auth/google/callback?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test"
```

### Test Token Storage

```kotlin
// In test or debug code
val token = preferenceManager.getAuthToken()
Log.d("AuthTest", "Stored Token: $token")

val isLoggedIn = preferenceManager.isLoggedIn()
Log.d("AuthTest", "Is Logged In: $isLoggedIn")
```

## Files Modified/Created

### ✅ Created/Updated Files:

1. **GoogleAuthCallbackActivity.kt** ✅
    - Extract token from deep link
    - Save to PreferenceManager
    - Navigate to Dashboard

2. **PreferenceManager.kt** ✅
    - saveAuthToken()
    - getAuthToken()
    - setLoggedIn()
    - isLoggedIn()
    - clearUserData()

3. **ProfileActivity.kt** ✅
    - Added logout button
    - Logout confirmation dialog
    - Clear user data on logout

4. **DashboardActivity.kt** ✅
    - Uses token for API calls
    - Loads token from PreferenceManager

5. **WelcomeUserActivity.kt** ✅
    - Initiates Google OAuth flow
    - Opens browser with auth URL

## Summary

✅ **Login Flow**: Complete dengan Google OAuth → Token → Save to local
✅ **Token Storage**: Tersimpan di SharedPreferences via PreferenceManager
✅ **API Integration**: Token digunakan sebagai Bearer token di header
✅ **Logout Flow**: Clear token dan navigate ke welcome screen
✅ **Deep Link**: Support custom scheme & HTTPS App Links

Token yang didapat dari callback Google OAuth akan otomatis tersimpan dan digunakan untuk semua API
calls yang membutuhkan authentication! 🎉

