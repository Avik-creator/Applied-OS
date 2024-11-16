#include <iostream>
#include <thread>


int lock = 0;

class thread_object{
    public:
        void operator()(int& x){
            for(int i = 0; i < 1000000; i++){
                while(lock);
                lock = 1;
                x++;
                lock = 0;
            }
        }
};

int main() {
    int x = 0;
    thread_object thread;
    std::thread t1(thread, std::ref(x));
    std::thread t2(thread, std::ref(x));

    t1.join();
    t2.join();

    std::cout << "Final value of x: " << x << std::endl;

    return 0;

}