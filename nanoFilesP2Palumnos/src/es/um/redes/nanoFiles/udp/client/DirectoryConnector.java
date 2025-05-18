package es.um.redes.nanoFiles.udp.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.LinkedList;

import es.um.redes.nanoFiles.udp.message.DirMessage;
import es.um.redes.nanoFiles.udp.message.DirMessageOps;
import es.um.redes.nanoFiles.util.FileInfo;

/**
 * Cliente con métodos de consulta y actualización específicos del directorio
 */
public class DirectoryConnector {
	/**
	 * Puerto en el que atienden los servidores de directorio
	 */
	private static final int DIRECTORY_PORT = 6868;
	/**
	 * Tiempo máximo en milisegundos que se esperará a recibir una respuesta por el
	 * socket antes de que se deba lanzar una excepción SocketTimeoutException para
	 * recuperar el control
	 */
	private static final int TIMEOUT = 1000;
	/**
	 * Número de intentos máximos para obtener del directorio una respuesta a una
	 * solicitud enviada. Cada vez que expira el timeout sin recibir respuesta se
	 * cuenta como un intento.
	 */
	private static final int MAX_NUMBER_OF_ATTEMPTS = 5;

	/**
	 * Valor inválido de la clave de sesión, antes de ser obtenida del directorio al
	 * loguearse
	 */
	public static final int INVALID_SESSION_KEY = -1;

	/**
	 * Socket UDP usado para la comunicación con el directorio
	 */
	private DatagramSocket socket;
	/**
	 * Dirección de socket del directorio (IP:puertoUDP)
	 */
	private InetSocketAddress directoryAddress;

	private int sessionKey = INVALID_SESSION_KEY;
	private boolean successfulResponseStatus;
	private String errorDescription;

	public DirectoryConnector(String address) throws IOException {

		/*
		 * TODO: Convertir el nombre de host 'address' a InetAddress y guardar la
		 * dirección de socket (address:DIRECTORY_PORT) del directorio en el atributo
		 * directoryAddress, para poder enviar datagramas a dicho destino.
		 */
		InetAddress serverIp = InetAddress.getByName(address);
		directoryAddress = new InetSocketAddress(serverIp, DIRECTORY_PORT);
		/*
		 * TODO: Crea el socket UDP en cualquier puerto para enviar datagramas al
		 * directorio
		 */
		socket = new DatagramSocket();

	}

	/**
	 * Método para enviar y recibir datagramas al/del directorio
	 * 
	 * @param requestData los datos a enviar al directorio (mensaje de solicitud)
	 * @return los datos recibidos del directorio (mensaje de respuesta)
	 */
	private byte[] sendAndReceiveDatagrams(byte[] requestData) throws IOException {
		byte responseData[] = new byte[DirMessage.PACKET_MAX_SIZE];
		byte response[] = null;
		if (directoryAddress == null) {
			System.err.println("DirectoryConnector.sendAndReceiveDatagrams: UDP server destination address is null!");
			System.err.println(
					"DirectoryConnector.sendAndReceiveDatagrams: make sure constructor initializes field \"directoryAddress\"");
			System.exit(-1);

		}
		if (socket == null) {
			System.err.println("DirectoryConnector.sendAndReceiveDatagrams: UDP socket is null!");
			System.err.println(
					"DirectoryConnector.sendAndReceiveDatagrams: make sure constructor initializes field \"socket\"");
			System.exit(-1);
		}
		/*
		 * TODO: Enviar datos en un datagrama al directorio y recibir una respuesta. El
		 * array devuelto debe contener únicamente los datos recibidos, *NO* el búfer de
		 * recepción al completo.
		 */
		
		DatagramPacket packetToServer = new DatagramPacket(requestData, requestData.length, directoryAddress);
		
		DatagramPacket packetFromServer = new DatagramPacket(responseData, responseData.length);
		int i = 0;
		while (i < MAX_NUMBER_OF_ATTEMPTS) {
			socket.send(packetToServer);
			socket.setSoTimeout(TIMEOUT);
			try {
				socket.receive(packetFromServer);
				String messageFromServer = new String(responseData, 0, packetFromServer.getLength());
				response = messageFromServer.getBytes();
				break;
			} catch (SocketTimeoutException e) {
				i++;
				continue;
			}
		}
		/*
		 * TODO: Una vez el envío y recepción asumiendo un canal confiable (sin
		 * pérdidas) esté terminado y probado, debe implementarse un mecanismo de
		 * retransmisión usando temporizador, en caso de que no se reciba respuesta en
		 * el plazo de TIMEOUT. En caso de salte el timeout, se debe reintentar como
		 * máximo en MAX_NUMBER_OF_ATTEMPTS ocasiones.
		 */
		/*
		 * TODO: Las excepciones que puedan lanzarse al leer/escribir en el socket deben
		 * ser capturadas y tratadas en este método. Si se produce una excepción de
		 * entrada/salida (error del que no es posible recuperarse), se debe informar y
		 * terminar el programa.
		 */
		/*
		 * NOTA: Las excepciones deben tratarse de la más concreta a la más genérica.
		 * SocketTimeoutException es más concreta que IOException.
		 */



		if (response != null && response.length == responseData.length) {
			System.err.println("Your response is as large as the datagram reception buffer!!\n"
					+ "You must extract from the buffer only the bytes that belong to the datagram!");
		}
		return response;
	}

