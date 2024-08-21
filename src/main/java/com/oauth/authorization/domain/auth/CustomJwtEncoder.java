package com.oauth.authorization.domain.auth;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.factories.DefaultJWSSignerFactory;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.produce.JWSSignerFactory;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.oauth.authorization.domain.tenant.dto.KeyResponse;
import com.oauth.authorization.domain.tenant.service.TenantInfoService;
import com.oauth.authorization.global.exception.BusinessException;
import com.oauth.authorization.global.exception.InternalServerErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URL;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class CustomJwtEncoder implements JwtEncoder {

    private final TenantInfoService tenantInfoService;
    private static final JWSSignerFactory JWS_SIGNER_FACTORY = new DefaultJWSSignerFactory();
    private final Map<JWK, JWSSigner> jwsSigners = new ConcurrentHashMap();

    @Override
    public Jwt encode(JwtEncoderParameters parameters) throws JwtEncodingException {
        RSAKey rsaKey = getRsaKey();
        JWKSet jwkSet = new JWKSet(rsaKey);
        JWKSource<SecurityContext> jwkSource = (jwkSelector, context) -> jwkSelector.select(jwkSet);

        JwsHeader headers = parameters.getJwsHeader();
        JwtClaimsSet claims = parameters.getClaims();
        JWK jwk = this.selectJwk(jwkSource, headers);
        headers = addKeyIdentifierHeadersIfNecessary(headers, jwk);
        String jws = this.serialize(headers, claims, jwk);
        return new Jwt(jws, claims.getIssuedAt(), claims.getExpiresAt(), headers.getHeaders(), claims.getClaims());
    }

    private RSAKey getRsaKey() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String clientId = authentication.getName();
        KeyResponse key = tenantInfoService.getKey(clientId);

        byte[] publicKeyBytes = key.pubKey();
        byte[] privateKeyBytes = key.priKey();

        RSAPublicKey rsaPublicKey = loadPublicKey(publicKeyBytes);
        RSAPrivateKey rsaPrivateKey = loadPrivateKey(privateKeyBytes);

        return new RSAKey.Builder(rsaPublicKey)
                .privateKey(rsaPrivateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    private RSAPublicKey loadPublicKey(byte[] publicKeyBytes) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw BusinessException.from(new InternalServerErrorCode(e.getMessage()));
        }
    }

    private RSAPrivateKey loadPrivateKey(byte[] privateKeyBytes) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw BusinessException.from(new InternalServerErrorCode(e.getMessage()));
        }
    }

    private JWK selectJwk(JWKSource<SecurityContext> jwkSource, JwsHeader headers) {
        List jwks;
        try {
            JWKSelector jwkSelector = new JWKSelector(createJwkMatcher(headers));
            jwks = jwkSource.get(jwkSelector, null);
        } catch (Exception var4) {
            Exception ex = var4;
            throw new JwtEncodingException(String.format("An error occurred while attempting to encode the Jwt: %s", "Failed to select a JWK signing key -> " + ex.getMessage()), ex);
        }

        if (jwks.size() > 1) {
            throw new JwtEncodingException(String.format("An error occurred while attempting to encode the Jwt: %s", "Found multiple JWK signing keys for algorithm '" + headers.getAlgorithm().getName() + "'"));
        } else if (jwks.isEmpty()) {
            throw new JwtEncodingException(String.format("An error occurred while attempting to encode the Jwt: %s", "Failed to select a JWK signing key"));
        } else {
            return (JWK) jwks.get(0);
        }
    }

    private String serialize(JwsHeader headers, JwtClaimsSet claims, JWK jwk) {
        JWSHeader jwsHeader = convert(headers);
        JWTClaimsSet jwtClaimsSet = convert(claims);
        JWSSigner jwsSigner = this.jwsSigners.computeIfAbsent(jwk, CustomJwtEncoder::createSigner);
        SignedJWT signedJwt = new SignedJWT(jwsHeader, jwtClaimsSet);

        try {
            signedJwt.sign(jwsSigner);
        } catch (JOSEException var9) {
            JOSEException ex = var9;
            throw new JwtEncodingException(String.format("An error occurred while attempting to encode the Jwt: %s", "Failed to sign the JWT -> " + ex.getMessage()), ex);
        }

        return signedJwt.serialize();
    }

    private static JWKMatcher createJwkMatcher(JwsHeader headers) {
        JWSAlgorithm jwsAlgorithm = JWSAlgorithm.parse(headers.getAlgorithm().getName());
        if (!JWSAlgorithm.Family.RSA.contains(jwsAlgorithm) && !JWSAlgorithm.Family.EC.contains(jwsAlgorithm)) {
            return JWSAlgorithm.Family.HMAC_SHA.contains(jwsAlgorithm) ? (new JWKMatcher.Builder()).keyType(KeyType.forAlgorithm(jwsAlgorithm)).keyID(headers.getKeyId()).privateOnly(true).algorithms(new Algorithm[]{jwsAlgorithm, null}).build() : null;
        } else {
            return (new JWKMatcher.Builder()).keyType(KeyType.forAlgorithm(jwsAlgorithm)).keyID(headers.getKeyId()).keyUses(KeyUse.SIGNATURE, null).algorithms(jwsAlgorithm, null).x509CertSHA256Thumbprint(Base64URL.from(headers.getX509SHA256Thumbprint())).build();
        }
    }

    private static JwsHeader addKeyIdentifierHeadersIfNecessary(JwsHeader headers, JWK jwk) {
        if (StringUtils.hasText(headers.getKeyId()) && StringUtils.hasText(headers.getX509SHA256Thumbprint())) {
            return headers;
        } else if (!StringUtils.hasText(jwk.getKeyID()) && jwk.getX509CertSHA256Thumbprint() == null) {
            return headers;
        } else {
            JwsHeader.Builder headersBuilder = JwsHeader.from(headers);
            if (!StringUtils.hasText(headers.getKeyId()) && StringUtils.hasText(jwk.getKeyID())) {
                headersBuilder.keyId(jwk.getKeyID());
            }

            if (!StringUtils.hasText(headers.getX509SHA256Thumbprint()) && jwk.getX509CertSHA256Thumbprint() != null) {
                headersBuilder.x509SHA256Thumbprint(jwk.getX509CertSHA256Thumbprint().toString());
            }

            return headersBuilder.build();
        }
    }

    private static JWSSigner createSigner(JWK jwk) {
        try {
            return JWS_SIGNER_FACTORY.createJWSSigner(jwk);
        } catch (JOSEException var2) {
            JOSEException ex = var2;
            throw new JwtEncodingException(String.format("An error occurred while attempting to encode the Jwt: %s", "Failed to create a JWS Signer -> " + ex.getMessage()), ex);
        }
    }

    private static JWSHeader convert(JwsHeader headers) {
        JWSHeader.Builder builder = new JWSHeader.Builder(JWSAlgorithm.parse(headers.getAlgorithm().getName()));
        if (headers.getJwkSetUrl() != null) {
            builder.jwkURL(convertAsURI("jku", headers.getJwkSetUrl()));
        }

        Map<String, Object> jwk = headers.getJwk();
        if (!CollectionUtils.isEmpty(jwk)) {
            try {
                builder.jwk(JWK.parse(jwk));
            } catch (Exception var11) {
                Exception ex = var11;
                throw new JwtEncodingException(String.format("An error occurred while attempting to encode the Jwt: %s", "Unable to convert 'jwk' JOSE header"), ex);
            }
        }

        String keyId = headers.getKeyId();
        if (StringUtils.hasText(keyId)) {
            builder.keyID(keyId);
        }

        if (headers.getX509Url() != null) {
            builder.x509CertURL(convertAsURI("x5u", headers.getX509Url()));
        }

        List<String> x509CertificateChain = headers.getX509CertificateChain();
        if (!CollectionUtils.isEmpty(x509CertificateChain)) {
            List<Base64> x5cList = new ArrayList();
            x509CertificateChain.forEach((x5c) -> {
                x5cList.add(new Base64(x5c));
            });
            if (!x5cList.isEmpty()) {
                builder.x509CertChain(x5cList);
            }
        }

        String x509SHA1Thumbprint = headers.getX509SHA1Thumbprint();
        if (StringUtils.hasText(x509SHA1Thumbprint)) {
            builder.x509CertThumbprint(new Base64URL(x509SHA1Thumbprint));
        }

        String x509SHA256Thumbprint = headers.getX509SHA256Thumbprint();
        if (StringUtils.hasText(x509SHA256Thumbprint)) {
            builder.x509CertSHA256Thumbprint(new Base64URL(x509SHA256Thumbprint));
        }

        String type = headers.getType();
        if (StringUtils.hasText(type)) {
            builder.type(new JOSEObjectType(type));
        }

        String contentType = headers.getContentType();
        if (StringUtils.hasText(contentType)) {
            builder.contentType(contentType);
        }

        Set<String> critical = headers.getCritical();
        if (!CollectionUtils.isEmpty(critical)) {
            builder.criticalParams(critical);
        }

        Map<String, Object> customHeaders = new HashMap();
        headers.getHeaders().forEach((name, value) -> {
            if (!JWSHeader.getRegisteredParameterNames().contains(name)) {
                customHeaders.put(name, value);
            }

        });
        if (!customHeaders.isEmpty()) {
            builder.customParams(customHeaders);
        }

        return builder.build();
    }

    private static JWTClaimsSet convert(JwtClaimsSet claims) {
        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
        Object issuer = claims.getClaim("iss");
        if (issuer != null) {
            builder.issuer(issuer.toString());
        }

        String subject = claims.getSubject();
        if (StringUtils.hasText(subject)) {
            builder.subject(subject);
        }

        List<String> audience = claims.getAudience();
        if (!CollectionUtils.isEmpty(audience)) {
            builder.audience(audience);
        }

        Instant expiresAt = claims.getExpiresAt();
        if (expiresAt != null) {
            builder.expirationTime(Date.from(expiresAt));
        }

        Instant notBefore = claims.getNotBefore();
        if (notBefore != null) {
            builder.notBeforeTime(Date.from(notBefore));
        }

        Instant issuedAt = claims.getIssuedAt();
        if (issuedAt != null) {
            builder.issueTime(Date.from(issuedAt));
        }

        String jwtId = claims.getId();
        if (StringUtils.hasText(jwtId)) {
            builder.jwtID(jwtId);
        }

        Map<String, Object> customClaims = new HashMap();
        claims.getClaims().forEach((name, value) -> {
            if (!JWTClaimsSet.getRegisteredNames().contains(name)) {
                customClaims.put(name, value);
            }

        });
        if (!customClaims.isEmpty()) {
            Objects.requireNonNull(builder);
            customClaims.forEach(builder::claim);
        }

        return builder.build();
    }

    private static URI convertAsURI(String header, URL url) {
        try {
            return url.toURI();
        } catch (Exception var3) {
            Exception ex = var3;
            throw new JwtEncodingException(String.format("An error occurred while attempting to encode the Jwt: %s", "Unable to convert '" + header + "' JOSE header to a URI"), ex);
        }
    }
}
