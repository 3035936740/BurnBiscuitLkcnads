package com.binglkcnads.exception.exceptpack;

@SuppressWarnings("all")
public class UnknownException extends RuntimeException {
    public UnknownException(){

    }
    public UnknownException(String s){
        super(s);
    }
}
