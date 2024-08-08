package com.oauth.authorization.global.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SourceIpUtil {

    private static final List<String> IP_HEADER_CANDIDATES = Arrays.asList(
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
    );

    public static String extractSourceIpFrom(HttpServletRequest request) {
        return IP_HEADER_CANDIDATES.stream()
                .map(request::getHeader)
                .filter(StringUtils::isNotBlank)
                .findFirst()
                .orElseGet(request::getRemoteAddr);
    }
}
