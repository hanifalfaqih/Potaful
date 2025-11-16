# Potaful - Smart Pot Management App

## 📱 Features Implemented

### ✅ Authentication

- Google Sign-In OAuth integration
- Deep linking for auth callback
- Token-based authentication
- Auto-fetch user profile (name, email, photo)

### ✅ Dashboard

- Weather integration (OpenWeatherMap API)
- My Pots list with expand/collapse detail
- Pot summary horizontal scroll
- Pull-to-refresh functionality
- Display user profile (name & photo)

### ✅ Pot Management

- Get My Pots list
- Add new pot by ID
- View detailed sensor data (expand/collapse)
- 10 sensor readings: N, P, K, Temperature, Moisture, pH, Salinity, Conductivity, Water Level, Soil
  Health

### ✅ Profile

- Display Google profile photo
- Edit profile name
- Logout functionality

## 🔧 Technical Stack

- **Language**: Kotlin
- **Architecture**: MVVM
- **Networking**: Retrofit + OkHttp
- **Image Loading**: Glide
- **Local Storage**: SharedPreferences
- **UI Components**: Material Design 3

## 📚 Documentation Files

1. **AUTHENTICATION_FLOW.md** - Google OAuth flow
2. **DEEPLINK_SETUP.md** - Deep linking configuration
3. **API_SETUP_README.md** - API integration guide
4. **MYPOTS_API_QUICK_REFERENCE.md** - Pot API endpoints
5. **PULL_TO_REFRESH_IMPLEMENTATION.md** - Pull-to-refresh guide
6. **POT_DETAIL_IMPLEMENTATION.md** - Expand/collapse implementation
7. **GOOGLE_PROFILE_INTEGRATION.md** - Profile integration guide

## 🚀 Quick Start

### 1. Setup API Keys

Create `local.properties`:

```properties
OPEN_WEATHER_API_KEY=your_api_key_here
```

### 2. Sync Gradle

```bash
File → Sync Project with Gradle Files
```

### 3. Build & Run

```bash
Build → Rebuild Project
Run → Run 'app'
```

## 🐛 Common Issues

### Glide Unresolved Reference

1. Sync Gradle Files
2. Clean & Rebuild Project
3. Invalidate Caches & Restart

### NumberFormatException

- Pastikan tipe data model sesuai dengan API response
- NPK (nitrogen, phosphorus, kalium) = `Int`
- Sensor lainnya = `Float`
- soil_health di PotItem = `Float`

### Token Not Found

- Pastikan deep linking dikonfigurasi dengan benar
- Cek AndroidManifest.xml untuk intent-filter

## 📞 Support

Cek file dokumentasi lengkap di root project untuk detail implementasi setiap fitur.

