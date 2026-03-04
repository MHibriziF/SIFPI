# UM-4 & UM-5 Registration Request/Response Examples

## UM-4: Register Project Owner

### Request Body (POST /api/auth/register/owner)

```json
{
  "nama": "Budi Santoso",
  "email": "budi.santoso@example.com",
  "jabatan": "Direktur Proyek",
  "organisasi": "PT Infrastruktur Indonesia",
  "phone": "081234567890",
  "password": "SecurePass123!",
  "confirmPassword": "SecurePass123!"
}
```

### Response Body (201 Created)

```json
{
  "status": 201,
  "message": "Berhasil mendaftar sebagai Project Owner.",
  "timestamp": "2026-03-04T10:30:45.123456Z",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "nama": "Budi Santoso",
    "email": "budi.santoso@example.com",
    "jabatan": "Direktur Proyek",
    "organisasi": "PT Infrastruktur Indonesia",
    "phone": "081234567890",
    "isVerified": false,
    "isActive": true,
    "roleName": "PROJECT_OWNER",
    "createdAt": "2026-03-04T10:30:45.123456Z"
  }
}
```

---

## UM-5: Register Investor

### Request Body (POST /api/auth/register/investor)

```json
{
  "nama": "Rina Wijaya",
  "email": "rina.wijaya@example.com",
  "jabatan": "Investment Manager",
  "organisasi": "PT Dana Investasi Nasional",
  "phone": "082345678901",
  "password": "InvestPass456!",
  "confirmPassword": "InvestPass456!",
  "budgetInvestasi": "1-5 Miliar",
  "sectorInterest": [
    "Transportasi",
    "Air Bersih",
    "Energi Terbarukan"
  ],
  "optInEmail": true,
  "agreePrivacy": true
}
```

### Response Body (201 Created)

```json
{
  "status": 201,
  "message": "Berhasil mendaftar sebagai Investor.",
  "timestamp": "2026-03-04T10:35:20.654321Z",
  "data": {
    "id": "660e8400-e29b-41d4-a716-446655440001",
    "nama": "Rina Wijaya",
    "email": "rina.wijaya@example.com",
    "jabatan": "Investment Manager",
    "organisasi": "PT Dana Investasi Nasional",
    "phone": "082345678901",
    "isVerified": false,
    "isActive": true,
    "roleName": "INVESTOR",
    "budgetInvestasi": "1-5 Miliar",
    "sectorInterest": [
      "Transportasi",
      "Air Bersih",
      "Energi Terbarukan"
    ],
    "optInEmail": true,
    "agreePrivacy": true,
    "createdAt": "2026-03-04T10:35:20.654321Z"
  }
}
```

---

## Error Responses

### 409 Conflict - Email Already Registered

**Request:** Same email as existing user

```json
{
  "status": 409,
  "message": "Email sudah terdaftar",
  "timestamp": "2026-03-04T10:40:15.789012Z",
  "data": null
}
```

### 400 Bad Request - Password Mismatch

**Request:** `password` ≠ `confirmPassword`

```json
{
  "status": 400,
  "message": "Password dan konfirmasi password tidak cocok",
  "timestamp": "2026-03-04T10:41:22.456789Z",
  "data": null
}
```

### 400 Bad Request - Insufficient Sectors (Investor Only)

**Request:** `sectorInterest.length < 3`

```json
{
  "status": 400,
  "message": "Minimal 3 sektor prioritas harus dipilih",
  "timestamp": "2026-03-04T10:42:18.234567Z",
  "data": null
}
```

### 400 Bad Request - Privacy Not Agreed (Investor Only)

**Request:** `agreePrivacy: false`

```json
{
  "status": 400,
  "message": "Anda harus menyetujui syarat dan kebijakan privasi",
  "timestamp": "2026-03-04T10:43:10.123456Z",
  "data": null
}
```

### 400 Bad Request - Validation Error

**Request:** Missing required field (e.g., `email` is blank)

```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2026-03-04T10:44:05.987654Z",
  "data": null
}
```

---

## Email Notifications

### Owner Registration Email

**Subject:** `Selamat Datang - Registrasi Project Owner SIFPI`

**Template variables:**
- `name`: User's full name
- `email`: User's email address
- `loginUrl`: Login page URL (from `app.email.login-url` config)

**Content includes:**
- Welcome message
- Account information (email, role)
- Call-to-action button to login
- Information about features available to project owners

### Investor Registration Email

**Subject:** `Selamat Datang - Registrasi Investor SIFPI`

**Template variables:**
- `name`: User's full name
- `email`: User's email address
- `loginUrl`: Login page URL
- `sectors`: Comma-separated list of sector interests (from `sectorInterest`)
- `budget`: Budget range (from `budgetInvestasi`)

**Content includes:**
- Welcome message
- Account information (email, role)
- Investor's registered preferences (sectors, budget)
- Call-to-action button to login
- Information about features available to investors

---

## How to Test Email Sending

1. **Check logs** - Email sending uses `SLF4J @Slf4j`, so check application logs for:
   - `INFO` level: "Registration email sent successfully to: {email}"
   - `WARN` level: If email fails, logs "Failed to send registration email to {email}: {error}"

