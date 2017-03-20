BIN = bin
CLASSPATH = src
JFLAGS = -g -d $(BIN)
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) -classpath $(CLASSPATH) $(JFLAGS) $*.java

SOURCE_FILES = \
src/Main.java \
src/TcpClient.java \
src/UdpClient.java	


build: $(SOURCE_FILES:.java=.class)

clean:
	$(RM) $(BIN)/*.class
	$(RM) xiangli_m2.db