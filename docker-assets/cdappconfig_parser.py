#!/usr/bin/python
import json
import sys
args = sys.argv[1:]
conf = args.pop()
f = open(conf,)
data = json.load(f)
a = 'data'
for arg in args:
        a += "['" + arg + "']"
try:
        print(eval(a))
except KeyError:
        print("null")
