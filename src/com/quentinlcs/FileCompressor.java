package com.quentinlcs;

import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FileCompressor {

    private String fileName;
    private RandomAccessFile firstFileReader;
    private RandomAccessFile secondFileReader;
    private FileOutputStream fileWriter;
    private FileOutputStream secondFileWriter;
    private long firstFileSize = 0;
    private long secondFileSize = 0;

    public void encode() {
        boolean ask = true;

        while (ask) {
            askForFile();
            try {
                this.firstFileReader = new RandomAccessFile("resources/"+this.fileName+".txt", "r");
                this.secondFileReader = new RandomAccessFile("resources/"+this.fileName+".txt.huf", "rw");
                this.fileWriter = new FileOutputStream("resources/"+this.fileName+".txt.huf");
                this.secondFileWriter = new FileOutputStream("resources/dec_"+this.fileName+".txt");
                firstFileSize = firstFileReader.length();
                ask = false;
            } catch (Exception e) {
                System.err.println("Nom de fichier incorrect !");
            }
        }

        System.out.println("Taille du fichier : "+this.firstFileSize+" bytes");

        Sorter sorter = new Sorter(countLetters());
        if ( !sorter.isSorted() ) sorter.sort();
        Huffman huffman = new Huffman(sorter.getKeys(), sorter.getValues());
        compress(huffman);

        System.out.println(huffman);
        System.out.println("Taille compressée : "+this.secondFileSize+" bytes");
        System.out.println("Compression : "+(100-(double)this.secondFileSize/this.firstFileSize*100.0)+" %");

    }

    public void decode() {
        decompress();
        System.out.println("Fichier décompressé dans : dec_"+this.fileName+".txt");
    }

    private void askForFile() {

        Scanner scanner = new Scanner( System.in, StandardCharsets.UTF_8);
        System.out.println("Quel fichier voulez-vous ouvrir ?");
        this.fileName =  scanner.nextLine();

    }

    private Map<Character, Integer> countLetters() {
        Map<Character, Integer> result;
        char c;

        result = new HashMap<>();
        try {
            for (int i = 0; i < this.firstFileReader.length(); i++){
                c = (char) this.firstFileReader.read();
                if (result.containsKey(c)) result.replace(c, result.get(c) + 1);
                else result.put(c, 1);
            }
            this.firstFileReader.seek(0);
        } catch (Exception e) {
            System.err.println(Arrays.toString(e.getStackTrace()));
        }

        result.put((char)3, 1);

        return result;
    }

    private byte[] convertToBytes(String stringBytes, byte biggerCodeLength) {
        byte[] result = new byte[biggerCodeLength == -1 ? (stringBytes.length()/8+1) : Math.abs(biggerCodeLength/8+1)];
        int i = 0;
        while (!stringBytes.isEmpty() || (i * 8) < biggerCodeLength) {
            for (int j = stringBytes.length(); j < 8; j++)
                stringBytes += '0';
            result[i++] = (byte) Integer.parseInt(stringBytes.substring(0, 8), 2);
            stringBytes = stringBytes.substring(8);
        }



        return result;
    }

    private void compress(Huffman huffman) {
        Map<Character, String> code = huffman.getEncodedLetters();
        char c;
        StringBuilder stringBytes = new StringBuilder();

        try {
            this.fileWriter.write(huffman.getBiggerCode());
            for (Map.Entry<Character, String> entry : code.entrySet()) {
                this.fileWriter.write(entry.getKey());
                this.fileWriter.write(this.convertToBytes(entry.getValue(), huffman.getBiggerCode()));
            }
            this.fileWriter.write((char)27);
            for (int i = 0; i < this.firstFileReader.length(); i++) {
                c = (char) this.firstFileReader.readByte();
                stringBytes.append(code.get(c));
            }
            stringBytes.append(code.get((char)3));
            this.fileWriter.write(this.convertToBytes(stringBytes.toString(), (byte) -1));
            this.secondFileSize = this.secondFileReader.length();
            this.firstFileReader.close();
            this.fileWriter.close();
        } catch (Exception e) {
            System.err.println(Arrays.toString(e.getStackTrace()));
        }
    }

    private String getCorrectByte(char c) {
        return String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0');
    }

    private void decompress() {
        StringBuilder result = new StringBuilder(), buffer = new StringBuilder();;
        Map<String, Character> map = new HashMap<>();
        char c, key = ' ', check = ' ';
        int j = 1, k = 0, biggerCodeBytes = 0;
        StringBuilder value = new StringBuilder();

        try {
            for (int i = 0; i < this.secondFileReader.length(); i++) {
                c = (char) this.secondFileReader.read();
                if (i == 0) biggerCodeBytes = (c/8)+1;
                else {
                    if (j > 0) {
                        if (c == (char) 27) j = -1;
                        if ( j % 2 == 0) {
                            if (k++ < biggerCodeBytes) {
                                value.append(this.getCorrectByte(c));
                            } if (k == biggerCodeBytes) {
                                map.put(value.toString(), key);
                                value = new StringBuilder();
                                k = 0; j++;
                            }
                        } else {
                            j++;
                            key = c;
                        }
                    } else buffer.append(this.getCorrectByte(c));
                }
            }

            while (buffer.length() > 0 && check != (char)3) {
                k = 0;
                check = (char)27;

                while ( check == (char)27  ) {
                    check = Huffman.checkIfExist(map, buffer.substring(0, ++k));
                }

                if (check != (char)3) result.append(check);
                buffer.delete(0, k);
            }
            this.secondFileWriter.write(result.toString().getBytes());
            this.secondFileReader.close();
        } catch (Exception e) {
            System.err.println(Arrays.toString(e.getStackTrace()));
        }

    }

}
