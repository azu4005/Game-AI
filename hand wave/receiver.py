from flask import Flask, request

app = Flask(__name__)

@app.route('/send_coordinates', methods=['POST'])
def receive_coordinates():
    data = request.json
    center_x = data.get('centerX')
    center_y = data.get('centerY')

    print(f"Received coordinates: X={center_x}, Y={center_y}")

    return 'Coordinates received', 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
