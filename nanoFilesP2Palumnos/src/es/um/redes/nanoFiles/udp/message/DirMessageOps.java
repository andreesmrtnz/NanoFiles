package es.um.redes.nanoFiles.udp.message;

public class DirMessageOps {

	/*
	 * TODO: Añadir aquí todas las constantes que definen los diferentes tipos de
	 * mensajes del protocolo de comunicación con el directorio.
	 */
	public static final String OPERATION_INVALID = "invalid_operation";
	public static final String OPERATION_LOGIN = "login";
	public static final String OPERATION_LOGINOK = "loginok";
	public static final String OPERATION_LOGOUT = "logout";
	public static final String OPERATION_LOGOUTOK = "logoutok";
	public static final String OPERATION_USERLIST = "userlist";
	public static final String OPERATION_USERLISTOK = "userlistok";
	public static final String OPERATION_REGISTER = "register";
	public static final String OPERATION_REGISTER_OK = "register_ok";
	public static final String OPERATION_UNREGISTER = "unregister";
	public static final String OPERATION_UNREGISTER_OK = "unregister_ok";
	public static final String OPERATION_ADDRESS_REQUEST = "address_request";
	public static final String OPERATION_ADDRESS_MESSAGE = "address_message";
	public static final String OPERATION_PUBLISH = "publish";
	public static final String OPERATION_PUBLISH_OK = "publish_ok";
	public static final String OPERATION_FILELIST_REQUEST = "filelist_request";
	public static final String OPERATION_FILELIST = "filelist";


}
