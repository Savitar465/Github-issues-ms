package com.githubx.Github_issues_ms.util.errorhandling;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public static EntityNotFoundException issue(Integer number) {
        return new EntityNotFoundException("Issue no encontrado con número: " + number);
    }

    public static EntityNotFoundException comment(String commentId) {
        return new EntityNotFoundException("Comentario no encontrado con ID: " + commentId);
    }

    public static EntityNotFoundException label(String name) {
        return new EntityNotFoundException("Label no encontrado: " + name);
    }
}
