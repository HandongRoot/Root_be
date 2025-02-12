package com.pard.root.helper.constants;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserState {
    ACTIVE,
    DEACTIVATED,
    BANNED
}
