package com.company;

import java.awt.*;

public class Main {

    public static void main(String[] args) {

        Schema schema = new Schema();

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                System.out.print(schema.getArray()[i][j].getColor() + " ");
            }
            System.out.println();
        }

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                System.out.print(schema.getArray()[i][j].getWord() + " ");
            }
            System.out.println();
        }



        Drawer drawer = new Drawer();
        drawer.drawCard(schema.getArray()[0][0]);
        drawer.save("test_file");

    }





}
