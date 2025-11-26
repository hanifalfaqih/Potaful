# All Pots Feature - Testing Checklist

## Pre-requisites

- [ ] User is logged in with valid token
- [ ] Backend API is accessible at `https://api.lutfialvarop.cloud`
- [ ] User has at least one pot added to their account

## Test Cases

### 1. Navigation

- [ ] Open Dashboard
- [ ] Tap on "All Pots" text on the Dashboard
- [ ] Verify AllPotsActivity opens
- [ ] Verify top bar shows "All Pots" title
- [ ] Tap back button
- [ ] Verify returns to Dashboard

### 2. Data Loading

- [ ] Open All Pots screen
- [ ] Verify loading overlay appears briefly
- [ ] Verify pots are loaded and displayed
- [ ] Verify pots are grouped by status (Urgent, Warning, Safe)
- [ ] Verify each section title is only shown if pots exist in that category

### 3. Pot Display

- [ ] Verify each pot card shows:
    - [ ] Pot ID/name
    - [ ] Pot type (e.g., "Potafull Home 1.0")
    - [ ] Status badge (URGENT/WARNING/SAFE)
    - [ ] Soil hydration percentage
    - [ ] "Water Now" button
- [ ] Verify status indicator bar color matches status:
    - [ ] Red for Urgent
    - [ ] Yellow/Orange for Warning
    - [ ] Green for Safe

### 4. Status Grouping

- [ ] Verify Urgent pots appear first (if any)
- [ ] Verify Warning pots appear second (if any)
- [ ] Verify Safe pots appear last (if any)
- [ ] If no pots exist, verify "No pots available" message is shown

### 5. Watering Functionality

- [ ] Tap "Water Now" button on any pot
- [ ] Verify haptic feedback
- [ ] Verify button animation (scale down/up)
- [ ] Verify toast message "Sending watering command..."
- [ ] Verify success toast appears
- [ ] Verify list refreshes after successful watering

### 6. Pull to Refresh

- [ ] Swipe down from top of list
- [ ] Verify refresh indicator appears
- [ ] Verify data reloads
- [ ] Verify refresh indicator disappears

### 7. Empty State

- [ ] If account has no pots, verify "No pots available" message
- [ ] Verify no section titles are shown
- [ ] Verify no error occurs

### 8. Error Handling

- [ ] Turn off internet connection
- [ ] Open All Pots screen
- [ ] Verify error toast appears
- [ ] Turn on internet connection
- [ ] Pull to refresh
- [ ] Verify data loads successfully

### 9. Visual/UI Testing

- [ ] Verify colors match app theme
- [ ] Verify fonts are consistent
- [ ] Verify spacing and padding are appropriate
- [ ] Verify cards have proper elevation/shadow
- [ ] Verify status badges have rounded corners
- [ ] Test on different screen sizes
- [ ] Verify scroll works smoothly

### 10. Performance

- [ ] Test with 1 pot
- [ ] Test with 5 pots
- [ ] Test with 10+ pots
- [ ] Verify scrolling is smooth
- [ ] Verify no lag or ANR

## Expected API Responses

### Success Response

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

### Watering Success Response

```json
{
    "status": "SUCCESS",
    "message": "Perintah penyiraman berhasil dikirim",
    "data": {
        "pot_id": "t8NTt3FhUZ",
        "action": "watering",
        "status": "sent"
    }
}
```

## Known Issues/Limitations

- None at this time

## Notes

- The screen uses the same hydration endpoint as the Dashboard summary list
- Watering triggers automatic refresh of the list
- Status colors are configurable in colors.xml
- All strings are in strings.xml for localization support

