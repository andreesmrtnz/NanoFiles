package es.um.redes.nanoFiles.udp.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

import es.um.redes.nanoFiles.application.NanoFiles;
import es.um.redes.nanoFiles.udp.message.DirMessage;
import es.um.redes.nanoFiles.udp.message.DirMessageOps;
import es.um.redes.nanoFiles.util.FileInfo;

public class NFDirectoryServer {
	/**
	 * Número de puerto UDP en el que escucha el directorio
	 */
	public static final int DIRECTORY_PORT = 6868;

	/**
	 * Socket de comunicación UDP con el cliente UDP (DirectoryConnector)
	 */
	private DatagramSocket socket = null;
	/**
	 * Estructura para guardar los nicks de usuarios registrados, y clave de sesión
	 * 
	 */
	private HashMap<String, Integer> nicks;
	/**
	 * Estructura para guardar las claves de sesión y sus nicks de usuario asociados
	 * 
	 */
	private HashMap<Integer, String> sessionKeys;
	/*
	 * TODO: Añadir aquí como atributos las estructuras de datos que sean necesarias
	 * para mantener en el directorio cualquier información necesaria para la
	 * funcionalidad del sistema nanoFilesP2P: ficheros publicados, servidores
	 * registrados, etc.
	 */
	private HashMap<InetSocketAddress, String> nicknamesByAddress;
	private HashMap<String, InetSocketAddress> addressByNickname;
	private HashSet<FileInfo> ficheros;

	/**
	 * Generador de claves de sesión aleatorias (sessionKeys)
	 */
	Random random = new Random();
	/**
	 * Probabilidad de descartar un mensaje recibido en el directorio (para simular
	 * enlace no confiable y testear el código de retransmisión)
	 */
	private double messageDiscardProbability;

	private String username;

	public NFDirectoryServer(double corruptionProbability) throws SocketException {
		/*
		 * Guardar la probabilidad de pérdida de datagramas (simular enlace no
		 * confiable)
		 */
		messageDiscardProbability = corruptionProbability;
		/*
		 * TODO: (Boletín UDP) Inicializar el atributo socket: Crear un socket UDP
		 * ligado al puerto especificado por el argumento directoryPort en la máquina
		 * local,
		 */
		socket = new DatagramSocket(DIRECTORY_PORT);
		/*
		 * TODO: (Boletín UDP) Inicializar el resto de atributos de esta clase
		 * (estructuras de datos que mantiene el servidor: nicks, sessionKeys, etc.)
		 */
		nicks = new HashMap<String, Integer>();
		sessionKeys = new HashMap<Integer, String>();
		nicknamesByAddress = new HashMap<InetSocketAddress, String>();
		addressByNickname = new HashMap<String, InetSocketAddress>();
		ficheros = new HashSet<FileInfo>();
		
		if (NanoFiles.testMode) {
			if (socket == null || nicks == null || sessionKeys == null) {
				System.err.println("[testMode] NFDirectoryServer: code not yet fully functional.\n"
						+ "Check that all TODOs in its constructor and 'run' methods have been correctly addressed!");
				System.exit(-1);
			}
		}
	}

