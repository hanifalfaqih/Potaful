# Deep Link Setup Guide for Potaful App

## Overview

This app uses deep links to handle Google OAuth callback from the backend.

## Two Options for Deep Links

### Option 1: Custom Scheme (✅ RECOMMENDED FOR DEVELOPMENT)

**Redirect URL:** `potaful://auth/google/callback?token=<JWT_TOKEN>`

**Advantages:**

- No server configuration needed
- Works immediately
- No SSL certificate issues
- Easy to test

**Backend Implementation:**
After successful Google OAuth, redirect to:

```
potaful://auth/google/callback?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Option 2: HTTPS App Links (For Production)

**Redirect URL:** `https://api.lutfialvarop.cloud/api/auth/google/callback?token=<JWT_TOKEN>`

**Requirements:**

1. Create file at: `https://api.lutfialvarop.cloud/.well-known/assetlinks.json`
2. Content (replace SHA256 with actual value):

```json
[
  {
    "relation": [
      "delegate_permission/common.handle_all_urls"
    ],
    "target": {
      "namespace": "android_app",
      "package_name": "id.hanifalfaqih.potaful",
      "sha256_cert_fingerprints": [
        "REPLACE_WITH_ACTUAL_SHA256_WITHOUT_COLONS"
      ]
    }
  }
]
```

3. Ensure:
    - File returns `Content-Type: application/json`
    - Accessible via HTTPS without redirects
    - No authentication required

## Getting SHA256 Fingerprint

### For Debug Builds:

```bash
cd /home/hanifalfaqih/AndroidStudioProjects/Potaful
./gradlew signingReport
```

Look for SHA-256 under the "debug" variant.

### For Release Builds:

```bash
keytool -list -v -keystore /path/to/release.keystore -alias <your_alias>
```

**Important:** Remove colons from the fingerprint before adding to assetlinks.json

- Example: `AA:BB:CC:DD` → `AABBCCDD`

## Current Configuration

### App Package Name

```
id.hanifalfaqih.potaful
```

### Supported Deep Link Formats

1. **Custom Scheme (Currently Active):**
   ```
   potaful://auth/google/callback?token=<JWT_TOKEN>
   ```

2. **HTTPS (Requires assetlinks.json):**
   ```
   https://api.lutfialvarop.cloud/api/auth/google/callback?token=<JWT_TOKEN>
   ```

## Testing Deep Links

### Test with ADB:

```bash
# Test custom scheme
adb shell am start -W -a android.intent.action.VIEW -d "potaful://auth/google/callback?token=test_token_123"

# Test HTTPS (after setting up assetlinks.json)
adb shell am start -W -a android.intent.action.VIEW -d "https://api.lutfialvarop.cloud/api/auth/google/callback?token=test_token_123"
```

### Verify App Links Status:

```bash
adb shell pm get-app-links id.hanifalfaqih.potaful
```

## Recommendation

**For immediate development:** Use custom scheme `potaful://`
**For production:** Set up HTTPS App Links with proper assetlinks.json

## Backend Redirect Example

After successful Google OAuth:

```javascript
// Node.js/Express example
app.get('/api/auth/google/callback', async (req, res) => {
  // ... handle Google OAuth ...
  const token = generateJWT(user);
  
  // For development (custom scheme)
  res.redirect(`potaful://auth/google/callback?token=${token}`);
  
  // For production (HTTPS App Links - after assetlinks.json is set up)
  // res.redirect(`https://api.lutfialvarop.cloud/api/auth/google/callback?token=${token}`);
});
```

## Troubleshooting

### "The app is stuck in browser"

- Make sure the backend redirects to the correct deep link URL
- Verify the app is installed on the device
- Test with ADB command to ensure the app can handle the deep link

### "App chooser dialog appears"

- This is normal for HTTPS links without proper assetlinks.json verification
- Use custom scheme for development to avoid this
- Set up assetlinks.json properly for production

### "Token not received in app"

- Check if the token is properly appended as query parameter
- Verify the deep link URL format matches exactly
- Check logs in GoogleAuthCallbackActivity

## Contact

If you need help setting up assetlinks.json or have questions about the deep link format, please
reach out to the mobile team.