	/**
	 * Método para probar la comunicación con el directorio mediante el envío y
	 * recepción de mensajes sin formatear ("en crudo")
	 * 
	 * @return verdadero si se ha enviado un datagrama y recibido una respuesta
	 * @throws IOException 
	 */
	public boolean testSendAndReceive() throws IOException {
		/*
		 * TODO: Probar el correcto funcionamiento de sendAndReceiveDatagrams. Se debe
		 * enviar un datagrama con la cadena "login" y comprobar que la respuesta
		 * recibida es "loginok". En tal caso, devuelve verdadero, falso si la respuesta
		 * no contiene los datos esperados.
		 */
		boolean success = false;
		String respuesta = new String(sendAndReceiveDatagrams("login".getBytes()), 0, "loginok".length());
		if (respuesta.equals("loginok")) {
			success = true;
		}
		
		return success;
	}

	public InetSocketAddress getDirectoryAddress() {
		return directoryAddress;
	}

	public int getSessionKey() {
		return sessionKey;
	}

	/**
	 * Método para "iniciar sesión" en el directorio, comprobar que está operativo y
	 * obtener la clave de sesión asociada a este usuario.
	 * 
	 * @param nickname El nickname del usuario a registrar
	 * @return La clave de sesión asignada al usuario que acaba de loguearse, o -1
	 *         en caso de error
	 * @throws IOException 
	 */
	public boolean logIntoDirectory(String nickname) throws IOException {
		assert (sessionKey == INVALID_SESSION_KEY);
		boolean success = false;
		// TODO: 1.Crear el mensaje a enviar (objeto DirMessage) con atributos adecuados
		// (operation, etc.) NOTA: Usar como operaciones las constantes definidas en la
		// clase
		// DirMessageOps
		DirMessage dirmessage = new DirMessage(DirMessageOps.OPERATION_LOGIN, nickname);
		// TODO: 2.Convertir el objeto DirMessage a enviar a un string (método toString)
		String string = dirmessage.toString();
		// TODO: 3.Crear un datagrama con los bytes en que se codifica la cadena
		byte[] bytes = string.getBytes();
		// TODO: 4.Enviar datagrama y recibir una respuesta (sendAndReceiveDatagrams).
		byte[] respBytes = sendAndReceiveDatagrams(bytes);
		String stringResp = new String(respBytes);
		// TODO: 5.Convertir respuesta recibida en un objeto DirMessage (método
		DirMessage dirMessage2 = DirMessage.fromString(stringResp);
		// TODO: 6.Extraer datos del objeto DirMessage y procesarlos (p.ej., sessionKey)
		if (dirMessage2.getSessionKey()==-1) {
			System.out.println("login_failed:-1");
		}
		else if(dirMessage2.getOperation().equals(DirMessageOps.OPERATION_LOGINOK)){
			sessionKey = dirMessage2.getSessionKey();
			System.out.println("Operación realizada con éxito, sessionKey: " + sessionKey);
			success =  true;
		}
		
		// TODO: 7.Devolver éxito/fracaso de la operación
		
		//String cadena = "login&" + nickname;
		//String respuesta = new String(sendAndReceiveDatagrams(cadena.getBytes()));
		
		//String[] respuestas = respuesta.split("&");
		//String loginok = respuestas[0];
		//sessionKey = Integer.parseInt(respuestas[1]);
		//if (loginok.equals("loginok")) {
		//	success = true;
		//	System.out.println("Operación realizada con éxito, sessionKey: "+ sessionKey);
		//}
		return success;
	}

