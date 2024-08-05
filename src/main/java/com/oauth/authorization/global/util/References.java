package com.oauth.authorization.global.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class References {

	public static final String ELASTIC_INDEX_PREFIX_OAUTH_COMPANY = "oauth_tenant_";
	public static final String ELASTIC_INDEX_PREFIX_OAUTH_CLIENT = "oauth_client_";
	public static final String ELASTIC_INDEX_PREFIX_OAUTH_USER = "oauth_user_";
	public static final String ELASTIC_INDEX_PREFIX_OAUTH_CLIENT_DETAILS = "oauth_client_details_";
	public static final String ELASTIC_INDEX_PREFIX_OAUTH_CODE = "oauth_authorization_code";
}
