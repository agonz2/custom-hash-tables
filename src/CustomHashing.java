package src;
/*
 * Alexander Gonzalez Ramirez
 * Custom Hash Tables
 */

 import java.io.*;
 import java.util.ArrayList;
 import src.RedBlackTree;
 
 class HashTable {
     String[] array;
     int[] counterArray;
     int size, count;
 
     HashTable(int capacity) {
         this.array = new String[capacity];
         this.counterArray = new int[capacity];
         this.size = capacity;
         this.count = 0;
     }
 
     // Custom hash calculation using prime number
     private int hashFunc(String key) {
         int hash = 0;
         for (int n = 0; n < key.length(); n++){
             hash = (hash * 37 + key.charAt(n)) % size;
         }
         return (hash * hash) % size;
     }
 
     // Rehash to a larger size that is the next prime number doubled the current size
     private void rehash(){
         int newSize = getPrime(size * 2);
         String [] newArray = new String[newSize];
         int[] newCounter = new int[newSize];
         int newCount = 0;
 
         for (int i = 0; i < size; i++){
             if (array[i] != null){
                 int index = hashFunc(array[i]);
 
                 while (newArray[index] != null){
                     index = (index + 1) % newSize;
                 }
 
                 newArray[index] = array[i];
                 newCounter[index] = counterArray[i];
                 newCount++;
             }
         }
 
         array = newArray;
         counterArray = newCounter;
         size = newSize;
         count = newCount;
 
     }
 
     // Insert into hash table, using linear probing if a slot is occupied
     public void insert(String word){
         final double LOAD_FACTOR = 0.75;
         int index = hashFunc(word);
 
         while (array[index] != null){
             if (array[index].equals(word)){
                 counterArray[index]++;
                 return;
             }
             index = (index + 1) % size;
         }
 
         array[index] = word;
         counterArray[index] = 1;
         count++;
 
         // Rehash if current load factor is more than 0.75
         if ((double) count / size > LOAD_FACTOR){
             rehash();
         }
     }
 
     // Check if number is a prime number
     private boolean primeCheck(int num){
         if (num <= 1){
             return false;
         }
 
         for(int i = 2; i <= Math.sqrt(num); i++){
             if (num % i == 0) return false;
         }
 
         return true;
     }
 
     // Return the next available largest prime number
     private int getPrime(int num){
         while(!primeCheck(num)){
             num++;
         }
         return num;
     }
 
     // Return the hash table size
     public int getSize(){
         return size;
     }
 
     // Return the hash table load factor
     public double getLoadFactor(){
         return (double) count / size;
     }
 }
 
 public class CustomHashing {
     public static void main(String[] args) {
         String filePath = "src/dracula.txt";
         File results = new File("results.txt");
 
         final int CAPACITY_ONE = 10007;
         final int CAPACITY_TWO = 30031;
        
         src.RedBlackTree Nodes = new RedBlackTree();
         HashTable Hash1 = new HashTable(CAPACITY_ONE);
         HashTable Hash2 = new HashTable(CAPACITY_TWO);
 
         ArrayList<String> StringList = new ArrayList<>();
 
         try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
             StringBuilder processString;
             StringBuilder secondString;
 
             String[] splitText;
             String currentLine;
 
             while ((currentLine = reader.readLine()) != null) {
                 try {
                     // Split and put each word into string array
                     splitText = currentLine.split("[\\s\\n]+");
 
                     // Iterate through each index in string array
                     checkString:
                     for (String text : splitText) {
                         String mainString;
                         int dashCount = 0;
                         processString = new StringBuilder();
                         secondString = new StringBuilder();
 
                         // Iterate through each character in text
                         for (int j = 0; j < text.length(); j++) {
                             char c = text.charAt(j);
                             if (c == '-') dashCount++;
 
                             // If character is a letter add it to string otherwise end loop
                             if (!Character.isLetter(c)) continue;
 
                             // Add characters to new string if two dashes are found
                             if (dashCount >= 2){
                                 secondString.append(c);
                                 continue;
                             }
 
                             processString.append(c);
                         }
 
                         // Convert to all lowercase string and store
                         mainString = processString.toString().toLowerCase();
                         StringList.add(mainString);
 
                         // Store second string also if minimum dashes are found
                         if (dashCount >= 2) StringList.add(secondString.toString().toLowerCase());
 
                     }
 
                 } catch (IllegalArgumentException e) {System.out.println(e.getMessage());}
             }
 
             reader.close();
 
         } catch (IOException e) {System.out.println(e.getMessage());}
 
         // Pass each word in StringList
         for (String word : StringList) {
             Nodes.insert(word);
             Hash1.insert(word);
             Hash2.insert(word);
         }
 
         // Write out analysis on file
         try (BufferedWriter writer = new BufferedWriter(new FileWriter(results))) {
             writer.write("Alexander Gonzalez Ramirez\n");
             writer.write(
                     "\n[HASH TABLE #1]\n" +
                     "Size: " + Hash1.getSize() +
                     "\nLoad Factor: " + Hash1.getLoadFactor());
             writer.newLine();
             writer.write(
                     "\n[HASH TABLE #2]\n" +
                     "Size: " + Hash2.getSize() +
                     "\nLoad Factor: " + Hash2.getLoadFactor());
             writer.close();
         } catch (IllegalArgumentException | IOException e) {System.out.println(e.getMessage());}
     }
 }