	/**
	 * Método para obtener la lista de "nicknames" registrados en el directorio.
	 * Opcionalmente, la respuesta puede indicar para cada nickname si dicho peer
	 * está sirviendo ficheros en este instante.
	 * 
	 * @return La lista de nombres de usuario registrados, o null si el directorio
	 *         no pudo satisfacer nuestra solicitud
	 * @throws IOException 
	 */
	public String[] getUserList() throws IOException {
		String[] userlist = null;
		String[] servidores = null;
		assert (sessionKey != INVALID_SESSION_KEY);
		// TODO: Ver TODOs en logIntoDirectory y seguir esquema similar
		DirMessage dirmessage = new DirMessage(DirMessageOps.OPERATION_USERLIST, sessionKey);
		String string = dirmessage.toString();
		byte[] bytes = string.getBytes();
		byte[] respBytes = sendAndReceiveDatagrams(bytes);
		String stringResp = new String(respBytes);
		DirMessage dirMessage2 = DirMessage.fromString(stringResp);
		userlist = dirMessage2.getUsuarios();
		servidores = dirMessage2.getServidores();
		System.out.print("La lista de usuarios es: ");
		for (String usuario : userlist) {
			System.out.print(usuario+" ");
		}
		System.out.println();
		System.out.print("La lista de servidores es: ");
		for (String servidor : servidores) {
			System.out.print(servidor + " ");
		}
		System.out.println();

		return userlist;
	}

	/**
	 * Método para "cerrar sesión" en el directorio
	 * 
	 * @return Verdadero si el directorio eliminó a este usuario exitosamente
	 * @throws IOException 
	 */
	public boolean logoutFromDirectory() throws IOException {
		assert (sessionKey != INVALID_SESSION_KEY);
		boolean success = false;
		DirMessage dirmessage = new DirMessage(DirMessageOps.OPERATION_LOGOUT, sessionKey);
		String string = dirmessage.toString();
		byte[] bytes = string.getBytes();
		byte[] respBytes = sendAndReceiveDatagrams(bytes);
		String stringResp = new String(respBytes);
		DirMessage dirMessage2 = DirMessage.fromString(stringResp);
		if (dirMessage2.isExito()) {
			sessionKey = INVALID_SESSION_KEY;
			System.out.println("Desconexión realizada con éxito del servidor");
			success =  true;
		}
		else{
			System.out.println("El usuario no esta en el servidor conectado");
		}

		return success;
	}

	/**
	 * Método para dar de alta como servidor de ficheros en el puerto indicado a
	 * este peer.
	 * 
	 * @param serverPort El puerto TCP en el que este peer sirve ficheros a otros
	 * @return Verdadero si el directorio acepta que este peer se convierta en
	 *         servidor.
	 */
	public boolean registerServerPort(int serverPort) throws IOException {
	    assert (sessionKey != INVALID_SESSION_KEY);
	    boolean success = false;
	    DirMessage dirmessage = new DirMessage(DirMessageOps.OPERATION_REGISTER, sessionKey);
	    dirmessage.setPuerto(serverPort);
	    String string = dirmessage.toString();
	    byte[] bytes = string.getBytes();
	    byte[] respBytes = sendAndReceiveDatagrams(bytes);
	    String stringResp = new String(respBytes);
	    DirMessage dirMessage2 = DirMessage.fromString(stringResp);
	    if (dirMessage2.getOperation().equals(DirMessageOps.OPERATION_REGISTER_OK)) {
	        System.out.println("Registro del puerto del servidor realizado con éxito en el directorio");
	        success =  true;
	    }
	    else{
	        System.out.println("No se pudo registrar el puerto del servidor en el directorio");
	    }

	    return success;
	}
	
	public boolean unregisterServerPort(int serverPort) throws IOException {
	    assert (sessionKey != INVALID_SESSION_KEY);
	    boolean success = false;
	    DirMessage dirmessage = new DirMessage(DirMessageOps.OPERATION_UNREGISTER, sessionKey);
	    dirmessage.setPuerto(serverPort);
	    String string = dirmessage.toString();
	    byte[] bytes = string.getBytes();
	    byte[] respBytes = sendAndReceiveDatagrams(bytes);
	    String stringResp = new String(respBytes);
	    DirMessage dirMessage2 = DirMessage.fromString(stringResp);
	    if (dirMessage2.getOperation().equals(DirMessageOps.OPERATION_UNREGISTER_OK)) {
	        System.out.println("Servidor de ficheros dado de baja");
	        success =  true;
	    }
	    else{
	        System.out.println("No se pudo borrar el servidor en el directorio");
	    }

	    return success;
	}
	
