# Java compiler
JAVAC = javac
JAVA = java

# Source files
CLIENT = UDPClient.java
SERVER = UDPServer.java

# Commands
# Compile both files
all: compile

compile:
	$(JAVAC) $(CLIENT)
	$(JAVAC) $(SERVER)

# Run server
server:
	$(JAVA) UDPServer

# Run client
client:
	$(JAVA) UDPClient