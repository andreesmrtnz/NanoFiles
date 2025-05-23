package es.um.redes.nanoFiles.tcp.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import javax.swing.filechooser.FileNameExtensionFilter;

import es.um.redes.nanoFiles.util.FileInfo;

public class PeerMessage {




	private byte opcode;
	private int tamanoHash;
	private byte[] filehash;
	
	private byte[] data;

	/*
	 * TODO: Añadir atributos y crear otros constructores específicos para crear
	 * mensajes con otros campos (tipos de datos)
	 * 
	 */




	public PeerMessage() {
		opcode = PeerMessageOps.OPCODE_INVALID_CODE;
	}

	public PeerMessage(byte op) {
		opcode = op;
	}

	/*
	 * TODO: Crear métodos getter y setter para obtener valores de nuevos atributos,
	 * comprobando previamente que dichos atributos han sido establecidos por el
	 * constructor (sanity checks)
	 */
	public byte getOpcode() {
		return opcode;
	}

	public void setOpcode(byte opcode) {
		this.opcode = opcode;
	}

	

	public byte[] getFilehash() {
		return filehash;
	}

	public void setFilehash(byte[] filehash) {
		this.filehash = filehash;
	}


	public int getTamanoHash() {
		return tamanoHash;
	}


	public void setTamanoHash(int tamanoHash) {
		this.tamanoHash = tamanoHash;
	}
	


	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	/**
	 * Método de clase para parsear los campos de un mensaje y construir el objeto
	 * DirMessage que contiene los datos del mensaje recibido
	 * 
	 * @param data El array de bytes recibido
	 * @return Un objeto de esta clase cuyos atributos contienen los datos del
	 *         mensaje recibido.
	 * @throws IOException
	 */
	public static PeerMessage readMessageFromInputStream(DataInputStream dis) throws IOException {
		/*
		 * TODO: En función del tipo de mensaje, leer del socket a través del "dis" el
		 * resto de campos para ir extrayendo con los valores y establecer los atributos
		 * del un objeto DirMessage que contendrá toda la información del mensaje, y que
		 * será devuelto como resultado. NOTA: Usar dis.readFully para leer un array de
		 * bytes, dis.readInt para leer un entero, etc.
		 */
		PeerMessage message = new PeerMessage();
		byte opcode = dis.readByte();
		message.setOpcode(opcode);
		switch (opcode) {
		
		case PeerMessageOps.OPCODE_DOWNLOADFROM:{
			int tamanoHash = dis.readInt();
			byte[] hash = new byte[tamanoHash];
			dis.readFully(hash);
			message.setFilehash(hash);
			break;
		}
		case PeerMessageOps.OPCODE_FILEMESSAGE:{
			int tamanoFile = dis.readInt();
			byte[] datos = new byte[tamanoFile];
			dis.readFully(datos);
			message.setData(datos);
			break;
		}
		case PeerMessageOps.OPCODE_FILE_NOT_FOUND:{
			break;
		}
		default:
			System.err.println("PeerMessage.readMessageFromInputStream doesn't know how to parse this message opcode: "
					+ PeerMessageOps.opcodeToOperation(opcode));
			System.exit(-1);
		}
		return message;
	}

	public void writeMessageToOutputStream(DataOutputStream dos) throws IOException {
		/*
		 * TODO: Escribir los bytes en los que se codifica el mensaje en el socket a
		 * través del "dos", teniendo en cuenta opcode del mensaje del que se trata y
		 * los campos relevantes en cada caso. NOTA: Usar dos.write para leer un array
		 * de bytes, dos.writeInt para escribir un entero, etc.
		 */

		dos.writeByte(opcode);
		
		switch (opcode) {
			
		case PeerMessageOps.OPCODE_DOWNLOADFROM:{
			dos.writeInt(filehash.length);
			dos.write(filehash);
			break;
		}
		
		case PeerMessageOps.OPCODE_FILEMESSAGE:{
			dos.writeInt(data.length);
			dos.write(data);
			break;
		}
		
		case PeerMessageOps.OPCODE_FILE_NOT_FOUND:{
			break;
		}
		
		default:
			System.err.println("PeerMessage.writeMessageToOutputStream found unexpected message opcode " + opcode + "("
					+ PeerMessageOps.opcodeToOperation(opcode) + ")");
		}
	}





}
