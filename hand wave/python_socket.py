import socket

def main():
    try:
        # Create a socket object and connect to the server
        client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        server_address = ("127.0.0.1", 80)
        client_socket.connect(server_address)

        # Create a stream to send data
        output_stream = client_socket.makefile('w')

        # Sending data to the server
        output_stream.write("ID:127.0.0.21/Team:1;setInitHP:18\n")
        output_stream.write("ID:127.0.0.1/Team:0;setInitHP:120\n")
        output_stream.flush()

        # Close the socket and stream
        output_stream.close()
        client_socket.close()

    except socket.error as e:
        print("Socket error:", e)

if __name__ == "__main__":
    main()
