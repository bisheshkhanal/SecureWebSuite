# SecureWebSuite

## Compile everything
javac *.java

## Run the HTTP server
java HttpServer

Make sure there is a test/index.html file to serve.

## Run the HTTP client
java HttpClient

This connects to the server, performs a handshake, and requests index.html.

## Run the UDP file receiver
java StopAndWaitServer

This waits for a file to be sent over UDP.

## Run the UDP file sender
java StopAndWaitClient

Make sure send_this.txt exists in the same folder. It will be sent to the receiver and saved as received_file.txt.
