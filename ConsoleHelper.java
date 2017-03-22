package com.javarush.task.task30.task3008;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Admin on 20.03.2017.
 */
public class ConsoleHelper {

    private static BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

    public static void writeMessage(String message) {
        System.out.println(message);
    }

    public static String readString() {
        String line;
        try {
            line = bufferedReader.readLine();
        } catch (IOException e) {
            System.out.println("Произошла ошибка при попытке ввода текста. Попробуйте еще раз.");
            line = readString();
        }
        return line;
    }

    public static int readInt() {
        int number;

        try {
            number = Integer.parseInt(readString());
        } catch (NumberFormatException nfe) {
            System.out.println("Произошла ошибка при попытке ввода числа. Попробуйте еще раз.");
            number = Integer.parseInt(readString());
        }

        return number;
    }
}
