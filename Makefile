

all: compile
	echo -e '[INFO] Done!'

clean:
	echo -e '[INFO] Cleaning Up..'
	-rm -rf ./src/*/*/*/*.class

compile:
	echo -e '[INFO] Compiling the Source..'
	javac ./src/*/*/*/*.java

