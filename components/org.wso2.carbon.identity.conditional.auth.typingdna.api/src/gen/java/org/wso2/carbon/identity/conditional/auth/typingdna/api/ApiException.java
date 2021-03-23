package org.wso2.carbon.identity.conditional.auth.typingdna.api;
//comment
public class ApiException extends Exception{
	private int code;
	public ApiException (int code, String msg) {
		super(msg);
		this.code = code;
	}
}
