# API Specification: Registration Features PM & Investor (UM-4 & UM-5)

Spesifikasi lengkap dengan request/response examples untuk fitur registrasi Project Owner (UM-4) dan Investor (UM-5) di aplikasi IPFO.

---

## 1. REGISTER PROJECT OWNER

### Endpoint
```
POST /api/auth/register/owner
```

### Request Body

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

### Request Field Specification

| Field | Tipe Data | Required | Validasi | Deskripsi |
|-------|-----------|----------|----------|-----------|
| `nama` | String | ✅ Yes | Max 255 char, non-empty | Nama lengkap project owner |
| `email` | String | ✅ Yes | Valid email format, max 255 char, unique | Email resmi (tidak boleh duplikat) |
| `organisasi` | String | ✅ Yes | Max 255 char, non-empty | Nama organisasi/instansi |
| `jabatan` | String | ✅ Yes | Max 100 char, non-empty | Jabatan di organisasi |
| `phone` | String | ✅ Yes | Max 20 char, non-empty | Nomor telepon (format: 08xxxxxxxxxx atau +62xxxxxxxxxx) |
| `password` | String | ✅ Yes | Min 6 char | Password (akan di-hash dengan BCrypt) |
| `confirmPassword` | String | ✅ Yes | Harus match dengan password | Konfirmasi password (untuk validasi di frontend) |

### Validasi Rules

1. **Email:**
   - Format valid (RFC 5322)
   - Belum terdaftar di sistem (unique)
   - Case-insensitive untuk pengecekan duplikat

2. **Password:**
   - Minimum 6 karakter
   - Password dan confirmPassword harus match
   - Akan di-hash menggunakan BCryptPasswordEncoder sebelum disimpan

3. **Phone:**
   - Format: `08xxxxxxxxxx` atau `+62xxxxxxxxxx`
   - Max 20 karakter

4. **Organisasi & Jabatan:**
   - Non-empty strings
   - Max length sesuai specification

### Success Response (201 Created)

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

### Response Field Specification (Success)

| Field | Tipe Data | Deskripsi |
|-------|-----------|-----------|
| `id` | UUID | User ID yang di-generate |
| `nama` | String | Nama lengkap user |
| `email` | String | Email user |
| `organisasi` | String | Organisasi/instansi |
| `jabatan` | String | Jabatan user |
| `phone` | String | Nomor telepon |
| `roleName` | String | Role user: `PROJECT_OWNER` |
| `isActive` | Boolean | Status aktif user (default: `true`) |
| `isVerified` | Boolean | Status verifikasi email (default: `false` - pending email verification) |
| `createdAt` | ISO 8601 | Timestamp user created |

### Error Response (400 Bad Request - Password Mismatch)

```json
{
  "status": 400,
  "message": "Permintaan tidak valid: Password dan konfirmasi password tidak cocok",
  "timestamp": "2026-03-01T23:35:29.613+07:00",
  "data": null
}
```

### Error Response (409 Conflict - Duplicate Email)

```json
{
  "status": 409,
  "message": "Email sudah terdaftar",
  "timestamp": "2026-03-01T23:35:29.613+07:00",
  "data": null
}
```

### Error Response (400 Bad Request - Validation Error)

```json
{
  "status": 400,
  "message": "Permintaan tidak valid: Nama wajib diisi",
  "timestamp": "2026-03-01T23:35:29.613+07:00",
  "data": null
}
```

### Business Logic

1. Validasi semua field required
2. Cek email tidak duplikat → jika duplikat throw `ConflictException` (409)
3. Cek password dan confirmPassword match → jika tidak match throw `BadRequestException` (400)
4. Get role `PROJECT_OWNER` dari database
5. Hash password menggunakan `BCryptPasswordEncoder`
6. Create User entity dengan:
   - `role` = PROJECT_OWNER
   - `isActive` = true
   - `isVerified` = false (pending admin verification)
   - `password` = hashed password
   - Field lainnya sesuai request
7. Save user ke database
8. Return UserDTO dengan 201 Created

---

## 2. REGISTER INVESTOR

### Endpoint
```
POST /api/auth/register/investor
```

### Request Body

```json
{
  "nama": "Rina Wijaya",
  "email": "rina.wijaya@example.com",
  "jabatan": "Investment Manager",
  "organisasi": "PT Dana Investasi Nasional",
  "phone": "082345678901",
  "password": "InvestPass456!",
  "confirmPassword": "InvestPass456!",
  "budgetInvestasi": "1-5",
  "sectorInterest": [
    "TRANSPORTASI",
    "AIR_BERSIH",
    "ENERGI"
  ],
  "preferredInvestmentInstrument": "Equity",
  "engagementModel": "Direct Investment",
  "stagePreference": "Greenfield",
  "riskAppetite": "Moderate",
  "esgStandards": "IFC Performance Standards",
  "localPresence": "Jakarta Office",
  "aumSize": "USD 100M",
  "optInEmail": true,
  "agreePrivacy": true
}
```

### Request Field Specification

