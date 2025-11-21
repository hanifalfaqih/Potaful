# Plant Recommendation Flow - Final Implementation

## Overview

Implementasi lengkap flow rekomendasi tanaman dengan loading screen dan hasil yang dapat
di-expand/collapse.

## Flow Architecture

```
OnboardingUserActivity (Fragment 1-4)
    ↓ (user selesai input semua data)
    ↓ (validasi sukses)
    ↓
RecommendationPlantLoadingActivity
    ↓ (call N8N API)
    ↓ (tampilkan loading animation)
    ↓ (simpan hasil ke SharedPreferences)
    ↓ (mark onboarding completed)
    ↓
RecommendationPlantResultActivity
    ↓ (parse & display recommendations)
    ↓ (card view dengan expand/collapse untuk reason)
    ↓
DashboardActivity (via button)
```

## Implementation Details

### 1. OnboardingUserActivity

**Location**: `app/src/main/java/id/hanifalfaqih/potaful/ui/onboarding/OnboardingUserActivity.kt`

**Changes**:

- ✅ Hapus API call logic dari activity
- ✅ Hapus observeViewModel, showLoading, hideLoading
- ✅ Tambah `navigateToLoadingActivity()` untuk kirim data via Intent
- ✅ Setelah validasi sukses, navigasi ke `RecommendationPlantLoadingActivity`

**Data yang dikirim via Intent**:

```kotlin
putExtra("location", sharedViewModel.location.value)
putExtra("skill_level", sharedViewModel.skillLevel.value)
putExtra("home_frequency", sharedViewModel.homeFrequency.value)
putExtra("preference", sharedViewModel.preference.value)
```

---

### 2. RecommendationPlantLoadingActivity

**Location**:
`app/src/main/java/id/hanifalfaqih/potaful/ui/recommendation/RecommendationPlantLoadingActivity.kt`

**Purpose**:

- Tampilkan loading animation saat call API N8N
- Handle API call dengan coroutines
- Simpan hasil ke SharedPreferences
- Mark onboarding completed
- Navigate ke RecommendationPlantResultActivity

**Key Functions**:

```kotlin
private fun getPlantRecommendation(
    location: String,
    skillLevel: String,
    homeFrequency: String,
    preference: String
) {
    lifecycleScope.launch {
        try {
            val response = ApiConfig.getN8NApiService().getPlantRecommendation(...)
            
            if (response.isNotEmpty()) {
                // Save to preferences
                val gson = Gson()
                val jsonString = gson.toJson(response)
                preferenceManager.savePlantRecommendation(jsonString)
                
                // Mark onboarding completed
                preferenceManager.setOnboardingCompleted(true)
                
                // Navigate to result activity
                val intent = Intent(this@RecommendationPlantLoadingActivity, 
                    RecommendationPlantResultActivity::class.java).apply {
                    putExtra("recommendation_data", jsonString)
                }
                startActivity(intent)
                finish()
            }
        } catch (e: Exception) {
            // Show error & finish
        }
    }
}
```

**Features**:

- ✅ Lifecycle-aware coroutine (lifecycleScope)
- ✅ Error handling dengan try-catch
- ✅ Toast message untuk error
- ✅ Automatic finish saat sukses/error

---

### 3. RecommendationPlantResultActivity

**Location**:
`app/src/main/java/id/hanifalfaqih/potaful/ui/recommendation/RecommendationPlantResultActivity.kt`

**Purpose**:

- Parse data recommendation dari Intent
- Tampilkan hasil dalam RecyclerView
- Card view dengan expand/collapse mechanism

**Layout**: `activity_recommendation_plant_result.xml`

- ScrollView sebagai root
- Title & subtitle
- RecyclerView untuk list recommendations
- Button "Go to Dashboard"

**Key Functions**:

```kotlin
private fun parseAndDisplayRecommendations() {
    val jsonString = intent.getStringExtra("recommendation_data")
    
    val gson = Gson()
    val type = object : TypeToken<List<PlantRecommendationResponse>>() {}.type
    val response: List<PlantRecommendationResponse> = gson.fromJson(jsonString, type)
    
    // Extract plant names and reasons
    val plantItems = mutableListOf<PlantRecommendationItem>()
    
    if (response.isNotEmpty() && response[0].output.isNotEmpty()) {
        val output = response[0].output[0]
        val textData = output.content[0].text
        val recommendations = textData.recommendation
        val reasons = textData.reason
        
        for (i in recommendations.indices) {
            plantItems.add(
                PlantRecommendationItem(
                    name = recommendations[i],
                    reason = reasons[i]
                )
            )
        }
    }
    
    adapter = PlantRecommendationAdapter(plantItems)
    binding.rvPlantRecommendations.adapter = adapter
}
```

---

### 4. PlantRecommendationAdapter

**Location**:
`app/src/main/java/id/hanifalfaqih/potaful/ui/recommendation/PlantRecommendationAdapter.kt`

**Purpose**: Adapter untuk RecyclerView dengan expand/collapse functionality

**Data Model**:

```kotlin
data class PlantRecommendationItem(
    val name: String,
    val reason: String
)
```

**Features**:

