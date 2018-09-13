package com.javarush.task.task30.task3008;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class ConsoleHelper {

    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    
    
    public static int readInt() {
        int number = 0;
            try {
                number = Integer.parseInt(readString());
            } catch (NumberFormatException e) {
                System.out.println( "Произошла ошибка при попытке ввода числа. Попробуйте еще раз.");
                number = Integer.parseInt(readString());
            }
        return number;
    }
    
    public static String readString() {
        String message = "";
            try {
                
                message = reader.readLine(); 
                
            } catch (IOException e) {
                System.out.println("Произошла ошибка при попытке ввода текста. Попробуйте еще раз.");
                message = readString();
            }
        
        return message;
    }
    
    public static void writeMessage(String message) {
        System.out.println(message);
    }
    
}