	/**
	 * Método para obtener del directorio la dirección de socket (IP:puerto)
	 * asociada a un determinado nickname.
	 * 
	 * @param nick El nickname del servidor de ficheros por el que se pregunta
	 * @return La dirección de socket del servidor en caso de que haya algún
	 *         servidor dado de alta en el directorio con ese nick, o null en caso
	 *         contrario.
	 * @throws IOException 
	 */
	public InetSocketAddress lookupServerAddrByUsername(String nick) throws IOException {
		InetSocketAddress serverAddr = null;
		// TODO: Ver TODOs en logIntoDirectory y seguir esquema similar
		assert (sessionKey != INVALID_SESSION_KEY);
	    DirMessage dirmessage = new DirMessage(DirMessageOps.OPERATION_ADDRESS_REQUEST, sessionKey);
	    dirmessage.setNickname(nick);
	    String string = dirmessage.toString();
	    byte[] bytes = string.getBytes();
	    byte[] respBytes = sendAndReceiveDatagrams(bytes);
	    String stringResp = new String(respBytes);
	    DirMessage dirMessage2 = DirMessage.fromString(stringResp);
	    if (dirMessage2.getOperation().equals(DirMessageOps.OPERATION_ADDRESS_MESSAGE) && dirMessage2.getPuerto()!= -1) {
	    	int puerto = dirMessage2.getPuerto();
	    	String hostname = dirMessage2.getHostname();
	        System.out.println("Puerto obtenido a partir del nickname "+ nick+" es "+puerto);
	        serverAddr = new InetSocketAddress(hostname, puerto);
	    }
	    else{
	        System.out.println("No se ha encontrado ese nombre de usuario como servidor de ficheros");
	    }

		return serverAddr;
	}

	/**
	 * Método para publicar ficheros que este peer servidor de ficheros están
	 * compartiendo.
	 * 
	 * @param files La lista de ficheros que este peer está sirviendo.
	 * @return Verdadero si el directorio tiene registrado a este peer como servidor
	 *         y acepta la lista de ficheros, falso en caso contrario.
	 * @throws IOException 
	 */
	public boolean publishLocalFiles(FileInfo[] files) throws IOException {
		boolean success = false;
		// TODO: Ver TODOs en logIntoDirectory y seguir esquema similar
		assert (sessionKey != INVALID_SESSION_KEY);
	    DirMessage dirmessage = new DirMessage(DirMessageOps.OPERATION_PUBLISH, sessionKey);
	    LinkedList<FileInfo> fileinfos = new LinkedList<FileInfo>();
	    for (FileInfo file: files) {
	    	fileinfos.add(file);
	    }
	    dirmessage.setFicheros(fileinfos);
	    String string = dirmessage.toString();
	    byte[] bytes = string.getBytes();
	    byte[] respBytes = sendAndReceiveDatagrams(bytes);
	    String stringResp = new String(respBytes);
	    DirMessage dirMessage2 = DirMessage.fromString(stringResp);
	    if (dirMessage2.getOperation().equals(DirMessageOps.OPERATION_PUBLISH_OK)) {
	    	success = true;
	    	System.out.println("Se han publicado los ficheros compartidos en el directorio");
	    }
	    else{
	        System.out.println("No se ha encontrado ese nombre de usuario como servidor de ficheros");
	    }
		return success;
	}

	/**
	 * Método para obtener la lista de ficheros que los peers servidores han
	 * publicado al directorio. Para cada fichero se debe obtener un objeto FileInfo
	 * con nombre, tamaño y hash. Opcionalmente, puede incluirse para cada fichero,
	 * su lista de peers servidores que lo están compartiendo.
	 * 
	 * @return Los ficheros publicados al directorio, o null si el directorio no
	 *         pudo satisfacer nuestra solicitud
	 * @throws IOException 
	 */
	public FileInfo[] getFileList() throws IOException {
		FileInfo[] filelist = null;
		// TODO: Ver TODOs en logIntoDirectory y seguir esquema similar
		assert (sessionKey != INVALID_SESSION_KEY);
	    DirMessage dirmessage = new DirMessage(DirMessageOps.OPERATION_FILELIST_REQUEST, sessionKey);
	    String string = dirmessage.toString();
	    byte[] bytes = string.getBytes();
	    byte[] respBytes = sendAndReceiveDatagrams(bytes);
	    String stringResp = new String(respBytes);
	    DirMessage dirMessage2 = DirMessage.fromString(stringResp);
	    if (dirMessage2.getOperation().equals(DirMessageOps.OPERATION_FILELIST)) {
	    	filelist = dirMessage2.getFicheros().toArray(new FileInfo[dirMessage2.getFicheros().size()]);
	    	System.out.println("Se ha recibido la lista de ficheros");
	    }
	    else{
	        System.out.println("La operación no se ha producido correctamente");
	    }
		return filelist;
	}

	/**
	 * Método para obtener la lista de nicknames de los peers servidores que tienen
	 * un fichero identificado por su hash. Opcionalmente, puede aceptar también
	 * buscar por una subcadena del hash, en vez de por el hash completo.
	 * 
	 * @return La lista de nicknames de los servidores que han publicado al
	 *         directorio el fichero indicado. Si no hay ningún servidor, devuelve
	 *         una lista vacía.
	 */
	public String[] getServerNicknamesSharingThisFile(String fileHash) {
		String[] nicklist = null;
		// TODO: Ver TODOs en logIntoDirectory y seguir esquema similar

		return nicklist;
	}

}
