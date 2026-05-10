package com.githubx.Github_issues_ms.util.errorhandling;

public class ForbiddenOperationException extends RuntimeException {

    public ForbiddenOperationException(String message) {
        super(message);
    }

    public static ForbiddenOperationException notAuthor() {
        return new ForbiddenOperationException("Solo el autor puede modificar o eliminar este recurso.");
    }

    public static ForbiddenOperationException notMember() {
        return new ForbiddenOperationException("No tienes permisos para realizar esta operación en el repositorio.");
    }
}
