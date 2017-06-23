#!/usr/bin/env bash
cd /home/application/
./wait-for-it.sh "event-store:2113" -- echo "event-store 2113 is up"
curl --request POST  --url 'http://event-store:2113/projection/$by_category/command/enable' --header 'authorization: Basic YWRtaW46Y2hhbmdlaXQ=' --data '{}'