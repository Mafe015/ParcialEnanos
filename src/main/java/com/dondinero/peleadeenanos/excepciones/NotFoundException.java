package com.dondinero.peleadeenanos.excepciones;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String message){
        super(message);
    }
}
