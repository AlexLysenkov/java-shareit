package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ErrorHandlerTest {
    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void testDuplicateEmailException() {
        DuplicateEmailException exception = new DuplicateEmailException("Пользователь с таким email уже существует");
        ErrorResponse response = errorHandler.handlerValidationException(exception);
        assertEquals(exception.getMessage(), response.getError());
    }

    @Test
    void testObjectNotFoundException() {
        ObjectNotFoundException exception = new ObjectNotFoundException("Объект не найден");
        ErrorResponse response = errorHandler.handlerNotFoundException(exception);
        assertEquals(exception.getMessage(), response.getError());
    }

    @Test
    void testBadRequestException() {
        BadRequestException exception = new BadRequestException("Некорректный запрос");
        ErrorResponse response = errorHandler.handleBadRequestException(exception);
        assertEquals(exception.getMessage(), response.getError());
    }

    @Test
    void testConstraintViolationException() {
        ConstraintViolationException exception =
                new ConstraintViolationException("Ошибка ограничений параметров", null);
        ErrorResponse response = errorHandler.handleConstraintViolationException(exception);
        assertEquals(exception.getMessage(), response.getError());
    }

    @Test
    public void handleThrowableTest() {
        Throwable throwable = Mockito.mock(Throwable.class);
        String errorMessage = "Произошла непредвиденная ошибка";
        Mockito.when(throwable.getMessage()).thenReturn(errorMessage);
        ErrorResponse response = errorHandler.handleThrowable(throwable);
        assertEquals(errorMessage, response.getError());
    }
}
