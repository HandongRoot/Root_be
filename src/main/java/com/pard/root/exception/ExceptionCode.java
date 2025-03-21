package com.pard.root.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExceptionCode implements BaseException {
    // Common
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "접근할 권한이 없습니다."),
    AUTHENTICATION_REQUIRED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생했습니다."),
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생했습니다."),

    // Contents
    CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "콘텐츠를 찾을 수 없습니다."),
    INVALID_CONTENT_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 콘텐츠 ID입니다."),
    CONTENT_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "콘텐츠 생성에 실패했습니다."),
    DUPLICATE_CONTENT(HttpStatus.CONFLICT, "이미 존재하는 콘텐츠입니다."),
    INVALID_CONTENT_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 콘텐츠 형식입니다."),
    CONTENT_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "콘텐츠 수정에 실패했습니다."),
    CONTENT_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "콘텐츠 삭제에 실패했습니다."),

    // Category
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다."),
    INVALID_CATEGORY_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 카테고리 ID입니다."),
    CATEGORY_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "카테고리 생성에 실패했습니다."),
    DUPLICATE_CATEGORY_NAME(HttpStatus.CONFLICT, "이미 존재하는 카테고리 이름입니다."),
    CATEGORY_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "카테고리 수정에 실패했습니다."),
    CATEGORY_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "카테고리 삭제에 실패했습니다.");

    private final HttpStatus status;
    private final String message;
}
