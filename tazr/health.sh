#!/bin/bash

export port=9000
export host=127.0.0.1

while true;
do
  for i in {1..7}
  do
    sleep 1
    $(ping -c http://$host:$port/)
    if [[ $? -ne 0 ]]; then
      gradle --stop && gradle run &
    fi
  done
done
