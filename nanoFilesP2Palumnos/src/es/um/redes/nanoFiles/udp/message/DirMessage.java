package es.um.redes.nanoFiles.udp.message;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;

import es.um.redes.nanoFiles.util.FileInfo;

/**
 * Clase que modela los mensajes del protocolo de comunicación entre pares para
 * implementar el explorador de ficheros remoto (servidor de ficheros). Estos
 * mensajes son intercambiados entre las clases DirectoryServer y
 * DirectoryConnector, y se codifican como texto en formato "campo:valor".
 * 
 * @author rtitos
 *
 */
public class DirMessage {
	public static final int PACKET_MAX_SIZE = 65507; // 65535 - 8 (UDP header) - 20 (IP header)

	private static final char DELIMITER = ':'; // Define el delimitador
	private static final char END_LINE = '\n'; // Define el carácter de fin de línea
	private static final char SEPARADOR = '&';
	/**
	 * Nombre del campo que define el tipo de mensaje (primera línea)
	 */
	private static final String FIELDNAME_OPERATION = "operation";
	/*
	 * TODO: Definir de manera simbólica los nombres de todos los campos que pueden
	 * aparecer en los mensajes de este protocolo (formato campo:valor)
	 */
	private static final String FIELDNAME_NICKNAME = "nickname";

	private static final String FIELDNAME_SESSIONKEY = "sessionkey";

	private static final String FIELDNAME_EXITO = "exito";

	private static final String FIELDNAME_USUARIOS = "usuarios";
	
	private static final String FIELDNAME_SERVIDORES = "servidores";

	private static final String FIELDNAME_PUERTO = "puerto";
	
	private static final String FIELDNAME_CLIENT_ADDRESS = "client-address";
	
	private static final String FIELDNAME_FICHERO = "fichero";

	/**
	 * Tipo del mensaje, de entre los tipos definidos en PeerMessageOps.
	 */
	private String operation = DirMessageOps.OPERATION_INVALID;
	/*
	 * TODO: Crear un atributo correspondiente a cada uno de los campos de los
	 * diferentes mensajes de este protocolo.
	 */
	private String nickname;

	private int sessionKey;

	private boolean exito;

	private String usuarios[];
	
	private String servidores[];
	
	private LinkedList<FileInfo> ficheros = new LinkedList<FileInfo>();;

	private int puerto;
	
	private String hostname;

	public DirMessage(String op) {
		operation = op;
	}

	/*
	 * TODO: Crear diferentes constructores adecuados para construir mensajes de
	 * diferentes tipos con sus correspondientes argumentos (campos del mensaje)
	 */
	public DirMessage(String op, String nick) {
		operation = op;
		nickname = nick;
	}

	public DirMessage(String op, int key) {
		operation = op;
		sessionKey = key;
	}

	public int getPuerto() {
		return puerto;
	}

	public void setPuerto(int puerto) {
		this.puerto = puerto;
	}

	public String getOperation() {
		return operation;
	}

	public void setNickname(String nick) {

		nickname = nick;
	}

	public String getNickname() {

		return nickname;
	}
	
	public String[] getServidores() {
		return servidores;
	}

	public void setServidores(String[] servidores) {
		this.servidores = servidores;
	}

	public int getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(int sessionKey) {
		this.sessionKey = sessionKey;
	}

	public boolean isExito() {
		return exito;
	}

	public void setExito(boolean exito) {
		this.exito = exito;
	}

	public String[] getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(String[] usuarios) {
		this.usuarios = usuarios;
	}

	public LinkedList<FileInfo> getFicheros() {
		return new LinkedList<FileInfo>(ficheros);
	}
	
	public void setFicheros(LinkedList<FileInfo> ficheros) {
		this.ficheros = ficheros;
	}