| Field | Tipe Data | Required | Validasi | Deskripsi |
|-------|-----------|----------|----------|-----------|
| `nama` | String | ✅ Yes | Max 255 char, non-empty | Nama lengkap investor |
| `email` | String | ✅ Yes | Valid email format, max 255 char, unique | Email resmi (tidak boleh duplikat) |
| `organisasi` | String | ✅ Yes | Max 255 char, non-empty | Nama organisasi/instansi investor |
| `jabatan` | String | ✅ Yes | Max 100 char, non-empty | Jabatan di organisasi |
| `phone` | String | ✅ Yes | Max 20 char, non-empty | Nomor telepon |
| `password` | String | ✅ Yes | Min 8 char | Password (akan di-hash) |
| `confirmPassword` | String | ✅ Yes | Harus match dengan password | Konfirmasi password |
| `budgetInvestasi` | String | ✅ Yes | Max 50 char, non-empty, dari enum BudgetRange | Rentang budget investasi (nilai: `<1`, `1-5`, `5-10`, `>10`) |
| `sectorInterest` | Array<String> | ✅ Yes | Min 3 items, nilai dari enum Sector | Array sektor yang dipilih (minimal 3 sektor) |
| `preferredInvestmentInstrument` | String | ❌ No | Max 255 char | Instrumen investasi yang disukai (e.g., Equity, Debt, Hybrid) |
| `engagementModel` | String | ❌ No | Max 255 char | Model engagement investasi (e.g., Direct Investment, Co-Investment) |
| `stagePreference` | String | ❌ No | Greenfield atau Brownfield | Preferensi stage proyek (pilihan: Greenfield, Brownfield) |
| `riskAppetite` | String | ❌ No | Max 255 char | Tingkat risk appetite (e.g., Conservative, Moderate, Aggressive) |
| `esgStandards` | String | ❌ No | Max 255 char | ESG standards yang diikuti (e.g., IFC Performance Standards) |
| `localPresence` | String | ❌ No | Max 255 char | Kehadiran lokal (e.g., Jakarta Office, Regional Presence) |
| `aumSize` | String | ❌ No | Max 255 char | Assets Under Management size (e.g., USD 100M, USD 1B+) |
| `optInEmail` | Boolean | ❌ No | - | Opt-in untuk update katalog proyek via email (default: false) |
| `agreePrivacy` | Boolean | ✅ Yes | Harus `true` | Persetujuan data benar & kebijakan privasi (REQUIRED TRUE) |

### Valid Sector Options (Sektor Interest)

Gunakan **value** (bukan label) dalam request:

| Value | Label |
|-------|-------|
| `AIR_BERSIH` | Air Bersih |
| `TRANSPORTASI` | Transportasi |
| `ENERGI` | Energi |
| `SAMPAH` | Sampah |
| `PERUMAHAN` | Perumahan |
| `MBGI` | MBGI |

**Tip:** Frontend dapat memanggil `GET /api/auth/register/registration-options` untuk mendapatkan daftar lengkap opsi dengan labels.

### Valid Stage Preference Options (Stage Preference)

Gunakan salah satu dari pilihan berikut:

| Value | Deskripsi |
|-------|-----------|
| `Greenfield` | Proyek baru (dari nol) |
| `Brownfield` | Proyek yang sudah ada atau pengembangan |

### Valid Budget Range Options (Budget Investasi)

Gunakan **value** dalam request:

| Value | Label |
|-------|-------|
| `<1` | < 1 Miliar |
| `1-5` | 1-5 Miliar |
| `5-10` | 5-10 Miliar |
| `>10` | > 10 Miliar |

### Validasi Rules

1. **Email:**
   - Format valid (RFC 5322)
   - Belum terdaftar (unique)
   - Case-insensitive untuk pengecekan duplikat

2. **Password:**
   - Minimum 6 karakter
   - Password dan confirmPassword harus match
   - Akan di-hash menggunakan BCryptPasswordEncoder

3. **Sektor Prioritas:**
   - Minimum 3 sektor harus dipilih
   - Maksimum 6 sektor
   - Hanya dari opsi yang valid
   - Tidak boleh ada duplikat dalam list

4. **Agree Privacy:**
   - HARUS `true` (mandatory checkbox)
   - Jika `false` → reject dengan 400 Bad Request

5. **Budget Investasi:**
   - Hanya dari opsi yang valid
   - Non-empty

6. **Phone & Organisasi:**
   - Sama seperti Project Owner

