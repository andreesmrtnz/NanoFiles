package es.um.redes.nanoFiles.tcp.server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;

import es.um.redes.nanoFiles.application.NanoFiles;
import es.um.redes.nanoFiles.tcp.message.PeerMessage;
import es.um.redes.nanoFiles.tcp.message.PeerMessageOps;
import es.um.redes.nanoFiles.util.FileDigest;
import es.um.redes.nanoFiles.util.FileInfo;

public class NFServerComm {

	public static void serveFilesToClient(Socket socket) throws IOException {
		/*
		 * TODO: Crear dis/dos a partir del socket
		 */
		DataInputStream dis = new DataInputStream(socket.getInputStream());
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		/*
		 * TODO: Mientras el cliente esté conectado, leer mensajes de socket,
		 * convertirlo a un objeto PeerMessage y luego actuar en función del tipo de
		 * mensaje recibido, enviando los correspondientes mensajes de respuesta.
		 */
		
		while(socket.isConnected()) {
			PeerMessage message = PeerMessage.readMessageFromInputStream(dis);
			String hash = new String(message.getFilehash());
			FileInfo[] coincidencias = FileInfo.lookupHashSubstring(NanoFiles.db.getFiles(), hash);
			FileInfo.printToSysout(coincidencias);
			System.out.println(coincidencias.length);
			switch (coincidencias.length) {
			case 0: {
				System.out.println("No se ha encontrado ninguna coincidencia");
				PeerMessage respuesta = new PeerMessage(PeerMessageOps.OPCODE_FILE_NOT_FOUND);
				respuesta.writeMessageToOutputStream(dos);
				break;
			}
			case 1:
				FileInfo match = coincidencias[0];
				String filepath = NanoFiles.db.lookupFilePath(match.fileHash);
				
				
				if(filepath != null) {
					System.out.println("Encontrado");
					
					File f = new File(filepath);
					DataInputStream dis2 = new DataInputStream(new FileInputStream(f));
					
					int filelength = (int) f.length();
					byte data[] = new byte[(int) filelength];
					dis2.readFully(data);
					dis2.close();
					
					PeerMessage respuesta = new PeerMessage(PeerMessageOps.OPCODE_FILEMESSAGE);
					respuesta.setData(data);
					respuesta.writeMessageToOutputStream(dos);
					break;
				}
			default:
				System.out.println("Se han encontrado varias coincidencias de la subcadena del hash, por favor especifica un poco más");
				PeerMessage respuesta = new PeerMessage(PeerMessageOps.OPCODE_FILE_NOT_FOUND);
				respuesta.writeMessageToOutputStream(dos);
				break;
			}
		
		}
		/*
		 * TODO: Para servir un fichero, hay que localizarlo a partir de su hash (o
		 * subcadena) en nuestra base de datos de ficheros compartidos. Los ficheros
		 * compartidos se pueden obtener con NanoFiles.db.getFiles(). El método
		 * FileInfo.lookupHashSubstring es útil para buscar coincidencias de una
		 * subcadena del hash. El método NanoFiles.db.lookupFilePath(targethash)
		 * devuelve la ruta al fichero a partir de su hash completo.
		*/
		socket.close();


	}




}
