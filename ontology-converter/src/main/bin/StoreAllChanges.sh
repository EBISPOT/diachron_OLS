#!/bin/sh


while read line; do
	./StoreChange.sh "$line" 2>&1;
done < ChangesArguments.txt
