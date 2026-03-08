# SIFPI - Catatan Pengembangan

## Identitas
- **Nama**: Muhammad Hibrizi Farghana
- **NPM**: 2306165585
- **Kelas**: Propensi A
- **Role**: Lead Programmer

---

## Sprint 1

### UM-1: Login

- Endpoint POST /api/auth/login menerima body: email, password
- Sistem memvalidasi email terhadap tabel USER dan mencocokkan password dengan PasswordHash.
- Jika email tidak ditemukan → return 401 "Email tidak terdaftar".
- Jika password salah → return 401 "Password salah".
- Jika akun isActive = false → return 403 "Akun telah dinonaktifkan".
- Jika akun PO dan is_verified = false → return 403 "Akun belum diverifikasi oleh Admin".
- Jika berhasil → return 200 dengan session token dan data user: email, nama, role, organisasi.
- Session disimpan menggunakan mekanisme auth JWT.

### UM-13: Create New Role

- Endpoint POST /api/roles
- Body:
    - role_name
    - description
    - status (draft/active)
    - permissions[]
- Validasi:
    - Nama role unik
    - Minimal 1 permission
- Role bisa dalam status Draft atau Active

### PM-8: Create Project Bulk Upload

- Endpoint POST /api/admin/projects/batch-upload menerima file CSV/Excel.
- Kolom file mapping ke field: title, description, sector, scope, location_province, location_city, capex, opex, roi, npv, estimated_budget, funding_scheme, length_of_connection, details, contact_name, contact_email, contact_phone.
- Sistem memvalidasi setiap row: field wajib, format data, referensi (sector, location valid).
- Batch dikaitkan dengan record batches: batch_name, type = upload, uploaded_by, uploaded_at, total_projects, status.
- Proyek yang valid dibuat dengan status approved (Ready to Publish, bypass review karena Admin upload).
- Proyek yang gagal validasi dikumpulkan sebagai error report.
- Return 200 dengan summary: { success_count, failed_count, errors[] }.
- Hanya role admin.