### Success Response (201 Created)

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
    "budgetInvestasi": "1-5",
    "sectorInterest": [
      "TRANSPORTASI",
      "AIR_BERSIH",
      "ENERGI"
    ],
    "preferredInvestmentInstrument": "Equity",
    "engagementModel": "Direct Investment",
    "stagePreference": "Greenfield",
    "riskAppetite": "Moderate",
    "esgStandards": "IFC Performance Standards",
    "localPresence": "Jakarta Office",
    "aumSize": "USD 100M",
    "optInEmail": true,
    "agreePrivacy": true,
    "createdAt": "2026-03-04T10:35:20.654321Z"
  }
}
```

### Response Field Specification (Success)

| Field | Tipe Data | Deskripsi |
|-------|-----------|-----------|
| `id` | UUID | User ID yang di-generate |
| `nama` | String | Nama lengkap user |
| `email` | String | Email user |
| `organisasi` | String | Organisasi/instansi |
| `jabatan` | String | Jabatan user |
| `phone` | String | Nomor telepon |
| `roleName` | String | Role user: `INVESTOR` |
| `isActive` | Boolean | Status aktif user (default: `true`) |
| `isVerified` | Boolean | Status verifikasi email (default: `false` - pending email verification) |
| `budgetInvestasi` | String | Rentang budget yang dipilih (value dari enum, e.g., `1-5`) |
| `sectorInterest` | Array<String> | Daftar sektor yang dipilih (values dari enum, e.g., `[TRANSPORTASI, AIR_BERSIH]`) |
| `preferredInvestmentInstrument` | String | Instrumen investasi yang disukai (optional, null jika tidak diisi) |
| `engagementModel` | String | Model engagement investasi (optional, null jika tidak diisi) |
| `stagePreference` | String | Preferensi stage proyek (optional, null jika tidak diisi) |
| `riskAppetite` | String | Tingkat risk appetite (optional, null jika tidak diisi) |
| `esgStandards` | String | ESG standards yang diikuti (optional, null jika tidak diisi) |
| `localPresence` | String | Kehadiran lokal (optional, null jika tidak diisi) |
| `aumSize` | String | Assets Under Management size (optional, null jika tidak diisi) |
| `optInEmail` | Boolean | Status opt-in email |
| `agreePrivacy` | Boolean | Status persetujuan kebijakan privasi |
| `createdAt` | ISO 8601 | Timestamp user created |

### Error Response (400 Bad Request - Sector < 3)

```json
{
  "status": 400,
  "message": "Minimal 3 sektor prioritas harus dipilih",
  "timestamp": "2026-03-04T10:42:18.234567Z",
  "data": null
}
```

### Error Response (400 Bad Request - Invalid Sector Value)

```json
{
  "status": 400,
  "message": "Sektor tidak valid: INVALID_SECTOR",
  "timestamp": "2026-03-04T10:42:18.234567Z",
  "data": null
}
```

### Error Response (400 Bad Request - Agreement Not Accepted)

```json
{
  "status": 400,
  "message": "Anda harus menyetujui syarat dan kebijakan privasi",
  "timestamp": "2026-03-04T10:43:10.123456Z",
  "data": null
}
```

### Error Response (400 Bad Request - Password Mismatch)

```json
{
  "status": 400,
  "message": "Password dan konfirmasi password tidak cocok",
  "timestamp": "2026-03-04T10:41:22.456789Z",
  "data": null
}
```

### Error Response (400 Bad Request - Invalid Budget)

```json
{
  "status": 400,
  "message": "Budget investasi tidak valid",
  "timestamp": "2026-03-04T10:42:18.234567Z",
  "data": null
}
```

### Error Response (409 Conflict - Duplicate Email)

```json
{
  "status": 409,
  "message": "Email sudah terdaftar",
  "timestamp": "2026-03-04T10:40:15.789012Z",
  "data": null
}
```

### Business Logic

1. Validasi semua field required (via `@Valid` annotation)
2. Cek `budgetInvestasi` adalah value yang valid dari enum `BudgetRange` → jika tidak throw `IllegalArgumentException` (400)
3. Cek email tidak duplikat → jika duplikat throw `ConflictException` (409)
4. Cek password dan confirmPassword match → jika tidak match throw `IllegalArgumentException` (400)
5. Cek `sectorInterest` minimal 3 items → jika < 3 throw `IllegalArgumentException` (400)
6. Validasi setiap nilai dalam `sectorInterest` adalah value yang valid dari enum `Sector` → jika ada yang invalid throw `IllegalArgumentException` (400)
7. Cek `agreePrivacy` = true → jika false throw `IllegalArgumentException` (400)
8. Get role `INVESTOR` dari database
9. Hash password menggunakan `BCryptPasswordEncoder`
10. Convert `sectorInterest` list to comma-separated string untuk storage di `investor_profiles.sector_interest`
11. Create dan save User entity dengan:
    - `role` = INVESTOR
    - `isActive` = true
    - `isVerified` = false (pending admin verification)
    - `password` = hashed password
    - Field lainnya sesuai request
12. Create dan save InvestorProfile entity dengan:
    - `budgetRange` = `budgetInvestasi` value
    - `sectorInterest` = comma-separated string
    - `optInEmail` = subscription preference
    - `agreePrivacy` = true
13. Set bidirectional relationship (User ↔ InvestorProfile)
14. Reload User dari database untuk memastikan InvestorProfile ter-load
15. Load email template `investor-registration.html` dan kirim email async (non-blocking)
16. Map User + InvestorProfile ke InvestorDTO
17. Return dengan HTTP 201 Created

---

## 3. EMAIL VERIFICATION FLOW

Setelah user berhasil register, mereka akan menerima email dengan link verifikasi yang harus diklik dalam **25 menit**. Jika tidak diklik dalam waktu tersebut, token akan expired dan user harus register ulang dari awal.

### Timeline & Alur

```
T+0 MENIT: User klik "Daftar" di registration form
    ↓
T+0 MENIT: Backend create User dengan:
    - emailVerified = false
    - emailVerificationToken = UUID token (25 min expiry)
    - emailVerificationTokenExpiry = now + 25 minutes
    ↓
T+0 MENIT: Email terkirim dengan:
    - Button: "Verifikasi Email"
    - Atau: Manual token code untuk copy-paste
    - Pesan: "Link ini berlaku 25 menit"
    ↓
T+1-25 MENIT: User klik link "Verifikasi Email" di email
    ↓
SKENARIO A: User BUKA LINK DALAM 25 MENIT ✅
    ├─ Browser redirect ke: /verify-email?token=xxxxx
    ├─ Frontend auto-call: GET /api/auth/register/verify-email?token=xxxxx
    ├─ Backend: Validate token, set emailVerified = true, save user
    ├─ Frontend: Redirect ke /login dengan MODAL notifikasi
    │   └─ Modal: "✅ Akun Anda Berhasil Terdaftar!"
    │       "Email sudah terverifikasi. Silakan login."
    │       [Tombol: OK/Lanjut]
    ├─ User klik OK → Modal close → Tetap di /login page
    └─ User bisa login sekarang ✅

SKENARIO B: Token EXPIRED (> 25 MENIT) ❌
    ├─ Link di email tidak bekerja lagi
    ├─ Token dianggap invalid
    ├─ Akun TIDAK tersimpan di database (data dianggap pending/invalid)
    ├─ User HARUS registrasi ulang dari awal
    │   └─ Bisa gunakan email sama atau email baru
    ├─ Dari sini scenario A berlaku: verify dalam 25 menit
    └─ TIDAK ADA TOMBOL RESEND di login atau email ❌
```

### Verification Methods (2 cara dari 1 email)

Dari email registration yang sama, user bisa verify dengan 2 cara:

#### Method 1: Click Link (Auto-Verify)
1. User terima email
2. Klik button "Verifikasi Email"
3. Browser auto-redirect ke `/verify-email?token=xxxxx`
4. Frontend auto-call verification endpoint
5. Token valid? → emailVerified = true → Redirect ke /login dengan modal

#### Method 2: Manual Token Input (Jika link tidak bekerja)
1. User terima email
2. Copy token code dari email
3. Buka halaman `/verify-email` manual (atau form di login page)
4. Paste token ke input field
5. Klik "Verifikasi"
6. Token valid? → emailVerified = true → Redirect ke /login dengan modal

**Catatan:** Kedua method menggunakan token yang sama dari email. Jika token expired (> 25 menit), user harus register ulang.

### Verification Endpoint

#### GET /api/auth/register/verify-email

Query parameter:
```
GET /api/auth/register/verify-email?token=<verification_token>
```

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "Email berhasil diverifikasi. Silakan login.",
  "timestamp": "2026-03-05T10:45:30.123456Z",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "budi.santoso@example.com",
    "emailVerified": true,
    "message": "Akun siap digunakan"
  }
}
```

