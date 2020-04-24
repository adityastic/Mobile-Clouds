"""This module will serve the api request."""
import firebase_admin
from config import client
from app import app
from firebase_admin import messaging, credentials
from flask import request

# Select the database
db = client.MobileClouds
# Select the collection
collection = db.Users

cred = credentials.Certificate("keys/mobileclouds-1de1c-firebase-adminsdk-1bs6c-d1fde87c41.json")
firebase_admin.initialize_app(cred)


def send_message(device_id, title, message, size):
    message = messaging.Message(
        data={
            'title': title,
            'message': message,
            'size': str(size)
        },
        token=device_id,
    )

    # Send a message to the device corresponding to the provided
    # registration token.
    return messaging.send(message)


@app.route("/api/v1/sendMessageToToken", methods=['POST'])
def send_message_to_device_id():
    """
    Function to create new users.
    """
    try:
        request_json = request.get_json()

        response = send_message(request_json['device_id'])

        print('Successfully sent message:', response)
        return "", 200
    except Exception as e:
        print(e)
        return "", 500


@app.route("/api/v1/sendMessageToAll", methods=['GET'])
def send_message_to_all():
    """
    Function to create new users.
    """
    try:
        fetched_users = [x for x in collection.find({}, {'_id': 0})]
        for user in fetched_users:
            try:
                response = send_message(user['device_id'])
                print(response)
            except Exception as e:
                print("Device Not Found, User: ", user['device_id'])
                # collection.delete_one({"device_id": user['device_id']})

        return "", 200
    except Exception as e:
        print(e)
        return "", 500