2. **Verify SendGrid** - If using real SendGrid:
   - Check SendGrid dashboard at https://app.sendgrid.com/email_activity
   - Filter by recipient email to see if email was delivered
   - Check email inbox (it may be in spam/promotions folder)

3. **Local Development** - If `SENDGRID_API_KEY` is not set or empty:
   - EmailService will throw exception → registration rolls back
   - To bypass: Set `SENDGRID_API_KEY` in `.env` or docker-compose environment

4. **Email Template Variables** - To verify template rendering:
   - Check the HTML in the SendGrid activity/logs
   - Ensure {{placeholders}} are replaced with actual values
   - Look for proper formatting and styling

---

## Database Verification

After successful registration, verify in database:

### Project Owner Check
```sql
SELECT u.id, u.email, u.name, u.organization, u.jabatan, u.verified, u.active, u.created_at
FROM users u
JOIN roles r ON u.role_id = r.id
WHERE r.name = 'PROJECT_OWNER' AND u.email = 'budi.santoso@example.com';
```

### Investor Check
```sql
SELECT u.id, u.email, u.name, u.organization, u.jabatan, u.verified, u.active,
       ip.budget_range, ip.sector_interest, ip.opt_in_email, ip.agree_privacy, 
       u.created_at
FROM users u
JOIN roles r ON u.role_id = r.id
LEFT JOIN investor_profiles ip ON u.investor_profile_id = ip.id
WHERE r.name = 'INVESTOR' AND u.email = 'rina.wijaya@example.com';
```

Expected results:
- `verified`: false (email verification required separately)
- `active`: true (account is active)
- `created_at`: timestamp of registration
- For investors: `investor_profiles` record with sector_interest as comma-separated values

---

## Implementation Details

### Service Flow

1. **OwnerRegistrationServiceImpl.registerOwner()**
   - Validate email uniqueness → throw `ConflictException` (409)
   - Validate password match → throw `IllegalArgumentException` (400)
   - Get PROJECT_OWNER role from database
   - Map request to User entity (name, email, organization, jabatan, phone)
   - Encode password with `PasswordEncoder`
   - Save User (JPA @PrePersist sets createdAt)
   - Load email template `owner-registration.html`
   - Send email via `EmailService.sendEmail()`
   - Map User to OwnerDTO
   - Return with HTTP 201

2. **InvestorRegistrationServiceImpl.registerInvestor()**
   - Validate email uniqueness → throw `ConflictException` (409)
   - Validate password match → throw `IllegalArgumentException` (400)
   - Validate sectorInterest.size() ≥ 3 → throw `IllegalArgumentException` (400)
   - Validate agreePrivacy == true → throw `IllegalArgumentException` (400)
   - Get INVESTOR role from database
   - Map request to User entity (name, email, organization, jabatan, phone)
   - Encode password
   - Save User
   - Map request to InvestorProfile entity (budgetRange, sectorInterest as CSV, optInEmail, agreePrivacy)
   - Save InvestorProfile
   - Set bidirectional relationship (User ↔ InvestorProfile)
   - Refresh User from DB to load InvestorProfile
   - Load email template `investor-registration.html` with sectors and budget
   - Send email via `EmailService.sendEmail()`
   - Map User to InvestorDTO (includes InvestorProfile fields)
   - Return with HTTP 201

### Email Sending Architecture

Per the refactored EmailService pattern:
- **EmailService interface**: `sendEmail(to, subject, htmlContent)` and `sendBulkEmail(toEmails, subject, htmlContent)`
- **EmailTemplateUtil**: Loads `.html` templates from `classpath:templates/email/` and replaces `{{key}}` placeholders
- **Templates**: HTML files in `src/main/resources/templates/email/`
- **Service responsibility**: Feature service loads template + builds HTML → calls EmailService
- **Email failure**: Non-blocking (caught in try-catch, logged but doesn't rollback registration)

### Field Mappings

**CreateOwnerRequest → User**
- `nama` → `name`
- `email` → `email`
- `organisasi` → `organization`
- `jabatan` → `jabatan`
- `phone` → `phone`
- (computed) `password` → `password` (encoded)
- (computed) `verified` → false
- (computed) `active` → true
- (computed) `role` → PROJECT_OWNER

**CreateInvestorRequest → User + InvestorProfile**
- User part: same as owner mapping
- InvestorProfile part:
  - `budgetInvestasi` → `budgetRange`
  - `sectorInterest` → `sectorInterest` (List converted to CSV)
  - `optInEmail` → `optInEmail`
  - `agreePrivacy` → `agreePrivacy`

**User → OwnerDTO**
- All User fields mapped to DTO with field name transformations:
  - `name` → `nama`
  - `organization` → `organisasi`
  - `jabatan` → `jabatan`
  - `verified` → `isVerified`
  - `active` → `isActive`

**User + InvestorProfile → InvestorDTO**
- All OwnerDTO mappings + investor-specific:
  - `investorProfile.budgetRange` → `budgetInvestasi`
  - `investorProfile.sectorInterest` (CSV) → `sectorInterest` (List)
  - `investorProfile.optInEmail` → `optInEmail`
  - `investorProfile.agreePrivacy` → `agreePrivacy`