**Error Response (400 Bad Request - Token Invalid):**
```json
{
  "status": 400,
  "message": "Token verifikasi tidak ditemukan atau sudah digunakan",
  "timestamp": "2026-03-05T10:45:30.123456Z",
  "data": null
}
```

**Error Response (400 Bad Request - Token Expired):**
```json
{
  "status": 400,
  "message": "Token verifikasi telah kedaluwarsa. Silakan registrasi ulang.",
  "timestamp": "2026-03-05T10:45:30.123456Z",
  "data": null
}
```

### Backend Implementation

**User Model Fields:**
```java
@Column(name = "email_verification_token", length = 100)
private String emailVerificationToken;  // UUID token

@Column(name = "email_verification_token_expiry")
private LocalDateTime emailVerificationTokenExpiry;  // Now + 25 minutes
```

**InvestorRegistrationServiceImpl & OwnerRegistrationServiceImpl:**
```java
// Token generation saat registration
String verificationToken = UUID.randomUUID().toString().replace("-", "");
user.setEmailVerificationToken(verificationToken);
user.setEmailVerificationTokenExpiry(LocalDateTime.now().plusMinutes(25));
userRepository.save(user);
```

**EmailVerificationServiceImpl:**
```java
public void verifyEmail(String token) {
    // 1. Find user by token
    Optional<User> user = userRepository.findByEmailVerificationToken(token);
    
    if (user.isEmpty()) {
        throw new BadRequestException("Token verifikasi tidak ditemukan atau sudah digunakan");
    }
    
    // 2. Check if token expired
    if (LocalDateTime.now().isAfter(user.get().getEmailVerificationTokenExpiry())) {
        throw new BadRequestException("Token verifikasi telah kedaluwarsa. Silakan registrasi ulang.");
    }
    
    // 3. Set emailVerified = true & clear token
    User foundUser = user.get();
    foundUser.setEmailVerified(true);
    foundUser.setEmailVerificationToken(null);
    foundUser.setEmailVerificationTokenExpiry(null);
    userRepository.save(foundUser);
}
```

**RegistrationController:**
```java
@GetMapping("/register/verify-email")
public ResponseEntity<BaseResponseDTO<VerifyEmailResponse>> verifyEmail(
        @RequestParam String token) {
    
    emailVerificationService.verifyEmail(token);
    
    return ResponseEntity.ok(
        BaseResponseDTO.success(200, "Email berhasil diverifikasi. Silakan login.", 
                new VerifyEmailResponse())
    );
}
```

### Email Content

**Email Subject:** `Verifikasi Email Anda - IPFO SIFPI`

**Email Body (HTML):**
```
Halo [USER_NAME],

Terima kasih telah mendaftar di IPFO SIFPI.

Untuk menyelesaikan pendaftaran, silakan verifikasi email Anda:

[BUTTON: Verifikasi Email] → link ke /verify-email?token=xxxxx

Atau, copy-paste kode berikut jika tombol tidak bekerja:
Kode Verifikasi: [TOKEN_CODE]

⏰ Link ini berlaku selama 25 MENIT dari waktu pendaftaran.
⚠️ Jika tidak diverifikasi dalam 25 menit, silakan daftar ulang.

---
Jika Anda tidak melakukan pendaftaran ini, abaikan email ini.
```

### Frontend Integration

#### /verify-email Page

```tsx
// pages/verify-email.tsx

export default function VerifyEmailPage() {
  const router = useRouter();
  const { token } = useSearchParams();
  const [status, setStatus] = useState<'loading' | 'success' | 'error'>('loading');
  const [message, setMessage] = useState('');
  
  useEffect(() => {
    if (!token) {
      setStatus('error');
      setMessage('Token tidak ditemukan');
      return;
    }
    
    // Auto-verify saat page load
    verifyEmail(token);
  }, [token]);
  
  const verifyEmail = async (token: string) => {
    try {
      const response = await fetch(
        `/api/auth/register/verify-email?token=${token}`
      );
      
      if (!response.ok) {
        const error = await response.json();
        setStatus('error');
        setMessage(error.message || 'Verifikasi gagal');
        return;
      }
      
      const data = await response.json();
      setStatus('success');
      setMessage('Email berhasil diverifikasi!');
      
      // Show modal notification
      showSuccessModal(() => {
        router.push('/login');
      });
      
    } catch (error) {
      setStatus('error');
      setMessage('Terjadi kesalahan. Silakan coba lagi.');
    }
  };
  
  return (
    <div className="verify-email-container">
      {status === 'loading' && (
        <div>
          <Spinner />
          <p>Memverifikasi email...</p>
        </div>
      )}
      
      {status === 'success' && (
        <div>
          <Icon name="check-circle" size="64" color="green" />
          <h2>Email Berhasil Diverifikasi!</h2>
          <p>Silakan tunggu, kami akan redirect Anda ke halaman login...</p>
        </div>
      )}
      
      {status === 'error' && (
        <div>
          <Icon name="x-circle" size="64" color="red" />
          <h2>Verifikasi Gagal</h2>
          <p>{message}</p>
          <p>Silakan <Link href="/register/investor">daftar ulang</Link></p>
        </div>
      )}
    </div>
  );
}
```

#### Success Modal (Saat redirect ke login)

```tsx
// Modal component yang muncul setelah verifikasi berhasil

function RegistrationSuccessModal({ onConfirm }: { onConfirm: () => void }) {
  return (
    <Modal isOpen={true} onClose={onConfirm}>
      <Modal.Header>
        <Icon name="check-circle" color="green" />
        <h2>✅ Akun Anda Berhasil Terdaftar!</h2>
      </Modal.Header>
      
      <Modal.Body>
        <p>Email Anda telah berhasil diverifikasi.</p>
        <p>Anda sekarang dapat login dan mengakses platform IPFO SIFPI.</p>
      </Modal.Body>
      
      <Modal.Footer>
        <Button onClick={onConfirm} variant="primary">
          Lanjut ke Login
        </Button>
      </Modal.Footer>
    </Modal>
  );
}
```

### Manual Token Input (Optional - untuk jika link tidak bekerja)

Bisa ditambahkan di `/verify-email` page atau di login page jika user belum verifikasi:

