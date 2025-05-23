package es.um.redes.nanoFiles.logic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.Random;

import es.um.redes.nanoFiles.application.NanoFiles;
import es.um.redes.nanoFiles.tcp.client.NFConnector;
import es.um.redes.nanoFiles.tcp.server.NFServer;
import es.um.redes.nanoFiles.tcp.server.NFServerSimple;
import es.um.redes.nanoFiles.util.FileInfo;

public class NFControllerLogicP2P {
	/*
	 * TODO: Para bgserve, se necesita un atributo NFServer que actuará como
	 * servidor de ficheros en segundo plano de este peer
	 */
	/**
	 * El servidor de ficheros de este peer
	 */
	private NFServer bgFileServer = null;
	/**
	 * El cliente para conectarse a otros peers
	 */
	NFConnector nfConnector;
	/**
	 * El controlador que permite interactuar con el directorio
	 */
	private NFControllerLogicDir controllerDir;
	
	private int port;

	protected NFControllerLogicP2P() {
	}

	protected NFControllerLogicP2P(NFControllerLogicDir controller) {
		this.controllerDir = controller; // Para gestionar la comunicación con el directorio
	}

	/**
	 * Método para arrancar un servidor de ficheros en primer plano.
	 * 
	 * @throws IOException
	 * 
	 */
	protected void foregroundServeFiles() throws IOException {
		/*
		 * TODO: Crear objeto servidor NFServerSimple y ejecutarlo en primer plano.
		 */
		NFServerSimple server;
		try {
			// TODO: Crear objeto servidor NFServerSimple ligado al puerto especificado
			server = new NFServerSimple();
			this.port = server.getPuerto();
			controllerDir.registerFileServer(port);
			server.run();
			controllerDir.unregisterFileServer(port);
		} catch (IOException e) {
			System.err.println("* Ha habido un error de entrada/salida");
		}
		/*
		 * TODO: Las excepciones que puedan lanzarse deben ser capturadas y tratadas en
		 * este método. Si se produce una excepción de entrada/salida (error del que no
		 * es posible recuperarse), se debe informar sin abortar el programa
		 */

	}

	/**
	 * Método para ejecutar un servidor de ficheros en segundo plano. Debe arrancar
	 * el servidor en un nuevo hilo creado a tal efecto.
	 * 
	 * @return Verdadero si se ha arrancado en un nuevo hilo con el servidor de
	 *         ficheros, y está a la escucha en un puerto, falso en caso contrario.
	 * 
	 */
	protected boolean backgroundServeFiles() {
		/*
		 * TODO: Comprobar que no existe ya un objeto NFServer previamente creado, en
		 * cuyo caso el servidor ya está en marcha. Si no lo está, crear objeto servidor
		 * NFServer y arrancarlo en segundo plano creando un nuevo hilo. Finalmente,
		 * comprobar que el servidor está escuchando en un puerto válido (>0) e imprimir
		 * mensaje informando sobre el puerto, y devolver verdadero.
		 */
		/*
		 * TODO: Las excepciones que puedan lanzarse deben ser capturadas y tratadas en
		 * este método. Si se produce una excepción de entrada/salida (error del que no
		 * es posible recuperarse), se debe informar sin abortar el programa
		 */
		try {
			// Comprueba si ya existe un objeto NFServer previamente creado
			if (bgFileServer == null) {
				// Si no existe, crea un nuevo objeto servidor NFServer
				this.bgFileServer = new NFServer();
				this.port = bgFileServer.getPuerto();
				// Arranca el servidor en un nuevo hilo que se ejecutará en segundo plano
				this.bgFileServer.startServer();
				System.out.println("* Servidor en marcha en segundo plano");
				return true;
			} else {
				System.out.println("* El servidor ya estaba en marcha");
				return false;
			}
		} catch (IOException e) {
			System.err.println("* Ha habido un error de entrada/salida");
			return false;
		}
	}

	/**
	 * Método para descargar un fichero del peer servidor de ficheros
	 * 
	 * @param fserverAddr    La dirección del servidor al que se conectará
	 * @param targetFileHash El hash del fichero a descargar
	 * @param localFileName  El nombre con el que se guardará el fichero descargado
	 * @throws IOException
	 */
	protected boolean downloadFileFromSingleServer(String username, String targetFileHash, String localFileName)
			throws IOException {
		boolean result = false;

		InetSocketAddress fserverAddr = controllerDir.getServerAddress(username);

		if (fserverAddr == null) {
			System.err.println("* Cannot start download - No server address provided");
			return false;
		}
		/*
		 * TODO: Crear un objeto NFConnector para establecer la conexión con el peer
		 * servidor de ficheros, y usarlo para descargar el fichero mediante su método
		 * "downloadFile". Se debe comprobar previamente si ya existe un fichero con el
		 * mismo nombre en esta máquina, en cuyo caso se informa y no se realiza la
		 * descarga. Si todo va bien, imprimir mensaje informando de que se ha
		 * completado la descarga.
		 */
		NFConnector nfConnector = new NFConnector(fserverAddr);
		nfConnector.downloadFile(targetFileHash, new File(localFileName));
		/*
		 * TODO: Las excepciones que puedan lanzarse deben ser capturadas y tratadas en
		 * este método. Si se produce una excepción de entrada/salida (error del que no
		 * es posible recuperarse), se debe informar sin abortar el programa
		 */

		return result;
	}

	/**
	 * Método para descargar un fichero del peer servidor de ficheros
	 * 
	 * @param serverAddressList La lista de direcciones de los servidores a los que
	 *                          se conectará
	 * @param targetFileHash    Hash completo del fichero a descargar
	 * @param localFileName     Nombre con el que se guardará el fichero descargado
	 */
	public boolean downloadFileFromMultipleServers(LinkedList<InetSocketAddress> serverAddressList,
			String targetFileHash, String localFileName) {
		boolean downloaded = false;

		if (serverAddressList == null) {
			System.err.println("* Cannot start download - No list of server addresses provided");
			return false;
		}
		/*
		 * TODO: Crear un objeto NFConnector para establecer la conexión con cada
		 * servidor de ficheros, y usarlo para descargar un trozo (chunk) del fichero
		 * mediante su método "downloadFileChunk". Se debe comprobar previamente si ya
		 * existe un fichero con el mismo nombre en esta máquina, en cuyo caso se
		 * informa y no se realiza la descarga. Si todo va bien, imprimir mensaje
		 * informando de que se ha completado la descarga.
		 */
		/*
		 * TODO: Las excepciones que puedan lanzarse deben ser capturadas y tratadas en
		 * este método. Si se produce una excepción de entrada/salida (error del que no
		 * es posible recuperarse), se debe informar sin abortar el programa
		 */

		return downloaded;
	}

	/**
	 * Método para obtener el puerto de escucha de nuestro servidor de ficheros en
	 * segundo plano
	 * 
	 * @return El puerto en el que escucha el servidor, o 0 en caso de error.
	 */
	public int getServerPort() {
		/*
		 * TODO: Devolver el puerto de escucha de nuestro servidor de ficheros en
		 * segundo plano
		 */

		return this.port;
	}

	/**
	 * Método para detener nuestro servidor de ficheros en segundo plano
	 * 
	 */
	public void stopBackgroundFileServer() {
		/*
		 * TODO: Enviar señal para detener nuestro servidor de ficheros en segundo plano
		 */
		if (this.bgFileServer != null) {
			this.bgFileServer.stopServer();
			System.out.println("*Servidor en segundo plano cerrado");
		}

	}

}