	public void run() throws IOException {
		byte[] receptionBuffer = new byte[DirMessage.PACKET_MAX_SIZE];
		InetSocketAddress clientAddr = null;
		int dataLength = -1;
		/*
		 * TODO: (Boletín UDP) Crear un búfer para recibir datagramas y un datagrama
		 * asociado al búfer
		 */
		DatagramPacket packetFromClient = new DatagramPacket(receptionBuffer, receptionBuffer.length);

		System.out.println("Directory starting...");

		while (true) { // Bucle principal del servidor de directorio

			// TODO: (Boletín UDP) Recibimos a través del socket un datagrama
			socket.receive(packetFromClient);

			// TODO: (Boletín UDP) Establecemos dataLength con longitud del datagrama
			// recibido
			dataLength = packetFromClient.getLength();

			// TODO: (Boletín UDP) Establecemos 'clientAddr' con la dirección del cliente,
			// obtenida del
			// datagrama recibido
			clientAddr = (InetSocketAddress) packetFromClient.getSocketAddress();

			if (NanoFiles.testMode) {
				if (receptionBuffer == null || clientAddr == null || dataLength < 0) {
					System.err.println("NFDirectoryServer.run: code not yet fully functional.\n"
							+ "Check that all TODOs have been correctly addressed!");
					System.exit(-1);
				}
			}
			System.out.println("Directory received datagram from " + clientAddr + " of size " + dataLength + " bytes");

			// Analizamos la solicitud y la procesamos
			if (dataLength > 0) {

				/*
				 * TODO: (Boletín UDP) Construir una cadena a partir de los datos recibidos en
				 * el buffer de recepción
				 */
				String messageFromClient = new String(receptionBuffer, 0, packetFromClient.getLength());

				if (NanoFiles.testMode) { // En modo de prueba (mensajes en "crudo", boletín UDP)
					System.out.println("[testMode] Contents interpreted as " + dataLength + "-byte String: \""
							+ messageFromClient + "\"");
					/*
					 * TODO: (Boletín UDP) Comprobar que se ha recibido un datagrama con la cadena
					 * "login" y en ese caso enviar como respuesta un mensaje al cliente con la
					 * cadena "loginok". Si el mensaje recibido no es "login", se informa del error
					 * y no se envía ninguna respuesta.
					 */

				} else { // Servidor funcionando en modo producción (mensajes bien formados)

					// Vemos si el mensaje debe ser ignorado por la probabilidad de descarte
					double rand = Math.random();
					if (rand < messageDiscardProbability) {
						System.err.println("Directory DISCARDED datagram from " + clientAddr);
						continue;
					}
					DirMessage message = DirMessage.fromString(messageFromClient);
					DirMessage mensajealCliente = buildResponseFromRequest(message, clientAddr);

					String respuesta = mensajealCliente.toString();
					byte[] responseData = respuesta.getBytes();
					DatagramPacket packetFromServer = new DatagramPacket(responseData, responseData.length, clientAddr);
					socket.send(packetFromServer);

					/*
					 * TODO: Construir String partir de los datos recibidos en el datagrama. A
					 * continuación, imprimir por pantalla dicha cadena a modo de depuración.
					 * Después, usar la cadena para construir un objeto DirMessage que contenga en
					 * sus atributos los valores del mensaje (fromString).
					 */
					/*
					 * TODO: Llamar a buildResponseFromRequest para construir, a partir del objeto
					 * DirMessage con los valores del mensaje de petición recibido, un nuevo objeto
					 * DirMessage con el mensaje de respuesta a enviar. Los atributos del objeto
					 * DirMessage de respuesta deben haber sido establecidos con los valores
					 * adecuados para los diferentes campos del mensaje (operation, etc.)
					 */
					/*
					 * TODO: Convertir en string el objeto DirMessage con el mensaje de respuesta a
					 * enviar, extraer los bytes en que se codifica el string (getBytes), y
					 * finalmente enviarlos en un datagrama
					 */

				}
			} else {
				System.err.println("Directory ignores EMPTY datagram from " + clientAddr);
			}

		}
	}

