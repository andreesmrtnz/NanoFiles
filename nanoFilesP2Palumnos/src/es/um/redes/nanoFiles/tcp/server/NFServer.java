package es.um.redes.nanoFiles.tcp.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Servidor que se ejecuta en un hilo propio. Creará objetos
 * {@link NFServerThread} cada vez que se conecte un cliente.
 */
public class NFServer implements Runnable {

	private ServerSocket serverSocket = null;
	private boolean stopServer = false;
	/*
	private static final int START_PORT = 10000;
    private static final int END_PORT = 10100; // Define el rango de puertos que quieres probar
	*/
	private static final int SERVERSOCKET_ACCEPT_TIMEOUT_MILISECS = 1000;
	
	private int puerto;

	public NFServer() throws IOException {
		/*
		 * TODO: Crear un socket servidor y ligarlo a cualquier puerto disponible
		 */
		/* CODIGO PREVIO A LA MEJORA DEL PUERTO EFIMERO
		serverSocket = new ServerSocket();
		for (int port = START_PORT; port <= END_PORT; port++) {
            try {
                InetSocketAddress addr = new InetSocketAddress(port);
                serverSocket = new ServerSocket();
                //this.serverSocket.setSoTimeout(SERVERSOCKET_ACCEPT_TIMEOUT_MILISECS);
                serverSocket.bind(addr);
                System.out.println("Servidor en segundo plano iniciado en el puerto: " + port);
                this.puerto = port;
                break; // Si el servidor se inicia correctamente, sal del bucle
            } catch (IOException e) {
                System.out.println("Puerto " + port + " no está disponible. Probando el siguiente puerto...");
            }
        }
		*/
		//MEJORA 6. PUERTO EFIMERO
		serverSocket = new ServerSocket(0); // 0 para que se enlace a cualquier puerto efímero disponible
        this.puerto = serverSocket.getLocalPort();
        System.out.println("Servidor en segundo plano iniciado en el puerto: " + puerto);
		
		

	}

	/**
	 * Método que crea un socket servidor y ejecuta el hilo principal del servidor,
	 * esperando conexiones de clientes.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		/*
		 * TODO: Usar el socket servidor para esperar conexiones de otros peers que
		 * soliciten descargar ficheros
		 */
		while (true) {
			try {
				Socket socket = this.serverSocket.accept();
				NFServerThread hilo = new NFServerThread(socket);
				hilo.start();
			} catch (SocketException e) {
				System.out.println("* Se ha interrumpido la conexion del servidor");
				break;
			} catch (IOException e) {
			}

		}
		/*
		 * TODO: Al establecerse la conexión con un peer, la comunicación con dicho
		 * cliente se hace en el método NFServerComm.serveFilesToClient(socket), al cual
		 * hay que pasarle el socket devuelto por accept
		 */
		
		/*
		 * TODO: (Opcional) Crear un hilo nuevo de la clase NFServerThread, que llevará
		 * a cabo la comunicación con el cliente que se acaba de conectar, mientras este
		 * hilo vuelve a quedar a la escucha de conexiones de nuevos clientes (para
		 * soportar múltiples clientes). Si este hilo es el que se encarga de atender al
		 * cliente conectado, no podremos tener más de un cliente conectado a este
		 * servidor.
		 */



	}
	/**
	 * TODO: Añadir métodos a esta clase para: 1) Arrancar el servidor en un hilo
	 * nuevo que se ejecutará en segundo plano 2) Detener el servidor (stopserver)
	 * 3) Obtener el puerto de escucha del servidor etc.
	 */

	public void startServer() {
		new Thread(this).start();
	}
	
	public void stopServer() {
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			System.out.println("* Ha ocurrido un error al intentar cerrar el servidor");
		}
	}
	
	public int getPuerto() {
		return puerto;
	}

	




}
