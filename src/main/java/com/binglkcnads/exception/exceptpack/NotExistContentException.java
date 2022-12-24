package com.binglkcnads.exception.exceptpack;

@SuppressWarnings("all")
public class NotExistContentException extends RuntimeException{
    public NotExistContentException(){

    }
    public NotExistContentException(String s){
        super(s);
    }
}
