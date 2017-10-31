package com.showcase.search.exceptions;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Standard Class to represent errors returned from Microservices.
 *
 * To standardize HTTP Errors thrown from backend services, all controllers will catch underlying
 *     service exceptions and throw an HttpServiceException wrapping the underlying exception as the
 *     cause
 *
 * A generic HttpServiceExceptionHandler will catch all these exceptions and return a standard
 *     error response that will return an Http Status code, a reason phrase and then a detailed payload
 *     with the errors. This class encapsulates that error response
 */
@Data
@Builder
@ApiModel(description = "Response object with details about the error that occurred")
public class HttpErrorResponse {
    @ApiModelProperty(required = true, value = "The time the error occurred", example = "1459877209749")
    private Date timestamp;

    @ApiModelProperty(required = true, value = "HTTP Status code" ,allowableValues = "[400, 499]", example = "4xx")
    private int status;

    @ApiModelProperty(required = true, value = "A duplication of the reason message for the status code", example = "Not found")
    private String error;

    @ApiModelProperty(required = false, value = "A message to describe what error occurred",
            example = "Bad Data Request")
    private String message;

    @ApiModelProperty(required = false, value = "Optional additional errors to enable the client to provide appropriate user experience",
            example = "Field A cannot be more than 10 characters")
    private List<String> detailedErrors;
}
