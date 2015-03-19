package com.solidleon.pluto.main; 

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class SimpleHTTPServer {

	public static void main(String[] args) {

		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				try (ServerSocket server = new ServerSocket(2345)) {
					System.out.println("SERVER BOUND TO 2345");
					while (true) {
						try (Socket socket = server.accept()) {
							
							System.out.println("READ REQUEST");
							BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
							String line = in.readLine();
							System.out.println(line);
						
							String requestFile = line.substring("GET ".length(), line.length() - " HTTP/1.1".length());
							
							System.out.println("REQUEST FILE:  '" + requestFile + "'");
							try (BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream())) {
								File file = new File("C:\\DEPLOYMENT\\public\\" + requestFile);
								if (file.exists()) {
									out.write(	("HTTP/1.1 200 OK \r\n"+
												"Content-Type: text/plain\r\n"+
												"Content-Length: "+file.length()+"\r\n"+
												"Connection: close\r\n\r\n")
											.getBytes());
									Files.copy(file.toPath(), out);
								} else {
									String msg = "File not found";
									out.write(	("HTTP/1.1 404 Not Found \r\n"+
												"Content-Type: text/plain\r\n"+
												"Content-Length: "+msg.length()+"\r\n"+
												"Connection: close\r\n\r\n")
											.getBytes());
									out.write(msg.getBytes());
								}
							}
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		th.start();
	}
}
