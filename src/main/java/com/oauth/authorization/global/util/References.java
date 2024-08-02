package com.oauth.authorization.global.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class References {

	public static final String ELASTIC_INDEX_PREFIX_OAUTH_COMPANY = "oauth_tenant_";
	public static final String ELASTIC_INDEX_PREFIX_OAUTH_CLIENT = "oauth_client_";
	public static final String ELASTIC_INDEX_PREFIX_OAUTH_USER = "oauth_user_";
}
