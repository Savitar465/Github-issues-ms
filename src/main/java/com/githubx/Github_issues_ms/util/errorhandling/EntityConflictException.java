package com.githubx.Github_issues_ms.util.errorhandling;

public class EntityConflictException extends RuntimeException {

    public EntityConflictException(String message) {
        super(message);
    }

    public static EntityConflictException labelName(String name) {
        return new EntityConflictException("Ya existe un label con el nombre: " + name);
    }
}
