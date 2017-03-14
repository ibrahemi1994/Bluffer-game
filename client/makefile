# All Targets
all: Client

# Tool invocations
Client: bin/Client.o bin/connectionHandler.o
	@echo 'Building target: Client'
	@echo 'Invoking: C++ Linker'
	g++ -o bin/Client bin/Client.o bin/connectionHandler.o -lboost_system -lboost_locale -lboost_thread
	@echo 'Finished building target: Client'
	@echo ' '
# Depends on the source and header files 
bin/Client.o: src/Client.cpp
	g++ -g -Wall -c -Linclude -o bin/Client.o src/Client.cpp
# Depends on the source and header files 
bin/connectionHandler.o: src/connectionHandler.cpp
	g++ -g -Wall -c -Linclude -o bin/connectionHandler.o src/connectionHandler.cpp
#Clean the build directory
clean: 
	rm -f bin/*
