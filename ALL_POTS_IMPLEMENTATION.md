# All Pots Screen Implementation

## Overview

This implementation adds a new "All Pots" screen that displays all pots grouped by their hydration
status (Urgent, Warning, Safe). Users can access this screen by clicking "All Pots" on the Dashboard
screen.

## Features Implemented

### 1. **AllPotsActivity**

- New activity that displays all pots from the hydration endpoint
- Groups pots into three categories: Urgent, Warning, and Safe
- Each section is shown/hidden dynamically based on availability
- Pull-to-refresh functionality
- Loading overlay with progress indicator

### 2. **AllPotsViewModel**

- Manages state for hydration data and watering actions
- Fetches data from `/api/mypot/hydration` endpoint
- Handles watering pot requests via `/api/mypot/{pot_id}/watering`
- Includes proper state management with sealed classes

### 3. **AllPotsAdapter**

- Custom RecyclerView adapter for displaying pot items
- Shows pot name, type, status badge, and soil hydration percentage
- Color-coded status indicators:
    - **Urgent**: Red (status_bahaya colors)
    - **Warning**: Yellow/Orange (status_perlu_perhatian colors)
    - **Safe**: Green (status_baik colors)
- Water Now button with haptic feedback and scale animation

### 4. **Layouts**

#### activity_all_pots.xml

- Top bar with back button and title
- Three separate RecyclerViews for each status category
- Empty state message
- Pull-to-refresh support
- Loading overlay

#### item_pot_all.xml

- Card-based design matching app theme
- Status indicator bar on left side
- Status badge with rounded corners
- Pot name and type display
- Soil hydration percentage
- Water Now button

### 5. **Navigation**

- Added click listener to `tv_see_all_pots` in DashboardActivity
- Registered AllPotsActivity in AndroidManifest.xml
- Seamless navigation from Dashboard to All Pots screen

## API Integration

### Endpoint Used

```
GET /api/mypot/hydration
```

### Response Structure

```json
{
    "status": "SUCCESS",
    "message": "Data hidrasi pot berhasil diambil",
    "data": {
        "pots": [
            {
                "id": "t8NTt3FhUZ",
                "user_id": "...",
                "type_pot_id": "...",
                "created_at": "...",
                "updated_at": "...",
                "type_name": "Potafull Home 1.0",
                "max_water": 6,
                "condition": "URGENT",
                "soil_hydration": 0
            }
        ],
        "total": 1
    }
}
```

## Files Created

1. `/app/src/main/java/id/hanifalfaqih/potaful/ui/allpots/AllPotsActivity.kt`
2. `/app/src/main/java/id/hanifalfaqih/potaful/ui/allpots/AllPotsViewModel.kt`
3. `/app/src/main/java/id/hanifalfaqih/potaful/ui/allpots/AllPotsViewModelFactory.kt`
4. `/app/src/main/java/id/hanifalfaqih/potaful/ui/allpots/adapter/AllPotsAdapter.kt`
5. `/app/src/main/res/layout/activity_all_pots.xml`
6. `/app/src/main/res/layout/item_pot_all.xml`
7. `/app/src/main/res/drawable/bg_status_badge.xml`

## Files Modified

1. `/app/src/main/AndroidManifest.xml` - Added AllPotsActivity
2. `/app/src/main/java/id/hanifalfaqih/potaful/ui/dashboard/DashboardActivity.kt` - Added click
   listener
3. `/app/src/main/res/values/strings.xml` - Added new string resources

## Design Highlights

- Consistent with existing app theme (forest/earth tones)
- Status-based color coding for quick visual identification
- Smooth animations and haptic feedback on interactions
- Responsive layout with proper empty states
- Pull-to-refresh for easy data updates
- Loading states with overlay

## User Experience Flow

1. User clicks "All Pots" on Dashboard
2. App navigates to AllPotsActivity
3. Loading overlay appears while fetching data
4. Pots are grouped by status and displayed
5. User can scroll through categorized pots
6. User can click "Water Now" to trigger watering
7. List refreshes after successful watering
8. User can pull-to-refresh for latest data
9. User can press back button to return to Dashboard

## Status Grouping Logic

- **Urgent**: condition == "URGENT" (Red theme)
- **Warning**: condition == "WARNING" (Yellow/Orange theme)
- **Safe**: condition == "SAFE" (Green theme)

Each section is only visible when it contains pots. If no pots exist at all, an empty state message
is shown.

