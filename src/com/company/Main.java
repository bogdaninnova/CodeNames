package com.company;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Schema schema = new Schema();

        new Drawer(schema, "field", false);
        new Drawer(schema, "fieldAdmin", true);




        Scanner scanner = new Scanner(System.in);
        try {
            while (true) {
                String line = scanner.nextLine();
                if (schema.checkWord(line))
                    new Drawer(schema, "field", false);
            }
        } catch(IllegalStateException | NoSuchElementException e) {
            e.printStackTrace();
        }

    }





}
