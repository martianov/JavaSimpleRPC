package com.martianov.simplerpc.common.message;

/**
 * @author Andrey Martyanov <martianovas@gmail.com>
 */
public interface IError extends IResponse {
    String getMessage();
}