```tsx
// Optional: Manual token input form di /verify-email atau /login

function ManualTokenVerificationForm() {
  const [token, setToken] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    
    try {
      const response = await fetch(
        `/api/auth/register/verify-email?token=${token}`
      );
      
      if (!response.ok) {
        const errorData = await response.json();
        setError(errorData.message);
        return;
      }
      
      // Success - show modal & redirect
      showSuccessModal(() => window.location.href = '/login');
      
    } catch (err) {
      setError('Terjadi kesalahan. Silakan coba lagi.');
    } finally {
      setLoading(false);
    }
  };
  
  return (
    <form onSubmit={handleSubmit}>
      <div>
        <label htmlFor="token">Masukkan Kode Verifikasi:</label>
        <input
          id="token"
          type="text"
          value={token}
          onChange={(e) => setToken(e.target.value)}
          placeholder="Paste kode dari email"
          required
        />
      </div>
      {error && <ErrorMessage>{error}</ErrorMessage>}
      <Button type="submit" disabled={loading}>
        {loading ? 'Memverifikasi...' : 'Verifikasi'}
      </Button>
    </form>
  );
}
```

### Testing Checklist

- ✅ Register investor → Email sent with verification link
- ✅ Click link dalam 25 menit → Email verified, redirect ke login with modal
- ✅ Modal shows "✅ Akun Anda Berhasil Terdaftar!"
- ✅ Click OK di modal → Go to login page
- ✅ Try token after 25 minutes → Get expiry error
- ✅ Manual token input → Same as click link
- ✅ Non-existent token → Get invalid error
- ✅ Try already verified token → Get invalid error

---

## 4. ENDPOINT METADATA

### GET /api/auth/register/registration-options

Endpoint publik untuk mendapatkan daftar opsi yang valid untuk dropdown dan checkbox di UI frontend.

**Response (200 OK):**

```json
{
  "budgetOptions": [
    { "value": "<1", "label": "< 1 Miliar" },
    { "value": "1-5", "label": "1-5 Miliar" },
    { "value": "5-10", "label": "5-10 Miliar" },
    { "value": ">10", "label": "> 10 Miliar" }
  ],
  "sectorOptions": [
    { "value": "AIR_BERSIH", "label": "Air Bersih" },
    { "value": "TRANSPORTASI", "label": "Transportasi" },
    { "value": "ENERGI", "label": "Energi" },
    { "value": "SAMPAH", "label": "Sampah" },
    { "value": "PERUMAHAN", "label": "Perumahan" },
    { "value": "MBGI", "label": "MBGI" }
  ]
}
```

**Keterangan:**
- Endpoint ini **publik** (tidak perlu authentication)
- Frontend panggil endpoint ini saat load registration page
- Gunakan `value` dalam request body, tampilkan `label` di UI

---

## 5. HTTP Status Code Reference

| Code | Scenario |
|------|----------|
| `200 OK` | Metadata retrieved successfully (GET /registration-options) |
| `201 Created` | Registration berhasil |
| `400 Bad Request` | Validasi gagal (password mismatch, sector < 3, invalid sector/budget value, agreePrivacy false, dll) |
| `409 Conflict` | Email sudah terdaftar |
| `500 Internal Server Error` | Server error |

---

## 6. Error Message Convention

Error messages dari service layer:
- Field validation errors (dari `@Valid` annotation): Sesuai message di DTO
- Business logic errors (email duplikat): `"Email sudah terdaftar"`
- Invalid sector: `"Sektor tidak valid: <values>"`
- Invalid budget: `"Budget investasi tidak valid"`
- Password mismatch: `"Password dan konfirmasi password tidak cocok"`
- Minimum sector: `"Minimal 3 sektor prioritas harus dipilih"`
- Agreement: `"Anda harus menyetujui syarat dan kebijakan privasi"`

Error response wrapper dari GlobalExceptionHandler:
```json
{
  "status": <HTTP_STATUS>,
  "message": "<error message>",
  "timestamp": "ISO_8601_TIMESTAMP",
  "data": null
}
```

---

## 7. Security Considerations

1. **Password Hashing:**
   - Semua password harus di-hash dengan BCryptPasswordEncoder sebelum disimpan
   - Jangan pernah return password di response

2. **Email Verification:**
   - Email belum terverifikasi setelah registration (isVerified = false)
   - Admin harus approve account sebelum bisa digunakan

3. **Data Privacy:**
   - Implement proper logging (jangan log password atau sensitive data)
   - Validate semua input di server-side
   - Implement rate limiting untuk prevent brute force

4. **SQL Injection & CSRF:**
   - Gunakan parameterized queries (JPA/Hibernate)
   - CSRF protection enabled di SecurityConfig

---

## 8. Implementation Notes

### DTOs

**CreateOwnerRequest:**
```java
@Data
public class CreateOwnerRequest {
    @NotBlank private String nama;
    @NotBlank @Email private String email;
    @NotBlank private String organisasi;
    @NotBlank private String jabatan;
    @NotBlank private String phone;
    @NotBlank @Size(min=6) private String password;
    @NotBlank private String confirmPassword;
}
```

**CreateInvestorRequest:**
```java
@Data
public class CreateInvestorRequest {
    @NotBlank private String nama;
    @NotBlank @Email private String email;
    @NotBlank private String organisasi;
    @NotBlank private String jabatan;
    @NotBlank private String phone;
    @NotBlank @Size(min=8) private String password;
    @NotBlank private String confirmPassword;
    @NotBlank private String budgetInvestasi;
    @NotEmpty @Size(min=3) private List<String> sectorInterest;
    
    // Additional optional fields (Investor Catalogue Template)
    @Size(max=255) private String preferredInvestmentInstrument;
    @Size(max=255) private String engagementModel;
    @Size(max=255) private String stagePreference;
    @Size(max=255) private String riskAppetite;
    @Size(max=255) private String esgStandards;
    @Size(max=255) private String localPresence;
    @Size(max=255) private String aumSize;
    
    private Boolean optInEmail = false;
    @NotNull @AssertTrue private Boolean agreePrivacy;
}
```

