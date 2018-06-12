restartcode="1337"
exitcode="$restartcode"
while [ $exitcode = $restartcode ]; do
	rm run-noukkisBot.jar
	cp noukkisBot.jar run-noukkisBot.jar
	java -cp noukkisBot.jar:lib/* noukkisBot.Main
	exitcode=$?
done
