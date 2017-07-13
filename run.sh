#!/usr/bin/env bash
cd /home/application/
./wait-for-it.sh "event-store:2113" -- echo "event-store 2113 is up"
curl --request POST  --url  'http://localhost:2113/projections/continuous?name=gather-myaggregateroot%26type=js%26enabled=true%26emit=true%26trackemittedstreams=true' --header 'authorization: Basic YWRtaW46Y2hhbmdlaXQ=' --data-binary "@resources/gather-aggregate.js"
curl --request PUT  --url  'http://localhost:2113/subscriptions/MyAggregateRoot/MyAggregateRootSubscriptionGroup' --header 'authorization: Basic YWRtaW46Y2hhbmdlaXQ=' --header 'content-type: application/json' --data-binary "@resources/my-aggregate-subscription-group.json"
curl --request POST  --url 'http://localhost:2113/projection/$by_category/command/enable' --header 'authorization: Basic YWRtaW46Y2hhbmdlaXQ=' --data '{}'