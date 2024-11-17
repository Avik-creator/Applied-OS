#include <iostream>
#include <thread>
#include <mutex>
#include <condition_variable>
#include <queue>

class Semaphore {
    private:
        std::mutex mutex_;
        std::condition_variable condition_;
        unsigned long count_ = 0;
    public:
        Semaphore(int value){
            count_ = value;
        }

        void release(){
            std::lock_guard<std::mutex> lock(mutex_);
            ++count_;
            condition_.notify_one();
        }

        void acquire(){
            std::unique_lock<std::mutex> lock(mutex_);
            while(count_ == 0){
                condition_.wait(lock);
            }
            --count_;
        }
};

std::mutex mtx;
std::queue<int> buffer;
const unsigned int MAX_BUFFER_SIZE = 10;

Semaphore empty_slots(MAX_BUFFER_SIZE);
Semaphore filled_slots(0);

void producer(int val){
    empty_slots.acquire();

    {
        std::lock_guard<std::mutex> lock(mtx);
        buffer.push(val);
        std::cout << "Produced: " << val << std::endl;
        std::cout << "Buffer size After Producing: " << buffer.size() << std::endl;
    }

    filled_slots.release();
}

void consumer(){
    filled_slots.acquire();

    int val;
    {
        std::lock_guard<std::mutex> lock(mtx);
        val = buffer.front();
        buffer.pop();
        std::cout << "Consumed: " << val << std::endl;
        std::cout << "Buffer size After Consuming: " << buffer.size() << std::endl;
    }

    empty_slots.release();
}

int main(){
    std::thread producerThread([] {
        for (int i = 1; i <= 20000; ++i) {
            producer(i);
        }
    });
    
    std::thread consumerThread([] {
        for (int i = 1; i <= 20000; ++i) {
            consumer();
        }
    });
    
    producerThread.join();
    consumerThread.join();
    
    return 0;
}