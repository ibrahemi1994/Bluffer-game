
#include "../include/Client.h"
#include "../include/connectionHandler.h"
#include <cstdlib>

using namespace boost;
using namespace std;

bool flag = true;

void ListenToServer(ConnectionHandler *connectH) {
	string answer = "";
	while (1) {
		connectH->getLine(answer);
		if (answer == "SYSMSG QUIT ACCEPTED\n") {
				std::cout << "SYSMSG QUIT ACCEPTED\n" << std::endl;
			std::cout << "Exiting...\n" << std::endl;
			  connectH->close();
			flag = false;
			  boost::this_thread::sleep(boost::posix_time::milliseconds(500));  
 			break;

		}
		std::cout << answer << std::endl;
		answer.clear();
		

	}
}


void ListenToKeyBoard(ConnectionHandler *connectH) {
	const short bufsize = 1024;
	char buf[bufsize];
	while (1) {
			if (flag == false){
		  connectH->close();
				break;

		  
		}

		cin.getline(buf, bufsize);
			if (flag == false){
		  connectH->close();
				break;

		  
		}

		string line(buf);
			if (flag == false){
		  connectH->close();
				break;

		  
		}

		connectH->sendLine(line);
		if (flag == false){
		  connectH->close();
				break;

		  
		}


	}
}

int main(int argc, char **argv) {

	ConnectionHandler *connectH = new ConnectionHandler("127.0.0.1", 8080);
	connectH->connect();
	thread thread1(&ListenToServer, connectH);
	thread thread2(&ListenToKeyBoard, connectH);
	thread1.join();
        delete connectH;
	return 0;
}



