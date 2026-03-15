package com.project.back_end.services;

import com.project.back_end.repositories.AdminRepository;
import com.project.back_end.repositories.DoctorRepository;
import com.project.back_end.repositories.PatientRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class TokenService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public TokenService(AdminRepository adminRepository,
                        DoctorRepository doctorRepository,
                        PatientRepository patientRepository) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    // 3. Signing Key
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // 4. Generate Token
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000))
                .signWith(getSigningKey())
                .compact();
    }

    // 5. Extract Email
    public String extractEmail(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // Extract user ID (email-based lookup helper used by other services)
    public Long extractUserId(String token) {
        String email = extractEmail(token);
        // Try patient first, then doctor
        var patient = patientRepository.findByEmail(email);
        if (patient != null) return patient.getId();
        var doctor = doctorRepository.findByEmail(email);
        if (doctor != null) return doctor.getId();
        throw new RuntimeException("User not found for token");
    }

    // 6. Validate Token
    public boolean validateToken(String token, String role) {
        try {
            String email = extractEmail(token);
            return switch (role.toLowerCase()) {
                case "admin"   -> adminRepository.findByUsername(email) != null;
                case "doctor"  -> doctorRepository.findByEmail(email) != null;
                case "patient" -> patientRepository.findByEmail(email) != null;
                default        -> false;
            };
        } catch (Exception e) {
            return false;
        }
    }
}