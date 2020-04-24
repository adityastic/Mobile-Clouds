"""This module will serve the frontend api request."""
from config import client
from app import app
from flask import jsonify
from flask_cors import cross_origin

# Select the database
db = client.MobileClouds
# Select the collection
user_collection = db['Users']
submitted_collection = db['Submitted-Jobs']


@app.route("/api/v1/getBasicInformation", methods=['GET'])
@cross_origin()
def get_basic_information():
    """
    Function to get frontend information.
    """
    try:
        result = {
            'total_size': 0,
            'people': 0,
            'locations': []
        }

        for i in submitted_collection.find({}):
            result['total_size'] += i['size']

        index = 1
        for i in user_collection.find({}):
            result['people'] += 1
            result['locations'].append(
                [i['user'], float(i['lat_long']['lat']), float(i['lat_long']['long']), index]
            )
            index += 1

        result['total_size'] = humansize(result['total_size'])
        return jsonify(result), 201
    except Exception as e:
        print(e)
        return "", 500


suffixes = ['B', 'KB', 'MB', 'GB', 'TB', 'PB']


def humansize(nbytes):
    i = 0
    while nbytes >= 1024 and i < len(suffixes) - 1:
        nbytes /= 1024.
        i += 1
    f = ('%.2f' % nbytes).rstrip('0').rstrip('.')
    return '%s %s' % (f, suffixes[i])
