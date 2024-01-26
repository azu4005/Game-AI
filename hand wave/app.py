

from flask import Flask, render_template, session, request, jsonify
import uuid
import json
import socket

app = Flask(__name__,template_folder="template")
app.secret_key = '123414'  # Replace with your actual secret key

def send_tcp_message(user_id,wave_count,user_team):


    server_ip = '172.21.102.143'  
    server_port = 80  
    message = "ID:"+str(user_id.replace('-',''))+"/Team:"+str(user_team[-1])+"/DisplayName:"+str(user_id[-4:])+";addInitHP:"+str(wave_count)


    try:
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
            sock.connect((server_ip, server_port))
            sock.sendall(message.encode())
            
    except Exception as e:
        print(f"Error sending message: {e}")

def generate_unique_id():
    return str(uuid.uuid4())

@app.route('/')
def home():
    if 'user_id' not in session:
        session['user_id'] = generate_unique_id()
    return render_template('index.html', user_id=session['user_id'])

@app.route('/send_waves', methods=['POST'])
def send_waves():
    data = request.get_json()
    user_id = data.get('userID')
    wave_count = data.get('waveCount')
    user_team=data.get('userTeam')

    # Specify the file path for saving the data
    file_path = 'received_data.json'
    #-------------------------------------------------------------------#
    send_tcp_message(user_id,wave_count,user_team)
    
    #----------------------------------------------------------------------#
    # Append data to the file
    with open(file_path, 'a') as file:
        json.dump({'user_id': user_id, 'wave_count': wave_count,'user_team':user_team}, file)
        file.write('\n')  # Add a newline for readability

    return jsonify({'status': 'success', 'user_id': user_id, 'wave_count': wave_count,'user_team':user_team})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)