	public void addFichero(FileInfo fichero) {
		this.ficheros.add(fichero);
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	/**
	 * Método que convierte un mensaje codificado como una cadena de caracteres, a
	 * un objeto de la clase PeerMessage, en el cual los atributos correspondientes
	 * han sido establecidos con el valor de los campos del mensaje.
	 * 
	 * @param message El mensaje recibido por el socket, como cadena de caracteres
	 * @return Un objeto PeerMessage que modela el mensaje recibido (tipo, valores,
	 *         etc.)
	 */
	public static DirMessage fromString(String message) {
		/*
		 * TODO: Usar un bucle para parsear el mensaje línea a línea, extrayendo para
		 * cada línea el nombre del campo y el valor, usando el delimitador DELIMITER, y
		 * guardarlo en variables locales.
		 */

		// System.out.println("DirMessage read from socket:");
		// System.out.println(message);
		String[] lines = message.split(END_LINE + "");
		// Local variables to save data during parsing
		DirMessage m = null;

		for (String line : lines) {
			int idx = line.indexOf(DELIMITER); // Posición del delimitador
			String fieldName = line.substring(0, idx).toLowerCase(); // minúsculas
			String value = line.substring(idx + 1).trim();

			switch (fieldName) {
			case FIELDNAME_OPERATION: {
				assert (m == null);
				m = new DirMessage(value);
				break;
			}
			case FIELDNAME_NICKNAME: {
				assert (m != null);
				m.setNickname(value);
				break;
			}
			case FIELDNAME_SESSIONKEY: {
				assert (m != null);
				m.setSessionKey(Integer.parseInt(value));
				break;
			}
			case FIELDNAME_EXITO: {
				assert (m != null);
				m.setExito(Boolean.parseBoolean(value));
				break;
			}
			case FIELDNAME_USUARIOS: {
				assert (m != null);
				m.setUsuarios(value.split("&"));
				break;
			}
			case FIELDNAME_SERVIDORES: {
				assert (m != null);
				m.setServidores(value.split("&"));
				break;
			}
			case FIELDNAME_FICHERO: {
				assert (m != null);
				FileInfo fileinfo = new FileInfo();
				fileinfo.fileName = value.split("&")[0];
				fileinfo.fileHash = value.split("&")[1];
				fileinfo.fileSize = Long.parseLong(value.split("&")[2]);
				
				m.addFichero(fileinfo);
				break;
			}
			case FIELDNAME_PUERTO: {
				assert (m != null);
				m.setPuerto(Integer.parseInt(value));
				break;
			}
			case FIELDNAME_CLIENT_ADDRESS: {
				assert (m != null);
				m.setHostname(value.split(":")[0]);
				int puerto = Integer.parseInt(value.split(":")[1]);
				m.setPuerto(puerto);
				break;
			}

			default:
				System.err.println("PANIC: DirMessage.fromString - message with unknown field name " + fieldName);
				System.err.println("Message was:\n" + message);
				System.exit(-1);
			}
		}

		return m;
	}

	/**
	 * Método que devuelve una cadena de caracteres con la codificación del mensaje
	 * según el formato campo:valor, a partir del tipo y los valores almacenados en
	 * los atributos.
	 * 
	 * @return La cadena de caracteres con el mensaje a enviar por el socket.
	 */
	public String toString() {

		StringBuffer sb = new StringBuffer();

		/*
		 * TODO: En función del tipo de mensaje, crear una cadena con el tipo y
		 * concatenar el resto de campos necesarios usando los valores de los atributos
		 * del objeto.
		 */
		switch (operation) {
		case DirMessageOps.OPERATION_LOGIN:
			sb.append(FIELDNAME_OPERATION + DELIMITER + operation + END_LINE); // Construimos el campo
			sb.append(FIELDNAME_NICKNAME + DELIMITER + nickname + END_LINE); // Construimos el campo
			break;
		case DirMessageOps.OPERATION_LOGINOK:
			sb.append(FIELDNAME_OPERATION + DELIMITER + operation + END_LINE); // Construimos el campo
			sb.append(FIELDNAME_SESSIONKEY + DELIMITER + sessionKey + END_LINE); // Construimos el campo
			break;
		case DirMessageOps.OPERATION_LOGOUT:
			sb.append(FIELDNAME_OPERATION + DELIMITER + operation + END_LINE); // Construimos el campo
			sb.append(FIELDNAME_SESSIONKEY + DELIMITER + sessionKey + END_LINE); // Construimos el campo
			break;
		case DirMessageOps.OPERATION_LOGOUTOK:
			sb.append(FIELDNAME_OPERATION + DELIMITER + operation + END_LINE); // Construimos el campo
			sb.append(FIELDNAME_EXITO + DELIMITER + exito + END_LINE); // Construimos el campo
			sb.append(FIELDNAME_SESSIONKEY + DELIMITER + sessionKey + END_LINE); // Construimos el campo
			break;
		case DirMessageOps.OPERATION_USERLIST:
			sb.append(FIELDNAME_OPERATION + DELIMITER + operation + END_LINE); // Construimos el campo
			sb.append(FIELDNAME_SESSIONKEY + DELIMITER + sessionKey + END_LINE); // Construimos el campo
			break;
		case DirMessageOps.OPERATION_USERLISTOK:
			sb.append(FIELDNAME_OPERATION + DELIMITER + operation + END_LINE); // Construimos el campo
			sb.append(FIELDNAME_USUARIOS + DELIMITER); // Construimos el campo
			for (String usuario : usuarios) {
				sb.append(usuario + SEPARADOR);
			}
			sb.append(END_LINE);
			sb.append(FIELDNAME_SERVIDORES + DELIMITER); // Construimos el campo
			for (String servidor : servidores) {
				sb.append(servidor + SEPARADOR);
			}
			sb.append(END_LINE);
			sb.append(FIELDNAME_SESSIONKEY + DELIMITER + sessionKey + END_LINE); // Construimos el campo

			break;
		case DirMessageOps.OPERATION_REGISTER:
			sb.append(FIELDNAME_OPERATION + DELIMITER + operation + END_LINE); // Construimos el campo
			sb.append(FIELDNAME_SESSIONKEY + DELIMITER + sessionKey + END_LINE); // Construimos el campo
			sb.append(FIELDNAME_PUERTO + DELIMITER + puerto +END_LINE); // Construimos el campo
			break;
		case DirMessageOps.OPERATION_REGISTER_OK:
			sb.append(FIELDNAME_OPERATION + DELIMITER + operation + END_LINE); // Construimos el campo
			break;
		case DirMessageOps.OPERATION_UNREGISTER:
			sb.append(FIELDNAME_OPERATION + DELIMITER + operation + END_LINE); // Construimos el campo
			sb.append(FIELDNAME_SESSIONKEY + DELIMITER + sessionKey + END_LINE); // Construimos el campo
			sb.append(FIELDNAME_PUERTO + DELIMITER + puerto + END_LINE); // Construimos el campo
			break;
		case DirMessageOps.OPERATION_UNREGISTER_OK:
			sb.append(FIELDNAME_OPERATION + DELIMITER + operation + END_LINE); // Construimos el campo
			break;
		case DirMessageOps.OPERATION_ADDRESS_REQUEST:
			sb.append(FIELDNAME_OPERATION + DELIMITER + operation + END_LINE); // Construimos el campo
			sb.append(FIELDNAME_NICKNAME + DELIMITER + nickname + END_LINE); // Construimos el campo
			sb.append(FIELDNAME_SESSIONKEY + DELIMITER + sessionKey + END_LINE); // Construimos el campo
			break;
		case DirMessageOps.OPERATION_ADDRESS_MESSAGE:
			sb.append(FIELDNAME_OPERATION + DELIMITER + operation + END_LINE); // Construimos el campo
			sb.append(FIELDNAME_SESSIONKEY + DELIMITER + sessionKey + END_LINE); // Construimos el campo
			sb.append(FIELDNAME_CLIENT_ADDRESS + DELIMITER + hostname + ":" + puerto +END_LINE); // Construimos el campo
			break;
		case DirMessageOps.OPERATION_PUBLISH:
			sb.append(FIELDNAME_OPERATION + DELIMITER + operation + END_LINE); // Construimos el campo
			sb.append(FIELDNAME_SESSIONKEY + DELIMITER + sessionKey + END_LINE); // Construimos el campo
			for (FileInfo file : ficheros) {
				sb.append(FIELDNAME_FICHERO + DELIMITER + file.fileName + SEPARADOR + file.fileHash + SEPARADOR + file.fileSize + END_LINE); // Construimos el campo
			}
			break;
		case DirMessageOps.OPERATION_PUBLISH_OK:
			sb.append(FIELDNAME_OPERATION + DELIMITER + operation + END_LINE); // Construimos el campo
			sb.append(FIELDNAME_SESSIONKEY + DELIMITER + sessionKey + END_LINE); // Construimos el campo
			break;
		case DirMessageOps.OPERATION_FILELIST:
			sb.append(FIELDNAME_OPERATION + DELIMITER + operation + END_LINE); // Construimos el campo
			sb.append(FIELDNAME_SESSIONKEY + DELIMITER + sessionKey + END_LINE); // Construimos el campo
			for (FileInfo file : ficheros) {
				sb.append(FIELDNAME_FICHERO + DELIMITER + file.fileName + SEPARADOR + file.fileHash + SEPARADOR + file.fileSize + END_LINE); // Construimos el campo
			}
			break;
		case DirMessageOps.OPERATION_FILELIST_REQUEST:
			sb.append(FIELDNAME_OPERATION + DELIMITER + operation + END_LINE); // Construimos el campo
			sb.append(FIELDNAME_SESSIONKEY + DELIMITER + sessionKey + END_LINE); // Construimos el campo
			break;
		default:
			break;
		}

		sb.append(END_LINE); // Marcamos el final del mensaje
		return sb.toString();
	}
}
