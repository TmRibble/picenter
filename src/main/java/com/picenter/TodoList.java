package com.picenter;


public class TodoList {
    
    public static final String SYSTEM_NAME = "picenter_todolist";

    private static Logger logger;
    
    public static boolean init(Logger log){
        logger = log;

        return true;
    }

}
