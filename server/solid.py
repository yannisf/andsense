#!/usr/bin/env python3


from flask import Flask
from sense_hat import SenseHat

app = Flask(__name__)

sense = SenseHat()
sense.low_light = True

def inflate(value):
    print("Inflating {}".format(value))
    return int(round(((value * 255) / 9)))

def split_triplet(triplet):
    r = inflate(int(triplet[0]))
    g = inflate(int(triplet[1]))
    b = inflate(int(triplet[2]))
    rgb = (r,g,b)
    print(rgb)
    return (r,g,b)

@app.route("/health")
def health():
    return "OK"

@app.route("/low/<value>")
def low(value):
    if value == "true":
        sense.low_light = True
    else:
        sense.low_light = False
    return "OK"

@app.route("/light/<value>")
def light(value):
    print(value)
    rgb = split_triplet(value)
    sense.clear(rgb[0], rgb[1], rgb[2])
    return "OK"

if __name__ == '__main__':
    app.run(host="0.0.0.0", port=5000, debug=True)

