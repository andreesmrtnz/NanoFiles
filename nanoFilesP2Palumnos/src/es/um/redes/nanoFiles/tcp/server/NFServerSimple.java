package es.um.redes.nanoFiles.tcp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

import es.um.redes.nanoFiles.udp.client.DirectoryConnector;
import es.um.redes.nanoFiles.udp.server.NFDirectoryServer;

public class NFServerSimple {

	private static final int SERVERSOCKET_ACCEPT_TIMEOUT_MILISECS = 1000;
	private static final String STOP_SERVER_COMMAND = "fgstop";
	private ServerSocket serverSocket = null;
	private static final int START_PORT = 10000;
    private static final int END_PORT = 10100; // Define el rango de puertos que quieres probar
	private int puerto;

	public NFServerSimple() throws IOException {
		/*
		 * TODO: Crear una direción de socket a partir del puerto especificado
		 */
		//InetSocketAddress addr = new InetSocketAddress(PORT);
		// codigo sin mejora fgservers
		/*
		 * TODO: Crear un socket servidor y ligarlo a la dirección de socket anterior
		 */
		//serverSocket = new ServerSocket();
		//serverSocket.bind(addr);
		// codigo sin mejora fgservers
		
		/*
1. Ampliar comando fgservers: Ampliar comando fgserve para si el puerto de escucha
predeterminado (10000) no está disponible, el servidor pueda utilizar otro números de puerto que sí
lo estén (10001...). De esta forma, será posible tener varias instancias del programa NanoFiles
ejecutándose en la misma máquina, actuando como servidor.
		 */
		for (int port = START_PORT; port <= END_PORT; port++) {
            try {
                InetSocketAddress addr = new InetSocketAddress(port);
                serverSocket = new ServerSocket();
                this.serverSocket.setSoTimeout(SERVERSOCKET_ACCEPT_TIMEOUT_MILISECS);
                serverSocket.bind(addr);
                System.out.println("Servidor iniciado en el puerto: " + port);
                this.puerto = port;
                break; // Si el servidor se inicia correctamente, sal del bucle
            } catch (IOException e) {
                System.out.println("Puerto " + port + " no está disponible. Probando el siguiente puerto...");
            }
        }
	}

	/**
	 * Método para ejecutar el servidor de ficheros en primer plano. Sólo es capaz
	 * de atender una conexión de un cliente. Una vez se lanza, ya no es posible
	 * interactuar con la aplicación a menos que se implemente la funcionalidad de
	 * detectar el comando STOP_SERVER_COMMAND (opcional)
	 * @throws IOException 
	 * 
	 */
	public void run() throws IOException {
		/*
		 * TODO: Comprobar que el socket servidor está creado y ligado
		 */
		if (this.serverSocket != null && this.serverSocket.isBound()) {
			boolean stopServer = false;
			System.out.println("Enter '" + STOP_SERVER_COMMAND + "' to stop the server");
			while (!stopServer) {
				try {
					Socket socket = this.serverSocket.accept();
					System.out.println("* Se ha establecido una conexion");
					NFServerThread hilo = new NFServerThread(socket);
					hilo.start();
				} catch (SocketTimeoutException e) {
					InputStreamReader fileInputStream = new InputStreamReader(System.in);
					BufferedReader bufferedReader = new BufferedReader(fileInputStream);
					try {
						if (bufferedReader.ready()) {
							String line = bufferedReader.readLine();
							if (line.equals("fgstop"))
								stopServer = true;
						}
					} catch (IOException ex) {
					}
				} catch (IOException e) {
				}
			}
			try {
				this.serverSocket.close();
				System.out.println("NFServerSimple stopped. Returning to the nanoFiles shell...");
			} catch (IOException e) {

			}

		}
		
		/*
		 * TODO: Usar el socket servidor para esperar conexiones de otros peers que
		 * soliciten descargar ficheros
		 */
		/*
		 * TODO: Al establecerse la conexión con un peer, la comunicación con dicho
		 * cliente se hace en el método NFServerComm.serveFilesToClient(socket), al cual
		 * hay que pasarle el socket devuelto por accept
		 */



		
	}

	public int getPuerto() {
		return puerto;
	}


	
	
	
}
