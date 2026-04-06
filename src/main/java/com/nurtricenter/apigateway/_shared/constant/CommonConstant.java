package com.nurtricenter.apigateway._shared.constant;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonConstant {

	public static final String X_REQUEST_ID_HEADER = "X-Request-Id";
	public static final String X_RATE_LIMIT_LIMIT_HEADER = "X-Rate-Limit-Limit";
	public static final String X_RATE_LIMIT_REMAINING_HEADER = "X-Rate-Limit-Remaining";
	public static final String ROLES_KEY = "roles";
	public static final String EMAIL_KEY = "email";
	public static final String NAME_KEY = "name";
	public static final String COMMA = ",";
	public static final String ROLE_PREFIX = "ROLE_";

}
