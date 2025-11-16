# Pot Detail Implementation (Expand/Collapse in Dashboard)

## Overview

Implementasi untuk menampilkan detail data pot dengan sensor data menggunakan API endpoint
`/api/mypot/{pot_id}/data`.

**Note**: Detail data ditampilkan langsung di Dashboard dengan mekanisme expand/collapse pada item
list, bukan dengan activity terpisah.

## Response API

```json
{
  "status": "SUCCESS",
  "message": "Data pot berhasil diambil",
  "data": {
    "pot_id": "abcdefghij",
    "type_name": "Potafull Home 1.0",
    "max_water": 6,
    "sensor_data": {
      "n": 0,
      "p": 0,
      "k": 0,
      "temperature": 0,
      "moisture": 0,
      "ph": 0,
      "salinity": 0,
      "conductivity": 0,
      "water_level": 0,
      "soil_health": 0
    },
    "timestamp": "2025-11-16T05:39:59.378Z"
  }
}
```

## Files Created/Modified

### Created/Modified Files:

1. **PotResponse.kt** - Response models untuk pot detail
    - `PotDetailResponse`
    - `PotDetailData`
    - `SensorData` (10 sensor fields dengan tipe Double)

2. **ApiService.kt** - Tambah endpoint `getPotDetail()`
   ```kotlin
   suspend fun getPotDetail(token: String, potId: String): PotDetailResponse
   ```

3. **ApiRepository.kt** - Tambah method `getPotDetail()`
   ```kotlin
   suspend fun getPotDetail(token: String, potId: String): Result<PotDetailResponse>
   ```

4. **DashboardViewModel.kt** - Tambah state dan method untuk pot detail
    - `PotDetailState` sealed class (Loading, Success, Error)
    - `loadPotDetail()` method untuk fetch detail data
    - LiveData `potDetailState` untuk observe state

5. **PotDashboardAdapter.kt** - Update adapter untuk expand/collapse
    - Tambah `expandedItems` map untuk track item yang di-expand
    - Method `updateExpandedItem()` untuk update item dengan detail data
    - Method `isExpanded()` untuk cek apakah item sudah di-expand
    - Bind sensor data ke GridLayout saat expanded
    - Toggle visibility GridLayout (VISIBLE/GONE) based on expand state
    - Update dropdown icon (ic_show_up/ic_show_down)

6. **DashboardActivity.kt** - Handle expand/collapse behavior
    - Observe `potDetailState` dari ViewModel
    - Update item click handler untuk toggle expand/collapse
    - Fetch detail data saat expand
    - Handle error saat fetch detail
    - PotSummaryAdapter juga bisa scroll dan expand item di main list

7. **item_pot_dashboard.xml** - Layout sudah ada dengan:
    - GridLayout untuk semua sensor data (2 kolom, 8 cards)
    - Dropdown icon untuk toggle expand
    - Semua TextView dengan id untuk bind data

## Sensor Data Yang Ditampilkan

1. **Nitrogen (N)** - dalam ppm
2. **Fosfor (P)** - dalam ppm
3. **Kalium (K)** - dalam ppm
4. **Suhu** - dalam °C
5. **Kelembaban** - dalam %
6. **pH Tanah** - skala pH
7. **Salinitas** - dalam ppm
8. **Konduktivitas** - dalam μS/cm
9. **Level Air** - dalam %
10. **Kesehatan Tanah** - dalam %

## UI Flow (Expand/Collapse)

```
DashboardActivity
  ├─ PotSummaryAdapter (Horizontal scroll)
  │   └─ Click item → Scroll to main list & expand
  │
  └─ PotDashboardAdapter (Vertical list)
      ├─ Click collapsed item → Fetch detail & expand GridLayout
      ├─ Click expanded item → Collapse GridLayout
      └─ GridLayout shows: 
          • Kesehatan Tanah (soil_health)
          • Water Level (water_level)
          • Salinitas (salinity)
          • Konduktivitas/EC (conductivity)
          • Nitrogen/N (nitrogen)
          • Fosfor/P (phosphorus)
          • Kalium/K (kalium)
          • pH Tanah (ph)
          • Kelembapan Tanah (moisture)
          • Suhu Tanaman (temperature)
```

## Usage

1. **Expand Detail**: Klik pada pot item di list untuk fetch dan menampilkan detail sensor
2. **Collapse Detail**: Klik lagi pada pot item yang sudah di-expand untuk menyembunyikan detail
3. **Quick Access**: Klik pot di horizontal scroll (PotSummaryAdapter) untuk auto-scroll dan expand
   di main list

## Resources Added

### Drawable Resources:

- `bg_loading_dialog.xml` - Background untuk loading dialog (rounded white rectangle)
- `ic_back.xml` - Icon untuk tombol back

### Color Resources:

- `soft_green_light` (#E8F5E9) - Background color untuk sensor data cards

## Troubleshooting

### Resource Not Found Error

Jika Anda mengalami error "resource not found" setelah semua file sudah dibuat:

1. **Invalidate Caches & Restart**
    - File → Invalidate Caches... → Invalidate and Restart

2. **Clean & Rebuild Project**
   ```bash
   ./gradlew clean
   ./gradlew build
   ```

3. **Sync Gradle Files**
    - File → Sync Project with Gradle Files

4. **Restart Android Studio**

## Future Improvements

- Implement actual watering functionality
- Add real-time sensor data updates
- Add charts/graphs for sensor history
- Add recommendations based on sensor values

