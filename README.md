# NanoFiles

## Descripción
NanoFiles es un sistema de intercambio de archivos peer-to-peer desarrollado en Java, diseñado para facilitar la comunicación entre clientes y un servidor de directorio mediante protocolos UDP y TCP. Este proyecto, creado en 2024 por **Andrés Martínez Lorca**, permite la autenticación de usuarios, la publicación de metadatos de archivos y la transferencia de archivos entre pares de manera eficiente y segura.

## Características
- **Autenticación de Usuarios**: Los clientes pueden iniciar sesión (`login`) y cerrar sesión (`logout`) con una clave de sesión para garantizar una comunicación segura.
- **Comunicación con el Directorio (UDP)**: Los clientes interactúan con el servidor de directorio para:
  - Registrarse o darse de baja como servidores de archivos (`register`, `unregister`).
  - Publicar metadatos de archivos (`publish`).
  - Solicitar listas de usuarios conectados (`userlist`) y archivos compartidos (`filelist`).
  - Obtener direcciones de pares por nickname (`address_request`).
- **Transferencia de Archivos (TCP)**: Protocolo basado en mensajes binarios para descargas, incluyendo:
  - Solicitud de archivos por hash (`DownloadFromRequest`).
  - Recepción de archivos (`FileMessage`) o mensajes de error (`FileNotFound`).
- **Autómatas de Protocolo**: Restricciones que aseguran, por ejemplo, que un cliente debe iniciar sesión antes de acceder a listas o descargar archivos.
- **Mejoras Implementadas**:
  - Selección dinámica de puertos para servidores de archivos.
  - Modo de servidor en segundo plano (`bgserve`) con soporte multi-hilo.
  - Descarga de archivos por nickname.
  - Uso de puertos efímeros para mayor flexibilidad.
  - Ampliación de `userlist` con información de servidores de archivos.

## Estructura del Proyecto
- **Protocolos**:
  - **UDP**: Mensajes textuales en formato "campo:valor" para la comunicación con el directorio.
  - **TCP**: Mensajes binarios multi-formato para transferencias de archivos.
- **Clases Principales**:
  - `NFServer`: Gestiona las operaciones del servidor de archivos, incluyendo el modo en segundo plano.
  - `NFServerThread`: Administra conexiones multi-hilo para servidores de archivos.
- **Documentación**: Consulta `Documentacion_Nanofiles.pdf` para especificaciones detalladas de protocolos, formatos de mensajes, autómatas y ejemplos de intercambio de mensajes.

## Instrucciones de Instalación
1. **Requisitos Previos**:
   - Java Development Kit (JDK) 8 o superior.
   - Maven (opcional, si se utiliza una estructura de proyecto basada en Maven).
2. **Clonar el Repositorio**:
   ```bash
   git clone https://github.com/andreesmrtnz/NanoFiles.git
   cd nanofiles
   ```
3. **Compilar y Ejecutar**:
   - Compila los archivos fuente de Java:
     ```bash
     javac src/*.java
     ```
   - Inicia el servidor de directorio:
     ```bash
     java src/DirectoryServer
     ```
   - Inicia un cliente:
     ```bash
     java src/NanoFilesClient
     ```
4. **Configuración**:
   - Configura la IP y el puerto del servidor de directorio en el cliente.
   - Coloca los archivos compartidos en la carpeta designada del cliente.

## Ejemplo de Uso
1. Inicia el servidor de directorio.
2. Lanza un cliente e inicia sesión:
   ```
   operation: login
   nickname: alicia
   ```
3. Publica archivos:
   ```
   operation: publish
   sessionkey: 1234
   fichero: lexico&tamano&hash
   ```
4. Solicita una lista de archivos:
   ```
   operation: filelist_request
   sessionkey: 1234
   ```
5. Descarga un archivo desde otro par usando su hash o nickname.

## Puntuación del Proyecto
El proyecto implementa las siguientes funcionalidades, sumando un máximo de 10 puntos:
- Funcionalidad básica: 5 puntos
- Puerto variable para `fgserve`: 0.5 puntos
- Descarga por nickname: 1 punto
- `bgserve` secuencial: 1 punto
- `bgserve` multi-hilo: 1 punto
- Comando `stopserver`: 0.5 puntos
- Puerto efímero para `bgserve`: 0.5 puntos
- `userlist` ampliado con servidores: 0.5 puntos
- Comandos `publish` y `filelist`: 0.5 puntos

## Licencia
Este proyecto está licenciado bajo la Licencia MIT. Consulta el archivo `LICENSE` para más detalles.

## Agradecimientos
Desarrollado por **Andrés Martínez Lorca** en 2024 como parte de un proyecto universitario para explorar el diseño de protocolos de red y sistemas peer-to-peer. ¡Gracias por tu interés en NanoFiles!