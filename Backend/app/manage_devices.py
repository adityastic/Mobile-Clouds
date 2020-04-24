"""This module will serve the api request."""
from config import client
from app import app
from flask import request, jsonify

# Select the database
db = client.MobileClouds
# Select the collection
collection = db['Users']


@app.route("/api/v1/device", methods=['POST'])
def refresh_device_info():
    """
    Function to create new users.
    """
    try:
        req_json = request.get_json()
        # Create new users
        try:
            body = request.get_json()
        except Exception as e:
            print(e)
            return "", 400
        collection.delete_many({'user': req_json['user']})
        record_created = collection.update({"user": req_json['user']}, body, upsert=True)

        # Prepare the response
        if isinstance(record_created, list):
            # Return list of Id of the newly created item
            return jsonify([str(v) for v in record_created]), 201
        else:
            # Return Id of the newly created item
            return jsonify(str(record_created)), 201
    except Exception as e:
        print(e)
        return "", 500
