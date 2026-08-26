package com.software.knowledgehub.security.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticatedUser {

    private final Long id;
    private final String username;
    private final String tokenId;
}
