#!/usr/bin/env bash
cd /home/application/
./wait-for-it.sh "event-store:2113" -- echo "event-store 2113 is up"
curl --request POST  --url  'http://event-store:2113/projections/continuous?name=gather-myaggregate%26type=js%26enabled=true%26emit=true%26trackemittedstreams=true' --header 'authorization: Basic YWRtaW46Y2hhbmdlaXQ=' --data-binary "@resources/gather-aggregate.js"
curl --request PUT  --url  'http://event-store:2113/subscriptions/MyAggregate/my-aggregate-subscription-group' --header 'authorization: Basic YWRtaW46Y2hhbmdlaXQ=' --header 'content-type: application/json' --data-binary "@resources/my-aggregate-subscription-group.json"
curl --request POST  --url 'http://event-store:2113/projection/$by_category/command/enable' --header 'authorization: Basic YWRtaW46Y2hhbmdlaXQ=' --data '{}'