package com.haruanbu.haruanbu_api.common.web;

import com.haruanbu.haruanbu_api.common.exception.ApiException;
import com.haruanbu.haruanbu_api.common.exception.ErrorCode;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.UUID;

public class CurrentUserIdArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String HEADER = "X-USER-ID";

    @Override
    public boolean supportsParameter(MethodParameter parameter){
        return parameter.hasParameterAnnotation(CurrentUserId.class)
                && UUID.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ){
        String raw = webRequest.getHeader(HEADER);
        if (raw == null || raw.isBlank()){
            throw new ApiException(ErrorCode.MISSING_USER_HEADER, "헤더 X-USER-ID 필요");
        }

        try{
            return UUID.fromString(raw.trim());
        } catch (IllegalArgumentException e){
            throw new ApiException(ErrorCode.INVALID_USER_HEADER, "X-USER-ID가 UUID 형식이 아님");
        }
    }
}
