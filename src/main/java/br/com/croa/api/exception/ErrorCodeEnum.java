package br.com.croa.api.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCodeEnum {

    ERROR_UNAUTHORIZED("error.unauthorized"),
    ERROR_CLIENT_NOT_FOUND("error.client.not.found"),
    ERROR_CLIENT_PROCESSOR_AUDIT("error.client.processor.audit");

    private final String messageKey;
}