**OwnerDTO (Response - Project Owner):**
```java
@Data
public class OwnerDTO {
    private UUID id;
    private String nama;
    private String email;
    private String organisasi;
    private String jabatan;
    private String phone;
    private String roleName;
    private Boolean isActive;
    private Boolean isVerified;
    private LocalDateTime createdAt;
}
```

**InvestorDTO (Response - Investor):**
```java
@Data
public class InvestorDTO {
    private UUID id;
    private String nama;
    private String email;
    private String organisasi;
    private String jabatan;
    private String phone;
    private String roleName;
    private Boolean isActive;
    private Boolean isVerified;
    private String budgetInvestasi;    // value dari enum (e.g., "1-5")
    private List<String> sectorInterest; // values dari enum (e.g., ["TRANSPORTASI", "AIR_BERSIH"])
    
    // Additional optional fields (may be null)
    private String preferredInvestmentInstrument;
    private String engagementModel;
    private String stagePreference;
    private String riskAppetite;
    private String esgStandards;
    private String localPresence;
    private String aumSize;
    
    private Boolean optInEmail;
    private Boolean agreePrivacy;
    
    private LocalDateTime createdAt;
}
```

### Services

**OwnerRegistrationService:**
- Validate CreateOwnerRequest
- Check email uniqueness
- Hash password
- Create & save User entity
- Return UserDTO

**InvestorRegistrationService:**
- Validate CreateInvestorRequest (including sector count & agreement)
- Check email uniqueness
- Hash password
- Create & save User entity with investor data
- Return UserDTO

### Controller

**RegistrationController:**
```java
@PostMapping("/owner")
public ResponseEntity<BaseResponseDTO<UserDTO>> registerOwner(
    @Valid @RequestBody CreateOwnerRequest request)

@PostMapping("/investor")
public ResponseEntity<BaseResponseDTO<UserDTO>> registerInvestor(
    @Valid @RequestBody CreateInvestorRequest request)
```

---

## 9. Testing Checklist

### Get Registration Options
- ✅ GET /api/auth/register/registration-options → 200 OK with budgetOptions & sectorOptions

### Project Owner Registration
- ✅ Valid request → 201 Created
- ✅ Duplicate email → 409 Conflict
- ✅ Password mismatch → 400 Bad Request
- ✅ Missing required field → 400 Bad Request
- ✅ Invalid email format → 400 Bad Request
- ✅ Password < 8 chars → 400 Bad Request (validation)

### Investor Registration
- ✅ Valid request → 201 Created
- ✅ Duplicate email → 409 Conflict
- ✅ Password mismatch → 400 Bad Request
- ✅ Sector < 3 → 400 Bad Request
- ✅ Invalid sector value (e.g., "INVALID_SECTOR") → 400 Bad Request
- ✅ Invalid budget value (e.g., "100 Miliar") → 400 Bad Request
- ✅ agreePrivacy = false → 400 Bad Request
- ✅ Missing required field → 400 Bad Request

### Email Verification
- ✅ Register → Email sent with verification link
- ✅ Click link dalam 25 menit → Auto-verify, redirect ke /login with modal
- ✅ Modal shows "✅ Akun Anda Berhasil Terdaftar!"
- ✅ Click OK di modal → Stay on /login page
- ✅ Try token after 25 minutes → Get expiry error
- ✅ Manual token input → Same result as clicking link
- ✅ Non-existent token → Get "token not found" error
- ✅ Try already verified token → Get "already used" error

---

## 10. UPDATE USER CONTACT VERIFICATION STATUS (ADMIN) - UM-10

Feature untuk admin memverifikasi akun Project Owner yang sudah terdaftar dan melakukan email verification. Hanya Project Owner dengan `emailVerified = true` dan `isVerified = false` yang akan ditampilkan di halaman verifikasi admin.

### Difference with Email Verification (UM-4/5)

| Aspek | Email Verification (UM-4/5) | Contact Verification (UM-10) |
|-------|---------------------------|------------------------------|
| **Siapa verify?** | User sendiri (click email link) | Admin |
| **Kapan?** | Saat registrasi | Setelah email verified |
| **Field** | `emailVerified` | `isVerified` |
| **Tujuan** | Validasi email valid | Validasi identitas PO valid |
| **Hasil** | User bisa login | User bisa submit proyek |

---

## 10. API Integration Quick Start

### Step 1: Get Registration Options
```bash
curl -s http://localhost:8080/api/auth/register/registration-options | jq
```

### Step 2: Register Project Owner
```bash
curl -X POST http://localhost:8080/api/auth/register/owner \
  -H "Content-Type: application/json" \
  -d '{
    "nama": "Budi Santoso",
    "email": "budi.test@example.com",
    "jabatan": "Direktur Proyek",
    "organisasi": "PT Infrastruktur Indonesia",
    "phone": "081234567890",
    "password": "SecurePass123!",
    "confirmPassword": "SecurePass123!"
  }' | jq
```

### Step 3: Register Investor
```bash
curl -X POST http://localhost:8080/api/auth/register/investor \
  -H "Content-Type: application/json" \
  -d '{
    "nama": "Rina Wijaya",
    "email": "rina.test@example.com",
    "jabatan": "Investment Manager",
    "organisasi": "PT Dana Investasi Nasional",
    "phone": "082345678901",
    "password": "InvestPass456!",
    "confirmPassword": "InvestPass456!",
    "budgetInvestasi": "1-5",
    "sectorInterest": ["TRANSPORTASI", "AIR_BERSIH", "ENERGI"],
    "preferredInvestmentInstrument": "Equity",
    "engagementModel": "Direct Investment",
    "stagePreference": "Greenfield",
    "riskAppetite": "Moderate",
    "esgStandards": "IFC Performance Standards",
    "localPresence": "Jakarta Office",
    "aumSize": "USD 100M",
    "optInEmail": true,
    "agreePrivacy": true
```

**Valid stagePreference values:**
- `Greenfield` - Proyek baru
- `Brownfield` - Proyek yang sudah ada/dikembangkan
  }' | jq