	@SuppressWarnings("unlikely-arg-type")
	private DirMessage buildResponseFromRequest(DirMessage msg, InetSocketAddress clientAddr) {
		/*
		 * TODO: Construir un DirMessage con la respuesta en función del tipo de mensaje
		 * recibido, leyendo/modificando según sea necesario los atributos de esta clase
		 * (el "estado" guardado en el directorio: nicks, sessionKeys, servers,
		 * files...)
		 */
		String operation = msg.getOperation();

		DirMessage response = null;
		switch (operation) {
		case DirMessageOps.OPERATION_LOGIN: {
			String username = msg.getNickname();

			int num = random.nextInt(10000);

			if (nicks.containsKey(username)) {
				response = new DirMessage(DirMessageOps.OPERATION_LOGINOK, -1);
			} else {
				nicks.put(username, num);
				sessionKeys.put(num, username);
				response = new DirMessage(DirMessageOps.OPERATION_LOGINOK, num);
			}

			/*
			 * TODO: Comprobamos si tenemos dicho usuario registrado (atributo "nicks"). Si
			 * no está, generamos su sessionKey (número aleatorio entre 0 y 1000) y añadimos
			 * el nick y su sessionKey asociada. NOTA: Puedes usar random.nextInt(10000)
			 * para generar la session key
			 */
			/*
			 * TODO: Construimos un mensaje de respuesta que indique el éxito/fracaso del
			 * login y contenga la sessionKey en caso de éxito, y lo devolvemos como
			 * resultado del método.
			 */
			/*
			 * TODO: Imprimimos por pantalla el resultado de procesar la petición recibida
			 * (éxito o fracaso) con los datos relevantes, a modo de depuración en el
			 * servidor
			 */

			break;
		}
		case DirMessageOps.OPERATION_LOGOUT: {
			int key = msg.getSessionKey();

			response = new DirMessage(DirMessageOps.OPERATION_LOGOUTOK, key);
			response.setExito(true);
			String username = sessionKeys.get(key);
			sessionKeys.remove(key);
			nicks.remove(username);
			break;

		}
		case DirMessageOps.OPERATION_USERLIST: {
			String[] usuarios = (String[]) nicks.keySet().toArray(new String[0]);
			String[] servidores = (String[]) addressByNickname.keySet().toArray(new String[0]);
			response = new DirMessage(DirMessageOps.OPERATION_USERLISTOK, msg.getSessionKey());
			response.setUsuarios(usuarios);
			response.setServidores(servidores);
			break;
		}
		case DirMessageOps.OPERATION_REGISTER: {
		    int key = msg.getSessionKey();
		    int port = msg.getPuerto();
		    InetSocketAddress address = new InetSocketAddress(clientAddr.getAddress(), port);
		    // Obtener el nickname correspondiente a la session key
		    String username = sessionKeys.get(key);
		    
		    // Registra el puerto asociado al usuario
		    if (username != null) {
		        nicknamesByAddress.put(address, username );
		        addressByNickname.put(username, address);
		    }
		    
		    // Construye la respuesta
		    response = new DirMessage(DirMessageOps.OPERATION_REGISTER_OK, key);
		    
		    // Imprime por pantalla el puerto y el nickname asociados
		    if (username != null) {
		        System.out.println("Puerto registrado: " + clientAddr.getHostString() + ", Nickname: " + username);
		        
		    } else {
		        System.out.println("No se pudo registrar el puerto. El puerto no está asociado a ningún usuario.");
		    }
		    
		    break;
		}
		case DirMessageOps.OPERATION_UNREGISTER: {
		    int key = msg.getSessionKey();
		    int puerto = msg.getPuerto();
		    InetSocketAddress address = new InetSocketAddress(clientAddr.getAddress(), puerto);
		    
		    // Obtener el nickname correspondiente a la session key
		    String username = sessionKeys.get(key);
		    
		    // Registra el puerto asociado al usuario
		    if (username != null) {
		        nicknamesByAddress.remove(address);
		        addressByNickname.remove(username);
		    }
		    
		    // Construye la respuesta
		    response = new DirMessage(DirMessageOps.OPERATION_UNREGISTER_OK, key);
		    
		    // Imprime por pantalla el puerto y el nickname asociados
		    if (username != null) {
		        System.out.println("Puerto borrado como servidor: " + puerto + ", Nickname: " + username);
		        
		    } else {
		        System.out.println("No se pudo borrar el puerto. El puerto no está asociado a ningún usuario.");
		    }
		    
		    break;
		}
		case DirMessageOps.OPERATION_ADDRESS_REQUEST: {
		    int key = msg.getSessionKey();
		    String username = msg.getNickname();
		 // Obtener el puerto correspondiente al nickname
		    InetSocketAddress address = addressByNickname.get(username);
		    
		    
		    // Construye la respuesta
		    if (address != null) {
		    	response = new DirMessage(DirMessageOps.OPERATION_ADDRESS_MESSAGE, key);
		    	response.setPuerto(address.getPort());
		    	response.setHostname(address.getHostName());
		    }
		    else {
		    	response = new DirMessage(DirMessageOps.OPERATION_LOGINOK, key); //no se ha encontrado el nickname pedido
		    }
		    
		    break;
		}
		case DirMessageOps.OPERATION_PUBLISH: {
		    int key = msg.getSessionKey();
		    if (ficheros.addAll(msg.getFicheros())) {
		    	System.out.println("Publicados los ficheros correctamente");
		    	response = new DirMessage(DirMessageOps.OPERATION_PUBLISH_OK, key);
		    }
		    else {
		    	System.out.println("Todos los ficheros ya estaban en el directorio");
		    	response = new DirMessage(DirMessageOps.OPERATION_PUBLISH_OK, key);
		    }
		    // Construye la respuesta
		    
		    break;
		}
		case DirMessageOps.OPERATION_FILELIST_REQUEST: {
			LinkedList<FileInfo> filelist = new LinkedList<FileInfo>();
			for (FileInfo file : ficheros) {
				filelist.add(file);
			}
			response = new DirMessage(DirMessageOps.OPERATION_FILELIST, msg.getSessionKey());
			response.setFicheros(filelist);
			break;
		}
		default:
			System.out.println("Unexpected message operation: \"" + operation + "\"");
		}
		return response;

	}
}
