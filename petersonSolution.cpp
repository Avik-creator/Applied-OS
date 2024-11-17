#include <iostream>
#include <thread>

#define TRUE 1
#define FALSE 0
#define N 2

int turn = 0;
int interested[N];
int x = 0;

void enter_region(int process){
    int other = 1 - process;
    interested[process] = TRUE;
    turn = process;
    while(turn == process && interested[other] == TRUE);
}

void leave_region(int process){
    interested[process] = FALSE;
}

class thread_object{
public:
    void operator()(int process){
        for(int i = 0; i < 1000000; ++i){
            enter_region(process);
            x++;
            std::cout << "Thread " << process << " x = " << x << std::endl;
            leave_region(process);
        }
    }
};

int main(int argc, char *argv[]){
   thread_object thread;
    std::thread t1(thread, std::ref(x));
    std::thread t2(thread, std::ref(x));

    t1.join();
    t2.join();
    return 0;
}