```

**Note:** Field tambahan (preferredInvestmentInstrument, engagementModel, dll) bersifat **optional** dan bisa dihilangkan dari request jika tidak diperlukan.

---

## 10. UPDATE USER CONTACT VERIFICATION STATUS (ADMIN) - UM-10

### Overview
Admin endpoint untuk memverifikasi akun Project Owner yang sudah terdaftar dan email-verified. Setelah admin verifikasi, PO baru bisa submit proyek ke platform.

**Permission Required:** `ADMIN` role only

**Endpoint:** `PATCH /api/admin/users/{email}/verify`

### Request

**Method:** `PATCH`

**URL:** `http://localhost:8080/api/admin/users/budi.santoso@example.com/verify`

**Headers:**
```
Authorization: Bearer <JWT_ADMIN_TOKEN>
Content-Type: application/json
```

**Note:** Email dikirim sebagai path parameter, bukan request body.

### Success Response (200 OK)

```json
{
  "status": 200,
  "message": "Project Owner berhasil diverifikasi.",
  "timestamp": "2026-03-07T10:35:00.000+0000",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "budi.santoso@example.com",
    "name": "Budi Santoso",
    "organization": "PT Infrastruktur Indonesia",
    "isVerified": true,
    "verifiedBy": "admin@ipfo.go.id",
    "verifiedAt": "2026-03-07T10:35:00.000+0000",
    "roleName": "PROJECT_OWNER"
  }
}
```

### Error Responses

#### 404 Not Found - User tidak ditemukan
```json
{
  "status": 404,
  "message": "Pengguna dengan email budi.unknown@example.com tidak ditemukan.",
  "timestamp": "2026-03-07T10:35:00.000+0000",
  "data": null
}
```

#### 400 Bad Request - Already Verified
```json
{
  "status": 400,
  "message": "Pengguna ini sudah terverifikasi sebelumnya.",
  "timestamp": "2026-03-07T10:35:00.000+0000",
  "data": null
}
```

#### 403 Forbidden - Not Admin
```json
{
  "status": 403,
  "message": "Akses ditolak.",
  "timestamp": "2026-03-07T10:35:00.000+0000",
  "data": null
}
```

### Testing di Postman

**Setup:**

1. Set method ke `PATCH`
2. URL: `http://localhost:8080/api/admin/users/budi.santoso@example.com/verify`
3. Go to **Headers** tab:
   - Key: `Content-Type`, Value: `application/json`
   - Key: `Authorization`, Value: `Bearer <JWT_ADMIN_TOKEN>`
4. **Body:** Kosong (tidak perlu request body)
5. Click **Send**

**Expected Response:**
- Status: `200 OK`
- `isVerified` = `true`
- `verifiedBy` = admin email
- `verifiedAt` = current timestamp
- Email terkirim ke PO: "Akun Anda telah diverifikasi. Anda sekarang dapat mengajukan proyek."

### Implementation Details

**User Model Fields:**
```java
@Column(nullable = false)
private boolean isVerified = false;          // Contact verification status (admin)

@Column(length = 255)
private String verifiedBy;                   // Email of verifying admin

private LocalDateTime verifiedAt;            // When verified
```

**Business Logic:**
1. Admin calls `PATCH /api/admin/users/{email}/verify` dengan email PO di path parameter
2. Sistem check:
   - User dengan email tersebut ada? → 404 if not
   - User sudah verified? → 400 if yes
3. Set `isVerified = true`, `verifiedBy = admin_email`, `verifiedAt = now()`
4. Save user ke database
5. Send email to PO: "Akun Anda telah diverifikasi..."
6. Return 200 dengan updated user data

### Email Content

**Subject:** Akun Anda Telah Diverifikasi - IPFO SIFPI

**Body:**
```
Halo [USER_NAME],

Kami dengan senang hati memberitahu bahwa akun Anda telah diverifikasi oleh administrator IPFO SIFPI.

Anda sekarang dapat mengajukan proyek ke platform.

Terima kasih telah bergabung dengan kami!

Salam,
Tim IPFO SIFPI
```

### Integration Notes

- Endpoint ini akan di-call dari halaman "Manajemen Pengguna" (fitur teman Anda) 
- Di halaman tersebut, admin akan: lihat list PO → filter/cari → klik "Verifikasi Akun" → dialog confirm → call endpoint ini
- Response: toast notification "Berhasil diverifikasi" + badge status PO berubah dari "Pending Verification" → "Terverifikasi"

---

## 11. DELETE USER (SOFT DELETE) - DEACTIVATE ACCOUNT

### Overview

Fitur untuk admin menonaktifkan akun pengguna (soft delete). Ketika akun di-deactivate, pengguna tidak bisa login. Admin dapat mengaktifkan kembali kapan saja. Fitur ini berlaku untuk **semua role pengguna** (Project Owner, Investor, Admin, Executive).

### Endpoint

```
PATCH /api/admin/users/{email}/status
```

**Method:** PATCH  
**Authentication:** Bearer Token (JWT)  
**Authorization:** ADMIN role only  

### Request

**URL Parameter:**
```
{email} = email pengguna (e.g., budi.santoso@example.com)
```

**Request Body:**
```json
{
  "isActive": false
}
```

**Request Field Specification:**

| Field | Tipe Data | Required | Valid Values | Deskripsi |
|-------|-----------|----------|--------------|-----------|
| `isActive` | Boolean | ✅ Yes | `true` atau `false` | Status aktif user. false = deactivate, true = activate |

### Success Response (200 OK)

```json
{
  "status": 200,
  "message": "Status pengguna berhasil diperbarui.",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "budi.santoso@example.com",
    "name": "Budi Santoso",
    "organization": "PT. Maju Jaya",
    "isActive": false,
    "changedBy": "admin@ipfo.go.id",
    "changedAt": "2026-03-07T14:30:00",
    "action": "DEACTIVATED",
    "roleName": "PROJECT_OWNER"
  }
}
```

**Response Field Specification:**

