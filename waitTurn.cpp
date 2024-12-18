#include <iostream>
#include <thread>

int turn = 0;

class thread1 {
    public:
        void non_critical_section(){
            int a = 0;
            while(++a < 10);
        }

        void operator()(int& x){
            while(true){
                while(turn != 0);
                x += 1;
                std::count << "Thread 1" << x << std::endl;
                turn = 1;
                non_critical_section();
            }
        }
};

class thread2 {
    public:
        void non_critical_section(){
            int a = 0;
            while(++a < 10);
        }

        void operator()(int& x){
            while(true){
                while(turn != 1);
                x += 1;
                std::count << "Thread 2" << x << std::endl;
                turn = 0;
                non_critical_section();
            }
        }
};

int main(){
    int x = 0;
    thread1 thread_obj_1;
    thread2 thread_obj_2;
    std::thread t1(std::ref(thread_obj_1), std::ref(x));
    std::thread t2(std::ref(thread_obj_2), std::ref(x));
    t1.join();
    t2.join();
    return 0;
}