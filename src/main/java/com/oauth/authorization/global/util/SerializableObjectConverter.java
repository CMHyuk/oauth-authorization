package com.oauth.authorization.global.util;

import com.oauth.authorization.global.exception.BusinessException;
import com.oauth.authorization.global.exception.InternalServerErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("deprecation")
public class SerializableObjectConverter {

	public static String serialize(OAuth2Authorization object) {
        try {
            byte[] bytes = SerializationUtils.serialize(object);
            return Base64.encodeBase64String(bytes);
        } catch(Exception e) {
            throw BusinessException.from(new InternalServerErrorCode(e.getMessage()));
        }
    }

    public static OAuth2Authorization  deserialize(String encodedObject) {
        try {
            byte[] bytes = Base64.decodeBase64(encodedObject);
            return SerializationUtils.deserialize(bytes);
        } catch(Exception e) {
            throw BusinessException.from(new InternalServerErrorCode(e.getMessage()));
        }
    }

    public static String serializeAuthentication(OAuth2Authorization  object) {
		try {
			byte[] bytes = SerializationUtils.serialize(object);
			return Base64.encodeBase64String(bytes);
		} catch (Exception e) {
            throw BusinessException.from(new InternalServerErrorCode(e.getMessage()));
		}
	}

	public static OAuth2Authorization  deserializeAuthentication(String encodedObject) {
		try {
			byte[] bytes = Base64.decodeBase64(encodedObject);
			return SerializationUtils.deserialize(bytes);
		} catch (Exception e) {
            throw BusinessException.from(new InternalServerErrorCode(e.getMessage()));
		}
	}
}