| Field | Tipe Data | Deskripsi |
|-------|-----------|-----------|
| `id` | UUID | User ID |
| `email` | String | Email pengguna |
| `name` | String | Nama lengkap pengguna |
| `organization` | String | Organisasi pengguna |
| `isActive` | Boolean | Status aktif saat ini (true = active, false = deactivate) |
| `changedBy` | String | Email admin yang melakukan perubahan |
| `changedAt` | ISO 8601 DateTime | Timestamp perubahan status |
| `action` | String | Aksi yang dilakukan: `DEACTIVATED` atau `ACTIVATED` |
| `roleName` | String | Role pengguna (PROJECT_OWNER, INVESTOR, ADMIN, EXECUTIVE) |

### Error Response (404 Not Found)

```json
{
  "status": 404,
  "message": "User dengan email budi.santoso@example.com tidak ditemukan",
  "data": null
}
```

### Error Response (403 Forbidden - Non-Admin)

```json
{
  "status": 403,
  "message": "Akses ditolak. Hanya admin yang dapat mengubah status pengguna.",
  "data": null
}
```

### Business Logic

1. **Validasi Admin:** Cek user yang melakukan request adalah ADMIN role → jika tidak, return 403 Forbidden
2. **Cari User:** Query user by email (case-insensitive) → jika tidak ketemu, return 404 Not Found
3. **Update Status:** Set field `active` = `isActive` (dari request)
4. **Audit Trail:** Set field `changedBy` = admin email (dari JWT), `changedAt` = now(), `action` = "DEACTIVATED" atau "ACTIVATED"
5. **Save & Return:** Save ke database, return UserStatusResponseDTO dengan 200 OK

### Data Model

**User Entity Fields (UM-11):**
```java
@Column(nullable = false)
private boolean active = true;  // Status aktif user

@Column(length = 255)
private String changedBy;  // Email admin yang melakukan perubahan status

private LocalDateTime changedAt;  // Timestamp perubahan status

@Column(length = 50)
private String action;  // "DEACTIVATED" atau "ACTIVATED"
```

### Use Cases

#### Use Case 1: Deactivate User (Admin menonaktifkan akun)

**Skenario:**
1. Admin membuka halaman user management
2. Admin menemukan user yang bermasalah (e.g., Budi Santoso)
3. Admin klik tombol "Nonaktifkan Akun"
4. Dialog confirm: "Akun ini akan dinonaktifkan. User tidak dapat login."
5. Admin klik confirm
6. Sistem call: `PATCH /api/admin/users/budi.santoso@example.com/status` dengan `isActive: false`
7. Response success → Toast: "Akun berhasil dinonaktifkan"
8. Halaman user management: status Budi berubah dari "Aktif" → "Tidak Aktif" (grayed out / disabled badge)

**Database State:**
```
UPDATE users SET active = false, changed_by = 'admin@ipfo.go.id', changed_at = '2026-03-07 14:30:00', action = 'DEACTIVATED' WHERE email = 'budi.santoso@example.com'
```

#### Use Case 2: Reactivate User (Admin mengaktifkan kembali akun)

**Skenario:**
1. Admin membuka halaman user management
2. Admin menemukan user yang sudah di-deactivate (e.g., Budi Santoso)
3. Admin klik tombol "Aktifkan Akun"
4. Dialog confirm: "Akun ini akan diaktifkan kembali. User dapat login."
5. Admin klik confirm
6. Sistem call: `PATCH /api/admin/users/budi.santoso@example.com/status` dengan `isActive: true`
7. Response success → Toast: "Akun berhasil diaktifkan"
8. Halaman user management: status Budi berubah dari "Tidak Aktif" → "Aktif"

**Database State:**
```
UPDATE users SET active = true, changed_by = 'admin@ipfo.go.id', changed_at = '2026-03-07 15:00:00', action = 'ACTIVATED' WHERE email = 'budi.santoso@example.com'
```

### Impact on Login & Application

**Deactivated User:**
- ❌ Tidak dapat login (authentication fails)
- ❌ Session invalid (jika sedang login, session langsung invalid)
- ❌ Tidak dapat akses API endpoints (semua request return 401 Unauthorized)

**Reactivated User:**
- ✅ Dapat login kembali
- ✅ Dapat mengakses semua features sesuai role
- ✅ Data terdahulu tetap preserved (tidak ada hard delete)

### Postman Testing Guide

**Request:**
```
PATCH http://localhost:8080/api/admin/users/budi.santoso@example.com/status

Headers:
Authorization: Bearer {ADMIN_JWT_TOKEN}
Content-Type: application/json

Body (raw JSON):
{
  "isActive": false
}
```

**Expected Response (200 OK):**
```json
{
  "status": 200,
  "message": "Status pengguna berhasil diperbarui.",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "budi.santoso@example.com",
    "name": "Budi Santoso",
    "organization": "PT. Maju Jaya",
    "isActive": false,
    "changedBy": "admin@ipfo.go.id",
    "changedAt": "2026-03-07T14:30:00",
    "action": "DEACTIVATED",
    "roleName": "PROJECT_OWNER"
  }
}
```

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 5.0 | 2026-03-07 | Added Section 11: DELETE USER (SOFT DELETE) - DEACTIVATE ACCOUNT (UM-11) - Admin endpoint `PATCH /api/admin/users/{email}/status` for all user roles with isActive toggle, audit trail (changedBy, changedAt, action) |
| 4.0 | 2026-03-07 | Added Section 10: UPDATE USER CONTACT VERIFICATION STATUS (UM-10) - Admin verification endpoint `PATCH /api/admin/users/{email}/verify` for Project Owners with isVerified field, verifiedBy audit trail, email notifications |
| 3.0 | 2026-03-05 | Added Section 3: EMAIL VERIFICATION FLOW (25-min expiry, token expired = re-register, success modal on /login) |
| 2.2 | 2026-03-05 | Final cleanup: User fields optimized (emailVerified + isActive only), event-driven email implemented, enum fields relocated to common/enums |
| 2.1 | 2026-03-05 | Added 7 optional fields to InvestorProfile (preferredInvestmentInstrument, engagementModel, stagePreference, riskAppetite, esgStandards, localPresence, aumSize) for Investor Catalogue Template |
| 2.0 | 2026-03-04 | Merged docs + added enum values + registration-options endpoint |
| 1.0 | 2026-03-01 | Initial specification |

