"""This module will serve the jobs api request."""
from config import client
from app import messaging
from app import app
from flask import request
from math import sin, cos, sqrt, atan2, radians

# Select the database
db = client.MobileClouds
# Select the collection
submitted_collection = db['Submitted-Jobs']
saved_collection = db['Saved-Jobs']
user_collection = db['Users']


@app.route("/api/v1/getSavedJobs", methods=['POST'])
def get_saved_jobs():
    """
    Function to get saved jobs.
    """
    try:
        req = request.get_json()

        job_list = {}

        for i in saved_collection.find({'saved_by': req}):
            if i['owned_by'] in job_list:
                job_list[i['owned_by']] += 1
            else:
                job_list[i['owned_by']] = 1

        result = {
            'allSavedJobs': [{'user': key, 'total_jobs': value} for (key, value) in
                             job_list.items()]}
        print(result)
        return result, 201
    except Exception as e:
        print(e)
        return "", 500


@app.route("/api/v1/getMyJobs", methods=['POST'])
def get_my_jobs():
    """
    Function to get jobs.
    """
    try:
        req = request.get_json()

        job_list = []

        for i in submitted_collection.find({'user': req}):
            i['saved_at_ip'] = []
            for j in i['saved_at']:
                item = user_collection.find_one(
                    {'user': saved_collection.find_one({'_id': j})['saved_by']})
                i['saved_at_ip'].append(item['ip_address'])

            del i['_id']
            del i['user']
            del i['saved_at']
            job_list.append(i)

        return {'allJobs': job_list}, 201
    except Exception as e:
        print(e)
        return "", 500


@app.route("/api/v1/addMyJob", methods=['POST'])
def add_my_job():
    """
    Function to add jobs.
    """
    try:
        req_json = request.get_json()

        current_user = user_collection.find_one({'user': req_json['user']})

        req_json['saved_at'] = []

        replication = 3
        for i in sorted(user_collection.find(),
                        key=lambda x: get_distance(x['lat_long']['lat'], x['lat_long']['long'],
                                                   current_user['lat_long']['lat'],
                                                   current_user['lat_long']['long'])):
            if i != current_user and replication > 0:
                try:
                    messaging.send_message(i['device_id'], 'Saving File',
                                           'Saving file from ' + req_json['user'], req_json['size'])
                    req_json['saved_at'].append(saved_collection.insert({
                        'file': req_json['file'],
                        'saved_by': i['user'],
                        'owned_by': req_json['user']
                    }))
                    replication -= 1
                except Exception as e:
                    print("Lost Device")

        submitted_collection.insert(req_json)

        return "!", 201
    except Exception as e:
        print(e)
        return "", 500


def get_distance(l1, lo1, l2, lo2):
    lat1 = radians(float(l1))
    lon1 = radians(float(lo1))
    lat2 = radians(float(l2))
    lon2 = radians(float(lo2))

    R = 6373.0

    dlon = lon2 - lon1
    dlat = lat2 - lat1

    a = sin(dlat / 2) ** 2 + cos(lat1) * cos(lat2) * sin(dlon / 2) ** 2
    c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return R * c
