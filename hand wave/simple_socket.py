import socket
import threading
import time

def send_tcp_message():
    server_ip = '172.20.100.114'  
    server_port = 5000  
    message = "ID:1234/Team:1/DisplayName:asd;addInitHP:20"


    try:
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
            sock.connect((server_ip, server_port))
            sock.sendall(message.encode())
            
    except Exception as e:
        print(f"Error sending message: {e}")

def send_message_repeatedly(interval):
    while True:
        send_tcp_message()
        time.sleep(interval)


if __name__ == "__main__":
    interval = 5  
    threading.Thread(target=send_message_repeatedly, args=(interval,)).start()
