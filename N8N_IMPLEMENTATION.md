# N8N Plant Recommendation Integration

## Overview

Implementasi N8N API untuk mendapatkan rekomendasi tanaman berdasarkan input user di onboarding (
Fragment 1-4).

## API Endpoint

**Base URL**: `https://potaful2.app.n8n.cloud/`
**Endpoint**: `/webhook/plant-recommendation`
**Method**: POST

## Parameters

| Parameter      | Type   | Required | Options/Value                              | Description                 |
|----------------|--------|----------|--------------------------------------------|-----------------------------|
| location       | String | Yes      | e.g., "Bekasi", "Tangerang"                | User's location             |
| skill_level    | String | Yes      | "Beginner", "Intermediate", "Professional" | User's gardening experience |
| home_frequency | String | Yes      | "Seldom", "Often"                          | How often user leaves home  |
| preference     | String | Yes      | "Vegetables", "Fruits"                     | User's plant preference     |

## Example Request

```
POST https://potaful2.app.n8n.cloud/webhook/plant-recommendation?location=Bekasi&skill_level=Beginner&home_frequency=Often&preference=Vegetables
```

## Example Response

```json
[
    {
        "output": [
            {
                "role": "assistant",
                "type": "message",
                "status": "completed",
                "content": [
                    {
                        "type": "output_text",
                        "text": {
                            "recommendation": [
                                "Basil",
                                "Cherry Tomato",
                                "Lettuce",
                                "Pepper"
                            ],
                            "reason": [
                                "Basil thrives in warm climates and is easy to grow.",
                                "Cherry tomatoes are well-suited for containers and love sunny spots.",
                                "Lettuce grows quickly and can tolerate some shade, making it beginner-friendly.",
                                "Peppers need warmth and sunlight, and are suitable for novice gardeners."
                            ]
                        },
                        "annotations": []
                    }
                ],
                "id": "msg_tmp_imkrxasvut"
            }
        ]
    }
]
```

## Implementation Flow

### 1. Fragment 1 - Location Input

- User inputs their location (e.g., "Bekasi", "Tangerang")
- Input disimpan di `OnboardingSharedViewModel.location`

### 2. Fragment 2 - Skill Level Selection

- User memilih skill level: Beginner / Intermediate / Professional
- Pilihan disimpan di `OnboardingSharedViewModel.skillLevel`
- Visual feedback: Card yang dipilih berubah warna menjadi `forest_green_light`

### 3. Fragment 3 - Preference Selection

- User memilih preference: Fruits / Vegetables
- Pilihan disimpan di `OnboardingSharedViewModel.preference`
- Visual feedback: Card yang dipilih berubah warna

### 4. Fragment 4 - Home Frequency Selection

- User memilih home frequency: Seldom / Often
- Pilihan disimpan di `OnboardingSharedViewModel.homeFrequency`
- Visual feedback: Card yang dipilih berubah warna

### 5. API Call

- Saat user klik button "Mulai" di halaman terakhir
- Validasi semua input telah terisi
- Call API N8N dengan parameter dari SharedViewModel
- Menampilkan loading dialog
- Menyimpan response ke SharedPreferences sebagai JSON
- Mark onboarding completed
- Navigate ke Dashboard

## Files Created/Modified

### New Files:

1. **PlantRecommendationResponse.kt**
    - Data class untuk response dari N8N API
    - `PlantRecommendationResponse`, `Output`, `Content`, `TextData`

2. **N8NApiService.kt**
    - Retrofit interface untuk N8N API
    - Function `getPlantRecommendation()` dengan query parameters

3. **OnboardingSharedViewModel.kt**
    - ViewModel untuk share data antar fragments
    - Menyimpan: location, skillLevel, homeFrequency, preference
    - Handle API call dan response
    - Loading & error states

### Modified Files:

1. **ApiConfig.kt**
    - Tambah `N8N_BASE_URL`
    - Tambah `provideN8NRetrofit()`
    - Tambah `getN8NApiService()`

2. **OnboardingPage1Fragment.kt**
    - Menangkap input location dari EditText
    - Simpan ke SharedViewModel

3. **OnboardingPage2Fragment.kt**
    - Menangkap pilihan skill level dari CardView clicks
    - Visual feedback (highlight selected card)
    - Simpan ke SharedViewModel

4. **OnboardingPage3Fragment.kt**
    - Menangkap pilihan preference dari CardView clicks
    - Visual feedback (highlight selected card)
    - Simpan ke SharedViewModel

5. **OnboardingPage4Fragment.kt**
    - Menangkap pilihan home frequency dari CardView clicks
    - Visual feedback (highlight selected card)
    - Simpan ke SharedViewModel

6. **OnboardingUserActivity.kt**
    - Inject SharedViewModel
    - Validate inputs sebelum call API
    - Observe loading, response, dan error dari ViewModel
    - Show/hide loading dialog
    - Simpan response ke PreferenceManager
    - Navigate ke Dashboard setelah sukses

7. **PreferenceManager.kt**
    - Tambah `KEY_PLANT_RECOMMENDATION`
    - Tambah `savePlantRecommendation(jsonString: String)`
    - Tambah `getPlantRecommendation(): String?`

## Data Storage

Plant recommendation disimpan di SharedPreferences sebagai JSON string:

```kotlin
val gson = Gson()
val jsonString = gson.toJson(response)
preferenceManager.savePlantRecommendation(jsonString)
```

Untuk mengambil kembali:

```kotlin
val jsonString = preferenceManager.getPlantRecommendation()
val gson = Gson()
val type = object : TypeToken<List<PlantRecommendationResponse>>() {}.type
val recommendations: List<PlantRecommendationResponse> = gson.fromJson(jsonString, type)
```

## UI/UX Features

1. **Visual Feedback**: Card yang dipilih berubah warna dari `forest_brown_medium` menjadi
   `forest_green_light`
2. **State Restoration**: Pilihan user disimpan dan di-restore saat kembali ke fragment sebelumnya
3. **Validation**: Validasi semua field terisi sebelum submit
4. **Loading State**: Loading dialog ditampilkan saat API call
5. **Error Handling**: Toast message untuk error

## Testing

Untuk test manual:

1. Run app
2. Login dengan Google
3. Isi semua onboarding fields:
    - Fragment 1: Masukkan lokasi (e.g., "Bekasi")
    - Fragment 2: Pilih skill level
    - Fragment 3: Pilih preference
    - Fragment 4: Pilih home frequency
4. Klik "Mulai"
5. Observe loading dialog
6. Check navigation ke Dashboard
7. Verify data tersimpan di SharedPreferences

## Notes

- ProgressDialog sudah deprecated, tapi masih berfungsi. Bisa diganti dengan custom dialog atau
  Material3 loading indicator di future update.
- Response dari N8N disimpan sebagai JSON string untuk flexibility
- Token authentication sudah disimpan sejak login, siap digunakan untuk API calls lain yang
  memerlukan auth

## Future Improvements

1. Replace ProgressDialog dengan Material3 CircularProgressIndicator
2. Tambah animation untuk card selection
3. Implement retry mechanism untuk failed API calls
4. Add input validation untuk location (min length, format, etc)
5. Cache recommendation untuk offline access

