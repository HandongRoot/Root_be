package com.pard.root.helper.constants;

public enum SocialLoginType {
        KAKAO("kakao"),
        NAVER("naver"),
        GOOGLE("google");

        private final String provider;

        SocialLoginType(String provider) {
                this.provider = provider;
        }

        public String getProvider() {
                return provider;
        }

        public static SocialLoginType fromProvider(String provider) {
                for (SocialLoginType type : values()) {
                        if (type.getProvider().equalsIgnoreCase(provider)) {
                                return type;
                        }
                }
                return null;
        }
}