- ✅ ViewHolder dengan state `isExpanded`
- ✅ Click listener pada header untuk expand/collapse
- ✅ Icon rotation (ic_show_down ↔ ic_show_up)
- ✅ Smooth visibility toggle untuk expandable content

**Key Logic**:

```kotlin
holder.llHeader.setOnClickListener {
    holder.isExpanded = !holder.isExpanded
    
    holder.llExpandableContent.visibility = 
        if (holder.isExpanded) View.VISIBLE else View.GONE
        
    holder.ivExpandIcon.setImageResource(
        if (holder.isExpanded) R.drawable.ic_show_up else R.drawable.ic_show_down
    )
}
```

---

### 5. Item Layout - item_plant_recommendation.xml

**Location**: `app/src/main/res/layout/item_plant_recommendation.xml`

**Structure**:

```xml
MaterialCardView
  └── LinearLayout (vertical)
      ├── LinearLayout (header, clickable)
      │   ├── TextView (plant name)
      │   └── ImageView (expand icon)
      │
      └── LinearLayout (expandable content, visibility = gone)
          ├── Divider (View)
          ├── TextView ("Why this plant?")
          └── TextView (reason)
```

**Styling**:

- Background: `forest_brown_medium`
- Text color: `white`
- Highlight color: `forest_green_light`
- Corner radius: 12dp
- Elevation: 4dp
- Padding: 16dp

---

## User Experience Flow

### Step by Step:

1. **Onboarding Input (Fragment 1-4)**
    - User mengisi lokasi
    - User pilih skill level
    - User pilih preference
    - User pilih home frequency
    - User klik "Start"

2. **Loading Screen**
    - Tampilkan loading animation
    - Call N8N API di background
    - Auto navigate setelah selesai

3. **Result Screen**
    - Tampilkan list rekomendasi tanaman
    - Setiap card collapsible (default collapsed)
    - Tap card untuk expand dan lihat reason
    - Tap lagi untuk collapse
    - Button "Go to Dashboard" di bawah

4. **Dashboard**
    - User masuk ke main dashboard
    - Data recommendation tersimpan di SharedPreferences

---

## Data Flow

```
User Input (OnboardingUserActivity)
    ↓
Intent Extras (String values)
    ↓
N8N API Call (RecommendationPlantLoadingActivity)
    ↓
JSON Response
    ↓
SharedPreferences (for persistence)
    ↓
Intent Extra (recommendation_data as JSON string)
    ↓
Parse & Display (RecommendationPlantResultActivity)
    ↓
RecyclerView with PlantRecommendationAdapter
    ↓
Expandable Card Items
```

---

## API Integration

**Endpoint**: `POST https://potaful2.app.n8n.cloud/webhook/plant-recommendation`

**Query Parameters**:

- location: String (e.g., "Bekasi")
- skill_level: String ("Beginner" | "Intermediate" | "Professional")
- home_frequency: String ("Seldom" | "Often")
- preference: String ("Vegetables" | "Fruits")

**Response Structure**:

```json
[{
  "output": [{
    "content": [{
      "text": {
        "recommendation": ["Plant1", "Plant2", ...],
        "reason": ["Reason1", "Reason2", ...]
      }
    }]
  }]
}]
```

---

## Files Summary

### New Files:

1. ✅ `PlantRecommendationAdapter.kt` - RecyclerView adapter dengan expand/collapse
2. ✅ `item_plant_recommendation.xml` - Layout untuk card item

### Modified Files:

1. ✅ `OnboardingUserActivity.kt` - Navigate ke loading activity
2. ✅ `RecommendationPlantLoadingActivity.kt` - API call & loading
3. ✅ `RecommendationPlantResultActivity.kt` - Parse & display results
4. ✅ `activity_recommendation_plant_result.xml` - Layout dengan RecyclerView

---

## Key Features

✅ **Separation of Concerns**

- Onboarding = Input gathering
- Loading Activity = API call & data processing
- Result Activity = Display & interaction

✅ **User Feedback**

- Loading animation during API call
- Error handling dengan Toast
- Success indication dengan navigation

✅ **Interactive UI**

- Expandable cards untuk see more/less
- Visual feedback (icon rotation)
- Smooth transitions

✅ **Data Persistence**

- Save to SharedPreferences
- Can retrieve later in Dashboard
- Mark onboarding completed

---

## Testing Checklist

- [ ] Input semua data di onboarding
- [ ] Klik "Start" dan verify navigate ke loading screen
- [ ] Verify loading animation muncul
- [ ] Verify API call sukses
- [ ] Verify navigate ke result screen
- [ ] Verify list tanaman tampil dengan benar
- [ ] Tap setiap card dan verify expand/collapse works
- [ ] Verify reason text muncul saat expanded
- [ ] Klik "Go to Dashboard" dan verify navigate ke dashboard
- [ ] Verify data tersimpan di SharedPreferences

---

## Future Enhancements

1. **Animation**: Add smooth expand/collapse animation
2. **Images**: Add plant images to cards
3. **Favorites**: Allow user to favorite certain plants
4. **Share**: Add share functionality for recommendations
5. **Retry**: Add retry button on loading screen if API fails
6. **Skeleton Loading**: Replace loading screen with skeleton UI

