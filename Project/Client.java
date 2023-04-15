import javax.crypto.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
public class Client {
private Socket socket = null;
private Scanner input = null;
private DataOutputStream out = null;
private DataInputStream in = null;
public Client(String address, int port) {
try {
// Generate key
KeyGenerator keygenerator = KeyGenerator.getInstance("DES");
SecretKey myDesKey = keygenerator.generateKey();
byte[] key = myDesKey.getEncoded();
// Connect to server
socket = new Socket(address, port);
System.out.println("Connected to the server.");
// Generate ciphers
Cipher ecipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
ecipher.init(Cipher.ENCRYPT_MODE, myDesKey);
Cipher decipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
decipher.init(Cipher.DECRYPT_MODE, myDesKey);
// Write key to file
File keyfile = new File("KeyFile.txt");
OutputStream fileStream = new FileOutputStream(keyfile);
fileStream.write(key);
fileStream.close();
// Initialize input/output streams
input = new Scanner(System.in);
in = new DataInputStream(socket.getInputStream());
out = new DataOutputStream(socket.getOutputStream());
String sentLine = "";
String decLine = "";
// Sending Message
while(!sentLine.equals("Exit") || !decLine.equals("Exit")) {
try {
System.out.print("\nEnter text: ");
// Get line + encrypted line
sentLine = input.nextLine();
byte[] encLine = ecipher.doFinal(sentLine.getBytes());
System.out.println("************************************************************");
System.out.println("Plaintext: " + sentLine);
System.out.println("Key: " + key);
System.out.println("Encrypted: " + new String(encLine));
System.out.println("************************************************************");
// Send to server
out.writeInt(encLine.length);
out.write(encLine);
