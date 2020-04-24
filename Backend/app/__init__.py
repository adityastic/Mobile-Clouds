"""This is init module."""

from flask import Flask
from flask_cors import CORS


# Place where app is defined
app = Flask(__name__)
cors = CORS(app)
app.config['CORS_HEADERS'] = 'Content-Type'

from app import usersData
from app import manage_devices
from app import manage_jobs
from app import messaging
from app import frontend_api
