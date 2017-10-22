restartcode="15"
exitcode="$restartcode"
while [ $exitcode = $restartcode ]; do
	rm run/noukkisBot.jar
	cp build/libs/noukkisBot.jar run/noukkisBot.jar
	java -jar run/noukkisBot.jar
	exitcode=$?
done
